/* 
================================================================================
Name:   setSiteCustomerReadOnlyAttributes
Author: ???  
Create date: ??? 
Description:  Sets the Customer _readOnly attributes based on _quote attributes.
        
Input:      30 quote level attributes, see debugger for list
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20141007 - JPalubinskas - Added contID_readOnly_quote for contID F/O indicator
        
=====================================================================================================
*/
retStr = "";
//Site related fields
finalSiteName = siteName_Cust_info_quote;
NRD = substring(nextReviewDate_quote, 5, 7) + "/" +
	  substring(nextReviewDate_quote, 8, 10) + "/" +
	  substring(nextReviewDate_quote, 0, 4);

if(finalSiteName == ""){
	finalSiteName = siteName_quote;
}
if(finalSiteName <> ""){
	retStr = retStr + "1~siteName_readOnly_quote~" + finalSiteName + "|";
	retStr = retStr + "1~siteName_Cust_info_quote~" + finalSiteName + "|";
}
if(_quote_process_siteAddress_quote_address <> ""){
	retStr = retStr + "1~siteAddress_readOnly_quote~" + _quote_process_siteAddress_quote_address + "|";
	retStr = retStr + "1~address1_cust_info_RO_quote~" + _quote_process_siteAddress_quote_address + "|";
}
if(_quote_process_siteAddress_quote_city <> ""){
	retStr = retStr + "1~siteCity_readOnly_quote~" + _quote_process_siteAddress_quote_city + "|";
	retStr = retStr + "1~city_cust_info_RO_quote~" + _quote_process_siteAddress_quote_city + "|";
}
if(_quote_process_siteAddress_quote_zip <> ""){
	retStr = retStr + "1~siteZip_readOnly_quote~" + _quote_process_siteAddress_quote_zip + "|";
	retStr = retStr + "1~zip_cust_info_RO_quote~" + _quote_process_siteAddress_quote_zip + "|";
}
if(_quote_process_siteAddress_quote_state <> ""){
	retStr = retStr + "1~siteState_readOnly_quote~" + _quote_process_siteAddress_quote_state + "|";
	retStr = retStr + "1~state_cust_info_RO_quote~" + _quote_process_siteAddress_quote_state + "|";
}
if(_quote_process_siteAddress_quote_phone <> ""){
	if(find(_quote_process_siteAddress_quote_phone, "-") == -1 AND find(_quote_process_siteAddress_quote_phone, "(") == -1){
		if(isnumber(_quote_process_siteAddress_quote_phone) AND len(_quote_process_siteAddress_quote_phone) == 10){
			tempPhoneArr = split(_quote_process_siteAddress_quote_phone, "");
			print "--tempPhoneArr--"; print tempPhoneArr;
			code = tempPhoneArr[1] + tempPhoneArr[2] + tempPhoneArr[3];
			formattedSiteAddrPhone = "(" + code + ") " + tempPhoneArr[4] + tempPhoneArr[5] + tempPhoneArr[6] + "-" + tempPhoneArr[7] + tempPhoneArr[8] + tempPhoneArr[9] + tempPhoneArr[10] ;
			retStr = retStr + "1~sitePhone_readOnly_quote~" + formattedSiteAddrPhone + "|";
		} 
	}else{
		retStr = retStr + "1~sitePhone_readOnly_quote~" + _quote_process_siteAddress_quote_phone + "|";
	}	
}

if(accountType_quote <> ""){
	retStr = retStr + "1~accountType_readOnly_quote~" + accountType_quote + "|";
}
if(industry_quote <> ""){
	retStr = retStr + "1~industry_readOnly_quote~" + industry_quote + "|";
}
if(segment_quote <> ""){
	retStr = retStr + "1~segment_readOnly_quote~" + segment_quote + "|";
}
if(lower(salesActivity_quote) <> "existing customer"){
	if( NOT isnull(feesToCharge_quote)){
		if(find(feesToCharge_quote, "FRF") > -1){
			retStr = retStr + "1~fRF_readOnly_quote~" + "Yes" + "|";	
		} 
		if(find(feesToCharge_quote, "ERF") > -1){
			retStr = retStr + "1~eRFreadOnly_quote~" + "Yes" + "|";
		} 
		if(find(feesToCharge_quote, "Admin") > -1){
			retStr = retStr + "1~admin_readOnly_quote~" + "Yes" + "|";	
		} 
	}
}

//contID (franchise/open market indicator)
if(contID_quote <> ""){
	retStr = retStr + "1~contID_readOnly_quote~" + contID_quote + "|";
}

//customer/invoiceTo info fields
if(lower(salesActivity_quote) == "existing customer"){
	//customer_name
	if(customer_name <> ""){
		retStr = retStr + "1~account_readOnly_quote~" + _quote_process_billTo_company_name + "|";
	}
	//_customer_id
	if(_quote_process_customer_id <> ""){
		retStr = retStr + "1~accountNumber_readOnly_quote~" + _quote_process_customer_id + "|";
	}
	//National Account
	if(nationalAccount_quote <> ""){
		retStr = retStr + "1~nationalAccount_readOnly_quote~" + nationalAccount_quote + "|";
	}
	//Next Review date
	if(nextReviewDate_quote <> ""){
		retStr = retStr + "1~nRD_readOnly_quote~" + NRD + "|";	
	}
	//ERF
	if(NOT isnull(feesToCharge_quote)){
		if(find(feesToCharge_quote, "Fixed Environment Recovery Fee (ERF)") > -1){
			erfPct = string(erfRate_quote * 100) + "%";
			retStr = retStr + "1~eRFreadOnly_quote~" + "Yes - Fixed " + erfPct + "|";	
		}elif(find(feesToCharge_quote, "ERF") > -1){
			retStr = retStr + "1~eRFreadOnly_quote~" + "Yes" + "|";
		}
		else{
			retStr = retStr + "1~eRFreadOnly_quote~" + "No" + "|";	
		}
	} 
	//FRF
	if(NOT isnull(feesToCharge_quote)){
		if(find(feesToCharge_quote, "Fixed Fuel Recovery Fee (FRF)") > -1){
			frfPct = string(frfRate_quote * 100) + "%";
			retStr = retStr + "1~fRF_readOnly_quote~" + "Yes - Fixed " + frfPct + "|";	
		}elif(find(feesToCharge_quote, "FRF") > -1){
			retStr = retStr + "1~fRF_readOnly_quote~" + "Yes" + "|";	
		}
		else{
			retStr = retStr + "1~fRF_readOnly_quote~" + "No" + "|";
		}
	}
	//Admin
	if(adminFeeCharged_quote <> ""){
		retStr = retStr + "1~admin_readOnly_quote~" + adminFeeCharged_quote + "|";	
	}
	//invoice address
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
//Quote information
if(quoteNumber_quote <> ""){
	retStr = retStr + "1~quoteNumber_RO_quote~" + quoteNumber_quote + "|";
}
if(quoteDescription_quote <> ""){
	retStr = retStr + "1~quoteDescription_RO_quote~" + quoteDescription_quote + "|";
}
if(salesActivity_quote <> ""){
	retStr = retStr + "1~salesActivity_RO_quote~" +  salesActivity_quote + "|";
}
if(division_quote <> ""){
	retStr = retStr + "1~division_RO_quote~" + division_quote + "|";
}
return retStr;