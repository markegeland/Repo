/*
20150528 - Gaurav Dawar - #325 This Rule was created to validate that user is not able to put anything in "after year2" text boxes other than float numbers and the word "CPI".
*/
yr_2_text = year2Rate_quote;
if(customerRateRestriction_quote == True){
	if(yr_2_text == "" OR (isnumber(yr_2_text)==True AND atof(yr_2_text) > 0.0) OR yr_2_text == "CPI"){
		return false;
	}
	return true;
}
return false;