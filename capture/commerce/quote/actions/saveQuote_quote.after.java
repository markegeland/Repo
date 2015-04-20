// Calculate Price After Action (saveQuote_quote)


// Status is updated here as the step transitioning is happening on this action after 
// adding products to the quote.
returnStr = "";

status = status_quote;
if(_system_current_step_var == "selectServices"){
    status = "Work In Progress";
}

returnStr = returnStr + commerce.setDeliveryNotes();
returnStr = returnStr + commerce.calc_Commission();
returnStr = returnStr + commerce.postPricingFormulas("");
returnStr = returnStr + commerce.setSiteCustomerReadOnlyAttributes();
returnStr = returnStr + commerce.captureAdHocConfigInfoToPrintOnOutput();

return returnStr + "1~status_quote~" + status + "|";
