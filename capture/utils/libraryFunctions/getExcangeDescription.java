/* 
	==========================================================================
	| Util LIBRARY													 |
	+------------------------------------------------------------------------+
	| getExcangeDescription										 |
	==========================================================================
	| Used in email notification generator											 		|
	==========================================================================
	Change Log
	20140202 - Julie Felberg Created
	20140203 - Julie Felberg Edited part description
	
*/


//Instantiate Variables
retStr= "";
tempSize = "";
tempWaste = "";
tempCode = "";
tempLifts = "";
lobCategoryDerived = "";
VALUE_DELIM = "^_^";
ATTR_DELIM = "@_@";
compactorFlag = hasCompactor_readOnly;
qty = quantity_sc;
oldDescLine = "";
oldPartDesc = "";
oldWasteType = "";
oldLiftsPerContainer = "";
newDescLine = "";
newPartDesc = "";
newWasteType = "";
newLiftsPerContainer = "";
//custom_field10 = 0.00;
//CF10 = 0.00;

//Handle Waste Type
if(wasteType_sc == "No Change"){
	tempWaste = wasteType_readonly;
}else{
	tempWaste = wasteType_sc;
}

//Handle Size
if(containerSize_sc == "No Change"){
	//Find Valid Sizes
	codeString = "";
	contCodeRecs = BMQL("SELECT container_cd FROM corp_container_map WHERE waste_type = $tempWaste");
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

	sizeArray = string[];
	if(containskey(divisionDict, division_config)){
		sizeArray = get(divisionDict, division_config);
	}elif(containskey(divisionDict, "0")){
		sizeArray = get(divisionDict, "0");
	}
	
	sort(sizeArray,"asc","numeric");
	print sizeArray;
	for each in sizeArray{

		if(atof(containerSize_readonly) == atof(each)){
			tempSize = containerSize_readonly;
			break;
		}elif(atof(containerSize_readonly) > atof(each)){
			continue;
		}elif(atof(containerSize_readonly) < atof(each)){
			tempSize = each;
			break;
		}
	}
	/*
	loopArray = int[1000];
	tempSize = atof(containerSize_readonly);
	for count in loopArray{
		if(findinarray(sizeArray,string(tempSize)) >= 0){
			break;
		}else{
			if(tempSize < 4.0){
				tempSize = tempSize + 0.01;
			}else{
				tempSize = tempSize + 1;
			}
		}
	}
	*/
}else{
	tempSize = containerSize_sc;
}

TempCodeRO = containerCode_readOnly;
TempCodeSC = "";
//Handle Container Code
print "ContainerCodes_SC: " + ContainerCodes_SC;
print "containerCode_readOnly: " + containerCode_readOnly;
if(ContainerCodes_SC == "No Change"){
	tempCodeSC = containerCode_readOnly;
}else{
	tempCodeSC = ContainerCodes_SC;
}
print "tempCodeSc: " + tempCodeSC;
//Handle Lifts Per Container
if(liftsPerContainer_sc == "No Change"){
	tempLifts = liftsPerContainer_readOnly;
}else{
	tempLifts = liftsPerContainer_sc;
}
print "tempLifts: " + tempLifts;
print "lifts SC: " + liftsPerContainer_sc;
print "lifts RO: " + liftsPerContainer_readOnly;

//Set LOB Category Derived 
containerSizeFloat = 0.0;
containerCategory = "";

if(isnumber(tempSize)){
	containerSizeFloat = atof(tempSize);
	if(containerSizeFloat <= 10){	//Indicates small container
		containerCategory = "Small Container";
	}
	elif(containerSizeFloat > 10){	//Indicates large container. Not currently used.
		containerCategory = "Large Container";
	} print containerCategory;
	lobRecordSet = bmql("SELECT LOB_Category_Derived FROM LOB_Category WHERE chooseContainer = $containerCategory AND wasteType = $tempWaste");
	print lobRecordSet;
	for record in lobRecordSet{
		lobCategoryDerived = get(record, "LOB_Category_Derived");
	}	
}
print "tempsize: ";
print tempSize;
print "tempCodeRO: ";
print tempCodeRO;
if(tempCodeRO <> "HP"){
containerSizeArray = split(tempSize, ".");
	if(sizeofarray(containerSizeArray) > 0) {
	  decimals = containerSizeArray[1];
	  if(len(decimals) == 1) {
	    tempSize = tempSize + "0";
	  }
	}
}
//print tempSize;
containerSizeString = string(containerSizeFloat);
containerSizeRO = containerSize_readOnly;
//Form Return String
print "containerSizeString: " + containerSizeString;
print "tempCodeSC: " + tempCodeSC;

//partsRecordSet = bmql("SELECT part_number, custom_field10, part_desc FROM _parts WHERE custom_field9 = $tempCodeSC AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");
//PartReplacementSet = bmql("SELECT part_number, custom_field10, part_desc FROM _parts WHERE custom_field9 = $tempCodeRO AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");

partsRecordSet = bmql("SELECT part_number, custom_field10, description FROM _parts WHERE custom_field9 = $tempCodeSC AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");
PartReplacementSet = bmql("SELECT part_number, custom_field10, description FROM _parts WHERE custom_field9 = $tempCodeRO AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");
//print "old record set J";
//print partsRecordSet;
for oldPart in PartReplacementSet{
	custom_field10 = getFloat(oldPart, "custom_field10");
	if(custom_field10 == atof(containerSizeRO)){
		print "we get an old part number: " + get(oldPart, "part_number");
		oldPartDesc = get(oldPart, "description");
		print "description: " + oldPartDesc;
	}
}
print "oldPartDesc:  " + oldPartDesc;

oldWasteType = wasteType_readOnly;
oldLiftsPerContainer = liftsPerContainer_readOnly;

for newPart in partsRecordSet{
	CF10 = getFloat(newPart, "custom_field10");
	if(CF10 == containerSizeFloat){
		print "we get a new part number: " + get(newPart, "part_number");
		newPartDesc = get(newPart, "description");
		print "description: " + newPartDesc;
	}
}
print "newPartDesc: " + newPartDesc;

newWasteType = tempWaste;
newLiftsPerContainer = tempLifts;

oldDescLine = oldPartDesc + "," + oldWasteType + "," + oldLiftsPerContainer;
newDescLine = newPartDesc + "," + newWasteType + "," + newLiftsPerContainer;


result = oldDescLine + "|^|" + newDescLine;
return result;
