/* 
Test Transaction: 5665313
================================================================================
Name:   Recommendation Rule - Box Mail Back Weight Calc
Author:   Mark Egeland
Create date:  20150715
Description:  #710 Calculates the total weight of all selected products and sets boxMailTotalWeight_er attribute.
        
Input:      boxMailProductQty_er:  Integer Array - Holds quantity of box mail products
            boxMailProductWeight_er:  Float Array - Holds weight of box mail products
                    
Output:     totalWeight

Updates:    20150715 - Mark Egeland - Example Update
=====================================================================================================
*/

totalWeight = 0;

counter = range(sizeofarray(boxMailProductQty_er));

for each in counter{
	totalWeight = totalWeight + (boxMailProductQty_er[each] * boxMailProductWeight_er[each]);
}

return totalWeight;