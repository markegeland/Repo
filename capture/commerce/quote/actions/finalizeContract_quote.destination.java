/* 
=====================================================================================================

       Name: finalizeContract_quote.destination
     Author: Rob Brozyna
    Created: 11 May 2015
Description: Used to redirect the user when they finalize the quote back to SFDC or the Quote Manager
        
      Input: sourceSystem_quote
             _system_company_name   
             crmOpportunityId_quote
                    
     Output: String with the redirection URL

Updates:    
20150512 - JPalubinskas - #518 updated the Capture redirect to always use the same Process ID
        
=====================================================================================================
*/
urlString = "";
totalURL = "";

if (sourceSystem_quote == "SFDC") {
	if(_system_company_name == "testsfdcrepublicservices")
	{
		urlString = "cs8";
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
	
	totalURL = "https://" + urlString + ".salesforce.com/" + crmOpportunityId_quote;
}
elif (sourceSystem_quote == "CAPTURE") {
	// Note that the process ID is identical in all environments since they have all originated from 
	// the same site.
	urlString = "4653759";
	
	totalURL = "https://" + _system_company_name + ".bigmachines.com/commerce/buyside/commerce_manager.jsp?bm_cm_process_id=" + urlString + "&from_hp=true&_bm_trail_refresh_=true";
}

return totalURL;
