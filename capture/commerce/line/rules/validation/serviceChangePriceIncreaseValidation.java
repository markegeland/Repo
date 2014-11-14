system_result = false;
indexExpression1 = (rateType_line == "Base");
indexExpression2 = (priceType_line == "Containers");
indexExpression3 = (activity_line == "Price adjustment");
indexExpression4 = ((priceAdjustmentReason_line == "Price Increase: Personally Secured") OR (priceAdjustmentReason_line == "Price Increase: Contractually Obligated"));
indexExpression5 = (priceIncreaseType_line == "New Rate");

maxPrice = currentPrice_line;
if(indexExpression5 == true) {
	sellPrice = adjustment_line;
}
else {
	sellPrice = maxPrice + (maxPrice * (adjustment_line/100));
}
indexExpression6 = (sellPrice <= maxPrice);

system_result = system_result OR (indexExpression1 AND indexExpression2 AND indexExpression3 AND indexExpression4 AND indexExpression6);
return system_result;