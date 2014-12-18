/* 
================================================================================
Name:   monthlyTotalsHTML_quote line default
Author:   
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

//return string parser for Calc_commission
parserResults = calcCommReturnString; //The Return String comes in the format of (Document Number ~ Attribute Name ~ Attribute's Value That's why all indexes selected start index of [2] and increase by multiples of 3 
parserResults = replace(parserResults,"|","~");
attributes = split(parserResults,"~");
parseSmall = attributes[2];		
parseSmallPercent = attributes[5];
parseLarge = attributes[8];	
parseLargePercent = attributes[11];	
parseTotalOTC = attributes[14];	
parseTotal = attributes[17];
parseShowSmall = false;
if(attributes[26]=="true"){parseShowSmall = true;}	
parseShowLarge = false;
if(attributes[29]=="true"){parseShowLarge = true;}

//attributeVar

detailSmall = split(parseSmall,"^_^");
detailSmallPct = split(parseSmallPercent,"^_^");
detailLarge = split(parseLarge,"^_^");
detailLargePct = split(parseLargePercent,"^_^");
detailTotalOTC = split(parseTotalOTC,"^_^");
detailTotal = split(parseTotal,"^_^");
showCommission = false;
if(parseShowSmall == true OR parseShowLarge == true){
	showCommission = true;
}

/******************************** START OF CALCULATIONS IN THIS SCRIPT ****************************/

//Change in frf fee from new to existing. For now, subtracting totalFRFnew with smallExisting frf alone. In future, when large container existing logic is done, we should subtract largeExisting frf amount as well 
frf_change_amount = frfTotalSell_quote - smallExistingFRFAmount_quote;
erf_change_amount = erfTotalSell_quote - smallExistingEFRFAmount_quote;
	
admin_change_amount = adminRate_quote - existingAdminAmount_quote;

total_change_amount_incl_fees = grandTotalSell_quote - totalRevenueBefore_quote;


/******************************** END OF CALCULATIONS IN THIS SCRIPT ****************************/

//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table  class='rs_table_style'  width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels row
						+ "<td align=\"center\"  width=\"20%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Base Rate and Fee(s)</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Cost Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Floor Price</td>"
						+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Average Price</td>"
						+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Target Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Current Price</td>";
}
returnStr = returnStr 	+ "<td align=\"center\"  width=\"10%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Proposed Price</td>";
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\"  width=\"20%\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Change in Price</td>";
}
returnStr = returnStr   + "</tr>";
 //End of labels row
 //Start of Small Containers row
if(commercialExists_quote){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td  >Small Container Base Rates</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(smallMonthlyTotalFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency(smallMonthlyTotalBase_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(smallMonthlyTotalTarget_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(smallMonthlyTotalStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(smallTotalCurrentPrice_quote, "USD") + "</td>";
	}	
	returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(smallMonthlyTotalProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(smallChangeInPrice_quote, "USD") + "</td>";
	}
	returnStr = returnStr	+ "</tr>" ;
}//End of Small Containers row
if(industrialExists_quote){
	//Start of Large Containers Haul row
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td  >Large Container Est. Haul Charges</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalHaulFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyHaulBasePrice_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalHaulTarget_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalHaulStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" + "NA" + "</td>";
	}	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalHaulProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  "NA" + "</td>";
	}
	returnStr = returnStr	+ "</tr>" //End of Large Containers Haul row
							
							+ "<tr class='rs_tr_td_style'> " //Start of Large Containers Disposal row
							+ "<td  >Large Container Est. Disposal Charges</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalDisposalFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency( 	largeMonthlyTotalDisposalBase_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalDisposalTarget_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalDisposalStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  "NA" + "</td>";
	}	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalDisposalProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  "NA" + "</td>";
	}
	returnStr = returnStr	+ "</tr>" //End of Large Containers Disposal row

							+ "<tr class='rs_tr_td_style'>" //Start of Large Containers Rental row
							+ "<td  >Large Container Rental</td>";
	if(NOT(isSalesrep)){
		returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalRentalFloor_quote,"USD") + "</td>";
	}
	returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalRentalBase_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalRentalTarget_quote,"USD") + "</td>"
							+ "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalRentalStretch_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  "NA" + "</td>";
	}	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(largeMonthlyTotalRentalProposed_quote,"USD") + "</td>";
	if(isExistingCustomer){	
		returnStr = returnStr + "<td  align=\"center\" >" +  "NA" + "</td>";
	}
	returnStr = returnStr	+ "</tr>";
} //End of Large Containers Rental row						
returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" //Start of FRF fees
						+ "<td  >FRF </td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(frfTotalSellFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(frfTotalSellBase_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(frfTotalSellTarget_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(frfTotalSellStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(smallExistingFRFAmount_quote, "USD") + "</td>";
}

returnStr = returnStr	+ "<td  align=\"center\" >" +  formatascurrency(frfTotalSell_quote,"USD") + "</td>";
						
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(frf_change_amount, "USD") + "</td>";
}
returnStr = returnStr	+ "</tr>"	//End of FRF fees
						+ "<tr class='rs_tr_td_style'>" //Start of ERF fees
						+ "<td  >ERF </td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(erfTotalSellFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(erfTotalSellBase_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(erfTotalSellTarget_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(erfTotalSellStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(smallExistingEFRFAmount_quote, "USD") + "</td>";
}
returnStr = returnStr	+ "<td  align=\"center\" >" +  formatascurrency(erfTotalSell_quote,"USD") + "</td>";
					
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(erf_change_amount, "USD") + "</td>";
}
returnStr = returnStr	+ "</tr>"	//End of ERF fees
						+ "<tr class='rs_tr_td_style'>" //Start of Admin fees
						+ "<td  >Admin Fee</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(adminRate_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(adminRate_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(adminRate_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(adminRate_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(existingAdminAmount_quote,"USD") + "</td>";
}
returnStr = returnStr	+ "<td  align=\"center\" >" +  formatascurrency(adminRate_quote,"USD") + "</td>";
						
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(admin_change_amount,"USD") + "</td>";
}
returnStr = returnStr	+ "</tr>";	//End of Admin fees	
//Start of Commission Section
if(parseShowSmall == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Small Container Commission on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailSmall[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Small Container Commission Rate on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[0])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[1])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[2])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[3])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailSmallPct[4])*100,2)) + "%" + "</td>"
							+ "</tr>";		
}		
if(parseShowLarge == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Large Container Commission on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailLarge[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Large Container Commission Rate on Recurring Monthly Revenue</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[0])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[1])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[2])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[3])*100,2)) + "%" + "</td>"
							+ "<td align=\"center\" >" + string(round(atof(detailLargePct[4])*100,2)) + "%" + "</td>"
							+ "</tr>";		
}	
if(showCommission == true){
	returnStr = returnStr	+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Commission on One Time Charges</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotalOTC[4]),"USD") + "</td>"
							+ "</tr>"		
							+ "<tr class='rs_tr_td_style'>" 
							+ "<td >Total Estimated Commission</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[0]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[1]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[2]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[3]),"USD") + "</td>"
							+ "<td align=\"center\" >" + formatascurrency(atof(detailTotal[4]),"USD") + "</td>"
							+ "</tr>";		
}				
//End of Commission Section				
returnStr = returnStr 	+ "<tr class=\"totalRow\">" //Start of Grand Totals row
						+ "<td  >Total Estimated Amount</td>";
if(NOT isSalesrep){
	returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(grandTotalFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(grandTotalBase_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(grandTotalTarget_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(grandTotalStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(totalRevenueBefore_quote,"USD") + "</td>";
}						
returnStr = returnStr	+ "<td  align=\"center\" >" +  formatascurrency(grandTotalSell_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(total_change_amount_incl_fees,"USD") + "</td>";
}
returnStr = returnStr	+ "</tr>"//End of Grand Totals row
						+ "<tr style='display:none;'>" //Start of Commissions - Hide this for Pilot
						+ "<td  >Est. Commission</td>";
if(NOT(isSalesrep)){
	returnStr = returnStr + "<td  align=\"center\" >" +  formatascurrency(totalCommissionFloor_quote,"USD") + "</td>";
}
returnStr = returnStr 	+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionBase_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionTarget_quote,"USD") + "</td>"
						+ "<td  align=\"center\" >" +  formatascurrency(totalCommissionStretch_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  "Commissiom Value" + "</td>";
}
returnStr = returnStr   + "<td  align=\"center\" >" +  formatascurrency(totalCommissionSell_quote,"USD") + "</td>";
if(isExistingCustomer){	
	returnStr = returnStr + "<td  align=\"center\" >" +  "Commission Value" + "</td>";
}
returnStr = returnStr + "</tr>" //End of Commissions
						+ "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;