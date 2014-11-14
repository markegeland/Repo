DELIM = "^_^";
polygonRegionDict = dict("string");
geoCodeArr = split(geoCodes,DELIM);
siteLatitude = 0.0;
siteLongitude = 0.0;
//Split the input string to get the disposal site's Latitude and Longitude
if(sizeofarray(geoCodeArr) > 0){
	siteLatitude =  atof(geoCodeArr[0]);
}
if(sizeofarray(geoCodeArr) > 1){
	siteLongitude =  atof(geoCodeArr[1]);
}

isInside = false;

polygonIdArr = string[];
polygonArr = float[][];
//Get all polygon IDs from the table
coordinatesSet = bmql("SELECT PolygonId FROM Coordinates_Shape");
resultPolygon = "";

//Create a dictionary to put all the polygon IDs
for each in coordinatesSet{
	thisId = get(each, "PolygonId");
	if(findinarray(polygonIdArr, thisId) == -1){
		append(polygonIdArr, thisId);
		polygonRegion = get(each, "PolygonId");
		put(polygonRegionDict, thisId, polygonRegion);
	}	
}

for eachId in polygonIdArr{
	latArr = float[];
	longArr = float[];
	coordinatesSet1 = bmql("SELECT Latitude, Longitude FROM Coordinates_Shape WHERE PolygonId = $eachId");
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

	// For every polygon id, create arrays to put all the latitudes and longitudes 
	for eachRec in coordinatesSet1{
		rowLat = getfloat(eachRec, "Latitude");
		append(latArr, rowLat);
		rowLong = getfloat(eachRec, "Longitude");
		append(longArr, rowLong);
	}
	
	//Get the min and max for both Lat and Long
	minLat = min(latArr);
	maxLat = max(latArr);

	minLong = min(longArr);
	maxLong = max(longArr);
	/*
	print "siteLongitude " + string(siteLongitude);
	print "siteLatitude " + string(siteLatitude);
	print "minLat " + string(minLat);
	print "maxLat " + string(maxLat);
	print "minLong " + string(minLong);
	print "maxLong " + string(maxLong);
	print coordinatesSet1;
	*/
	
	//If the site lat/ long does not fall in the range, ignore this record, continue to the next polygon id.
	if(siteLongitude < minLong OR siteLongitude > maxLong OR siteLatitude < minLat OR siteLatitude > maxLat){
		//print "here?";
		continue;
	}

	// Create a 2- dimensional array to populate the lats and longs. Every record basically contains lat and long of the point that helps form the polygon
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
		/*Each iteration of the loop, the test point is checked against one of the polygon's edges. The first line of the if-test succeeds if the point's y-coord is within the edge's scope. The second line checks whether the test point is to the left of the line. If that is true the line drawn rightwards from the test point crosses that edge. 
		By repeatedly inverting the value of j, the algorithm counts how many times the rightward line crosses the polygon. If it crosses an odd number of times, then the point is inside; if an even number, the point is outside.*/
		if((polygonArr[i][latIndx] > siteLatitude) <> (polygonArr[j][latIndx] > siteLatitude) AND siteLongitude < (polygonArr[j][longIndx] - polygonArr[i][longIndx]) * (siteLatitude - polygonArr[i][latIndx]) / (polygonArr[j][latIndx] - polygonArr[i][latIndx]) + polygonArr[i][longIndx]){
		isInside = true;
			//print "---here--"; print eachId;
			//print "polygonRegionDict"; print polygonRegionDict;
			if(containskey(polygonRegionDict, eachId)){
				resultPolygon = get(polygonRegionDict, eachId);
			}
			break;
		}
		j = i;
	}
	//his y is our latitude, his x is our longitude siteLatitude = 34.2462; //This is latitude, 
	
}
//Get Disposal cost based on the polygon 
dispCost = "";
disposalCosts = bmql("SELECT Disp_Site_Cost FROM DisposalCosts_Comm WHERE PolygonId = $resultPolygon");
for each in disposalCosts{
	dispCost = get(each,"Disp_Site_Cost");
}

return dispCost;