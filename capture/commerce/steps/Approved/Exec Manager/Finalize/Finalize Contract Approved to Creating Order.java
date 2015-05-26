/*
20150526 Gaurav Dawar - #481 Added "Customer Accepted: Third party Agreement" to the conditionals.
*/
SIGNED = "Customer Accepted: Signed";
NOT_SIGNED = "Customer Accepted: Did not sign";
CLOSE_ACCOUNT = "Close Account";
CLOSE_SITE = "Close Site";
if(contractStatus_quote == SIGNED OR contractStatus_quote == "Customer Accepted: Signed third party agreement" OR contractStatus_quote == NOT_SIGNED OR contractStatus_quote == CLOSE_ACCOUNT OR contractStatus_quote == CLOSE_SITE){
	return true;
}
return false;