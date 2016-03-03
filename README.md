# xld-adv-checksum-plugin

This plugins adds steps in the deployment plan to verify that the checksum of the artifact has not changed since it has been imported into XL Deploy. This is specifically for deployables that have the binary stored on a third party location (Nexus, Artifactory, HTTP server) This builds an extra layer of confidence that the right file is being deployed. Note that tools like Nexus and Artifactory already take care of this requirements, so this plugin should mainly be used if you download binaries from untrusted locations.
