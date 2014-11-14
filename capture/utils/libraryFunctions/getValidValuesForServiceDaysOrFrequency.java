NO= "no";
divServiceDaysRecordSet = bmql("SELECT service_day_count,service_day_count,mon_service_ind,tue_service_ind,wed_service_ind,thu_service_ind,fri_service_ind, sat_service_ind,sun_service_ind, EveryOtherWeek,EveryFourWeeks FROM div_service_days WHERE division = $division OR division = '0' ORDER BY division DESC");
print divServiceDaysRecordSet ;
ret = "";
pickupDaysArr = string[];
for each in divServiceDaysRecordSet{
	if(get(each, "mon_service_ind") == NO){
		append(pickupDaysArr, "Mon");
	}
	if(get(each, "tue_service_ind") == NO){
		append(pickupDaysArr, "Tue");
	}
	if(get(each, "wed_service_ind") == NO){
		append(pickupDaysArr, "Wed");
	}
	if(get(each, "thu_service_ind") == NO){
		append(pickupDaysArr, "Thu");
	}if(get(each, "fri_service_ind") == NO){
		append(pickupDaysArr, "Fri");
	}
	if(get(each, "sat_service_ind") == NO){
		append(pickupDaysArr, "Sat");
	}
	if(get(each, "sun_service_ind") == NO){
		append(pickupDaysArr, "Sun");
	}
	if(get(each, "EveryOtherWeek") == NO){
		ret = ret + "EOW|^|";
	}
	if(get(each, "EveryFourWeeks") == NO){
		ret = ret + "Every 4 Weeks|^|";
	}
	
	break;
}
if(input == "pickUpDays"){
	return join(pickupDaysArr,"|^|");
}
elif(input == "frequency"){
	size = sizeofarray(pickupDaysArr);

	if(size == 6){
		ret = ret + "2/Week|^|3/Week|^|4/Week|^|5/Week|^|6/Week|^|7/Week|^|";
	}
	elif(size == 5){
		ret = ret + "3/Week|^|4/Week|^|5/Week|^|6/Week|^|7/Week|^|";
	}
	elif(size == 4){
		ret = ret + "4/Week|^|5/Week|^|6/Week|^|7/Week|^|";
	}
	elif(size == 3){
		ret = ret + "5/Week|^|6/Week|^|7/Week|^|";
	}
	elif(size == 2){
		ret = ret + "6/Week|^|7/Week|^|";
	}
	elif(size == 1){
		ret = ret + "7/Week|^|";
	}
	
	
	return ret;
}

return "";