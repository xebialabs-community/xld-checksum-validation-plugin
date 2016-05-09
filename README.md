# xld-checksum-validation-plugin

# Build status #

[![Build Status](https://travis-ci.org/xebialabs-community/xld-checksum-validation-plugin.svg?branch=master)](https://travis-ci.org/xebialabs-community/xld-checksum-validation-plugin)

# Description
This plugins adds a custom resolver that verifies that the checksum of the deployables in a deployment package have not changed since they have been imported into XL Deploy. This is specifically for deployables that have the binary stored on a third party location (Nexus, Artifactory, HTTP server). This builds an extra layer of confidence that the right file is being deployed. Note that tools like Nexus and Artifactory already take care of this requirements, so this plugin should mainly be used if you download binaries from untrusted locations.

This plugin overrides the default checksum functionality and populates the checksum property with the sha1 sum of the artifact exposed by the HTTP server. If you explicit set checksums on artifacts that are downloaded using this protocol, your deployment will fail. So when using 'checksum-http' urls, you should never override the checksum property.

# Installation
Drop the built plugin (a JAR file) into the \<XLD_SERVER\>/plugins directory. You can download released version from the 'releases' tab on Github.

# Usage
To trigger this plugin, use a prefix of 'checksum-http' instead of 'http' in the File URI property of a deployable that is based on a File, Folder or Artifact. For example: checksum-http://server/file.zip and not http://server/file.zip.

During execution of the deployment plan, it is verified that the underlying binary (a file or archive) has not changed since initial import into XLD.

## Tips and tricks
XL Deploy caches downloads as well, so even when it changes externally, XL Deploy will successfully deploy the original cached version. When testing this plugin, shutdown and restart XL Deploy to clear this download cache.

## Example output of a failure
````
Step failed
java.lang.RuntimeException: Remote artifact was modified since last import at location checksum-http://localhost:4516/, expected checksum Some(203e6b5f52f23149e4ddb314aaa7d819247314f6) but was 203e6b5f52f23149e4ddb314aaa7d819247314f5.
at com.xebialabs.xlplatform.artifact.resolution.http.ShasumCheckingHttpArtifactResolver$$anon$2$$anon$1.validateOrSetChecksum(ShasumCheckingHttpValidatingArtifactResolver.scala:106)
at com.xebialabs.xlplatform.artifact.resolution.http.ShasumCheckingHttpArtifactResolver$$anon$2$$anon$1.read(ShasumCheckingHttpValidatingArtifactResolver.scala:87)
````
