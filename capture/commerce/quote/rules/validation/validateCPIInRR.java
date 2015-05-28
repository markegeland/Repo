/*
20150528 - Gaurav Dawar - #325 This Rule was created to validate that user is not able to put anything in "after year" text boxes other than float numbers and the word "CPI".
*/
yr_1_text = year1Rate_quote;
yr_2_text = year2Rate_quote;
yr_3_text = year3Rate_quote;
yr_4_text = afterYear4_quote;
print yr_1_text;
if(customerRateRestriction_quote == True){
	if(yr_1_text == "" OR (isnumber(yr_1_text)==True AND atof(yr_1_text) > 0.0) OR yr_1_text == "CPI"){
		if(yr_2_text == "" OR (isnumber(yr_2_text)==True AND atof(yr_2_text) > 0.0) OR yr_2_text == "CPI"){
			if(yr_3_text == "" OR (isnumber(yr_3_text)==True AND atof(yr_3_text) > 0.0) OR yr_3_text == "CPI"){
				if(yr_4_text == "" OR (isnumber(yr_4_text)==True AND atof(yr_4_text) > 0.0) OR yr_4_text == "CPI"){
					return false;
				}	
			}
		}	
	}
	return true;	
}
return false;