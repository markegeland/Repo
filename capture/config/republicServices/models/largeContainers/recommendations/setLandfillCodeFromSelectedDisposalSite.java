RECDELIM = "@_@";
COLDELIM = "#_#";
VALUE_DELIM = "^_^";
arrIndx = 0;
siteName = "";
landFillCode = "";
disposalSiteNum = 0;
if(isnumber(alternateSite_l)){
	disposalSiteNum = atoi(alternateSite_l);
}
for each in site_disposalSite{
	if(arrIndx == (disposalSiteNum-1)){
		siteName = each;
		break;
	}
	arrIndx = arrIndx + 1;
}

disposalSitesArr = split(closestDisposalSitesCoordinates, RECDELIM);

for eachElement in disposalSitesArr{
	elementsDict = util.convertStringToDictionary(eachElement, VALUE_DELIM, COLDELIM);
	if(containskey(elementsDict, "Site_Name")){
		thisSiteName = get(elementsDict, "Site_Name");
		if(thisSiteName == siteName){
			if(containskey(elementsDict, "landFillCode")){
				landFillCode = get(elementsDict, "landFillCode");
				break;
			}
		}
	}
}

return landFillCode;