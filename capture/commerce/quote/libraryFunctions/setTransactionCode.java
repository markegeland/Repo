/*
================================================================================
       Name:  setTransactionCode
     Author:  Unknown
Create date:  Unknown
Description:  Sets the Transaction Code and Reason Code based on multiple conditions.
        
Input:      contractStatus_quote: String - 
            salesActivity_quote: String - 
            reasonCode_quote: String - 
            division_quote: String - 
            competitorCode_quote: String - 
            existingCustomerWithNewSite_quote: String - 
                    
Output:     transactionCode_quote
            reasonCodeOutput_quote
            oldTransactionCode_quote
            oldReasonCodeForOutput_quote
            competitorCode_quote

Updates:    20140625 - Andrew - pulled in competitorCode_quote to prevent blank competitor code
            20140630 - Andrew - Added in logic for when division is 0.
            20140901 - Aaron Q - SR 3-9423125271 to prevent blank competitor code
            20140918 - John Palubinskas - Added functionality to handle setting trans/reason code to 01/01
                       for existing customers adding service to a new site.  Added document header to
                       track updates. Issue 776.
            20150326 - John Palubinskas - #449 remove check for new from competitor
            20150402 - John Palubinskas - #449 set transaction/reason codes at the line level
            20150409 - John Palubinskas - #449 rework to consolidate trans/reason codes so for existing you will
                       get the same codes for every line item on the CSA
            20150420 - John Palubinskas - #532 fix new site new/new getting set as new/competitor
            20150421 - John Palubinskas - #540 fix in-progress new/new getting set as new/competitor
            20150428 - John Palubinskas - #549 fix close container not evaluating service inc/dec correctly
    
=====================================================================================================
*/
SERVICE_CHANGE = "Service Change";
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";

returnStr = "";

newSite = existingCustomerWithNewSite_quote;
processExisting = false;
newLargeContainer = false;
newSmallContainer = false;
serviceChange = false;
priceAdjustment = false;
closeContainerGroup = false;
totalYardsPerMonthNew = 0.0;
totalYardsPerMonthCurrent = 0.0;
totalSellPriceNew = 0.0;
totalSellPriceCurrent = 0.0;

for line in line_process {
    competitorCode = "";
    transactionCode = "";
    reasonCode = "";

    docNum = line._document_number;
    parentDocNum = line._parent_doc_number;
    print "----------->  line item: " + docNum;
    // Get config attributes
    competitorCode = getconfigattrvalue(docNum, "competitor");
    if (isnull(competitorCode)) { competitorCode = "NEW"; }
    else { competitorCode = substring(competitorCode,0,3); }

    salesActivity = getconfigattrvalue(docNum, "salesActivity");
    if (isnull(salesActivity)) { salesActivity = ""; }

    closureReason = getconfigattrvalue(docNum, "closureReason");
    if (isnull(closureReason)) { closureReason = ""; }

    priceAdjustmentReason = getconfigattrvalue(docNum, "priceAdjustmentReason");
    if (isnull(priceAdjustmentReason)) { priceAdjustmentReason = ""; }

    print "competitorCode: " + competitorCode;
    print "salesActivity: " + salesActivity;
    print "closureReason: " + closureReason;
    print "priceAdjustmentReason: " + priceAdjustmentReason;

    if(line._model_name <> ""){
        //-------------------------------------------------------------
        // Handle 01-01, 01-02, 01-11, 04-##
        //-------------------------------------------------------------

        // 01-01 = New - New
        if(salesActivity_quote == "New/New" AND competitorCode == "NEW") {
            transactionCode = "01";
            reasonCode = "01";
        }
        // 01-02 = New - From Competitor
        if(salesActivity_quote == "New/New" AND competitorCode <> "NEW") {
            transactionCode = "01";
            reasonCode = "02";
        }
        // 01-11 = New - Change of Owner
        if(salesActivity_quote == "Change of Owner") {
            transactionCode = "01";
            reasonCode = "11";
            competitorCode = "";
        }
        // 04-## = Close Account or Close Site
        // Apply Opportunity Status Close Account or Close Site to all line items, and keep any competitor values if supplied
        if (contractStatus_quote == "Close Account" OR contractStatus_quote == "Close Site") {
            transactionCode = "04";
            reasonCodeArr = split(reasonCode_quote, "-");
            reasonCode = reasonCodeArr[0];
            if (reasonCode <> "02") {
                competitorCode = "";
            }
        }

        //-------------------------------------------------------------
        // First loop for Existing Customers - Perform Calculations
        //-------------------------------------------------------------
        elif(salesActivity_quote == "Existing Customer") {
            processExisting = true;

            // Adding a new large or small container
            // 02-58 = Service Increase - Permanent
            if(line._model_name == LARGE_CONTAINER) {
                print "Existing Customer - Large Container Added";
                newLargeContainer = true;
            }

            if(line._model_name == SMALL_CONTAINER) {
                print "Existing Customer - Small Container Added";
                newSmallContainer = true;

                if(NOT isnull(line.yardsPerMonth_line)) {
                    totalYardsPerMonthNew = totalYardsPerMonthNew + line.yardsPerMonth_line;
                }
                if(NOT isnull(line.currentYardsPerMonth_line)) {
                    totalYardsPerMonthCurrent = totalYardsPerMonthCurrent + line.currentYardsPerMonth_line;
                }
            }

            // Activity to Existing Container
            if (salesActivity <> "") {

                if(NOT isnull(line.yardsPerMonth_line)) {
                    totalYardsPerMonthNew = totalYardsPerMonthNew + line.yardsPerMonth_line;
                }
                if(NOT isnull(line.currentYardsPerMonth_line)) {
                    totalYardsPerMonthCurrent = totalYardsPerMonthCurrent + line.currentYardsPerMonth_line;
                }

                if(lower(salesActivity) == "service level change") {
                    print "Existing Customer - Service Change";
                    serviceChange = true;
                }

                if(lower(salesActivity) == "price adjustment") {
                    print "Existing Customer - Price Adjustment";
                    priceAdjustment = true;
                }

                if(lower(salesActivity) == "close container group") {
                    print "Existing Customer - Close Container Group";
                    closeContainerGroup = true;
                }
            }
        }

        if (NOT processExisting) {
            returnStr = returnStr + docNum + "~competitorCode_line~" + competitorCode + "|"
                                  + docNum + "~transactionCode_line~" + transactionCode + "|"
                                  + docNum + "~reasonCode_line~" + reasonCode + "|";
        }

    }
    else
    {
        if(line.rateType_line == "Base") {
            totalSellPriceCurrent = totalSellPriceCurrent + line.currentPrice_line;
            totalSellPriceNew = totalSellPriceNew + line.sellPrice_line;
        }
    }

    print "--- Loop 1 Results ---";
    print "competitorCode: " + competitorCode;
    print "transactionCode: " + transactionCode;
    print "reasonCode: " + reasonCode;
    print "totalYardsPerMonthNew: " + string(totalYardsPerMonthNew);
    print "totalYardsPerMonthCurrent: " + string(totalYardsPerMonthCurrent);
    print "totalSellPriceNew: " + string(totalSellPriceNew);
    print "totalSellPriceCurrent: " + string(totalSellPriceCurrent);
}

if (processExisting) {
    //--------------------------------------------------------------------------
    // Second loop for Existing Customers - Set Trans/Reason/Competitor Codes
    //--------------------------------------------------------------------------
    for line in line_process{
        print "----- inside second loop -----";
        competitorCode = "";
        transactionCode = "";
        reasonCode = "";

        docNum = line._document_number;
        parentDocNum = line._parent_doc_number;
        print "----------->  line item: " + docNum;
        // Get config attributes

        salesActivity = getconfigattrvalue(docNum, "salesActivity");
        if (isnull(salesActivity)) { salesActivity = ""; }

        closureReason = getconfigattrvalue(docNum, "closureReason");
        if (isnull(closureReason)) { closureReason = ""; }

        priceAdjustmentReason = getconfigattrvalue(docNum, "priceAdjustmentReason");
        if (isnull(priceAdjustmentReason)) { priceAdjustmentReason = ""; }

        print "competitorCode: " + competitorCode;
        print "salesActivity: " + salesActivity;
        print "closureReason: " + closureReason;
        print "newSite: " + string(newSite);
        print "newLargeContainer: " + string(newLargeContainer);
        print "newSmallContainer: " + string(newSmallContainer);

        if(salesActivity_quote == "Existing Customer" and line._model_name <> "") {
            // Only need to get a competitor value on model lines
            competitorCode = getconfigattrvalue(docNum, "competitor");
            if (isnull(competitorCode)) { competitorCode = "NEW"; }
            else { competitorCode = substring(competitorCode,0,3); }

            // Any new sites should have all lines coded as New business
            // 01-01 = New - New
            if(newSite AND competitorCode == "NEW"){
                transactionCode = "01";
                reasonCode = "01";
            }
            // 01-02 = New - From Competitor
            elif(newSite AND competitorCode <> "NEW"){
                transactionCode = "01";
                reasonCode = "02";
            }
            // 02-58 = Service Increase Perm
            elif (newLargeContainer) {
                print "in newLargeContainer elif";
                // Any time we add a large container to an existing customer it is a Service Increase
                transactionCode = "02";
                reasonCode = "58";
                competitorCode = "";
            }
            elif (newSmallContainer OR serviceChange OR closeContainerGroup) {
                print "in newSmallContainer elif";
                // If there is no new Large Container, but there is a new Small Container, determine if it is
                // a Service Increase or Decrease by comparing the total yards per month before and after.

                // 05-58 = Service Decrease Perm
                if(totalYardsPerMonthNew < totalYardsPerMonthCurrent) { 
                    transactionCode = "05";
                    reasonCode = "58";
                }
                // 02-58 = Service Increase Perm
                else{
                    transactionCode = "02";
                    reasonCode = "58";
                }
                competitorCode = "";
            }
            else{
                if(priceAdjustment 
                    AND (totalSellPriceNew >= totalSellPriceCurrent)) {
                    // 03-62 Price Increase
                    transactionCode = "03";
                    reasonCode = "62";
                    competitorCode = "";
                }
                else{
                    // 06-## Price Decrease
                    transactionCode = "06";
                    reasonCode = "62";
                    if (lower(priceAdjustmentReason) == "rollback: competitive bid") { reasonCode = "13"; }
                    if (lower(priceAdjustmentReason) == "rollback of pi")            { reasonCode = "17"; competitorCode = "";}
                    if (lower(priceAdjustmentReason) == "rollback of current price") { reasonCode = "62"; competitorCode = "";}
                }
            }
        }    

        print "transactionCode: " + transactionCode;
        print "reasonCode: " + reasonCode;
        print "competitorCode: " + competitorCode;

        returnStr = returnStr + docNum + "~transactionCode_line~" + transactionCode + "|"
                              + docNum + "~reasonCode_line~" + reasonCode + "|"
                              + docNum + "~competitorCode_line~" + competitorCode + "|";

    }
}

return returnStr;
