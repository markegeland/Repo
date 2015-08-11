/*Change made for Large Existing Release*/
result = false;
dateFormat="%m/%d/%Y";
formattedEffectiveDate = strtodate(substring(effectiveServiceDate_line,0,10), dateFormat);
formattedServiceCloseDate = strtodate(substring(oldOwnerCloseDate_line,0,10), dateFormat);

if(salesActivity_quote == "Change of Owner" AND comparedates(formattedServiceCloseDate ,formattedEffectiveDate) <> -1){
	result = true;
}

return result;
}