/*
================================================================================
Name:   monthlyTotalsHTML_quote line default
Author:   Zach Schlieder
Create date:  11/12/13
Description:  Collects base price, FRF, ERF, admin fee, total price, and commission for floor, base, target, stretch, and sell prices for display on the commerce page
        
Input:   	monthlyTotal: String - Monthly Total attributes for floor, base, target, stretch, and sell
			frfTotalSell: String - FRF Total Sell attributes for floor, base, target, stretch, and sell
			erfTotalSell: String - ERF Total Sell attributes for floor, base, target, stretch, and sell
			adminRate: String - Admin Rate attribute
			grandTotal: String - Grand Total attributes for floor, base, target, stretch, and sell
			totalCommission: String - Total Commission attributes for floor, base, target, stretch, and sell
                    
Output:  	String - Output parsed as HTML by the attribute to display table of attributes

Updates:	11/13/13 - Zach Schlieder - Updated table formatting to match site branding, especially commerce line item grid      
    
=====================================================================================================
*/
//=============================== START - Building HTML String ===============================//
returnStr = "";
isSalesrep = true;
isExistingCustomer = false;

// Cost is seen only by manager profile, exec manager profile, and admin profile
// Manager profile is assigned to users in manager groups; exec manager profile is assigned to users in exec manager groups
// Admin profile is assigned to users with Full Access user type
if(find(lower(_system_user_groups), "manager"  ) <> -1 OR _system_user_type == "FullAccess"){
	isSalesrep = false;
}

if(lower(salesActivity_quote) == "existing customer"){
	isExistingCustomer = true;
}

/******************************** START OF CALCULATIONS IN THIS SCRIPT ****************************/

total_change_in_fees = totalERFFRFAdminSellAmount_quote - smallExistingTotalFee_quote; //includes change in fees of frf, erf and admin
total_change_amount_incl_fees = grandTotalSell_quote - totalRevenueBefore_quote;

//return string parser for Calc_commission
parserResults = calcCommReturnString; //The Return String comes in the format of (Document Number ~ Attribute Name ~ Attribute's Value That's why all indexes selected start index of [2] and increase by multiples of 3 
parserResults = replace(parserResults,"|","~");
attributes = split(parserResults,"~");
parseTotal = attributes[20];	
parseTotalPct = attributes[23];
parseShowSmall = false;
if(attributes[26]=="true"){parseShowSmall = true;}	
parseShowLarge = false;
if(attributes[29]=="true"){parseShowLarge = true;}

summaryTotal = split(ParseTotal,"^_^");
summaryTotalPct = split(ParseTotalPct,"^_^");
showCommission = false;
if(parseShowSmall == true OR parseShowLarge == true){
	showCommission = true;
}

/******************************** END OF CALCULATIONS IN THIS SCRIPT ****************************/

//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table class='rs_table_style' width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels row
						+ "<td align=\"center\"  style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"20%\" >Base Rate and Fee(s)</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Cost Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\"  style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Floor Price</td>"
						+ "<td align=\"center\"  style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Average Price</td>"
						+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Target Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Current Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"10%\" >Proposed Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;' width=\"20%\" >Change in Price</td>";
}
returnStr = returnStr   + "</tr>";
 //End of labels row
 //Start of Small Containers row
if(commercialExists_quote){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Small Containers</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallMonthlyTotalFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td align=\"center\" >" +  formatascurrency(smallMonthlyTotalBase_quote,"USD") + "</td>"
							+ "<td align=\"center\" >" +  formatascurrency(smallMonthlyTotalTarget_quote,"USD") + "</td>"
							+ "<td align=\"center\" >" +  formatascurrency(smallMonthlyTotalStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallTotalCurrentPrice_quote, "USD") + "</td>";
	}	
	returnStr = returnStr 	+ "<td align=\"center\" >" +  formatascurrency(smallMonthlyTotalProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallChangeInPrice_quote, "USD") + "</td>";
	}
	returnStr = returnStr	+ "</tr>";
} //End of Small Containers row
//Start of Large Containers row
if(industrialExists_quote){
	returnStr = returnStr   + "<tr class='rs_tr_td_style'>" 
							+ "<td >Large Containers</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(largeMonthlyTotalFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td align=\"center\" >" +  formatascurrency(largeMonthlyTotalBase_quote,"USD") + "</td>"
							+ "<td align=\"center\" >" +  formatascurrency(largeMonthlyTotalTarget_quote,"USD") + "</td>"
							+ "<td align=\"center\" >" +  formatascurrency(largeMonthlyTotalStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td align=\"center\" >" +  "NA" + "</td>";
	}	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(largeMonthlyTotalProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td align=\"center\" >" +  "NA" + "</td>";
	}
	returnStr = returnStr	+ "</tr>";
} //End of Large Containers row
						
						
returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" //Start of fees
						+ "<td >Fees</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(totalERFFRFAdminFloorAmount_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td align=\"center\" >" +  formatascurrency(totalERFFRFAdminBaseAmount_quote,"USD") + "</td>"
						+ "<td align=\"center\" >" +  formatascurrency(totalERFFRFAdminTargetAmount_quote,"USD") + "</td>"
						+ "<td align=\"center\" >" +  formatascurrency(totalERFFRFAdminStretchAmount_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallExistingTotalFee_quote,"USD")+ "</td>";
}
returnStr = returnStr	+ "<td align=\"center\" >" +  formatascurrency(totalERFFRFAdminSellAmount_quote,"USD") + "</td>";
						
if(isExistingCustomer){	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(total_change_in_fees,"USD") + "</td>";
}
returnStr = returnStr	+ "</tr>";	//End of fees					

returnStr = returnStr	+ "<tr  class='totalRow'>" //Start of Grand Totals row
						+ "<td >Total Estimated Amount</td>";
if(NOT isSalesrep){
	returnStr = returnStr 	+ "<td align=\"center\" >" +  formatascurrency(grandTotalFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td align=\"center\" >" +  formatascurrency(grandTotalBase_quote,"USD") + "</td>"
						+ "<td align=\"center\" >" +  formatascurrency(grandTotalTarget_quote,"USD") + "</td>"
						+ "<td align=\"center\" >" +  formatascurrency(grandTotalStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(totalRevenueBefore_quote,"USD") + "</td>";
}						
returnStr = returnStr	+ "<td align=\"center\" >" +  formatascurrency(grandTotalSell_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(total_change_amount_incl_fees,"USD") + "</td>";
}
returnStr = returnStr	+ "</tr>"//End of Grand Totals row

						+ "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;