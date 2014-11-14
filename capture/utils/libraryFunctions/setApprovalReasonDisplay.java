returnStr = "";

returnStr = returnStr + "<b>Sales Supervisor or Manager</b>"; //<br>
returnStr = returnStr + "<ul>";

if(NOT isempty(level1ApprovalReasonArr)){
	for level1ApprovalReason in level1ApprovalReasonArr{
		returnStr = returnStr + "<li>" + level1ApprovalReason + "</li>";
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
		returnStr = returnStr + "<li>" + level2ApprovalReason + "</li>";
	}
}
else{
	returnStr = returnStr + "<li>" + "No approval required" + "</li>";
}
returnStr = returnStr + "</ul>";


return returnStr;