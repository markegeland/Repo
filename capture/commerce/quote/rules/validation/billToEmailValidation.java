// Bill to email validation-- email should always contain "@' after the it should have one dot(.)
email = _quote_process_billTo_email;
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