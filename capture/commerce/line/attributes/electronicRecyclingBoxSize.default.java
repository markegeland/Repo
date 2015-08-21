/*
==================================================================================================
Name:   		boxSize Default Function
Author:			Mark Egeland
Create date:    	8/21/2015
Description:		Sets the Electronic Recycling Box Size attribute from the value in the line item comment
        
Input:   		N/A
                    
Output:  		String with box size

Updates:		
==================================================================================================
*/
result = "";
inputDict = dict("string");
COMM_VALUE_DELIM = "^_^";
FIELD_DELIM = "@_@";

put(inputDict, "inputStr", _line_item_comment);
put(inputDict, "COMM_VALUE_DELIM", COMM_VALUE_DELIM);
put(inputDict, "FIELD_DELIM", FIELD_DELIM);

put(inputDict, "key", "boxSize");
result = util.parseThroughLineItemComment(inputDict);

return result;