/*
===================================================================================
Name:   trash_quote.before.java
Author:   
Create date:  
Description:  The before formulas logic for the Delete action.  Sets the status.
        
Input:   	None
                    
Output:  	String - Output parsed as HTML by the attribute to display table 
                of attributes

Updates:	04/04/15 - Mike (Republic) - added reporting status
===================================================================================
*/
return "1~reportingStatus_quote~Deleted|" + "1~quoteTrashed_quote~true|";
