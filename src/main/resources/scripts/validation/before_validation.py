#Grab the SHA1 hash of any filepath
#Read in as binary mode to match XLD
def sha1OfFile(filepath):
    import hashlib
    with open(filepath, 'rb') as f:
        return hashlib.sha1(f.read()).hexdigest()

#Grab the checksum and filepath of a deployable artifact
#Calculate and compare values, throw an exception in order to stop the deployment
checksum = deployable["checksum"]
dep_file = deployable.file
dep_file_location = dep_file.path
print 'Deployable Checksum: ' + str(checksum)
print 'Calculated Checksum: ' + str(sha1OfFile(dep_file_location))
if (str(checksum) == str(sha1OfFile(dep_file_location))):
    print 'Checksum match!'
else:
    print 'Checksum mismatch!'
    raise ValueError('Checksum mismatch!')
