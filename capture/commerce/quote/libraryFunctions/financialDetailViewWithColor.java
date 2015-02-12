/*
====================================================================================================

Description:  Collects base price, FRF, ERF, admin fee, total price, and commission for floor, base, 
              target, stretch, and sell prices for display on the commerce page
        
Input:      
                    
Output:     String - Output parsed as HTML by the attribute to display table of attributes

Updates:    20140916 John (Republic) - fixed margin before % calculation
            201401   Julie (Oracle) - Added Red logic
            20140114 Julie (Oracle) - Added Direct Cost row
            
    
=====================================================================================================
*/
//=============================== START - Building HTML String ===============================//
returnStr = "";

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
    smallSolidWasteNetRevenueWithAdmin = smallSolidWasteNetRevenue_quote + adminRatePerCol;
}
if(smallRecyclingNetRevenue_quote <> 0.0){
    smallRecyclingNetRevenueWithAdmin = smallRecyclingNetRevenue_quote + adminRatePerCol;
}
if(largeSolidWasteNetRevenue_quote <> 0.0){
    largeSolidWasteNetRevenueWithAdmin = largeSolidWasteNetRevenue_quote + adminRatePerCol;
}
if(largeRecyclingNetRevenue_quote <> 0.0){
    largeRecyclingNetRevenueWithAdmin = largeRecyclingNetRevenue_quote + adminRatePerCol;
}
if(smallSolidWasteRevenue_quote <> 0.0){
    smallSolidWasteRevenueWithAdmin = smallSolidWasteRevenue_quote + adminRatePerCol;
}
if(smallRecyclingRevenue_quote <> 0.0){
    smallRecyclingRevenueWithAdmin = smallRecyclingRevenue_quote + adminRatePerCol;
}
if(largeSolidWasteRevenue_quote <> 0.0){
    largeSolidWasteRevenueWithAdmin = largeSolidWasteRevenue_quote + adminRatePerCol;
}
if(largeRecyclingRevenue_quote <> 0.0){ 
    largeRecyclingRevenueWithAdmin = largeRecyclingRevenue_quote + adminRatePerCol;
}


/*****   START OF Calculations done in this script *****/
totalVariance_Revenue = totalMonthlyAmtInclFees_quote - totalRevenueBefore_quote;
total_NetRevenue_After = smallSolidWasteNetRevenue_quote + smallRecyclingNetRevenue_quote + largeSolidWasteNetRevenue_quote + largeRecyclingNetRevenue_quote + adminRate_quote; //J.Felberg + adminERFAndFRFPerCol; //adminRate must be separaetly added because individual container revues don't contain adminRate because it is quote level and not model level fees

//OpIncome Calc
small_SolidWaste_OpIncome = smallSolidWasteRevenueWithAdmin - (smallSolidWasteDisposalExpense_quote + smallSolidWasteOperatingExpense_quote);
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
    total_Margin_Percent_Before =  (total_OpIncome_Before/totalRevenueBefore_quote) * 100.0;
}   

total_OpIncome_Variance = total_OpIncome_After - total_OpIncome_Before;

//marginPercentvariance will be marginDollarsVariave divided by RevenueVariance
total_Margin_Percent_Variance = 0.0; 
if(totalVariance_Revenue <> 0.0){
    total_Margin_Percent_Variance = (total_OpIncome_Variance/totalVariance_Revenue) * 100.0;
}

total_NetRevenue_Before = totalRevenueBefore_quote - (total_disposal_expense_before);
total_NetRevenue_Variance = total_NetRevenue_After - total_NetRevenue_Before;

/* End of Existing Calc */



//Combine the HTML code into a string for storage in an attribute
redClass = " class='red'";

returnStr = returnStr   + "<table>"
                        + "<tr>"//Start of labels - 1 row
                        + "<th rowspan = \"2\"></th>";
if(commercialExists_quote){
    returnStr = returnStr + "<th colspan = \"2\">Small Container</th>";
}
if(industrialExists_quote){
    returnStr = returnStr   + "<th colspan = \"2\">Large Container</th>";
}
if(NOT(isExistingCustomer)){
    returnStr = returnStr + "<th rowspan = \"2\">Total</th>";
}
if(isExistingCustomer){
    returnStr = returnStr + "<th rowspan = \"2\">Total - After</th>"
                          + "<th rowspan = \"2\">Total - Before</th>"
                          + "<th rowspan = \"2\">Variance</th>";
}
returnStr = returnStr   + "</tr><tr>";

if(commercialExists_quote){
    returnStr = returnStr + "<th>Solid Waste</th>"
                          + "<th>Recycling</th>";
}
if(industrialExists_quote){
    returnStr = returnStr   + "<th>Solid Waste</th>"
                            + "<th>Recycling</th>";
}
returnStr = returnStr   + "</tr>"
                        + "<tr>" //Start of Revenue row
                        + "<td>Revenue</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td>" +  formatascurrency(smallSolidWasteRevenueWithAdmin,"USD") + "</td>"
                          + "<td>" +  formatascurrency(smallRecyclingRevenueWithAdmin,"USD") + "</td>";
}
if(industrialExists_quote){
    returnStr = returnStr   + "<td>" + formatascurrency(largeSolidWasteRevenueWithAdmin,"USD") + "</td>";
    returnStr = returnStr   + "<td>" + formatascurrency(largeRecyclingRevenueWithAdmin,"USD") + "</td>";
}

returnStr = returnStr + "<td>" + formatascurrency(totalMonthlyAmtInclFees_quote, "USD") + "</td>";

if(isExistingCustomer){
    returnStr = returnStr + "<td>" + formatascurrency(totalRevenueBefore_quote, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(totalVariance_Revenue < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(totalVariance_Revenue, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Revenue row
                        + "<tr>" //Start of Disposal Expense row
                        + "<td>Disposal Expense</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td>" +  formatascurrency(smallSolidWasteDisposalExpense_quote, "USD") + "</td>"
                          + "<td>" +  formatascurrency(smallRecyclingDisposalExpense_quote,"USD") + "</td>";
}
if(industrialExists_quote){
    returnStr = returnStr   + "<td>" + formatascurrency(largeSolidWasteDisposalExpense_quote, "USD") + "</td>"
                            + "<td>" + formatascurrency(largeRecyclingDisposalExpense_quote,"USD") + "</td>";
}

returnStr = returnStr + "<td>" + formatascurrency(totalDisposalExpenseAfter_quote, "USD") + "</td>";

if(isExistingCustomer){
    returnStr = returnStr + "<td>" + formatascurrency(total_disposal_expense_before, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(total_disposal_variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(total_disposal_variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Disposal Expense row
                        + "<tr>" //Start of Net Revenue row
                        + "<td>Net Revenue</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td>" +  formatascurrency(smallSolidWasteNetRevenueWithAdmin, "USD") + "</td>"
                          + "<td>" +  formatascurrency(smallRecyclingNetRevenueWithAdmin,"USD") + "</td>";
}
if(industrialExists_quote){
    returnStr = returnStr + "<td>" + formatascurrency(largeSolidWasteNetRevenueWithAdmin, "USD")  + "</td>"
                          + "<td>" + formatascurrency(largeRecyclingNetRevenueWithAdmin,"USD") + "</td>";
}

returnStr = returnStr + "<td>" + formatascurrency(total_NetRevenue_After, "USD") + "</td>";

if(isExistingCustomer){
    returnStr = returnStr + "<td>" + formatascurrency(total_NetRevenue_Before, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(total_NetRevenue_Variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(total_NetRevenue_Variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Net Revenue row
                        + "<tr>" //Start of Operating Expenses row
                        + "<td>Operating Expense</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td>" +  formatascurrency(smallSolidWasteOperatingExpense_quote, "USD") + "</td>"
                          + "<td>" +  formatascurrency(smallRecyclingOperatingExpense_quote,"USD") + "</td>";
}
if(industrialExists_quote){
    returnStr = returnStr + "<td>" + formatascurrency(largeSolidWasteOperatingExpense_quote, "USD") + "</td>"
                          + "<td>" + formatascurrency(largeRecyclingOperatingExpense_quote,"USD") + "</td>";
}

returnStr = returnStr + "<td>" + formatascurrency(totalOperatingExpenseAfter_quote, "USD") + "</td>";

if(isExistingCustomer){
    returnStr = returnStr + "<td>" + formatascurrency(total_operating_expense_before, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(total_operating_variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(total_operating_variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Operating Expenses row
                        + "<tr>" //Start of Op Income/Margin Dollars row
                        + "<td>Op Income $</td>";

if(commercialExists_quote){
    returnStr = returnStr + "<td";
    if(small_SolidWaste_OpIncome < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(small_SolidWaste_OpIncome, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(small_Recycling_OpIncome < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(small_Recycling_OpIncome, "USD") + "</td>";
}

if(industrialExists_quote){
    returnStr = returnStr + "<td";
    if(large_SolidWaste_OpIncome < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(large_SolidWaste_OpIncome, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(large_Recycling_OpIncome < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(large_Recycling_OpIncome, "USD") + "</td>";
}

returnStr = returnStr + "<td";
if(total_OpIncome_After < 0){ returnStr = returnStr + redClass; }
returnStr = returnStr + ">" + formatascurrency(total_OpIncome_After, "USD") + "</td>";

if(isExistingCustomer){
    returnStr = returnStr + "<td";
    if(total_OpIncome_Before < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(total_OpIncome_Before, "USD") + "</td>";

    returnStr = returnStr + "<td";
    if(total_OpIncome_Variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + formatascurrency(total_OpIncome_Variance, "USD") + "</td>";
}
returnStr = returnStr   + "</tr>" //End of Op Income/Margin Dollars row
                        + "<tr>" //Start of Margin Percent row
                        + "<td>Margin %</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td";
    if(small_SolidWaste_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(small_SolidWaste_Margin_Percent, 2)) + "%</td>";

    returnStr = returnStr + "<td";
    if(small_Recycling_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(small_Recycling_Margin_Percent, 2)) + "%</td>";
}

if(industrialExists_quote){
    returnStr = returnStr + "<td";
    if(large_SolidWaste_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(large_SolidWaste_Margin_Percent, 2)) + "%</td>";

    returnStr = returnStr + "<td";
    if(large_Recycling_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(large_Recycling_Margin_Percent, 2)) + "%</td>";
}

returnStr = returnStr + "<td";
if(total_Margin_Percent_After < 0){ returnStr = returnStr + redClass; }
returnStr = returnStr + ">" + string(round(total_Margin_Percent_After, 2)) + "%</td>";
    
if(isExistingCustomer){
    returnStr = returnStr + "<td";
    if(total_Margin_Percent_Before < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(total_Margin_Percent_Before, 2)) + "%</td>";

    returnStr = returnStr + "<td";
    if(total_Margin_Percent_Variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(total_Margin_Percent_Variance, 2)) + "%</td>";
}
returnStr = returnStr   + "</tr>"; //End of Margin Percent row
                        

//If Color is "Red" then we are dealing with the approval e-mail, so we add the Direct Cost row
if(Color == "Red"){
    returnStr = returnStr + "<tr>" //Start of Direct Cost row
                        + "<td>Direct Cost %</td>";
    if(commercialExists_quote){
        returnStr = returnStr + "<td";
        if(smallSolidWasteCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" +  "TBD %</td>"; //string(round(smallSolidWasteCost_quote, 2)) + (proposed price - cost) / proposed price
        
        returnStr = returnStr + "<td";
        if(smallRecyclingCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "TBD %</td>";//string(round(smallRecyclingCost_quote, 2)) + 
    }

    if(industrialExists_quote){
        returnStr = returnStr + "<td";
        if(LargeSolidWasteCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "TBD %</td>";//string(round(LargeSolidWasteCost_quote, 2)) + 
        
        returnStr = returnStr + "<td";
        if(LargeRecyclingCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "TBD %</td>";//string(round(LargeRecyclingCost_quote, 2)) + 
    }    

    returnStr = returnStr + "<td";
    if(totalContainerCost_quote < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + "TBD %</td>";//string(round(totalContainerCost_quote, 2)) + 

   if(isExistingCustomer){
        returnStr = returnStr + "<td";
        if(totalContainerCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "TBD %</td>";// 

        returnStr = returnStr + "<td";
        if(totalContainerCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "TBD %</td>";//
    }
}

returnStr = returnStr + "</tr></table>";

return returnStr;
