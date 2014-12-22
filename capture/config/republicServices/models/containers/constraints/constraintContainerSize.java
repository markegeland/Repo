retStr = "";
hasCompactor = 0;
if(compactor){
	hasCompactor = 1;
}
//containterCodes takes the delimited string stored in routeTypeDerived into an array used in the BMQL Query
containerCodes = split(routeTypeDervied, "^_^");
resultset = bmql("SELECT containerSize, division, container_cd FROM Div_Container_Size WHERE division = $division_config   AND container_cd IN $containerCodes AND has_compactor = $hasCompactor");
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
return retStr;