ret = "";
retArray = string[];
//Get all level 2 approvers from attribute level2Approver_quote 
if(level2Approver_quote <> ""){
	level2ApproverArr = split(level2Approver_quote, ",");
	for each in level2ApproverArr{
		append(retArray, "1~"+ each + "~" + _system_company_name);
	}
}
// Concatenate and return  all the approvers
if(sizeofarray(retArray) > 0){
print "hai";
	ret = join(retArray, "|");
}

return ret;