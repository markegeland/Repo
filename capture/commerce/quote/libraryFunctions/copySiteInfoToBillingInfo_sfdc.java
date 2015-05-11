returnStr = "";
custInfoCompany = siteName_quote;
custInfoAttn = authorizedBy_quote;
custInfoAddr1 = _quote_process_siteAddress_quote_address;
custInfoAddr2 = _quote_process_siteAddress_quote_address_2;
custInfoCity = _quote_process_siteAddress_quote_city;
custInfoState = _quote_process_siteAddress_quote_state;
custInfoZip = _quote_process_siteAddress_quote_zip;
custInfoCounty = _quote_process_siteAddress_quote_fax;
custInfoPhone = _quote_process_siteAddress_quote_phone;
siteContact = _quote_process_siteAddress_quote_first_name;

print _quote_process_billTo_state;
print _quote_process_billTo_city;
if(_quote_process_billTo_company_name == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_company_name" + "~" + custInfoCompany + "|";
}
if(_quote_process_billTo_company_name_2 == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_company_name_2" + "~" + custInfoAttn + "|";
}
if(_quote_process_billTo_address == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_address" + "~" + custInfoAddr1 + "|";
}
if(_quote_process_billTo_address_2 == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_address_2" + "~" + custInfoAddr2 + "|";
}
if(_quote_process_billTo_city == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_city" + "~" + custInfoCity + "|";
}
if(_quote_process_billTo_state == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_state" + "~" + custInfoState + "|";
}
if(_quote_process_billTo_zip == ""){
	returnStr = returnStr + "1~" + "_quote_process_billTo_zip" + "~" + custInfoZip + "|";
}
if(county_bill_To_quote == ""){
	returnStr = returnStr + "1~" + "county_bill_To_quote" + "~" + custInfoCounty + "|";
}



if(len(_quote_process_siteAddress_quote_phone) == 10) {
	areaCode = substring(_quote_process_siteAddress_quote_phone, 0, 3);
	exchange = substring(_quote_process_siteAddress_quote_phone, 3, 6);
	lineNumber = substring(_quote_process_siteAddress_quote_phone, 6, 10);
	thisPhone = "(" + areaCode + ") " + exchange + "-" + lineNumber;
	returnStr = returnStr + "1~" + "_quote_process_siteAddress_quote_phone" + "~" + thisPhone + "|";
}
 
if(len(_quote_process_billTo_phone) == 10) {
	areaCode = substring(_quote_process_billTo_phone, 0, 3);
	exchange = substring(_quote_process_billTo_phone, 3, 6);
	lineNumber = substring(_quote_process_billTo_phone, 6, 10);
	thisPhone = "(" + areaCode + ") " + exchange + "-" + lineNumber;
	returnStr = returnStr + "1~" + "_quote_process_billTo_phone" + "~" + thisPhone + "|";
}

if(_quote_process_billTo_phone == ""){
	if(len(custInfoPhone) == 10) {
		areaCode = substring(custInfoPhone, 0, 3);
		exchange = substring(custInfoPhone, 3, 6);
		lineNumber = substring(custInfoPhone, 6, 10);
		custInfoPhone = "(" + areaCode + ") " + exchange + "-" + lineNumber;
	}
	returnStr = returnStr + "1~" + "_quote_process_billTo_phone" + "~" + custInfoPhone + "|";
}

if(sourceSystem_quote == "CAPTURE"){
	if(_quote_process_billTo_first_name == ""){
		returnStr = returnStr + "1~" + "_quote_process_billTo_first_name" + "~" + siteContact + "|";
	}
}

if(contactNameGenerateDocs_quote  == "") {
	returnStr = returnStr + "1~" + "contactNameGenerateDocs_quote" + "~" + _quote_process_siteAddress_quote_first_name + "|";
}
if(contactEmailGenerateDocs_quote  == "") {
	returnStr = returnStr + "1~" + "contactEmailGenerateDocs_quote" + "~" + _quote_process_siteAddress_quote_email + "|";
	returnStr = returnStr + "1~" + "contactConfirmEmailGenerateDocs_quote" + "~" + _quote_process_siteAddress_quote_email + "|";
}
if(contactTitleGenerateDocs_quote  == "") {
	returnStr = returnStr + "1~" + "contactTitleGenerateDocs_quote" + "~" + _quote_process_siteAddress_quote_last_name + "|";
}
if(contactTelNoGenerateDocs_quote  == "") {
	returnStr = returnStr + "1~" + "contactTelNoGenerateDocs_quote" + "~" + custInfoPhone + "|";
}


return returnStr;