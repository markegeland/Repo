userGroupsArr = string[];
systemUserGroupsArr = split(_system_user_groups,"+");

for each in systemUserGroupsArr{
      append(userGroupsArr,substring(each,1,5));
}

recSet = bmql("select DISTINCT Cur_Area_Nbr from Corporate_Hierarchy WHERE Cur_Div_Nbr in $userGroupsArr");
ind =0;
area="";
for each in recSet{
	ind = ind + 1;
	area = get(each,"Cur_Area_Nbr");
}
if(output == "number"){
	return string(ind);
}
elif(output == "area" and ind == 1){
	return area;
}

return "";