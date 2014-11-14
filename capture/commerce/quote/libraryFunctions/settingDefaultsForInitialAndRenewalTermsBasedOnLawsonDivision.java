initialDef = 0;
renDef = 0;
retStr = "";
//If division entry exists in datatble, get defaults from there
if(division_quote <> ""){
	termRecSet = bmql("SELECT initialDefault, renewalDefault FROM Div_Term_Exceptions WHERE division = $division_quote");
	for eachRec in termRecSet{
		initialDef = getint(eachRec, "initialDefault");
		renDef = getint(eachRec, "renewalDefault");
		break;
	}
}
//If division entry does not exist, apply the defaults at corporate level ie 0-division level
if(initialDef == 0 OR renDef == 0){
	termRecSet = bmql("SELECT initialDefault, renewalDefault FROM Div_Term_Exceptions WHERE division = '0'");
	for eachRec in termRecSet{
		initialDef = getint(eachRec, "initialDefault");
		renDef = getint(eachRec, "renewalDefault");
		break;
	}
}
if(accountType_quote == "Temporary"){ //For Temp Accounts, division defaults don't matter
	initialDef = 1;
	renDef = 1;
}

retStr = retStr + "1" + "~initialTerm_quote~" + string(initialDef) + "|";
retStr = retStr + "1" + "~renewalTerm_quote~" + string(renDef) + "|";
return retStr;