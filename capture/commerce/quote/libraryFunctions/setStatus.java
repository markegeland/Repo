// Commerce BML Function: setStatus
// Purpose: Sets the status based on the action performed with the conditions
//
// Updates
// -----------------------------------------------------------------------------------------------
// 20141022 - John Palubinskas - Modified to keep the finalized status set once the contractStatus_quote is populated.  Issue 864.
// 20140912 - John Palubinskas - Modified to remove the status Quote Finalized and instead display Proposal/CSA with Customer 
// 20150429 - Gaurav Dawar     - #558 Fixed the contract status on CSA to be "05" only when renewal term is MTM and rep selects "signed".
// 20150428 - Mike Boylan      - #501 Used the finalizeContract step in the conditional to prevent 
//                               status from changing on actions other than Finalize on the finalize page.
// 20150325 - Rob Brozyna      - #518 Modified to set opportunityStage_quote. TODO: What is the opportunity stage when closing the site?
// 20150428 - Mike Boylan      - #501 Used the finalizeContract step in the conditional to prevent status from changing on actions other 
//                               than Finalize on the finalize page.
// 20150430 - Rob Brozyna      - #518 Modified to correctly set the Opp Stage to "Propose" even if the user does not print the document. 
//                               Refactored currentStep -> currentStatus
//                               Created new currentStep = _system_current_step_var
// 20150506 - Mike Boylan      - #501 Oracle did not take into account the state of _system_current_step_var
//                               when they provided their solution.  I corrected this.
// 20150506 - John Palubinskas - #518 set opportunityReadOnly_quote = true where newStage = Close or Lost
// -----------------------------------------------------------------------------------------------
res="";
contractStatusCode = contractStatusSite_quote;
currentStatus=status_quote;
retStr = "";
currentStep = _system_current_step_var;
currentStage = opportunityStage_quote;
newStage = "";
opportunityReadOnly = false;

if(actionName == "next" OR actionName == "previous"){ 
/*steps-  1) New Customer and Site 
          2) Existing Customer Select Account/Site 
          3) Existing Customer view Account/Site 
          4) Select Services 
          Till these 4 steps status will be work in progress*/
    res="Work In Progress";
    newStage = "Configure";
}elif(actionName == "request_approval"){  //step- Adjust Pricing
    res="Pending Approval";
    newStage = currentStage;
}elif(actionName == "submit"){ //step-Submitted for approval
    res="Approved";
    newStage = currentStage;
}elif(actionName == "revise"){ //step-Submitted for approval
    res="Revision in Progress";
    newStage = currentStage;
}elif(actionName == "reject"){ //step-Submitted for approval
    res="Rejected";
    newStage = currentStage;
}elif(actionName == "print"){ // step - Generate Documents
    if(chooseProposal_quote == true){
        res="Proposal with Customer";
        if(currentStage == "Configure") { //Only move from Configure to Propose, and not backwards.
            newStage = "Propose";
        } else {
            newStage = currentStage;
        }
    }
    if(chooseCSA_quote == true){ 
        res="CSA with Customer";
        if(currentStage == "Configure") { //Only move from Configure to Propose, and not backwards.
            newStage = "Propose";
        } else {
            newStage = currentStage;
        }
    }elif(chooseProposal_quote == true AND chooseCSA_quote == true){
        res="CSA with Customer";
        newStage = "Propose";
    }   
}elif(actionName == "email"){ // step - Generate Documents
    if(chooseProposal_quote == true AND chooseCSA_quote == true){
        res="CSA with Customer";
        if(currentStage == "Configure") { //Only move from Configure to Propose, and not backwards.
            newStage = "Propose";
        } else {
            newStage = currentStage;
        }
    }elif(chooseCSA_quote == true){
        res="CSA with Customer";
        if(currentStage == "Configure") { //Only move from Configure to Propose, and not backwards.
            newStage = "Propose";
        } else {
            newStage = currentStage;
        }
    }elif(chooseProposal_quote == true){
        res="Proposal with Customer";
        if(currentStage == "Configure") { //Only move from Configure to Propose, and not backwards.
            newStage = "Propose";
        } else {
            newStage = currentStage;
        }
    }
}elif(actionName == "finalizeContract"){ // step - Generate Documents
    // User clicked Next to get to Finalize step. Do not display Quote Finalized, instead display CSA with Customer
    // until the CSA was acted upon.
    if(chooseProposal_quote == true AND chooseCSA_quote == true){
        res="CSA with Customer";
        newStage = "Propose";
    }elif(chooseCSA_quote == true){
        res="CSA with Customer";
        newStage = "Propose";
    }elif(chooseProposal_quote == true){
        res="Proposal with Customer";
        newStage = "Propose";
    }
    else {
        res="Work in Progress";
        newStage = "Configure";
    }
    // Set contractStatusCode
    if(contractStatus_quote == "Customer Accepted: Signed"){
        contractStatusCode = "01";
        newStage = "Close";
        opportunityReadOnly = true;
    }elif(contractStatus_quote == "Customer Accepted: Did not sign"){
        contractStatusCode = "02";
        newStage = "Close";
        opportunityReadOnly = true;
    }
    if(renewalTerm_quote == "1" AND contractStatusCode == "01"){
        contractStatusCode = "05";
    }
    /*20150430 - Rob Brozyna - ActionName "finalizeContract" is used both in finalizeQuote_quote action, 
      which is on the generateDocuments step, and finalizeContract_quote action, which has a display name 
      of "Finalize" and is on the subsequent step (submitted_process). Therefore, in order to support moving 
      the Stage to "Propose" if the user just moves through generateDocuments without actually printing, 
      I am adding the following condition:*/
    if(currentStep == "generateDocs"){
        newStage = "Propose";
    }
    
}elif(actionName == "expire"){ // step- Generate Documets (Quote will go to expired step if it excceds 90 days in this step)
    res="Expired";
    newStage = currentStage;
}elif(actionName == "abandon"){ // once quote created and till the Adjust Pricing it exceeded 120 days then quote will be automatically goes to Abandoned(goes to Abandoned step).
    res="Abandoned";
    newStage = currentStage;
}elif(actionName == "trash"){ // except in start step every step has this action
    res=currentStatus +" To Deleted";
    newStage = currentStage;
}elif(actionName == "assign"){ // except start step every  step has this action
    res="Flipped – Not Claimed";
    newStage = currentStage;
}elif(actionName == "claim"){ // step - Assigned - Not Claimed
    res="Superseded";
    newStage = currentStage;
}

// Finalize Button was Clicked
if(_system_current_step_var == "submitted_process") {
    if(contractStatus_quote == "Customer Accepted: Signed" OR 
       contractStatus_quote == "Customer Accepted: Did not sign") {
        res="Customer Accepted";
    }elif(contractStatus_quote == "Customer Rejected") {
        res="Customer Rejected";
        newStage = "Lost";
        opportunityReadOnly = true;
    }elif(contractStatus_quote == "Close Account") {
        res="Lost Account";
        newStage = "Lost";
        opportunityReadOnly = true;
    }elif(contractStatus_quote == "Close Site") {
        res="Closed Site";
        //TODO - what is the Opp stage if we're closing the site?
    }   
}


retStr = retStr + "1~status_quote~"+res+"|";
retStr = retStr + "1~contractStatusSite_quote~"+contractStatusCode+"|";
retStr = retStr + "1~opportunityStage_quote~" + newStage + "|";
retStr = retStr + "1~opportunityReadOnly_quote~" + string(opportunityReadOnly) + "|";

return retStr;
