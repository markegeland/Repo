// Commerce BML Function: setStatus
// Purpose: Sets the status based on the action performed with the conditions
//
// Updates
// -----------------------------------------------------------------------------------------------
// 20141022 - John Palubinskas - Modified to keep the finalized status set once the contractStatus_quote
//                               is populated.  Issue 864.
// 20140912 - John Palubinskas - Modified to remove the status Quote Finalized and instead display
//             					 Proposal/CSA with Customer 
// 20150429 - Gaurav Dawar - #558 - Fixed the contract status on CSA to be "05" only when renewal term is MTM and rep selects "signed".
// -----------------------------------------------------------------------------------------------
res="";
contractStatusCode = contractStatusSite_quote;
currentStep=status_quote;
retStr = "";

if(actionName == "next" OR actionName == "previous"){ 
/*steps-  1) New Customer and Site 
		  2) Existing Customer Select Account/Site 
		  3) Existing Customer view Account/Site 
		  4) Select Services 
		  Till these 4 steps status will be work in progress*/
	res="Work In Progress";
}elif(actionName == "request_approval"){  //step- Adjust Pricing
	res="Pending Approval";
}elif(actionName == "submit"){ //step-Submitted for approval
	res="Approved";
}elif(actionName == "revise"){ //step-Submitted for approval
	res="Revision in Progress";
}elif(actionName == "reject"){ //step-Submitted for approval
	res="Rejected ";
}elif(actionName == "print"){ // step - Generate Documents
	if(chooseProposal_quote == true){
		res="Proposal with Customer";
	}
	if(chooseCSA_quote == true){ 
		res="CSA with Customer";
	}elif(chooseProposal_quote == true AND chooseCSA_quote == true){
		res="CSA with Customer";
	}	
}elif(actionName == "email"){ // step - Generate Documents
	if(chooseProposal_quote == true AND chooseCSA_quote == true){
		res="CSA with Customer";
	}elif(chooseCSA_quote == true){
		res="CSA with Customer";
	}elif(chooseProposal_quote == true){
		res="Proposal with Customer";
	}
}elif(actionName == "finalizeContract"){ // step - Generate Documents
	// User clicked Next to get to Finalize step. Do not display Quote Finalized, instead display CSA with Customer
	// until the CSA was acted upon.
	if(chooseProposal_quote == true AND chooseCSA_quote == true){
		res="CSA with Customer";
	}elif(chooseCSA_quote == true){
		res="CSA with Customer";
	}elif(chooseProposal_quote == true){
		res="Proposal with Customer";
	}
	else {
		res="Work in Progress";
	}
	// Set contractStatusCode
	if(contractStatus_quote == "Customer Accepted: Signed"){
		contractStatusCode = "01";
	}elif(contractStatus_quote == "Customer Accepted: Did not sign"){
		contractStatusCode = "02";
	}
	if(renewalTerm_quote == "1" AND contractStatusCode == "01"){
		contractStatusCode = "05";
	}
}elif(actionName == "expire"){ // step- Generate Documets (Quote will go to expired step if it excceds 90 days in this step)
	res="Expired";
}elif(actionName == "abandon"){ // once quote created and till the Adjust Pricing it exceeded 120 days then quote will be automatically goes to Abandoned(goes to Abandoned step).
	res="Abandoned";
}elif(actionName == "trash"){ // except in start step every step has this action
	res=currentStep +" To Deleted";
}elif(actionName == "assign"){ // except start step every  step has this action
	res="Flipped – Not Claimed";
}elif(actionName == "claim"){ // step - Assigned - Not Claimed
	res="Superseded";
}

// CSA was acted upon, set the final status no matter which action is chosen
if(contractStatus_quote == "Customer Accepted: Signed" OR contractStatus_quote == "Customer Accepted: Did not sign"){
	res="Customer Accepted";
}elif(contractStatus_quote == "Customer Rejected"){
	res="Customer Rejected";
}elif(contractStatus_quote == "Close Account"){
	res="Lost Account";
}elif(contractStatus_quote == "Close Site"){
	res="Closed Site";
}

retStr = retStr + "1~status_quote~"+res+"|";
retStr = retStr + "1~contractStatusSite_quote~"+contractStatusCode+"|";

return retStr;