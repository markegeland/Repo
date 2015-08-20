/*
==================================================================================================
Name:   		Electronic Recycling Pricing Commerce Library
Author:			Sheehuavah Zaj Moua
Create date:    	7/23/2015
Description:		Sets the pricing for Electronic Recycling
        
Input:   		Electronic Recycling Pack Up & Pick Up configuration attributes 
                    
Output:  		String describing the prices for all of the configuration

Updates:		Zaj - #731 Added Box Mail Pricing 
			20150817 - Zaj - #731 Added initialization of ER base/floor/average/target
			20150817 - Zaj - #731 Added line item comments for Box Mail
			20150818 - Mark Egeland - #731 Added occurrence to return string, edited sell price logic
====
*/

//strings attributes
COMM_VALUE_DELIM = "^_^";
FIELD_DELIM = "@_@";
liftGateFee = "";
onSiteTrailerFee = "";
commodityMix = "";
businessType = "";
retStr = "";
numberOfPallets = "";	
totalWeight = "";
assetQuantity = "";
stairCarry = "";	
fuelSurcharge = "";
additionalStopFee = "";
specificPickupTime = "";
electronicRecyclingCategory = "";
occurrence = "";

//integer attributes	

//float attributes
rateOfPallets = 0.0;					
totalRateOfPallets = 0.0;
rateTotalWeight = 0.0;
baseWeightRate = 0.0 ;			totalRateTotalWeight = 0.0;
liftGateFeeRate = 0.0;			ERILiftGateFee = 0.0;
totalLiftGateFeeRate = 0.0;
onSiteTrailerFeeRate = 0.0;
videoDisplayDevices = 0.0;		videoDisplayDevicesRate = 0.0;
totalVideoDisplayDevices = 0.0;
highGrade = 0.0;			highGradeRate = 0.0;
totalHighGrade = 0.0;
lowGrade = 0.0;				lowGradeRate = 0.0;
totalLowGrade = 0.0;
totalCommodityMix = 0.0;
assetQuantityRate = 0.0;		totalAssetQuantity = 0.0;
businessTypeRate = 0.0;			totalBusinessType = 0.0;
stairCarryRate = 0.0;			totalStairCarry = 0.0;
additionalStopFeeRate = 0.0;		totalAdditionalStopFee = 0.0;
fuelSurchargeRate = 0.0;		totalFuelSurcharge = 0.0;
specificPickupTimeRate = 0.0;		totalSpecificPickupTime = 0.0;
erBasePrice = 0.0;
erFloorPrice = 0.0;
erAveragePrice = 0.0;
erTargetPrice = 0.0;
electonicRecyclingTotalFloor = 0.0;
electonicRecyclingTotalBase = 0.0;
electonicRecyclingTotalTarget = 0.0;
electonicRecyclingTotalStretch = 0.0;

//boolean attributes

//dict attributes

//Electronic Recycling Margins
erFloorMargin = 0.25;
erAverageMargin = 0.325;
erTargetMargin = 0.4;
erFloorPrice = 0.0;
erAveragePrice = 0.0;
erTargetPrice = 0.0;

//bmql to gather Electronic Recycling Prices
recordSet = bmql("Select rate, numberOfPallets, charge, maxFreight, minFreight, condVarName FROM ERPrice");

for line in line_process{

	if(line._parent_doc_number <> "" AND line._parent_line_item == "Electronic Recycling"){
		electronicRecyclingCategory = getconfigattrvalue(line._parent_doc_number, "electronicRecyclingCategory_er");	
			
		if(electronicRecyclingCategory == "Box Mail-Back"){
			inputDict = dict("string");	//Build the input dict to parse Config line item comments. inputStr, COMM_VALUE_DELIM, and FIELD_DELIM are constant for all inputs
			
			put(inputDict, "inputStr", line._line_item_comment);
			put(inputDict, "COMM_VALUE_DELIM", COMM_VALUE_DELIM);
			put(inputDict, "FIELD_DELIM", FIELD_DELIM);
			
			put(inputDict,"key","Occurrence");
			occurrence = util.parseThroughLineItemComment(inputDict);
			
			put(inputDict, "key", "boxSize");
			boxSize = util.parseThroughLineItemComment(inputDict);
			query = bmql("SELECT price FROM BoxMailBackBoxInfo WHERE box = $boxSize");

			for each in query{
				erBasePrice = getfloat(each, "price") * line._price_quantity;
			}
			
		}elif(electronicRecyclingCategory == "Pack-Up and Pick-Up" OR electronicRecyclingCategory == "Full Service"){
			
			//gather config information
			numberOfPallets = getconfigattrvalue(line._parent_doc_number, "numberOfPallets_er");
			totalWeight = getconfigattrvalue(line._parent_doc_number, "totalWeight_er");
			liftGateFee = getconfigattrvalue(line._parent_doc_number, "liftGateFee_er");
			commidityMix = getconfigattrvalue(line._parent_doc_number, "commidityMix_er");
			assetQuantity = getconfigattrvalue(line._parent_doc_number, "assetQuantity_er");
			businessType = getconfigattrvalue(line._parent_doc_number, "businessType_er");
			stairCarry = getconfigattrvalue(line._parent_doc_number, "stairCarry_er");
			additionaStopFee = getconfigattrvalue(line._parent_doc_number, "additionalStopFee_er");
			fuelSurcharge = getconfigattrvalue(line._parent_doc_number, "fuelSurcharge_er");
			specificPickupTime = getconfigattrvalue(line._parent_doc_number, "specificPickupTime_er");
			
			for record in recordSet{
			
				if(totalWeight > get(record, "minFreight") AND totalWeight < get(record, "maxFreight")){
					baseWeightRate = atof(get(record, "charge")); 
					rateTotalWeight = atof(get(record, "rate"));
				}
							
				if(liftGateFee == "Yes" AND get(record, "condVarName") == "lifeGateFee_er") {
					liftGateFeeRate = atof(get(record, "rate"));
				}
				
				if(onSiteTrailerFee == "Yes" AND get(record, "condVarName") == "onSiteTrailerFee_er"){
					onSiteTrailerFeeRate = atof(get(record, "rate"));
				}
				
				if(numberOfPallets == get(record, "numberOfPallets")){
					rateOfPallets = atof(get(record, "rate"));
				}
				
				if(commodityMix == "Yes"){
					videoDisplayDevices = atoi(getconfigattrvalue(line._parent_doc_number, "videoDisplayDevices_er"))/100;
					highGrade = atoi(getconfigattrvalue(line._parent_doc_number, "highGrade_er"))/100;
					lowGrade = atoi(getconfigattrvalue(line._parent_doc_number, "lowGrade_er"))/100;
				}
				else{
					videoDisplayDevices = 0.5;
					highGrade = 0.15;
					lowGrade = 0.35;
				}
						
				if(get(record, "condVarName") == "videoDisplayDevices_er"){
					videoDisplayDevicesRate = atof(get(record, "rate"));
				}
				
				if(get(record, "condVarName") == "highGrade_er"){
					highGradeRate = atof(get(record, "rate"));
				}
				
				if(get(record, "condVarName") == "lowGrade_er"){
					lowGradeRate = atof(get(record, "rate"));
				}
				
				if(get(record, "condVarName") == "assetQuantity_er"){
					assetQuantityRate = atof(get(record, "rate"));
				}
				
				if(get(record, "condVarName") == businessType){
					businessTypeRate = atof(get(record,"rate"));
				}
				
				if(get(record, "condVarNam") == "stairCarry_er"){
					stairCarryRate = atof(get(record, "rate"));
				}
				
				if(additionalStopFee == "True" AND get(record, "condVarName") == "additionalStopFee_er"){
					additionalStopFeeRate = atof(get(record, "rate"));
				}
				
				if(fuelSurcharge == "True" AND get(record, "condVarNam") == "fuelSurcharge_er"){
					fuelSurchargeRate = atof(get(record, "rate"));
				}
				
				if(specificPickupTime == "True" AND get(record, "condVarNam") == "specificPickupTime_er"){
					specificPickupTimeRate = atof(get(record, "rate"));
				}
			}

			//Calculations of configuration
			totalRateOfPallets = rateOfPallets;
			totalRateTotalWeight = baseWeightRate + rateTotalWeight * atof(totalWeight);
			totalLiftGateFee = liftGateFeeRate + ERILiftGateFee;
			//commodityMix
			totalVideoDisplayDevices = atof(totalWeight) * videoDisplayDevices * videoDisplayDevicesRate;
			totalHighGrade = atof(totalWeight) * highGrade * highGradeRate;
			totalLowGrade = atof(totalWeight) * lowGrade * lowGradeRate;
			totalCommodityMix = totalVideoDisplayDevices + totalHighGrade + totalLowGrade;

			totalAssetQuantity = atof(assetQuantity) * assetQuantityRate;
			totalBusinessType = businessTypeRate;
			totalStairCarry = atof(stairCarry) * stairCarryRate;
			totalAdditionalStopFee = additionalStopFeeRate;
			totalFuelSurchargeRate = fuelSurchargeRate;
			totalSpecificPickupTime = specificPickupTimeRate;
			
			//Sum of configuration 
			erBasePrice = totalRateOfPallets + totalRateTotalWeight + totalLiftGateFee + totalCommodityMix + totalAssetQuantity + totalBusinessType + totalStairCarry 
										+ totalAdditionalStopFee + totalFuelSurcharge + totalSpecificPickupTime;
		}
		
		//Calculations of Margins
		erFloorPrice = erBasePrice * (1.0 + erFloorMargin);
		erAveragePrice = erBasePrice * (1.0 + erAverageMargin);
		erTargetPrice = erBasePrice * (1.0 + erTargetMargin);
		
		electonicRecyclingTotalFloor = electonicRecyclingTotalFloor + erBasePrice;
		electonicRecyclingTotalBase = electonicRecyclingTotalBase + erFloorPrice;
		electonicRecyclingTotalTarget = electonicRecyclingTotalTarget + erAveragePrice;
		electonicRecyclingTotalStretch = electonicRecyclingTotalStretch + erTargetPrice;
		
		
		retStr = retStr + line._document_number + "~" + "totalFloorPrice_line" + "~" + string(erBasePrice) + "|"
								+ line._document_number + "~" + "totalBasePrice_line" + "~" + string(erFloorPrice) + "|"
								+ line._document_number + "~" + "totalTargetPrice_line" + "~" + string(erAveragePrice) + "|"
								+ line._document_number + "~" + "targetPrice_line" + "~" + string(erAveragePrice) + "|"
								+ line._document_number + "~" + "totalStretchPrice_line" + "~" + string(erTargetPrice) + "|"
								+ line._document_number + "~" + "frequency_line" + "~" + occurrence + "|"
								+ line._document_number + "~" + "billingType_line" + "~" + occurrence + "|"
								+ line._document_number + "~" + "isSellPriceDefaultSetCopy_line" + "~" + line.isSellPriceDefaultSet_line + "|"
								+ line._document_number + "~" + "sellPriceTemp_line" + "~" + string(line.sellPrice_line) + "|";
	}
}

retStr = retStr + "1" + "~" + "electronicRecyclingTotalFloor_quote" + "~" + string(electonicRecyclingTotalFloor) + "|"
						+ "1" + "~" + "electronicRecyclingTotalBase_quote" + "~" + string(electonicRecyclingTotalBase) + "|"
						+ "1" + "~" + "electronicRecyclingTotalTarget_quote" + "~" + string(electonicRecyclingTotalTarget) + "|"
						+ "1" + "~" + "electronicRecyclingTotalStretch_quote" + "~" + string(electonicRecyclingTotalStretch) + "|";

return retStr;