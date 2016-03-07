# xld-checksum-validation-plugin

This plugins adds steps in the deployment plan to verify that the checksum of the deployables in a deployment package have not changed since they have been imported into XL Deploy. This is specifically for deployables that have the binary stored on a third party location (Nexus, Artifactory, HTTP server) This builds an extra layer of confidence that the right file is being deployed. Note that tools like Nexus and Artifactory already take care of this requirements, so this plugin should mainly be used if you download binaries from untrusted locations.

# Installation
Drop the built plugin (a JAR file) into the \<XLD_SERVER\>/plugins directory.

# Usage
For all deployable types that have a checksum property, a new step will appear at the beginning of the deployment plan. During execution of this step, it is verified that the underlying binary (a file or archive) has not changed since initial import into XLD.

## Example of deployment plan
![Preview](/docs/img/preview.png)

## Example of success
![Success](/docs/img/success.png)

## Example of failure
![Failure](/docs/img/failure.png)
