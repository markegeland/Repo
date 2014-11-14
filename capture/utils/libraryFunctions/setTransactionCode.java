/*
================================================================================
Name:   setTransactionCode
Author:   Zach Schlieder
Create date:  3/6/14
Description:  Generates the transaction code for output on the CSA by checking the values of new/current sell price and yards per month
        
Input:   	currentYardsPerMonth: 	Float - The value of Yards Per Month for the existing configuration
			newYardsPerMonth: 		Float - The value of Yards Per Month entered by the user (if different from the existing value)
			currentSellPrice:		Float - The value of Sell Price for the existing configuration
			newSellPrice: 			Float - The sell price for the current line item, entered by the user
			currentTransactionCode: Float - The transaction code for any previous line items on this quote (if applicable)
                    
Output:  	String - Contains the new or updated transaction code

Updates:	
    
=====================================================================================================
*/
scenario = "";
transactionCode = currentTransactionCode;

//Identify if a transaction code already exists. If so, determine which situation created the code. This affects which codes can be set.

if(transactionCode <> ""){
	transactionCodeRecordSet = bmql("SELECT scenario FROM transactionCode WHERE code = $transactionCode");
	for record in transactionCodeRecordSet{
		scenario = get(record, "scenario");
	}
	//The first service increase or decrease on the quote takes precedence. If this code already exists, take no further action.
	if(scenario == "Service Increase" OR scenario == "Service Decrease"){
		return transactionCode;
	}
}

//If the transaction code does not already exist or is not a service change, check to find the appropriate code.
if(newYardsPerMonth > currentYardsPerMonth){
	scenario = "Service Increase";
}
elif(currentYardsPerMonth > newYardsPerMonth){
	scenario = "Service Decrease";
}
elif(newSellPrice > currentSellPrice){
	scenario = "Rate Increase";
}
elif(currentSellPrice > newSellPrice){
	scenario = "Rate Decrease";
}

print scenario;

//Find the appropriate transaction code from the scenario
if(scenario <> ""){
	transactionCodeRecordSet = bmql("SELECT code FROM transactionCode WHERE scenario = $scenario");
	for record in transactionCodeRecordSet{
		transactionCode = get(record, "code");
	}
}

return transactionCode;