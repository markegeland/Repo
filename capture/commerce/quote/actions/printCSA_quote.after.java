result = "";

DATE_PREFERENCE = "MM/dd/yyyy";
result = result + "1~lastContactedDate_quote~" + getstrdate() + "|";

result = result + "1~printedDate_quote~" + getstrdate() + "|";
return result + commerce.setRateRestrictions() + commerce.captureAdHocConfigInfoToPrintOnOutput() + commerce.printing()+commerce.setStatus("print") + commerce.updateSiteAddressFIelds() + commerce.commentsForDeliveryAndExchangeCreditOnOutput() + commerce.unconfiguredServicesString();