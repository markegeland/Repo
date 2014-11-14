//Returns SOAP XML
latitude1 = "";
latitude2 = "";
longitude1 = "";
longitude2 = "";
locationURL = "";
KEY = "";
travelDist = 0.0;
travelDuration = 0.0;
unitOfTravelDuration = "";
distanceUnit= "";
DEBUG = false;
system_supplier_company = "";
if(containskey(coordinatesDict, "latitude1")){
	latitude1 = get(coordinatesDict, "latitude1");	
}
if(containskey(coordinatesDict, "latitude2")){
	latitude2 = get(coordinatesDict, "latitude2");	
}
if(containskey(coordinatesDict, "longitude1")){
	longitude1 = get(coordinatesDict, "longitude1");	
}
if(containskey(coordinatesDict, "longitude2")){
	longitude2 = get(coordinatesDict, "longitude2");	
}
if(containskey(coordinatesDict, "longitude2")){
	longitude2 = get(coordinatesDict, "longitude2");	
}
if(containskey(coordinatesDict, "DEBUG")){
	DEBUGStr = get(coordinatesDict, "DEBUG");	
	if(DEBUGStr == "true"){
		DEBUG = true;
	}
}

if(containskey(coordinatesDict, "system_supplier_company")){
	system_supplier_company = get(coordinatesDict, "system_supplier_company");	
}
if(system_supplier_company == ""){
	system_supplier_company = "testrepublicservices";
}
//Get the location URL from maps data table
resultset = bmql("SELECT url, key FROM maps WHERE site = $system_supplier_company AND type = 'route'");
for result in resultset{
	locationURL = get(result, "url");
	KEY = get(result, "key");
}
//Form parameters for the webservice call
parameters = "o=xml&wp.0=" + latitude1 + "," + longitude1 + "&wp.1=" + latitude2 + "," + longitude2 + "&avoid=minimizeTolls&key="+KEY;
if(DEBUG){
	print parameters;
}

outputFromService =  urldatabyget(locationURL, parameters, "n/a");
if(DEBUG){
	print "--outputFromService--"; print outputFromService;
}
return outputFromService;