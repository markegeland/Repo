/*SR 3-9470906761 BY Yi
If chooseProposal_quote or chooseCSA_quote is unchecked,
the corresponding checkboxs will be unchecked.*/ 

ret = "";
if(NOT(chooseProposal_quote)){
	ret = ret + "1~includeCoverLetter_quote~false|1~proposalMarketingCollateral1_quote~false|1~proposalMarketingCollateral2_quote~false|";
}
if(NOT(chooseCSA_quote)){
	ret = ret + "1~includeSampleInvoice_quote~false|1~csaMarketingCollateral1_quote~false|1~csaMarketingCollateral2_quote~false|";
}
return ret;