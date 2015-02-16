/*
====================================================================================================

Description:  Collects base price, FRF, ERF, admin fee, total price, and commission for floor, base, 
              target, stretch, and sell prices for display on the commerce page
        
Input:      
                    
Output:     String - Output parsed as HTML by the attribute to display table of attributes

Updates:    20140916 John (Republic) - fixed margin before % calculation
            201401   Julie (Oracle) - Added Red logic
            20140114 Julie (Oracle) - Added Direct Cost row
            20150215 John Palubinskas - implemented direct cost calculations, and refactored
                                        function to not set inline styles
    
=====================================================================================================
*/
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
    adminRatePerCol = adminRate_quote / numOfCols;
    adminERFAndFRFPerCol = adminERFAndFRF / numOfCols;
}

//--------------------------------------------------------
// Revenue with admin
//--------------------------------------------------------
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

totalVariance_Revenue = totalMonthlyAmtInclFees_quote - totalRevenueBefore_quote;
//adminRate must be separaetly added since individual container revenues don't contain adminRate as it is at the quote level and not model level fees
total_NetRevenue_After = smallSolidWasteNetRevenue_quote + smallRecyclingNetRevenue_quote + largeSolidWasteNetRevenue_quote + largeRecyclingNetRevenue_quote + adminRate_quote;

//--------------------------------------------------------
// Op Income $
//--------------------------------------------------------
small_SolidWaste_OpIncome = smallSolidWasteRevenueWithAdmin - (smallSolidWasteDisposalExpense_quote + smallSolidWasteOperatingExpense_quote);
small_Recycling_OpIncome = smallRecyclingRevenueWithAdmin - (smallRecyclingDisposalExpense_quote + smallRecyclingOperatingExpense_quote);

large_SolidWaste_OpIncome = largeSolidWasteRevenueWithAdmin - (largeSolidWasteDisposalExpense_quote + largeSolidWasteOperatingExpense_quote);
large_Recycling_OpIncome = largeRecyclingRevenueWithAdmin - (largeRecyclingDisposalExpense_quote + largeRecyclingOperatingExpense_quote);

total_OpIncome_After = totalMonthlyAmtInclFees_quote - (totalDisposalExpenseAfter_quote + totalOperatingExpenseAfter_quote);

//--------------------------------------------------------
// Margin %
//--------------------------------------------------------
small_SolidWaste_Margin_Percent = 0.0;
if(smallSolidWasteRevenueWithAdmin <> 0.0){
    small_SolidWaste_Margin_Percent = (small_SolidWaste_OpIncome / smallSolidWasteRevenueWithAdmin) * 100.0;
}
 
small_Recycling_Margin_Percent = 0.0;
if(smallRecyclingRevenueWithAdmin <> 0.0){
    small_Recycling_Margin_Percent = (small_Recycling_OpIncome / smallRecyclingRevenueWithAdmin) * 100.0;
}

large_SolidWaste_Margin_Percent = 0.0;
if(largeSolidWasteRevenueWithAdmin <> 0.0){
    large_SolidWaste_Margin_Percent = (large_SolidWaste_OpIncome / largeSolidWasteRevenueWithAdmin) * 100.0;
}
large_Recycling_Margin_Percent = 0.0;
if(largeRecyclingRevenueWithAdmin <> 0.0){
    large_Recycling_Margin_Percent = (large_Recycling_OpIncome / largeRecyclingRevenueWithAdmin) * 100.0;
}

total_Margin_Percent_After = 0.0;
if(totalMonthlyAmtInclFees_quote <> 0.0){
    total_Margin_Percent_After = (total_OpIncome_After / totalMonthlyAmtInclFees_quote) * 100.0;
}


//--------------------------------------------------------
// Existing Service - Before and Variance
//--------------------------------------------------------

//In future, we should add largeContainer existing operating expenses as well
total_operating_expense_before = existingSmallContainerSWOperatingExpense_quote + existingSmallContainerRecyOperatingExpense_quote;
total_operating_variance = totalOperatingExpenseAfter_quote - total_operating_expense_before;

total_disposal_expense_before = existingSmallContainerSWeDisposalExpense_quote + existingSmallContainerRecyDisposalExpense_quote;
total_disposal_variance = totalDisposalExpenseAfter_quote - total_disposal_expense_before;

total_OpIncome_Before = totalRevenueBefore_quote - (total_disposal_expense_before + total_operating_expense_before);

total_Margin_Percent_Before = 0.0;
if(totalRevenueBefore_quote <> 0.0){
    total_Margin_Percent_Before =  (total_OpIncome_Before / totalRevenueBefore_quote) * 100.0;
}   

total_OpIncome_Variance = total_OpIncome_After - total_OpIncome_Before;

//--------------------------------------------------------
// Margin % Variance =  op income variance / revenue variance
//--------------------------------------------------------
total_Margin_Percent_Variance = 0.0; 
if(totalVariance_Revenue <> 0.0){
    total_Margin_Percent_Variance = (total_OpIncome_Variance / totalVariance_Revenue) * 100.0;
}

total_NetRevenue_Before = totalRevenueBefore_quote - total_disposal_expense_before;
total_NetRevenue_Variance = total_NetRevenue_After - total_NetRevenue_Before;


//--------------------------------------------------------
// Direct Cost %
//--------------------------------------------------------
smallSolidWasteDirectCostPct = 0.0;
smallRecyclingDirectCostPct  = 0.0;
largeSolidWasteDirectCostPct = 0.0;
largeRecyclingDirectCostPct  = 0.0;
totalDirectCostPct = 0.0;
if(smallSolidWasteRevenueWithAdmin <> 0){
    smallSolidWasteDirectCostPct = ((smallSolidWasteRevenueWithAdmin - smallSolidWasteCost_quote) / smallSolidWasteRevenueWithAdmin) * 100;
}
if(smallRecyclingRevenueWithAdmin <> 0){
    smallRecyclingDirectCostPct = ((smallRecyclingRevenueWithAdmin - smallRecyclingCost_quote) / smallRecyclingRevenueWithAdmin) * 100;
}
if(largeSolidWasteRevenueWithAdmin <> 0){
    largeSolidWasteDirectCostPct = ((largeSolidWasteRevenueWithAdmin - largeSolidWasteCost_quote) / largeSolidWasteRevenueWithAdmin) * 100;
}
if(largeRecyclingRevenueWithAdmin <> 0){
    largeRecyclingDirectCostPct = ((largeRecyclingRevenueWithAdmin - largeRecyclingCost_quote) / largeRecyclingRevenueWithAdmin) * 100;
}
if(total_NetRevenue_After <> 0){
    totalDirectCostPct = ((total_NetRevenue_After - totalContainerCost_quote) / total_NetRevenue_After) * 100;
}


//--------------------------------------------------------
// Create HTML output
//--------------------------------------------------------
redClass = " class='red'";

returnStr = returnStr   + "<table>"
                        + "<tr>" // Row Labels
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
                        + "<tr>" // Revenue
                        + "<td class='first-column'>Revenue</td>";
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
returnStr = returnStr   + "</tr>"
                        + "<tr>" // Disposal Expense
                        + "<td class='first-column'>Disposal Expense</td>";
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
                        + "<td class='first-column'>Net Revenue</td>";
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
                        + "<td class='first-column'>Operating Expense</td>";
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
returnStr = returnStr   + "</tr>"
                        + "<tr>" // Op Income $
                        + "<td class='first-column'>Op Income $</td>";

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
returnStr = returnStr   + "</tr>"
                        + "<tr>" // Margin %
                        + "<td class='first-column'>Margin %</td>";
if(commercialExists_quote){
    returnStr = returnStr + "<td";
    if(small_SolidWaste_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(small_SolidWaste_Margin_Percent, 1)) + "%</td>";

    returnStr = returnStr + "<td";
    if(small_Recycling_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(small_Recycling_Margin_Percent, 1)) + "%</td>";
}

if(industrialExists_quote){
    returnStr = returnStr + "<td";
    if(large_SolidWaste_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(large_SolidWaste_Margin_Percent, 1)) + "%</td>";

    returnStr = returnStr + "<td";
    if(large_Recycling_Margin_Percent < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(large_Recycling_Margin_Percent, 1)) + "%</td>";
}

returnStr = returnStr + "<td";
if(total_Margin_Percent_After < 0){ returnStr = returnStr + redClass; }
returnStr = returnStr + ">" + string(round(total_Margin_Percent_After, 1)) + "%</td>";
    
if(isExistingCustomer){
    returnStr = returnStr + "<td";
    if(total_Margin_Percent_Before < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(total_Margin_Percent_Before, 1)) + "%</td>";

    returnStr = returnStr + "<td";
    if(total_Margin_Percent_Variance < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(total_Margin_Percent_Variance, 1)) + "%</td>";
}
returnStr = returnStr   + "</tr>";
                        

//If Color is "Red" then we are dealing with the approval e-mail, so we add the Direct Cost row
if(Color == "Red"){
    returnStr = returnStr + "<tr>" // Direct Cost %
                        + "<td class='first-column'>Direct Cost %</td>";
    if(commercialExists_quote){
        returnStr = returnStr + "<td";
        if(smallSolidWasteDirectCostPct < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + string(round(smallSolidWasteDirectCostPct, 1)) + "%</td>";

        returnStr = returnStr + "<td";
        if(smallRecyclingDirectCostPct < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + string(round(smallRecyclingDirectCostPct, 1)) + "%</td>";
    }

    if(industrialExists_quote){
        returnStr = returnStr + "<td";
        if(largeSolidWasteDirectCostPct < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + string(round(largeSolidWasteDirectCostPct, 1)) + "%</td>";
        
        returnStr = returnStr + "<td";
        if(largeRecyclingDirectCostPct < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + string(round(largeRecyclingDirectCostPct, 1)) + "%</td>";
    }    

    returnStr = returnStr + "<td";
    if(totalDirectCostPct < 0){ returnStr = returnStr + redClass; }
    returnStr = returnStr + ">" + string(round(totalDirectCostPct, 1)) + "%</td>";

   if(isExistingCustomer){
        returnStr = returnStr + "<td";
        if(totalContainerCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "</td>";

        returnStr = returnStr + "<td";
        if(totalContainerCost_quote < 0){ returnStr = returnStr + redClass; }
        returnStr = returnStr + ">" + "</td>";
    }
}

returnStr = returnStr + "</tr></table>";

return returnStr;
