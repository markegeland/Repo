result = maxDiscountDict;
/*
  Get Max Discount 2 
    maxDiscountDict = util.getMaxDiscount(partNumList, partNumDocDict, familyDict, maxDiscountDict);
  parameter:
    partNumList  - String[]
    partNumDocDict - String[] dictionary
    familyDict - String[];
    maxDiscountDict = float dictionary
    groups = String
  result: 
      maxDiscountDict = float dictionary
    


listGroups = split(groups, "+");

result = maxDiscountDict;
groupsParam = string[]{""};
familyParam = string[]{""};
partNumParam = string[]{""};
// check all parameters used in bmql
if(sizeofarray(listGroups) > 0){
	groupsParam = listGroups;
}
if(sizeofarray(familyList) > 0){
	familyParam = familyList;
}
if(sizeofarray(partNumList) > 0){
	partNumParam = partNumList;
}

partMaxDiscount = bmql("SELECT item, maxDiscountPercent FROM maxDiscounts WHERE salesGroup IN $groupsParam AND (item IN $familyParam OR item IN $partNumParam)");

maxDiscountPercentDict = dict("float");
maxDiscountPercent = 0.0;

for row in partMaxDiscount{
	maxDiscount = get(row, "maxDiscountPercent");
	if ( isnumber(maxDiscount) ) {
		put(maxDiscountPercentDict, get(row, "item"), atof(maxDiscount));
	}
} 
docNumList = String[];
maxDiscountFamily = 0.0;
maxDiscountPart = 0.0;

for partNum in partNumList {
	docNumList = get(partNumDocDict, partNum);
	
	for eachDocNum in docNumList {
		maxDiscountFamily = 0.0;
		maxDiscountPart = 0.0;
		maxDiscount = 0.0;
		if ( containskey(familyDict, eachDocNum) ) {
			value = get(familyDict, eachDocNum);
			if ( containskey(maxDiscountPercentDict, value) ) {
				maxDiscountFamily = get(maxDiscountPercentDict, value);
			} 
		}
		if ( containskey(maxDiscountPercentDict, partNum) ) {
			maxDiscountPart = get(maxDiscountPercentDict, partNum);
		}
		if ( maxDiscountFamily > maxDiscountPart ) {
			maxDiscount = maxDiscountFamily;
		} 
		else {
			maxDiscount = maxDiscountPart;
		} 
		put(result, eachDocNum, maxDiscount);
	} // END for eachDocNum in docNumList
} */// END for partNum in partNumList 

return result;