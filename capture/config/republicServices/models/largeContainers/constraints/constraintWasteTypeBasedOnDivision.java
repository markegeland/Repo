// Check for valid waste types in this division

wasteTypeRecordSet = bmql("select DISTINCT WasteType from Disposal_Sites where DisposalSite_DivNbr = $division_config");
print wasteTypeRecordSet;
allowedWasteTypeArray = String[];

for eachWasteType in wasteTypeRecordSet {
	thisWasteType = get(eachWasteType, "WasteType");
	append(allowedWasteTypeArray, thisWasteType);
}

if(sizeofarray(allowedWasteTypeArray) <> 0) {
	return join(allowedWasteTypeArray, "|^|");
}

// If no results for this specific division, check the corporate defaults for Division 0
/*
wasteTypeRecordSet = bmql("select wasteType from Div_Waste_Types where division = '0'");

allowedWasteTypeArray = String[];

for eachWasteType in wasteTypeRecordSet {
	thisWasteType = get(eachWasteType, "wasteType");
	append(allowedWasteTypeArray, thisWasteType);
}

if(sizeofarray(allowedWasteTypeArray) <> 0) {
	return join(allowedWasteTypeArray, "|^|");
}
*/
return "";