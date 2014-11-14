currentDate = getdate();
numOfDaysIn15Months = integer((365 * 15)/12);
numOfDaysIn1Month = 30;
dateFrom15MonthsFromToday = adddays(currentDate , numOfDaysIn15Months);
dateFrom1MonthFromToday = adddays(currentDate , numOfDaysIn1Month);
if(nextReviewDate_editable_quote <> ""){
	//Editable NRD must NOT be greater than 15 months from current date.
	formattedNRD = strtojavadate(substring(nextReviewDate_editable_quote,0,10), "yyyy-MM-dd");
	if(comparedates(formattedNRD, dateFrom15MonthsFromToday) == 1){ //if user entered NRD is after 15 months from today
		return true;
	}
	//Editable NRD must be at least 1 month from today's date OR at least be 1st day of next month.
	if(comparedates(formattedNRD, currentDate) <> 1){//if user entered NRD is NOT after current date
		return true;
	}else{
		print "here?";
		diffinDays = getdiffindays(formattedNRD, currentDate);
		print diffinDays ;
		if(diffinDays < 30){
			currentDateSubStr = substring(datetostr(currentDate),0,10);
			currentDateArr = split(currentDateSubStr, "/");
			currentYear = 0;
			currentMonth = 0;
			currentDay = 0;
			
			nrdYear = 0;
			nrdMonth = 0;
			nrdDay = 0;
			
			if(isnumber(currentDateArr[0])){
				currentMonth = atoi(currentDateArr[0]);
			}
			if(isnumber(currentDateArr[1])){
				currentDay = atoi(currentDateArr[1]);
			}	
			if(isnumber(currentDateArr[2])){
				currentYear = atoi(currentDateArr[2]);
			}
			
			formattedNRDArr = split(substring(datetostr(formattedNRD), 0, 10), "/");
			print formattedNRDArr;
			if(isnumber(formattedNRDArr[0])){
				nrdMonth = atoi(formattedNRDArr[0]);
			}	
			if(isnumber(formattedNRDArr[1])){
				nrdDay = atoi(formattedNRDArr[1]);
			}	
			if(isnumber(formattedNRDArr[2])){
				nrdYear = atoi(formattedNRDArr[2]);
			}	
			print "--currentYear--"; print currentYear;
			print "--nrdYear--"; print nrdYear;
			if(currentYear == nrdYear){
				print "here    ....?";
				if((currentMonth + 1) <> nrdMonth){
					return true;
				}
			}else{
				if(currentMonth == 12){
					if(nrdMonth <> 1){
						return true;
					}
				}
			}
		}
	}	
}
return false;