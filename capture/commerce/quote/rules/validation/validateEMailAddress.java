email = _quote_process_siteAddress_quote_email;

if (email <> "") {
	ampPos = find(email,"@");
	
	if (ampPos == -1) {
		return true;
	}
	else {
		periodPos = find(email,".",ampPos);
		if (periodPos == -1) {
			return true;
		}
	}
}

return false;