/*
===================================================================================
       Name: next.before.java
     Author: ???
Create date: ???
Description: The before formulas logic for the general Next action.
        
      Input: _system_company_name - String - system being used
             _system_current_step_var - String - current step 
             _quote_process_customer_id - customer ID
             division_quote - String - Lawson Division
             salesActivity_quote - String - New, Existing Customer, Change of Owner
             SiteNumber_quote - customer site number        
                    
     Output: orderManagementEmail_quote
             quoteAssignedTo_Quote
             siteContainerGroupsHTMLText_quote
             siteContainersData_quote
             reportingStatus_quote

Updates: 
  05/08/15 - John Palubinskas - update email to send dev/test approvals to captureSupport instead of spamming gmail.
                                Clean up setting reportingStatus_quote.

===================================================================================
*/
retStr = "";
status = "";
orderMgmtEmailStr = "";
quoteAssignEmailStr = "";
reportingStatus = reportingStatus_quote;

retStr = retStr + commerce.setSiteCustomerReadOnlyAttributes();
retStr = retStr + commerce.copySiteInfoToBillingInfo();
retStr = retStr + commerce.settingDefaultsForInitialAndRenewalTermsBasedOnLawsonDivision();
retStr = retStr + commerce.feeDefaults();

//Order management email setup
if(lower(_system_company_name) == "testrepublicservices" OR lower(_system_company_name) == "devrepublicservices"){
    orderMgmtEmailStr = "capturesupport@republicservices.com";
    quoteAssignEmailStr = "capturesupport@republicservices.com";
}elif(lower(_system_company_name) == "republicservices"){
    orderMgmtEmailStr = "CSA" + division_quote + "@republicservices.com";
    quoteAssignEmailStr = "Assign" + division_quote + "@republicservices.com"; 
}

retStr = retStr + "1~orderManagementEmail_quote~" + orderMgmtEmailStr + "|";
retStr = retStr + "1~quoteAssignedTo_Quote~" + quoteAssignEmailStr + "|";

siteContainershtmlStr = "";
siteContainerstextStr = "";
if(salesActivity_quote == "Existing Customer" OR salesActivity_quote == "Change of Owner"){

    containersInputDict = dict("string");
    put(containersInputDict, "_quote_process_customer_id", _quote_process_customer_id);
    put(containersInputDict, "SiteNumber_quote", SiteNumber_quote);
    put(containersInputDict, "containerGroupForTransaction_quote", containerGroupForTransaction_quote);
    containersOutputDict = util.getContainerGroups(containersInputDict);
    
    if(containskey(containersOutputDict, "html")){
        siteContainershtmlStr = get(containersOutputDict, "html");
    }
    if(containskey(containersOutputDict, "text")){
        siteContainerstextStr = get(containersOutputDict, "text");
    }
}
retStr = retStr + "1~siteContainerGroupsHTMLText_quote~" + siteContainershtmlStr + "|"
                + "1~siteContainersData_quote~" + siteContainerstextStr + "|";

//Set the reportingStatus_quote
if(_system_current_step_var == "newCustomerAndSite"){
    reportingStatus = "Created";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_3"){
    reportingStatus = "Created";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_1"){
    reportingStatus = "Created";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_2"){
    reportingStatus = "Created";
}
elif(_system_current_step_var == "newCustomerAndSite_bmClone_4"){
    reportingStatus = "Created";
}
elif(_system_current_step_var == "selectServices"){
    reportingStatus = "Configured";
}
elif(_system_current_step_var == "selectServices_bmClone_1"){
    reportingStatus = "Configured";
}
elif(_system_current_step_var == "selectServices_bmClone_2"){
    reportingStatus = "Configured";
}
elif(_system_current_step_var == "selectServices_bmClone_3"){
    reportingStatus = "Configured";
}
retStr = retStr + "1~reportingStatus_quote~" + reportingStatus + "|";

return retStr;
