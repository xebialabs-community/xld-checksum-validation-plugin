#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS 
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from java.util import HashSet

#Grab all deployables
def unique_deployables():
    result = HashSet()
    for delta in deltas.deltas:
        deployed = delta.deployedOrPrevious
        if delta.operation == "CREATE" or delta.operation == "MODIFY":
            result.add(deployed.deployable)
    return result
        
#Create a before validation step for each deployable artifact
for deployable in unique_deployables():
    context.addStep(steps.jython(
        description="Validate checksum of %s" % deployable.name,
        order=0,
        script="scripts/validation/before_validation.py",
        jython_context={'deployable': deployable})
    )

#Cycle through every delta and its corresponding deployables and containers to create an os-script step for after validation
#Manually pass values into the freemarker context
#Every container MUST have a "home" property
#Every deployable must install through the container's home property
for delta in deltas.deltas:
    if delta.operation == "CREATE" or delta.operation == "MODIFY":
        deployed = delta.deployedOrPrevious
        container = deployed.container
        deployable = deployed.deployable
        checksum = deployable["checksum"]
        
        context.addStep(steps.os_script(
            description="Validate checksum of %s for %s on %s" % (deployable.name, container.name, container.host.name),
            order=0,
            script="scripts/validation/after_validation",
            freemarker_context={'checksum': checksum, 'deployedFileName': deployed.file.name, 'containerHome': container.home},
            target_host=container.host)
        )
