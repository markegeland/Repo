userGroupsArr = string[];
userGroupsArr = commerce.getUserGroups();
//For corporate users - allow all areas to be selected
if(findinarray(userGroupsArr, "0") > -1){
	return false;
}

recSet = bmql("select Cur_Area_Nbr from Corporate_Hierarchy WHERE Cur_Div_Nbr in $userGroupsArr");

for each in recSet{
	if(get(each,"Cur_Area_Nbr") == "A53"){
		return false;

	}
}

return true;