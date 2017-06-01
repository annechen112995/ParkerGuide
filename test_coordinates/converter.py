lines = []

file = open("raw_output.txt", "r")
for line in file: 
	lines.append(line)

print lines


file = open("converted_output.txt", "w")

for line in lines:
	line_split = line.split(" ")
	
	if (line_split[0] == "Latitude:"):
		print line_split
		file.write(line_split[1] + " " + line_split[4])

file.close()