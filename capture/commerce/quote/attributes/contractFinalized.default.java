// 20150402 John Palubinskas - #449 updated for reason code storing just code instead of full description

SIGNED = "Customer Accepted: Signed";
NOT_SIGNED = "Customer Accepted: Did not sign";
REJECTED = "Customer Rejected";

retStr = "";

if(contractStatus_quote == SIGNED){
    retStr = "Customer Accepted - Signed";
}elif(contractStatus_quote == NOT_SIGNED){
    retStr = "Customer Accepted - Not Signed";
}elif(contractStatus_quote == REJECTED){
    retStr = "Customer Rejected - " + reasonCode_quote;
    if (reasonCode_quote == "02") {
        retStr = retStr + " Lost to Competitor";
    }
    elif (reasonCode_quote == "18") { retStr = retStr + " Service Issues"; }
    elif (reasonCode_quote == "21") { retStr = retStr + " Closed Business"; }
    elif (reasonCode_quote == "56") { retStr = retStr + " Competitor Pricing"; }
    else{ 
        retStr = retStr + " Price Increase"; 
    }
}

return retStr;