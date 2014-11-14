// Effective date should always be greater than today's date
/*dateFormat="%m/%d/%Y";
formattedCurrentDate = strtodate(substring(datetostr(getdate()),0,10),dateFormat);
formattedeffectiveServiceDate = strtodate(substring(effectiveServiceDate_quote,0,10), dateFormat);

if(salesActivity_quote == "New/New" OR salesActivity_quote == "New from Competitor"){
	if(effectiveServiceDate_quote <> "" AND comparedates(formattedCurrentDate, formattedeffectiveServiceDate) == 1){
		return true;
	}
}
return false;
*/

//changed 6/27/14, to affect all sales activity instead of just NEW/NEW and New from Competitor
results = false;
//This rule should run only if CSA is selected to be printed/emailed
if(_system_current_step_var == "generateDocuments" AND chooseCSA_quote)
{
	results = true;
}

return results;