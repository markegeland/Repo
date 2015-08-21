/*
==================================================================================================
Name:   		Hide Financial Summaries When ER Only - Condition
Author:			Mark Egeland
Create date:    	8/21/2015
Description:		Returns true if no core products are on the quote
     
Input:   		N/A
     
Output:  		Boolean

Updates:		
==================================================================================================
*/
result = true;

for line in line_process{
	if(line._parent_line_item == "Large Containers"
	OR line._parent_line_item == "Containers"
	OR line._parent_line_item == "Service Change"
	OR line._parent_line_item == "Ad-Hoc Line Items"){
		result = false;
		break;
	}
}

return result;