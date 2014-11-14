// Function to add six months to the today's date
DATE_FORMAT = "mm/dd/yyyy"; 
DELIM= "/";
result="";
/*----------------------------------Return zero if the input date is empty----------------------------------*/
if(effDate == ""){
	return "";
}
effYear = atoi(substring(effDate,6,10));
effMonth = atoi(substring(effDate,0,2));
effDay = atoi(substring(effDate,3,5));
//we have today's day,month,year with us,Now lets add six months to it
if(effMonth <=6){
	effMonth = effMonth + 6;
	effYear=effYear;
}else{
	effMonth = effMonth+6;
		if(effMonth > 12){
			effMonth= effMonth - 12 ;
		}
	effYear = effYear + 1;
}
//-------leap year check-------------
if(isleap(effYear)){
	if(effMonth > 1){
		effDay=effDay+1;
	}
}else{
	effDay = effDay - 1;
}

result = string(effMonth)+ DELIM + string(effDay)+DELIM +string(effYear); 
print result;
return result;