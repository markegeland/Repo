/* 
================================================================================
Name:   _auto_fill_action - Refresh Address
Author: ???  
Create date: ??? 
Description:  Sets attributes based on querying Account_Status at the site level.
        
Input:      
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20141007 - JPalubinskas - Added contID functionality for F/O indicator
            20141218 - JPalubinskas - Upgrade fix for split function
			20150220 - Gaurav Dawar - To make sub contract "C" as an option similar to permanent for account type.
        
=====================================================================================================
*/

// Query Account Status table based on the Customer Id and site number
formattedSiteAddrPhone = _quote_process_siteAddress_quote_phone; //(713) 275-7227
if(isnumber(_quote_process_siteAddress_quote_phone) AND len(_quote_process_siteAddress_quote_phone) == 10){
	tempPhoneArr = split(_quote_process_siteAddress_quote_phone, "");
	//print "--tempPhoneArr--"; print tempPhoneArr;
	code = tempPhoneArr[0] + tempPhoneArr[1] + tempPhoneArr[2];
	formattedSiteAddrPhone = "(" + code + ") " + tempPhoneArr[3] + tempPhoneArr[4] + tempPhoneArr[5] + "-" + tempPhoneArr[6] + tempPhoneArr[7] + tempPhoneArr[8] + tempPhoneArr[9] ;
}
accountStatusRecordSet = bmql("SELECT Container_Grp_Nbr,Sales_Rep_Id,Original_Open_Dt,Expiration_Dt,Serv_Contract_Status,is_national_account,is_frf_charged,is_erf_charged,is_Admin_Charged,is_franchise,Next_Review_Dt,Acct_Type,is_frf_locked,is_erf_locked,latitude,longitude FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id  AND Site_Nbr = $siteNumber_quote");

//print "--accountStatusRecordSet--"; print accountStatusRecordSet;

result = "";
nationalAccount = nationalAccount_quote;
contID = "F";
isFranchise = "";
nextReviewDate = "";
eRFCharged = eRFCharged_quote;
fRFCharged = fRFCharged_quote;
adminCharged = adminFeeCharged_quote;
accountType = "";
salesRepSite = "";
originalStartDateSite = "";
expirationDateSite = "";
contractStatusSite = "";
isFrfLocked = false;
isErfLocked = false;
latitude = "";
longitude = "";
accountTypeDict = dict("string");
put(accountTypeDict,"P", "Permanent");
put(accountTypeDict,"T", "Temporary");
put(accountTypeDict,"S", "Seasonal");
put(accountTypeDict,"C", "Permanent");//Added (20150220) - GD - #404 - To make sub contract "C" as an option similar to permanent for account type.
//containerGroupToDisplayTextArr = string[];

if(eRFCharged_quote == "1" ) {
	eRFCharged = "Yes";
}
elif(eRFCharged_quote == "0") {
	eRFCharged = "No";
}
if(fRFCharged_quote == "1" ) {
	fRFCharged = "Yes";
}
elif(fRFCharged_quote == "0") {
	fRFCharged = "No";
}
if(adminFeeCharged_quote == "1" ) {
	adminCharged = "Yes";
}
elif(adminFeeCharged_quote == "0") {
	adminCharged = "No";
}
if(nationalAccount_quote == "1") {
	nationalAccount = "Yes";
}
elif(nationalAccount_quote == "0") {
	nationalAccount = "No";
}

for each in accountStatusRecordSet{
	//append(containerGroupToDisplayTextArr,get(each,"Container_Grp_Nbr"));
	
	//Latitude, Longitude
	latitude = get(each, "latitude");
	longitude = get(each, "longitude");
	if(get(each,"is_frf_locked") == "1"){
		isFrfLocked = true;
	}
	if(get(each,"is_erf_locked") == "1"){
		isErfLocked = true;
	}

	if(get(each,"Next_Review_Dt") <> "99991231" AND get(each,"Next_Review_Dt") <> "") {
		nextReviewDateStr = get(each,"Next_Review_Dt");
		if(len(nextReviewDateStr) == 8){
			nextReviewDateYearStr = substring(nextReviewDateStr, 0, 4);
			nextReviewDateMonthStr = substring(nextReviewDateStr, 4, 6);
			nextReviewDateDayStr = substring(nextReviewDateStr, 6, 8);
			nextReviewDateTemp = nextReviewDateYearStr + "-" + nextReviewDateMonthStr + "-" + nextReviewDateDayStr;
			nextReviewDate = datetostr(strtojavadate(nextReviewDateTemp, "yyyy-MM-dd"));
		}	
		//print "nextReviewDate"; print nextReviewDate;
	}
	
	if(containskey(accountTypeDict,get(each,"Acct_Type"))){
		accountType = get(accountTypeDict,get(each,"Acct_Type"));
	}

	isFranchise = get(each,"is_franchise");
	if (isFranchise == "1"){
		contID = "F";
	}
	else {
		contID = "O";
	}
	//print "isFranchise: " + isFranchise;
	//print "contID: " + contID;
	
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
		//print originalStartDateSite;
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
		//print expirationDateSite;
	}
	
}

if(lower(salesActivity_quote) == "change of owner"){
// When "Change of Owner" make current Contact Name, Title, Email, Phone, Site Name, Authorized By empty 
// When "Change of Owner" persist old values of Site Name,Address 1,Address 2,City,State,Zipcode,County, Tel No
 
	result = 
			"1~_quote_process_siteAddress_quote_first_name~|" + //Contact name
			"1~_quote_process_siteAddress_quote_last_name~|" + 	// Title
			"1~_quote_process_siteAddress_quote_phone~|" + 		//Phone
			"1~authorizedBy_quote~|" +							//Authorized By
			"1~siteName_quote~|" +			
			"1~_quote_process_siteAddress_quote_email~|" +	 //Email
			"1~confirmSiteAddress_quote~|" +				//Confirm Email
			"1~contactNameoldSite_quote~" + _quote_process_siteAddress_quote_first_name + "|" +
			"1~titleOldSite_quote~" + _quote_process_siteAddress_quote_last_name + "|" +
			"1~emailoldSite_quote~" + _quote_process_siteAddress_quote_email + "|" +
			"1~phoneoldSite_quote~" + formattedSiteAddrPhone + "|" +
			"1~siteNameoldSite_quote~" + siteName_quote + "|" +
			"1~authorizedByoldSite_quote~" + authorizedBy_quote + "|" +
			"1~address1OldSite_quote~" + _quote_process_siteAddress_quote_address + "|" +
			"1~address2OldSite_quote~" + _quote_process_siteAddress_quote_address_2 + "|" +
			"1~cityOldSite_quote~" + _quote_process_siteAddress_quote_city + "|" +
			"1~stateOldSite_quote~" + _quote_process_siteAddress_quote_state + "|" +
			"1~zipcodeoldSite_quote~" + _quote_process_siteAddress_quote_zip + "|" +
			"1~countryOldSite_quote~" + _quote_process_siteAddress_quote_fax + "|" +
			"1~telNoOldSite_quote~" + formattedSiteAddrPhone + "|" +
			"1~customerNameoldSite_quote~" + _quote_process_billTo_company_name + "|" +
			"1~attnoldSite_quote~" + _quote_process_billTo_company_name_2 + "|" +
			"1~billToAddress1OldSite_quote~" + _quote_process_billTo_address + "|" +
			"1~billToAddress2OldSite_quote~" + _quote_process_billTo_address_2 + "|" +
			"1~billToCityoldSite_quote~" + _quote_process_billTo_city + "|" +
			"1~billToStateoldSite_quote~" + _quote_process_billTo_state + "|" +
			"1~billToZipoldSite_quote~" + _quote_process_billTo_zip + "|" +
			"1~billToPhoneOldSite_quote~" + _quote_process_billTo_phone + "|" +
			"1~billToFaxoldSite_quote~" + _quote_process_billTo_fax + "|" ;
}
 

return result + 
			"1~nextReviewDate_quote~" + nextReviewDate + "|" +
			"1~nationalAccount_quote~" + nationalAccount + "|" +
			"1~contID_quote~" + contID + "|" +
			"1~eRFCharged_quote~" + eRFCharged + "|" +
			"1~fRFCharged_quote~" + fRFCharged + "|" +
			"1~adminFeeCharged_quote~" + adminCharged + "|" +
			"1~accountType_quote~" + accountType + "|" +
			"1~salesRepSite_quote~" + salesRepSite + "|" +
			"1~originalStartDateSite_quote~" + originalStartDateSite + "|" +
			"1~expirationDateSite_quote~" + expirationDateSite + "|" +
			"1~_siteAddress_quote_company_name~" + latitude + "|" +
			"1~_siteAddress_quote_company_name_2~" + longitude + "|" +
			"1~contractStatusSite_quote~" + contractStatusSite + "|"+ 
			"1~existingCustomerWithNewSite_quote~false"+ "|" +
			"1~_quote_process_siteAddress_quote_phone~" + formattedSiteAddrPhone + "|";