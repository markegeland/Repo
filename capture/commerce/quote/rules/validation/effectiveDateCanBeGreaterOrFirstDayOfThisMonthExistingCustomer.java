//20150326 - #59 Changed the limit from 1st of this month to 1st of last month.
dateFormat="%m/%d/%Y";
dateSeperator="/";
formattedCurrentDate = strtodate(substring(datetostr(getdate()),0,10), dateFormat);
formattedeffectiveServiceDate = strtodate(substring(effectiveServiceDate_quote,0,10), dateFormat);

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

if(_system_current_step_var == "generateDocuments" AND chooseCSA_quote){
	if(effectiveServiceDate_quote <> "" AND comparedates(formattedeffectiveServiceDate,firstDayOfthisMonth) == -1){
		return true;
	}
}

return false;