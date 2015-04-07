/*
=======================================================================================================================
Name:   Print Action - comments For Delivery And Exchange Credit On Output
Author:   
Create date:  

Description: Runs code on print and email actions. Performs complex functionality to generate string values to be used in DOC engine
        
Input:       line item grid values.            
                    
Output:      String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:   	 01/21/15 - Gaurav (Republic) - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
		 03/27/15 - Mike (Republic( - #145 - Small Container Compactor - Added the compactor asset value comments for the CSA
		 04/07/15 - Mike (Republic( - #145 - Small Container Compactor - Fixed the source of compactor asset value.


=======================================================================================================================
*/
retStr = "";
creditStr = "";
installStr = "";
assetStr = "";
pickupStr = "";
minTonStr = "";
//line 24 to 31 - added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
container_count = "";
numberOfTotalRemovalContainers = 0;
numberOfTotalDeliveryContainers = 0;
exchangeChargeSubtotal_onetime = 0.0;
deliveryChargeSubtotal_onetime = 0.0;
remCredit2 = 0.0;
deliveryCredit2 = 0.0;
divisionDelStr_ui = 0.0;
modelDocNumArr = string[];
routeTypeDict = dict("string"); 
sizeDict = dict("string");
pickupDaysDict = dict("string");
minTonsDict = dict("string");

for line in line_process{
	docNum = line._document_number;
	if(line._model_variable_name <> ""){
		containerSize = "";
		append(modelDocNumArr, docNum);
		//put(routeTypeDict, docNum, line._part_custom_field9);
		if(line._model_variable_name == "containers_m"){
			containerSize = getconfigattrvalue(docNum, "containerSize");
			if(getconfigattrvalue(docNum, "containerCodeDerived") <> ""){
				put(routeTypeDict, docNum, getconfigattrvalue(docNum, "containerCodeDerived"));
			}
			if(getconfigattrvalue(docNum, "tentativePickupDays") <> ""){
				put(pickupDaysDict, docNum, getconfigattrvalue(docNum, "tentativePickupDays"));
			}
		}elif(line._model_variable_name == "largeContainers"){
			containerSize = getconfigattrvalue(docNum, "equipmentSize_l");
			billingType = getconfigattrvalue(docNum, "billingType_l");
			if(billingType == "Haul + Minimum Tonnage"){
				minTons = getconfigattrvalue(docNum, "minimumTonshaul_l");
				put(minTonsDict, docNum, minTons);
			}
			if(getconfigattrvalue(docNum, "routeTypeDervied") <> ""){
				put(routeTypeDict, docNum, getconfigattrvalue(docNum, "routeTypeDervied"));
			}
			if(getconfigattrvalue(docNum, "tentativePickupDays") <> ""){
				put(pickupDaysDict, docNum, getconfigattrvalue(docNum, "tentativePickupDays"));
			}
		}elif(line._model_variable_name == "serviceChange"){
			containerSize = getconfigattrvalue(docNum, "containerSize_sc");
			if (containerSize == "No Change"){
				containerSize = getconfigattrvalue(docNum, "containerSize_readOnly");
			} 
			if(getconfigattrvalue(docNum, "containerCodeDerived") <> ""){
				put(routeTypeDict, docNum, getconfigattrvalue(docNum, "containerCodeDerived"));
			}
			if(getconfigattrvalue(docNum, "tentativePickupDays_sc") <> ""){
				put(pickupDaysDict, docNum, getconfigattrvalue(docNum, "tentativePickupDays_sc"));
			}
		}
		put(sizeDict, docNum, containerSize);
		
		
	}
	if(line._model_name == ""){
		parentDocNum = line._parent_doc_number;
		routeType = "";
		containerSize = "";
		if(containskey(routeTypeDict, parentDocNum)){
			routeType = get(routeTypeDict, parentDocNum);
		}
		if(containskey(sizeDict, parentDocNum)){
			containerSize = get(sizeDict, parentDocNum);
		}
		if(line.rateType_line == "Delivery"){
			//line 90 to 112 - added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
			if(line._price_quantity > 1){
				container_count = "containers";
			}else{
				container_count = "container";
			}
			numberOfTotalDeliveryContainers = line._price_quantity;
			deliveryCredit = (line.totalTargetPrice_line -  line.sellPrice_line) *  line._price_quantity; print deliveryCredit;
			parentDoc = line._parent_doc_number;
			if(deliveryCredit > 0.0){
				deliveryCredit2 = deliveryCredit2 + (line.totalTargetPrice_line -  line.sellPrice_line) *  line._price_quantity;
				deliveryChargeSubtotal_onetime = deliveryChargeSubtotal_onetime + line.totalTargetPrice_line * line._price_quantity;
				print "total";print deliveryChargeSubtotal_onetime;
				print "deliveryCredit2";print deliveryCredit2;
				divisionDelStr_ui = line.totalTargetPrice_line;
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Delivery for " + string(numberOfTotalDeliveryContainers) + " " + container_count + " " + routeType + " " + containerSize + " yard - " + formatascurrency(deliveryCredit, "USD") + "</p>";	
				retStr = retStr + parentDoc + "~" + "divisionDelivery_ui_line" + "~" + string(divisionDelStr_ui) + "|";
			}
			else{
				deliveryChargeSubtotal_onetime = deliveryChargeSubtotal_onetime + line.sellPrice_line * line._price_quantity;
				divisionDelStr_ui = line.sellPrice_line;
				retStr = retStr + parentDoc + "~" + "divisionDelivery_ui_line" + "~" + string(divisionDelStr_ui) + "|";
			}
		}
		if(line.rateType_line == "Exchange"){
			excCredit = line.totalTargetPrice_line -  line.sellPrice_line ;
			if(excCredit > 0.0){
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Exchange for " + routeType + " " + containerSize + " yard - " + formatascurrency(excCredit, "USD") + "</p>";
			}
		}
		if(line.rateType_line == "Removal"){
			//line 121 to 136 - added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
			if(line._price_quantity > 1){
				container_count = "containers";
			}else{
				container_count = "container";
			}
			numberOfTotalRemovalContainers = line._price_quantity;
			remCredit = (line.totalTargetPrice_line -  line.sellPrice_line) *  line._price_quantity;
			if(remCredit > 0.0){
				remCredit2 = remCredit2 + (line.totalTargetPrice_line -  line.sellPrice_line) *  line._price_quantity;
				exchangeChargeSubtotal_onetime = exchangeChargeSubtotal_onetime + line.totalTargetPrice_line * line._price_quantity;
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Removal for " + string(numberOfTotalRemovalContainers) + " " + container_count + " " + routeType + " " + getconfigattrvalue(line._parent_doc_number,"containerSize_readOnly") + " yard - " + formatascurrency(remCredit, "USD") + "</p>";
			}
			else{
				exchangeChargeSubtotal_onetime = exchangeChargeSubtotal_onetime + line.sellPrice_line * line._price_quantity;
			}
		}
		if(line.rateType_line == "Installation"){
			installationChg = line.sellPrice_line;
			if(installationChg > 0.0){
				installStr = installStr + "<p>" + "One-time Installation Charge for " + routeType + " " + containerSize + " yard - " + formatascurrency(installationChg, "USD") + "</p>";
			}
		}
		if(line.rateType_line == "Compactor Rental"){
			
			assetValueStr = getconfigattrvalue(line._parent_doc_number, "compactorValue");
			if(isnumber(assetValueStr)){
				assetValue = atof(assetValueStr);
				if(assetValue > 0.0){
					assetStr = assetStr + "<p>" + "Compactor Asset Value for " + routeType + " " + containerSize + " yard - " + formatascurrency(assetValue, "USD") + "</p>";
				}
			}
		}
		if(line.rateType_line == "Base" OR line.rateType_line == "Haul"){
			//print "parentDocNum"; print parentDocNum; 
			
			if(containskey(pickupDaysDict, parentDocNum)){
				pickupStr = pickupStr + "<p>" + "Tentative Pickup Days for " + routeType + " " + containerSize + " yard - " + get(pickupDaysDict, parentDocNum) + "</p>";
			}
		}
		if(line.rateType_line == "Haul"){
			//print "parentDocNum"; print parentDocNum; 	
			if(containskey(minTonsDict, parentDocNum)){
				minTonStr = minTonStr + "<p>" + "Minimum Tons for " + routeType + " " + containerSize + " yard - " + get(minTonsDict, parentDocNum) + "</p>";
			}
		}
	}
}


retStr = retStr + "1~" + "deliveryAndExchangeCreditStringForOutput_quote" + "~" + creditStr + "|"; 
retStr = retStr + "1~" + "installationChargeHTMLString_quote" + "~" + installStr + "|";
retStr = retStr + "1~" + "assetValueHTMLString_quote" + "~" + assetStr + "|";
retStr = retStr + "1~" + "pickUpDaysCommentHTML_quote" + "~" + pickupStr + "|";
retStr = retStr + "1~" + "minimumTonnageHTMLStr_quote" + "~" + minTonStr + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "oneTime_exchangeCustomerDiscount_quote" + "~" + string(remCredit2) + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "numberOfTotalRemovalContainers_quote" + "~" + string(numberOfTotalRemovalContainers) + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "numberOfTotalDeliveryContainers_quote" + "~" + string(numberOfTotalDeliveryContainers) + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "oneTime_valuedCustomerDiscount_quote" + "~" + string(deliveryCredit2) + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "exchangeChargeSubtotal_onetime_quote" + "~" + string(exchangeChargeSubtotal_onetime) + "|";
//added 20150119 - GD - #352 #322 - making delivery and removal "Per Service compared to "One time" and correcting the calculations for Delivery and removal
retStr = retStr + "1~" + "deliveryChargeSubtotal_onetime_quote" + "~" + string(deliveryChargeSubtotal_onetime) + "|";

return retStr;
