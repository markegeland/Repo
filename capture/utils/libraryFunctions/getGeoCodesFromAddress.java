/* 
========================================================================================================
       Name: getGeoCodesFromAddress
     Author: ???
Create date: ???
Description: Util function to call the Bing Maps API and return the XML response
             containing the address lat/long info.

Updates
20150521 John Palubinskas - #615 add additional character replacement for when we get punctuation in the
                            site address, so it won't blow up on the Bing API call.
========================================================================================================
*/

res = "";
siteStreet = "";
siteCity = "";
siteState = "";
zipcode = "";
system_supplier_company = ""; // environment site name
KEY = "";
locationURL = "";

if(containskey(addressDict, "siteStreet")) { siteStreet = get(addressDict, "siteStreet"); }
if(containskey(addressDict, "siteCity"))   {   siteCity = get(addressDict, "siteCity");   }
if(containskey(addressDict, "siteState"))  {  siteState = get(addressDict, "siteState");  }
if(containskey(addressDict, "zipcode"))    {    zipcode = get(addressDict, "zipcode");    }
if(containskey(addressDict, "system_supplier_company")){ 
    system_supplier_company = get(addressDict, "system_supplier_company"); }

if(system_supplier_company == ""){ system_supplier_company = "testrepublicservices"; }

//Get the Key & URL from maps table
resultset = bmql("SELECT url, key FROM maps WHERE site = $system_supplier_company AND type = 'location'");
for result in resultset{
    KEY = get(result, "key");
    locationURL = get(result, "url");
}

// URL encode characters: space # . , -
siteStreet = replace(siteStreet, " ", "%20");
siteStreet = replace(siteStreet, "#", "%23");
siteStreet = replace(siteStreet, ".", "%2E");
siteStreet = replace(siteStreet, ",", "%2C");
siteStreet = replace(siteStreet, "-", "%2D");
siteCity = replace(siteCity, " ", "%20");
siteCity = replace(siteCity, "#", "%23");
siteCity = replace(siteCity, ".", "%2E");
siteCity = replace(siteCity, ",", "%2C");
siteCity = replace(siteCity, "-", "%2D");

//http://dev.virtualearth.net/REST/v1/Locations?query=locationQuery&includeNeighborhood=includeNeighborhood&include=includeValue&maxResults=maxResults&key=BingMapsKey
trailingParameters = "&inclnb=0&maxRes=1&o=xml&key=" + KEY;

if (siteState == "PR") { // try more specific encoding for Puerto Rico addresses
    // I solved it for some addresses by stating that the country (or CountryRegion) was PR and not US.  
    // Also don't code the state (or AdminDistrict) as PR
    // country / adminDistrict (state) / locality (city) / postalCode / addressLine
    locationURL = locationURL + "/PR/-/" + siteCity + "/" + zipcode + "/" + siteStreet + "/";
}
else {
    locationURL = locationURL + "?q=" + siteStreet + "%20" + siteCity + "%20" + siteState + "%20" + zipcode;
}


print locationURL + trailingParameters;

outputXML = urldatabyget(locationURL, trailingParameters, "n/a");
return outputXML;