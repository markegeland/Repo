retStr = "";
status = "";
orderMgmtEmailStr = "";
quoteAssignEmailStr = "";
retStr = retStr +  commerce.setSiteCustomerReadOnlyAttributes()+ commerce.copySiteInfoToBillingInfo() + commerce.settingDefaultsForInitialAndRenewalTermsBasedOnLawsonDivision();

//Order management email setup
if(lower(_system_company_name) == "testrepublicservices" OR lower(_system_company_name) == "devrepublicservices"){
	orderMgmtEmailStr = "approvals.testing@gmail.com";
	quoteAssignEmailStr = "approvals.testing@gmail.com";
}elif(lower(_system_company_name) == "republicservices"){
	orderMgmtEmailStr = "CSA" + division_quote + "@republicservices.com"; // + "," +"dl_bmi_ops_" + division_quote + "@republicservices.com";
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
retStr = retStr + "1" + "~" + "siteContainerGroupsHTMLText_quote" + "~" + siteContainershtmlStr + "|"
	            + "1" + "~" + "siteContainersData_quote" + "~" + siteContainerstextStr + "|";

retStr = retStr + commerce.feeDefaults();

return retStr;