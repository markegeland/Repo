userGroupsArr = string[];
systemUserGroupsArr = split(_system_user_groups,"+");
for each in systemUserGroupsArr{
	division = substring(each,1,5);
	if(isnumber(division)){
		append(userGroupsArr,division);
	}else{
		division = substring(division, 0, 1);
		if(isnumber(division)){
			append(userGroupsArr,division);
		}	
	}
}
return userGroupsArr;