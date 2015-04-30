/* 
================================================================================
       Name:  finalizeContract_quote - After action
     Author:  ???
Create date:  ???
Description:  Runs after the finalizeContract action fires
        
  Input:  none
                    
 Output:  String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:  20150204 - John Palubinskas - #387 Added saleFinalizedDate_quote functionality.
		  20150429 - Gaurav Dawar - #458 - add the commentsForDeliveryAndExchangeCreditOnOutput function to generate the string for VCD
================================================================================
*/

res = "";

//Comments section on CSA needs only the letter in Lead Source Code
leadCodeOnCSA = "";
if(leadSourceCode_quote <> ""){
    leadSouceCodeArr = split(leadSourceCode_quote, " - ");
    leadCodeOnCSA = leadSouceCodeArr[0];
}

res = res
        + "1~isSaleFinalized_quote~true|"
        + "1~saleFinalizedBy_quote~" + _system_user_first_name + " "  + _system_user_last_name + "|"
        + "1~leadSourceCodeOnCSA_quote~" + leadCodeOnCSA + "|"
        + "1~saleFinalizedDate_quote~" + datetostr(getDate(),"MM/dd/yyyy HH:mm:ss") + "|"
        + commerce.printing()
        + commerce.setStatus("finalizeContract")
        + commerce.setTransactionCode()
        + commerce.unconfiguredServicesString()
		+ commerce.commentsForDeliveryAndExchangeCreditOnOutput();

return res;