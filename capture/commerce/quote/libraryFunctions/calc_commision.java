/* ================================================================================
       Name: calc_commission
     Author: Blake Henderson (Oracle)
Create date: December 2014
Description: Used by Compensation feature to output comp info on Pricing page.
        
Input:      feesToCharge_quote:
            customerRateRestriction_quote:
            division_RO_quote:
            initialTerm_quote:
            industry_readOnly_quote:
            segment_readOnly_quote:
            smallMonthlyTotalFloor_quote:
            smallMonthlyTotalBase_quote:
            smallMonthlyTotalTarget_quote:
            smallMonthlyTotalStretch_quote:
            smallMonthlyTotalProposed_quote:
            largeMonthlyTotalFloor_quote:
            largeMonthlyTotalBase_quote:
            largeMonthlyTotalTarget_quote:
            largeMonthlyTotalStretch_quote:
            largeMonthlyTotalProposed_quote:
            area_quote:
            compOwnerLogin_quote:
                    
Output:     String (documentNumber + "~" + attributeVariableName + "~" + value + "|")

Updates:    20141229 - John Palubinskas - initialize large cont dictionaries to prevent RTE when doing a
                                          get on a dictionary that doesn't contain the key.  Looping fix.
	    20150109 - Gaurav Dawar - Line 785, added this piece to hide the commission for temporary accounts
		20150115 - Aaron Quintanilla - Changed small container calculations starting on line 121 to include fees with delivery to more accurately calculate commision.  Moved fees to about other line calculations, added fees to delivery values, removed 'addErfFrf' variable.
		20150117 - Aaron Quintanilla - Changed tier commission calculations to check for 0 in denominators. 
		20150122 - #361 - Gaurav Dawar - Added Functionality to hide Comp for Change of owner and Existing Customer Quotes(Except New Site).
<<<<<<< HEAD
		20150130 - Aaron Quintanilla - Fixed small container one time commission, small container display percentage, and small container displayed value, and corrected the totalling of all commission.
		20150202 - seperated AND and OR for correct logical iterations for correct calculation of comp on fees.
=======
>>>>>>> origin/develop



================================================================================ */



//CREATE MODEL DICTIONARY WHICH STORES CHILDREN DOCUMENT NUMBERS 
showERF = false;
showFRF = false; 
if(find(feesToCharge_quote, "ERF") <> -1){
	showERF = true;
}
if(find(feesToCharge_quote, "FRF") <> -1){
	showFRF = true;
}
showSmall = false;
showLarge = false;
ret = "";

upperCreator = upper(compOwnerLogin_quote);
lowerCreator = lower(compOwnerLogin_quote);
creatorCode = "";
creatorJobCode = bmql("SELECT Job_Code FROM User_Hierarchy WHERE User_Login = $compOwnerLogin_quote OR User_Login = $upperCreator OR User_Login = $lowerCreator");
for each in creatorJobCode{
	creatorCode = get(each, "Job_Code");
	break;
}
if(creatorCode == ""){
	creatorExceptionJobCode = bmql("SELECT Job_Code FROM Hierarchy_Exceptions WHERE User_Login = $compOwnerLogin_quote OR User_Login = $upperCreator OR User_Login = $lowerCreator");
	for each in creatorExceptionJobCode{
		creatorCode = get(each, "Job_Code");
		break;
	}
}

//IDENTIFIER DICTIONARIES
tempModelDict = dict("string");
modelDict = dict("string");
modelCategory = dict("string");
tempQtyDict = dict("integer");
//DICTIONARIES USED FOR FINAL CALCULATIONS
modelAdderDict = dict("float");
modelDeliveryComm = dict("float");
modelWasteCategory = dict("float");
//DICTIONARIES FOR GUARDRAIL VALUES
floorComBasePct = dict("float");
baseComBasePct = dict("float");
targetComBasePct = dict("float");
stretchComBasePct = dict("float");
proposedComBasePct = dict("float"); //RENAMED BE SURE TO REMOVE modelComBasePct SWAP TO THIS
//NEW DICTIONARIES NECESSARY
estTons = dict("float");
estHaulsPerMonth = dict("float");
//RENTAL
floorRental = dict("float");
baseRental = dict("float");
targetRental = dict("float");
stretchRental = dict("float");
proposedRental = dict("float");
//DISPOSAL
floorDisposal = dict("float");
baseDisposal = dict("float");
targetDisposal = dict("float");
stretchDisposal = dict("float");
proposedDisposal = dict("float");
//HAUL
floorHaul = dict("float");
baseHaul = dict("float");
targetHaul = dict("float");
stretchHaul = dict("float");
proposedHaul = dict("float");
//DELIVERY
floorDelivery = dict("float");
baseDelivery = dict("float");
targetDelivery = dict("float");
stretchDelivery = dict("float");
proposedDelivery = dict("float");
//PRICES
floorModelPrice = dict("float");
baseModelPrice = dict("float");
targetModelPrice = dict("float");
stretchModelPrice = dict("float");
proposedModelPrice = dict("float");
//FrfErf
floorFrfErf = dict("float");
baseFrfErf = dict("float");
targetFrfErf = dict("float");
stretchFrfErf = dict("float");
proposedFrfErf = dict("float");

modelName = "";
for line in line_process{
	if(line._parent_doc_number <> ""){
		addFrfErf = false; 
		if(NOT containskey(tempQtyDict,line._parent_doc_number)){ //AQ 01292015
			put(tempQtyDict,line._parent_doc_number,line._price_quantity); 
		}
		if(modelName == "Containers"){ 
			put(floorDelivery,line._parent_doc_number,0.0);
			put(baseDelivery,line._parent_doc_number,0.0);
			put(targetDelivery,line._parent_doc_number,0.0);
			put(stretchDelivery,line._parent_doc_number,0.0);
			put(proposedDelivery,line._parent_doc_number,0.0);
			tempFloorFees = 0.0; //AQ 20150115
			tempBaseFees = 0.0;
			tempTargetFees = 0.0;
			tempStretchFees = 0.0;
			tempProposedFees = 0.0;
			put(tempModelDict,line._parent_doc_number,line._document_number);
			
			if(showERF == true){ //AQ 20150115
					tempFloorFees = tempFloorFees + line.erfAmountFloor_line; 
					tempBaseFees = tempBaseFees + line.erfAmountBase_line; 
					tempTargetFees = tempTargetFees + line.erfAmountTarget_line;  
					tempStretchFees = tempStretchFees + line.erfAmountStretch_line;
					tempProposedFees = tempProposedFees + line.erfAmountSell_line; 
			}
			if(showFRF == true){ //AQ 20150115
					tempFloorFees = tempFloorFees + line.frfAmountFloor_line; 
					tempBaseFees = tempBaseFees + line.frfAmountBase_line; 
					tempTargetFees = tempTargetFees + line.frfAmountTarget_line; 
					tempStretchFees = tempStretchFees + line.frfAmountStretch_line; 
					tempProposedFees = tempProposedFees + line.frfAmountSell_line; 
			}
			if(find(line._line_item_comment,"Base")<>-1){
				put(floorModelPrice,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees);
				put(baseModelPrice,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);
				put(targetModelPrice,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);
				put(stretchModelPrice,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees);
				put(proposedModelPrice,line._parent_doc_number,line.sellPrice_line+tempProposedFees);
				//addFrfErf = true;
			}
			if(find(line._line_item_comment,"Delivery")<>-1){ //AQ 20150115
				put(floorDelivery,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees); 
				put(baseDelivery,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);  
				put(targetDelivery,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);  
				put(stretchDelivery,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees); 
				put(proposedDelivery,line._parent_doc_number,line.sellPrice_line+tempProposedFees);

			}
				if(NOT containskey(floorFrfErf,line._parent_doc_number)){
					put(floorFrfErf,line._parent_doc_number,tempFloorFees);
					put(baseFrfErf,line._parent_doc_number,tempBaseFees);
					put(targetFrfErf,line._parent_doc_number,tempTargetFees);
					put(stretchFrfErf,line._parent_doc_number,tempStretchFees);
					put(proposedFrfErf,line._parent_doc_number,tempProposedFees);
				}
				else{
					put(floorFrfErf,line._parent_doc_number,(tempFloorFees)+get(floorFrfErf,line._parent_doc_number));
					put(baseFrfErf,line._parent_doc_number,(tempBaseFees)+get(baseFrfErf,line._parent_doc_number));
					put(targetFrfErf,line._parent_doc_number,(tempTargetFees)+get(targetFrfErf,line._parent_doc_number));
					put(stretchFrfErf,line._parent_doc_number,(tempStretchFees)+get(stretchFrfErf,line._parent_doc_number));
					put(proposedFrfErf,line._parent_doc_number,(tempProposedFees)+get(proposedFrfErf,line._parent_doc_number));
				}
			

		}
		elif(modelName == "Large Containers"){
			put(floorDelivery,line._parent_doc_number,0.0);
			put(baseDelivery,line._parent_doc_number,0.0);
			put(targetDelivery,line._parent_doc_number,0.0);
			put(stretchDelivery,line._parent_doc_number,0.0);
			put(proposedDelivery,line._parent_doc_number,0.0);
			put(tempModelDict,line._parent_doc_number,line._document_number);
			tempFloorFees = 0.0;
			tempBaseFees = 0.0;
			tempTargetFees = 0.0;
			tempStretchFees = 0.0;
			tempProposedFees = 0.0;
			if(showERF == true){
				tempFloorFees = tempFloorFees + line.erfAmountFloor_line;  
				tempBaseFees = tempBaseFees + line.erfAmountBase_line; 
				tempTargetFees = tempTargetFees + line.erfAmountTarget_line; 
				tempStretchFees = tempStretchFees + line.erfAmountStretch_line; 
				tempProposedFees = tempProposedFees + line.erfAmountSell_line; 
			}
			if(showFRF == true){
				tempFloorFees = tempFloorFees + line.frfAmountFloor_line;  
				tempBaseFees = tempBaseFees + line.frfAmountBase_line; 
				tempTargetFees = tempTargetFees + line.frfAmountTarget_line; 
				tempStretchFees = tempStretchFees + line.frfAmountStretch_line; 
				tempProposedFees = tempProposedFees + line.frfAmountSell_line; 
			}
			if(find(line._line_item_comment,"Delivery")<>-1){
				put(floorDelivery,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees); 
				put(baseDelivery,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);  
				put(targetDelivery,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);  
				put(stretchDelivery,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees); 
				put(proposedDelivery,line._parent_doc_number,line.sellPrice_line+tempProposedFees); 

			}
			if(find(line._line_item_comment,"Rental")<>-1){
				put(floorRental,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees);
				put(baseRental,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);
				put(targetRental,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);
				put(stretchRental,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees);
				put(proposedRental,line._parent_doc_number,line.sellPrice_line+tempProposedFees);
			}
			if(find(line._line_item_comment,"Disposal")<>-1){
				put(floorDisposal,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees);
				put(baseDisposal,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);
				put(targetDisposal,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);
				put(stretchDisposal,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees);
				put(proposedDisposal,line._parent_doc_number,line.sellPrice_line+tempProposedFees);
			}
			if(find(line._line_item_comment,"Haul")<>-1){
				put(floorHaul,line._parent_doc_number,line.totalFloorPrice_line+tempFloorFees);
				put(baseHaul,line._parent_doc_number,line.totalBasePrice_line+tempBaseFees);
				put(targetHaul,line._parent_doc_number,line.totalTargetPrice_line+tempTargetFees);
				put(stretchHaul,line._parent_doc_number,line.totalStretchPrice_line+tempStretchFees);
				put(proposedHaul,line._parent_doc_number,line.sellPrice_line+tempProposedFees);
				
			}
		}
	}
	else{
		modelName = line._model_name;
		put(tempModelDict,line._document_number,"");
		put(tempQtyDict,line._parent_doc_number,line._price_quantity); 
		if(line._model_name=="Large Containers"){
			showLarge = true;
			put(modelCategory,line._document_number,"LARGE CONTAINER");
			tempTons = atof(getconfigattrvalue(line._document_number,"estTonsHaul_l"));
			put(estTons,line._document_number,tempTons);
			tempHaulsPerMonth = 0.0;
			if(NOT isnull(getconfigattrvalue(line._document_number,"totalEstimatedHaulsMonth_l"))){
				tempHaulsPerMonth = atof(getconfigattrvalue(line._document_number,"totalEstimatedHaulsMonth_l"));
			}
			put(estHaulsPerMonth,line._document_number,tempHaulsPerMonth);
		}
		elif(line._model_name=="Containers"){
			showSmall = true;
			put(modelCategory,line._document_number,"SMALL CONTAINER");
		}
	}
}

largeContainerCalc = keys(floorHaul);
floorLargeTotal = 0.0;
baseLargeTotal = 0.0;
targetLargeTotal = 0.0;
stretchLargeTotal = 0.0;
proposedLargeTotal = 0.0;
for container in largeContainerCalc{

	modelDocNumber = container;
	// Initialize dictionaries - 20141229
	if(NOT containskey(floorRental,modelDocNumber)){
		put(floorRental,modelDocNumber,0.0);
		put(baseRental,modelDocNumber,0.0);
		put(targetRental,modelDocNumber,0.0);
		put(stretchRental,modelDocNumber,0.0);
		put(proposedRental,modelDocNumber,0.0);
	}
	if(NOT containskey(floorDisposal,modelDocNumber)){
		put(floorDisposal,modelDocNumber,0.0);
		put(baseDisposal,modelDocNumber,0.0);
		put(targetDisposal,modelDocNumber,0.0);
		put(stretchDisposal,modelDocNumber,0.0);
		put(proposedDisposal,modelDocNumber,0.0);
	}
	if(NOT containskey(floorHaul,modelDocNumber)){
		put(floorHaul,modelDocNumber,0.0);
		put(baseHaul,modelDocNumber,0.0);
		put(targetHaul,modelDocNumber,0.0);
		put(stretchHaul,modelDocNumber,0.0);
		put(proposedHaul,modelDocNumber,0.0);
	}

	tempTons = get(estTons,modelDocNumber);
	tempHaulsPerMonth = get(estHaulsPerMonth,modelDocNumber);
	floorCalc = get(floorRental,modelDocNumber)+(get(floorDisposal,modelDocNumber)*tempTons*tempHaulsPerMonth)+(get(floorHaul,modelDocNumber)*tempHaulsPerMonth);
	floorLargeTotal = floorLargeTotal + floorCalc;
	put(floorModelPrice,modelDocNumber,floorCalc);

	baseCalc = get(baseRental,modelDocNumber)+(get(baseDisposal,modelDocNumber)*tempTons*tempHaulsPerMonth)+(get(baseHaul,modelDocNumber)*tempHaulsPerMonth);
	baseLargeTotal = baseLargeTotal + baseCalc;
	put(baseModelPrice,modelDocNumber,baseCalc);

	targetCalc = get(targetRental,modelDocNumber)+(get(targetDisposal,modelDocNumber)*tempTons*tempHaulsPerMonth)+(get(targetHaul,modelDocNumber)*tempHaulsPerMonth);
	targetLargeTotal = targetLargeTotal + targetCalc;
	put(targetModelPrice,modelDocNumber,targetCalc);

	stretchCalc = get(stretchRental,modelDocNumber)+(get(stretchDisposal,modelDocNumber)*tempTons*tempHaulsPerMonth)+(get(stretchHaul,modelDocNumber)*tempHaulsPerMonth);
	stretchLargeTotal = stretchLargeTotal + stretchCalc;
	put(stretchModelPrice,modelDocNumber,stretchCalc);

	proposedCalc = get(proposedRental,modelDocNumber)+(get(proposedDisposal,modelDocNumber)*tempTons*tempHaulsPerMonth)+(get(proposedHaul,modelDocNumber)*tempHaulsPerMonth);
	proposedLargeTotal = proposedLargeTotal + proposedCalc;
	put(proposedModelPrice,modelDocNumber,proposedCalc);
}

//Loop through tempModelDict to create modelDict
tempModelLoop = keys(tempModelDict);
for each in tempModelLoop{
	modelDocNumber = each;
	if(get(tempModelDict,modelDocNumber)<>""){
		put(modelDict,modelDocNumber,"");
	}
}
modelLoop = keys(modelDict);
i = 0;
for each in modelLoop{
	modelDocNumber = each;
	modelLOB = get(modelCategory,modelDocNumber);
	tempPercent = 0.0;
	//CALCULATE FEES
	queryOne = "";
	queryTwo = "";
	FRF = false;
	ERF = false;
	Admin_Fee = false;
	tempTest = feesToCharge_quote;
	if(find(feesToCharge_quote,"FRF")<>-1){
		FRF = true;
	}
	if(find(feesToCharge_quote,"ERF")<>-1){
		ERF = true;
	}
	if(find(feesToCharge_quote,"Admin Fee")<>-1){
		Admin_Fee = true;
	}
	if(FRF == true AND ERF == true AND Admin_Fee == true){
		queryOne = "FRF~ERF~Admin Fee";
		queryTwo = queryOne;
	}
	elif(FRF == true AND ERF == true AND Admin_Fee == false){
		queryOne = "FRF~ERF";
		queryTwo = queryOne;
	}
	elif((FRF == true OR ERF == true) AND Admin_Fee == true){//seperated AND and OR for correct logical iterations. (GD) - 20150202
		queryTwo = "Admin Fee";
		if(FRF == true){
			queryOne = "FRF";
		}
		if(ERF == true){
			queryOne = "ERF";
		}
	}
	else{
		if(FRF == true){
			queryOne = "FRF";
		}
		if(ERF == true){
			queryOne = "ERF";
		}
		if(Admin_Fee == true){
			queryOne = "Admin Fee";
		}
		queryTwo = queryOne;
	}
	if(queryOne <> ""){
		feesQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'feesToCharge_quote' AND lob = $modelLOB AND ( variable_level = $queryOne OR variable_level = $queryTwo )");
		for fee in feesQuery{
			tempPercent = tempPercent + atof(get(fee,"comp_pct"));
			if(queryOne == queryTwo){
				break;
			}
		}
	}
	//CALCULATE RATE RESTRICTION
	if(customerRateRestriction_quote == false){
		rateRestrictionQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'customerRateRestriction_quote' AND lob = $modelLOB");
		for rate in rateRestrictionQuery{
			tempPercent = tempPercent + atof(get(rate,"comp_pct"));
			break;
		}
	}
	//CALCULATE DEFAULT CONTRACT TERM
	termThreshold = "";
	termQuery = BMQL("SELECT initialDefault FROM Div_Term_Exceptions WHERE division = $division_RO_quote");
	for term in termQuery{
		termThreshold = get(term,"initialDefault");
		break;
	}
	if(initialTerm_quote >= termThreshold){
		termCommisionQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'initialTerm_quote' AND lob = $modelLOB");
		for term in termCommisionQuery{
			tempPercent = tempPercent + atof(get(term,"comp_pct"));
			break;
		}
	}
	//CALCULATE SEGMENT
	segmentQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'segment_readOnly_quote' AND lob = $modelLOB AND variable_level = $segment_readOnly_quote");
	for segment in segmentQuery{
		tempPercent = tempPercent + atof(get(segment,"comp_pct"));
		break;
	}
	//CALCULATE INDUSTRY
	industryQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'industry_readOnly_quote' AND lob = $modelLOB AND variable_level = $industry_readOnly_quote");
	for industry in industryQuery{
		tempPercent = tempPercent + atof(get(industry,"comp_pct"));
		break;
	}
	//END SMALL/LARGE COM ADDER PCT
	put(modelAdderDict,modelDocNumber,tempPercent);
	tempPercent = 0.0;
	//CALCULATE WASTE CATEGORY
	wasteCategory = getconfigattrvalue(modelDocNumber,"wasteCategory");
	wasteCategoryQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'wasteCategory' AND lob = $modelLOB AND variable_level = $wasteCategory");	
	for category in wasteCategoryQuery{
		tempPercent = tempPercent + atof(get(category,"comp_pct"));
		break;
	}
	put(modelWasteCategory,modelDocNumber,tempPercent);
	
	//CALCULATE TOTAL DELIVERY %
	deliveryQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'numberOfTotalDeliveryContainers_quote' AND lob = $modelLOB");
	deliveryFee = 0.0; 
	for delivery in deliveryQuery{
		deliveryFee = atof(get(delivery,"comp_pct"));
		break;
	}
	put(modelDeliveryComm,modelDocNumber,deliveryFee);
	//BEGIN SMALL/LARGE COM BASE PCT
	commissionPct = Float[];
	baseComQuery = BMQL("SELECT comp_pct FROM comp_job_code_rules WHERE job_code = $creatorCode AND variable_id = 'currentQuoteTotal_quote' AND lob = $modelLOB");
	for baseCom in baseComQuery{
		append(commissionPct,atof(get(baseCom,"comp_pct")));
	}
	commissionPct = sort(commissionPct,"asc");
	put(floorComBasePct,modelDocNumber,commissionPct[1]);
	put(baseComBasePct,modelDocNumber,commissionPct[2]);
	put(targetComBasePct,modelDocNumber,commissionPct[3]);
	put(stretchComBasePct,modelDocNumber,commissionPct[4]);
	//SMALL COM BASE PCT
	if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
		priceTier = 0;
		priceBreakOne = 0;
		priceBreakTwo = 0;
		if(smallMonthlyTotalProposed_quote<smallMonthlyTotalFloor_quote){
			priceTier = 0;
			priceBreakOne = smallMonthlyTotalFloor_quote;
		}
		elif(smallMonthlyTotalProposed_quote >= smallMonthlyTotalFloor_quote AND smallMonthlyTotalProposed_quote < smallMonthlyTotalBase_quote){
			priceTier = 1;
			priceBreakOne = smallMonthlyTotalFloor_quote;
			priceBreakTwo = smallMonthlyTotalBase_quote;
		}
		elif(smallMonthlyTotalProposed_quote >= smallMonthlyTotalBase_quote AND smallMonthlyTotalProposed_quote < smallMonthlyTotalTarget_quote){
			priceTier = 2;
			priceBreakOne = smallMonthlyTotalBase_quote;
			priceBreakTwo = smallMonthlyTotalTarget_quote;
		}
		elif(smallMonthlyTotalProposed_quote >= smallMonthlyTotalTarget_quote AND smallMonthlyTotalProposed_quote < smallMonthlyTotalStretch_quote){
			priceTier = 3;
			priceBreakOne = smallMonthlyTotalTarget_quote;
			priceBreakTwo = smallMonthlyTotalStretch_quote;
		}
		elif(smallMonthlyTotalProposed_quote >= smallMonthlyTotalStretch_quote){
			priceTier = 4;
			priceBreakOne = smallMonthlyTotalStretch_quote;
		}
		nextTier = priceTier + 1;
		com_base_pct = 0;
		if(priceTier == 0){
			if(smallMonthlyTotalFloor_quote <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				com_base_pct = commissionPct[0] + (smallMonthlyTotalProposed_quote*((commissionPct[1]-commissionPct[0])/smallMonthlyTotalFloor_quote));
			}else{ 
				com_base_pct = commissionPct[0] + 0.0;
			}
			put(proposedComBasePct,modelDocNumber,com_base_pct);
			
		}
		if(priceTier >= 1 AND priceTier <= 3){
			if((priceBreakTwo-priceBreakOne) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				com_base_pct = commissionPct[priceTier] + (((smallMonthlyTotalProposed_quote-priceBreakOne)*(commissionPct[nextTier]-commissionPct[priceTier]))/(priceBreakTwo-priceBreakOne));
			}else{
				com_base_pct = commissionPct[priceTier] + 0.0;
			}
			put(proposedComBasePct,modelDocNumber,com_base_pct);
		}
		if(priceTier == 4){
			pctRatio = Float[];
			if((smallMonthlyTotalBase_quote-smallMonthlyTotalFloor_quote) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioOne = (commissionPct[1]-commissionPct[0])/(smallMonthlyTotalBase_quote-smallMonthlyTotalFloor_quote);
			}else{
				ratioOne = 0.0;
			}
			if((smallMonthlyTotalTarget_quote-smallMonthlyTotalBase_quote) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioTwo = (commissionPct[2]-commissionPct[1])/(smallMonthlyTotalTarget_quote-smallMonthlyTotalBase_quote);
			}else{
				ratioTwo = 0.0;
			}
			if((smallMonthlyTotalStretch_quote-smallMonthlyTotalTarget_quote)<> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioThree = (commissionPct[3]-commissionPct[2])/(smallMonthlyTotalStretch_quote-smallMonthlyTotalTarget_quote);
			}else{
				ratioThree = 0.0;
			}
			append(pctRatio,ratioOne);
			append(pctRatio,ratioTwo);
			append(pctRatio,ratioThree);
			pctToDollarRatio = max(pctRatio);
			dollarAmmountOver = smallMonthlyTotalProposed_quote - priceBreakOne;
			proposedPct = (dollarAmmountOver*pctToDollarRatio)+commissionPct[4];
			if(proposedPct > commissionPct[5]){
				put(proposedComBasePct,modelDocNumber,commissionPct[5]);
			}
			else{
				put(proposedComBasePct,modelDocNumber,proposedPct);
			}
		}
	}
	//LARGE COM BASE PCT
	elif(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
		priceTier = 0;
		priceBreakOne = 0;
		priceBreakTwo = 0;
		if(largeMonthlyTotalProposed_quote<largeMonthlyTotalFloor_quote){
			priceTier = 0;
			priceBreakOne = largeMonthlyTotalFloor_quote;
		}
		elif(largeMonthlyTotalProposed_quote >= largeMonthlyTotalFloor_quote AND largeMonthlyTotalProposed_quote < largeMonthlyTotalBase_quote){
			priceTier = 1;
			priceBreakOne = largeMonthlyTotalFloor_quote;
			priceBreakTwo = largeMonthlyTotalBase_quote;
		}
		elif(largeMonthlyTotalProposed_quote >= largeMonthlyTotalBase_quote AND largeMonthlyTotalProposed_quote < largeMonthlyTotalTarget_quote){
			priceTier = 2;
			priceBreakOne = largeMonthlyTotalBase_quote;
			priceBreakTwo = largeMonthlyTotalTarget_quote;
		}
		elif(largeMonthlyTotalProposed_quote >= largeMonthlyTotalTarget_quote AND largeMonthlyTotalProposed_quote < largeMonthlyTotalStretch_quote){
			priceTier = 3;
			priceBreakOne = largeMonthlyTotalTarget_quote;
			priceBreakTwo = largeMonthlyTotalStretch_quote;
		}
		elif(largeMonthlyTotalProposed_quote >= largeMonthlyTotalStretch_quote){
			priceTier = 4;
			priceBreakOne = largeMonthlyTotalStretch_quote;
		}
		nextTier = priceTier + 1;
		com_base_pct = 0;
		if(priceTier == 0){
			if(largeMonthlyTotalFloor_quote <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				com_base_pct = commissionPct[0] + (largeMonthlyTotalProposed_quote*((commissionPct[1]-commissionPct[0])/largeMonthlyTotalFloor_quote));
			}else{ 
				com_base_pct = commissionPct[0] + 0.0;
			}
			put(proposedComBasePct,modelDocNumber,com_base_pct);
			
		}
		if(priceTier >= 1 AND priceTier <= 3){
			if((priceBreakTwo-priceBreakOne) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				com_base_pct = commissionPct[priceTier] + (((largeMonthlyTotalProposed_quote-priceBreakOne)*(commissionPct[nextTier]-commissionPct[priceTier]))/(priceBreakTwo-priceBreakOne));
			}else{
				com_base_pct = commissionPct[priceTier] + 0.0;
			}
			put(proposedComBasePct,modelDocNumber,com_base_pct);
		}
		if(priceTier == 4){
			pctRatio = Float[];
			if((largeMonthlyTotalBase_quote-largeMonthlyTotalFloor_quote) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioOne = (commissionPct[1]-commissionPct[0])/(largeMonthlyTotalBase_quote-largeMonthlyTotalFloor_quote);
			}else{
				ratioOne = 0.0;
			}
			if((largeMonthlyTotalTarget_quote-largeMonthlyTotalBase_quote) <> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioTwo = (commissionPct[2]-commissionPct[1])/(largeMonthlyTotalTarget_quote-largeMonthlyTotalBase_quote);
			}else{
				ratioTwo = 0.0;
			}
			if((largeMonthlyTotalStretch_quote-largeMonthlyTotalTarget_quote)<> 0.0){ //Added if statement to check for 0 in denominator, if so use 1.0 instead.
				ratioThree = (commissionPct[3]-commissionPct[2])/(largeMonthlyTotalStretch_quote-largeMonthlyTotalTarget_quote);
			}else{
				ratioThree = 0.0;
			}
			append(pctRatio,ratioOne);
			append(pctRatio,ratioTwo);
			append(pctRatio,ratioThree);
			pctToDollarRatio = max(pctRatio);
			dollarAmmountOver = largeMonthlyTotalProposed_quote - priceBreakOne;
			proposedPct = (dollarAmmountOver*pctToDollarRatio)+commissionPct[4];
			if(proposedPct > commissionPct[5]){
				put(proposedComBasePct,modelDocNumber,commissionPct[5]);
			}
			else{
				put(proposedComBasePct,modelDocNumber,proposedPct);
			}
		}
	}
	i = i + 1;
}

smallGroupPrices = float[]; //DO NOT INCLUDE DELIVERY
largeGroupPrices = float[]; //DO NOT INCLUDE DELIVERY
totalGroupPrices = float[]; //DO NOT INCLUDE DELIVERY
smallGroupCommission = float[]; //DO NOT INCLUDE DELIVERY
largeGroupCommission = float[]; //DO NOT INCLUDE DELIVERY
totalGroupNoOTC = float[]; //DO NOT INCLUDE DELIVERY
totalGroupCommission = float[]; 
totalOneTimeCommission = float[]; 
smallGroupPct = float[];
largeGroupPct = float[];
totalGroupPct = float[];

i = 0;
tierPricing = range(5);
for tier in tierPricing{
	tempSmallErfFrf = 0.0;
	tempLargeErfFrf = 0.0;
	tempTotalErfFrf = 0.0;
	if(i == 0){
		totalGroupPrices[i] = totalGroupPrices[i] + smallMonthlyTotalFloor_quote + floorLargeTotal; 
		smallGroupPrices[i] = smallGroupPrices[i] + smallMonthlyTotalFloor_quote; 
		largeGroupPrices[i] = largeGroupPrices[i] + floorLargeTotal; 
		tempOneTimeCom = 0.0;
		for each in modelLoop{ 
			modelDocNumber = each;
			tempBaseCom = get(floorComBasePct,modelDocNumber); 
			tempModelPrice = get(floorModelPrice,modelDocNumber); 
			tempAdderCom = get(modelAdderDict,modelDocNumber); 
			tempWasteCom = get(modelWasteCategory,modelDocNumber);
			
			if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
				//tempModelPrice = tempModelPrice + get(floorFrfErf,modelDocNumber); 
				smallGroupCommission[i] = smallGroupCommission[i]+(tempModelPrice*(tempBaseCom+tempAdderCom+tempWasteCom)); 
				tempSmallErfFrf = tempSmallErfFrf + get(floorFrfErf,modelDocNumber);
				tempTotalErfFrf = tempTotalErfFrf + get(floorFrfErf,modelDocNumber);
				smallGroupPct[i] = (tempBaseCom+tempAdderCom+tempWasteCom);
			}
			if(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
				largeGroupCommission[i] = largeGroupCommission[i]+(tempModelPrice*(tempBaseCom+tempAdderCom+tempWasteCom)); 			
			} 

			tempOneTimeCom = (get(floorDelivery,modelDocNumber)*get(modelDeliveryComm,modelDocNumber)*get(tempQtyDict,modelDocNumber)); 
			totalOneTimeCommission[i] = totalOneTimeCommission[i] + tempOneTimeCom; 
			totalGroupNoOTC[i] = smallGroupCommission[i] + largeGroupCommission[i];
			totalGroupCommission[i] = totalGroupNoOTC[i] + totalOneTimeCommission[i];
		}
	}
	elif(i == 1){
		totalGroupPrices[i] = totalGroupPrices[i] + smallMonthlyTotalBase_quote + baseLargeTotal; 
		smallGroupPrices[i] = smallGroupPrices[i] + smallMonthlyTotalBase_quote; 
		largeGroupPrices[i] = largeGroupPrices[i] + baseLargeTotal; 
		tempOneTimeCom = 0.0;
		for each in modelLoop{
			modelDocNumber = each;
			tempBaseCom = get(baseComBasePct,modelDocNumber); 
			tempModelPrice = get(baseModelPrice,modelDocNumber); 
			tempAdderCom = get(modelAdderDict,modelDocNumber); 
			tempWasteCom = get(modelWasteCategory,modelDocNumber);
			
			if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
				//tempModelPrice = tempModelPrice+get(baseFrfErf,modelDocNumber); 
				smallGroupCommission[i] = smallGroupCommission[i]+(tempModelPrice*(tempBaseCom+tempAdderCom+tempWasteCom)); 
				tempSmallErfFrf = tempSmallErfFrf + get(baseFrfErf,modelDocNumber);
				tempTotalErfFrf = tempTotalErfFrf + get(baseFrfErf,modelDocNumber);
				smallGroupPct[i] = (tempBaseCom+tempAdderCom+tempWasteCom);
			}
			if(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
				largeGroupCommission[i] = largeGroupCommission[i]+(tempModelPrice*(tempBaseCom+tempAdderCom+tempWasteCom)); 	
			} 

			tempOneTimeCom = (get(baseDelivery,modelDocNumber)*get(modelDeliveryComm,modelDocNumber)*get(tempQtyDict,modelDocNumber));  
			totalOneTimeCommission[i] = totalOneTimeCommission[i] + tempOneTimeCom; 
			totalGroupNoOTC[i] = smallGroupCommission[i] + largeGroupCommission[i];
			totalGroupCommission[i] = totalGroupNoOTC[i] + totalOneTimeCommission[i];
		}
	}
	elif(i == 2){
		totalGroupPrices[i] = totalGroupPrices[i] + smallMonthlyTotalTarget_quote + targetLargeTotal; 
		smallGroupPrices[i] = smallGroupPrices[i] + smallMonthlyTotalTarget_quote; 
		largeGroupPrices[i] = largeGroupPrices[i] + targetLargeTotal; 
		tempOneTimeCom = 0.0;
		for each in modelLoop{ 
			modelDocNumber = each;
			tempTargetCom = get(targetComBasePct,modelDocNumber); 
			tempModelPrice = get(targetModelPrice,modelDocNumber); 
			tempAdderCom = get(modelAdderDict,modelDocNumber); 
			tempWasteCom = get(modelWasteCategory,modelDocNumber);
			
			if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
				//tempModelPrice = tempModelPrice+get(targetFrfErf,modelDocNumber); print smallGroupCommission[i];
				smallGroupCommission[i] = smallGroupCommission[i]+(tempModelPrice*(tempTargetCom+tempAdderCom+tempWasteCom)); 
				tempSmallErfFrf = tempSmallErfFrf + get(targetFrfErf,modelDocNumber);
				tempTotalErfFrf = tempTotalErfFrf + get(targetFrfErf,modelDocNumber);
				smallGroupPct[i] = (tempTargetCom+tempAdderCom+tempWasteCom);
			}
			if(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
				largeGroupCommission[i] = largeGroupCommission[i]+(tempModelPrice*(tempTargetCom+tempAdderCom+tempWasteCom)); 
			} 

			tempOneTimeCom = (get(targetDelivery,modelDocNumber)*get(modelDeliveryComm,modelDocNumber)*get(tempQtyDict,modelDocNumber));  
			totalOneTimeCommission[i] = totalOneTimeCommission[i] + tempOneTimeCom; 
			totalGroupNoOTC[i] = smallGroupCommission[i] + largeGroupCommission[i];
			totalGroupCommission[i] = totalGroupNoOTC[i] + totalOneTimeCommission[i];
		}
	}
	elif(i == 3){
		totalGroupPrices[i] = totalGroupPrices[i] + smallMonthlyTotalStretch_quote + stretchLargeTotal; 
		smallGroupPrices[i] = smallGroupPrices[i] + smallMonthlyTotalStretch_quote; 
		largeGroupPrices[i] = largeGroupPrices[i] + stretchLargeTotal; 
		tempOneTimeCom = 0.0;
		for each in modelLoop{ 
			modelDocNumber = each;
			tempStretchCom = get(stretchComBasePct,modelDocNumber); 
			tempModelPrice = get(stretchModelPrice,modelDocNumber); 
			tempAdderCom = get(modelAdderDict,modelDocNumber); 
			tempWasteCom = get(modelWasteCategory,modelDocNumber);
			
			if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
				//tempModelPrice = tempModelPrice +get(stretchFrfErf,modelDocNumber); print smallGroupCommission[i];
				smallGroupCommission[i] = smallGroupCommission[i]+(tempModelPrice*(tempStretchCom+tempAdderCom+tempWasteCom)); 
				tempSmallErfFrf = tempSmallErfFrf + get(stretchFrfErf,modelDocNumber);
				tempTotalErfFrf = tempTotalErfFrf + get(stretchFrfErf,modelDocNumber);
				smallGroupPct[i] = (tempStretchCom+tempAdderCom+tempWasteCom);
			}
			if(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
				largeGroupCommission[i] = largeGroupCommission[i]+(tempModelPrice*(tempStretchCom+tempAdderCom+tempWasteCom)); 			
			} 

			tempOneTimeCom = (get(stretchDelivery,modelDocNumber)*get(modelDeliveryComm,modelDocNumber)*get(tempQtyDict,modelDocNumber));  
			totalOneTimeCommission[i] = totalOneTimeCommission[i] + tempOneTimeCom; 
			totalGroupNoOTC[i] = smallGroupCommission[i] + largeGroupCommission[i];
			totalGroupCommission[i] = totalGroupNoOTC[i] + totalOneTimeCommission[i];
		}
	}
	elif(i == 4){
		totalGroupPrices[i] = totalGroupPrices[i] + smallMonthlyTotalProposed_quote + proposedLargeTotal; 
		smallGroupPrices[i] = smallGroupPrices[i] + smallMonthlyTotalProposed_quote; 
		largeGroupPrices[i] = largeGroupPrices[i] + proposedLargeTotal; 
		tempOneTimeCom = 0.0;
		for each in modelLoop{ 
			modelDocNumber = each;
			tempProposedCom = get(proposedComBasePct,modelDocNumber); 
			tempModelPrice = get(proposedModelPrice,modelDocNumber); 
			tempAdderCom = get(modelAdderDict,modelDocNumber); 
			tempWasteCom = get(modelWasteCategory,modelDocNumber);
			if(get(modelCategory,modelDocNumber)=="SMALL CONTAINER"){
				//tempModelPrice = tempModelPrice+get(proposedFrfErf,modelDocNumber); print smallGroupCommission[i];
				smallGroupCommission[i] = smallGroupCommission[i]+(tempModelPrice*(tempProposedCom+tempAdderCom+tempWasteCom)); 
				tempSmallErfFrf = tempSmallErfFrf + get(proposedFrfErf,modelDocNumber);
				tempTotalErfFrf = tempTotalErfFrf + get(proposedFrfErf,modelDocNumber);
				smallGroupPct[i] = (tempProposedCom+tempAdderCom+tempWasteCom);
			}
			if(get(modelCategory,modelDocNumber)=="LARGE CONTAINER"){
				largeGroupCommission[i] = largeGroupCommission[i]+(tempModelPrice*(tempProposedCom+tempAdderCom+tempWasteCom)); 			
			}
			tempOneTimeCom = (get(proposedDelivery,modelDocNumber)*get(modelDeliveryComm,modelDocNumber)*get(tempQtyDict,modelDocNumber)); 
			totalOneTimeCommission[i] = totalOneTimeCommission[i] + tempOneTimeCom; 
			totalGroupNoOTC[i] = smallGroupCommission[i] + largeGroupCommission[i];
			totalGroupCommission[i] = totalGroupNoOTC[i] + totalOneTimeCommission[i];
		}
	}
	/*if(smallGroupPrices[i]<>0){
		smallGroupPct[i] = smallGroupCommission[i]/(smallGroupPrices[i]+tempSmallErfFrf); print "smallGroupPct: "; print smallGroupCommission[i]; print smallGroupPrices[i]; print tempSmallErfFrf;
	}
	else{
		smallGroupPct[i] = 0.0;
	} */
	if(largeGroupPrices[i]<>0){
		largeGroupPct[i] = largeGroupCommission[i]/(largeGroupPrices[i]);
	}
	else{
		largeGroupPct[i] = 0.0;
	}
	if(totalGroupPrices[i]<>0){
		totalGroupPct[i] = totalGroupNoOTC[i]/(totalGroupPrices[i]+tempTotalErfFrf);
	}
	else{
		totalGroupPct[i] = 0.0;
	}	
	i = i + 1;
}

detailSmall = "";				
detailSmallPercent = "";		
detailLarge = "";				
detailLargePercent = "";
detailSmallLargeOTC = "";
detailSmallLargeTotal = "";
summarySmallLargeTotal = "";
summarySmallLargePercent = "";

i = 0;
for tier in tierPricing{
	if(i <> 0){
		detailSmall = detailSmall + "^_^";
		detailSmallPercent = detailSmallPercent + "^_^";	
		detailLarge = detailLarge + "^_^";			
		detailLargePercent = detailLargePercent + "^_^";
		detailSmallLargeOTC = detailSmallLargeOTC + "^_^";
		detailSmallLargeTotal = detailSmallLargeTotal + "^_^";
		summarySmallLargeTotal = summarySmallLargeTotal + "^_^";
		summarySmallLargePercent = summarySmallLargePercent + "^_^";
	}
	detailSmall = detailSmall + string(smallGroupCommission[i]);
	detailSmallPercent = detailSmallPercent + string(smallGroupPct[i]);	
	detailLarge = detailLarge + string(largeGroupCommission[i]);			
	detailLargePercent = detailLargePercent + string(largeGroupPct[i]);
	detailSmallLargeOTC = detailSmallLargeOTC + string(totalOneTimeCommission[i]); 
	detailSmallLargeTotal = detailSmallLargeTotal + string(totalGroupCommission[i]);
	summarySmallLargeTotal = summarySmallLargeTotal + string(totalGroupCommission[i]);
	summarySmallLargePercent = summarySmallLargePercent + string(totalGroupPct[i]);
	i = i + 1;
}

queryResponseOne = false;
queryResponseTwo = false;
jobCodeQuery = BMQL("SELECT comp_eligible FROM comp_job_codes WHERE job_code = $creatorCode");
for jobCode in jobCodeQuery{
	queryResponseOne = true;
	if(get(jobCode,"Comp_Eligible") == "0"){
		showSmall = false;
		showLarge = false;
	}
} 
areaQuery = BMQL("SELECT is_comp_active FROM lawson_division WHERE division = $division_RO_quote");
for area in areaQuery{
	queryResponseTwo = true;
	if(get(area,"is_comp_active") == "0"){
		showSmall = false;
		showLarge = false;
	}
} 
if(accountType_quote == "Temporary"){ //added this piece to hide the commission for temporary accounts
	showSmall = false;
	showLarge = false;
}
if(salesActivity_quote == "Change of Owner"){//added this piece to hide the commission for Change of Owner
	showSmall = false;
	showLarge = false;
}
if(salesActivity_quote == "Existing Customer" AND existingCustomerWithNewSite_quote == false){//added this piece to hide the commission for Existing customer except new site
	showSmall = false;
	showLarge = false;
}
if(queryResponseOne == false OR queryResponseTwo == false){
	showSmall = false;
	showLarge = false;
}

shouldShowSmall = showSmall;
shouldShowLarge = showLarge;

if(compOwnerLogin_quote <> _system_user_login){
	shouldShowSmall = false;
	shouldShowLarge = false;
}

ret = ret + "1~hTMLdetailSmall~" + detailSmall + "|"
	+ "1~hTMLdetailSmallPercent~" + detailSmallPercent + "|"
	+ "1~hTMLdetailLarge~" + detailLarge + "|"
	+ "1~hTMLdetailLargePercent~" + detailLargePercent + "|"
	+ "1~hTMLdetailTotalOTC~" + detailSmallLargeOTC + "|"
	+ "1~hTMLdetailTotal~"+ detailSmallLargeTotal + "|"
	+ "1~hTMLsummaryTotal~"+ summarySmallLargeTotal + "|"
	+ "1~hTMLsummaryTotalPercent~"+ summarySmallLargePercent + "|"
	+ "1~hTMLshowSmall~"+ string(shouldShowSmall) + "|" 
	+ "1~hTMLshowLarge~"+ string(shouldShowLarge) + "|"
	+ "1~hTMLshouldShowSmall~" + string(showSmall) + "|"
	+ "1~hTMLshouldShowLarge~" + string(showLarge) + "|"; 


return ret;