#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS 
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from java.util import HashSet

# Grab all deployables
def unique_supported_deployables():
    result = HashSet()
    for delta in deltas.deltas:
        deployed = delta.deployedOrPrevious


        has_checksum = hasattr(deployed.deployable, "checksum")
        is_supported_operation = delta.operation == "CREATE" or delta.operation == "MODIFY"

        if has_checksum and is_supported_operation:
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
