frequencyFloat = 0.0;
if(isnumber(frequency)){
	frequencyFloat = atof(frequency);
}	
if(frequencyFloat >= 1.0){
	tempArr = split(tentativePickupDays, "~");
	size = sizeofArray(tempArr);
	if(size <> frequencyFloat){
		return tentativePickupDays;
	}
}
return "";