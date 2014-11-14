userGroupsArr = string[];
userGroupsArr = commerce.getUserGroups();
//For corporate users - allow all areas to be selected
if(findinarray(userGroupsArr, "0") > -1){
	return false;
}
elif(commerce.checkIfAreaHasOnlyOneOption("number") == "1" OR commerce.checkIfAreaHasOnlyOneOption("number") == "0"){
	return true;
}

return false;