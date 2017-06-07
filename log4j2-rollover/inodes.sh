outputFile=inodes.txt

echo -n "" > $outputFile

while true ; do
	logs=$(ls -1 /app/build/logs/json-rollover.log*)
	for file in $logs ; do
		inode=$(stat --format=%i $file)
		text=""
		if [ $? -eq 1 ] ; then
			text="-1,"
		else
			text="$inode,"
		fi
		echo -n "$text" >> $outputFile
	done

	echo "" >> $outputFile
done
