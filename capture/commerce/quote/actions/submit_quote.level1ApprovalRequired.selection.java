ret = "";
retArray = string[];
//Get all level 1 approvers from attribute level1Approver_quote 
/*if(level1Approver_quote <> ""){
	level1ApproverArr = split(level1Approver_quote, ",");
	for each in level1ApproverArr{
		
		append(retArray, "1~"+ each + "~" + _system_company_name);
	}
}*/
level1ApproverArr = split(level1Approver_quote, ",");
	for each in level1ApproverArr{
		
		append(retArray, "1~"+ each + "~" + _system_company_name);
	}
// Concatenate and return  all the approvers
if(sizeofarray(retArray) > 0){
	ret = join(retArray, "|");
}
return ret;