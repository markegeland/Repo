/* 
Test Transaction: 567971149 (transaction ID in the dev environment)
================================================================================
Name:   Set Approval Reason Display With Color
Author:   Julie (Oracle)
Create date:  1/5/2015
Description:  Runs when post pricing formula commerce library is called.
        
Input:      level1ApprovalReason: String Array - Array of level 1 approval reasons
			level2ApprovalReason: String Array - Array of level 2 approval reasons
			Color: String - The special coloring applied            
                    
Output:     String of html; is used to populate a text area attribute

Updates:    20150105 - Copied code from the Set Approval Reason Display util and then added the color parameter and color specific code.
            
        
=====================================================================================================
*/


returnStr = "";

returnStr = returnStr + "<b>Sales Supervisor or Manager</b>"; //<br>
returnStr = returnStr + "<ul>";


if(NOT isempty(level1ApprovalReasonArr)){
	for level1ApprovalReason in level1ApprovalReasonArr{
		if(level1ApprovalReason == "Total Sell< Total Base" AND Color == "red"){
			returnStr = returnStr + "<li><font color=#FF0000>" + level1ApprovalReason + "</font></li>";
		}
		else{
			returnStr = returnStr + "<li>" + level1ApprovalReason + "</li>";
		}
	}
}
else{
	returnStr = returnStr + "<li>" + "No approval required" + "</li>";
}
returnStr = returnStr + "</ul>";

//returnStr = returnStr + "<br>";

returnStr = returnStr + "<b>General Manager</b>"; //<br>
returnStr = returnStr + "<ul>";
if(NOT isempty(level2ApprovalReasonArr)){
	for level2ApprovalReason in level2ApprovalReasonArr{
		if(level2ApprovalReason == "Total Sell< Total Base" AND Color == "red"){
			returnStr = returnStr + "<li><font color=#FF0000>" + level2ApprovalReason + "</font></li>";
		}
		else{
			returnStr = returnStr + "<li>" + level2ApprovalReason + "</li>";
		}
	}
}
else{
	returnStr = returnStr + "<li>" + "No approval required" + "</li>";
}
returnStr = returnStr + "</ul>";


return returnStr;
