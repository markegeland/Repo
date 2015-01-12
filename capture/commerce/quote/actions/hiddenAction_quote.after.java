/*
11/20/2014 AQ - Changed to set sellPrice to 0.00 for removal line items when exchnaging existiing customers
*/

result = "";
// Status is updated here as the step transitioning is happening on this action after adding products to the quote.
status = status_quote;
if(_system_current_step_var == "selectServices"){
	status = "Work In Progress";
}
//AQ 11/20/2014
for line in line_process{
	if (line.rateType_line == "Removal" AND line.hasDelivery_line){
		result = result + line._document_number + "~sellPrice_line~0.0|";
	}
}
return result + commerce.calc_Commission() + commerce.postPricingFormulas("save") + commerce.setSiteCustomerReadOnlyAttributes() + commerce.captureAdHocConfigInfoToPrintOnOutput() + "1~status_quote~" + status + "|";