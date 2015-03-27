/*
================================================================================
       Name:  setTransactionCode
     Author:  Unknown
Create date:  Unknown
Description:  Sets the Transaction Code and Reason Code based on multiple conditions.
        
Input:   	contractStatus_quote: String - 
			salesActivity_quote: String - 
			reasonCode_quote: String - 
			division_quote: String - 
			competitorCode_quote: String - 
			existingCustomerWithNewSite_quote: String - 
                    
Output:  	transactionCode_quote
			reasonCodeOutput_quote
			oldTransactionCode_quote
			oldReasonCodeForOutput_quote
			competitorCode_quote

Updates:	20140625 - Andrew - pulled in competitorCode_quote to prevent blank competitor code
			20140630 - Andrew - Added in logic for when division is 0.
			20140901 - Aaron Q - SR 3-9423125271 to prevent blank competitor code
			20140918 - John Palubinskas - Added functionality to handle setting trans/reason code to 01/01
                     for existing customers adding service to a new site.  Added document header to
                     track updates. Issue 776.
            20150326 - John Palubinskas - #449 remove check for new from competitor
    
=====================================================================================================
*/
SERVICE_CHANGE = "Service Change";
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";

parentDocNumList = string[];

newSmallContainerFlag = false;
newLargeContainerFlag = false;
priceIncreaseExists = false;
priceDecreaseExists = false;
serviceChangeExists = false;

configSalesActivityDict = dict("string"); //key is modelDocNum, Value is salesActivity selection in config
configPriceAdjDict = dict("string"); //key is modelDocNum, Value is priceAdj selection in config
configCloseContDict = dict("string");
currentPriceDict = dict("float"); //key is modelDocNum, value is currentPrice of the child line item whose rateType is Base
sellPriceDict = dict("float"); //key is modelDocNum, value is core/sell/proposed price of the child line item whose rateType is Base
competitorNameDict = dict("string"); //key is modelDocNum, value is config attr "competitor"

newSite = existingCustomerWithNewSite_quote; // 20140918
competitorCode = competitorCode_quote; // 20140625
print existingCustomerWithNewSite_quote;
print competitorCode;
print competitorCode_quote;
totalNewYardsPerMonth = 0.0;
totalOldYardsPerMonth = 0.0;
retStr = "";
transactionCode_Old_CSA = "";
reasonCode_Old_CSA = "";
transactionCode_New_CSA = "";
reasonCode_New_CSA = "";

if(contractStatus_quote == "Close Account" OR contractStatus_quote == "Close Site"){
	transactionCode_New_CSA = "04";
	reasonCodeArr = split(reasonCode_quote, "-");
	reasonCode_New_CSA = reasonCodeArr[0];
}elif(salesActivity_quote == "New/New"){
	transactionCode_New_CSA = "01";
	reasonCode_New_CSA = "01";
// }elif(salesActivity_quote == "New from Competitor"){
// 	transactionCode_New_CSA = "01";
// 	reasonCode_New_CSA = "02";
}elif(salesActivity_quote == "Change of Owner"){ //Only for Change of Owner, Old CSA applies, for everything else it is new CSA
	transactionCode_New_CSA = "01";
	reasonCode_New_CSA = "11";
	transactionCode_Old_CSA = "04";
	reasonCode_Old_CSA = "11";
}elif(salesActivity_quote == "Existing Customer"){
	/*START OF LINE ITEM LOOP*/
	for line in line_process{
		docNum = line._document_number;
		parentDocNum = line._parent_doc_number;
		if(line._model_name <> ""){
			append(parentDocNumList, docNum);
			if(line._model_name == SERVICE_CHANGE){
				salesActivity = getconfigattrvalue(docNum, "salesActivity");
				competitorName = getconfigattrvalue(docNum, "competitor");
				print salesActivity;
				//if(lower(salesActivity) == "Close container group"){
					closureReason = getconfigattrvalue(docNum, "closureReason");
					print closureReason;
					put(configCloseContDict, docNum, closureReason);
					
				//}
				
				if(lower(salesActivity) == "price adjustment"){
					priceAdjustmentReason = getconfigattrvalue(docNum, "priceAdjustmentReason");
					put(configPriceAdjDict, docNum, priceAdjustmentReason);
				}
				totalNewYardsPerMonth = totalNewYardsPerMonth + line.yardsPerMonth_line;
				totalOldYardsPerMonth = totalOldYardsPerMonth + line.currentYardsPerMonth_line;
				
				put(configSalesActivityDict, docNum, salesActivity);
				put(competitorNameDict, docNum, competitorName);
				
			}
			
			if(line._model_name == SMALL_CONTAINER){
				newSmallContainerFlag = true;
				totalNewYardsPerMonth = totalNewYardsPerMonth + line.yardsPerMonth_line;
			}
			if(line._model_name == LARGE_CONTAINER){
				newLargeContainerFlag = true;
			}
			
		}else{
			if(line.rateType_line == "Base"){
				put(currentPriceDict, parentDocNum, line.currentPrice_line);
				put(sellPriceDict, parentDocNum, line.sellPrice_line);
			}
		}
		
	}
	/*END OF LINE ITEM LOOP*/

	// All new sites on existing customers should code to 01/01. 20140918
	if(newSite){
		transactionCode_New_CSA = "01";
		reasonCode_New_CSA = "01";
	// We need to assume that if a large container exists on an Existing Customer quote and it is not a new site, 
	// then it is overall SERVICE INCREASE irrespective of any calculations
	}elif(newLargeContainerFlag){
		//overallServiceIncrease = true;
		/****OVERALL SERVICE INCREASE****/
		transactionCode_New_CSA = "02";
		reasonCode_New_CSA = "58";
	// If there is no largeContainer, but there is a new Small container, then it is "Service change', 
	// but we need to determine if it is Service increase or decrease by comparing existingYards and NewYards
	}elif(newSmallContainerFlag){ 
		//Brittany's comment: .  If the change in yards is 0, I would default to service increase of 02/58.
		if(totalNewYardsPerMonth >= totalOldYardsPerMonth){ 
			/****OVERALL SERVICE INCREASE****/
			transactionCode_New_CSA = "02";
			reasonCode_New_CSA = "58";
		}else{
			//overallServiceDecrease = true;
			/****OVERALL SERVICE DECREASE****/
			transactionCode_New_CSA = "05";
			reasonCode_New_CSA = "58";
		}
	}
	/*If there is no "New small Container", then find out if it is Service Change or Price Adjustment.
	  If there are two models - one service change and one price adjustment (only rollback can co-exist 
	  with service change), then it will be serviceChnage and we need to determine it is increase or decrease 
	  by number of yards if there is price Increase, there won't be anything else but price increase, 
	  so look for price increase first.
	*/
	else{ 
		for eachDoc in parentDocNumList{
			if(containskey(configSalesActivityDict, eachDoc)){
				salesActivity = get(configSalesActivityDict, eachDoc);
				if(lower(salesActivity) == "price adjustment"){
					if(containskey(configPriceAdjDict, eachDoc)){
						priceAdjReason = get(configPriceAdjDict, eachDoc);
						if(find(lower(priceAdjReason), "price increase") <> -1){
							priceIncreaseExists = true;
							break;
						}else{
							priceDecreaseExists = true;
						}
					}
				}elif(lower(salesActivity) == "service level change"){
					serviceChangeExists = true;
				}
			}
		}
	}
	//If Price Increase, then only price increase exists on all models (new small, new large do not exist), so just check for overall change in Price
	if(priceIncreaseExists){ 
		totalCurrentPrice = 0.0;
		totalSellPrice = 0.0;
		for each in parentDocNumList{
			if(containskey(currentPriceDict, each)){
				thisCurrPrice = get(currentPriceDict, each);
				totalCurrentPrice = totalCurrentPrice + thisCurrPrice;
			}
			if(containskey(sellPriceDict, each)){
				thisSellPrice = get(sellPriceDict, each);
				totalSellPrice = totalSellPrice + thisSellPrice;
			}
		}
		if(totalSellPrice >= totalCurrentPrice){
			/****OVERALL PRICE INCREASE****/
			transactionCode_New_CSA = "03";
			reasonCode_New_CSA = "62";
		}else{//if user selected price Increase, but overall New Price is more than currentPrice, then it is price decrease
			/****OVERALL PRICE DECREASE****/
			transactionCode_New_CSA = "06";
			reasonCode_New_CSA = ""; //As per Brad's email on 04/24: I suppose it's possible the user selects the price increase transaction code but decreases the price in the end, meaning that you would not have a code. Just leave it blank in that case. 
		}
	} 
	//if there is no Price Increase on quote, then check if service change exists, then we don't have to check for price decrease, because service change will overwrite price decrease
	elif(serviceChangeExists){ //if servicechange exists, then determine if it is service increase or decrease
		if(totalNewYardsPerMonth >= totalOldYardsPerMonth){ 
			/****OVERALL SERVICE INCREASE****/
			transactionCode_New_CSA = "02";
			reasonCode_New_CSA = "58";
		}else{
			/****OVERALL SERVICE DECREASE****/
			transactionCode_New_CSA = "05";
			reasonCode_New_CSA = "58";
		}
	}
	//if there is no service change, then check for price decrease, but even though user selected price decrease, but overall Price increases, then it is Price increase	
	elif(priceDecreaseExists){
		totalCurrentPrice = 0.0;
		totalSellPrice = 0.0;
		for each in parentDocNumList{
			if(containskey(currentPriceDict, each)){
				thisCurrPrice = get(currentPriceDict, each);
				totalCurrentPrice = totalCurrentPrice + thisCurrPrice;
			}
			if(containskey(sellPriceDict, each)){
				thisSellPrice = get(sellPriceDict, each);
				totalSellPrice = totalSellPrice + thisSellPrice;
			}
		}
		if(totalSellPrice >= totalCurrentPrice){
			/****OVERALL PRICE INCREASE****/
			transactionCode_New_CSA = "03";
			reasonCode_New_CSA = "62";
		}else{
			/****OVERALL PRICE DECREASE****/
			transactionCode_New_CSA = "06";
			for each in parentDocNumList{
					priceAdjReason = "";
					print configPriceAdjDict;
					thisCurrentPrice = get(currentPriceDict, each);
					thisSellPrice = get(sellPriceDict, each);
					if(thisSellPrice < thisCurrentPrice){
						print "each"; print each;
						if(containskey(configPriceAdjDict, each)){
							priceAdjReason = lower(get(configPriceAdjDict, each));
						}	
						print "--thisSellPrice--"; print thisSellPrice;
						print "--thisCurrentPrice-"; print thisCurrentPrice;
						print "--priceAdjReason-"; print priceAdjReason;
						if(priceAdjReason == "rollback of pi"){
							reasonCode_New_CSA = "17";
						}elif(priceAdjReason == "rollback of current price"){
							reasonCode_New_CSA = "62";
						}if(priceAdjReason == "rollback: competitive bid"){
							reasonCode_New_CSA = "13";
							// Jun 02, 2014: Case 0127 - "you will see that the user selected price decrease due to competitive bid and the competitor 
							// code is showing N/A instead of mapping to the code just like the new from competition would.  This would only show up when 
							// you see the transaction/reason code of 06/13 when the user selects price decrease competitive bid."
							competitorName = get(competitorNameDict, each);
							
							print competitorName;
							print division_quote;

							/*if(competitorCode == ""){  // 20140901*/
								compCodeRecSet = bmql("SELECT Competitor_Cd, division FROM div_competitor_adj WHERE  competitor = $competitorName AND division = $division_quote OR competitor = $competitorName AND division = '0'");
								for eachRec in compCodeRecSet
								{
									if(get(eachRec, "division") == "0")
									{
										competitorCode = get(eachRec, "Competitor_Cd");
										print "pass here";
										break;
									}
									else
									{
										competitorCode = get(eachRec, "Competitor_Cd");
										print "pass there";
										break;
									}
								}
							/*}*/ print competitorCode;
						}
						break; //Break after you get the reasonCode of 1st model which has lower sell price than current price as per Brad's email on 04/24
					}	
				
			}	
		}
	}
	for eachDoc in parentDocNumList{
		if(containskey(configSalesActivityDict, eachDoc)){
			salesActivity = get(configSalesActivityDict, eachDoc);
						
			if(lower(salesActivity) == "close container group"){
				if(containskey(configCloseContDict, eachDoc)){
					closureReason = get(configCloseContDict, eachDoc);
					print closureReason;
					if(lower(closureReason) == "02 - lost to competitor" OR lower(closureReason) == "56 - competitor pricing"){
						print closureReason;
						competitorName = get(competitorNameDict, eachDoc);
						compCodeRecSet = bmql("SELECT Competitor_Cd, division FROM div_competitor_adj WHERE  competitor = $competitorName AND division = $division_quote OR competitor = $competitorName AND division = '0'");
						for eachRec in compCodeRecSet
						{
							if(get(eachRec, "division") == "0")
							{
								competitorCode = get(eachRec, "Competitor_Cd");
								print "pass here";
								break;
							}
							else
							{
								competitorCode = get(eachRec, "Competitor_Cd");
								print "pass there";
								break;
							}
						}
					}
				}
			}
		}	
	}
}
print competitorCode;
retStr = retStr +  "1~" + "transactionCode_quote" + "~" + transactionCode_New_CSA + "|"
				+  "1~" + "reasonCodeOutput_quote" + "~" + reasonCode_New_CSA + "|"
				+  "1~" + "oldTransactionCode_quote" + "~" + transactionCode_Old_CSA + "|"
				+  "1~" + "oldReasonCodeForOutput_quote" + "~" + reasonCode_Old_CSA + "|"
				+  "1~" + "competitorCode_quote" + "~" + competitorCode + "|";
				
return retStr;