result = "";
result = result + "1~emailedDate_quote~" + getstrdate() + "|";
result = result + "1~lastContactedDate_quote~" + getstrdate() + "|";
return result + commerce.setRateRestrictions() + commerce.captureAdHocConfigInfoToPrintOnOutput() + commerce.printing()+commerce.setStatus("email") + commerce.updateSiteAddressFIelds()  + commerce.commentsForDeliveryAndExchangeCreditOnOutput() + commerce.unconfiguredServicesString();