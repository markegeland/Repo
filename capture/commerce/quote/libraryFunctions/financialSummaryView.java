/*
================================================================================

Description:  Collects base price, FRF, ERF, admin fee, total price, and commission for floor, base, target, stretch, and sell prices for display on the commerce page
        
Input:   	
                    
Output:  	String - Output parsed as HTML by the attribute to display table of attributes

Updates:	
    
=====================================================================================================
*/
//=============================== START - Building HTML String ===============================//

returnStr = "";
/*isSalesrep = false;
if(find(lower(_system_user_groups), "salesrep" ) <> -1){
	isSalesrep = true;
}*/
isExistingCustomer = false;
if(lower(salesActivity_quote) == "existing customer"){
	isExistingCustomer = true;
}


//Number of columns showing
numOfCols = 0;
if(commercialExists_quote){
	numOfCols = numOfCols + 1;
}
if(industrialExists_quote){
	numOfCols = numOfCols + 1;
}

//ERF & FRF Rate on Admin Rate
adminERFAndFRF = eRFOnAdminFee_quote + fRFOnAdminFee_quote;

adminRatePerCol = 0.0;
if(numOfCols <> 0){
	adminRatePerCol = adminRate_quote/numOfCols;
	adminRatePerCol = round(adminRatePerCol, 2);
	adminERFAndFRFPerCol = adminERFAndFRF/numOfCols;
}
//Calculations done in this script
totalVariance_Revenue = totalMonthlyAmtInclFees_quote - totalRevenueBefore_quote;
total_operating_expense_before = existingSmallContainerSWOperatingExpense_quote + existingSmallContainerRecyOperatingExpense_quote;
total_disposal_expense_before = existingSmallContainerSWeDisposalExpense_quote + existingSmallContainerRecyDisposalExpense_quote;
total_expense_before = total_operating_expense_before + total_disposal_expense_before;
total_variance_expense = totalExpense_quote - total_expense_before;



//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table class='rs_table_style' width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels row
						+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'></td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Small Container</td>";
}
if(industrialExists_quote){
	returnStr = returnStr 	+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Large Container</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Total</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Total - After</td>"
						  + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Total - Before</td>"
						  + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color:#000059;'>Variance</td>";
}
returnStr = returnStr   + "</tr>" //End of labels row
						+ "<tr class='rs_tr_td_style'>" //Start of Revenue row
						+ "<td>Revenue</td>";
if(commercialExists_quote){
	smallMonthlyTotalPriceInclAllFee = smallMonthlyTotalPriceInclFees_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
	returnStr = returnStr + "<td align=\"center\">" +  formatascurrency(smallMonthlyTotalPriceInclAllFee,"USD") + "</td>";
}
if(industrialExists_quote){
	largeMonthlyTotalPriceInclAllFee = largeMonthlyTotalPriceInclFees_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
	returnStr = returnStr   + "<td align=\"center\">" + formatascurrency(largeMonthlyTotalPriceInclAllFee,"USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\">" + formatascurrency(totalMonthlyAmtInclFees_quote, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\">" + formatascurrency(totalMonthlyAmtInclFees_quote, "USD") + "</td>"
						  + "<td align=\"center\">" + formatascurrency(totalRevenueBefore_quote, "USD") + "</td>"
						  + "<td align=\"center\">" + formatascurrency(totalVariance_Revenue, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Revenue row
						+ "<tr  class='rs_tr_td_style'>" //Start of Expense row
						+ "<td >Expense</td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td  align=\"center\">" +  formatascurrency(smallContainerTotalExpense_quote, "USD") + "</td>";
}
if(industrialExists_quote){
	returnStr = returnStr   + "<td  align=\"center\">" + formatascurrency(largeContainerTotalExpense_quote, "USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td  align=\"center\">" + formatascurrency(totalExpense_quote, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td  align=\"center\">" + formatascurrency(totalExpense_quote, "USD") + "</td>"
						  + "<td  align=\"center\">" + formatascurrency(total_expense_before, "USD") + "</td>"
						  + "<td  align=\"center\">" + formatascurrency(total_variance_expense, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Expense row
						+ "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;