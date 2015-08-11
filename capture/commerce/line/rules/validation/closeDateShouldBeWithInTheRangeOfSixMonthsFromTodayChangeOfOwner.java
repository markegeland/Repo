/*Change made for Large Existing Release*/
// validate the Service close date-- Cap period will be 6 months
DELIM= "/";
result="";
dateSeperator="/";
dateFormat="%m/%d/%Y";
if(oldOwnerCloseDate_line<> ""){
	formattedServiceCloseDate = strtodate(substring(oldOwnerCloseDate_line,0,10), dateFormat);
	dateAfter6Months = adddays(getdate(), 182); //gives date approx after 6 months - leap year is not considered separately, will add the check if needed
	if(comparedates(formattedServiceCloseDate, dateAfter6Months) == 1){ //if service close date is greater than or after 6month date from current date, run this validation
		return true;
	}
}	
return false;