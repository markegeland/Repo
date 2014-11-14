/*This functionality will allow the user to view sales of similar products within a specified radius (example: within 1 kilometer) of the current customer location
Post Pricing script calls this function passing string dictionary with inputs
Division
Customer Latitude & Longitude
Selected Configuration delimited string (Waste Type, Quantity, Container Size, Frequency)
Range/Radius can be customized by adjusting default value for radiusInMiles_quote quote attribute 
Query Account_Status table (with existing customer data) to get related configurations 
*/

/*
This uses the ‘haversine’ formula to calculate the great-circle distance between two points – that is, 
the shortest distance over the earth’s surface
*/

// Variables intialization
distanceArr = float[];
locationValuesDict = dict("string");

lat1= 0.0;
lon1 = 0.0;
lat2 = 0.0;
lon2 = 0.0;
qty = 0;
containerGroup="";
containerSize = "";
wasteType="";
frequency = "";
qtyStr = "";
maxDistance =0.0;
division=0;
// Get all the necessry inputs from the input Dictionary
if(containskey(inputDict, "lat1")){
	if(isnumber(get(inputDict, "lat1"))){
		lat1 = atof(get(inputDict, "lat1"));
	}
}

if(containskey(inputDict, "lon1")){
	if(isnumber(get(inputDict, "lon1"))){
		lon1 = atof(get(inputDict, "lon1"));
	}
}

if(containskey(inputDict, "range")){
	if(isnumber(get(inputDict, "range"))){
		maxDistance = atof(get(inputDict, "range"));
	}
}

if(containskey(inputDict, "division") and isnumber(get(inputDict, "division"))){
	division = atoi(get(inputDict, "division"));
}
//This is a delimited string of Waste type $$ Quantity $$ Container Size $$ Frequency
if(containskey(inputDict, "containerGroup")){
	containerGroup = (get(inputDict, "containerGroup"));
}
containerGrpArray = string[];
if(containerGroup <> ""){
	containerGrpArray = split(containerGroup, "$$");
}
if(sizeofarray(containerGrpArray) >= 4){
	wasteType = containerGrpArray[0]; 
	wasteType = replace(wasteType, "&#32;"," ");
	if(wasteType == "All in One - Single stream"){
		wasteType = "Recycling";
	}
	qtyStr = containerGrpArray[1]; 
	containerSize = containerGrpArray[2];
	frequency = containerGrpArray[3];
}
frequencyFactor = 0.0;

resultset = bmql("SELECT Frequency, conversionFactor FROM Frequency_Conversion");
for eachRec in resultset{
	frequency_db = get(eachRec, "Frequency");
	if(lower(frequency_db) == lower(frequency)){
		frequencyFactor = getfloat(eachRec, "conversionFactor");
	}
}
	

if(isnumber(qtyStr)){
	qty = atoi(qtyStr);
}
containerSizeFloat = 0.0;
if(isnumber(containerSize)){
	containerSizeFloat = atof(containerSize);
}
//print "==INSIDE UTIL FN==";
/*print "frequency=="+frequency;
print "wasteType=="+wasteType;
print "containerSize=="+containerSize;
print "qtyStr=="+qtyStr;
print "maxDistance=="+string(maxDistance);
print "frequencyFactor=="+string(frequencyFactor);
print "==INSIDE UTIL FN==";
*/
//Initialising the constants
earthRadius = 6371.0;
PI = 3.141592653589793;
factor = PI/180;
i = 0;
counter = 0;
sitesDict = dict("string[]"); //key: counter, value: locations array
	
//Fetch the values from Account_Status email for the appropriate model values
if(isnumber(qtyStr) ) {
	qty = atof(qtyStr);
}
//print qtyInt;
//resultset = bmql("SELECT  site_nm, Original_Open_Dt,monthly_rate,frf_rate_pct,is_erf_on_frf,erf_rate_pct, is_erf_charged, is_frf_charged, latitude, longitude,last_pi_dt_sk,orig_sale_dt_sk  FROM Account_Status WHERE container_cnt = $qtyInt AND division_nbr = $division AND Container_Grp_Nbr = $containerGroup AND waste_type = $wasteType AND Container_Size = $frequency");
resultset = bmql("SELECT  site_nm, Original_Open_Dt,monthly_rate,frf_rate_pct,is_erf_on_frf,erf_rate_pct, is_erf_charged, is_frf_charged, latitude, longitude,last_pi_dt_sk,orig_sale_dt_sk, period, Pickup_Period_Length, Pickup_Per_Tot_Lifts, container_cnt, is_Admin_Charged FROM Account_Status WHERE division_nbr = $division AND waste_type = $wasteType AND Container_Size = $containerSizeFloat AND container_cnt = $qty");

 //print resultset;

for result in resultset{	
	period = getFloat(result, "period");
	container_cnt = getFloat(result, "container_cnt");
	Pickup_Period_Length = getFloat(result, "Pickup_Period_Length");
	Pickup_Per_Tot_Lifts = getFloat(result, "Pickup_Per_Tot_Lifts");
	is_Admin_Charged = get(result, "is_Admin_Charged");
	is_erf_charged = get(result, "is_erf_charged");
	is_frf_charged = get(result, "is_frf_charged");
	frequencyFctr = 0.0;
	if(Pickup_Period_Length == 1.0){
		frequencyFctr = Pickup_Per_Tot_lifts/( container_cnt * period);	
	}elif(Pickup_Period_Length == 2.0 OR Pickup_Period_Length == 4.0){
		frequencyFctr = period;
	}
	//print "period_DB=="+string(frequencyFctr) +"AND config frequency=="+ string(frequencyFactor);
	if(frequencyFctr <> frequencyFactor){
		continue;
	}
	
	lat2 = getFloat(result, "latitude");
	lon2 = getFloat(result, "longitude");
	originalOpenDate = get(result, "Original_Open_Dt");
	siteName = get(result, "site_nm");
	currentRate = getfloat(result, "monthly_rate");
	totalRevenue = currentRate * (1 + getfloat(result,"frf_rate_pct")/100.0 * getint(result,"is_frf_charged") * (1 + getfloat(result,"is_erf_on_frf") * getfloat(result,"erf_rate_pct")/100.0 * getint(result,"is_erf_charged")) + getfloat(result,"erf_rate_pct")/100.0 * getint(result,"is_erf_charged"));
	totalRevenue = round(totalRevenue, 2);
	
	rateEffectiveDate = "";
	origOpenDateTemp="";
	if(get(result,"Original_Open_Dt") <> "99991231" AND get(result,"Original_Open_Dt") <> ""){
		origOpenDateStr = get(result,"Original_Open_Dt");
		if(len(origOpenDateStr) == 8){
			origOpenDateYearStr = substring(origOpenDateStr, 0, 4);
			origOpenDateMonthStr = substring(origOpenDateStr, 4, 6);
			origOpenDateDayStr = substring(origOpenDateStr, 6, 8);
			origOpenDateTemp = origOpenDateMonthStr + "/" + origOpenDateDayStr + "/" + origOpenDateYearStr;			
		}
	}
	if( get(result,"last_pi_dt_sk") <> "" ){
		if(get(result,"last_pi_dt_sk") <> "99991231" ){
			rateEffDate = get(result,"last_pi_dt_sk");
			if(len(rateEffDate) == 8){
				rateEffDateYearStr = substring(rateEffDate, 0, 4);
				rateEffDateMonthStr = substring(rateEffDate, 4, 6);
				rateEffDateDayStr = substring(rateEffDate, 6, 8);
				rateEffectiveDate = rateEffDateYearStr + "-" + rateEffDateMonthStr + "-" + rateEffDateDayStr;
			}
		}
	}
	else{
		if(get(result,"orig_sale_dt_sk") <> "99991231" ){
			rateEffDate = get(result,"orig_sale_dt_sk");
			if(len(rateEffDate) == 8){
				rateEffDateYearStr = substring(rateEffDate, 0, 4);
				rateEffDateMonthStr = substring(rateEffDate, 4, 6);
				rateEffDateDayStr = substring(rateEffDate, 6, 8);
				rateEffectiveDate = rateEffDateYearStr + "-" + rateEffDateMonthStr + "-" + rateEffDateDayStr;
			}
		}
	}
	adminCharegd = "No";
	erfCharged = "No";
	frfCharged = "No";
	
	if(is_Admin_Charged == "1"){
		adminCharegd = "Yes";
	}
	if(erfCharged == "1"){
		erfCharged = "Yes";
	}
	if(frfCharged == "1"){
		frfCharged = "Yes";
	}
	 
	tempArr = string[]{siteName,origOpenDateTemp,"$"+string(currentRate), "$"+string(totalRevenue),rateEffectiveDate, string(lat2), string(lon2),  adminCharegd, erfCharged, frfCharged};
	
	dLat = (lat2-lat1)*factor;
	dLon = (lon2-lon1)*factor; 

	a = sin(dLat/2) * sin(dLat/2) + cos(lat1*factor) * cos(lat2*factor) * sin(dLon/2) * sin(dLon/2); 

	asqrt = sqrt(a);
	bsqrt = sqrt(1-a);
	//c = 2 * atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	c =0.0;
	if(asqrt > 0){
		c = 2 * atan(asqrt/bsqrt);
	}elif(asqrt < 0 AND bsqrt >= 0){
		c = 2 * (atan(asqrt/bsqrt) + PI);
	}elif(asqrt < 0 AND bsqrt < 0){
		c = 2 * (atan(asqrt/bsqrt) - PI);
	}elif(asqrt == 0 AND bsqrt > 0){
		c = 2 * (PI);
	}elif(asqrt == 0 AND bsqrt < 0){
		c = 2 * (PI * -1);
	}elif(asqrt == 0 AND bsqrt == 0){
		c = NaN;
	}
	d = 0.0;
	d = earthRadius * c;
	// Check this - if this is less than 1 we should get output from this util function
	//print "d_____________" + string(d); 
	
	if(d <= maxDistance){
		/*returnArray[i][0] = lat2;
		returnArray[i][1] = lon2;
		returnArray[i][2] = d;*/
		put(locationValuesDict, d, join(tempArr,"@@"));
		//Displays only first 1000 locations
		if(sizeofarray(distanceArr) < 1000){
			append(distanceArr, (d) );
		}
		i = i + 1;
	}
}
sort(distanceArr);

j =0;
locationValuesArr = string[];

for eachVal in distanceArr{
	
	if(containskey(locationValuesDict, eachVal)){
		append(locationValuesArr,get(locationValuesDict,eachVal));
	}
	if(j == 5){
		break;
	}
	j = j +1;
	
}
//print locationValuesArr;
return join(locationValuesArr, "$$");