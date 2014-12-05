retStr = "";
creditStr = "";
installStr = "";
pickupStr = "";
minTonStr = "";
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
			deliveryCredit = line.totalTargetPrice_line -  line.sellPrice_line ;
			if(deliveryCredit > 0.0){
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Delivery for " + routeType + " " + containerSize + " yard - " + formatascurrency(deliveryCredit, "USD") + "</p>";				
			}
		}
		if(line.rateType_line == "Exchange"){
			excCredit = line.totalTargetPrice_line -  line.sellPrice_line ;
			if(excCredit > 0.0){
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Exchange for " + routeType + " " + containerSize + " yard - " + formatascurrency(excCredit, "USD") + "</p>";
			}
		}
		if(line.rateType_line == "Removal"){
			remCredit = line.totalTargetPrice_line -  line.sellPrice_line ;
			if(remCredit > 0.0){
				creditStr = creditStr + "<p>" + "Valued Customer Discount - Removal for " + routeType + " " + getconfigattrvalue(line._parent_doc_number,"containerSize_readOnly") + " yard - " + formatascurrency(remCredit, "USD") + "</p>";
			}
		}
		if(line.rateType_line == "Installation"){
			installationChg = line.sellPrice_line ;
			if(installationChg > 0.0){
				installStr = installStr + "<p>" + "One-time Installation Charge for " + routeType + " " + containerSize + " yard - " + formatascurrency(installationChg, "USD") + "</p>";
			}
		}
		if(line.rateType_line == "Base" OR line.rateType_line == "Haul"){
			print "parentDocNum"; print parentDocNum; 
			
			if(containskey(pickupDaysDict, parentDocNum)){
				pickupStr = pickupStr + "<p>" + "Tentative Pickup Days for " + routeType + " " + containerSize + " yard - " + get(pickupDaysDict, parentDocNum) + "</p>";
			}
		}
		if(line.rateType_line == "Haul"){
			print "parentDocNum"; print parentDocNum; 	
			if(containskey(minTonsDict, parentDocNum)){
				minTonStr = minTonStr + "<p>" + "Minimum Tons for " + routeType + " " + containerSize + " yard - " + get(minTonsDict, parentDocNum) + "</p>";
			}
		}
	}
}


print minTonStr;
retStr = retStr + "1~" + "deliveryAndExchangeCreditStringForOutput_quote" + "~" + creditStr + "|";
retStr = retStr + "1~" + "installationChargeHTMLString_quote" + "~" + installStr + "|";
retStr = retStr + "1~" + "pickUpDaysCommentHTML_quote" + "~" + pickupStr + "|";
retStr = retStr + "1~" + "minimumTonnageHTMLStr_quote" + "~" + minTonStr + "|";

return retStr;
