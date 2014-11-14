system_result = false;
indexExpression1 = (rateType_line == "Base");
indexExpression2 = (priceType_line == "Service Change");
indexExpression3 = (activity_line == "Price adjustment");
indexExpression4 = ((priceAdjustmentReason_line == "Rollback of Current Price") OR (priceAdjustmentReason_line == "Rollback of PI") OR (priceAdjustmentReason_line == "Rollback: Competitive Bid"));

sellPrice = sellPrice_line;
maxPrice = totalStretchPrice_line;
indexExpression5 = (sellPrice  > maxPrice);

system_result = system_result OR (indexExpression1 AND indexExpression2 AND indexExpression3 AND indexExpression4 AND indexExpression5);
return system_result;