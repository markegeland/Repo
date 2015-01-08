//This function needs to narrow down possible container codes by comparing against data in both Div_Container_Sizes to solve for division limits, and for waste type restrictions stored in corp_container_map
//Removed "No Change" as an option.  AQ 10/31/2014
result = "";
tempWasteType = "";
tempSize = "";
codeArr1 = string[]; //This storest the results from Div_Container_Size, which is passed into the next BMQL
codeArr2 = string[]; //This stores the valid codes from corp_container_map narrowed by waste type and valid codes from codeArr1

if(wasteType_sc == "No Change"){
	tempWasteType = wasteType_readOnly;
}else{
	tempWasteType = wasteType_sc;
}

if(containerSize_sc == "No Change"){
	tempSize = containerSize_readOnly;
}else{
	tempSize = containerSize_sc;
} 
print tempSize;

tempSizeArray = split(tempSize, ".");
if(sizeofarray(tempSizeArray) > 0) {
  decimals = tempSizeArray[1];
  if(len(decimals) == 1) {
    tempSize = tempSize + "0";
  }
}

if(startswith(tempSize, "0")){
	tempSize = substring(tempSize,1);
}
records = bmql("SELECT container_cd FROM Div_Container_Size WHERE division = $division_config AND containerSize = $tempSize");
print records;
for each in records {
	append(codeArr1, get(each, "container_cd"));
}
print codeArr1;
records = bmql("SELECT container_cd FROM corp_container_map WHERE container_cd IN $codeArr1 AND waste_type = $tempWasteType");
for each in records {
	append(codeArr2, get(each, "container_cd"));
}
print codeArr2;
//if(wasteType_sc == "No Change" AND containerSize_sc == "No Change"){ append(codeArr2, "No Change"); } 
// This allows for the "No Change" option only if nothing else has been changed.

result = join(codeArr2, "|^|");
return result;