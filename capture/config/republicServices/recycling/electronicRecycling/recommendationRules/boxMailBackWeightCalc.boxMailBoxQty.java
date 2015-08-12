/* 
Test Transaction: 5665313
================================================================================
Name:   Recommendation Rule - Box Mail Back Weight Calc
Author:   Mark Egeland
Create date:  20150716
Description:  #710 Calculates the total weight of all selected products and sets boxMailTotalWeight_er attribute.
        
Input:      boxMailProductQty_er:  Integer Array - Holds quantity of box mail products
			  boxMailProductWeight_er:  Float Array - Holds weight of box mail products
			  boxMailBoxLimit_er:  Float Array - Holds box weight limits
                    
Output:     boxQtyArray:  Integer Array - Holds qty for each box size

Updates:    
=====================================================================================================
*/

boxQtyArray = Integer[sizeofarray(boxMailBoxLimit_er)];
orderedLimitArray = Float[];
boxQtyDict = dict("integer");
totalWeight = 0.0;

/* =====================  Calculate Total Weight ============================ */
counter = range(sizeofarray(boxMailProductQty_er));

for each in counter{
	totalWeight = totalWeight + (boxMailProductQty_er[each] * boxMailProductWeight_er[each]);
}
/* =====================  End Calculate Total Weight ========================= */

// Store the order of the original array so we can ensure our return string is in the correct order
for each in boxMailBoxLimit_er{
	append(orderedLimitArray, each);
}

orderedLimits = sort(boxMailBoxLimit_er, "desc");

/* ======================  Calculate Boxes Needed ========================= */
remainingWeight = totalWeight;
lastWeight = 0;
for each in orderedLimits{

	// If remaining weight is greater than the current box limit add one of the next largest boxes and break loop
	if(remainingWeight > each AND lastWeight <> 0){
		if(containskey(boxQtyDict, lastWeight)){
			put(boxQtyDict, lastWeight, get(boxQtyDict, lastWeight) + 1);
		}else{
			put(boxQtyDict, lastWeight, 1);
		}
		break;
	}
	
	// If remaining weight is greater or equal to current box limit calculate how many boxes needed
	if(remainingWeight >= each){
		currentBoxCount = integer(remainingWeight / each);
		remainingWeight = fmod(remainingWeight, each);
		
		if(containskey(boxQtyDict, each)){
			put(boxQtyDict, each, get(boxQtyDict, each) + currentBoxCount);
		}else{
			put(boxQtyDict, each, currentBoxCount);
		}
	}
	
	lastWeight = each;
}

// Check if there is any weight left over and put in smallest box
if(remainingWeight >= 0){
	if(containskey(boxQtyDict, lastWeight)){
		put(boxQtyDict, lastWeight, get(boxQtyDict, lastWeight) + 1);
	}else{
		put(boxQtyDict, lastWeight, 1);
	}
}
/* ======================  End Calculate Boxes Needed ========================= */

/* ======================  Build Return Value  ============================== */
counter = range(sizeofarray(boxMailBoxLimit_er));
for each in counter{

	if(containskey(boxQtyDict, orderedLimitArray[each])){
		boxQtyArray[each] = get(boxQtyDict, orderedLimitArray[each]);
	}else{
		boxQtyArray[each] = 0;
	}
}
/* ======================  End Build Return Value  ============================== */

return boxQtyArray;