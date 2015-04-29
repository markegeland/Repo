// Document : Quick Start > Line
// Sets ad-hoc configuration items
result = "";
parentAdHocProducts = dict("string[]");
parentAdHocDescriptions = dict("string[]");
parentAdHocFamily = dict("string[]"); 
parentAdHocFeeType = dict("string[]");
MOVE_TO_PRICING_STEP = true;

for line in line_process {
	thisRes = "";
	if(line._model_name == "Ad-Hoc Line Items") {
		adHocName = getconfigattrvalue(line._document_number, "product_adhoc");
		adHocDesc = getconfigattrvalue(line._document_number, "productDescription_adhoc");
		adHocFamily = getconfigattrvalue(line._document_number,"productFamily_adhoc");
		adHocModel = getconfigattrvalue(line._document_number,"adHocModelDescription");
		adHocFeeType = getconfigattrvalue(line._document_number,"feeType_adhoc");
		if(NOT(containskey(parentAdHocProducts,line._document_number))) {
			put(parentAdHocProducts, line._document_number, split(adHocName,"$,$"));
			put(parentAdHocDescriptions, line._document_number, split(adHocDesc,"$,$"));
			put(parentAdHocFamily, line._document_number, split(adHocFamily,"$,$"));
			put(parentAdHocFeeType, line._document_number,split(adhocFeeType,"$,$"));
		}	
		thisRes = thisRes + line._document_number +"~description_line~"+adHocModel+"|";	
	}
	if(line._line_item_comment <> "" AND containskey(parentAdHocProducts,line._parent_doc_number)) {
		tempNames = get(parentAdHocProducts,line._parent_doc_number);
		tempDescs = get(parentAdHocDescriptions,line._parent_doc_number);
		tempFamily = get(parentAdHocFamily,line._parent_doc_number);
		tempFeeType = get(parentAdHocFeeType,line._parent_doc_number);
		if(isnumber(line._line_item_comment)) {
			myAdHocName = tempNames[atoi(line._line_item_comment)];
			myAdHocDesc = tempDescs[atoi(line._line_item_comment)];
			myAdHocFamily = tempFamily[atoi(line._line_item_comment)];
			myFeeType = tempFeeType[atoi(line._line_item_comment)];
			thisRes = thisRes + line._document_number + "~description_line~" + myAdHocDesc + "|";
			thisRes = thisRes + line._document_number + "~feeType_line~" + myFeeType + "|";
			thisRes = thisRes + line._document_number + "~adHocFeeType_line~" + myFeeType + "|";
			thisRes = thisRes + line._document_number + "~contractDays_line~365|";
			
			//thisRes = thisRes + line._document_number + "~productDescription_line~" + myAdHocName + "|";
			
			//print myAdHocName; print myAdHocDesc; print myAdHocFamily;
		}
	}
	
	result = result + thisRes;
}
result = result + "1" + "~moveFromselectServicesToAdjustPricing_quote~" + string(MOVE_TO_PRICING_STEP) + "|";
return result;