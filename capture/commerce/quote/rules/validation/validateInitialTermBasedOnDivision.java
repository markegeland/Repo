termOnQuote = 0;
minTerm = 0;
maxTerm = 0;
if(_system_current_step_var == "adjustPricing"){
	initialTermSet = bmql("SELECT initialMin, initialMax FROM Div_Term_Exceptions WHERE division = $division_quote");
	for each in initialTermSet{
		minTerm = getint(each, "initialMin");
		maxTerm = getint(each, "initialMax");
		break;
	}
	if(isnumber(initialTerm_quote)){
		termOnQuote = atoi(initialTerm_quote);
		if(termOnQuote < minTerm OR termOnQuote > maxTerm){
			return true;
		}
	}
}
return false;