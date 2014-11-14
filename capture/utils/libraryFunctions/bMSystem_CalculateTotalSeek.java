/*DEBUG = false;

if(DEBUG) {
	print QuoteFields;
	print LineItems;
}

// Dictionary Keys composed as following: {DOC_NUM}{FIELD_SEP}{VAR_NAME}
FIELD_SEP = "~";
// Floats considered "equal" if the difference is less than EPSILON
EPSILON = 0.000001;

QUOTE_DOC_NUM = "1";
NUM_DECIMAL = 2;

// Error codes
ERROR_MAX = "ERROR_MAX";
ERROR_MIN = "ERROR_MIN";
ERROR_NOTOTAL = "ERROR_NOTOTAL";
ERROR_NEG = "ERROR_NEG";

// Desired Total is required in order to perform Calculate Discounts
desiredTotalStr = get(QuoteFields,QUOTE_DOC_NUM + FIELD_SEP + "desiredTotal_quote");
if(isnull(desiredTotalStr) or not isnumber(desiredTotalStr)) {
	//errorMsg = "Please enter a desired total in order to Calculate Discounts";
	errorMsg = ERROR_NOTOTAL;
	if(DEBUG) {
		print errorMsg;
	}
	return errorMsg;
}
desiredTotal = atof(desiredTotalStr);
if(DEBUG) {
	print "DESIRED TOTAL: " + desiredTotalStr;
}

// Desired Total is negative
if(desiredTotal < 0) {
	//errorMsg = "Desired Total: US Dollar: Invalid Amount value found";
	errorMsg = ERROR_NEG;
	if(DEBUG) {
		print errorMsg;
	}
	return errorMsg;
}

// Initialize totals
maxDiscountTotal = 0;
hardMaxDiscountTotal = 0;
minQty = 0;
currentTotal = 0.0;

// Non-discountable Categories specified as Cat1~Cat2~Cat3 according to BigMachines multi-select field standard
nonDiscountableCategories = String[];
nonDiscCategoriesDefined = false;
nonDiscountableCategoryKey = QUOTE_DOC_NUM + FIELD_SEP + "nondiscountableCategories_quote";
if(DEBUG) { print nonDiscountableCategoryKey; }
if(containskey(QuoteFields,nonDiscountableCategoryKey)) {
	nonDiscountableCategoriesStr = get(QuoteFields,nonDiscountableCategoryKey);
	if(not isnull(nonDiscountableCategoriesStr)) {
		nonDiscountableCategories = split(get(QuoteFields,nonDiscountableCategoryKey),"~");
		nonDiscCategoriesDefined = true;
	}
	
}
if(DEBUG and nonDiscCategoriesDefined) {
	print "Non-discountable categories: " + join(nonDiscountableCategories,",");
}

// Initialize Misc Charge fields (Post-Discount Charges)
miscCharges = String[]{"miscChargePostDiscount1_quote","miscChargePostDiscount2_quote","miscChargePostDiscount3_quote"};
for charge in miscCharges {
	key = QUOTE_DOC_NUM + FIELD_SEP + charge;
	if(containskey(QuoteFields,key)) {
		chargeStr = get(QuoteFields,key);
		if(not isnull(chargeStr) and isnumber(chargeStr)) {
			currentTotal = currentTotal + atof(chargeStr);
		}
	}
}

discKeys = String[];
discTypeKeys = String[];
lineItemListPrice = Float[];
qty = Integer[];
maxDisct = Float[];
categories = String[];
optional = String[];
quoteOrLine = String[];

// Use the max doc num specified in input fields to save time on iterations
maxDocNumInt = 1000;
if(containskey(QuoteFields,"1~maxDocNum")) {
	maxDocNumInt = atoi(get(QuoteFields,"1~maxDocNum"));
	if(DEBUG) {
		print "Max Doc Num: " + string(maxDocNumInt);
	}
}

// term length
termLengthMonths = 1;
termLengthKey = QUOTE_DOC_NUM + FIELD_SEP + "contractTermMenu_quote";
if(containskey(QuoteFields,termLengthKey)) {
	termLength = get(QuoteFields,termLengthKey);
	if(isnumber(termLength)) {
		termLengthMonths = integer(atof(termLength));
		if(DEBUG) {
			print "Term Length: " + string(termLengthMonths) + " months";
		}
	}
}

// accumulate totals/set up dictionaries for regular line items
maxDocNum = range(maxDocNumInt);
for eaDocNum in maxDocNum {
	docNumString = string(eaDocNum + 1);
	listKey = docNumString + FIELD_SEP + "listPrice_line";
	qtyKey = docNumString + FIELD_SEP + "_price_quantity";
	maxDiscKey = docNumString + FIELD_SEP + "maxDiscountPercent_line";
	categoryKey = docNumString + FIELD_SEP + "category_line";
	optionalKey = docNumString + FIELD_SEP + "optional_line";
	feeTypeKey = docNumString + FIELD_SEP + "feeType_line";
	if(containskey(LineItems,listKey)) {
		listPriceStr = get(LineItems,listKey);
		listPriceNum = 0.0;
		qtyNum = 1;
		maxDiscount = 100.0;
		

		if(isnumber(listPriceStr)) {
			

			listPriceNum = atof(listPriceStr);
		}

		if(containskey(LineItems,qtyKey)) {
			qtyStr = get(LineItems,qtyKey);
			if(isnumber(qtyStr)) {
				qtyNum = integer(atof(qtyStr));

				// term length acts like a multiplier, similar to quantity
				if(containskey(LineItems,feeTypeKey)) {
					feeType = get(LineItems,feeTypeKey);
					if(feeType == "Monthly") {
						qtyNum = qtyNum * termLengthMonths;
					}
				}

				if(minQty == 0 or qtyNum < minQty) {
					minQty = qtyNum;
				}
			}
		}
		maxDiscountTotalNum = listPriceNum * qtyNum;
		hardMaxDiscount = maxDiscountTotalNum;
		
		if(containskey(LineItems,maxDiscKey)) {
			maxDiscStr = get(LineItems,maxDiscKey);
			if(isnumber(maxDiscStr)) {
				maxDiscount = atof(maxDiscStr);
				if(maxDiscount == 0) {
					// "0" means no limit
					maxDiscount = 100.0;
				}
			}
			maxDiscountTotalNum = maxDiscount / 100 * listPriceNum * qtyNum;
			if(DEBUG) {
				print docNumString + ": Max Discount: " + string(maxDiscountTotalNum);
			}
			//hardMaxDiscount = maxDiscountTotalNum;			
		}
		category = "";
		if(containskey(LineItems,categoryKey)) {
			category = get(LineItems,categoryKey);
			//if(DEBUG) { print "Category: " + category; }
			if(nonDiscCategoriesDefined and not isnull(category) and category <> "" and findinarray(nonDiscountableCategories,category) <> -1) {
				if(DEBUG) {
					print "Category: " + category + " not discountable!";
				}
				hardMaxDiscount = 0;
			}
		}
		opt = "false";
		if(containskey(LineItems,optionalKey)) {
			opt = get(LineItems,optionalKey);
			if(opt == "true") {
				hardMaxDiscount = 0.0;
				listPriceNum = 0.0;
				if(DEBUG) {
					print "Optional items not discountable!";
				}
			}
		}
		
		maxDiscountTotal = maxDiscountTotal + maxDiscountTotalNum;
		if(DEBUG) {
			print "Hard Max Discount: " + string(hardMaxDiscount);
		}
		hardMaxDiscountTotal = hardMaxDiscountTotal + hardMaxDiscount;
		currentTotal = currentTotal + listPriceNum * qtyNum;
		append(lineItemListPrice,listPriceNum);
		append(qty,qtyNum);
		discKey = docNumString + FIELD_SEP + "discount_line";
		append(discKeys,discKey);
		put(LineItems,discKey,"0");
		append(discTypeKeys,docNumString + FIELD_SEP + "discountType_line");
		append(maxDisct,maxDiscount);
		append(categories,category);
		append(optional,opt);
		append(quoteOrLine,"Line");
	}
}

// Ad Hoc Items
NUM_ADHOC_ITEMS = 6;
adHocItemsRange = range(NUM_ADHOC_ITEMS);
for adHocIndex in adHocItemsRange {
	adHocIndexStr = string(adHocIndex + 1);
	listKey = QUOTE_DOC_NUM + FIELD_SEP + "miscChargeList" + adHocIndexStr + "_quote";
	if(containskey(QuoteFields,listKey)) {
		listStr = get(QuoteFields,listKey);
		if(isnumber(listStr)) {
			listPriceNum = atof(listStr);
			qtyNum = 1;
			maxDiscount = 100.0;
			opt = "false";
			qtyKey = QUOTE_DOC_NUM + FIELD_SEP + "miscChargeQty" + adHocIndexStr + "_quote";

			if(containskey(QuoteFields,qtyKey)) {
				qtyStr = get(QuoteFields,qtyKey);
				if(isnumber(qtyStr)) {
					qtyNum = integer(atof(qtyStr));
					discKey = QUOTE_DOC_NUM + FIELD_SEP + "miscChargeDiscount" + adHocIndexStr + "_quote";

					feeTypeKey = QUOTE_DOC_NUM + FIELD_SEP + "miscChargeFeeType" + adHocIndexStr + "_quote";
					if(containskey(QuoteFields,feeTypeKey)) {
						feeType = get(QuoteFields,feeTypeKey);
						if(feeType == "Monthly") {
							qtyNum = qtyNum * termLengthMonths;
						}
					}

					maxDiscountTotalNum = listPriceNum * qtyNum;
					hardMaxDiscount = maxDiscountTotalNum;

					optKey = QUOTE_DOC_NUM + FIELD_SEP + "miscOptional" + adHocIndexStr + "_quote";
					if(containskey(QuoteFields,optKey)) {
						opt = get(QuoteFields,optKey);
						if(opt == "true") {
							hardMaxDiscount = 0;
							listPriceNum = 0.0;
						}
					}

					maxDiscountTotal = maxDiscountTotal + maxDiscountTotalNum;
					hardMaxDiscountTotal = hardMaxDiscountTotal + hardMaxDiscount;
					currentTotal = currentTotal + listPriceNum * qtyNum;

					append(lineItemListPrice,listPriceNum);
					append(qty,qtyNum);
					append(discKeys,discKey);
					put(QuoteFields,discKey,"0");
					append(discTypeKeys,QUOTE_DOC_NUM + FIELD_SEP + "miscChargeDiscountType" + adHocIndexStr + "_quote");
					append(maxDisct,maxDiscount);
					append(categories,"");
					append(optional,opt);
					append(quoteOrLine,"Quote");
				}
			}
			
		}
	}
}

if(DEBUG) { print quoteOrLine; }

listTotal = currentTotal;
smallestDiscount = 0.01 * minQty;
if(DEBUG) {
	print "INITIAL TOTAL: " + string(currentTotal);
	print "DISCOUNTABLE: " + string(maxDiscountTotal);
	print "HARD MAX TOTAL: " + string(hardMaxDiscountTotal);
	print "SMALLEST POSSIBLE DISCOUNT: " + string(smallestDiscount);
}

put(QuoteFields,QUOTE_DOC_NUM + FIELD_SEP + "maximumTotal_quote",string(listTotal));
minTotal = listTotal - hardMaxDiscountTotal;
put(QuoteFields,QUOTE_DOC_NUM + FIELD_SEP + "minimumTotal_quote",string(minTotal));

if(listTotal < desiredTotal) {
	//totalStr = formatascurrency(listTotal,currencyCode);
	//errorMsg = "Based on the total list value of your quote, the maximum total quote value is " + totalStr + ". Please adjust the Desired Total to be less than or equal to " + totalStr + " and try again.";
	errorMsg = ERROR_MAX;
	if(DEBUG) {
		print errorMsg;
	}
	return errorMsg;
} elif (desiredTotal < minTotal) {
	//maxTotalStr = formatascurrency(listTotal-hardMaxDiscountTotal,currencyCode);
	//errorMsg = "Based on the total discountable value of your items, the minimum total quote value is " + maxTotalStr + ". Please adjust the Desired Total to be greater than or equal to " + maxTotalStr + " and try again.";
	errorMsg = ERROR_MIN;
	if(DEBUG) {
		print errorMsg;
	}
	return errorMsg;
}

// main loop - determine discounts
lineItemRange = range(sizeofarray(lineItemListPrice));
iterRange = range(1000);
for iter in iterRange {
	// break when current total equals desired total
	if(fabs(currentTotal - desiredTotal) < EPSILON) {
		if(DEBUG) { print "SUCCESS!"; }
		break;
	}
	if(DEBUG) { 
		print "Remaining Total: " + string(desiredTotal) + "\nCurrent Total: " + string(currentTotal) + "\nMax Discount: " + string(maxDiscountTotal); 
	}

	// discount all items evenly
	discountPct = 0;
	if(listTotal <> 0) {
		discountPct = (currentTotal - desiredTotal) / listTotal * 100.0;	
	}
	totalAdditionalDiscount = 0.0;
	if(DEBUG) {
		print "DISC %: " + string(discountPct);
	}
	for i in lineItemRange {
		discKey = discKeys[i];
		discTypeKey = discTypeKeys[i];
		if(DEBUG) { print "-----------\n" + discKey; }
		lineItemCategory = categories[i];
		
		skipLine = false;
		if(nonDiscCategoriesDefined and not isnull(lineItemCategory) and lineItemCategory <> "" and findinarray(nonDiscountableCategories,lineItemCategory) <> -1) {
			if(DEBUG) {
				print "Category " + lineItemCategory + " not discountable";
			}
			skipLine = true;
		}
		if(optional[i] == "true") {
			skipLine = true;
		}

		listPrice = lineItemListPrice[i];

		if(listPrice == 0) {
			skipLine = true;
		}

		if(skipLine) {
			if(quoteOrLine[i] == "Line") {
				put(LineItems,discKey,"0");
			} else {
				put(QuoteFields,discKey,"0");
			}
			continue;
		}
		
		curDiscLevel = 0.0;
		discDict = LineItems;
		if(quoteOrLine[i] == "Line") {
			if(containskey(LineItems,discKey)) {
				curDiscStr = get(LineItems,discKey);
				if(isnumber(curDiscStr)) {
					curDiscLevel = atof(curDiscStr);
				}
			}
		} else {
			if(containskey(QuoteFields,discKey)) {
				curDiscStr = get(QuoteFields,discKey);
				if(isnumber(curDiscStr)) {
					curDiscLevel = atof(curDiscStr);
				}
			}
		}
		
		if(DEBUG) { print "CUR DISC LEVEL: " + string(curDiscLevel); }

		// add additional discount amt
		additionalDiscount = round(discountPct / 100.0 * listPrice,NUM_DECIMAL);
		if(additionalDiscount == 0 and discountPct > 0 and currentTotal - desiredTotal > smallestDiscount / 2) {
			additionalDiscount = 0.01;
		}
		
		newDiscLevel = 0;
		if(listPrice <> 0) {
			newDiscLevel = (curDiscLevel + additionalDiscount) / listPrice * 100.0;
		}
		maxDiscountNum = maxDisct[i] / 100.0 * listPrice;
		if(DEBUG) { print "NEW DISC LEVEL: " + string(newDiscLevel); }
		// Allowed to exceed maxDiscount only if maxDiscount level reached already
		if((maxDiscountTotal > 0 or newDiscLevel >= 100) and newDiscLevel > maxDisct[i]) {
			if(DEBUG) { print "MAX DISCT: " + string(maxDisct[i]); }
			additionalDiscount = round(maxDisct[i] / 100.0 * listPrice - curDiscLevel,NUM_DECIMAL);
			if(additionalDiscount < 0) {
				additionalDiscount = 0.0;
			}
		}
		
		additionalDiscountAmt = round(additionalDiscount * qty[i],NUM_DECIMAL);
		totalAdditionalDiscount = totalAdditionalDiscount + additionalDiscountAmt;
		if(DEBUG) { print "Adding Discount: " + string(additionalDiscountAmt); }
		totalDiscount = round(curDiscLevel + additionalDiscount,NUM_DECIMAL);
		currentTotal = round(currentTotal - additionalDiscountAmt,NUM_DECIMAL);
		
		if(quoteOrLine[i] == "Line") {
			put(LineItems,discKey,string(totalDiscount));
			put(LineItems,discTypeKey,"Amt");
		} else {
			put(QuoteFields,discKey,string(totalDiscount));
			put(QuoteFields,discTypeKey,"Amt");
		}
		
	}
	maxDiscountTotal = round(maxDiscountTotal - totalAdditionalDiscount,NUM_DECIMAL);
	if(DEBUG) {
		print "TOTAL NET: " + string(currentTotal);
		print "==================";
	}
}

// Zero Quote-level fields
fieldsToZero = String[]{"customGroup1Discount_quote","customGroup2Discount_quote","customGroup3Discount_quote","customGroup4Discount_quote","customGroup5Discount_quote","customGroup6Discount_quote","bottomLineDiscountPercent_quote"};
for field in fieldsToZero {
	put(QuoteFields,QUOTE_DOC_NUM + FIELD_SEP + field,"0");
}

if(DEBUG){
	print LineItems;
}*/

return "";