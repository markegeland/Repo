1	frfAmountStretch_line	stretchPrice_line * frfRate_quote 
 	
2	erfAmountBase_line	( basePrice_line * ( 1 + frfRate_quote ) ) * erfRate_quote 
 	
3	isSellPriceDefaultSet_line	if( ( isSellPriceDefaultSetCopy_line = "" ), "false", "true" ) 
 	
4	largeMonthlyDisposalTargetPrice_line	if( ( rateType_line = "Disposal" ), ( ( totalTargetPrice_line * toFloat(estimatedLifts_line) ) * estTonsHaul_Line ), 0.0 ) 
 	
5	largeMonthlyTotalDisposalTarget_quote	sum(largeMonthlyDisposalTargetPrice_line) 
 	
6	smallMonthlyTotalTarget_quote	sumIf( ( rateType_line = "Base" ), totalTargetPrice_line) 
 	
7	includeERF_quote	if(contains(feesToCharge_quote, "ERF" ), "Yes", "No" ) 
 	
8	includeFRF_quote	if(contains(feesToCharge_quote, "FRF" ), "Yes", "No" ) 
 	
9	commissionRate_quote	query( commissionRate, commissionRate, ( priceBand = priceBand_quote OR priceBand = 'All' ) AND ( ERF = includeERF_quote OR ERF = 'All' ) AND ( FRF = includeFRF_quote OR FRF = 'All' ), 0.0 ) 
 	
10	totalCommissionSell_quote	grandTotalSell_quote * commissionRate_quote 
 	
11	totalCommissionStretch_quote	grandTotalStretch_quote * commissionRate_quote 
 	
12	totalCommissionTarget_quote	grandTotalTarget_quote * commissionRate_quote 
 	
13	totalCommissionBase_quote	grandTotalBase_quote * commissionRate_quote 
 	
14	totalCommissionFloor_quote	grandTotalFloor_quote * commissionRate_quote 
 	
15	fixedFRFflag_quote	if( ( find(feesToCharge_quote, "Fixed Fuel" ) = -1 ), 0, 1 ) 
 	
16	monthlyTotalTargetNoFees_quote	sumIf( ( ( rateType_line NOT= "Delivery" ) AND ( rateType_line NOT= "Disposal" ) ), targetPrice_line) 
 	
17	sICCode_quote	query( industries, SIC_Code, industryName = industry_quote, "OTHR" ) 
 	
18	largeMonthlyHaulTargetPrice_line	if( ( ( rateType_line = "Haul" ) OR ( rateType_line = "Flat" ) ), ( totalTargetPrice_line * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
19	largeMonthlyTotalHaulTarget_quote	sum(largeMonthlyHaulTargetPrice_line) 
 	
20	family_line	_part_custom_field1 
 	
21	description_line	if( ( isPartLineItem_line AND ( family_line NOT= "Ad-Hoc" ) ), if(contains(serviceLevel_line, "Call", ignoreCase ),if( ( ( rateType_line = "Delivery" ) OR ( rateType_line = "Installation" ) ), concat(_part_desc, ",", "On-Call", ",", rateType_line),concat(_part_desc, ",", wasteType_line, ",", liftsPerContainer_line, ",", "On-Call", ",", rateType_line)),if( ( ( rateType_line = "Delivery" ) OR ( rateType_line = "Installation" ) ), concat(_part_desc, ",", rateType_line),if( ( rateType_line = "Overage" ), concat(_part_desc, ",", wasteType_line, ",", liftsPerContainer_line, "-", "Dollars per additional tons" ),concat(_part_desc, ",", wasteType_line, ",", liftsPerContainer_line, ",", rateType_line)))),adHocDescription_line) 
 	
22	pctOfTotalTargetPrice_line	targetPrice_line / monthlyTotalTarget_quote 
 	
23	currentQuoteTotal_quote	grandTotalSell_quote 
 	
24	fixedERFflag_quote	if( ( find(feesToCharge_quote, "Fixed Environment" ) = -1 ), 0, 1 ) 
 	
25	desiredCorePrice_quote	( ( desiredQuoteTotal_quote - erfTotalSell_quote ) - frfTotalSell_quote ) - adminRate_quote 
 	
26	isFRFwaived_quote	if( ( find(feesToCharge_quote, "FRF" ) = -1 ), 1, 0 ) 
 	
27	isERFwaived_quote	if( ( find(feesToCharge_quote, "ERF" ) = -1 ), 1, 0 ) 
 	
28	subtotalPreQuoteDiscounts_quote	sumIf( ( NOT optional_line ), contractValueExtended_line) 
 	
29	frfPremium_quote	if( ( isFRFwaived_quote = 1.0 ), ( 1.0 + query( Fee_Removal_Premiums, premium, feeType = 'FRF' AND minMonthlyRevenue < grandTotalSell_quote AND maxMonthlyRevenue >= grandTotalSell_quote, 0.0 ) ), 1.0 ) 
 	
30	totalStretchPrice_line	if( ( priceType_line = "Large Containers" ), stretchPrice_line, ( ( ( stretchPrice_line + ( erfAmountStretch_line * isERFwaived_quote ) ) + ( frfAmountStretch_line * isFRFwaived_quote ) ) * frfPremium_quote ) ) 
 	
31	totalBasePrice_line	if( ( priceType_line = "Large Containers" ), basePrice_line, ( ( ( basePrice_line + ( erfAmountBase_line * isERFwaived_quote ) ) + ( frfAmountBase_line * isFRFwaived_quote ) ) * frfPremium_quote ) ) 
 	
32	totalFloorPrice_line	if( ( priceType_line = "Large Containers" ), floorPrice_line, ( ( ( floorPrice_line + ( erfAmountFloor_line * isERFwaived_quote ) ) + ( frfAmountFloor_line * isFRFwaived_quote ) ) * frfPremium_quote ) ) 
 	
33	largeMonthlyDisposalFloorPrice_line	if( ( rateType_line = "Disposal" ), ( ( totalFloorPrice_line * estTonsHaul_Line ) * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
34	largeMonthlyTotalDisposalFloor_quote	sum(largeMonthlyDisposalFloorPrice_line) 
 	
35	largeMonthlyHaulFloorPrice_line	if( ( ( rateType_line = "Haul" ) OR ( rateType_line = "Flat" ) ), ( totalFloorPrice_line * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
36	largeMonthlyDisposalBasePrice_line	if( ( rateType_line = "Disposal" ), ( ( totalBasePrice_line * toFloat(estimatedLifts_line) ) * estTonsHaul_Line ), 0.0 ) 
 	
37	largeMonthlyTotalDisposalBase_quote	sum(largeMonthlyDisposalBasePrice_line) 
 	
38	largeMonthlyDisposalStretchPrice_line	if( ( rateType_line = "Disposal" ), ( ( totalStretchPrice_line * toFloat(estimatedLifts_line) ) * estTonsHaul_Line ), 0.0 ) 
 	
39	smallMonthlyTotalFloor_quote	sumIf( ( rateType_line = "Base" ), totalFloorPrice_line) 
 	
40	smallMonthlyTotalBase_quote	sumIf( ( rateType_line = "Base" ), totalBasePrice_line) 
 	
41	largeMonthlyTotalDisposalStretch_quote	sum(largeMonthlyDisposalStretchPrice_line) 
 	
42	smallMonthlyTotalStretch_quote	sumIf( ( rateType_line = "Base" ), totalStretchPrice_line) 
 	
43	largeMonthlyHaulBasePrice_line	if( ( ( rateType_line = "Haul" ) OR ( rateType_line = "Flat" ) ), ( totalBasePrice_line * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
44	largeMonthlyHaulStretchPrice_line	if( ( ( rateType_line = "Haul" ) OR ( rateType_line = "Flat" ) ), ( totalStretchPrice_line * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
45	largeMonthlyHaulBasePrice_quote	sum(largeMonthlyHaulBasePrice_line) 
 	
46	largeMonthlyTotalHaulStretch_quote	sum(largeMonthlyHaulStretchPrice_line) 
 	
47	largeMonthlyTotalHaulFloor_quote	sum(largeMonthlyHaulFloorPrice_line) 
 	
48	renewalTerm_quote	if( ( ( initialTerm_quote = "1" ) OR ( ( initialTerm_quote = "Existing Terms" ) AND ( contractLength_quote = 1 ) ) ), "1", renewalTerm_quote)
 	
49	maxPossibleDiscountAmt_quote	sumIf( ( ( NOT optional_line ) AND ( _model_name = "" ) ), totalPossibleDiscount_line) 
 	
50	percentPossible_line	if( ( ( _model_name = "" ) AND ( maxPossibleDiscountAmt_quote NOT= 0.0 ) ), ( totalPossibleDiscount_line / maxPossibleDiscountAmt_quote ), 0.0 ) 
 	
51	existingSmallContainerRecyOperatingExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( existingWasteCategory_line = "Recycling" ) ), existingOperatingExpensePerMonth_line) 
 	
52	existingSmallContainerSWOperatingExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( existingWasteCategory_line = "Solid Waste" ) ), existingOperatingExpensePerMonth_line) 
 	
53	existingSmallContainerRecyDisposalExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( existingWasteCategory_line = "Recycling" ) ), existingDisposalExpensePerMonth_line) 
 	
54	existingSmallContainerSWeDisposalExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( existingWasteCategory_line = "Solid Waste" ) ), existingDisposalExpensePerMonth_line) 
 	
55	existingSmallContainerDisposalExpense_quote	sumIf( ( rateType_line = "Base" ), existingDisposalExpensePerMonth_line) 
 	
56	existingSmallContainerTotalExpense_quote	sumIf( ( rateType_line = "Base" ), existingCostPerMonthIncludingOverhead_line) 
 	
57	infoProNumberDisplayOnly_quote	if( ( division_quote NOT= "" ), query( Division_Mapping, infoProDivision, lawsonDivisionNumber = division_quote),division_quote) 
 	
58	largeMonthlyRentalStretchPrice_line	if( ( rateType_line = "Rental" ), if( ( billingType_line = "Monthly" ), totalStretchPrice_line, ( totalStretchPrice_line * ( 365.0 / 12.0 ) ) ), 0.0 ) 
 	
59	largeMonthlyTotalRentalStretch_quote	sum(largeMonthlyRentalStretchPrice_line) 
 	
60	largeMonthlyTotalStretch_quote	sum(largeMonthlyTotalHaulStretch_quote,largeMonthlyTotalDisposalStretch_quote,largeMonthlyTotalRentalStretch_quote) 
 	
61	largeMonthlyRentalBasePrice_line	if( ( rateType_line = "Rental" ), if( ( billingType_line = "Monthly" ), totalBasePrice_line, ( totalBasePrice_line * ( 365.0 / 12.0 ) ) ), 0.0 ) 
 	
62	largeMonthlyTotalRentalBase_quote	sum(largeMonthlyRentalBasePrice_line) 
 	
63	largeMonthlyTotalBase_quote	sum(largeMonthlyHaulBasePrice_quote,largeMonthlyTotalDisposalBase_quote,largeMonthlyTotalRentalBase_quote) 
 	
64	largeMonthlyRentalTargetPrice_line	if( ( rateType_line = "Rental" ), if( ( billingType_line = "Monthly" ), totalTargetPrice_line, ( totalTargetPrice_line * ( 365.0 / 12.0 ) ) ), 0.0 ) 
 	
65	largeMonthlyTotalRentalTarget_quote	sum(largeMonthlyRentalTargetPrice_line) 
 	
66	largeMonthlyTotalTarget_quote	sum(largeMonthlyTotalHaulTarget_quote,largeMonthlyTotalDisposalTarget_quote,largeMonthlyTotalRentalTarget_quote) 
 	
67	largeMonthlyRentalFloorPrice_line	if( ( rateType_line = "Rental" ), if( ( billingType_line = "Monthly" ), totalFloorPrice_line, ( totalFloorPrice_line * ( 365.0 / 12.0 ) ) ), 0.0 ) 
 	
68	largeMonthlyTotalRentalFloor_quote	sum(largeMonthlyRentalFloorPrice_line) 
 	
69	largeMonthlyTotalFloor_quote	sum(largeMonthlyTotalHaulFloor_quote,largeMonthlyTotalDisposalFloor_quote,largeMonthlyTotalRentalFloor_quote) 
 	
70	contractValueUnit_line	contractDays_line 
 	
71	largeRecyclingDisposalExpense_quote	sumIf( ( ( rateType_line = "Haul" ) AND ( wasteCategory_line = "Recycling" ) ), disposalExpensePerMonth_line) 
 	
72	largeSolidWasteDisposalExpense_quote	sumIf( ( ( rateType_line = "Haul" ) AND ( wasteCategory_line = "Solid Waste" ) ), disposalExpensePerMonth_line) 
 	
73	smallSolidWasteOperatingExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( wasteCategory_line = "Solid Waste" ) ), operatingExpensePerMonth_line) 
 	
74	smallRecyclingOperatingExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( wasteCategory_line = "Recycling" ) ), operatingExpensePerMonth_line) 
 	
75	smallRecyclingDisposalExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( wasteCategory_line = "Recycling" ) ), disposalExpensePerMonth_line) 
 	
76	smallSolidWasteDisposalExpense_quote	sumIf( ( ( rateType_line = "Base" ) AND ( wasteCategory_line = "Solid Waste" ) ), disposalExpensePerMonth_line) 
 	
77	largeContainerOperatingExpense_quote	sumIf( ( rateType_line = "Haul" ), operatingExpensePerMonth_line) 
 	
78	largeContainerDisposalExpense_quote	sumIf( ( rateType_line = "Haul" ), disposalExpensePerMonth_line) 
 	
79	totalRevenueBefore_quote	sum(currentPriceIncludingFees_line) + existingAdminAmount_quote 
 	
80	largeSolidWasteOperatingExpense_quote	sumIf( ( ( rateType_line = "Haul" ) AND ( wasteCategory_line = "Solid Waste" ) ), operatingExpensePerMonth_line) 
 	
81	conversionRate_quote	query( currencyConversion, conversionRate, outputCurrency = outputCurrency_quote, 1.0 ) 
 	
82	largeRecyclingOperatingExpense_quote	sumIf( ( ( rateType_line = "Haul" ) AND ( wasteCategory_line = "Recycling" ) ), operatingExpensePerMonth_line) 
 	
83	smallExistingFRFAmount_quote	sumIf( ( rateType_line = "Base" ), currentPriceFRFAmount_line) 
 	
84	smallExistingEFRFAmount_quote	sumIf( ( rateType_line = "Base" ), currentPriceERFAmount_line) 
 	
85	smallExistingTotalFee_quote	( smallExistingFRFAmount_quote + smallExistingEFRFAmount_quote ) + existingAdminAmount_quote 
 	
86	monthlyTotalDisposalBase_line	if( ( rateType_line = "Disposal" ), ( totalBasePrice_line * customerTonsPerMonth_line ), 0 ) 
 	
87	smallTotalCurrentPrice_quote	sumIf( ( rateType_line = "Base" ), currentPrice_line) 
 	
88	totalDiscountPercent_quote	if( ( subtotalPreQuoteDiscounts_quote NOT= 0 ), ( ( totalDiscount_quote / subtotalPreQuoteDiscounts_quote ) * 100 ), 0.0 ) 
 	
89	bottomLineDiscountAmt_quote	if( ( bottomLineDiscountType_quote = "Amt" ), bottomLineDiscount_quote, ( ( bottomLineDiscount_quote / 100 ) * subtotalPreQuoteDiscounts_quote ) ) 
 	
90	smallContainerDisposalExpense_quote	sumIf( ( rateType_line = "Base" ), disposalExpensePerMonth_line) 
 	
91	totalDisposalExpenseAfter_quote	sum(smallContainerDisposalExpense_quote,largeContainerDisposalExpense_quote) 
 	
92	smallContainerOperatingExpense_quote	sumIf( ( rateType_line = "Base" ), operatingExpensePerMonth_line) 
 	
93	totalOperatingExpenseAfter_quote	sum(smallContainerOperatingExpense_quote,largeContainerOperatingExpense_quote) 
 	
94	smallContainerTotalExpense_quote	sumIf( ( rateType_line = "Base" ), costPerMonthIncludingOverhead_line) 
 	
95	totalExpense_quote	sumIf( ( ( rateType_line = "Base" ) OR ( rateType_line = "Haul" ) ), costPerMonthIncludingOverhead_line) 
 	
96	includeAdmin_quote	if(contains(feesToCharge_quote, "Admin Fee" ), "Yes", "No" ) 
 	
97	desiredSellPrice_line	if( ( isPartLineItem_line AND ( rateType_line = "Base" ) ), ( ( ( ( ( desiredQuoteTotal_quote - adminRate_quote ) - if( ( ( includeAdmin_quote = "Yes" ) AND ( ( isERFAndFRFChargedOnAdmin_quote = "Yes" ) AND ( includeFRF_quote = "Yes" ) ) ), ( frfRate_quote * adminRate_quote ), 0.0 ) ) - if( ( ( includeERF_quote = "Yes" ) AND ( ( isERFAndFRFChargedOnAdmin_quote = "Yes" ) AND ( includeAdmin_quote = "Yes" ) ) ), if( ( includeFRF_quote = "Yes" ), ( ( adminRate_quote * ( 1 + frfRate_quote ) ) * erfRate_quote ), ( erfRate_quote * adminRate_quote ) ), 0.0 ) ) * if( ( smallMonthlyTotalTarget_quote > 0 ), ( targetPrice_line / smallMonthlyTotalTarget_quote ), 0.0 ) ) / ( ( 1.0 + ( ( ( 1.0 + if( ( isFRFwaived_quote = 1 ), 0.0, frfRate_quote) ) * erfRate_quote ) * if( ( isERFwaived_quote = 1 ), 0, 1 ) ) ) + ( frfRate_quote * if( ( isFRFwaived_quote = 1 ), 0, 1 ) ) ) ), targetPrice_line) 
 	
98	sellPrice_line	if( ( rateType_line = "Installation" ), installationCharge_line,if(priceIncreaseQuote_quote,if( ( adjustment_line > 0 ), if( ( priceIncreaseType_line = "% Increase" ), if( ( currentPrice_line > ( currentPrice_line + ( ( currentPrice_line * adjustment_line ) / 100 ) ) ), currentPrice_line, ( currentPrice_line + ( ( currentPrice_line * adjustment_line ) / 100 ) ) ),if( ( currentPrice_line > adjustment_line ), currentPrice_line,adjustment_line)),targetPrice_line),if(useDesiredPrice_quote,desiredSellPrice_line,if( ( isSellPriceDefaultSet_line = "true" ), sellPriceTemp_line,targetPrice_line)))) 
 	
99	largeMonthlyRentalProposedPrice_line	if( ( rateType_line = "Rental" ), if( ( billingType_line = "Monthly" ), sellPrice_line, ( sellPrice_line * ( 365.0 / 12.0 ) ) ), 0.0 ) 
 	
100	largeMonthlyDisposalProposedPrice_line	if( ( rateType_line = "Disposal" ), ( ( sellPrice_line * toFloat(estimatedLifts_line) ) * estTonsHaul_Line ), 0.0 ) 
 	
101	erfAmountSell_line	( sellPrice_line * ( 1 + if( ( isFRFwaived_quote = 1 ), 0.0, frfRate_quote) ) ) * erfRate_quote 
 	
102	adHocTotal_quote	sumIf( ( rateType_line = "Ad-Hoc" ), sellPrice_line) 
 	
103	largeMonthlyTotalDisposalProposed_quote	sum(largeMonthlyDisposalProposedPrice_line) 
 	
104	largeMonthlyHaulProposedPrice_line	if( ( ( rateType_line = "Haul" ) OR ( rateType_line = "Flat" ) ), ( sellPrice_line * toFloat(estimatedLifts_line) ), 0.0 ) 
 	
105	largeMonthlyTotalHaulProposed_quote	sum(largeMonthlyHaulProposedPrice_line) 
 	
106	largeMonthlyTotalRentalProposed_quote	sum(largeMonthlyRentalProposedPrice_line) 
 	
107	largeMonthlyTotalProposed_quote	sum(largeMonthlyTotalHaulProposed_quote,largeMonthlyTotalDisposalProposed_quote,largeMonthlyTotalRentalProposed_quote) 
 	
108	smallMonthlyTotalProposed_quote	sumIf( ( rateType_line = "Base" ), sellPrice_line) 
 	
109	totalPrice_line	( sellPrice_line + if( ( isFRFwaived_quote = 1 ), 0.0, frfAmountSell_line) ) + if( ( isERFwaived_quote = 1 ), 0.0, erfAmountSell_line) 
 	
110	monthlyTotalDisposalSell_line	if( ( rateType_line = "Disposal" ), ( sellPrice_line * customerTonsPerMonth_line ), 0 ) 
 	
111	changeInPrice_line	sellPrice_line - currentPrice_line 
 	
112	smallChangeInPrice_quote	sumIf( ( rateType_line = "Base" ), changeInPrice_line) 
 	
113	disposalRateForInvoiceSample_line	if( ( rateType_line = "Disposal" ), ( sellPrice_line * estTonsHaul_Line ), 0.0 ) 
 	
114	largeContainerTotalExpense_quote	sumIf( ( rateType_line = "Haul" ), costPerMonthIncludingOverhead_line) 
 	
115	eRFOnAdminFee_quote	if( ( ( includeERF_quote = "Yes" ) AND ( ( isERFAndFRFChargedOnAdmin_quote = "Yes" ) AND ( ( includeAdmin_quote = "Yes" ) AND ( ( smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote ) > 0 ) ) ) ), if( ( includeFRF_quote = "Yes" ), ( ( adminRate_quote * ( 1 + frfRate_quote ) ) * erfRate_quote ), ( erfRate_quote * adminRate_quote ) ), 0.0 ) 
 	
116	fRFOnAdminFee_quote	if( ( ( includeAdmin_quote = "Yes" ) AND ( ( isERFAndFRFChargedOnAdmin_quote = "Yes" ) AND ( ( includeFRF_quote = "Yes" ) AND ( ( largeMonthlyTotalProposed_quote + smallMonthlyTotalProposed_quote ) > 0 ) ) ) ), ( frfRate_quote * adminRate_quote ), 0.0 ) 
 	
117	bottomLineDiscountPercent_quote	if( ( bottomLineDiscountType_quote = "%" ), bottomLineDiscount_quote,if( ( subtotalPreQuoteDiscounts_quote NOT= 0 ), ( ( bottomLineDiscount_quote / subtotalPreQuoteDiscounts_quote ) * 100 ), 0 )) 
 	
118	feeType_line	if( ( ( _model_name = "" ) AND ( ( _part_custom_field8 NOT= "" ) AND ( _part_custom_field1 NOT= "Ad-Hoc" ) ) ), _part_custom_field8,if( ( _part_custom_field1 = "Ad-Hoc" ), adHocFeeType_line, "One-Time" )) 
 	
119	monthlyTotalDisposalStretch_line	if( ( rateType_line = "Disposal" ), ( totalStretchPrice_line * customerTonsPerMonth_line ), 0 ) 
 	
120	subtotalPostQuoteDiscounts_quote	subtotalPreQuoteDiscounts_quote - bottomLineDiscountAmt_quote