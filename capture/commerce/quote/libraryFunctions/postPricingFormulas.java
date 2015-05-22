/*
=======================================================================================================================
Name:        postPricingFormulas
Author:   
Create date:  

Description: Runs code after Commerce formulas. Performs complex functionality not supported by
             formulas, but requiring formula values to run first. Currently used in the Save And
             Price action
        
Input:       division_quote:    String - Division of quote
             region_quote:      String - Region of quote
             _part_number:      String - Part number for this line
             _config_attr_info: String - attributes from Config for use by getconfigattrvalue
                    
Output:      String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:     11/21/13 - Zach Schlieder - Removed Sell Price calculations (moved to Formula Management)
                                         Removed building of description_line (moved to Formula Management)
             11/22/13 - Zach Schlieder - Added Commission Rate calculations from Pre-Pricing formulas
             11/26/13 - Latha - Added code for approvals
             01/30/14 - Srikar - Formulas -> BML conversion
             02/11/14 - Zach - Added additional Delivery line functionality for Doc Engine
             ...other changes were made that were not documented between these entries
             08/18/14 - ??? (Oracle) - 3-9457966641 Delivery price updates
             09/09/14 - John (Republic) - Fixed issue where ERF was being set to 0.00 if FRF was unchecked.
             09/15/14 - Blabes (Republic) - Fixed Hierarchy_Exceptions processing to match that used by User_Hierarchy
             09/24/14 - John (Republic) - Added AdHoc line items to grandTotalSell_quote and totalOneTimeAmount_quote
             09/25/14 - Julie (Oracle) - Added logic to calculate the total number of containers that have a 1 time delivery fee
             11/11/14 - Aaron Q (Oracle) - Added logic to manage container removal/delivery in place of exchange code, including credit for removal
             11/18/14 - Aaron Q (Oracle) - Added exchange charge value from removal in place of exchange codes.
             01/06/15 - John (Republic) - #207 Updated ERF logic to use eRFOnFRF_quote to apply or not apply ERF on FRF.
             01/07/15 - John (Republic) - #321 Replaced calls to the util.eval function with util.evaluate
             01/10/15 - Mike (Republic) - #247 Zero prices should not affect the guard rails.  If zero rates are present, they should go through an approver
             01/15/15 - Julie (Oracle) - #68 Added logic for approvalReasonDisplayWithColorTA
             01/21/15 - John (Republic) - #233 Fix issue where ERF/FRF approval was being requested when the fees were already waived
             01/21/15 - Gaurav (Republic) - #322 making delivery and removal "Per Service" compared to "One time"
             01/26/15 - John (Republic) - #316 code cleanup during analysis of monthly totals issue
             02/03/15 - Julie (Oracle) - #68 Set the Model Description
             02/05/15 - Julie (Oracle) - #68 truncated the Model Description
             02/10/15 - John (Republic) - #68 remove all references to approvalReasonDisplayWithColorTA as it is handled by approvalReasonDisplay
                                          Moved all rate restriction logic from Printing action to postPricing formulas since it's needed for approvals.
                                          Fix incorrect disposal site being displayed.
             03/27/15 - Mike (Republic) - #145 Small Container Compactor - split small containers into sets of Base and Compactor Rental.
             04/30/15 - Mike (Republic) - #508 Restructuring totals by omitting adhoc fees.  Fixed several existing bugs.  Added variables 
	                                  for adhoc fees on CSA and Proposal.
			05/08/15 - Mike (Republic) - #508 Restructuring totals for multiple container groups of additional items.
			05/14/15 - 	Aaron Q ( Oracle) - Changed disposal fee calculations to account for unit of measure

             

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
smallMonthlyBaseIncludingFees = 0.0; //Both Solid waste and recycling combined - small
smallMonthlyRentalIncludingFees = 0.0; //Both Solid waste and recycling combined - small
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
adHocMonthlyTotalSell   = 0.0;
adHocPerHaulTotalSell   = 0.0;
adHocOneTimeTotalSell   = 0.0;
adHocOneTimeERFandFRF   = 0.0;
adHocMonthlyERFandFRF   = 0.0;
adHocPerHaulERFandFRF   = 0.0;
grandTotalInclAdHoc     = 0.0;
erfAndFrfTotalInclAdHoc = 0.0;

testcount = 0;
adminRateAlreadyAppliedOnLine = false;
docNumOfLineAdminRateApplied = "";

//Default attributes for setting the model description
ModelDescArray = string[];
ModelDescString = "";
ModelSiteString = "";
ModelSiteArray = string[];
ModelDescDict = dict("string");

//=============================== END - Variable Initialization ===============================//
print "Erf Rate Quote " + string(erfRate_quote);
print "FRF Rate Quote " + string(frfRate_quote);

for line in line_process{
    if(line._parent_doc_number <> ""){ //Only part line items
	
		modelDescString = "";
		//Check if it is a large container
		if(line.billingType_line == "Per Haul"){
			append(ModelDescArray, line._parent_doc_number);
			ModelSiteString = getconfigattrvalue(line._parent_doc_number, "site_disposalSite");
			ModelSiteArray = split(ModelSiteString, "$,$");
            siteIndex = getconfigattrvalue(line._parent_doc_number, "alternateSite_l");
            if(isnumber(siteIndex) AND (atoi(siteIndex) > 0)){
                ModelDescString = "Disposal Site: " + ModelSiteArray[atoi(siteIndex) - 1] + ", Time: " + getconfigattrvalue(line._parent_doc_number, "adjustedTotalTime_l") + " min";
                put(ModelDescDict, line._parent_doc_number, ModelDescString);
            }
            else{
                put(ModelDescDict, line._parent_doc_number, "");
            }
		}
			
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

        //ERF Fees per line item
        if(isFRFWaived_quote == 1){ //If FRF Waived

            if(includeAdmin_quote == "No" OR adminRateAlreadyAppliedOnLine){
                erfAmountSell = line.sellPrice_line * erfRate_quote;    
                erfAmountTarget = line.targetPrice_line *  erfRate_quote;
                erfAmountFloor = line.floorPrice_line * erfRate_quote;
                erfAmountBase = line.basePrice_line * erfRate_quote;
                erfAmountStretch = line.stretchPrice_line * erfRate_quote;
            }
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

            if(includeAdmin_quote == "No" OR adminRateAlreadyAppliedOnLine){
                erfAmountSell = (line.sellPrice_line * (1 + (frfRate_quote * eRFOnFRF_quote))) * erfRate_quote;
                erfAmountTarget = (line.targetPrice_line * (1 + (frfRate_quote * eRFOnFRF_quote))) * erfRate_quote;
                erfAmountFloor = (line.floorPrice_line * (1 + (frfRate_quote * eRFOnFRF_quote))) * erfRate_quote;
                erfAmountBase = (line.basePrice_line * (1 + (frfRate_quote * eRFOnFRF_quote))) * erfRate_quote;
                erfAmountStretch = (line.stretchPrice_line * (1 + (frfRate_quote * eRFOnFRF_quote))) * erfRate_quote;
            }
            else{
                erfAmountSell = (line.sellPrice_line + adminRate_quote) * (1 + (frfRate_quote * eRFOnFRF_quote)) * erfRate_quote;
                erfAmountTarget = (line.targetPrice_line + adminRate_quote) *  (1 + (frfRate_quote * eRFOnFRF_quote)) * erfRate_quote;
                erfAmountFloor = (line.floorPrice_line + adminRate_quote) * (1 + (frfRate_quote * eRFOnFRF_quote)) * erfRate_quote;
                erfAmountBase = (line.basePrice_line + adminRate_quote) * (1 + (frfRate_quote * eRFOnFRF_quote)) * erfRate_quote;
                erfAmountStretch = (line.stretchPrice_line + adminRate_quote) * (1 + (frfRate_quote * eRFOnFRF_quote)) * erfRate_quote;
                docNumOfLineAdminRateApplied = line._document_number;
                adminRateAlreadyAppliedOnLine = true;
            }

        }
        
        //FRF Fees per line item
        if(includeAdmin_quote == "No" OR (adminRateAlreadyAppliedOnLine AND docNumOfLineAdminRateApplied <> line._document_number)){
            frfAmountTarget = line.targetPrice_line * frfRate_quote;
            frfAmountFloor = line.floorPrice_line * frfRate_quote;
            frfAmountSell = line.sellPrice_line * frfRate_quote;
            frfAmountBase = line.basePrice_line * frfRate_quote;
            frfAmountStretch = line.stretchPrice_line * frfRate_quote;
        }
        else{
            frfAmountTarget = (line.targetPrice_line + adminRate_quote) * frfRate_quote;
            frfAmountFloor = (line.floorPrice_line + adminRate_quote)* frfRate_quote;
            frfAmountSell = (line.sellPrice_line + adminRate_quote)* frfRate_quote;
            frfAmountBase = (line.basePrice_line + adminRate_quote)* frfRate_quote;
            frfAmountStretch = (line.stretchPrice_line + adminRate_quote)* frfRate_quote;
            docNumOfLineAdminRateApplied = line._document_number;
            adminRateAlreadyAppliedOnLine = true;
        }
        
        
        //Calculate Monthly Total ERF Fees - Calculate these only for Core/ Base line item
        if(line.rateType_line == "Base" AND line.isPartLineItem_line){ //if  the line item is smallcontainer base or largecontainer rental, the fee values on line item grid can be directly used as there is no multiplication factor of estimatedLifts/esthaulspermonth or disposal tons per haul that need to be applied on these line items

            if(isERFWaived_quote == 0){
                erfTotalSellFloor = erfTotalSellFloor + erfAmountFloor;
                erfTotalSellBase = erfTotalSellBase + erfAmountBase;
                erfTotalSellTarget = erfTotalSellTarget + erfAmountTarget;
                erfTotalSellStretch = erfTotalSellStretch + erfAmountStretch;
                erfTotalSell = erfTotalSell + erfAmountSell;
            }
            
            if(isFRFWaived_quote == 0){
                frfTotalSellTarget = frfTotalSellTarget + frfAmountTarget;
                frfTotalSellFloor = frfTotalSellFloor + frfAmountFloor;
                frfTotalSellBase = frfTotalSellBase + frfAmountBase;
                frfTotalSellStretch = frfTotalSellStretch + frfAmountStretch;
                frfTotalSell = frfTotalSell + frfAmountSell;
            }

	}elif(line.rateType_line == "Compactor Rental" AND line.isPartLineItem_line){ 

            if(isERFWaived_quote == 0){
                erfTotalSellFloor = erfTotalSellFloor + erfAmountFloor;
                erfTotalSellBase = erfTotalSellBase + erfAmountBase;
                erfTotalSellTarget = erfTotalSellTarget + erfAmountTarget;
                erfTotalSellStretch = erfTotalSellStretch + erfAmountStretch;
                erfTotalSell = erfTotalSell + erfAmountSell;
            }
            
            if(isFRFWaived_quote == 0){
                frfTotalSellTarget = frfTotalSellTarget + frfAmountTarget;
                frfTotalSellFloor = frfTotalSellFloor + frfAmountFloor;
                frfTotalSellBase = frfTotalSellBase + frfAmountBase;
                frfTotalSellStretch = frfTotalSellStretch + frfAmountStretch;
                frfTotalSell = frfTotalSell + frfAmountSell;
            }

        }elif(line.rateType_line == "Haul" AND line.isPartLineItem_line){

            if(isERFWaived_quote == 0){
                erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * estLiftsPerMonth);
                erfTotalSellBase = erfTotalSellBase + (erfAmountBase * estLiftsPerMonth);
                erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * estLiftsPerMonth);
                erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * estLiftsPerMonth);
                erfTotalSell = erfTotalSell + (erfAmountSell * estLiftsPerMonth);
            }
            
            if(isFRFWaived_quote == 0){
                frfTotalSellTarget = frfTotalSellTarget + (frfAmountTarget * estLiftsPerMonth);
                frfTotalSellFloor = frfTotalSellFloor + (frfAmountFloor * estLiftsPerMonth);
                frfTotalSellBase = frfTotalSellBase + (frfAmountBase * estLiftsPerMonth);
                frfTotalSellStretch = frfTotalSellStretch + (frfAmountStretch * estLiftsPerMonth);
                frfTotalSell = frfTotalSell + (frfAmountSell * estLiftsPerMonth);
            }

        }elif(line.rateType_line == "Disposal" AND line.isPartLineItem_line){

            // Disposal includes rate factor to account for different units of measure
			rateFactor = 0.0;
			containerSize = "";
			containerSizeFloat = 0.0; print line.billingType_line;
			if(line.billingType_line == "Per Ton"){
				rateFactor = estLiftsPerMonth * estTonsPerHaul;
			}elif(line.billingType_line == "Per Load"){
				rateFactor = estLiftsPerMonth;
			}elif(line.billingType_line == "Per Yard"){
				if(NOT isnull(getconfigattrvalue(line._parent_doc_number, "equipmentSize_l"))){
                        containerSize=getconfigattrvalue(line._parent_doc_number, "equipmentSize_l");
				}
				if(isnumber(containerSize)){
					containerSizeFloat = atof(containerSize);
				}
				rateFactor = containerSizeFloat * estLiftsPerMonth;
				
			} print "rateFactor " + string(rateFactor);
			
            if(isERFWaived_quote == 0){
                erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * rateFactor);
                erfTotalSellBase = erfTotalSellBase + (erfAmountBase * rateFactor);
                erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * rateFactor);
                erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * rateFactor);
                erfTotalSell = erfTotalSell + (erfAmountSell * rateFactor);
            }
            
            if(isFRFWaived_quote == 0){
                frfTotalSellTarget = frfTotalSellTarget + (frfAmountTarget * rateFactor);
                frfTotalSellFloor = frfTotalSellFloor + (frfAmountFloor * rateFactor);
                frfTotalSellBase = frfTotalSellBase + (frfAmountBase * rateFactor);
                frfTotalSellStretch = frfTotalSellStretch + (frfAmountStretch * rateFactor);
                frfTotalSell = frfTotalSell + (frfAmountSell * rateFactor);
            }
			print "ERF Total Disp " + string(erfTotalSell) + " ERF Amount: " + string(erfAmountSell); print "FRF Total Disp " + string(frfTotalSell) + " FRF Amount: " + string(frfAmountSell);

        }elif(line.rateType_line == "Rental" AND line.isPartLineItem_line){ 

            //Monthly Rental is the default. Daily Rental should be multiplied by factor 365/12.
            if(isERFWaived_quote == 0){
                if(line.billingType_line == "Monthly"){
                    erfTotalSellFloor = erfTotalSellFloor + erfAmountFloor;
                    erfTotalSellBase = erfTotalSellBase + erfAmountBase;
                    erfTotalSellTarget = erfTotalSellTarget + erfAmountTarget;
                    erfTotalSellStretch = erfTotalSellStretch + erfAmountStretch;
                    erfTotalSell = erfTotalSell + erfAmountSell;
                }elif(line.billingType_line == "Daily"){
                    erfTotalSellFloor = erfTotalSellFloor + (erfAmountFloor * 365/12);
                    erfTotalSellBase = erfTotalSellBase + (erfAmountBase * 365/12);
                    erfTotalSellTarget = erfTotalSellTarget + (erfAmountTarget * 365/12);
                    erfTotalSellStretch = erfTotalSellStretch + (erfAmountStretch * 365/12);
                    erfTotalSell = erfTotalSell + (erfAmountSell * 365/12);
                }   
            }
            
            if(isFRFWaived_quote == 0){
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
            } 			print "ERF Total Rent " + string(erfTotalSell); print "FRF Total Rent " + string(frfTotalSell);

        }
        
        //Calculate Total Price of the line item; Price + FRF + ERF
        FRF_CONST = frfAmountSell;
        ERF_CONST = erfAmountSell;
        
        if(isFRFwaived_quote == 1){
            FRF_CONST = 0;
        }
        if(isERFwaived_quote == 1){
            ERF_CONST = 0;
        }
        totalPrice = line.sellPrice_line + FRF_CONST + ERF_CONST;
        

        //-------------------------------------------------------------------------------------
        // Service Revenue Calculations
        //
        // Note: these are one-time charges, so they are not added to the monthly total tables.
        //-------------------------------------------------------------------------------------

        //Delivery
        if(line.rateType_line == "Delivery"){

            //If deliveryPrice is increased, that should become the new deliverysubtotal, 
            //however, if it is decreased, the default value will be subtotal
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
            deliveryERFandFRFTotal = deliveryERFandFRFTotal + (FRF_CONST + ERF_CONST) * line._price_quantity;
            totalOneTimePrice = totalOneTimePrice + totalPrice * line._price_quantity;
            append(hasDeliveryArr, line._parent_doc_number);
        }
        
        //Exchange / Removal
        if(line.rateType_line == "Exchange"){
            if(line.totalTargetPrice_line > line.sellPrice_line){
                exchangeChargeSubtotal = exchangeChargeSubtotal + line.totalTargetPrice_line ;
            }else{
                exchangeChargeSubtotal = exchangeChargeSubtotal + line.sellPrice_line;
            }   
            oneTimeExchangeCredit = oneTimeExchangeCredit + (line.totalTargetPrice_line - line.sellPrice_line); //Credit is Sell/Proposed Price - Target Price
            exchangeERFandFRFTotal = (exchangeERFandFRFTotal + FRF_CONST + ERF_CONST) * line._price_quantity;
            totalOneTimePrice = totalOneTimePrice + totalPrice * line._price_quantity;
        }
        elif(line.rateType_line == "Removal"){
            exchangeERFandFRFTotal = (exchangeERFandFRFTotal + FRF_CONST + ERF_CONST) * line._price_quantity;
            totalOneTimePrice = totalOneTimePrice + totalPrice * line._price_quantity;
            put(hasRemovalDict, line._parent_doc_number, line.sellPrice_line);
        }
        
        //Installation
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
        if(line._parent_doc_number == "" AND line._part_number == ""){  // Model Line       
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
            smallMonthlyBaseIncludingFees = smallMonthlyBaseIncludingFees + totalPrice;
            if(lower(line.wasteCategory_line) == "solid waste"){
                smallMonthlyRevenue_SolidWaste = smallMonthlyRevenue_SolidWaste + totalPrice; //totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
            }elif(lower(line.wasteCategory_line) == "recycling"){
                smallMonthlyRevenue_Recycling = smallMonthlyRevenue_Recycling + totalPrice;//totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
            }
        }
        elif(line.rateType_line == "Compactor Rental" AND line.isPartLineItem_line){
            smallMonthlyPriceIncludingFees = smallMonthlyPriceIncludingFees + totalPrice;
            smallMonthlyRentalIncludingFees = smallMonthlyRentalIncludingFees + totalPrice;
            if(lower(line.wasteCategory_line) == "solid waste"){
                smallMonthlyRevenue_SolidWaste = smallMonthlyRevenue_SolidWaste + totalPrice; //totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
            }elif(lower(line.wasteCategory_line) == "recycling"){
                smallMonthlyRevenue_Recycling = smallMonthlyRevenue_Recycling + totalPrice;//totalPrice includes fee except adminRate, but admin should be added only once at quote level, hence only shown in totalRevenue but not smallContainerRevenue
            }
        }

        // 20140924 - Include amounts for adHoc items on grandTotalSell_quote and totalOneTimeAmount_quote
        if(line.rateType_line == "Ad-Hoc") {

            billingMethod = line.frequency_line;
            showOnProposal = line.adHocDisplayOnProposal_line;

			//MPB
	    print "Show On Proposal";
	    print showOnProposal;

            if(showOnProposal == true) {
                if(billingMethod == "Monthly") { 
                    adHocMonthlyERFandFRF = adHocMonthlyERFandFRF + FRF_CONST + ERF_CONST;
                    adHocMonthlyTotalSell = adHocMonthlyTotalSell + line.sellPrice_line + FRF_CONST + ERF_CONST;
                }
                if(billingMethod == "Per Haul") { 
                    adHocMonthlyERFandFRF = adHocMonthlyERFandFRF + FRF_CONST + ERF_CONST;
                    adHocPerHaulTotalSell = adHocPerHaulTotalSell + line.sellPrice_line + FRF_CONST + ERF_CONST;  
                }
                if(billingMethod == "One Time") { 
                    adHocOneTimeERFandFRF = adHocOneTimeERFandFRF + FRF_CONST + ERF_CONST;//- added 20150119 - GD - #322 - making delivery and removal "Per Service compared to "One time"
                    adHocOneTimeTotalSell = adHocOneTimeTotalSell + line.sellPrice_line + FRF_CONST + ERF_CONST;//- updated 20150119 - GD - #322 - making delivery and removal "Per Service compared to "One time"
                }
		//MPB
		print "Billing Method";
		print billingMethod;
		print "Monthly ERF and FRF";
		print adHocMonthlyERFandFRF;
            }
        }
    }
}

totalOneTimePrice = totalOneTimePrice + adHocOneTimeTotalSell;

smallMonthlyNetRevenue_SolidWaste = smallMonthlyRevenue_SolidWaste - smallSolidWasteDisposalExpense_quote;
smallMonthlyNetRevenue_Recycling = smallMonthlyRevenue_Recycling - smallRecyclingDisposalExpense_quote;

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
oneTimeERFandFRFTotal = deliveryERFandFRFTotal + exchangeERFandFRFTotal + installationERFandFRFTotal + adHocOneTimeERFandFRF;//- added 20150119 - GD - #322 - making delivery and removal "Per Service compared to "One time"

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
    
    //Guard rails need to be present even for a zero total.  I left the condition in to account for negative rates (should never happen) - MPB
        if(smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote >= 0){
        grandTotalFloor = smallMonthlyTotalFloor_quote + largeMonthlyTotalFloor_quote + erfTotalSellFloor + frfTotalSellFloor + adminRate_quote;
        grandTotalBase = smallMonthlyTotalBase_quote + largeMonthlyTotalBase_quote + erfTotalSellBase + frfTotalSellBase + adminRate_quote;
        grandTotalTarget = smallMonthlyTotalTarget_quote + largeMonthlyTotalTarget_quote + erfTotalSellTarget + frfTotalSellTarget + adminRate_quote;
        grandTotalStretch = smallMonthlyTotalStretch_quote + largeMonthlyTotalStretch_quote + erfTotalSellStretch + frfTotalSellStretch + adminRate_quote;
        grandTotalSell = smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote + erfTotalSell + frfTotalSell + adminRate_quote;
    }
    else {
        returnStr = returnStr   + "1~" + "adminRate_quote" + "~" + "0.0" + "|";
    }
    
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
    
    //Create reporting values for the Proposal
    grandTotalInclAdHoc = grandTotalSell + adHocMonthlyTotalSell + adHocPerHaulTotalSell;
    erfAndFrfTotalInclAdHoc = erfAndFrfTotalSell + adHocMonthlyERFandFRF;
    
    //MPB
    print "ERF and FRF Total Sell";
    print erfAndFrfTotalSell;
    print "Ad Hoc Monthly ERF and FRF";
    print adHocMonthlyERFandFRF;
    print "ERF and FRF Including Ad Hoc";
    print erfAndFrfTotalInclAdHoc;

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

if(_system_current_step_var == "adjustPricing"){

    // check for a change from ERF/FRF charged to not-charged for an approval to be required
    fRFApprovalRequired = 0;
    if((isFRFwaived_quote == 1) and (fRF_readOnly_quote == "Yes")){
        fRFApprovalRequired = 1;
    }

    eRFApprovalRequired = 0;
    if((isERFwaived_quote == 1) and (eRFreadOnly_quote == "Yes")){
        eRFApprovalRequired = 1;
    }

    /* Check quote level Approval_Triggers */
    approvalTriggersRecordSet = bmql("SELECT Division, ReasonText, ManagerAndSupervisor, GeneralManager,Condition FROM Approval_Triggers WHERE Level = 'Quote' AND (Division = $division_quote OR Division = '0') ORDER BY Division DESC");
    put(attrDict, "grandTotalSell_quote",string(grandTotalSell));
    put(attrDict, "adHocFlag_quote",string(adHocFlag_quote));
    put(attrDict, "grandTotalBase_quote",string(grandTotalBase));
    put(attrDict, "dealValue",string(grandTotalSell));
    put(attrDict, "isFRFwaived_quote",string(fRFApprovalRequired));
    put(attrDict, "isERFwaived_quote",string(eRFApprovalRequired));
    put(attrDict, "adHocTotal", string(adHocTotal_quote));
    put(attrDict, "salesActivity_quote", salesActivity_quote);
    put(attrDict, "eRFCharged_quote", eRFCharged_quote);
    put(attrDict, "fRFCharged_quote", fRFCharged_quote);


    for eachRec in approvalTriggersRecordSet{
        if(util.evaluate(get(eachRec, "Condition"),attrDict) == "TRUE"){
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
        if(line._parent_doc_number == "" AND line._part_number == ""){  // Model Line       
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
            if(util.evaluate(get(eachRec, "Condition"),attrDict) == "TRUE"){
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

    print "level1ApproverArr";
    print level1ApproverArr;

    print "level2ApproverArr";
    print level2ApproverArr;

    print "level1NotifierArr";
    print level1NotifierArr;

    print "level2NotifierArr";
    print level2NotifierArr;

    //Get the notifier (Non-Approver) logins from User Hierarchy table, if not already found
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

//Populate the group names for Sales reps , Managers, Executive Managers
divisionSalesGroup = "d" + division_quote + "SalesReps";
divisionManagerGroup = "d" + division_quote + "Managers";
divisionExecManagerGroup = "d" + division_quote + "ExecManagers";

//If exchange is done with separate removal and delivery line items, add credit for full amount of removal
for each in hasDeliveryArr{
    if (containsKey(hasRemovalDict, each)){
        //oneTimeExchangeCredit = oneTimeExchangeCredit + get(hasRemovalDict, each);
        exchangeChargeSubtotal = exchangeChargeSubtotal + get(hasRemovalDict, each);
    }
}


// Set rate restriction attributes used by approval email and doc engine
returnStr = returnStr + commerce.setRateRestrictions();

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
                        + "1~" + "smallMonthlyBasePriceInclFees_quote" + "~" + string(smallMonthlyBaseIncludingFees) + "|"
                        + "1~" + "smallMonthlyRentalPriceInclFees_quote" + "~" + string(smallMonthlyRentalIncludingFees) + "|"
                        + "1~" + "smallMonthlyTotalPriceInclFees_quote" + "~" + string(smallMonthlyPriceIncludingFees) + "|"
                        + "1~" + "totalMonthlyAmtInclFees_quote" + "~" + string(totalMonthlyAmount) + "|"
                        + "1~" + "smallSolidWasteRevenue_quote" + "~" + string(smallMonthlyRevenue_SolidWaste) + "|"
                        + "1~" + "smallRecyclingRevenue_quote" + "~" + string(smallMonthlyRevenue_Recycling) + "|"
                        + "1~" + "largeSolidWasteRevenue_quote" + "~" + string(largeMonthlyRevenue_SolidWaste) + "|"
                        + "1~" + "largeRecyclingRevenue_quote" + "~" + string(largeMonthlyRevenue_Recycling) + "|"
                        + "1~" + "smallSolidWasteNetRevenue_quote" + "~" + string(smallMonthlyNetRevenue_SolidWaste) + "|"
                        + "1~" + "smallRecyclingNetRevenue_quote" + "~" + string(smallMonthlyNetRevenue_Recycling) + "|"
                        + "1~" + "largeSolidWasteNetRevenue_quote" + "~" + string(largeMonthlyNetRevenue_SolidWaste) + "|"
                        + "1~" + "largeRecyclingNetRevenue_quote" + "~" + string(largeMonthlyNetRevenue_Recycling) + "|"
                        + "1~" + "grandTotalInclAdHoc_quote" + "~" + string(grandTotalInclAdHoc) + "|"
                        + "1~" + "erfAndFrfTotalInclAdHoc_quote" + "~" + string(erfAndFrfTotalInclAdHoc) + "|";
if(_system_current_step_var == "adjustPricing"){                    
    returnStr = returnStr   + "1~" + "level1ApprovalRequired_quote" + "~" + string(level1ApprovalRequired) + "|"
                            + "1~" + "level2ApprovalRequired_quote" + "~" + string(level2ApprovalRequired) + "|"
                            + "1~" + "level1ApprovalReason_quote" + "~" + join(level1ApprovalReasonArr, ";") + "|"
                            + "1~" + "level2ApprovalReason_quote" + "~" + join(level2ApprovalReasonArr, ";") + "|"
                            + "1~" + "approvalReasonDisplayText_quote" + "~" + approvalReasonHTML + "|"
                            + "1~" + "level1Approver_quote" + "~" + join(level1ApproverArr, ",") + "|"
                            + "1~" + "level2Approver_quote" + "~" + join(level2ApproverArr, ",") + "|"
                            + "1~" + "hasCompactor_quote" + "~" + string(hasCompactor) + "|"                        
                            + "1~" + "emailAddressNotifiers_quote" + "~" + join(notifiersArray, ";") + "|"
                            + "1~" + "reasonDescription_quote" + "~" + join(notificationReasonDescriptionArray, ";") + "|";
}

//==============================Start - Model Description ========================================//
for eachModelDesc in ModelDescArray{
	returnStr = returnStr + eachModelDesc + "~" + "description_line" + "~" + get(ModelDescDict, eachModelDesc) + "|";
}
//==============================End - Model Description ==========================================//
return returnStr;
