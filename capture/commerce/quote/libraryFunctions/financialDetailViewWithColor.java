/*
====================================================================================================

Description:  Collects base price, FRF, ERF, admin fee, total price, and commission for floor, base, 
              target, stretch, and sell prices for display on the commerce page
        
Input:   	
                    
Output:  	String - Output parsed as HTML by the attribute to display table of attributes

Updates:	20140916 John (Republic) - fixed margin before % calculation
			201401	 Julie (Oracle) - Added Red logic
			20140114 Julie (Oracle) - Added Direct Cost row
			
    
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
	if(smallSolidWasteRevenue_quote <> 0.0){
		numOfCols = numOfCols + 1;
	}
	if(smallRecyclingRevenue_quote <> 0.0){
		numOfCols = numOfCols + 1; //Solid waste and recycling column
	}
}
if(industrialExists_quote){
	if(largeSolidWasteRevenue_quote <> 0.0){
		numOfCols = numOfCols + 1; //Solid waste and recycling column
	}
	if(largeRecyclingRevenue_quote <> 0.0){	
		numOfCols = numOfCols + 1; //Solid waste and recycling column
	}
}

//ERF & FRF Rate on Admin Rate
adminERFAndFRF = eRFOnAdminFee_quote + fRFOnAdminFee_quote;

adminERFAndFRFPerCol = 0.0;
adminRatePerCol = 0.0;
if(numOfCols <> 0){
	adminRatePerCol = adminRate_quote/numOfCols;
	adminERFAndFRFPerCol = adminERFAndFRF/numOfCols;
	//adminRatePerCol = round(adminRatePerCol, 2);
}

smallSolidWasteRevenueWithAdmin = 0.0;
smallRecyclingRevenueWithAdmin = 0.0;
largeSolidWasteRevenueWithAdmin = 0.0;
largeRecyclingRevenueWithAdmin = 0.0;
smallSolidWasteNetRevenueWithAdmin = 0.0;
smallRecyclingNetRevenueWithAdmin = 0.0;
largeSolidWasteNetRevenueWithAdmin = 0.0;
largeRecyclingNetRevenueWithAdmin = 0.0;


if(smallSolidWasteNetRevenue_quote <> 0.0){
	smallSolidWasteNetRevenueWithAdmin = smallSolidWasteNetRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}

if(smallRecyclingNetRevenue_quote <> 0.0){
	smallRecyclingNetRevenueWithAdmin = smallRecyclingNetRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}


if(largeSolidWasteNetRevenue_quote <> 0.0){
	largeSolidWasteNetRevenueWithAdmin = largeSolidWasteNetRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}

if(largeRecyclingNetRevenue_quote <> 0.0){
	largeRecyclingNetRevenueWithAdmin = largeRecyclingNetRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}

if(smallSolidWasteRevenue_quote <> 0.0){
	smallSolidWasteRevenueWithAdmin = smallSolidWasteRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}
if(smallRecyclingRevenue_quote <> 0.0){
	smallRecyclingRevenueWithAdmin = smallRecyclingRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}
if(largeSolidWasteRevenue_quote <> 0.0){
	largeSolidWasteRevenueWithAdmin = largeSolidWasteRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}
if(largeRecyclingRevenue_quote <> 0.0){	
	largeRecyclingRevenueWithAdmin = largeRecyclingRevenue_quote + adminRatePerCol; //SR 3-9476835591 + adminERFAndFRFPerCol;
}

/*****   START OF Calculations done in this script *****/
totalVariance_Revenue = totalMonthlyAmtInclFees_quote - totalRevenueBefore_quote;
total_NetRevenue_After = smallSolidWasteNetRevenue_quote + smallRecyclingNetRevenue_quote + largeSolidWasteNetRevenue_quote + largeRecyclingNetRevenue_quote + adminRate_quote; //J.Felberg + adminERFAndFRFPerCol; //adminRate must be separaetly added because individual container revues don't contain adminRate because it is quote level and not model level fees

//OpIncome Calc
//small_SolidWaste_OpIncome = smallSolidWasteRevenue_quote - (smallSolidWasteDisposalExpense_quote + smallSolidWasteOperatingExpense_quote);
small_SolidWaste_OpIncome = smallSolidWasteRevenueWithAdmin - (smallSolidWasteDisposalExpense_quote + smallSolidWasteOperatingExpense_quote);
//small_Recycling_OpIncome = smallRecyclingRevenue_quote - (smallRecyclingDisposalExpense_quote + smallRecyclingOperatingExpense_quote);
small_Recycling_OpIncome = smallRecyclingRevenueWithAdmin - (smallRecyclingDisposalExpense_quote + smallRecyclingOperatingExpense_quote);

large_SolidWaste_OpIncome = largeSolidWasteRevenueWithAdmin - (largeSolidWasteDisposalExpense_quote + largeSolidWasteOperatingExpense_quote);
large_Recycling_OpIncome = largeRecyclingRevenueWithAdmin - (largeRecyclingDisposalExpense_quote + largeRecyclingOperatingExpense_quote);

total_OpIncome_After = totalMonthlyAmtInclFees_quote - (totalDisposalExpenseAfter_quote + totalOperatingExpenseAfter_quote);

/* Start of Margin Percent Calc */
small_SolidWaste_Margin_Percent = 0.0;
if(smallSolidWasteRevenueWithAdmin <> 0.0){
	small_SolidWaste_Margin_Percent = (small_SolidWaste_OpIncome/smallSolidWasteRevenueWithAdmin) * 100.0;
}
 
small_Recycling_Margin_Percent = 0.0;
if(smallRecyclingRevenueWithAdmin <> 0.0){
	small_Recycling_Margin_Percent = (small_Recycling_OpIncome/smallRecyclingRevenueWithAdmin) * 100.0;
}

large_SolidWaste_Margin_Percent = 0.0;
if(largeSolidWasteRevenueWithAdmin <> 0.0){
	large_SolidWaste_Margin_Percent = (large_SolidWaste_OpIncome/largeSolidWasteRevenueWithAdmin) * 100.0;
}
large_Recycling_Margin_Percent = 0.0;
if(largeRecyclingRevenueWithAdmin <> 0.0){
	large_Recycling_Margin_Percent = (large_Recycling_OpIncome/largeRecyclingRevenueWithAdmin) * 100.0;
}

total_Margin_Percent_After = 0.0;
if(totalMonthlyAmtInclFees_quote <> 0.0){
	total_Margin_Percent_After = (total_OpIncome_After/totalMonthlyAmtInclFees_quote) * 100.0;
}
/* End of Margin Percent Calc */

/* Start of Existing Calc */

//In future, we should add largeContainer existing operating expenses as well
total_operating_expense_before = existingSmallContainerSWOperatingExpense_quote + existingSmallContainerRecyOperatingExpense_quote;
total_operating_variance = totalOperatingExpenseAfter_quote - total_operating_expense_before;

total_disposal_expense_before = existingSmallContainerSWeDisposalExpense_quote + existingSmallContainerRecyDisposalExpense_quote;
total_disposal_variance = totalDisposalExpenseAfter_quote - total_disposal_expense_before;

total_OpIncome_Before = totalRevenueBefore_quote - (total_disposal_expense_before + total_operating_expense_before);

total_Margin_Percent_Before = 0.0;
if(totalRevenueBefore_quote <> 0.0){
	total_Margin_Percent_Before =  (total_OpIncome_Before/totalRevenueBefore_quote) * 100.0; // 9/16/2014 JP - fix for incorrect margin before %
}	

total_OpIncome_Variance = total_OpIncome_After - total_OpIncome_Before;

total_Margin_Percent_Variance = 0.0; //marginPercentvariance will be marginDollarsVariave divided by RevenueVariance
if(totalVariance_Revenue <> 0.0){
	total_Margin_Percent_Variance = (total_OpIncome_Variance/totalVariance_Revenue) * 100.0;
}

total_NetRevenue_Before = totalRevenueBefore_quote - (total_disposal_expense_before);
total_NetRevenue_Variance = total_NetRevenue_After - total_NetRevenue_Before;

/* End of Existing Calc */
/*****   END OF  Calculations done in this script *****/

//Combine the HTML code into a string for storage in an attribute
returnStr = returnStr   + "<table class='rs_table_style' width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" style=\"font-family:Open Sans;\">"
						+ "<tr class='rs_table_style'>"//Start of labels - 1 row
						+ "<td rowspan = \"2\" align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'></td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td colspan = \"2\" align=\"center\" style='color: #000059;' >Small Container</td>";
}
if(industrialExists_quote){
	returnStr = returnStr 	+ "<td colspan = \"2\" align=\"center\" style='color: #000059;'>Large Container</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td rowspan = \"2\" align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Total</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td rowspan = \"2\" align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Total - After</td>"
						  + "<td rowspan = \"2\" align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Total - Before</td>"
						  + "<td rowspan = \"2\" align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Variance</td>";
}
returnStr = returnStr   + "</tr>" //End of labels - 1 row
						+ "<tr class='rs_table_style'>";//Start of labels - 2 row
						//+ "<td align=\"center\" ></td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Solid Waste</td>";
	returnStr = returnStr + "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Recycling</td>";
}
if(industrialExists_quote){
	returnStr = returnStr 	+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Solid Waste</td>";
	returnStr = returnStr 	+ "<td align=\"center\" style='border-bottom: thin solid #3D9BCB;color: #000059;'>Recycling</td>";
}
/*if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" ></td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" ></td>"
						  + "<td align=\"center\" ></td>";
}*/
returnStr = returnStr   + "</tr>" //End of labels - 2 row
						+ "<tr class='rs_tr_td_style'>" //Start of Revenue row
						+ "<td >Revenue</td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallSolidWasteRevenueWithAdmin,"USD") + "</td>";
	
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallRecyclingRevenueWithAdmin,"USD") + "</td>";
}
if(industrialExists_quote){
	
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeSolidWasteRevenueWithAdmin,"USD") + "</td>";
	
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeRecyclingRevenueWithAdmin,"USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalMonthlyAmtInclFees_quote, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalMonthlyAmtInclFees_quote, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(totalRevenueBefore_quote, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(totalVariance_Revenue, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Revenue row
						+ "<tr class='rs_tr_td_style'>" //Start of Disposal Expense row
						+ "<td >Disposal Expense</td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallSolidWasteDisposalExpense_quote, "USD") + "</td>";
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallRecyclingDisposalExpense_quote,"USD") + "</td>";
}
if(industrialExists_quote){
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeSolidWasteDisposalExpense_quote, "USD") + "</td>";
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeRecyclingDisposalExpense_quote,"USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalDisposalExpenseAfter_quote, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalDisposalExpenseAfter_quote, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_disposal_expense_before, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_disposal_variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Disposal Expense row
						+ "<tr class='rs_tr_td_style'>" //Start of Net Revenue row
						+ "<td >Net Revenue</td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallSolidWasteNetRevenueWithAdmin, "USD") + "</td>";
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallRecyclingNetRevenueWithAdmin,"USD") + "</td>";
}
if(industrialExists_quote){
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeSolidWasteNetRevenueWithAdmin, "USD")  + "</td>";
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeRecyclingNetRevenueWithAdmin,"USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_NetRevenue_After, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_NetRevenue_After, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_NetRevenue_Before, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_NetRevenue_Variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Net Revenue row
						+ "<tr class='rs_tr_td_style'>" //Start of Operating Expenses row
						+ "<td >Operating Expense</td>";
if(commercialExists_quote){
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallSolidWasteOperatingExpense_quote, "USD") + "</td>";
	returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(smallRecyclingOperatingExpense_quote,"USD") + "</td>";
}
if(industrialExists_quote){
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeSolidWasteOperatingExpense_quote, "USD") + "</td>";
	returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(largeRecyclingOperatingExpense_quote,"USD") + "</td>";
}
if(NOT(isExistingCustomer)){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalOperatingExpenseAfter_quote, "USD") + "</td>";
}
if(isExistingCustomer){
	returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(totalOperatingExpenseAfter_quote, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_operating_expense_before, "USD") + "</td>"
						  + "<td align=\"center\" >" + formatascurrency(total_operating_variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Operating Expenses row
						+ "<tr class='rs_tr_td_style'>" //Start of Op Income/Margin Dollars row
						+ "<td >Op Income $</td>";
if(commercialExists_quote){
	if(Color == "Red" AND small_SolidWaste_OpIncome < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(small_SolidWaste_OpIncome, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(small_SolidWaste_OpIncome, "USD") + "</td>";
	}
	if(Color == "Red" AND small_Recycling_OpIncome < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(small_Recycling_OpIncome, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" +  formatascurrency(small_Recycling_OpIncome, "USD") + "</td>";
	}

}
if(industrialExists_quote){
	if(Color == "Red" AND large_SolidWaste_OpIncome < 0){
		returnStr = returnStr   + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(large_SolidWaste_OpIncome, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(large_SolidWaste_OpIncome, "USD") + "</td>";
	}
	if(Color == "Red" AND large_Recycling_OpIncome < 0){
		returnStr = returnStr   + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(large_Recycling_OpIncome, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr   + "<td align=\"center\" >" + formatascurrency(large_Recycling_OpIncome, "USD") + "</td>";
	}
	
}
if(NOT(isExistingCustomer)){
	if(Color == "Red" AND total_OpIncome_After < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(total_OpIncome_After, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_OpIncome_After, "USD") + "</td>";
	}
	
}
if(isExistingCustomer){
	if(Color == "Red" AND total_OpIncome_After < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(total_OpIncome_After, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_OpIncome_After, "USD") + "</td>";
	}
	if(Color == "Red" AND total_OpIncome_Before < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(total_OpIncome_Before, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_OpIncome_Before, "USD") + "</td>";
	}
	if(Color == "Red" AND total_OpIncome_Variance < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + formatascurrency(total_OpIncome_Variance, "USD") + "</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + formatascurrency(total_OpIncome_Variance, "USD") + "</td>";
	}
	
}
returnStr = returnStr   + "</tr>" //End of Op Income/Margin Dollars row
						+ "<tr class='rs_tr_td_style'>" //Start of Margin Percent row
						+ "<td >Margin %</td>";
if(commercialExists_quote){
		if(Color == "Red" AND round(small_SolidWaste_Margin_Percent, 2) < 0){
			returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(small_SolidWaste_Margin_Percent, 2)) + "%</font></td>";
		}
		else{
			returnStr = returnStr + "<td align=\"center\" >" +  string(round(small_SolidWaste_Margin_Percent, 2)) + "%</td>";
		}
		if(Color == "Red" AND round(small_Recycling_Margin_Percent, 2) < 0){
			returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(small_Recycling_Margin_Percent, 2)) + "%</font></td>";
		}
		else{
			returnStr = returnStr + "<td align=\"center\" >" +  string(round(small_Recycling_Margin_Percent, 2)) + "%</td>";
		}
		
}
if(industrialExists_quote){
	if(Color == "Red" AND round(large_SolidWaste_Margin_Percent, 2) < 0){
		returnStr = returnStr   + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(large_SolidWaste_Margin_Percent, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr   + "<td align=\"center\" >" + string(round(large_SolidWaste_Margin_Percent, 2)) + "%</td>";
	}
	if(Color == "Red" AND round(large_Recycling_Margin_Percent, 2) < 0){
		returnStr = returnStr   + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(large_Recycling_Margin_Percent, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr   + "<td align=\"center\" >" + string(round(large_Recycling_Margin_Percent, 2)) + "%</td>";
	}
	
}
if(NOT(isExistingCustomer)){
	if(Color == "Red" AND round(total_Margin_Percent_After, 2) < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(total_Margin_Percent_After, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(total_Margin_Percent_After, 2)) + "%</td>";
	}
	
}
if(isExistingCustomer){
	if(Color == "Red" AND round(total_Margin_Percent_After, 2) < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(total_Margin_Percent_After, 2))+ "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(total_Margin_Percent_After, 2))+ "%</td>";
	}
	if(Color == "Red" AND round(total_Margin_Percent_Before, 2) < 0 ){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(total_Margin_Percent_Before, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(total_Margin_Percent_Before, 2)) + "%</td>";
	}
	if(Color == "Red" AND round(total_Margin_Percent_Variance, 2) < 0){
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(total_Margin_Percent_Variance, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(total_Margin_Percent_Variance, 2)) + "%</td>";
	}
	
}
returnStr = returnStr   + "</tr>"; //End of Margin Percent row
						

//If Color is "Red" then we are dealing with the approval e-mail, so we add the Direct Cost row
if(Color == "Red"){
	returnStr = returnStr + "<tr class='rs_tr_td_style'>" //Start of Margin Percent row
						+ "<td >Margin %</td>";
	if(smallSolidWasteCost_quote < 0){ //if negative, make red
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(smallSolidWasteCost_quote, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(smallSolidWasteCost_quote, 2)) + "%</td>";
	}
	
	if(smallRecyclingCost_quote < 0){ //if negative, make red
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(smallRecyclingCost_quote, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(smallRecyclingCost_quote, 2)) + "%</td>";
	}
	
	if(LargeSolidWasteCost_quote < 0){ //if negative, make red
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(LargeSolidWasteCost_quote, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(LargeSolidWasteCost_quote, 2)) + "%</td>";
	}
	
	if(LargeRecyclingCost_quote < 0){ //if negative, make red
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(LargeRecyclingCost_quote, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(LargeRecyclingCost_quote, 2)) + "%</td>";
	}
	
	if(totalContainerCost_quote < 0){ //if negative, make red
		returnStr = returnStr + "<td align=\"center\" >" + "<font color=#FF0000>" + string(round(totalContainerCost_quote, 2)) + "%</font></td>";
	}
	else{
		returnStr = returnStr + "<td align=\"center\" >" + string(round(totalContainerCost_quote, 2)) + "%</td>";
	}	
	
	returnStr = returnStr   + "</tr>";	
}//end Direct Cost row


returnStr = returnStr + "</table>";

//=============================== End - Building HTML String ===============================//

return returnStr;
