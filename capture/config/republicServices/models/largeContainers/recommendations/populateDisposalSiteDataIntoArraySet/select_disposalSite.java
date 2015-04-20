returnArray = string[];
findWhat = "counter";
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
		append(returnArray, string(ctr + 1));
		ctr = ctr + 1;
	}
	
}
return returnArray;