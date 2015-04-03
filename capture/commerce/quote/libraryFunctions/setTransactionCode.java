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
    
=====================================================================================================
*/
SERVICE_CHANGE = "Service Change";
LARGE_CONTAINER = "Large Containers";
SMALL_CONTAINER = "Containers";

returnStr = "";

for line in line_process{
    competitorCode = "";
    transactionCode = "";
    reasonCode = "";

    docNum = line._document_number;
    parentDocNum = line._parent_doc_number;
    //print "----------->  line item: " + docNum;
    // Get config attributes
    competitorCode = getconfigattrvalue(docNum, "competitor");
    if (isnull(competitorCode)) { competitorCode = ""; }
    else { competitorCode = substring(competitorCode,0,3); }

    salesActivity = getconfigattrvalue(docNum, "salesActivity");
    if (isnull(salesActivity)) { salesActivity = ""; }

    closureReason = getconfigattrvalue(docNum, "closureReason");
    if (isnull(closureReason)) { closureReason = ""; }

    priceAdjustmentReason = getconfigattrvalue(docNum, "priceAdjustmentReason");
    if (isnull(priceAdjustmentReason)) { priceAdjustmentReason = ""; }

    //print "competitorCode: " + competitorCode;
    //print "salesActivity: " + salesActivity;
    //print "closureReason: " + closureReason;
    //print "priceAdjustmentReason: " + priceAdjustmentReason;

    if(line._model_name <> ""){
        // 01-01 = New - New
        if(salesActivity_quote == "New/New" AND competitorCode == ""){
            transactionCode = "01";
            reasonCode = "01";
        }
        // 01-02 = New - From Competitor
        if(salesActivity_quote == "New/New" AND competitorCode <> ""){
            transactionCode = "01";
            reasonCode = "02";
        }
        // 01-11 = New - Change of Owner
        if(salesActivity_quote == "Change of Owner"){
            transactionCode = "01";
            reasonCode = "11";
            competitorCode = "";
        }

        if(salesActivity_quote == "Existing Customer"){

            // Adding a new large or small container
            // 02-58 = Service Increase - Permanent
            if(line._model_name == LARGE_CONTAINER){
                //print "Existing Customer - Large Container Added";
                transactionCode = "02";
                reasonCode = "58";
                competitorCode = "";
            }

            if(line._model_name == SMALL_CONTAINER){
                //print "Existing Customer - Small Container Added";
                newYards = 0.0;
                oldYards = 0.0;
                if(NOT isnull(line.yardsPerMonth_line)){
                    newYards = line.yardsPerMonth_line;
                }
                if(NOT isnull(line.currentYardsPerMonth_line)){
                    oldYards = line.currentYardsPerMonth_line;
                }
                //print "newYards: " + string(newYards);
                //print "oldYards: " + string(oldYards);

                if (newYards >= oldYards) {
                    transactionCode = "02";
                }
                else{
                   //05-58 = Service Decrease - Permanent
                    transactionCode = "05";
                }
                reasonCode = "58";
                competitorCode = "";
            }

            // Activity to Existing Container
            if (salesActivity <> "") {
                // 04-02 = Lost - To Competitor
                if(lower(salesActivity) == "close container group" AND
                   lower(closureReason) == "02 - lost to competitor"){
                    transactionCode = "04";
                    reasonCode = "02";
                }

                // 04-11 = Lost - Change of Owner
                // This is hard-coded on the Change of Owner CSA

                // 04-18 = Lost - Service Issues
                if(lower(salesActivity) == "close container group" AND
                   lower(closureReason) == "18 - service issues"){
                    transactionCode = "04";
                    reasonCode = "18";
                    competitorCode = "";
                }

                // 04-21 = Lost - Business Closed
                if(lower(salesActivity) == "close container group" AND
                   lower(closureReason) == "21 - closed business"){
                    transactionCode = "04";
                    reasonCode = "21";
                    competitorCode = "";
                }

                // 04-56 = Lost - Competitor Pricing
                if(lower(salesActivity) == "close container group" AND
                   lower(closureReason) == "56 - competitor pricing"){
                    transactionCode = "04";
                    reasonCode = "56";
                }

                // 04-57 = Lost - Price Increase
                if(lower(salesActivity) == "close container group" AND
                   lower(closureReason) == "57 - price increase"){
                    transactionCode = "04";
                    reasonCode = "57";
                    competitorCode = "";
                }

                // 03-62 = Price Increase - Operational (Personally Secured or Contractually Obligated)
                if(lower(salesActivity) == "price adjustment" AND
                   find(lower(priceAdjustmentReason), "price increase") <> -1){
                    transactionCode = "03";
                    reasonCode = "62";
                    competitorCode = "";
                }

                // 06-13 = Price Decrease - Competitive Bid
                if(lower(salesActivity) == "price adjustment" AND
                   lower(priceAdjustmentReason) == "rollback: competitive bid"){
                    transactionCode = "06";
                    reasonCode = "13";
                }

                // 06-17 = Price Decrease - Rollback of PI
                if(lower(salesActivity) == "price adjustment" AND
                   lower(priceAdjustmentReason) == "rollback of pi"){
                    transactionCode = "06";
                    reasonCode = "17";
                    competitorCode = "";
                }

                // 06-62 = Price Decrease - Operational
                if(lower(salesActivity) == "price adjustment" AND
                   lower(priceAdjustmentReason) == "rollback of current price"){
                    transactionCode = "06";
                    reasonCode = "62";
                    competitorCode = "";
                }
            }

        }
    }
    //print "competitorCode: " + competitorCode;
    //print "transactionCode: " + transactionCode;
    //print "reasonCode: " + reasonCode;

    returnStr = returnStr + docNum + "~competitorCode_line~" + competitorCode + "|"
                          + docNum + "~transactionCode_line~" + transactionCode + "|"
                          + docNum + "~reasonCode_line~" + reasonCode + "|";

}
//print "returnStr: " + returnStr;

return returnStr;
