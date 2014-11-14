/*
Quote > Add Renewal Line Items
Function: Takes in renewalsHolderString_quote and parses it out and makes webservice 1) add line items 
and 2) set attributes of said line items
Format: Asset1!!Asset2!!
Individual format: value1#value2#value3!!value1#value2#value3!!

*/

//10-25-2012 
//Function will add items passed in to the desired quote
//remove the last "!!" from the string and turn into an array to loop over
itemArray = split(substring(renewalsHolderString_quote,0, len(renewalsHolderString_quote)-2), "!!");
bmiSessionId = _system_user_session_id;
siteName = lower(_system_company_name);
commerceProcess = "quickstart_commerce_process";
quoteProcess = "quote_process";
buyerCompanyName = "HT-QSDev";

NumOfAssets = 0; //will count how many assets we add dynamically
UpdatePartsXML = "";
//prep for addToTransaction API
addTransactionURL = "https://" + lower(siteName)  +".bigmachines.com/bmfsweb/"+ lower(siteName) + "/image/WebServices/addToTransaction.xml";
soapString = urldatabyget(addTransactionURL,"","error");
soapString = replace(soapString, "SESSION_ID", bmiSessionId);
soapString = replace(soapString, "SITE_NAME", lower(siteName));
soapString = replace(soapString, "PROCESS_VAR_NAME", commerceProcess);
soapString = replace(soapString, "TRANSACTION_ID", transactionID_quote);
soapString = replace(soapString, "DOCUMENT_VAR_NAME_MAIN", quoteProcess);

itemsToAdd = "";
//Loop through items that are passed into function to add to quote
for item in itemArray {
	//PartNumber = [0];
	//Quantity = [1];
	
	thisItem = split(item, "#");
itemsToAdd = itemsToAdd + "<bm:partItem>"
		+ "<bm:part>"
			+ thisItem[0]
		+ "</bm:part>"
		+ "<bm:quantity>"
			+ substring(thisItem[1],0,find(thisItem[1],"."))
		+ "</bm:quantity>"
		+ "<bm:price_book_var_name>";
			//+ thisItem[2]
			//+ "_default_price_book"
			if(_quote_process_price_book_var_name <> "") {
				itemsToAdd = itemsToAdd + _quote_process_price_book_var_name;
			}else{
				itemsToAdd = itemsToAdd +"_default_price_book";
			}
		itemsToAdd = itemsToAdd + "</bm:price_book_var_name>"
	+ "</bm:partItem>";
	NumOfAssets = NumOfAssets + 1;	
}					
soapString =  replace(soapString , "ITEMS_TO_ADD", itemsToAdd);

	//print soapString;
	//soap call to add items
	result = "";
	result = urldatabypost("https://"+siteName +".bigmachines.com/v1_0/receiver", soapString, "");
	
//print result;

/* The result of adding line items will tell us the last document number. We can auto assume what the next
document numbers will be by finding this document number that is returned
*/
ldnIndex1 = find(result,"<bm:last_document_number>");
		ldnIndex2 = find(result,">",ldnIndex1);
		ldnIndex3 = find(result,"</bm:last_document_number>");
		ldnStr = substring(result,ldnIndex2+1,ldnIndex3);
		
//print ldnStr;
if (isnumber(ldnStr)) {
	ldn = atoi(ldnStr);
	//print ldn;
	curDocNum = ldn - numOfAssets;
	//print curDocNum;
}
else {
	curDocNum = 2;
}
curSeqNum = 0;


updateTransactionURL = "https://" + lower(buyerCompanyName)  +".bigmachines.com/bmfsweb/"+ lower(buyerCompanyName) + "/image/WebServices/update_transaction.xml";
//print updateTransactionURL;
SoapUpdate = urldatabyget(updateTransactionURL,"","error");
//print "SOAPSTRING: " + soapString;
SoapUpdate = replace(SoapUpdate, "SESSION_ID", bmiSessionId);
SoapUpdate = replace(SoapUpdate, "SITE_NAME", lower(siteName));
SoapUpdate = replace(SoapUpdate, "PROCESS_VAR_NAME", commerceProcess);
SoapUpdate = replace(SoapUpdate, "TRANSACTION_ID", transactionID_quote);
SoapUpdate = replace(SoapUpdate, "BUYER_COMPANY_NAME", buyerCompanyName);
SoapUpdate = replace(SoapUpdate, "SUPPLIER_COMPANY_NAME", _system_supplier_company_name);
SoapUpdate = replace(SoapUpdate, "DOCUMENT_VAR_NAME_MAIN", quoteProcess);
SoapUpdate = replace(SoapUpdate, "ACTION_VAR_NAME", "_update_line_items");
SoapUpdate = replace(SoapUpdate, "LOGIN_NAME", _system_user_login);


		//Get sequence number that is required for updateTransaction API
		 for line in line_process{
		 	if(line._sequence_number > curSeqNum) {
				curSeqNum = line._sequence_number;
			}
		}	
		 for item in itemArray {
		 	//Asset Start Date(previous) = [2]
		 	//Asset End Date(previous)/Auto Set Asset Start Date (new) = [3]
		 	//Asset SFDC ID = [5]
			 curDocNum = curDocNum + 1;
			 curSeqNum = curSeqNum + 1;
			 thisItem = split(item, "#");
		 	UpdatePartsXML = UpdatePartsXML + "<bm:line_process bm:bs_id='" + transactionID_quote + "'  bm:buyer_company_name='" + buyerCompanyName + "' bm:buyer_user_name='superuser' bm:currency_pref='USD' bm:data_type='3' bm:document_name='Line'  bm:document_number='" + string(curDocNum) + "'  bm:document_var_name='line_process' bm:process_var_name='quickstart_commerce_process' bm:supplier_company_name='" + buyerCompanyName + "'>"
				 + "<bm:_document_number>" + string(curDocNum) + "</bm:_document_number>"
				 + "<bm:_sequence_number>" + string(curSeqNum) + "</bm:_sequence_number>"
				 + "<bm:assetStartDate_line>" + thisItem[2]+ " 00:00:00"   + "</bm:assetStartDate_line>"
				 + "<bm:assetEndDate_line>" + thisItem[3]+ " 00:00:00"   + "</bm:assetEndDate_line>"
				  + "<bm:assetId_line>" + thisItem[5] + "</bm:assetId_line>";
				 
				 //Do calculation for base contractEndDate on contractTermMenu_quote
				 if(thisItem[3] <> "") {
					 contractEndDate = strtojavadate(thisItem[3],"yyyy-MM-dd");
					 contractStartDate = adddays(strtojavadate(thisItem[3],"yyyy-MM-dd"),1);
					 if(contractTermMenu_quote == "12") {
					 	contractEndDate = adddays(contractStartDate,365);
					 }
					UpdatePartsXML = UpdatePartsXML + "<bm:contractEndDate_line>" + datetostr(contractEndDate,"yyyy-MM-dd HH:mm:ss")  + "</bm:contractEndDate_line>"
									+ "<bm:contractStartDate_line>" + datetostr(contractStartDate,"yyyy-MM-dd HH:mm:ss")+ "</bm:contractStartDate_line>";
				
				}
				UpdatePartsXML = UpdatePartsXML + "</bm:line_process>";
				
			}
		
	SoapUpdate = replace(SoapUpdate, "LINE_PROCESS", UpdatePartsXML);
	//print SoapUpdate;
	update_result = urldatabypost("https://"+siteName +".bigmachines.com/v1_0/receiver", SoapUpdate, "");
	//print update_result;
return "";