// 20150325 - John Palubinskas - #449 remove check for new from competitor

//Get the Longitude for site location
longitude = _quote_process_siteAddress_quote_company_name_2;
if(lower(salesActivity_quote) == "new/new" OR lower(salesActivity_quote) == "existing customer" OR longitude == ""){
    if(geoCodeForCustomerAddress_quote <> ""){
        longitudeArr = util.getValueFromXMLNodeByTag(geoCodeForCustomerAddress_quote, "Longitude");
        if(sizeofarray(longitudeArr ) > 0){
            longitude = longitudeArr[0];
        }
    }
}
return longitude;