/*Change made for Large Existing Release*/
// validate the Effective service date-- Cap period will be 6 months
DELIM= "/";
result="";
dateSeperator="/";
dateFormat="%m/%d/%Y";
if(effectiveServiceDate_line <> ""){
	formattedeffectiveServiceDate = strtodate(substring(effectiveServiceDate_line,0,10), dateFormat);
	dateAfter6Months = adddays(getdate(), 182); //gives date approx after 6 months - leap year is not considered separately, will add the check if needed
	if(comparedates(formattedeffectiveServiceDate, dateAfter6Months) == 1){ //if effective date is greater than or after 6month date from current date, run this validation
		return true;
	}
}	
return false;