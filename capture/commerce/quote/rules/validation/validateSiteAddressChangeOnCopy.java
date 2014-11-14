newAddress = _quote_process_siteAddress_quote_address + "," + _quote_process_siteAddress_quote_city + "," + _quote_process_siteAddress_quote_state;
if(newAddress <> backupCustomerSiteAddress_quote AND backupCustomerSiteAddress_quote <> ""){
	return true;
}
return false;