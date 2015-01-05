/*
=======================================================================================================================
Name:   Save Action - Post Formula Modify
Author:   
Create date:  

Description: Runs code after Commerce formulas. Performs complex functionality not supported by
             formulas, but requiring formula values to run first. Currently used in the Save And
             Price action
        
Input:       division_quote:    String - Division of quote
             region_quote:      String - Region of quote
             _part_number:      String - Part number for this line
             _config_attr_info:	String - attributes from Config for use by getconfigattrvalue
                    
Output:      String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:     11/21/13 - Zach Schlieder - Removed Sell Price calculations (moved to Formula Management)
                                         Removed building of description_line (moved to Formula Management)
             11/22/13 - Zach Schlieder - Added Commission Rate calculations from Pre-Pricing formulas
             11/26/13 - Latha - Added code for approvals
             01/30/14 - Srikar - Formulas -> BML conversion
             02/11/14 - Zach - Added additional Delivery line functionality for Doc Engine
             ...other changes were made that were not documented between these entries
             09/09/14 - John (Republic) - Fixed issue where ERF was being set to 0.00 if FRF was unchecked.
             09/15/14 - Blabes (Republic) - Fixed Hierarchy_Exceptions processing to match that used by User_Hierarchy
             09/24/14 - John (Republic) - Added AdHoc line items to grandTotalSell_quote and totalOneTimeAmount_quote
			 09/25/14 - Julie (Oracle) - Added logic to calculate the total number of containers that have a 1 time delivery fee
			 11/11/14 - Aaron Q (Oracle) - Added logic to manage container removal/delivery in place of exchange code, including credit for removal
			 11/18/14 - Aaron Q (Oracle) - Added exchange charge value from removal in place of exchange codes.
	     01/05/15 - Julie (Oracle) - Populating the approval attribute for the approval e-mails.  Added a new util (setApprovalReasonDisplayWithColor)
Debugging:   Under "System" set _system_user_login and _system_current_step_var=adjustPricing
    
=======================================================================================================================
*/

//=============================== START - Variable Initialization ===============================//
returnStr = "";
divisionSalesGroup= "";
divisionManagerGroup= "";
divisionExecManagerGroup= "";

frfTotalSellFloor = 0.0;
frfTotalSellBase = 0.0;
frfTotalSellTarget = 0.0;
frfTotalSellStretch = 0.0;
frfTotalSell = 0.0;

erfTotalSellFloor = 0.0;
erfTotalSellBase = 0.0;
erfTotalSellTarget = 0.0; 
erfTotalSell = 0.0; 
erfTotalSellStretch = 0.0;



largeMonthlyHaulPriceInclFees = 0.0;
largeMonthlyDisposalPriceInclFees = 0.0;
largeMonthlyRentalPriceInclFees = 0.0;

largeMonthlyHaulPriceInclFees_SW = 0.0;
largeMonthlyDisposalPriceInclFees_SW = 0.0;
largeMonthlyRentalPriceInclFees_SW = 0.0;

largeMonthlyHaulPriceInclFees_Recycling = 0.0;
largeMonthlyDisposalPriceInclFees_Recycling = 0.0;
largeMonthlyRentalPriceInclFees_Recycling = 0.0;

largeMonthlyTotalPriceInclFees = 0.0; //Both Solid waste and recycling combined - large
largeMonthlyRevenue_SolidWaste = 0.0; //Only solid waste - large
largeMonthlyRevenue_Recycling = 0.0; //Only Recycling - small
largeMonthlyNetRevenue_SolidWaste = 0.0; //Only solid waste - large
largeMonthlyNetRevenue_Recycling = 0.0; //Only Recycling - small

smallMonthlyPriceIncludingFees = 0.0; //Both Solid waste and recycling combined - small
smallMonthlyRevenue_SolidWaste = 0.0; //Only solid waste - small
smallMonthlyRevenue_Recycling = 0.0; //Only Recycling - small 
smallMonthlyNetRevenue_SolidWaste = 0.0; //Only solid waste - small
smallMonthlyNetRevenue_Recycling = 0.0; //Only Recycling - small 

totalMonthlyAmount = 0.0;
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";
SERVICE_CHANGE = "Service Change";
firstModelDataStr = "";
delimer = "$$";



//Calculation of One time delivery credit and amount
totalOneTimePrice = 0.0;
oneTimeDeliveryCredit = 0.0;
deliveryChargeSubtotal = 0.0;
deliveryERFandFRFTotal = 0.0;

//delivery containers
 numberOfTotalDeliveryContainers = 0;

//Exchange Credit Variables
exchangeChargeSubtotal = 0.0;
oneTimeExchangeCredit = 0.0;
exchangeERFandFRFTotal = 0.0;
hasDeliveryArr = string[]; //ParentDocNo Used to determine if there is an exchange happening using a deliver and removal instead of a single exchange line AQ 11-11-14
hasRemovalDict = dict("float"); //ParentDocNo => line price

//Installation Charges Variables
installationChargeSubtotal = 0.0;
installationERFandFRFTotal = 0.0;

oneTimeERFandFRFTotal = 0.0;

//AdHoc Variables
adHocMonthlyTotalSell = 0.0;
adHocPerHaulTotalSell = 0.0;
adHocOneTimeTotalSell = 0.0;



//=============================== END - Variable Initialization ===============================//
testcount = 0;
adminRateAlreadyAppliedOnLine = false;
docNumOfLineAdminRateApplied = "";
for line in line_process{
	if(line._parent_doc_number <> ""){ //Only part line items
		deliveryPrice = 0.0;
		estLiftsPerMonth = 0.0;
		estTonsPerHaul = 0.0;
		testcount = testcount + 1;
		print testcount;
		if(isnumber(line.estimatedLifts_line)){
			estLiftsPerMonth = atof(line.estimatedLifts_line);
		}
		estTonsPerHaul = line.estTonsHaul_Line;

		erfAmountTarget = 0.0;
		erfAmountFloor = 0.0;
		erfAmountBase = 0.0;
		erfAmountStretch = 0.0;
		erfAmountSell = 0.0;
		
		frfAmountTarget = 0.0;
		frfAmountFloor = 0.0;
		frfAmountSell = 0.0;
		frfAmountBase = 0.0;
		frfAmountStretch = 0.0;
		
		
		// Begin Calculate ERF & FRF Fees
		//Begin ERF Fees calculation for each line item
		/*erfAmountTarget = line.targetPrice_line;
		erfAmountFloor = line.floorPrice_line;
		erfAmountBase = line.basePrice_line;
		erfAmountStretch = line.stretchPrice_line;
		erfAmountSell = line.sellPrice_line;*/

		if(isFRFWaived_quote == 1){ //If FRF Waived
			//Added 7/8/14, if-condition when user doesn't select administrative fee
			//20140909 JP: added adminRateAlreadyAppliedOnLine check since ERF was being set to 0.00 if FRF was unchecked
			if(includeAdmin_quote == "No" OR adminRateAlreadyAppliedOnLine){
				erfAmountSell = line.sellPrice_line * erfRate_quote;	
				erfAmountTarget = line.targetPrice_line *  erfRate_quote;
				erfAmountFloor = line.floorPrice_line * erfRate_quote;
				erfAmountBase = line.basePrice_line * erfRate_quote;
				erfAmountStretch = line.stretchPrice_line * erfRate_quote;
			}
			//Added 7/8/14, including admin rate to erf 
			else{
				if(NOT(adminRateAlreadyAppliedOnLine)){
					erfAmountSell = (line.sellPrice_line + adminRate_quote) * erfRate_quote;	
					erfAmountTarget = (line.targetPrice_line + adminRate_quote) *  erfRate_quote;
					erfAmountFloor = (line.floorPrice_line + adminRate_quote) * erfRate_quote;
					erfAmountBase = (line.basePrice_line + adminRate_quote) * erfRate_quote;
					erfAmountStretch = (line.stretchPrice_line + adminRate_quote) * erfRate_quote;
					adminRateAlreadyAppliedOnLine = true;
					docNumOfLineAdminRateApplied = line._document_number;
				}	
			}
		}else{
			//Added 7/8/14, if-condition when user doesn't select administrative fee
			if(includeAdmin_quote == "No" OR adminRateAlreadyAppliedOnLine){
				erfAmountSell = (line.sellPrice_line * (1 + frfRate_quote)) * erfRate_quote;
				erfAmountTarget = (line.targetPrice_line * (1 + frfRate_quote)) * erfRate_quote;
				erfAmountFloor = (line.floorPrice_line * (1 + frfRate_quote)) * erfRate_quote;
				erfAmountBase = (line.basePrice_line * (1 + frfRate_quote)) * erfRate_quote;
				erfAmountStretch = (line.stretchPrice_line * (1 + frfRate_quote)) * erfRate_quote;
			}
			//Added 7/8/14, including admin rate to erf 
			else{
				//if((docNumOfLineAdminRateApplied == line._document_number) OR NOT(adminRateAlreadyAppliedOnLine)){
					erfAmountSell = (line.sellPrice_line + adminRate_quote) * (1 + frfRate_quote) * erfRate_quote;
					erfAmountTarget = (line.targetPrice_line + adminRate_quote) *  (1 + frfRate_quote) * erfRate_quote;
					erfAmountFloor = (line.floorPrice_line + adminRate_quote) * (1 + frfRate_quote) * erfRate_quote;
					erfAmountBase = (line.basePrice_line + adminRate_quote) * (1 + frfRate_quote) * erfRate_quote;
					erfAmountStretch = (line.stretchPrice_line + adminRate_quote) * (1 + frfRate_quote) * erfRate_quote;
					docNumOfLineAdminRateApplied = line._document_number;
					adminRateAlreadyAppliedOnLine = true;
				//}	
			}
		}

		//End ERF Fees calculation for each line item
		
		//FRF Fees Per each Line item
		//Added 7/8/14, if-condition when user doesn't select administrative fee
		if(includeAdmin_quote == "No" OR (adminRateAlreadyAppliedOnLine AND docNumOfLineAdminRateApplied <> line._document_number)){
			frfAmountTarget = line.targetPrice_line * frfRate_quote;
			frfAmountFloor = line.floorPrice_line * frfRate_quote;
			frfAmountSell = line.sellPrice_line * frfRate_quote;
			frfAmountBase = line.basePrice_line * frfRate_quote;
			frfAmountStretch = line.stretchPrice_line * frfRate_quote;
		}
		//Added 7/8/14, including admin rate to frf 
		else{
			frfAmountTarget = (line.targetPrice_line + adminRate_quote) * frfRate_quote;
			frfAmountFloor = (line.floorPrice_line + adminRate_quote)* frfRate_quote;
			frfAmountSell = (line.sellPrice_line + adminRate_quote)* frfRate_quote;
			frfAmountBase = (line.basePrice_line + adminRate_quote)* frfRate_quote;
			frfAmountStretch = (line.stretchPrice_line + adminRate_quote)* frfRate_quote;
			docNumOfLineAdminRateApplied = line._document_number;
			adminRateAlreadyAppliedOnLine = true;
		//End of FRF Fees Per each Line item
		}
		
		
		//Calculate Monthly Total ERF Fees - Calculate these only for Core/ Base line item
		if(line.rateType_line == "Base" AND line.isPartLineItem_line){ //if  the line item is smallcontainer base or largecontainer rental, the fee values on line item grid can be directly used as there is no multiplication factor of estimatedLifts/esthaulspermonth or disposal tons per haul that need to be applied on these line items
			if(isERFWaived_quote == 0){ //feesToCharge_quote has ERF
				//Begin - Calculate Monthly Total ERF Fees
				erfTotalSellFloor = erfTotalSellFloor + erfAmountFloor;
				erfTotalSellBase = erfTotalSellBase + erfAmountBase;
				erfTotalSellTarget = erfTotalSellTarget + erfAmountTarget;
				erfTotalSellStretch = erfTotalSellStretch + erfAmountStretch;
				erfTotalSell = erfTotalSell + erfAmountSell;
			}//End of ERF Fee Calculation
			
			//Begin - Calculate Monthly FRF Fees
			if(isFRFWaived_quote == 0){ ////feesToCharge_quote has FRF
				frfTotalSellTarget = frfTotalSellTarget + frfAmountTarget;
				frfTotalSellFloor = frfTotalSellFloor + frfAmountFloor;
				frfTotalSellBase = frfTotalSellBase + frfAmountBase;
				frfTotalSellStretch = frfTotalSellStretch + frfAmountStretch;
				frfTotalSell = frfTotalSell + frfAmountSell;
			}
			//End of FRF Fee calculation
		}elif(line.rateType_line == "Haul" AND line.isPartLineItem_line){ //multiply the erfAmt with estlifts/esthaulspermonth to get total erf monthly amount
			if(isERFWaived_quote == 0){ //feesToCharge_quote has ERF
				//Begin - Calculate Monthly Total ERF Fees
				erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * estLiftsPerMonth);
				erfTotalSellBase = erfTotalSellBase + (erfAmountBase * estLiftsPerMonth);
				erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * estLiftsPerMonth);
				erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * estLiftsPerMonth);
				erfTotalSell = erfTotalSell + (erfAmountSell * estLiftsPerMonth);
			}//End of ERF Fee Calculation
			
			//Begin - Calculate Monthly FRF Fees
			if(isFRFWaived_quote == 0){ ////feesToCharge_quote has FRF
				frfTotalSellTarget = frfTotalSellTarget + (frfAmountTarget * estLiftsPerMonth);
				frfTotalSellFloor = frfTotalSellFloor + (frfAmountFloor * estLiftsPerMonth);
				frfTotalSellBase = frfTotalSellBase + (frfAmountBase * estLiftsPerMonth);
				frfTotalSellStretch = frfTotalSellStretch + (frfAmountStretch * estLiftsPerMonth);
				frfTotalSell = frfTotalSell + (frfAmountSell * estLiftsPerMonth);
			}
			//End of FRF Fee calculation	
		}elif(line.rateType_line == "Disposal" AND line.isPartLineItem_line){ //multiply the erfAmt with estlifts/esthaulspermonth and estTonsperhaul to get total erf monthly amount
			if(isERFWaived_quote == 0){ //feesToCharge_quote has ERF
				//Begin - Calculate Monthly Total ERF Fees
				erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * estLiftsPerMonth * estTonsPerHaul);
				erfTotalSellBase = erfTotalSellBase + (erfAmountBase * estLiftsPerMonth * estTonsPerHaul);
				erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * estLiftsPerMonth * estTonsPerHaul);
				erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * estLiftsPerMonth * estTonsPerHaul);
				erfTotalSell = erfTotalSell + (erfAmountSell * estLiftsPerMonth * estTonsPerHaul);
			}//End of ERF Fee Calculation
			
			//Begin - Calculate Monthly FRF Fees
			if(isFRFWaived_quote == 0){ ////feesToCharge_quote has FRF
				frfTotalSellTarget = frfTotalSellTarget + (frfAmountTarget * estLiftsPerMonth * estTonsPerHaul);
				frfTotalSellFloor = frfTotalSellFloor + (frfAmountFloor * estLiftsPerMonth * estTonsPerHaul);
				frfTotalSellBase = frfTotalSellBase + (frfAmountBase * estLiftsPerMonth * estTonsPerHaul);
				frfTotalSellStretch = frfTotalSellStretch + (frfAmountStretch * estLiftsPerMonth * estTonsPerHaul);
				frfTotalSell = frfTotalSell + (frfAmountSell * estLiftsPerMonth * estTonsPerHaul);
			}
			//End of FRF Fee calculation
		}elif(line.rateType_line == "Rental" AND line.isPartLineItem_line){ //if  the line item is largecontainer rental, the fee values on line item grid should be used directly Monthly Rental as there is no multiplication factor. If Rental is daily, should be multiplied by factor 365/12
			if(isERFWaived_quote == 0){ //feesToCharge_quote has ERF
				if(line.billingType_line == "Monthly"){
					erfTotalSellFloor = erfTotalSellFloor + erfAmountFloor;
					erfTotalSellBase = erfTotalSellBase + erfAmountBase;
					erfTotalSellTarget = erfTotalSellTarget + erfAmountTarget;
					erfTotalSellStretch = erfTotalSellStretch + erfAmountStretch;
					erfTotalSell = erfTotalSell + erfAmountSell;
				}elif(line.billingType_line == "Daily"){
					//Begin - Calculate Monthly Total ERF Fees
					erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * 365/12);
					erfTotalSellBase = erfTotalSellBase + (erfAmountBase * 365/12);
					erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * 365/12);
					erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * 365/12);
					erfTotalSell = erfTotalSell + (erfAmountSell * 365/12);
				}	
				
			}//End of ERF Fee Calculation
			
			//Begin - Calculate Monthly FRF Fees
			if(isFRFWaived_quote == 0){ ////feesToCharge_quote has FRF
				if(line.billingType_line == "Monthly"){
					frfTotalSellTarget = frfTotalSellTarget + frfAmountTarget;
					frfTotalSellFloor = frfTotalSellFloor + frfAmountFloor;
					frfTotalSellBase = frfTotalSellBase + frfAmountBase;
					frfTotalSellStretch = frfTotalSellStretch + frfAmountStretch;
					frfTotalSell = frfTotalSell + frfAmountSell;
				}elif(line.billingType_line == "Daily"){
					frfTotalSellTarget = frfTotalSellTarget + (frfAmountTarget * 365/12);
					frfTotalSellFloor = frfTotalSellFloor + (frfAmountFloor * 365/12);
					frfTotalSellBase = frfTotalSellBase + (frfAmountBase * 365/12);
					frfTotalSellStretch = frfTotalSellStretch + (frfAmountStretch * 365/12);
					frfTotalSell = frfTotalSell + (frfAmountSell * 365/12);
				}	
			}
			//End of FRF Fee calculation
		}
		//End of Monthly Fees Calculation
		
		//Calculate Total Price of the line item; Price + FRF + ERF
		FRF_CONST = frfAmountSell;
		ERF_CONST = erfAmountSell;
		
		if(isFRFwaived_quote == 1){//If FRF Waived
			FRF_CONST = 0;
		}
		if(isERFwaived_quote == 1){//If ERF Waived
			ERF_CONST = 0;
		}
		totalPrice = ( ( line.sellPrice_line + FRF_CONST ) + ERF_CONST );
		//End of Total Price calculation
		
		//Begin special calculations for Delivery line items
		//08/18/2014 - 3-9457966641 if deliveryPrice is increased, that should become the new deliverysubtotal, however, if it is decreased, the default value will be subtotal
		if(line.rateType_line == "Delivery"){
			if(line.totalTargetPrice_line > line.sellPrice_line){
				deliveryChargeSubtotal = deliveryChargeSubtotal + line.totalTargetPrice_line;
				numberOfTotalDeliveryContainers = numberOfTotalDeliveryContainers + line._price_quantity;
				
			}else{
				deliveryChargeSubtotal = deliveryChargeSubtotal + line.sellPrice_line;
				returnStr = returnStr +  line._parent_doc_number + "~divisionDelivery_line" + "~" + string(line.sellPrice_line) + "|";
				numberOfTotalDeliveryContainers = numberOfTotalDeliveryContainers + line._price_quantity;
				
			}	
			oneTimeDeliveryCredit = oneTimeDeliveryCredit + (line.totalTargetPrice_line - line.sellPrice_line); //Credit is Target Price Sell/Proposed Price 
			deliveryPrice = line.sellPrice_line;
			deliveryERFandFRFTotal = deliveryERFandFRFTotal + FRF_CONST + ERF_CONST;
			totalOneTimePrice = totalOneTimePrice + totalPrice;
		}
		//End special calculations for Delivery line items
		
		//Begin Exchange calc for Exchange Lines
		if(line.rateType_line == "Exchange"){
			if(line.totalTargetPrice_line > line.sellPrice_line){
				exchangeChargeSubtotal = exchangeChargeSubtotal + line.totalTargetPrice_line ;
			}else{
				exchangeChargeSubtotal = exchangeChargeSubtotal + line.sellPrice_line;
			}	
			oneTimeExchangeCredit = oneTimeExchangeCredit + (line.totalTargetPrice_line - line.sellPrice_line); //Credit is Sell/Proposed Price - Target Price
			exchangeERFandFRFTotal = exchangeERFandFRFTotal + FRF_CONST + ERF_CONST;
			totalOneTimePrice = totalOneTimePrice + totalPrice;
		}
		elif(line.rateType_line == "Delivery"){
			append(hasDeliveryArr, line._parent_doc_number);
		}
		elif(line.rateType_line == "Removal"){
			put(hasRemovalDict, line._parent_doc_number, line.sellPrice_line);
		}
		
		//Installation line item
		if(line.rateType_line == "Installation"){
			installationChargeSubtotal = installationChargeSubtotal + line.sellPrice_line ;
			installationERFandFRFTotal = installationERFandFRFTotal + FRF_CONST + ERF_CONST;
			totalOneTimePrice = totalOneTimePrice + totalPrice;
		}
		pctOfTotalTargetPrice = 0.0;
		if(smallMonthlyTotalTarget_quote <> 0.0){
			pctOfTotalTargetPrice = line.targetPrice_line / smallMonthlyTotalTarget_quote;
		}

		//firstModelDataStr
		if(line._parent_doc_number == "" AND line._part_number == ""){	// Model Line		
			//Populate only with first model data
			wasteType = "";
			frequency = "";
			containerSize = "";
			quantity = "";
			if(firstModelDataStr == ""){
				if(line._model_name == SMALL_CONTAINER){
					if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType"))){
						wasteType = getconfigattrvalue(line._document_number, "wasteType");
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_s"))){
						frequency = getconfigattrvalue(line._document_number, "liftsPerContainer_s"); //Ex: 1/Week
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize"))){	
						containerSize = getconfigattrvalue(line._document_number, "containerSize");	
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "quantity"))){
						quantity=getconfigattrvalue(line._document_number, "quantity");
					}
				}
				//Get Service Change 
				elif(line._model_name == SERVICE_CHANGE){
					if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType_readOnly"))){
						wasteType = getconfigattrvalue(line._document_number, "wasteType_readOnly");
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "quantity_readOnly"))){
						quantity = getconfigattrvalue(line._document_number, "quantity_readOnly");
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize_readOnly"))){
						containerSize = getconfigattrvalue(line._document_number, "containerSize_readOnly");
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_readOnly"))){
						frequency = getconfigattrvalue(line._document_number, "liftsPerContainer_readOnly");
					}
					
					if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType_sc"))){
						wasteType_sc=getconfigattrvalue(line._document_number, "wasteType_sc");
						if(wasteType_sc <> "No Change"){
							wasteType = wasteType_sc;
						}
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "quantity_sc"))){
						quantity_sc=getconfigattrvalue(line._document_number, "quantity_sc");
						if(quantity_sc <> quantity){
							quantity = quantity_sc;
						}	
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "containerSize_sc"))){
						containerSize_sc=getconfigattrvalue(line._document_number, "containerSize_sc");
						if(containerSize_sc <> "No Change"){
							containerSize = containerSize_sc;
						}
					}
					if(NOT isnull(getconfigattrvalue(line._document_number, "liftsPerContainer_sc"))){
						liftsPerContainer_sc=getconfigattrvalue(line._document_number, "liftsPerContainer_sc");
						if(liftsPerContainer_sc <> "No Change"){
							frequency = liftsPerContainer_sc;
						}
					}
				}
				//Get Large Container
				elif(line._model_name == LARGE_CONTAINER){
					if(NOT isnull(getconfigattrvalue(line._document_number, "wasteType"))){
						wasteType = getconfigattrvalue(line._document_number, "wasteType");
					}
					
					if(NOT isnull(getconfigattrvalue(line._document_number, "haulsPerPeriod"))){
						frequency = getconfigattrvalue(line._document_number, "haulsPerPeriod");
					}
					
					if(NOT isnull(getconfigattrvalue(line._document_number, "equipmentSize_l"))){
						containerSize=getconfigattrvalue(line._document_number, "equipmentSize_l");
					}
					
					if(NOT isnull(getconfigattrvalue(line._document_number, "quantity"))){
						quantity=getconfigattrvalue(line._document_number, "quantity");
					}
					
				}

				containerSizeFloat = 0.0;
				if(isnumber(containerSize)){
					containerSizeFloat = atof(containerSize);
				}
				//Set the menu value
				firstModelDataStr = wasteType + delimer + quantity + delimer + string(containerSizeFloat) + delimer + frequency;
			}

		}
		
		//Return line item values
		returnStr = returnStr +  line._document_number + "~erfAmountTarget_line" + "~" + string(erfAmountTarget) + "|"
							  +  line._document_number + "~erfAmountFloor_line" + "~" + string(erfAmountFloor) + "|"
							  +  line._document_number + "~erfAmountSell_line" + "~" + string(erfAmountSell) + "|"
							  +  line._document_number + "~erfAmountBase_line" + "~" + string(erfAmountBase) + "|"
							  +  line._document_number + "~erfAmountStretch_line" + "~" + string(erfAmountStretch) + "|"
							  +  line._document_number + "~frfAmountTarget_line" + "~" + string(frfAmountTarget) + "|"
							  +  line._document_number + "~frfAmountFloor_line" + "~" + string(frfAmountFloor) + "|"
							  +  line._document_number + "~frfAmountSell_line" + "~" + string(frfAmountSell) + "|"
							  +  line._document_number + "~frfAmountBase_line" + "~" + string(frfAmountBase) + "|"
							  +  line._document_number + "~frfAmountStretch_line" + "~" + string(frfAmountStretch) + "|"
							  +  line._document_number + "~totalPrice_line" + "~" + string(totalPrice) + "|"
							  +  line._document_number + "~pctOfTotalTargetPrice_line" + "~" + string(pctOfTotalTargetPrice) + "|";
							  
		if(deliveryPrice <> 0.0){
			returnStr = returnStr + line._parent_doc_number + "~deliveryPrice_line" + "~" + string(deliveryPrice) + "|";
		}

		/*
		X = A + B + C; X is total fee, A is proposed price, B is FRF, C is ERF
		A * const + B * const + C * const = (A + B + C) * const = (X * const); constant is estlifts, estTons wherever relevant
		*/
		if(line.rateType_line == "Haul" AND line.isPartLineItem_line){
			largeMonthlyHaulPriceInclFees = largeMonthlyHaulPriceInclFees + (totalPrice * estLiftsPerMonth);
			if(lower(line.wasteCategory_line) == "solid waste"){
				largeMonthlyHaulPriceInclFees_SW = largeMonthlyHaulPriceInclFees_SW + (totalPrice * estLiftsPerMonth);
			}elif(lower(line.wasteCategory_line) == "recycling"){
				largeMonthlyHaulPriceInclFees_Recycling = largeMonthlyHaulPriceInclFees_Recycling + (totalPrice * estLiftsPerMonth);
			}
		}elif(line.rateType_line == "Disposal" AND line.isPartLineItem_line){
			largeMonthlyDisposalPriceInclFees = largeMonthlyDisposalPriceInclFees + (totalPrice * estLiftsPerMonth * estTonsPerHaul);
			if(lower(line.wasteCategory_line) == "solid waste"){
				largeMonthlyDisposalPriceInclFees_SW = largeMonthlyDisposalPriceInclFees_SW + (totalPrice * estLiftsPerMonth * estTonsPerHaul);
			}elif(lower(line.wasteCategory_line) == "recycling"){
				largeMonthlyDisposalPriceInclFees_Recycling = largeMonthlyDisposalPriceInclFees_Recycling + (totalPrice * estLiftsPerMonth * estTonsPerHaul);
			}
		}elif(line.rateType_line == "Rental" AND line.isPartLineItem_line){
			if(line.billingType_line == "Monthly"){
				largeMonthlyRentalPriceInclFees = largeMonthlyRentalPriceInclFees + totalPrice;
				if(lower(line.wasteCategory_line) == "solid waste"){
					largeMonthlyRentalPriceInclFees_SW = largeMonthlyRentalPriceInclFees_SW + totalPrice;
				}elif(lower(line.wasteCategory_line) == "recycling"){
					largeMonthlyRentalPriceInclFees_Recycling = largeMonthlyRentalPriceInclFees_Recycling + totalPrice;
				}
			}elif(line.billingType_line == "Daily"){
				largeMonthlyRentalPriceInclFees = largeMonthlyRentalPriceInclFees + (totalPrice * 365/12);
				if(lower(line.wasteCategory_line) == "solid waste"){
					largeMonthlyRentalPriceInclFees_SW = largeMonthlyRentalPriceInclFees_SW + (totalPrice * 365/12);
				}elif(lower(line.wasteCategory_line) == "recycling"){
					largeMonthlyRentalPriceInclFees_Recycling = largeMonthlyRentalPriceInclFees_Recycling + (totalPrice * 365/12);
				}
			}	
		}
		elif(line.rateType_line == "Base" AND line.isPartLineItem_line){
			smallMonthlyPriceIncludingFees = smallMonthlyPriceIncludingFees + totalPrice;
			if(lower(line.wasteCategory_line) == "solid waste"){
				smallMonthlyRevenue_SolidWaste = smallMonthlyRevenue_SolidWaste + totalPrice; //totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
			}elif(lower(line.wasteCategory_line) == "recycling"){
				smallMonthlyRevenue_Recycling = smallMonthlyRevenue_Recycling + totalPrice;//totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
			}
		}

		// 20140924 - Include amounts for adHoc items on grandTotalSell_quote and totalOneTimeAmount_quote
		if(line.rateType_line == "Ad-Hoc") {

			billingMethod = line.frequency_line;
			showOnProposal = getconfigattrvalue(line._parent_doc_number, "isDisplayedOnProposal_adhoc");

			if(showOnProposal == "true") {
			    if(billingMethod == "Monthly") { 
			    	adHocMonthlyTotalSell = adHocMonthlyTotalSell + line.sellPrice_line; 
			    }
			    if(billingMethod == "Per Haul") { 
			    	adHocPerHaulTotalSell = adHocPerHaulTotalSell + line.sellPrice_line;  
			    }
			    if(billingMethod == "One Time") { 
			    	adHocOneTimeTotalSell = adHocOneTimeTotalSell + line.sellPrice_line;
			    }
			}
			print "------------------JP";
			print "rateType_line: " + line.rateType_line;
			print "line.frequency_line: " + billingMethod;
			print "config.isDisplayedOnProposal_adhoc: " + showOnProposal;
			print "adHocMonthlyTotalSell: " + string(adHocMonthlyTotalSell);
			print "adHocPerHaulTotalSell: " + string(adHocPerHaulTotalSell);
			print "adHocOneTimeTotalSell: " + string(adHocOneTimeTotalSell);
		}

	}
}
totalOneTimePrice = totalOneTimePrice + adHocOneTimeTotalSell;

smallMonthlyNetRevenue_SolidWaste = smallMonthlyRevenue_SolidWaste - smallSolidWasteDisposalExpense_quote;
smallMonthlyNetRevenue_Recycling = smallMonthlyRevenue_Recycling - smallRecyclingDisposalExpense_quote;

//Changed 7/8/14, commented out since the admin fee for both erf and frf are already included in earlier logic
//Begin - Include ERF & FRF of Admin Fee to the actual ERF & FRF Totals
/*frfTotalSellFloor = frfTotalSellFloor + fRFOnAdminFee_quote;
frfTotalSellBase = frfTotalSellBase + fRFOnAdminFee_quote;
frfTotalSellTarget = frfTotalSellTarget + fRFOnAdminFee_quote;
frfTotalSellStretch = frfTotalSellStretch + fRFOnAdminFee_quote;
frfTotalSell = frfTotalSell + fRFOnAdminFee_quote;
*/
/*
erfTotalSellFloor = erfTotalSellFloor + eRFOnAdminFee_quote;
erfTotalSellBase = erfTotalSellBase + eRFOnAdminFee_quote;
erfTotalSellTarget = erfTotalSellTarget + eRFOnAdminFee_quote;
erfTotalSellStretch = erfTotalSellStretch + eRFOnAdminFee_quote;
erfTotalSell = erfTotalSell + eRFOnAdminFee_quote;
//End - Include ERF & FRF of Admin Fee to the actual ERF & FRF Totals
*/

//Total ERF and FRF fees - frf + erf (admin NOT included) for all 4 guardrails and final price
erfAndFrfTotalFloor = erfTotalSellFloor + frfTotalSellFloor;
erfAndFrfTotalBase = erfTotalSellBase + frfTotalSellBase;
erfAndFrfTotalTarget = erfTotalSellTarget + frfTotalSellTarget;
erfAndFrfTotalStretch = erfTotalSellStretch + frfTotalSellStretch;
erfAndFrfTotalSell = erfTotalSell + frfTotalSell;

//Total fees - frf + erf + admin for all 4 guardrails and final price
erfFrfAdminTotalFloor = erfTotalSellFloor + frfTotalSellFloor + adminRate_quote;
erfFrfAdminTotalBase = erfTotalSellBase + frfTotalSellBase + adminRate_quote;
erfFrfAdminTotalTarget = erfTotalSellTarget + frfTotalSellTarget + adminRate_quote;
erfFrfAdminTotalStretch = erfTotalSellStretch + frfTotalSellStretch + adminRate_quote;
erfFrfAdminTotalSell = erfTotalSell + frfTotalSell + adminRate_quote;



oneTimeTotalDeliveryAmount = deliveryChargeSubtotal - oneTimeDeliveryCredit + deliveryERFandFRFTotal;
oneTimeERFandFRFTotal = deliveryERFandFRFTotal + exchangeERFandFRFTotal + installationERFandFRFTotal;

//Total of Large Containers including fees
largeMonthlyTotalPriceInclFees = largeMonthlyHaulPriceInclFees + largeMonthlyDisposalPriceInclFees + largeMonthlyRentalPriceInclFees;
largeMonthlyRevenue_SolidWaste = largeMonthlyHaulPriceInclFees_SW + largeMonthlyDisposalPriceInclFees_SW + largeMonthlyRentalPriceInclFees_SW;
largeMonthlyRevenue_Recycling = largeMonthlyHaulPriceInclFees_Recycling + largeMonthlyDisposalPriceInclFees_Recycling + largeMonthlyRentalPriceInclFees_Recycling;

largeMonthlyNetRevenue_SolidWaste = largeMonthlyRevenue_SolidWaste - largeSolidWasteDisposalExpense_quote;
largeMonthlyNetRevenue_Recycling = largeMonthlyRevenue_Recycling - largeRecyclingDisposalExpense_quote;

//Total of small and large combined including fees
totalMonthlyAmount = largeMonthlyHaulPriceInclFees + largeMonthlyDisposalPriceInclFees + largeMonthlyRentalPriceInclFees + smallMonthlyPriceIncludingFees + adminRate_quote; //J.Felberg + eRFOnAdminFee_quote + fRFOnAdminFee_quote;

/* Calculate Monthly Grand Totals and commission for the guard rails */
grandTotalBase = 0.0;
grandTotalTarget = 0.0;
grandTotalStretch = 0.0;
grandTotalSell = 0.0;
grandTotalFloor = 0.0;

totalCommissionFloor = 0.0;
totalCommissionTarget = 0.0;
totalCommissionStretch = 0.0;
totalCommissionSell = 0.0;
totalCommissionBase = 0.0;

priceBand = "";
commissionRate = 0.0;
//Calculate totals only when at least 1 line item exists on quote
if(hasLineItemsOnQuote_quote){
	//Grandtotals - "grandTotalSell" include fees amount and so does totalMonthlyAmount = so both should be equal
	
	//Updated on My 28th 2014 - GD
	if(smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote > 0){
		grandTotalFloor = smallMonthlyTotalFloor_quote + largeMonthlyTotalFloor_quote + erfTotalSellFloor + frfTotalSellFloor + adminRate_quote;
		grandTotalBase = smallMonthlyTotalBase_quote + largeMonthlyTotalBase_quote + erfTotalSellBase + frfTotalSellBase + adminRate_quote;
		grandTotalTarget = smallMonthlyTotalTarget_quote + largeMonthlyTotalTarget_quote + erfTotalSellTarget + frfTotalSellTarget + adminRate_quote;
		grandTotalStretch = smallMonthlyTotalStretch_quote + largeMonthlyTotalStretch_quote + erfTotalSellStretch + frfTotalSellStretch + adminRate_quote;
		grandTotalSell = smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote + erfTotalSell + frfTotalSell + adminRate_quote + adHocMonthlyTotalSell + adHocPerHaulTotalSell;
	}
	else {
		returnStr = returnStr   + "1~" + "adminRate_quote" + "~" + "0.0" + "|";
	}
	
	//End of Monthly grand totals
	
	//Get the Price Band based on the monthly grand guard rail totals
	if((grandTotalSell >= grandTotalFloor) AND (grandTotalSell < grandTotalBase)){
		priceBand = "floor";
	}elif((grandTotalSell >= grandTotalBase) AND (grandTotalSell < grandTotalTarget)){
		priceBand = "base";
	}elif((grandTotalSell >= grandTotalTarget) AND (grandTotalSell < grandTotalStretch)){
		priceBand = "target";
	}elif((grandTotalSell >= grandTotalStretch)){
		priceBand = "stretch";
	}
	//End of Price Band check
	
	
	//Get Commission Rate from commissionRate table for corresponding price band
	commissionRateRS = bmql("SELECT commissionRate FROM commissionRate WHERE ((priceBand = $priceBand OR priceBand = 'All' OR priceBand = '') AND (ERF = $includeERF_quote OR ERF = 'All') AND (FRF = $includeFRF_quote OR FRF = 'All'))");
	for eachRecord in commissionRateRS{
		commissionRate = getFloat(eachRecord, "commissionRate");
	}
	
	//Calculate Monthly total guard rail commissions 
	totalCommissionFloor = grandTotalFloor * commissionRate; 
	totalCommissionTarget =  grandTotalTarget * commissionRate;
	totalCommissionStretch =  grandTotalStretch * commissionRate;
	totalCommissionSell =  grandTotalSell * commissionRate;
	totalCommissionBase =  grandTotalBase * commissionRate;
	//End of monthly commissions
	
	//Reset Proposed Total & Goal Seek values based on Total Sell Price
	currentQuoteTotal = grandTotalSell;
	if(setPriceTo_quote == "target"){
		currentQuoteTotal = grandTotalTarget;
	}elif(setPriceTo_quote == "stretch"){ //Action seems to be Not used - confirm
		currentQuoteTotal = grandTotalStretch; 
	}
	desiredQuoteTotal = currentQuoteTotal;
	returnStr = returnStr +  "1~" + "currentQuoteTotal_quote" + "~" + string(currentQuoteTotal) + "|";
	returnStr = returnStr +  "1~" + "desiredQuoteTotal_quote" + "~" + string(desiredQuoteTotal) + "|";
	
}
//End Monthly grand total calculations


//=============================== START - APPROVALS ===============================//
/* Check table Approval_Triggers to determine what level of approval are required based on the Division*/
approvalReasonHTML  = "";
hasCompactor = false;
level1ApprovalReasonArr = string[];
level2ApprovalReasonArr = string[];
level1ApprovalRequired = false;
level2ApprovalRequired = false;
level1ApproverArr = string[];
level2ApproverArr = string[];
attrDict = dict("string");
//Notifiers
level1NotifierRequired = false;
level2NotifierRequired = false;
level1NotifierArr = string[];
level2NotifierArr = string[];
notificationReasonDescriptionArray = string[];
notifiersArray = string[];
userLoginArray = string[];

//if(action == "request approval" OR action == "calculate price"){
if(_system_current_step_var == "adjustPricing"){
	/* Check quote level Approval_Triggers */
	approvalTriggersRecordSet = bmql("SELECT Division, ReasonText, ManagerAndSupervisor, GeneralManager,Condition FROM Approval_Triggers WHERE Level = 'Quote' AND (Division = $division_quote OR Division = '0') ORDER BY Division DESC");
	put(attrDict, "grandTotalSell_quote",string(grandTotalSell));
	put(attrDict, "adHocFlag_quote",string(adHocFlag_quote));
	put(attrDict, "grandTotalBase_quote",string(grandTotalBase));
	put(attrDict, "dealValue",string(grandTotalSell));
	put(attrDict, "isFRFwaived_quote",string(isFRFwaived_quote));
	put(attrDict, "isERFwaived_quote",string(isERFwaived_quote));
	put(attrDict, "adHocTotal", string(adHocTotal_quote));
	//ERF, FRF for existing
	put(attrDict, "salesActivity_quote", salesActivity_quote);
	put(attrDict, "eRFCharged_quote", eRFCharged_quote);
	put(attrDict, "fRFCharged_quote", fRFCharged_quote);

	for eachRec in approvalTriggersRecordSet{
		if(util.eval(get(eachRec, "Condition"),attrDict) == "TRUE"){
			if(get(eachRec, "ManagerAndSupervisor") == "Approval"){
				if(findinarray(level1ApprovalReasonArr, get(eachRec, "ReasonText")) == -1){
					append(level1ApprovalReasonArr, get(eachRec, "ReasonText"));
					level1ApprovalRequired = true;
				}	
			}
			if(get(eachRec, "GeneralManager") == "Approval"){
				if(findinarray(level2ApprovalReasonArr, get(eachRec, "ReasonText")) == -1){
					append(level2ApprovalReasonArr, get(eachRec, "ReasonText"));
					level2ApprovalRequired = true;
				}
			}
			
			//Notifier
			if(get(eachRec, "ManagerAndSupervisor") == "Notify"){
				level1NotifierRequired = true;
				if(findinarray(notificationReasonDescriptionArray, get(eachRec, "ReasonText")) == -1){
					append(notificationReasonDescriptionArray, get(eachRec, "ReasonText"));
				}
			}
			if(get(eachRec, "GeneralManager") == "Notify"){
				level2NotifierRequired = true;
				if(findinarray(notificationReasonDescriptionArray, get(eachRec, "ReasonText")) == -1){
					append(notificationReasonDescriptionArray, get(eachRec, "ReasonText"));
				}
			}
		}
	}

	/* Check line level Approval_Triggers */
	approvalTriggersRecordSet = bmql("SELECT Division, ReasonText, ManagerAndSupervisor, GeneralManager,Condition FROM Approval_Triggers WHERE Level = 'Line' AND (Division = $division_quote OR Division = '0') ");
	
	

	for line in line_process{	
		if(line._parent_doc_number == "" AND line._part_number == ""){	// Model Line		
			compactorValue = getconfigattrvalue(line._document_number, "compactor");
			if(NOT isnull(compactorValue) AND compactorValue == "true"){
				hasCompactor = true;
			}
			
			containerType_l = getconfigattrvalue(line._document_number, "containerType_l");
			if(NOT isnull(containerType_l) AND (containerType_l == "Self-Contained Container" OR containerType_l== "Stationary Compactor")){
				hasCompactor = true;
			}
			put(attrDict, "hasCompactor",string(hasCompactor));
		}
		
		else{ // Part Line
			put(attrDict, "rateType_line", line.rateType_line);
			put(attrDict, "sellPrice_line",string(line.sellPrice_line));
			put(attrDict, "basePrice_line",string(line.basePrice_line));
		}
		for eachRec in approvalTriggersRecordSet{
			if(util.eval(get(eachRec, "Condition"),attrDict) == "TRUE"){
				if(get(eachRec, "ManagerAndSupervisor") == "Approval"){
					if(findinarray(level1ApprovalReasonArr, get(eachRec, "ReasonText")) == -1){
						append(level1ApprovalReasonArr, get(eachRec, "ReasonText"));
						level1ApprovalRequired = true;
					}	
				}
				if(get(eachRec, "GeneralManager") == "Approval"){
					if(findinarray(level2ApprovalReasonArr, get(eachRec, "ReasonText")) == -1){
						append(level2ApprovalReasonArr, get(eachRec, "ReasonText"));
						level2ApprovalRequired = true;
					}
				}
				//Notifier
				if(get(eachRec, "ManagerAndSupervisor") == "Notify"){
					level1NotifierRequired = true;
					if(findinarray(notificationReasonDescriptionArray, get(eachRec, "ReasonText")) == -1){
						append(notificationReasonDescriptionArray, get(eachRec, "ReasonText"));
					}
				}
				if(get(eachRec, "GeneralManager") == "Notify"){
					level2NotifierRequired = true;
					if(findinarray(notificationReasonDescriptionArray, get(eachRec, "ReasonText")) == -1){
						append(notificationReasonDescriptionArray, get(eachRec, "ReasonText"));
					}
				}
			}
		}
	}

	//Build HTML to display approval reasons on the quote
	approvalReasonHTML = util.setApprovalReasonDisplay(level1ApprovalReasonArr, level2ApprovalReasonArr);
	
	//J. Felberg 20150105
	if(grandTotalFloor > grandTotalSell){
		returnStr  = "1~approvalReasonDisplayWithColorTA_quote~" + util.setApprovalReasonDisplayWithColor(level1ApprovalReasonArr, level2ApprovalReasonArr, "red") + "|";
	}
	else{
		returnStr  = "1~approvalReasonDisplayWithColorTA_quote~" + util.setApprovalReasonDisplayWithColor(level1ApprovalReasonArr, level2ApprovalReasonArr, "black") + "|";
	}

	//build an array with all 3 possible "spellings" of the current login
	append(userLoginArray, lower(_system_user_login));
	append(userLoginArray, upper(_system_user_login));
	append(userLoginArray, _system_user_login);

        print "userLoginArray";
	print userLoginArray;

	/* Get the Hierarchy_Exception approvers for the current logged in user */
	hierarchyExceptionsRecordSet = bmql("SELECT Level_1_Approver, " +
                                            "       Level_2_Approver, " +
                                            "       Level_3_Approver  " +
                                            "  FROM Hierarchy_Exceptions " +
                                            " WHERE User_Login IN $userLoginArray ");

	for eachRec in hierarchyExceptionsRecordSet{ // fixed 2013-09-16 to use same logic as User_Hierarchy
		append(level1ApproverArr,get(eachRec, "Level_1_Approver") );
		append(level1ApproverArr,get(eachRec, "Level_2_Approver") );
		append(level2ApproverArr,get(eachRec, "Level_3_Approver") );
	}
	print "level1ApproverArr";
	print level1ApproverArr;

	print "level2ApproverArr";
	print level2ApproverArr;

	print "array sizes";
	print sizeofarray(level1ApproverArr);
	print sizeofarray(level2ApproverArr);

	if((sizeofarray(level1ApproverArr) == 0) AND (sizeofarray(level2ApproverArr) == 0)){
	    print "Checking User_Hierarchy as nothing was found in Hierarchy_Exceptions";
		//Get the list of approvers from User_Hierarchy if the table Hierarchy_Exceptions does not return any.
		userHierarchyRecordSet = bmql("SELECT Level_1_Approver, " +
                                              "       Level_2_Approver, " +
                                              "       Level_3_Approver, " +
                                              "       Level_1_Email,    " +
                                              "       Level_2_Email,    " +
                                              "       Level_3_Email     " +
                                              "  FROM User_Hierarchy " +
                                              " WHERE User_Login IN $userLoginArray "); // 2014-04-08 Removed  Home_Division_Nbr = $division_quote
		print "userHierarchyRecordSet";
		print userHierarchyRecordSet;

		for eachRec in userHierarchyRecordSet{
			append(level1ApproverArr,get(eachRec, "Level_1_Approver") );
			append(level1ApproverArr,get(eachRec, "Level_2_Approver") );
			append(level2ApproverArr,get(eachRec, "Level_3_Approver") );
			
			//Add notifiers to Notifiers array
			if(level1NotifierRequired){
				append(level1NotifierArr,get(eachRec, "Level_1_Email") );
				append(level1NotifierArr,get(eachRec, "Level_2_Email") );
			}
			if(level2NotifierRequired){
				append(level2NotifierArr,get(eachRec, "Level_3_Email") );
			}
			break;
		}
	}

	//Get the notifier (Non-Approver) logins from User Hierarchy table, if not already found
	/*
	if((level1NotifierRequired OR  level2NotifierRequired) AND (sizeofarray(level1NotifierArr) ==0 AND sizeofarray(level2NotifierArr) == 0)){
		//Get the list of approvers from User_Hierarchy
		userHierarchyRecordSet = bmql("SELECT Level_1_Approver,Level_2_Approver,Level_3_Approver FROM User_Hierarchy WHERE User_Login = $userLoginLower OR User_Login = $userLoginUpper OR User_Login = $_system_user_login AND Home_Division_Nbr = $division_quote");
		for eachRec in userHierarchyRecordSet{
			//Add approvers to Notifiers array
			if(level1NotifierRequired){
				append(level1NotifierArr,get(eachRec, "Level_1_Approver") );
			}
			if(level2NotifierRequired){
				append(level2NotifierArr,get(eachRec, "Level_2_Approver") );
			}
		}
	}
	*/

	//Generate email id's for Notifiers (Non-approvers)
	notifiersArray = string[];
	if((sizeofarray(level1NotifierArr) > 0 OR sizeofarray(level2NotifierArr) > 0) AND (level2NotifierRequired OR level1NotifierRequired)){
		for eachNotifier in level1NotifierArr{
			if(eachNotifier <> ""){
				loginEmail = eachNotifier; // full email address should be used in table + "@republicservices.com";
				if(findinarray(notifiersArray, loginEmail) == -1){
					append(notifiersArray, loginEmail);
				}
			}
		}
		
		for eachNotifier in level2NotifierArr{
			if(eachNotifier <> ""){
				//loginEmail = eachNotifier + "@republicservices.com";
				loginEmail = eachNotifier; // full email address should be used in table + "@gmail.com"; //for testing, enable previous line and disable this after testing 
				if(findinarray(notifiersArray, loginEmail) == -1){
					append(notifiersArray, loginEmail);
				}
			}
		}
	}
}
//=============================== END - APPROVALS ===============================//


/*==============Get closer customer locations=============*/ 
inputDict = dict("string");

put(inputDict, "division", division_quote);
put(inputDict, "containerGroup", containerDetailsString_quote);
//This is required to avoid users to manually refresh page to see the nearby locations first time when they enter adjust pricing step
if(containerDetailsString_quote == ""){
	put(inputDict, "containerGroup", firstModelDataStr);
}

put(inputDict, "lat1", _quote_process_siteAddress_quote_company_name);
put(inputDict, "lon1", _quote_process_siteAddress_quote_company_name_2);
put(inputDict, "range", radiusInMiles_quote);
nearbyServicesCustomerLocations = util.getProximityValuesForTheCustomer(inputDict);
/*==============Get closer customer locations================*/

//@DS: Updated to populate fields regardless of whether or not submited has been added to a user group
/*if(_system_user_groups <> ""){

	userGroupsArr = split(_system_user_groups, "+");
	for group in userGroupsArr{
		if(division_quote == substring(group, 1,4)){*/
		
//Populate the group names for Sales reps , Managers, Executive Managers
divisionSalesGroup = "d" + division_quote + "SalesReps";
divisionManagerGroup = "d" + division_quote + "Managers";
divisionExecManagerGroup = "d" + division_quote + "ExecManagers";
		/*}
	}
}*/

//If exchange is done with separate removal and delivery line items, add credit for full amount of removal
for each in hasDeliveryArr{
	if (containsKey(hasRemovalDict, each)){
		//oneTimeExchangeCredit = oneTimeExchangeCredit + get(hasRemovalDict, each);
		exchangeChargeSubtotal = exchangeChargeSubtotal + get(hasRemovalDict, each);
	}
}


//Write totals to quote attributes

returnStr = returnStr   + "1~" + "divisionSalesGroup_quote" + "~" + (divisionSalesGroup) + "|"
						+ "1~" + "divisionManagerGroup_quote" + "~" + (divisionManagerGroup) + "|"
						+ "1~" + "divisionExecManagerGroup_quote" + "~" + (divisionExecManagerGroup) + "|"	
						+ "1~" + "oneTimeTotalDeliveryAmount_quote" + "~" + string(oneTimeTotalDeliveryAmount) + "|"	
						+ "1~" + "oneTimeDeliveryCredit_quote" + "~" + string(oneTimeDeliveryCredit) + "|"
						+ "1~" + "numberOfTotalDeliveryContainers_quote" + "~" + string(numberOfTotalDeliveryContainers) + "|"
						+ "1~" + "totalOneTimeAmount_quote" + "~" + string(totalOneTimePrice)+"|"
						+ "1~" + "deliveryChargeSubtotal_quote" + "~" + string(deliveryChargeSubtotal) + "|"
						+ "1~" + "oneTimeExchangeCredit_quote" + "~" + string(oneTimeExchangeCredit) + "|"
						+ "1~" + "exchangeERFandFRFTotal_quote" + "~" + string(exchangeERFandFRFTotal)+"|"
						+ "1~" + "oneTimeERFandFRFTotal_quote" + "~" + string(oneTimeERFandFRFTotal)+"|"
						+ "1~" + "exchangeChargeSubtotal_quote" + "~" + string(exchangeChargeSubtotal) + "|"
						+ "1~" + "installationChargesSubTotal_quote" + "~" + string(installationChargeSubtotal) + "|"
						+ "1~" + "frfTotalSellTarget_quote" + "~" + string(frfTotalSellTarget) + "|"
						+ "1~" + "frfTotalSellFloor_quote" + "~" + string(frfTotalSellFloor) + "|"
						+ "1~" + "frfTotalSellBase_quote" + "~" + string(frfTotalSellBase) + "|"
						+ "1~" + "frfTotalSellStretch_quote" + "~" + string(frfTotalSellStretch) + "|"
						+ "1~" + "frfTotalSell_quote" + "~" + string(frfTotalSell) + "|"
						+ "1~" + "erfTotalSellFloor_quote" + "~" + string(erfTotalSellFloor) + "|"
						+ "1~" + "erfTotalSellBase_quote" + "~" + string(erfTotalSellBase) + "|"
						+ "1~" + "erfTotalSellTarget_quote" + "~" + string(erfTotalSellTarget) + "|"
						+ "1~" + "erfTotalSellStretch_quote" + "~" + string(erfTotalSellStretch) + "|"
						+ "1~" + "erfTotalSell_quote" + "~" + string(erfTotalSell) + "|"
						+ "1~" + "erfAndFrfTotalSell_quote" + "~" + string(erfAndFrfTotalSell) + "|"
						+ "1~" + "erfAndFrfTotalFloor_quote" + "~" + string(erfAndFrfTotalFloor) + "|"
						+ "1~" + "erfAndFrfTotalBase_quote" + "~" + string(erfAndFrfTotalBase) + "|"
						+ "1~" + "erfAndFrfTotalTarget_quote" + "~" + string(erfAndFrfTotalTarget) + "|"
						+ "1~" + "erfAndFrfTotalStretch_quote" + "~" + string(erfAndFrfTotalStretch) + "|"
						+ "1~" + "totalERFFRFAdminSellAmount_quote" + "~" + string(erfFrfAdminTotalSell) + "|"
						+ "1~" + "totalERFFRFAdminFloorAmount_quote" + "~" + string(erfFrfAdminTotalFloor) + "|"
						+ "1~" + "totalERFFRFAdminBaseAmount_quote" + "~" + string(erfFrfAdminTotalBase) + "|"
						+ "1~" + "totalERFFRFAdminTargetAmount_quote" + "~" + string(erfFrfAdminTotalTarget) + "|"
						+ "1~" + "totalERFFRFAdminStretchAmount_quote" + "~" + string(erfFrfAdminTotalStretch) + "|"
						+ "1~" + "deliveryERFandFRFTotal_quote" + "~" + string(deliveryERFandFRFTotal) + "|"
						+ "1~" + "grandTotalBase_quote" + "~" + string(grandTotalBase) + "|"
						+ "1~" + "grandTotalTarget_quote" + "~" + string(grandTotalTarget) + "|"
						+ "1~" + "grandTotalStretch_quote" + "~" + string(grandTotalStretch) + "|"
						+ "1~" + "grandTotalSell_quote" + "~" + string(grandTotalSell) + "|"
						+ "1~" + "grandTotalFloor_quote" + "~" + string(grandTotalFloor) + "|"
						+ "1~" + "totalCommissionFloor_quote" + "~" + string(totalCommissionFloor) + "|"
						+ "1~" + "totalCommissionTarget_quote" + "~" + string(totalCommissionTarget) + "|"
						+ "1~" + "totalCommissionStretch_quote" + "~" + string(totalCommissionStretch) + "|"
						+ "1~" + "totalCommissionSell_quote" + "~" + string(totalCommissionSell) + "|"
						+ "1~" + "totalCommissionBase_quote" + "~" + string(totalCommissionBase) + "|"
						+ "1~" + "priceBand_quote" + "~" + priceBand + "|"
						+ "1~" + "commissionRate_quote" + "~" + string(commissionRate) + "|"
						+ "1~" + "nearbyServicesCustomerLocations_quote" + "~" + nearbyServicesCustomerLocations + "|"
						+ "1~" + "largeMonthlyHaulPriceInclFees_quote" + "~" + string(largeMonthlyHaulPriceInclFees) + "|"
						+ "1~" + "largeMonthlyDisposalPriceInclFees_quote" + "~" + string(largeMonthlyDisposalPriceInclFees) + "|"
						+ "1~" + "largeMonthlyRentalPriceInclFees_quote" + "~" + string(largeMonthlyRentalPriceInclFees) + "|"
						+ "1~" + "largeMonthlyTotalPriceInclFees_quote" + "~" + string(largeMonthlyTotalPriceInclFees) + "|"
						+ "1~" + "smallMonthlyTotalPriceInclFees_quote" + "~" + string(smallMonthlyPriceIncludingFees) + "|"
						+ "1~" + "totalMonthlyAmtInclFees_quote" + "~" + string(totalMonthlyAmount) + "|"
						+ "1~" + "smallSolidWasteRevenue_quote" + "~" + string(smallMonthlyRevenue_SolidWaste) + "|"
						+ "1~" + "smallRecyclingRevenue_quote" + "~" + string(smallMonthlyRevenue_Recycling) + "|"
						+ "1~" + "largeSolidWasteRevenue_quote" + "~" + string(largeMonthlyRevenue_SolidWaste) + "|"
						+ "1~" + "largeRecyclingRevenue_quote" + "~" + string(largeMonthlyRevenue_Recycling) + "|"
						+ "1~" + "smallSolidWasteNetRevenue_quote" + "~" + string(smallMonthlyNetRevenue_SolidWaste) + "|"
						+ "1~" + "smallRecyclingNetRevenue_quote" + "~" + string(smallMonthlyNetRevenue_Recycling) + "|"
						+ "1~" + "largeSolidWasteNetRevenue_quote" + "~" + string(largeMonthlyNetRevenue_SolidWaste) + "|"
						+ "1~" + "largeRecyclingNetRevenue_quote" + "~" + string(largeMonthlyNetRevenue_Recycling) + "|";
if(_system_current_step_var == "adjustPricing"){					
	returnStr = returnStr   + "1~" + "level1ApprovalRequired_quote" + "~" + string(level1ApprovalRequired) + "|"
							+ "1~" + "level2ApprovalRequired_quote" + "~" + string(level2ApprovalRequired) + "|"
							+ "1~" + "level1ApprovalReason_quote" + "~" + join(level1ApprovalReasonArr, ",") + "|"
							+ "1~" + "level2ApprovalReason_quote" + "~" + join(level2ApprovalReasonArr, ",") + "|"
							+ "1~" + "approvalReasonDisplayText_quote" + "~" + approvalReasonHTML + "|"
							+ "1~" + "level1Approver_quote" + "~" + join(level1ApproverArr, ",") + "|"
							+ "1~" + "level2Approver_quote" + "~" + join(level2ApproverArr, ",") + "|"
							+ "1~" + "hasCompactor_quote" + "~" + string(hasCompactor) + "|"						
							+ "1~" + "emailAddressNotifiers_quote" + "~" + join(notifiersArray, ";") + "|"
							+ "1~" + "reasonDescription_quote" + "~" + join(notificationReasonDescriptionArray, ",") + "|";
}
return returnStr;
