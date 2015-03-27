// 20150325 - John Palubinskas - #449 remove check for new from competitor

//Get the Latitude for site location
latitude = _quote_process_siteAddress_quote_company_name;
if(lower(salesActivity_quote) == "new/new" OR lower(salesActivity_quote) == "existing customer" OR latitude == ""){
	if(geoCodeForCustomerAddress_quote <> ""){
		latitudeArr = util.getValueFromXMLNodeByTag(geoCodeForCustomerAddress_quote, "Latitude");
		if(sizeofarray(latitudeArr ) > 0){
			latitude = latitudeArr[0];
		}
	}
}
return latitude;