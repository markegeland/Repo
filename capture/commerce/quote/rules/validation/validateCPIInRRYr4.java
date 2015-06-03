/*
20150528 - Gaurav Dawar - #325 This Rule was created to validate that user is not able to put anything in "after year4" text boxes other than float numbers and the word "CPI".
*/
yr_4_text = afterYear4_quote;
if(customerRateRestriction_quote == True){
	if(yr_4_text == "" OR (isnumber(yr_4_text)==True AND atof(yr_4_text) > 0.0) OR yr_4_text == "CPI"){
		return false;
	}
	return true;
}
return false;