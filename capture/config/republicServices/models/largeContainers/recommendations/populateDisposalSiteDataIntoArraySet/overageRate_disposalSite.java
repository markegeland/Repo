returnArray = float[];
findWhat = "overage";
VALUE_DELIM = "^_^";
RECDELIM = "@_@";
COLDELIM = "#_#";
str = "";
if(closestDisposalSitesCoordinates <> ""){
	index = find(closestDisposalSitesCoordinates, RECDELIM+"returnLocations"+COLDELIM);
	str = substring(closestDisposalSitesCoordinates, 0, index);
	tempArray = split(str, RECDELIM);
	ctr = 0;
	for each in tempArray{
		resultDict = util.convertStringToDictionary(each, VALUE_DELIM, COLDELIM);
		res = 0.0;
		if(containskey(resultDict, findWhat)){
			res = atof(get(resultDict, findWhat));
		}
		append(returnArray, res);
	}
	
}else{
	returnArray[0]= 0.0;
}
return returnArray;