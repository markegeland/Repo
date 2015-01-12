ctr = 0;
VALUE_DELIM = "^_^";
RECDELIM = "@_@";
COLDELIM = "#_#";
str = "";
if(closestDisposalSitesCoordinates <> ""){
	index = find(closestDisposalSitesCoordinates, RECDELIM+"returnLocations"+COLDELIM);
	str = substring(closestDisposalSitesCoordinates, 0, index);
	tempArray = split(str, RECDELIM);
	for each in tempArray{
		ctr = ctr + 1;
	}
	
}
if(ctr == 0){
	ctr = 1;
}
return ctr;