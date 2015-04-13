/*
===================================================================================
Name:   request_approval_submit_quote.before.java
Author:   
Create date:  
Description:  The before formulas logic for the Submit action on the approval
              dialog box.  Sets the status and the submitted date.
        
Input:   	_system_date: Date - current date
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	04/04/15 - Mike (Republic) - added reporting status
===================================================================================
*/
submitDate = strtojavadate(substring(_system_date,0,10), "MM/dd/yyyy");
result = "";
result = result + "1~reportingStatus_quote~Submitted|";

//Approval Submitted Date Stamp
result = result + "1~submittedDate_quote~" + substring(datetostr(submitDate),0,10) + "|";

return result + commerce.prePricingFormulas("submit");

