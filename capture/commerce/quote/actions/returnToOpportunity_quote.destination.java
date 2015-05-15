// -----------------------------------------------------------------------------------------------
//    Name: returnToOpportunity_quote destination function
//
// Purpose: Sets the destination URL for the Return to Opportunity button action
// Created: 5/1/2015 - #518 CRM integration
//
// Updates:
// 20150512 - John Palubinskas - #518 update testSFDC url to point to cs7
//
// -----------------------------------------------------------------------------------------------

urlString = "";

if(_system_company_name == "testsfdcrepublicservices")
{
	urlString = "cs7";
}
elif(_system_company_name == "devrepublicservices")
{
	urlString = "cs8";
}
elif(_system_company_name == "testrepublicservices")
{
	//TODO
	urlString = "";
}
elif(_system_company_name == "republicservices")
{
	//TODO
	urlString = "";
}

return "https://" + urlString + ".salesforce.com/" + crmOpportunityId_quote;