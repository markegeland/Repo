/*added for Large Existing*/
salesActivityQuote = "";
salesActivityRecSet = bmql("SELECT salesActivity_quote FROM commerce.quote_process");

for each in salesActivityRecSet{
	salesActivityQuote = get(each, "salesActivity_quote");
}

if(salesActivityQuote == "Change of Owner"){
	return false;
}
return true;