# xld-checksum-validation-plugin

!EXTREMELY ALPHA PLUGIN!

This plugins adds steps in the deployment plan to verify that the checksum of the deployables in a deployment package have not changed since they have been imported into XL Deploy. This is specifically for deployables that have the binary stored on a third party location (Nexus, Artifactory, HTTP server) This builds an extra layer of confidence that the right file is being deployed. Note that tools like Nexus and Artifactory already take care of this requirements, so this plugin should mainly be used if you download binaries from untrusted locations.

Todo:
* filter out unsupported deployable types (only supports file.File, file.Archive, file.Folder)

!EXTREMELY ALPHA PLUGIN!
