// For Eixsting customerdate should be always greater than the First day of the current month.
dateFormat="%m/%d/%Y";
dateSeperator="/";
formattedCurrentDate = strtodate(substring(datetostr(getdate()),0,10), dateFormat);
formattedeffectiveServiceDate = strtodate(substring(effectiveServiceDate_quote,0,10), dateFormat);

todayDate=datetostr(formattedCurrentDate);
temp=split(substring(todayDate,0,10),dateSeperator);
newDate=temp[0]+dateSeperator+"01"+dateSeperator+temp[2];
firstDayOfthisMonth=strtodate(newDate, dateFormat);

if(salesActivity_quote == "Existing Customer"){
	if(effectiveServiceDate_quote <> "" AND comparedates(formattedeffectiveServiceDate,firstDayOfthisMonth) == -1){
		return true;
	}
}

return false;