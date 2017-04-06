import sys

def readFile(filename):
	return tuple(open(filename, "r"))

def writeFile(filename, text):
	file = open(filename, "w")
	file.write(text)
	file.close()

def main():
	rFile = sys.argv[1]
	wFile = sys.argv[2]
	
	weights = readFile(rFile)
	output = "{\n"
	for i in range(1, len(weights)):
		output += "\t" + weights[i].rstrip()
		if i < len(weights):
			output += ","
		output += "\n"
	output += "}"
	
	writeFile(wFile, output)
	
if __name__ == "__main__":
	main()	
