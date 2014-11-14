/* BML Utilities (getVolumePricing) - Get the volume prices based on Part Number AND Qty
@param 
	- partNumList (String[]) 
	- partNumDict (String[] Dictionary) --> Key: part number, value is a string[] which contains document numbers 
	- qtyDict (Integer Dictionary) --> Key: document number, value: qty 
	- priceDict (Float Dictionary) --> Key: document number, value: price 
	- accumulate (String) : None, Tiered, Aggregate
@return 
	- priceDict (Float Dictionary) 
*/ 

result = dict("float");
numDecimal = 2;

volumePrices = dict("float[][]");

minVol = 0.0;
maxVol = 0.0;

partVolPriceListMinVolIndex = 0;
partVolPriceListMaxVolIndex = 1;
partVolPriceListPriceIndex = 2;
price = 0.0;
priceInDict = 0.0;
currentQty = 0;

partVolumePricing = bmql("SELECT partNum, minVol, maxVol, price FROM partsVolumePricing WHERE partNum IN $partNumList");

// put the volume price data into a dictionary in the following format:
// partnum as key
// list of prices 
if ( isnull(partVolumePricing) ) {
	put(result, "error", -1.0);
}
else {
	// there are some element in the volume pricing table 
	for row in partVolumePricing {
		size = 0;
		partVolPriceList = float[][];
		// get current row's part number
		currPartNumber = get(row, "partNum");
		if ( containskey(volumePrices, currPartNumber) ) {
			partVolPriceList = get(volumePrices, currPartNumber);
			size = sizeofarray(partVolPriceList);
		} 
		partVolPriceList[size][partVolPriceListMinVolIndex] = getfloat(row, "minVol");
		partVolPriceList[size][partVolPriceListMaxVolIndex] = getfloat(row, "maxVol");
		partVolPriceList[size][partVolPriceListPriceIndex] = getfloat(row, "price");
		put(volumePrices, currPartNumber, partVolPriceList);
	}

	for partNum in partNumList {
		partNumDocNumList = get(partNumDocDict, partNum);
		qty = 0;
		
		// sums quantity of all part nums that are the same
		for eachDocNum in partNumDocNumList{
			if(containskey(qtyDict, eachDocNum)){
				qty = qty + get(qtyDict, eachDocNum);
			}
		}
		
		if(qty <> 0 ){
			for eachDocNum in partNumDocNumList{
				currentQty = qty;
				orgPrice = get(priceDict, eachDocNum);
				finalMaxVol = 0;
				finalMaxRangePrice = 0;
				priceInDict = orgPrice;
			
			
				if ( not(containskey(volumePrices, partNum) ))	{
					put(result, eachDocNum, priceInDict);
				}
				else{
					priceInDict = 0;
					partVolPriceList = get(volumePrices, partNum);
					for eachPartVolPriceRow in partVolPriceList {
						currentQty = qty;
						minVol = eachPartVolPriceRow[partVolPriceListMinVolIndex];
						maxVol = eachPartVolPriceRow[partVolPriceListMaxVolIndex];
						price = eachPartVolPriceRow[partVolPriceListPriceIndex];
						
						if(accumulate == "Tiered" ) {
							//print "Tiered Pricing";
							if( qty >= minVol AND qty <= maxVol){
								priceInDict = price;
							} 
						}
						else{
							// Accumulate pricing and average all of them 
							if ( containskey(result, eachDocNum) ) {
								// Get the accumulated price
								priceInDict = get(result, eachDocNum);
			
							}
							
							if ( currentQty >= minVol AND currentQty <= maxVol ) {
							// donothing
								currentQty = currentQty - minVol + 1;
							}
							elif ( currentQty >= maxVol ) {
								currentQty = maxVol - minVol + 1;
							}
														
							priceInDict = priceInDict + price * currentQty;
						}
						
						if(finalMaxVol < maxVol){
							finalMaxVol = maxVol;
							finalMaxRangePrice = price;
						}
						
						priceInDict = round(priceInDict, numDecimal);
						put(result, eachDocNum, priceInDict);
						
						if ( qty <= maxVol AND qty >= minVol ) {
							break;
						}
						
					}
					
					priceInDict = get(result, eachDocNum);
			
					// if qty isn't in the table, adjust price
					if(qty > finalMaxVol){
						if(accumulate == "Tiered") {
							priceInDict = finalMaxRangePrice;
						}
						else{
							remainingQty = qty - finalMaxVol;
							priceInDict = priceInDict + finalMaxRangePrice * remainingQty;
						}
					
						priceInDict = round(priceInDict , numDecimal);
						put(result, eachDocNum, priceInDict);
					}
					
					unitPrice = priceInDict;
					extUnitPrice = 0.0;
					if ( accumulate == "Aggregate" ) {
						if ( qty > 0 ) {
							unitPrice = priceInDict / qty;
						}
					}
					
					unitPrice = round(unitPrice, numDecimal);
					put(result, eachDocNum, unitPrice);
				}
			
			} // END for eachDocNum in partNumDocNumList
		} // END if (qty <> 0)
	} // END for partNum in partNumList
} // END level 1 else

return result;