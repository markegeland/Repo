if(lower(salesActivity_quote) == "existing customer" OR (lower(salesActivity_quote) == "change of owner" )){
	return true;
}
return false;