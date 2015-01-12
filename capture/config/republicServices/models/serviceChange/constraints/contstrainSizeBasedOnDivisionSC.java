retStr = "";
tempWasteType = "";
codeString = "";

if(wasteType_sc == "No Change"){
	tempWasteType = wasteType_readOnly;
}else{
	tempWasteType = wasteType_sc;
}

contCodeRecs = BMQL("SELECT container_cd FROM corp_container_map WHERE waste_type = $tempWasteType");
for code in contCodeRecs{
	codeString = codeString + get(code, "container_cd") + "^_^";
}

contCodes = split(codeString,"^_^");

resultset = bmql("SELECT containerSize, division, container_cd FROM Div_Container_Size WHERE division = $division_config and container_cd IN $contCodes" );
divisionDict = dict("string[]");

print resultset;

for result in resultset{
	size = get(result, "containerSize");
	division = get(result, "division");
	code = get(result, "container_cd");
	
	if(NOT(isnull(size)) AND NOT(isnull(division))){
		sizesArray = string[];
		if(containskey(divisionDict, division)){
			sizesArray = get(divisionDict, division);
		}

		print find(code, "H");
		if(size == ".50" AND (find(code, "H")  == 0)){
			append(sizesArray, ".5");
		}else{
			append(sizesArray, size);
		}
		put(divisionDict, division, sizesArray);
	}
}
print divisionDict;

returnArray = string[];
if(containskey(divisionDict, division_config)){
	returnArray = get(divisionDict, division_config);
}elif(containskey(divisionDict, "0")){
	returnArray = get(divisionDict, "0");
}
if(sizeofarray(returnArray) > 0){
	retStr = join(returnArray, "|^|");
}
retStr = retStr + "|^|No Change";

return retStr;