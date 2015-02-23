/* 
================================================================================
Name: Select Site
Author: ???  
Create date: ??? 
Description:  Sets attributes based on querying Account_Status at the site level.
        
Input:      
                    
Output:     return "1~siteName_quote~" + siteName_quote + "X|";

Updates:    20150220 - Gaurav Dawar - To make sub contract "C" as an option similar to permanent for account type.
        
=====================================================================================================
*/
accountStatusRecordSet = bmql("SELECT Acct_Type, Sales_Rep_Id,Original_Open_Dt, Expiration_Dt, Serv_Contract_Status, is_national_account,  is_frf_charged, is_erf_charged, is_Admin_Charged, Next_Review_Dt FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote");

result = "";
nationalAccount = "No";
nextReviewDate = "";
eRFCharged = "No";
fRFCharged = "No";
adminCharged = "No";
accountType = "";
salesRepSite = "";
originalStartDateSite = "";
expirationDateSite = "";
contractStatusSite = "";
accountType = "";
accountTypeDict = dict("string");
put(accountTypeDict,"P", "Permanent");
put(accountTypeDict,"T", "Temporary");
put(accountTypeDict,"S", "Seasonal");
put(accountTypeDict,"C", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"A", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"B", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"E", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"I", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"L", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
put(accountTypeDict,"X", "Temporary");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.

for each in accountStatusRecordSet{
	//05/08/2014 - Decision has been made to query erf, frf, admin status only from Account object and not from AccountStatus table that considers sitenumber as well, so, these charges are mapped in Auto fill action and hence this logic is commented out. This applies to national account flag as well
	/*if(getint(each,"is_national_account") == 1){
		nationalAccount = "Yes";
	}
	if(getint(each,"is_frf_charged") == 1){
		fRFCharged = "Yes";
	}
	if(getint(each,"is_erf_charged") == 1){
		eRFCharged = "Yes";
	}
	if(getint(each,"is_Admin_Charged") == 1){
		adminCharged = "Yes";
	}*/
	if(get(each,"Next_Review_Dt") <> "99991231" AND get(each,"Next_Review_Dt") <> "") {
		nextReviewDateStr = get(each,"Next_Review_Dt");
		print "--nextReviewDateStr--"; print nextReviewDateStr;
		if(len(nextReviewDateStr) == 8){
			nextReviewDateYearStr = substring(nextReviewDateStr, 0, 4); print "--nextReviewDateYearStr--"; print nextReviewDateYearStr;
			nextReviewDateMonthStr = substring(nextReviewDateStr, 4, 6);print "--nextReviewDateMonthStr--"; print nextReviewDateMonthStr;
			nextReviewDateDayStr = substring(nextReviewDateStr, 6, 8);print "--nextReviewDateDayStr--"; print nextReviewDateDayStr;
			nextReviewDateTemp = nextReviewDateYearStr + "-" + nextReviewDateMonthStr + "-" + nextReviewDateDayStr;
			nextReviewDate = datetostr(strtojavadate(nextReviewDateTemp, "yyyy-MM-dd"));
		}	
		print "nextReviewDate"; print nextReviewDate;
	}
	
	salesRepSite = get(each,"Sales_Rep_Id") ;
	contractStatusSite = get(each,"Serv_Contract_Status") ;
	
	if(get(each,"Original_Open_Dt") <> "99991231" AND get(each,"Original_Open_Dt") <> ""){
		origOpenDateStr = get(each,"Original_Open_Dt");
		if(len(origOpenDateStr) == 8){
			origOpenDateYearStr = substring(origOpenDateStr, 0, 4);
			origOpenDateMonthStr = substring(origOpenDateStr, 4, 6);
			origOpenDateDayStr = substring(origOpenDateStr, 6, 8);
			origOpenDateTemp = origOpenDateYearStr + "-" + origOpenDateMonthStr + "-" + origOpenDateDayStr;
			originalStartDateSite = datetostr(strtojavadate(origOpenDateTemp, "yyyy-MM-dd"));
		}
		print originalStartDateSite;
	}
	if(get(each,"Expiration_Dt") <> "99991231" AND get(each,"Expiration_Dt") <> "" ){
		expirationDateStr = get(each,"Expiration_Dt");
		if(len(expirationDateStr) == 8){
			expirationDateYearStr = substring(expirationDateStr, 0, 4);
			expirationDateMonthStr = substring(expirationDateStr, 4, 6);
			expirationDateDayStr = substring(expirationDateStr, 6, 8);
			expirationDateTemp = expirationDateYearStr + "-" + expirationDateMonthStr + "-" + expirationDateDayStr;
			expirationDateSite = datetostr(strtojavadate(expirationDateTemp, "yyyy-MM-dd"));
		}
		print expirationDateSite;
	}
	
	if(containskey(accountTypeDict,get(each,"Acct_Type"))){
		accountType = get(accountTypeDict,get(each,"Acct_Type"));
	}
	
}

return "1~salesRepSite_quote~" + salesRepSite + "|" +
			"1~originalStartDateSite_quote~" + originalStartDateSite + "|" +
			"1~expirationDateSite_quote~" + expirationDateSite + "|" +
			"1~contractStatusSite_quote~" + contractStatusSite + "|" + 
			"1~existingCustomerWithNewSite_quote~false"+ "|" +
			//"1~nationalAccount_quote~" + nationalAccount + "|" +
			"1~nextReviewDate_quote~" + (nextReviewDate) + "|" +
			/*"1~eRFCharged_quote~" + eRFCharged + "|" +
			"1~fRFCharged_quote~" + fRFCharged + "|" +
			"1~adminFeeCharged_quote~" + adminCharged + "|" +*/
			"1~accountType_quote~" + accountType + "|";