//Set Current Service Attributes - recommendation for accountType_current_readonly
accountType = "";
isFound = false;
//Because account_status_ind uses shorted site numbers, we have to format it for that table call
	siteNumberInt = 0;
	siteNumberShort = "0";
	if(isnumber(siteNumber_config)){
		siteNumberInt = atoi(siteNumber_config);
		siteNumberShort = string(siteNumberInt);
	}
	
accountStatusRecs = bmql("SELECT Acct_Type FROM Account_Status WHERE infopro_acct_nbr = $accountNumber AND Site_Nbr = $siteNumber_config");

accountTypeDict = dict("string");
put(accountTypeDict,"P", "Permanent");
put(accountTypeDict,"T", "Temporary");
put(accountTypeDict,"S", "Seasonal");
put(accountTypeDict,"C", "Permanent");
put(accountTypeDict,"A", "Permanent");
put(accountTypeDict,"B", "Permanent");
put(accountTypeDict,"E", "Permanent");
put(accountTypeDict,"I", "Permanent");
put(accountTypeDict,"L", "Permanent");
put(accountTypeDict,"X", "Temporary");

for rec in accountStatusRecs{
	if(containskey(accountTypeDict,get(rec, "Acct_Type"))){
		accountType = get(accountTypeDict,get(rec, "Acct_Type"));
	}
	isFound = true;
}
if(not(isFound)){
	accountStatusRecsLarge = bmql("SELECT acct_type FROM Account_Status_Ind WHERE container_grp_nbr = $containerGroup_config AND  acct_key = $accountNumber AND site_nbr = $siteNumberShort");

	for rec in accountStatusRecsLarge{
		if(containskey(accountTypeDict,get(rec, "acct_type"))){
		accountType = get(accountTypeDict,get(rec, "acct_type"));
	}
	}
}
return accountType;