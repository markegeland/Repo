/* Updates by Gaurav Dawar - 20150226 - #431 - added Existing Terms being treated as MTM functionality
			  Gaurav Dawar - 20150429 - #458 - add the commentsForDeliveryAndExchangeCreditOnOutput function to generate the string for VCD
*/
result = "";

initialTermForDocOutput = "";
renewalTermForDocOutput =  "";

if(salesActivity_quote == "Existing Customer" AND initialTerm_quote == "Existing Terms" AND contractLength_quote<> 1){
	initialTermForDocOutput = string(hiddenExisitingTerm) + " MONTHS";
}elif(salesActivity_quote == "Existing Customer" AND initialTerm_quote == "Existing Terms" AND contractLength_quote == 1){
	initialTermForDocOutput = "MONTH TO MONTH";
}elif(initialTerm_quote == "1"){
	initialTermForDocOutput = "MONTH TO MONTH";
}else{
	initialTermForDocOutput = initialTerm_quote + " MONTHS";
}

if(salesActivity_quote == "Existing Customer" AND renewalTerm_quote == "Existing Terms" AND contractLength_quote<> 1){
	renewalTermForDocOutput = string(hiddenExisitingTerm)  + " MONTHS";
}elif(salesActivity_quote == "Existing Customer" AND renewalTerm_quote == "Existing Terms" AND contractLength_quote == 1){
	initialTermForDocOutput = "MONTH TO MONTH";
}elif(renewalTerm_quote == "1"){
	renewalTermForDocOutput = "MONTH TO MONTH";
}else{
	renewalTermForDocOutput = renewalTerm_quote + " MONTH";
}

result = result   + "1~initialTermForDocument_quote~"+initialTermForDocOutput+"|"
		  + "1~renewalTermForDocument_quote~"+renewalTermForDocOutput+"|";
		  
result = result + commerce.commentsForDeliveryAndExchangeCreditOnOutput();

return result;