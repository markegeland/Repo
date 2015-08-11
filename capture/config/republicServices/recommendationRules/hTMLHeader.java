/* 
Prepared by: Srikar Mamillapalli
Print Quote Header on Config screen similar to what we have on commerce quote page
Get all commerce attributes and display in same layout similar to commerce info header
*/
htmlStr = "";
division_RO_quote = "";
salesActivity_RO_quote = "";
industry_readOnly_quote = "";
segment_readOnly_quote = "";
//accountType_readOnly_quote = "";//GD-accountType_move
quoteNumber_RO_quote = "";
siteName_readOnly_quote = "";
siteAddress_readOnly_quote = "";
siteCity_readOnly_quote = "";
siteState_readOnly_quote = "";
siteZip_readOnly_quote = "";
sitePhone_readOnly_quote = "";
account_readOnly_quote = "";
accountNumber_readOnly_quote = "";
invoiceToAddress_readOnly_quote = "";
fRF_readOnly_quote = "";
eRFreadOnly_quote = "";
admin_readOnly_quote = "";
nRD_readOnly_quote = "";
contID_readOnly_quote = "";
nationalAccount_readOnly_quote = "";
preparedByName_quote = "";
preparedByTitle_quote = "";
preparedByPhone_quote = "";
preparedByEmail_quote = "";

//Get all commerce attributes
/*commerceRecordSet = bmql("SELECT division_RO_quote, salesActivity_RO_quote, industry_readOnly_quote, segment_readOnly_quote, accountType_readOnly_quote, quoteNumber_RO_quote, siteName_readOnly_quote, siteAddress_readOnly_quote, siteCity_readOnly_quote, siteState_readOnly_quote, siteZip_readOnly_quote, sitePhone_readOnly_quote, account_readOnly_quote, accountNumber_readOnly_quote, invoiceToAddress_readOnly_quote, fRF_readOnly_quote, eRFreadOnly_quote, admin_readOnly_quote, nRD_readOnly_quote, contID_readOnly_quote, nationalAccount_readOnly_quote, preparedByName_quote, preparedByTitle_quote, preparedByPhone_quote, preparedByEmail_quote FROM commerce.quote_process");*/

//GD-accountType_move
commerceRecordSet = bmql("SELECT division_RO_quote, salesActivity_RO_quote, industry_readOnly_quote, segment_readOnly_quote, quoteNumber_RO_quote, siteName_readOnly_quote, siteAddress_readOnly_quote, siteCity_readOnly_quote, siteState_readOnly_quote, siteZip_readOnly_quote, sitePhone_readOnly_quote, account_readOnly_quote, accountNumber_readOnly_quote, invoiceToAddress_readOnly_quote, fRF_readOnly_quote, eRFreadOnly_quote, admin_readOnly_quote, nRD_readOnly_quote, contID_readOnly_quote, nationalAccount_readOnly_quote, preparedByName_quote, preparedByTitle_quote, preparedByPhone_quote, preparedByEmail_quote FROM commerce.quote_process");

for each in commerceRecordSet{
	division_RO_quote = get(each, "division_RO_quote");
	salesActivity_RO_quote= get(each, "salesActivity_RO_quote"); 
	industry_readOnly_quote= get(each, "industry_readOnly_quote"); 
	segment_readOnly_quote= get(each, "segment_readOnly_quote"); 
	//accountType_readOnly_quote= get(each, "accountType_readOnly_quote"); //GD-accountType_move
	quoteNumber_RO_quote= get(each, "quoteNumber_RO_quote"); 
	siteName_readOnly_quote= get(each, "siteName_readOnly_quote"); 
	siteAddress_readOnly_quote= get(each, "siteAddress_readOnly_quote"); 
	siteCity_readOnly_quote= get(each, "siteCity_readOnly_quote"); 
	siteState_readOnly_quote= get(each, "siteState_readOnly_quote"); 
	siteZip_readOnly_quote= get(each, "siteZip_readOnly_quote"); 
	sitePhone_readOnly_quote= get(each, "sitePhone_readOnly_quote"); 
	account_readOnly_quote= get(each, "account_readOnly_quote"); 
	accountNumber_readOnly_quote= get(each, "accountNumber_readOnly_quote"); 
	invoiceToAddress_readOnly_quote= get(each, "invoiceToAddress_readOnly_quote"); 
	fRF_readOnly_quote= get(each, "fRF_readOnly_quote"); 
	eRFreadOnly_quote= get(each, "eRFreadOnly_quote"); 
	admin_readOnly_quote= get(each, "admin_readOnly_quote"); 
	nRD_readOnly_quote= get(each, "nRD_readOnly_quote"); 
	contID_readOnly_quote= get(each, "contID_readOnly_quote"); 
	nationalAccount_readOnly_quote= get(each, "nationalAccount_readOnly_quote"); 
	preparedByName_quote= get(each, "preparedByName_quote"); 
	preparedByTitle_quote= get(each, "preparedByTitle_quote"); 
	preparedByPhone_quote= get(each, "preparedByPhone_quote"); 
	preparedByEmail_quote= get(each, "preparedByEmail_quote"); 
}

//col1LabelsArray = string[]{"Division", "Sales Activity", "Industry", "Segment", "Account Type", "Quote Number"};
//GD-accountType_move
col1LabelsArray = string[]{"Division", "Sales Activity", "Industry", "Segment", "Quote Number", ""};

/*col1VariablesArray = string[]{division_RO_quote, salesActivity_RO_quote, industry_readOnly_quote, segment_readOnly_quote, accountType_readOnly_quote, quoteNumber_RO_quote};*/

//GD-accountType_move
col1VariablesArray = string[]{division_RO_quote, salesActivity_RO_quote, industry_readOnly_quote, segment_readOnly_quote, quoteNumber_RO_quote, ""};
col2LabelsArray = string[]{"Site Name", "Address", "City", "State", "Zip Code", "Phone #"};
col2VariablesArray = string[]{siteName_readOnly_quote, siteAddress_readOnly_quote, siteCity_readOnly_quote, siteState_readOnly_quote, siteZip_readOnly_quote, sitePhone_readOnly_quote};
col3LabelsArray = string[]{"Account", "Account Number", "NRD", "National Account", "Invoice To", ""};// Add empty string to make all arrays same size
col3VariablesArray = string[]{account_readOnly_quote, accountNumber_readOnly_quote, nRD_readOnly_quote, nationalAccount_readOnly_quote, invoiceToAddress_readOnly_quote, ""};// Add empty string to make all arrays same size
col4LabelsArray = string[]{"FRF", "ERF", "Admin", "Cont ID", "", ""};
col4VariablesArray = string[]{fRF_readOnly_quote, eRFreadOnly_quote, admin_readOnly_quote, contID_readOnly_quote, "", ""};
col5LabelsArray = string[]{"Prepared By", "Title", "Phone", "Email", "", ""}; // Add empty string to make all arrays same size
col5VariablesArray = string[]{preparedByName_quote, preparedByTitle_quote, preparedByPhone_quote, preparedByEmail_quote, "", ""}; // Add empty string to make all arrays same size

columnsArray = integer[]{sizeofarray(col1LabelsArray), sizeofarray(col2LabelsArray), sizeofarray(col3LabelsArray), sizeofarray(col4LabelsArray), sizeofarray(col5LabelsArray)};
tempArray = integer[max(columnsArray)];

//Define column widths
labelColumnWidth = 10;
fieldColumnWidth = 15;
if(lower(salesActivity_RO_quote) == "existing customer"){
	labelColumnWidth = 8;
	fieldColumnWidth = 12;
}

//Define HTML
htmlStr = htmlStr + "<table>";
//Column 1 does not need left padding
COL1BEGINHEADERTD = "<td nowrap class='config-form-item' style='padding-left:0px;'><div class='infobar'><label>";
BEGINHEADERTD = "<td nowrap class='config-form-item'><div class='infobar'><label>";
ENDHEADERTD = "</label></div></td>";
BEGINTD = "<td class='config-form-field-item'><span class='readonly-wrapper' style='font-size:11px;'>";
ENDTD = "</span></td>";

//These are number of possible rows in header
i = 0;
for eachRow in tempArray{
	//Each Row
	htmlStr = htmlStr + "<tr>";	
		//Column 1
	htmlStr = htmlStr + COL1BEGINHEADERTD + col1LabelsArray[i] + ENDHEADERTD + BEGINTD + col1VariablesArray[i] + ENDTD 
					//Column 2
					  + BEGINHEADERTD + col2LabelsArray[i] + ENDHEADERTD + BEGINTD + col2VariablesArray[i] + ENDTD; 
					  ////Column 3
					  if(lower(salesActivity_RO_quote) == "existing customer"){
						htmlStr = htmlStr + BEGINHEADERTD + col3LabelsArray[i] + ENDHEADERTD + BEGINTD + col3VariablesArray[i] + ENDTD;
					  }
					  //Column 4
	htmlStr = htmlStr + BEGINHEADERTD + col4LabelsArray[i] + ENDHEADERTD + BEGINTD + col4VariablesArray[i] + ENDTD
					//Column 5	
					  + BEGINHEADERTD + col5LabelsArray[i] + ENDHEADERTD + BEGINTD + col5VariablesArray[i] + ENDTD; 
	htmlStr = htmlStr + "</tr>";	
	//End of Each Row
	i = i + 1;
}
htmlStr = htmlStr + "</table>";
return htmlStr;