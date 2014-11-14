retStr = "";
status = "";
retStr = retStr +  commerce.setSiteCustomerReadOnlyAttributes()+ commerce.copySiteInfoToBillingInfo();
/*if(_system_current_step_var == "start_step"){
	status = "Pending";
}
retStr = retStr + "1~status_quote~" + status + "|"; */
return retStr;