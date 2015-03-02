/* 
================================================================================
       Name: printCSA_quote before function - Print Documents Action
     Author:   
Create date:  
Description: Runs before the Print Documents action in order to clear the checkboxes if the
             proposal or csa checkbox is deselected.
        
Input:      
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20150225 - JPalubinskas - #25 branding: consolidated marketing collateral attributes
        
=====================================================================================================
*/

ret = "";

if(NOT(chooseProposal_quote)){
	ret = ret + "1~includeCoverLetter_quote~false|";
}

if(NOT(chooseCSA_quote)){
	ret = ret + "1~includeSampleInvoice_quote~false|";
}

return ret;