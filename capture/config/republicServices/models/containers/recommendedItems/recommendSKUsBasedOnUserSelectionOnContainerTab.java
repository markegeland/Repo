/*
================================================================================
Name:   		Recommend SKUs Based On User Selection On Container Tab
Author:
Create date:
Description:		Sets the line item grid for small containers
        
Input:   		Small container configuration attributes
                    
Output:  		String describing the template for small container line items

Updates:	
		03/27/15 - Mike (Republic) - #145 - Small Container Compactor - Added the Compactor Rental and Installation line items
=====================================================================================================
*/
retStr  = "";
VALUE_DELIM = "^_^";
ATTR_DELIM = "@_@";
// Change the Compactor from Boolean to String.
compactorStr = "0";
compactorInt = 0;
if(compactor){
	compactorStr = "1";
	compactorInt = 1;//Parts DB has 0 for compactor part and "1" for non-compactor part
}
//Added as part of cart pricing to pull a specific code from Div_Container_Size 
containerCodes = split(routeTypeDervied, "^_^");


tempSize = containerSize;
containerSizeArray = split(containerSize, ".");
	if(sizeofarray(containerSizeArray) > 0) {
	  decimals = containerSizeArray[1];
	  if(len(decimals) == 1) {
	    tempSize = containerSize + "0";
	  }
	}


codeRecords = BMQL("SELECT container_cd FROM Div_Container_Size WHERE division = $division_config AND containerSize = $tempSize AND has_compactor = $compactorInt AND container_cd IN $containerCodes");
print codeRecords;
containerCode = "";

for record in codeRecords{
	if(get(record, "container_cd") <> "HP"){
		containerCode = get(record, "container_cd");
	}elif(containerSize == ".5"){ 
		containerCode = "HP";
	}
}

partsRecordSet = bmql("SELECT part_number FROM _parts WHERE custom_field9 = $containerCode AND custom_field10 = $tempSize AND custom_field11 = $compactorStr AND  custom_field12 = $lOBCategoryDerived");
qty = quantity;
print partsRecordSet;
for eachRec in partsRecordSet{
	partNum = get(eachRec, "part_number");
	retStr = retStr   + partNum + "~" + string(qty) + "~" + "Occurrence" + VALUE_DELIM + "Monthly" +ATTR_DELIM+"rateType" + VALUE_DELIM + "Base"+ "~" + "~" + "identifier1|^|";
	if(compactor){
		retStr = retStr   + partNum + "~" + string(qty) + "~" + "Occurrence" + VALUE_DELIM + "Monthly" +ATTR_DELIM+"rateType" + VALUE_DELIM + "Compactor Rental"+ "~" + "~" + "identifier4|^|";
	}
	if(serviceRequirement == "On-call"){
		retStr = retStr  + partNum + "~" + "1" + "~" + "Occurrence" + VALUE_DELIM + "One-Time" + "~"  + "~" + "identifier2|^|";
	}
	//08/28/2014 - 3-9453379691 : Only if the container is NOT customer owned, delivery must be added as a line item
	if(NOT(isCustomerOwned)){
		retStr = retStr   + partNum + "~" + string(qty) + "~" + "Occurrence" + VALUE_DELIM + "One-Time" +ATTR_DELIM+"rateType" + VALUE_DELIM + "Delivery"+ "~" + "~" + "identifier3|^|";
	}	
	//Installation Charges
	if(compactor AND installationCostEstimate_s > 0){
		retStr = retStr   + partNum + "~" + string(qty) + "~" + "Occurrence" + VALUE_DELIM + "One-Time" +ATTR_DELIM+"rateType" + VALUE_DELIM + "Installation"+ "~" + "~" + "identifier5|^|";
	}
}

return retStr ;
