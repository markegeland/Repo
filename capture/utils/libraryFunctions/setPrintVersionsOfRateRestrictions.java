/* 
	==========================================================================
	| Util LIBRARY													 |
	+------------------------------------------------------------------------+
	| setPrintVersionsOfRateRestrictions											 |
	==========================================================================
	| Generates the print versions of the rate restrictions										 		|
	==========================================================================
	Change Log
	20150117 - Julie Felberg - Created Util based on rate restriction logic in doc engine
	
*/

result = "";

if (find(yearRate, "CPI")> -1){
	result = yearRate + " Increase";
}
elif(find(yearRate, "%")> -1){
	result = "Increase " + yearRate;
}
elif(yearRate <> ""){
	result = "Increase " + yearRate;
}
else{
	result = "";
}

return result;
