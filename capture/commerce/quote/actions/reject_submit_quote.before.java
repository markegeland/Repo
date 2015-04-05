/*
===================================================================================
Name:         reject_submit_quote.before.java
Author:       Mike Boylan
Create date:  04/04/15
Description:  The before formulas logic for the Reject action on approval. Sets 
              status.
        
Input:   	None
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	
===================================================================================
*/
return "1~reportingStatus_quote~Rejected|";
