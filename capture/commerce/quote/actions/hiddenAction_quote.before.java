/*
===================================================================================
Name:   hiddenAction_quote.before.java
Author:   
Create date:  
Description:  The before formulas logic for the action that moves configuration to
              commerce.  Sets the status and runs proPricingFormulas
        
Input:   	
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	04/04/15 - Mike (Republic) - added reporting status
===================================================================================
*/
inputParams = dict("string");
return "1~reportingStatus_quote~Configured|" + commerce.prePricingFormulas("save");
