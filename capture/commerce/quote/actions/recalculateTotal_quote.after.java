//Sets useDesiredPrice to false after formulas run to avoid the Sell Price being reset to the expected value
return commerce.postPricingFormulas("save") + "1~useDesiredPrice_quote~false|";