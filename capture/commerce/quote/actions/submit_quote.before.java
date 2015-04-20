//Action: SalesEngine Commerce Process -> Quote -> Submit: Pre-Modify Advanced Function
/*
===================================================================================
Name:   submit_quote.before.java
Author:   
Create date:  
Description:  The before formulas logic for the Submit action after a quote has 
              been approved. Sets a set of status variables
        
Input:   	_system_date: Date - current date
                submittedDate_quote = date the quote was submitted for approval
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	04/04/15 - Mike (Republic) - added reporting status
===================================================================================
*/
result = "";

result = result + "1~reportingStatus_quote~Approved|";

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
