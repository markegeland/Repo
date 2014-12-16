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

}else{
	tempSize = containerSize_sc;
}

TempCodeRO = containerCode_readOnly;
TempCodeSC = "";
//Handle Container Code
if(ContainerCodes_SC == "No Change"){
	tempCodeSC = containerCode_readOnly;
}else{
	tempCodeSC = ContainerCodes_SC;
}
//Handle Lifts Per Container
if(liftsPerContainer_sc == "No Change"){
	tempLifts = liftsPerContainer_readonly;
}else{
	tempLifts = liftsPerContainer_sc;
}

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

print tempSize;
print tempCode;
if(tempCode <> "HP"){
containerSizeArray = split(tempSize, ".");
	if(sizeofarray(containerSizeArray) > 0) {
	  decimals = containerSizeArray[1];
	  if(len(decimals) == 1) {
	    tempSize = tempSize + "0";
	  }
	}
}
print tempSize;
//Form Return String
partsRecordSet = bmql("SELECT part_number, custom_field10 FROM _parts WHERE custom_field9 = $tempCodeSC AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");
PartReplacementSet = bmql("SELECT part_number, custom_field10 FROM _parts WHERE custom_field9 = $tempCodeRO AND custom_field11 = $compactorFlag AND custom_field12 = $lobCategoryDerived AND custom_field13 = 'Y'");
index = 0;
for eachRec in partsRecordSet{
	custom_field10 = getFloat(eachRec, "custom_field10");
	if(custom_field10 == containerSizeFloat){
		partNum = get(eachRec, "part_number");
		retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Base" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "Monthly" + "~" + "~" + string(index) + "|^|";	
		
		//Add separate parts for Exchange, Delivery, or Removal if quantity or container size changes from the original value
		if(containerSize_sc <> "No Change" AND atof(containerSize_sc) <> atof(containerSize_readOnly)){
			//retStr = retStr  + partNum + "~" + "1" + "~" + "rateType" + VALUE_DELIM + "Exchange" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "One-Time" + ATTR_DELIM + "ServiceCode" + VALUE_DELIM + "EXC" + "~" + "~" + "EXC" + string(index) + "|^|";	
			retStr = retStr  + partNum + "~" + string(qty) + "~" + "rateType" + VALUE_DELIM + "Delivery" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "One-Time" + ATTR_DELIM + "ServiceCode" + VALUE_DELIM + "DEL" + "~" + "~" + "DEL" + string(index) + "|^|";
			for eachRep in PartReplacementSet{
				CF10 = getFloat(eachRep, "custom_field10");
				if(CF10 == atof(containerSize_readOnly)){
					if(containerSize_sc <> "No Change" AND atof(containerSize_sc) <> atof(containerSize_readOnly)){
						retStr = retStr  + get(eachRep, "part_number") + "~" + string(quantity_readOnly) + "~" + "rateType" + VALUE_DELIM + "Removal" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "One-Time" + ATTR_DELIM + "ServiceCode" + VALUE_DELIM + "REM" + "~" + "0.0" + "~" + "REM" + string(index) + "|^|";
					}
				}
			}
		}
		else{
			if(quantity_sc > quantity_readOnly){
				tempQty = quantity_sc - quantity_readOnly;
				retStr = retStr  + partNum + "~" + string(tempQty) + "~" + "rateType" + VALUE_DELIM + "Delivery" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "One-Time" + ATTR_DELIM + "ServiceCode" + VALUE_DELIM + "DEL" + "~" + "~" + "DEL" + string(index) + "|^|";	
			}
			elif(quantity_sc < quantity_readOnly){
				tempQty = quantity_readOnly - quantity_sc;
				retStr = retStr  + partNum + "~" + string(tempQty) + "~" + "rateType" + VALUE_DELIM + "Removal" + ATTR_DELIM + "Occurrence" + VALUE_DELIM + "One-Time" + ATTR_DELIM + "ServiceCode" + VALUE_DELIM + "REM" + "~" + "~" + "REM" + string(index) + "|^|";	
			}
		}
	}
	index = index + 1;
}

//lp_cs = makeurlparam({'name': "aquintanilla", 'log':" Return String:  "+ retStr + " This is at runtime"});
//cslog=urldatabypost("http://resources.bigmachines.com/cgi-bin/cs_log_Master.cgi", lp_cs, "");

return retStr;