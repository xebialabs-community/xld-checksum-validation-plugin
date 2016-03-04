#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS 
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

# Grab the SHA1 hash of filepath
def sha1OfFile(filepath):
    import hashlib
    # Read in as binary mode to match XLD
    with open(filepath, 'rb') as f:
        return hashlib.sha1(f.read()).hexdigest()

# Grab the checksum and filepath of a deployable artifact
checksum = str(deployable["checksum"])
deployable_file = deployable.file
deployable_file_location = deployable_file.path
calculated_checksum = str(sha1OfFile(deployable_file_location))

print "Deployable Checksum: %s" % checksum
# Calculate checksum on the filesystem
print "Calculated Checksum: %s" % calculated_checksum

if (checksum == calculated_checksum):
    print "Checksum match!"
else:
    print "Checksum mismatch!
    # Throw an exception in order to stop the deployment
    raise ValueError('Checksum mismatch!')
