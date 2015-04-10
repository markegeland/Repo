//Action: SalesEngine Commerce Process -> Quote -> Submit: Pre-Modify Advanced Function
result = "";

submitDate = strtojavadate(substring(_system_date,0,10), "MM/dd/yyyy");
				 

//Approval Date Stamp and Expiration Date calculations
	if(submittedDate_quote == "") {
		result = result + "1~submittedDate_quote~" + substring(datetostr(submitDate),0,10) + "|";
	}
	else {
		submitDate = strtojavadate(substring(submittedDate_quote,0,10), "MM/dd/yyyy");
	}
	approvalDate = strtojavadate(substring(_system_date,0,10), "MM/dd/yyyy");
	expireDate = adddays(approvalDate, adminExpirationTime_quote);
	numApprovalDays = getdiffindays(approvalDate,submitDate);

	result = result + "1~expirationDate_quote~" + substring(datetostr(expireDate),0,10) + "|"
			+ "1~approvalDate_quote~" + substring(datetostr(approvalDate),0,10) + "|"
			+ "1~numberOfDaysForApproval_quote~" + string(numApprovalDays) + "|";

//--------------------------- END: APPROVAL DATES --//

return result;