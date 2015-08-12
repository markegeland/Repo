/* 
================================================================================
Name:    boxMailAssetRegistrationQty_er
Author:   Mark Egeland
Create date:  20150728
Description:  #710 Prevents asset registration qty from being greater than the total products entered
        
Input:      boxMailProductQty_er - Integer Array: Holds the quantity for the products in the box mail-back configurator
			  assetRegistrationQty_er - Integer: Holds the entered qty for asset registration
                    
Output:    Integer: value to constrain

Updates:    
=====================================================================================================
*/
result = -1;
total = 0;

for each in boxMailProductQty_er{
	total = total + each;
}

if(total > 0 and total < assetRegistrationQty_er){
	result = assetRegistrationQty_er;
}

return result;