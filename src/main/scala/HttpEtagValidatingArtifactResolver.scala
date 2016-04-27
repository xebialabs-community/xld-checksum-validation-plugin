/*
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package com.xebialabs.xlplatform.artifact.resolution.http

import java.io.{FileNotFoundException, InputStream}
import java.net.{URI, UnknownHostException}
import java.security.{DigestInputStream, MessageDigest}

import com.xebialabs.deployit.engine.spi.artifact.resolution.ArtifactResolver.Resolver
import com.xebialabs.deployit.engine.spi.artifact.resolution.{ArtifactResolver, ResolvedArtifactFile}
import com.xebialabs.deployit.plugin.api.udm.artifact.SourceArtifact
import com.xebialabs.deployit.plugin.api.udm.artifact.SourceArtifact._
import org.apache.commons.codec.binary.Hex
import org.apache.commons.httpclient.auth.AuthScope
import org.apache.commons.httpclient.{HttpClient, HttpMethod, HttpStatus, UsernamePasswordCredentials}
import org.apache.commons.httpclient.methods.{GetMethod, HeadMethod}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

object HttpEtagArtifactResolver {
  val Protocols = Array("checksum-http", "checksum-https")

  private[http] val ContentDispositionHeader = "Content-Disposition"

  private[http] val ContentDispositionFileName = """.*;[ ]*filename="?([^;^"]*)"?.*""".r
}

@Resolver(protocols = Array("checksum-http", "checksum-https"))
class HttpEtagArtifactResolver extends ArtifactResolver {
  private val logger: Logger  = LoggerFactory.getLogger(HttpEtagArtifactResolver.getClass);

  import com.xebialabs.xlplatform.artifact.resolution.http.HttpEtagArtifactResolver._

  override def resolveLocation(artifact: SourceArtifact): ResolvedArtifactFile = new ResolvedArtifactFile {

    private var fileName: Option[String] = None

    private val httpFileUri: String = artifact.getFileUri.stripPrefix("checksum-")

    private var openConnections: Seq[GetMethod] = Seq()

    override def getFileName: String = fileName.getOrElse {
      fileName = filenameOption(new HeadMethod(httpFileUri))
        .orElse(filenameOption(new GetMethod(httpFileUri)))
        .orElse(getFilenameFromUri)
      fileName.get
    }

    override def close(): Unit = openConnections.foreach(_.releaseConnection())

    override def openStream: InputStream = {
      val client: HttpClient = new HttpClient()
      val m = new GetMethod(httpFileUri)
      m.setFollowRedirects(true)

      Option(getFileUri(artifact).getUserInfo).map(_.split(":")) match {
        case None =>
          m.setDoAuthentication(false)
        case Some(Array(u, p)) =>
          m.setDoAuthentication(true)
          client.getState.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(u, p))
      }

      try {
        client.executeMethod(m)
        fileName = fileName.orElse(filenameFromHeader(m))
        openConnections :+= m
      } catch {
        case e: UnknownHostException => throw new RuntimeException(s"Host can not be found", e)
        case e: FileNotFoundException => throw new RuntimeException(s"File not found: ${artifact.getFileUri}", e)
        case e: Throwable => throw new RuntimeException(s"Error downloading artifact: ${artifact.getFileUri}", e)
      }

      m.getStatusCode match {
        case sc if isSuccessfulStatus(sc) => {

          new DigestInputStream(m.getResponseBodyAsStream, MessageDigest.getInstance("SHA1", "BC")) {

            override def read(b: Array[Byte], off: Int, len: Int): Int = {
              val numberOfBytesRead: Int = super.read(b, off, len)

              if (isStreamDepleted(len, numberOfBytesRead)) {
                validateOrSetChecksum
              }

              numberOfBytesRead
            }

            def isStreamDepleted(len: Int, numberOfBytesRead: Int): Boolean = {
              numberOfBytesRead == -1 || numberOfBytesRead < len
            }

            def validateOrSetChecksum: Unit = {
              val expectedChecksum: Option[String] = Option(artifact.getChecksum)
              val actualChecksum: String = Hex.encodeHexString(getMessageDigest.digest)

              logger.info(s"Remote artifact ${artifact.getFileUri}, expected checksum ${expectedChecksum.get}, actual ${actualChecksum}.")

              if (expectedChecksum.isEmpty || expectedChecksum.get.isEmpty) {
                artifact.setProperty(CHECKSUM_PROPERTY_NAME, actualChecksum)
              } else if (!actualChecksum.equals(expectedChecksum.get)) {
                throw new RuntimeException(s"Remote artifact was modified since last import at location ${artifact.getFileUri}, expected checksum ${expectedChecksum} but was ${actualChecksum}.")
              }
            }

          }

        }
        case _ => throw new RuntimeException(s"Server returned non-successful status code ${m.getStatusCode} when accessing ${artifact.getFileUri}")
      }
    }

    private def filenameFromHeader(m: HttpMethod): Option[String] = {
      Option(m.getResponseHeader(ContentDispositionHeader)).map(_.getValue).collect {
        case ContentDispositionFileName(filename) => filename
      }.orElse(getFilenameFromUri)
    }

    private def filenameOption(m: HttpMethod): Option[String]  = {
      try {
        Try(new HttpClient().executeMethod(m)).toOption.collect {
          case status if isSuccessfulStatus(status) =>
            filenameFromHeader(m).get
        }
      } finally {
        m.releaseConnection()
      }
    }

    private def getFilenameFromUri: Option[String] = Some(getFileUri(artifact).getSchemeSpecificPart.split('/').last)

    private def isSuccessfulStatus(sc: Int): Boolean = sc >= HttpStatus.SC_OK && sc < HttpStatus.SC_MULTIPLE_CHOICES
  }

  def getFileUri(artifact: SourceArtifact): URI = {
    URI.create(artifact.getFileUri)
  }

  override def validateCorrectness(artifact: SourceArtifact): Boolean = Protocols.contains(getFileUri(artifact).getScheme)

}
