// Status is updated here as the step transitioning is happening on this action after adding products to the quote.
status = status_quote;
if(_system_current_step_var == "selectServices"){
	status = "Work In Progress";
}
return commerce.postPricingFormulas("") + commerce.captureAdHocConfigInfoToPrintOnOutput() + "1~status_quote~" + status + "|";