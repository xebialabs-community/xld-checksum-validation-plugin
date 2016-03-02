output=$(sha1sum ${containerHome}/${deployedFileName} | awk '{print $1}')
echo "Deployable Checksum: ${checksum}"
echo "Calculated Checksum: $output"

if [ $output != '${checksum}' ]
	then
		echo "Checksum mismatch!"
		exit 1
fi
