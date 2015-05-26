/* 
=====================================================================================================
Name:        setSiteCustomerReadOnlyAttributes

Author:      ???  

Create date: ??? 

Description: Sets _readOnly attributes based on _quote attributes.
             We only want to call this function on the Next or Assign Quote actions from the Enter
             Customer Site Information screen.
        
Input:      30 quote level attributes, see debugger for list
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20141007 - JPalubinskas - Added contID_readOnly_quote for contID F/O indicator
            20141218 - JPalubinskas - Upgrade fix for split function
            20150123 - JPalubinskas - #233 set the fees from the data tables rather than from the
                                      checkboxes since this is for the read only attributes.
                                      Refactored for clarity.
			20150515 - RN - #291 query divisionFeeRate to determine if admin fee is charged for new customer
			20150526 - Gaurav Dawar	- #573 - Fixed Fee percentage in the header and CSA.
        
=====================================================================================================
*/
retStr = "";

//================================ Quote Information

// Quote Number
if(quoteNumber_quote <> ""){
    retStr = retStr + "1~quoteNumber_RO_quote~" + quoteNumber_quote + "|";
}

// Quote Description
if(quoteDescription_quote <> ""){
    retStr = retStr + "1~quoteDescription_RO_quote~" + quoteDescription_quote + "|";
}

// Sales Activity*
if(salesActivity_quote <> ""){
    retStr = retStr + "1~salesActivity_RO_quote~" +  salesActivity_quote + "|";
}

// Lawson Division*
if(division_quote <> ""){
    retStr = retStr + "1~division_RO_quote~" + division_quote + "|";
}


//================================ Site Information

// Industry*
if(industry_quote <> ""){
    retStr = retStr + "1~industry_readOnly_quote~" + industry_quote + "|";
}

// Segment*
if(segment_quote <> ""){
    retStr = retStr + "1~segment_readOnly_quote~" + segment_quote + "|";
}

// Account Type*
if(accountType_quote <> ""){
    retStr = retStr + "1~accountType_readOnly_quote~" + accountType_quote + "|";
}

// Site Name*
finalSiteName = siteName_Cust_info_quote; 
if(finalSiteName == ""){
    finalSiteName = siteName_quote;
}
if(finalSiteName <> ""){
	retStr = retStr + "1~siteName_readOnly_quote~" + finalSiteName + "|";
	retStr = retStr + "1~siteName_Cust_info_quote~" + finalSiteName + "|";
}

// Address 1*
if(_quote_process_siteAddress_quote_address <> ""){
	retStr = retStr + "1~siteAddress_readOnly_quote~" + _quote_process_siteAddress_quote_address + "|";
	retStr = retStr + "1~address1_cust_info_RO_quote~" + _quote_process_siteAddress_quote_address + "|";
}

// City*
if(_quote_process_siteAddress_quote_city <> ""){
	retStr = retStr + "1~siteCity_readOnly_quote~" + _quote_process_siteAddress_quote_city + "|";
	retStr = retStr + "1~city_cust_info_RO_quote~" + _quote_process_siteAddress_quote_city + "|";
}

// State*
if(_quote_process_siteAddress_quote_state <> ""){
    retStr = retStr + "1~siteState_readOnly_quote~" + _quote_process_siteAddress_quote_state + "|";
    retStr = retStr + "1~state_cust_info_RO_quote~" + _quote_process_siteAddress_quote_state + "|";
}

// ZIP Code*
if(_quote_process_siteAddress_quote_zip <> ""){
	retStr = retStr + "1~siteZip_readOnly_quote~" + _quote_process_siteAddress_quote_zip + "|";
	retStr = retStr + "1~zip_cust_info_RO_quote~" + _quote_process_siteAddress_quote_zip + "|";
}

// Tel No*
if(_quote_process_siteAddress_quote_phone <> ""){
	if(find(_quote_process_siteAddress_quote_phone, "-") == -1 AND find(_quote_process_siteAddress_quote_phone, "(") == -1){
		if(isnumber(_quote_process_siteAddress_quote_phone) AND len(_quote_process_siteAddress_quote_phone) == 10){
			tempPhoneArr = split(_quote_process_siteAddress_quote_phone, "");
			code = tempPhoneArr[0] + tempPhoneArr[1] + tempPhoneArr[2];
			formattedSiteAddrPhone = "(" + code + ") " + tempPhoneArr[3] + tempPhoneArr[4] + tempPhoneArr[5] + "-" + tempPhoneArr[6] + tempPhoneArr[7] + tempPhoneArr[8] + tempPhoneArr[9] ;
			retStr = retStr + "1~sitePhone_readOnly_quote~" + formattedSiteAddrPhone + "|";
		} 
	}else{
		retStr = retStr + "1~sitePhone_readOnly_quote~" + _quote_process_siteAddress_quote_phone + "|";
	}	
}

//================================ Division Information

// The read only values are populated with the division default fee rates for new
// and overwritten with account level data for existing.
divisionFeeRateRecordSet = bmql("SELECT fRFRate, eRFRate, adminAmount, erf_on_frf, infopro_div_nbr FROM divisionFeeRate WHERE divisionNumber = $division_quote AND infopro_div_nbr = $infoProNumberDisplayOnly_quote");

print infoproDivision_quote;
frfOn = "No";
erfOn = "No";
adminOn = "No";
frfRateStr = "0.0";
erfRateStr = "0.0";
adminRateStr = "0.0";

for eachRecord in divisionFeeRateRecordSet{
    frfRateStr = get(eachRecord, "fRFRate");
    erfRateStr = get(eachRecord, "eRFRate");
    adminRateStr = get(eachRecord, "adminAmount");
    //erfOnFrfDivision = getint(eachRecord, "erf_on_frf");
    break;
}

// If the division amount is set to 0, consider that fee turned off for that division
if(frfRateStr <> "0.0"){ frfOn = "Yes"; }
if(erfRateStr <> "0.0"){ erfOn = "Yes"; }
if(adminRateStr <> "0.0"){ adminOn = "Yes"; }
print "frfRateStr: " + frfRateStr;
print "erfRateStr: " + erfRateStr;
print "adminRateStr: " + adminRateStr;
print "frfOn: " + frfOn;
print "erfOn: " + erfOn;
print "adminOn: " + adminOn;


//================================ Existing Customer Invoice Information

contID = "O";
if(lower(salesActivity_quote) == "existing customer"){

contID = "O";
frfOn = "No";
erfOn = "No";
adminOn = "No";
frfRateStr = "0.0";
erfRateStr = "0.0";
adminRateStr = "0.0";

    // Fees
    accountStatusRS = bmql("SELECT infopro_div_nbr, is_frf_locked, is_erf_locked, is_Admin_Charged, is_frf_charged, is_erf_charged, is_franchise, frf_rate_pct, erf_rate_pct FROM Account_Status WHERE infopro_acct_nbr = $_quote_process_customer_id AND Site_Nbr = $siteNumber_quote");
    print accountStatusRS;

    // handle when no results are returned
    // handle when multiple sites are returned
    adminCharged = 0;
    is_erf_charged = 0;
    is_frf_charged = 0;
    is_franchise = 0;
	is_frf_locked = 0;
	is_erf_locked = 0;
	frf_rate_pct = 0.0;
	erf_rate_pct = 0.0;

    for record in accountStatusRS{
        adminCharged = getint(record, "is_Admin_Charged");
        is_erf_charged = getint(record, "is_erf_charged");
        is_frf_charged = getint(record, "is_frf_charged");
        is_franchise = getint(record, "is_franchise");
		is_frf_locked = getint(record, "is_frf_locked");
		is_erf_locked = getint(record, "is_erf_locked");
		frf_rate_pct = getfloat(record, "frf_rate_pct");
		erf_rate_pct = getfloat(record, "erf_rate_pct");
        
        if(adminCharged == 1){
            adminOn = "Yes";
        }   
        if(is_erf_charged == 1){
            erfOn = "Yes";
        }
        if(is_frf_charged == 1){
            frfOn = "Yes";
        }
        if(is_franchise == 1){
            contID = "F";
        }
        break;
    }

	// customer_name
	if(customer_name <> ""){
		retStr = retStr + "1~account_readOnly_quote~" + _quote_process_billTo_company_name + "|";
	}

	// _customer_id
	if(_quote_process_customer_id <> ""){
		retStr = retStr + "1~accountNumber_readOnly_quote~" + _quote_process_customer_id + "|";
	}

	// National Account
	if(nationalAccount_quote <> ""){
		retStr = retStr + "1~nationalAccount_readOnly_quote~" + nationalAccount_quote + "|";
	}

	// Next Review Date
	if(nextReviewDate_quote <> ""){
        NRD = substring(nextReviewDate_quote, 5, 7) + "/" +
              substring(nextReviewDate_quote, 8, 10) + "/" +
              substring(nextReviewDate_quote, 0, 4);
		retStr = retStr + "1~nRD_readOnly_quote~" + NRD + "|";	
	}

	// Invoice Address   
	invoiceAddressStr = "";
	invoiceAddressArr = string[];
	if(_quote_process_billTo_address <> ""){
		append(invoiceAddressArr, _quote_process_billTo_address);
	}
	if(_quote_process_billTo_city <> ""){
		append(invoiceAddressArr, _quote_process_billTo_city);
	}
	if(_quote_process_billTo_state <> ""){
		append(invoiceAddressArr, _quote_process_billTo_state);
	}
	if(_quote_process_billTo_zip <> ""){
		append(invoiceAddressArr, _quote_process_billTo_zip);
	}
	if(NOT(isempty(invoiceAddressArr))){
		invoiceAddressStr = join(invoiceAddressArr, ", ");
	}
	retStr = retStr + "1~invoiceToAddress_readOnly_quote~" + invoiceAddressStr + "|";

}

retStr = retStr + "1~fRF_readOnly_quote~" + frfOn + "|"
                + "1~eRFreadOnly_quote~" + erfOn + "|"
                + "1~admin_readOnly_quote~" + adminOn + "|"
                + "1~contID_readOnly_quote~" + contID + "|";
				
if(lower(salesActivity_quote) == "existing customer"){
//     ERF
     
    if(is_erf_locked == 1){
        erfPct = string(erf_rate_pct) + "%";
        retStr = retStr + "1~eRFreadOnly_quote~" + "Yes - Fixed " + erfPct + "|";
    }
     //FRF
    if(is_frf_locked == 1){
        frfPct = string(frf_rate_pct) + "%";
        retStr = retStr + "1~fRF_readOnly_quote~" + "Yes - Fixed " + frfPct + "|";    
    }
}

return retStr;


// if(lower(salesActivity_quote) <> "existing customer"){
//     if( NOT isnull(feesToCharge_quote)){
//         if(find(feesToCharge_quote, "FRF") > -1){
//             retStr = retStr + "1~fRF_readOnly_quote~" + "Yes" + "|";    
//         } 
//         if(find(feesToCharge_quote, "ERF") > -1){
//             retStr = retStr + "1~eRFreadOnly_quote~" + "Yes" + "|";
//         } 
//         if(find(feesToCharge_quote, "Admin") > -1){
//             retStr = retStr + "1~admin_readOnly_quote~" + "Yes" + "|";  
//         } 
//     }
// }

// if(lower(salesActivity_quote) == "existing customer"){
//     //ERF
//     if(NOT isnull(feesToCharge_quote)){
//         if(find(feesToCharge_quote, "Fixed Environment Recovery Fee (ERF)") > -1){
//             erfPct = string(erfRate_quote * 100) + "%";
//             retStr = retStr + "1~eRFreadOnly_quote~" + "Yes - Fixed " + erfPct + "|";   
//         }elif(find(feesToCharge_quote, "ERF") > -1){
//             retStr = retStr + "1~eRFreadOnly_quote~" + "Yes" + "|";
//         }
//         else{
//             retStr = retStr + "1~eRFreadOnly_quote~" + "No" + "|";  
//         }
//     } 
//     //FRF
//     if(NOT isnull(feesToCharge_quote)){
//         if(find(feesToCharge_quote, "Fixed Fuel Recovery Fee (FRF)") > -1){
//             frfPct = string(frfRate_quote * 100) + "%";
//             retStr = retStr + "1~fRF_readOnly_quote~" + "Yes - Fixed " + frfPct + "|";  
//         }elif(find(feesToCharge_quote, "FRF") > -1){
//             retStr = retStr + "1~fRF_readOnly_quote~" + "Yes" + "|";    
//         }
//         else{
//             retStr = retStr + "1~fRF_readOnly_quote~" + "No" + "|";
//         }
//     }
//     //Admin
//     if(adminFeeCharged_quote <> ""){
//         retStr = retStr + "1~admin_readOnly_quote~" + adminFeeCharged_quote + "|";  
//     }