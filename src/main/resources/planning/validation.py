#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS 
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from java.util import HashSet

supported_types = ['file.File', 'file.Folder', 'file.Archive']

# Grab all deployables
def unique_supported_deployables():
    result = HashSet()
    for delta in deltas.deltas:
        deployed = delta.deployedOrPrevious

        is_supported_type = str(deployed.deployable.type) in supported_types
        is_supported_operation = delta.operation == "CREATE" or delta.operation == "MODIFY"

        if is_supported_type and is_supported_operation:
            result.add(deployed.deployable)
    return result
        
# Create a before validation step for each deployable artifact
for deployable in unique_supported_deployables():
    context.addStep(steps.jython(
        description="Validate checksum of %s" % deployable.name,
        order=0,
        script="scripts/validation/before_validation.py",
        jython_context={'deployable': deployable})
    )
