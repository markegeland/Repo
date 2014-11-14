if(coTermDate_quote <> "") {
	return coTermDate_quote;
}
if(contractStartDate_line == "") {
	return datetostr(adddays(strtojavadate(contractStartDate_line,"yyyy-MM-dd"),365));
}
return "";