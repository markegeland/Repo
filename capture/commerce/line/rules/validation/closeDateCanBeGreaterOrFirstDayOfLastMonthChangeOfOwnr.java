/*Change made for Large Existing Release*/
dateFormat="%m/%d/%Y";
dateSeperator="/";
formattedCurrentDate = strtodate(substring(datetostr(getdate()),0,10), dateFormat);
formattedServiceCloseDate = strtodate(substring(oldOwnerCloseDate_line,0,10), dateFormat);

todayDate=datetostr(formattedCurrentDate);
temp=split(substring(todayDate,0,10),dateSeperator);
if(atoi(temp[0]) < 10){
	temp[0]="0"+string(atoi(temp[0])-1);
}
else{
	temp[0]=string(atoi(temp[0])-1);
}
newDate=temp[0]+dateSeperator+"01"+dateSeperator+temp[2];
firstDayOfthisMonth=strtodate(newDate, dateFormat);
lastdayOfMonthBefore=adddays(firstDayOfthisMonth,-1);
if(salesActivity_quote == "Change of Owner"){
	if(comparedates(formattedServiceCloseDate ,lastdayOfMonthBefore) == -1){
		return true;
	}
}

return false;