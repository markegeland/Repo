/*
20150528 - Gaurav Dawar - #325 This Rule was created to validate that user is not able to put anything in "after year1" text boxes other than float numbers and the word "CPI".
*/
yr_1_text = year1Rate_quote;
if(customerRateRestriction_quote == True){
	if(yr_1_text == "" OR (isnumber(yr_1_text)==True AND atof(yr_1_text) > 0.0) OR yr_1_text == "CPI"){
		return false;
	}
	return true;
}
return false;