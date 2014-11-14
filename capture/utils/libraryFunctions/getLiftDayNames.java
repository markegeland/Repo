weeknamesArray = string[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
liftDaysArray = split(liftDays, "");
returnArray = string[];
if(sizeofarray(liftDaysArray) > 0){
	counter = 0;
	for each in liftDaysArray{
		if(each <> ""){
			if(each == "1"){
				append(returnArray, weeknamesArray[counter]);		
			}
			counter = counter + 1;
		}
	}
}
return join(returnArray, ",");