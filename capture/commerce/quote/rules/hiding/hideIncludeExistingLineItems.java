if(salesActivity_quote <> "Existing Customer"){ //if salesActivity is not existing customer, always hide
	return true;
}else{
	if(NOT(chooseProposal_quote)){ //if salesActivity is existing customer, but proposal checkbox is unchecked, then hide it
		return true;
	}
} 
return false;