// -----------------------------------------------------------------------------------------------
//    Name: returnToOpportunity_quote destination function
//
// Purpose: Sets the destination URL for the Return to Opportunity button action
// Created: 5/1/2015 - #518 CRM integration
//
// Updates:
// 20150512 - John Palubinskas - #518 update testSFDC url to point to cs7
// 20150604 - John Palubinskas - #653 change to pull return URL from new integration salesforce data table
//
// -----------------------------------------------------------------------------------------------

urlString = "";

returnLinkRS = bmql("SELECT value FROM salesforce WHERE type = 'returnLink' AND key = $_system_company_name");

for record in returnLinkRS{
	urlString = get(record, "value");
	break;
}

if (urlString == "") {
	urlString = "https://test.salesforce.com/";
}

return urlString + crmOpportunityId_quote;
