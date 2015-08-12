//Pack Up & Pick Up Full Truckload

/*
==================================================================================================
Name:   		Recommend SKUs Based On User Selection On Electronic Recycling
Author:			Sheehuavah Zaj Moua & Mark Egeland
Create date:    	7/16/2015
Description:		Sets the line item grid for Electronic Recycling 
        
Input:   		Electronic Recycling configuration attributes
                    
Output:  		String describing the template for Electronic Recycling line items
Updates:		20150723 - Mark Egeland - #710 added section for Box Mail-Back rec items
					20150729 - Mark Egeland - #710 modified recommendation for Box Mail-Back, removed base part
==================================================================================================
*/
retStr  = "";
VALUE_DELIM = "^_^";
ATTR_DELIM = "@_@";
partNum = "";

codeRecords = bmql("SELECT containerType, customField12 FROM wasteCategory WHERE wasteCategory = $electronicRecyclingCatagory_er");

containerCode = "";
fieldType = "";
for record in codeRecords{
	containerCode = get(record, "containerType");
	fieldType = get(record, "customField12");
}

partsRecordSet = bmql("SELECT part_number FROM _parts WHERE custom_field9 = $containerCode AND custom_field12 = $fieldType");

for eachRec in partsRecordSet{
	partNum = get(eachRec, "part_number");
	
	if(electronicRecyclingCatagory_er <> "Box Mail-Back"){
		retStr = retStr   + partNum + "~" + "1" + "~" + "Occurrence" + VALUE_DELIM + "One-Time" +ATTR_DELIM+"rateType" + VALUE_DELIM + "Base"+ "~" + "~" + "identifier1|^|";
	}
}

/* ===========================  Box Mail-Back Rec Items =============================== */
if(electronicRecyclingCatagory_er == "Box Mail-Back"){
	loopCount = range(sizeofarray(boxMailBoxName_er));
	for each in loopCount{
		if(boxMailBoxQty_er[each] > 0){
			retStr = retStr   + partNum + "~" + string(boxMailBoxQty_er[each]) + "~" + "Occurrence" + VALUE_DELIM + "One-Time" +ATTR_DELIM+ "boxSize" + VALUE_DELIM + boxMailBoxName_er[each] + "~~identifier" + string(each) + "|^|";
		}
	}
	if(assetRegistrationQty_er > 0){
		retStr = retStr   + partNum + "~" + string(assetRegistrationQty_er) + "~" + "Occurrence" + VALUE_DELIM + "One-Time" +ATTR_DELIM+ "boxSize" + VALUE_DELIM + "Asset Registration" + "~~identifierA|^|";
	}
	
} /* ============================ Box Mail-Back Rec Items End =============================== */

return retStr;