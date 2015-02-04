retStr = "";
retStr = retStr +  "1~isSaleFinalized_quote~true|" +commerce.printing() + commerce.setStatus("finalizeContract") + commerce.setTransactionCode() + commerce.unconfiguredServicesString();
retStr = retStr + "1~saleFinalizedBy_quote~" + _system_user_first_name + " "  + _system_user_last_name + "|";
//Comments section on CSA needs only the letter in Lead Source Code
leadCodeOnCSA = "";
if(leadSourceCode_quote <> ""){
    leadSouceCodeArr = split(leadSourceCode_quote, " - ");
    leadCodeOnCSA = leadSouceCodeArr[0];
}
retStr = retStr + "1~leadSourceCodeOnCSA_quote~" + leadCodeOnCSA + "|";
return retStr;