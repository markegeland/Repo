//20150327 - #59 - Rule to stop close date to be on or after effective date.
result = false;
dateFormat="%m/%d/%Y";
formattedEffectiveDate = strtodate(substring(effectiveServiceDate_quote,0,10), dateFormat);
formattedServiceCloseDate = strtodate(substring(serviceCloseDate_quote,0,10), dateFormat);

if(_system_current_step_var == "generateDocuments" AND chooseCSA_quote)
{
	if(serviceCloseDate_quote <> "" AND comparedates(formattedServiceCloseDate ,formattedEffectiveDate) <> -1){
    		result = true;
    	}
}

return result;