<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
    
-->

output=$(sha1sum ${containerHome}/${deployedFileName} | awk '{print $1}')
echo "Deployable Checksum: ${checksum}"
echo "Calculated Checksum: $output"

if [ $output != '${checksum}' ]
	then
		echo "Checksum mismatch!"
		exit 1
fi
