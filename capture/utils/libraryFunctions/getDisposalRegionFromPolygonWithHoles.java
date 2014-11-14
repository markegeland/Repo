DELIM = "^_^";
polygonRegionDict = dict("string");
geoCodeArr = split(geoCodes,DELIM);
siteLatitude = 0.0;
siteLongitude = 0.0;
if(sizeofarray(geoCodeArr) > 0){
	siteLatitude =  atof(geoCodeArr[0]);
}
if(sizeofarray(geoCodeArr) > 1){
	siteLongitude =  atof(geoCodeArr[1]);
}

isInside = false;

polygonIdArr = string[];
polygonArr = float[][];
coordinatesSet = bmql("SELECT PolygonId, PolygonRegion FROM CoordinatesWithHoles");
resultPolygon = "";

for each in coordinatesSet{
	thisId = get(each, "PolygonId");
	if(findinarray(polygonIdArr, thisId) == -1){
		append(polygonIdArr, thisId);
		polygonRegion = get(each, "PolygonRegion");
		put(polygonRegionDict, thisId, polygonRegion);
	}	
}

for eachId in polygonIdArr{
	latArr = float[];
	longArr = float[];
	extraLatArr = float[];
	extraLongArr = float[];
	coordinatesSet1 = bmql("SELECT Latitude, Longitude FROM CoordinatesWithHoles WHERE PolygonId = $eachId");
	//siteLatitude = 34.1652; //This is latitude,  his y is our latitude, his x is our longitude
	//siteLongitude = -84.7999; //This is longitude
	
	//gowdon 33.5378745, -85.2533038 = 4 (green)
	//barnesville 33.053090, -84.156217 = 3
	//trenton : 34.875609, -85.508644 = 5 
//Gainesville 34.30444, -83.83389 = 2 (Red), crawfordville
//winterville ; 33.9667, -83.2817
//locust grove = 33.3456, -84.1050 = 3 (yellow)
//greenville safeway : 1224 Terrell St, Greenville, GA 30222 (green)`

//Landfills

//Oak Grove LF 33.96258, -83.773876 = 2 (Red)
//Pine Ridge Regional LF = 3 (yellow)
//Newnan Transfer Station = 4 (green)
//richland creek, 34.128756, -84.045658; = 2 //This is latitude,  his y is our latitude, his x is our longitude

	ctr = 0;
	for eachRec in coordinatesSet1{
		rowLat = getfloat(eachRec, "Latitude");
		rowLong = getfloat(eachRec, "Longitude");
		if(ctr < 1000){
			append(latArr, rowLat);
			append(longArr, rowLong);
		}else{
			append(extraLatArr, rowLat);
			append(extraLongArr, rowLong);
		}	
		ctr = ctr + 1;
	}
	
	minLat = min(latArr);
	maxLat = max(latArr);
	
	if(NOT(isempty(extraLatArr))){
		minExtraLat = min(extraLatArr);
		maxExtraLat = max(extraLatArr);
		if(minLat > minExtraLat){ //if first array min is greater than 2nd array min, then the most minimum is 2nd array minimum
			minLat = minExtraLat;
		}
		
		if(maxLat < maxExtraLat){ //if first array max is less than 2nd array max, then the most maximum is 2nd array max
			maxLat = maxExtraLat;
		}
		print "--minExtraLat--"; print minExtraLat;
		print "--maxExtraLat--"; print maxExtraLat;
	}	
	
	

	minLong = min(longArr);
	maxLong = min(longArr);
	
	if(NOT(isempty(extraLongArr))){
		minExtraLong = min(extraLongArr);
		maxExtraLong = max(extraLongArr);
		if(minLong > minExtraLong){ //if first array min is greater than 2nd array min, then the most minimum is 2nd array minimum
			minLong = minExtraLong;
		}
		
		if(maxLong < maxExtraLong){ //if first array max is less than 2nd array max, then the most maximum is 2nd array max
			maxLong = maxExtraLong;
		}
		print "--minExtraLong--"; print minExtraLong;
		print "--maxExtraLong--"; print maxExtraLong;
	}	
	
	
	
	
	print "siteLongitude " + string(siteLongitude);
	print "siteLatitude " + string(siteLatitude);
	print "minLat " + string(minLat);
	print "maxLat " + string(maxLat);
	print "minLong " + string(minLong);
	print "maxLong " + string(maxLong);
	
	
	if(siteLongitude < minLong OR siteLongitude > maxLong OR siteLatitude < minLat OR siteLatitude > maxLat){
		print "here?";
		continue;
	}

	sizeCtr = 0;
	for eachRec in coordinatesSet1{
		polygonArr[sizeCtr][0] = getfloat(eachRec, "Latitude");
		polygonArr[sizeCtr][1] = getfloat(eachRec, "Longitude");
		sizeCtr = sizeCtr + 1;
	}

	latIndx = 0;
	longIndx = 1;
	intArr = range(sizeCtr);
	j = sizeCtr - 1;
	for i in intArr{
		if((polygonArr[i][latIndx] > siteLatitude) <> (polygonArr[j][latIndx] > siteLatitude) AND siteLongitude < (polygonArr[j][longIndx] - polygonArr[i][longIndx]) * (siteLatitude - polygonArr[i][latIndx]) / (polygonArr[j][latIndx] - polygonArr[i][latIndx]) + polygonArr[i][longIndx]){
			isInside = true;
			print "---here--"; print eachId;
			print "polygonRegionDict"; print polygonRegionDict;
			if(containskey(polygonRegionDict, eachId)){
				resultPolygon = get(polygonRegionDict, eachId);
			}
			break;
		}
		j = i;
	}
	//his y is our latitude, his x is our longitude siteLatitude = 34.2462; //This is latitude, 
	
}
return resultPolygon;