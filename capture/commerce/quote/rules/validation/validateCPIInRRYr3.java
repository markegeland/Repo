/*
20150528 - Gaurav Dawar - #325 This Rule was created to validate that user is not able to put anything in "after year3" text boxes other than float numbers and the word "CPI".
20150609 - Gaurav Dawar - #325 Value "0.0" was added to allowed values.
*/
yr_3_text = year3Rate_quote;
if(customerRateRestriction_quote == True){
	if(yr_3_text == "" OR (isnumber(yr_3_text)==True AND atof(yr_3_text) >= 0.0) OR yr_3_text == "CPI"){
		return false;
	}
	return true;
}
return false;