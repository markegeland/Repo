/*
================================================================================
       Name:  setApprovalReasonDisplay
     Author:  
Create date:  
Description:  Creates HTML to display containing approver titles and a list of approval reasons.
              This function is called by postPricingFormulas.
        
Input:   level1ApprovalReason: String Array - Array of level 1 approval reasons
		 level2ApprovalReason: String Array - Array of level 2 approval reasons
                    
Output:  String of HTML

Updates: 20150209 - John Palubinskas - Reworked function so we only have one place for creating the approval HTML
                                       for use in both the quote and approval email.            
        
=====================================================================================================
*/
returnStr = "";
reasonStyle = "";

// Level 1 Approval
returnStr = returnStr + "<b>Sales Supervisor or Manager</b>"
                      + "<ul class='approval-reason-list'>";

if(NOT isempty(level1ApprovalReasonArr)){
	for level1ApprovalReason in level1ApprovalReasonArr{
		if(level1ApprovalReason == "Core price set outside guardrails"){
			reasonStyle = " class='red'";
		}
		returnStr = returnStr + "<li" + reasonStyle + ">" + level1ApprovalReason + "</li>";
	}
}
else{
	returnStr = returnStr + "<li>" + "No approval required" + "</li>";
}
returnStr = returnStr + "</ul>";


// Level 2 Approval
returnStr = returnStr + "<b>General Manager</b>"
                      + "<ul class='approval-reason-list'>";

if(NOT isempty(level2ApprovalReasonArr)){
	for level2ApprovalReason in level2ApprovalReasonArr{
		if(level2ApprovalReason == "Core price set outside guardrails"){
			reasonStyle = " class='red'";
		}
		returnStr = returnStr + "<li" + reasonStyle + ">" + level2ApprovalReason + "</li>";
	}
}
else{
	returnStr = returnStr + "<li>" + "No approval required" + "</li>";
}
returnStr = returnStr + "</ul>";


return returnStr;