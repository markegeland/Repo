/* 
================================================================================
       Name:  emailNotificationGenerator
     Author:   
Create date:  
Description:  
        
  Input:     SubmittedBy       - String
             SubmitComment     - String
             ReasonName        - String
             ReasonDescription - String
             Approver          - String
             TransactionURL    - String
                    
 Output:     String containing HTML used to generate approval emails

Updates:
    20141211- 
    20150203 - Julie Felberg    - Implemented 4 new tables with logic.  This logic involved a new line
                                  level loop and several new variables.
    20150209 - John Palubinskas - #68 Replaced existing function with logic from newEmailNotificaitonGenerator.
                                  That function will be deleted.  Cleaned up header and comments.
                                  Completely reworked function to get rid of styles embedded in the tags.
    20150215 - John Palubinskas - #68 Additional work for adding quantity columns.  Calculate fees correctly on conatiner
                                  comparison tables, and fix formatting issues.
    20150223 - John Palubinskas - #430 fix approval email incorrectly showing competitor by checking priceAdjustmentReason_line.
    20150223 - Mike Boylan      - #145 Add the Compactor Asset Value
    20150413 - John Palubinskas - #449 Handle multiple competitors
    20150223 - Mike Boylan      - #145 Change Compactor Asset Value to Total Compactor Expense
    
================================================================================
*/
emailBody = "";

//Calculations
monthlyTotalBase = smallMonthlyTotalBase_quote + largeMonthlyTotalBase_quote;
monthlyTotalTarget = smallMonthlyTotalTarget_quote + largeMonthlyTotalTarget_quote;
monthlyTotalStretch = smallMonthlyTotalStretch_quote + largeMonthlyTotalStretch_quote;
monthlyTotalSell = smallMonthlyTotalProposed_quote + largeMonthlyTotalProposed_quote;

AdHocIndex = 0;
AdHocDescArr = string[];
AdHocBillingMethodArr = string[];
AdHocQtyArr = string[];
AdHocTotalPriceArr = string[];
ConfigAttrDict=dict("string");
SCDocNum = string[];
ContPDocNum = string[];
ContQuantityArr = string[];
ContainerArr = string[];
RatePerHaulArr = string[];
ContTypeArr = string[];
CompactorValArr = string[];
TotalServiceTimeArr = string[];
TonsPerHaulArr = string[];
DisposalSiteArr = string[];
CostPerHaulArr = string[];
newPriceDict = dict("string");
newFeesDict = dict("string");
oldContainerArray = string[];
NewContainerArray = string[];
oldSizeArray = string[];
newSizeArray = string[];
oldNbrArray = string[];
newNbrArray = string[];
oldWtArray = string[];
newWtArray = string[];
oldFreqArray = string[];
newFreqArray = string[];
oldPriceArray = string[];
oldFeesArray = string[];
newPriceArray = string[];
newFeesArray = string[];
approverFullName = "";
submittedByFullName = "";
submittersComments = "";
rateRestrictions = "";
displayCompetitor = "";
salesActivity = salesActivity_quote;
competitor = "";
competitorArray = string[];

for line in line_process{
    //models
    if(NOT isnumber(line._parent_doc_number)){
        put(ConfigAttrDict, line._document_number, line._config_attributes);
    }
    //line items
    if(isnumber(line._parent_doc_number)){

        //Competitors
        if(line.billingType_line == "Monthly"){
            competitor = getconfigattrvalue(line._parent_doc_number, "competitor");
            competitorCd = "";
            if (NOT isnull(competitor) AND 
                len(competitor) == 4 AND 
                NOT substring(competitor,0,3) == "NEW") {
                competitorCd = substring(competitor,0,3);
                append(competitorArray, competitorCd);
            }
        }

        //Ad Hoc Items
        if(line._parent_line_item == "Ad-Hoc Line Items"){
            AdHocDescArr[AdHocIndex] = line.description_line;
            AdHocBillingMethodArr[AdHocIndex] = line.billingType_line;
            AdHocQtyArr[AdHocIndex] = string(line._price_quantity);
            AdHocTotalPriceArr[AdHocIndex] = string(line.sellPrice_line);
            AdHocIndex = AdHocIndex + 1;
        }
        //Service Changes
        elif(line.priceType_line == "Service Change"){
            salesActivity = line.activity_line + " - " + line.priceAdjustmentReason_line;

            if(line.activity_line == "Service level change"){
                if(findinarray(SCDocNum, line._parent_doc_number) == -1){
                    append(SCDocNum, line._parent_doc_number);
                }
                //if monthly fee, then populate new price array
                if(line.billingType_line == "Monthly"){
                    put(newPriceDict,line._parent_doc_number, formatascurrency(line.sellPrice_line,"USD"));
                    frfCharged = 0.0;
                    erfCharged = 0.0;
                    adminCharged = 0.0;
                    if (isFRFwaived_quote <> 1) {
                        frfCharged = line.frfAmountSell_line;
                    }
                    if (isERFwaived_quote <> 1) {
                        erfCharged = line.erfAmountSell_line;
                    }
                    if (adminFeeCharged_quote <> "No") {
                        adminCharged = adminRate_quote;
                    }

                    put(newFeesDict, line._parent_doc_number, formatascurrency(frfCharged + erfCharged + adminCharged,"USD"));
                }
            }
        }
        //Containers
        else{
            if(findinarray(ContPDocNum, line._parent_doc_number) == -1){
                append(ContPDocNum,line._parent_doc_number);        
                
                //LARGE_CONTAINER
                if(line.billingType_line == "Per Haul"){
                    allSites = getconfigattrvalue(line._parent_doc_number, "site_disposalSite");
                    siteIndex = atoi(getconfigattrvalue(line._parent_doc_number, "alternateSite_l"));
                    disposalSites = split(allSites, "$,$");

                    print "we have a large container";
                    append(ContQuantityArr, string(line._price_quantity));
                    append(ContainerArr, line.description_line);
                    append(RatePerHaulArr, string(line.perHaulRate_line));
                    append(ContTypeArr, line._model_name);
                    append(CostPerHaulArr, string(line.totalFloorPrice_line));
                    append(CompactorValArr, getconfigattrvalue(line._parent_doc_number, "compactorValue"));
                    append(DisposalSiteArr, substring(disposalSites[siteIndex-1], 0, 49));
                    append(TotalServiceTimeArr, getconfigattrvalue(line._parent_doc_number, "adjustedTotalTime_l"));
                    append(TonsPerHaulArr, getconfigattrvalue(line._parent_doc_number, "estTonsHaul_l"));
                    
                }
                //SMALL_CONTAINER
                else{
                    print "we have a small container";
                    append(ContQuantityArr, string(line._price_quantity));
                    append(ContainerArr, line.description_line);
                    append(RatePerHaulArr, string(line.perHaulRate_line));
                    append(ContTypeArr, line._model_name); 
                    append(CompactorValArr, getconfigattrvalue(line._parent_doc_number, "compactorValue"));
                    append(TotalServiceTimeArr, "N/A");
                    append(CostPerHaulArr, "N/A");
                    append(TonsPerHaulArr, "N/A");
                    
                    polygonRegion = getconfigattrvalue(line._parent_doc_number, "polygonRegion");
                    if((getconfigattrvalue(line._parent_doc_number, "wasteCategory") == "Solid Waste") AND polygonRegion <> ""){
                        append(DisposalSiteArr, polygonRegion);
                    }
                    else{
                        append(DisposalSiteArr, "N/A");
                    }
                }               
            }
        }
    }
}

// Get the competitors from the competitor codes
CompetitorRecordSet = BMQL("SELECT competitor FROM div_competitor_adj WHERE division = $division_quote AND Competitor_Cd IN $competitorArray");

competitor = "";
for eachCompetitor in CompetitorRecordSet{
    if(competitor == ""){
        competitor = get(eachCompetitor,"competitor");
    }
    else{
        competitor = competitor + ", " + get(eachCompetitor,"competitor");
    }
}

// Hide the competitor row if there is no competitor
if(trim(competitor) == ""){
    displayCompetitor = " class='hide'";
}

// We have approvers and submitter arrays passed in as Strings when they're really string[].
// Remove the array brackets from SubmittedBy (there should be just a single entry)
// and create our own string[] for approvers for the User_Hierarchy lookup.
approvers = replace(replace(Approver,"[",""),"]","");
approversArr = split(approvers,",");
submitter = replace(replace(SubmittedBy,"[",""),"]","");
NameRecordSet = BMQL("SELECT First_Name, Last_Name, User_Login FROM User_Hierarchy WHERE User_Login IN $approversArr OR User_Login = $submitter");

for eachName in NameRecordSet{
    Login = get(eachName, "User_Login");
    if(Login == approversArr[0]){ // just grabbing first name (might be bad...)
        approverFullName = get(eachName, "First_Name") + " " + get(eachName, "Last_Name");
    }
    if(Login == submitter){
        submittedByFullName = get(eachName, "First_Name") + " " + get(eachName, "Last_Name");
    }
}

// Set default names if we do not successfully pull names from User_Hierarchy
if(approverFullName == ""){
    approverFullName = "Capture Approver";
}
if(submittedByFullName == ""){
    submittedByFullName = SubmittedBy;
}
if(NOT isnull(SubmitComment)){
    submittersComments = SubmitComment;
}

// Set rate restriction display text
if(customerRateRestriction_quote == false){
    rateRestrictions =  "No Rate Restriction";
}
else{
    rateRestrictions = "Rate Firm until " + afterYear1Date_quote;

    if(year1Rate_quote <> ""){
        rateRestrictions = rateRestrictions + "<br />" + year1RatePrint_quote + ", " + afterYear1Date_quote;
    }
    if(year2Rate_quote <> ""){
        rateRestrictions = rateRestrictions + "<br />" + year2RatePrint_quote + ", " + afterYear2Date_quote;
    }
    if(year3Rate_quote <> ""){
        rateRestrictions = rateRestrictions + "<br />" + year3RatePrint_quote + ", " + afterYear3Date_quote;
    }
    if(afterYear4_quote <> ""){
        rateRestrictions = rateRestrictions + "<br />" + year4RatePrint_quote + ", " + afterYear4Date_quote;
    }   
}


// % of Floor
if(smallMonthlyTotalBase_quote == 0){
    pctOfFloorSmall = 0.00;
}else{
    pctOfFloorSmall = round(((smallMonthlyTotalProposed_quote / smallMonthlyTotalBase_quote) - 1) * 100, 1);
}
if(largeMonthlyTotalBase_quote == 0){
    pctOfFloorLarge = 0.00;
}else{
    pctOfFloorLarge = round(((largeMonthlyTotalProposed_quote / largeMonthlyTotalBase_quote) - 1) * 100, 1);
}
if(totalERFFRFAdminBaseAmount_quote == 0){
    pctOfFloorFees = 0.00;
}else{
    pctOfFloorFees = round(((totalERFFRFAdminSellAmount_quote / totalERFFRFAdminBaseAmount_quote) - 1) * 100, 1);
}
if(grandTotalBase_quote == 0){
    pctOfFloorTotal = 0.00;
}else{
    pctOfFloorTotal = round(((grandTotalSell_quote / grandTotalBase_quote) - 1) * 100, 1);
}

// Determine Price Band to be displayed next to Quote Totals
priceBand = "<span class='price-band'>Price Band - ";
if (grandTotalSell_quote < grandTotalFloor_quote) {
    priceBand = priceBand + "Below Cost";
}elif (grandTotalSell_quote <= grandTotalBase_quote) {
    priceBand = priceBand + "Cost";
}elif (grandTotalSell_quote <= grandTotalTarget_quote) {
    priceBand = priceBand + "Floor";
}elif (grandTotalSell_quote <= grandTotalStretch_quote) {
    priceBand = priceBand + "Average";
}else {
    priceBand = priceBand + "Target";
}
priceBand = priceBand + "</span>";

// Reformat the ReasonDescription to give some space between multiple reasons
reasonsArr = split(ReasonDescription,";");
reasonsFormatted = "";
i=0;
for reason in reasonsArr{
    if(i == 0){
        reasonsFormatted = reasonsArr[i];
    }
    else{
        reasonsFormatted = reasonsFormatted + ",  " + reasonsArr[i];
    }
    i = i + 1;
}

// HTML HEADER AND STYLES
// Note that the funky breaking of tags is needed due to mod_security filtering
emailBody = "<ht" + "ml><head>"
          + "<st" + "yle type=\'text" + "/css\'>"
            + "body { margin-left: 35px; font-family: Arial, \'Open Sans\', sans-serif; }"
            + "p { font-size: 16px; }"
            + "h1 { text-align: right; background-color: #00ADEF; color: #fff; padding: 0 5px 3px 0; font-size: 24px;}"
            + "h2 { }"
            + "table { border-collapse: collapse; }"
            + "table, th, td {  border: 1px solid #000; font-size: 14px; }"
            + "th { background-color: #004A7C; color: #FFF; font-weight: normal; padding: 3px 5px 3px 5px;}"
            + "td { text-align: right; padding: 3px 3px 3px 3px;}"
            + "p.reason-description { padding-left: 20px; font-weight: bold; color: #004A7C;}"
            + ".quote-details," 
            + ".quote-details tr td { border: none; }"
            + ".quote-details tr td:first-child { font-weight: bold; vertical-align: top; }"
            + ".price-band { font-size: 18px; color: #004A7C; }"
            + ".totals { background-color: #00ADEF; color: #fff; }"
            + ".left { text-align: left; }"
            + ".right { text-align: right; }"
            + ".red { color: #ff0000; }"
            + ".white-background { background-color: #fff; color: #000; }"
            + ".bold { font-weight: bold; }"
            + ".pad-bottom { margin-bottom: 10px; }"
            + ".top { vertical-align: top; }"
            + ".hide { display: none; }"
            + ".approval-reason-list { margin: 0; padding: 0; list-style-type: none; }"
            + ".approval-reason-list li{ margin: 0 0 0 10px; padding: 0; }"
          + "</style>"
          + "</head>"
          + "<body>";



// HEADING AND SALUTATION
emailBody = emailBody + "<h1>QUOTE APPROVAL REQUIRED</h1>"
                      + "<img src='https://" + _system_company_name + ".bigmachines.com/bmfsweb/testrepublicservices/image/RP_HorizontalLatest.jpeg'/>"
                      + "<p>Dear " + approverFullName +",</p>"
                      + "<p>Quote "+ quoteNumber_quote + ", " + siteName_quote + " requires your approval for the following reason(s): </p>"
                      + "<p class='reason-description'>" + reasonsFormatted + "</p>";

// QUOTE DETAILS
emailBody = emailBody + "<h2>Quote Details</h2>"
                      + "<table class='quote-details'>"
                        + "<tr><td class='top'><b>Submitted By:</b></td><td class='left'>" + submittedByFullName +"</td></tr>"
                        + "<tr><td class='top'><b>Submitted Date:</b></td><td class='left'>" + substring(submittedDate_quote,0,10) +"</td></tr>"
                        + "<tr class='pad-bottom'><td class='top'><b>Comments:</b></td><td class='left'>" + submittersComments +"</td></tr>"
                        + "<tr><td class='top'><b>Approvals Required:</b></td><td class='left'>" + approvalReasonDisplayText_quote +"</td></tr>"
                        + "<tr><td class='top'><b>Quote Description:</b></td><td class='left'>" + quoteDescription_quote +"</td></tr>"
                        + "<tr><td class='top'><b>Sales Activity:</b></td><td class='left'>" + salesActivity +"</td></tr>"
                        + "<tr><td class='top'><b>Division Number:</b></td><td class='left'>" + division_quote +"</td></tr>"
                        + "<tr><td class='top'><b>Customer Name:</b></td><td class='left'>" + _quote_process_billTo_company_name +"</td></tr>"
                        + "<tr" + displayCompetitor + "><td class='top'><b>Competitor:</b></td><td class='left'>" + competitor +"</td></tr>"
                        + "<tr><td class='top'><b>Industry:</b></td><td class='left'>" + industry_quote +"</td></tr>"
                        + "<tr><td class='top'><b>Segment:</b></td><td class='left'>" + segment_quote +"</td></tr>"
                        + "<tr><td class='top'><b>Term:</b></td><td class='left'>" + initialTerm_quote +" Months</td></tr>"
                        + "<tr><td class='top'><b>Rate Restriction:</b></td><td class='left'>" + rateRestrictions +"</td></tr>"
                      + "</table>"; 

// CONTAINER DETAILS
ContIndex = 0;
if(NOT isempty(ContPDocNum)){
    emailBody = emailBody + "<h2>Container Details:</h2>"
                          + "<table><tr>"
                            + "<th>Qty</th>"  
                            + "<th>Container</th>"  
                            + "<th>Total Compactor Expense</th>"
                            + "<th>Total Service Time</th>"
                            + "<th>Tons Per Haul</th>"
                            + "<th>Disposal Site</th>"
                          + "</tr>";
    
    for eachCont in ContPDocNum{
        compactorValue = "N/A";
        if(isnumber(CompactorValArr[ContIndex]) AND atof(CompactorValArr[ContIndex]) > 0){
            compactorValue = formatascurrency(atof(CompactorValArr[ContIndex]),"USD");
        }

        emailBody = emailBody
                    + "<tr>" 
                      + "<td>" + ContQuantityArr[ContIndex] + "</td>"
                      + "<td>" + ContainerArr[ContIndex] + "</td>"
                      + "<td>" + compactorValue + "</td>"
                      + "<td>" + TotalServiceTimeArr[ContIndex] + "</td>"
                      + "<td>" + TonsPerHaulArr[ContIndex] + "</td>"
                      + "<td>" + DisposalsiteArr[ContIndex] + "</td>"
                    + "</tr>";
        ContIndex = ContIndex + 1;
    }
    emailBody = emailBody + "</table>";
}

// QUOTE TOTALS
emailBody = emailBody + "<h2>Quote Totals: " + priceBand + "</h2>";
emailBody = emailBody + "<table>"
                        + "<tr>"
                            + "<th></th>"   
                            + "<th>Cost Price</th>"
                            + "<th>Floor Price</th>"
                            + "<th>Average Price</th>"
                            + "<th>Target Price</th>"
                            + "<th>Proposed Price</th>"
                            + "<th>% of Floor</th>"
                        + "</tr>";
                        if(commercialExists_quote){
                            pctOfFloorStyle = "";
                            if (pctOfFloorSmall < 0) {
                                pctOfFloorStyle = " class='red'";
                            }
                            emailBody = emailBody +  "<tr>"
                                + "<td>" +  "Small Containers" + "</td>"
                                + "<td>" +  formatascurrency(smallMonthlyTotalFloor_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(smallMonthlyTotalBase_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(smallMonthlyTotalTarget_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(smallMonthlyTotalStretch_quote,"USD") + "</td>"
                                + "<td class='totals'>" +  formatascurrency(smallMonthlyTotalProposed_quote,"USD") + "</td>"
                                + "<td" + pctOfFloorStyle + ">" + string(pctOfFloorSmall) + "%</td>"
                            + "</tr>";
                        }
                        if(industrialExists_quote){
                            pctOfFloorStyle = "";
                            if (pctOfFloorLarge < 0) {
                                pctOfFloorStyle = " class='red'";
                            }
                            emailBody = emailBody +  "<tr>"
                                + "<td>" +  "Large Containers" + "</td>"
                                + "<td>" +  formatascurrency(largeMonthlyTotalFloor_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(largeMonthlyTotalBase_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(largeMonthlyTotalTarget_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(largeMonthlyTotalStretch_quote,"USD") + "</td>"
                                + "<td class='totals'>" +  formatascurrency(largeMonthlyTotalProposed_quote,"USD") + "</td>"
                                + "<td" + pctOfFloorStyle + ">" + string(pctOfFloorLarge) + "%</td>"
                            + "</tr>";
                        }
                        pctOfFloorStyle = "";
                        if (pctOfFloorFees < 0) {
                            pctOfFloorStyle = " class='red'";
                        }
                        emailBody = emailBody +  "<tr>"
                                + "<td>" +  "Fees" + "</td>"
                                + "<td>" +  formatascurrency(totalERFFRFAdminFloorAmount_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(totalERFFRFAdminBaseAmount_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(totalERFFRFAdminTargetAmount_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(totalERFFRFAdminStretchAmount_quote,"USD") + "</td>"
                                + "<td class='totals'>" +  formatascurrency(totalERFFRFAdminSellAmount_quote,"USD") + "</td>"
                                + "<td" + pctOfFloorStyle + ">" + string(pctOfFloorFees) + "%</td>"
                            + "</tr>";
                        pctOfFloorStyle = "";
                        if (pctOfFloorTotal < 0) {
                            pctOfFloorStyle = " class='red'";
                        }
                        emailBody = emailBody +   "<tr class='totals'>"
                                + "<td>" +  "Total Estimated Amount" + "</td>"
                                + "<td>" +  formatascurrency(grandTotalFloor_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(grandTotalBase_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(grandTotalTarget_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(grandTotalStretch_quote,"USD") + "</td>"
                                + "<td>" +  formatascurrency(grandTotalSell_quote,"USD") + "</td>"
                                + "<td class='white-background'><span" + pctOfFloorStyle + ">" + string(pctOfFloorTotal) + "%</span></td>"
                            + "</tr></table>";

emailBody = emailBody + "<h2>Financial Values:</h2>"
                      + financialSummaryForEmailTemplate_quote;

emailBody = emailBody + "<h2>Service Details:</h2>"
                      + lineItemGridForEmailTemplate_quote;


// ADDITIONAL ITEMS
if(NOT isempty(AdHocDescArr)){
    emailBody = emailBody + "<h2>Additional Items:</h2>"
                          + "<table>"
                          + "<tr>"
                            + "<th>Description</th>"    
                            + "<th>Quantity</th>"
                            + "<th>Total Price</th>"
                            + "<th>Billing Method</th>"
                          + "</tr>";
    
    eachAHDIndex = 0;
    for eachAHD in AdHocDescArr{
        
        emailBody = emailBody + "<tr>"
                                + "<td>" + AdHocDescArr[eachAHDIndex] + "</td>"
                                + "<td>" + AdHocQtyArr[eachAHDIndex] + "</td>"
                                + "<td>" + formatascurrency(atof(AdHocTotalPriceArr[eachAHDIndex]),"USD") + "</td>"
                                + "<td>" + AdHocBillingMethodArr[eachAHDIndex] + "</td>"
                              + "</tr>";
        eachAHDIndex = eachAHDIndex + 1;
    }                   
                        
    emailBody = emailBody + "</table>";
}

//Container Comparison Table
ConfigAttrStr = "";
ConfigAttrArr = string[];
NConfigAttrStr = "";
NConfigAttrArr = string[];
if(NOT isempty(SCDocNum)){
    emailBody = emailBody + "<h2>Container Comparison:</h2>";
    
    // Old Containers
    emailBody = emailBody + "<table>"
                        + "<tr>"
                            + "<th>Qty</th>"
                            + "<th>Old Container</th>"  
                            + "<th>Size</th>"
                            + "<th>Waste Type</th>"
                            + "<th>Frequency</th>"
                            + "<th>Price</th>"
                            + "<th>Fees</th>"
                        + "</tr>";
    
    for eachSC in SCDocNum{
        if(containskey(ConfigAttrDict, eachSC)){
            divison_config = getconfigattrvalue(eachSC, "division_config");
            quantity_readOnly = getconfigattrvalue(eachSC, "quantity_readOnly");
            wasteType_readOnly = getconfigattrvalue(eachSC, "wasteType_readOnly");
            containerSize_readOnly = getconfigattrvalue(eachSC, "containerSize_readOnly");
            liftsPerContainer_readOnly = getconfigattrvalue(eachSC, "liftsPerContainer_readOnly");
            wasteType_sc = getconfigattrvalue(eachSC, "wasteType_sc");
            quantity_sc = getconfigattrvalue(eachSC, "quantity_sc");
            containerSize_sc = getconfigattrvalue(eachSC, "containerSize_sc");
            liftsPerContainer_sc = getconfigattrvalue(eachSC, "liftsPerContainer_sc");
            hasCompactor_readOnly = getconfigattrvalue(eachSC, "hasCompactor_readOnly");
            containerCode_readOnly = getconfigattrvalue(eachSC, "containerCode_readOnly");
            containerCodes_SC = getconfigattrvalue(eachSC, "containerCodes_SC");
            
            bothContainers = util.getExcangeDescription(divison_config, quantity_readOnly, wasteType_readOnly, containerSize_readOnly, liftsPerContainer_readOnly, wasteType_sc, quantity_sc, containerSize_sc, liftsPerContainer_sc, hasCompactor_readOnly, containerCode_readOnly, containerCodes_SC);
            bothContainersArray = split(bothContainers, "|^|");
            
            append(oldContainerArray, bothContainersArray[0]);
            append(newContainerArray, bothContainersArray[1]);
            
            append(oldSizeArray, containerSize_readOnly);
            append(newSizeArray, containerSize_sc);
            append(oldNbrArray, quantity_readOnly);
            append(newNbrArray, quantity_sc);
            append(oldWtArray, wasteType_readOnly);
            append(newWtArray, wasteType_sc);
            append(oldFreqArray, liftsPerContainer_readOnly);
            append(newFreqArray, liftsPerContainer_sc);
            append(oldPriceArray, getconfigattrvalue(eachSC, "monthlyRevenue_sc"));
            // Need to calculate the Fees for the old container from totalWithFees - monthlyRevenue
            oldMonthlyRevenueStr = replace(getconfigattrvalue(eachSC, "monthlyRevenue_sc"),"$","");
            oldTotalWithFeesStr = replace(getconfigattrvalue(eachSC, "totalWithFees_sc"),"$","");

            oldFees = 0.0;
            oldTotalWithFees = 0.0;
            oldMonthlyRevenue = 0.0;
            if (isnumber(oldTotalWithFeesStr)) {
                oldTotalWithFees = atof(oldTotalWithFeesStr);
            }
            if (isnumber(oldMonthlyRevenueStr)) {
                oldMonthlyRevenue = atof(oldMonthlyRevenueStr);
            }
            oldFees = oldTotalWithFees - oldMonthlyRevenue;
            append(oldFeesArray, formatascurrency(oldFees,"USD"));

            if(containskey(newPriceDict, eachSC)){
                append(newPriceArray, get(newPriceDict, eachSC));
            }
            else{
                append(newPriceArray, "No New Price");
            }

            if(containskey(newFeesDict, eachSC)){
                append(newFeesArray, get(newFeesDict, eachSC));
            }
            else{
                append(newFeesArray, "No New Fee");
            }
            
        }
    }
    
    indexOld = 0;
    for eachSCOld in SCDocNum{      
        emailBody = emailBody + "<tr>"
                + "<td>" + oldNbrArray[indexOld] + "</td>"
                + "<td>" + oldContainerArray[indexOld] + "</td>"
                + "<td>" + oldSizeArray[indexOld] + "</td>"
                + "<td>" + oldWTArray[indexOld] + "</td>"
                + "<td>" + oldFreqArray[indexOld] + "</td>"
                + "<td>" + oldPriceArray[indexOld] + "</td>"
                + "<td>" + oldFeesArray[indexOld] + "</td>"
                + "</tr>"; 
        indexOld = indexOld + 1;
    }
    
    // New Containers
    emailBody = emailBody + "<tr>"
                            + "<th>Qty</th>"
                            + "<th>New Container</th>"  
                            + "<th>Size</th>"
                            + "<th>Waste Type</th>"
                            + "<th>Frequency</th>"
                            + "<th>Price</th>"
                            + "<th>Fees</th>"
                        + "</tr>";
    indexNew = 0;
    for NeachSC in SCDocNum{
            
            emailBody = emailBody + "<tr>"
                    + "<td>" + newNbrArray[indexNew] + "</td>"
                    + "<td>" + newContainerArray[indexNew] + "</td>"
                    + "<td>" + newSizeArray[indexNew] + "</td>"
                    + "<td>" + newWTArray[indexNew] + "</td>"
                    + "<td>" + newFreqArray[indexNew] + "</td>"
                    + "<td>" + newPriceArray[indexNew] + "</td>"
                    + "<td>" + newFeesArray[indexNew] + "</td>"
                + "</tr>";
            
            indexNew = indexNew + 1;
        
    }
    emailBody = emailBody + "</table>";
}

emailBody = emailBody + "<p>Please reply to this e-mail with the word <span class='bold'>Approve</span> or <span class='bold'>Reject</span>.<br />"
                      + "Alternatively, you can also <a href='"+ TransactionURL + "'>click this link to approve or reject the quote</a> within Capture.</p>"
                      + "</body></ht" + "ml>";

return emailBody;
