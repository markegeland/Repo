//Latitude>37.552329674363136</Latitude><Longitude>-122.26170524954796<
res = "";
DELIM = "^_^";
siteNumber = "";
siteStreet = "";
siteCity = "";
siteState = "";
zipcode = "";
system_supplier_company = ""; //bigmachines site name
KEY = "";
locationURL = "";

if(containskey(addressDict, "siteNumber")){
	siteNumber = get(addressDict, "siteNumber");
}

if(containskey(addressDict, "siteStreet")){
	siteStreet = get(addressDict, "siteStreet");
}

if(containskey(addressDict, "siteCity")){
	siteCity = get(addressDict, "siteCity");
}

if(containskey(addressDict, "siteState")){
	siteState = get(addressDict, "siteState");
}

if(containskey(addressDict, "zipcode")){
	zipcode = get(addressDict, "zipcode");
}

if(containskey(addressDict, "system_supplier_company")){
	system_supplier_company = get(addressDict, "system_supplier_company");
}
if(system_supplier_company == ""){
	system_supplier_company = "testrepublicservices";
}
//Get the Key & URL from maps table
resultset = bmql("SELECT url, key FROM maps WHERE site = $system_supplier_company AND type = 'location'");
for result in resultset{
	KEY = get(result, "key");
	locationURL = get(result, "url");
}

streetLocal = replace(siteStreet, " ", "%20");
cityLocal = replace(siteCity, " ", "%20");

/*url = urldatabyget("http://dev.virtualearth.net/REST/v1/Locations/US/WA/98052/Redmond/1 Microsoft Way", "o=xml&key=AtYrIUXwKFjWY299MSA3DeLhe0nGq0vqNLD0-aZ2HYsI4weX03JfnIK29joVGL3w", "n/a");
return url;*/
//http://dev.virtualearth.net/REST/v1/Locations/US/WA/98052/Redmond/1%20Microsoft%20Way?o=xml&key=BingMapsKey
//locationURL = locationURL + siteState + "/" + zipcode + "/" + cityLocal + "/" + siteNumber + "%20" + streetLocal;
if(zipcode == ""){
	locationURL = locationURL + siteState + "/" + cityLocal + "/" + streetLocal;
}else{
	locationURL = locationURL + siteState + "/" + zipcode + "/" + cityLocal + "/" + streetLocal;
}
print locationURL+"?o=xml&key="+KEY;

outputXML = urldatabyget(locationURL, "o=xml&key="+KEY, "n/a");
return outputXML;