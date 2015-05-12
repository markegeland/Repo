/*
=======================================================================================================================
       Name: newWorkflowImage_quote Advanced Default function
     Author: ???
Create date: ???

Description: generates the HTML img tag for the workflow image based on the current step

      Input: _system_supplier_company_name
             _system_current_step_var
             sourceSystem_quote

     Output: HTML String containing the workflow image tag.
             Also outputs CSS to hide the header for SFDC quotes.

Updates:
20150504 - John Palubinskas - #518 CRM integration.  Handle new start step, and hide the Capture header for all SFDC quotes.

=======================================================================================================================
*/

ret = "<img src='/bmfsweb/" + lower(_system_supplier_company_name) + "/image/images/";
alt = "";
if(_system_current_step_var == "start_step"){
    ret = ret + "timeline_1";
    alt = "New Opportunity";
}elif(_system_current_step_var == "startNewQuote"){
    ret = ret + "timeline_1";
    alt = "New Opportunity";
}
elif(_system_current_step_var == "newCustomerAndSite"){
    ret = ret + "timeline_2";
    alt = "Customer & Site";
}elif(_system_current_step_var == "newCustomerAndSite_bmClone_3"){
    ret = ret + "timeline_2";
    alt = "Customer & Site";    
}elif(_system_current_step_var == "newCustomerAndSite_bmClone_1"){
    ret = ret + "timeline_2";
    alt = "Customer & Site";    
}elif(_system_current_step_var == "newCustomerAndSite_bmClone_2"){
    ret = ret + "timeline_2";
    alt = "Customer & Site";
}elif(_system_current_step_var == "newCustomerAndSite_bmClone_4"){
    ret = ret + "timeline_2";
    alt = "Customer & Site";
}
elif(_system_current_step_var == "selectServices_bmClone_1"){
    ret = ret + "timeline_3";
    alt = "Select Services";
    
}elif(_system_current_step_var == "selectServices_bmClone_2"){
    ret = ret + "timeline_3";
    alt = "Select Services";
}elif(_system_current_step_var == "selectServices_bmClone_3"){
    ret = ret + "timeline_3";
    alt = "Select Services";
}
elif(_system_current_step_var == "selectServices"){
    ret = ret + "timeline_3";
    alt = "Select Services";
}
elif(_system_current_step_var == "adjustPricing"){
    ret = ret + "timeline_4";
    alt = "Provide Pricing";
}
elif(_system_current_step_var == "underManagerReview_process"){
    ret = ret + "timeline_5";
    alt = "Obtain Approvals";
}
elif(_system_current_step_var == "generateDocuments"){
    ret = ret + "timeline_6";
    alt = "Generate Documents";
}
elif(_system_current_step_var == "submitted_process"){
    ret = ret + "timeline_7";
    alt = "Finalize Opportunity";
}
else{
    ret = ret + "approval-workflow-in-progress";
    alt = "Obtain Approvals";
}

ret = ret + ".png' width='100%' alt='" + alt + "' >";
if(sourceSystem_quote == "SFDC"){
    ret = ret + "<style type = \"text/css\">a[href=\"/commerce/buyside/commerce_manager.jsp?bm_cm_process_id=4653759&from_hp=true&_bm_trail_refresh_=true\"]{ display:none; } #header-wrapper{display:none;}</style>";
}

return ret;