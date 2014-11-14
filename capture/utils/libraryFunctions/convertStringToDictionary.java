/*
	Create by: Srikar M
	Purpose: Converting a key value pair delimited string into string dictionary
	inputDelimStr 	: 	Delimited String
	valueDelim 	:	Delimiter between key and value
	attrDelim	:	Delimiter between attributes
*/
returnDict = dict("string");

if(inputDelimStr <> ""){
	attributesArray = split(inputDelimStr,attrDelim);
	for eachAttr in attributesArray{
		if(eachAttr <> ""){
			thisAttrArray = split(eachAttr,valueDelim);
			if(sizeofarray(thisAttrArray) == 2){
				put(returnDict,thisAttrArray[0],thisAttrArray[1]);
			}
		}
	} 
}
return returnDict;