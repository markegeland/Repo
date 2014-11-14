adHocStr = "";
retStr = "";
DELIM = "$,$";
prodDescDict = dict("string[]"); //want a dictionary of arrays
qtyDict = dict("string[]");
priceDict = dict("string[]");
billingTypeDict = dict("string[]");
TempParentDocNum = "0";
i = 0;

for line in line_process{
	if(line._model_variable_name == "adHocLineItems"){
		docNum = line._document_number;
		prodDescArr = split(getconfigattrvalue(docNum, "productDescription_adhoc"), DELIM);
		qtyArr = split(getconfigattrvalue(docNum, "quantity_adhoc"), DELIM);
		priceArr = split(getconfigattrvalue(docNum, "listPrice_adhoc"), DELIM);
		billingTypeArr = split(getconfigattrvalue(docNum, "feeType_adhoc"), DELIM);
		
		//put all arrays into dictionaries with the document_number (effectively the parent doc #) as the key
		put(prodDescDict, docNum, prodDescArr);
		put(qtyDict, docNum, qtyArr);
		put(priceDict, docNum, priceArr);
		put(billingTypeDict, docNum, billingTypeArr);
		
		//Forming a string of Desc, Qty, Price, billType with all the array elements in format <Billing Method> charge of <Price> for <Qty> <Description>.
		
	}
	
	//if the parent doc number is in the dictionary we pull it, take next index for that parent doc
	//if the temp parent does not match the actual parent, set the temp to the actual and set i to 0
	//This keeps everything in order
	if(containskey(prodDescDict, line._parent_doc_number)){
		if(tempParentDocNum <> line._parent_doc_number){
			tempParentDocNum = line._parent_doc_number;
			i = 0;
		}
		
		 
		//pull all of the applicable arrays from the dictionary
		prodDesc = get(prodDescDict, line._parent_doc_number);
		qty = get(qtyDict, line._parent_doc_number);
		price = get(priceDict, line._parent_doc_number);
		billingType = get(billingTypeDict, line._parent_doc_number);
	
		
			thisPrice = line.sellPrice_line;
			adHocStr = adHocStr + "<p>" + billingType[i] + " charge of " + formatascurrency(thisPrice,"USD") + " for " + qty[i] + " " + prodDesc[i] + "</p>";
			i = i + 1;
			
		
		
	}
}
if(adHocStr <> ""){
	adHocStr = "<p>Additional Items:</p>" + adHocStr;
}	
retStr = "1~" + "adHocInfoForDocOutput_quote" + "~" + replace(adHocStr,"&","&amp;") + "|";
return retStr;