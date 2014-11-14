isInside = false;

polygonIdArr = string[];
polygonArr = float[][];
coordinatesSet = bmql("SELECT PolygonId FROM Coordinates");
resultPolygon = "";

for each in coordinatesSet{
	thisId = get(each, "PolygonId");
	if(findinarray(polygonIdArr, thisId) == -1){
		append(polygonIdArr, thisId);
	}	
}

for eachId in polygonIdArr{
	latArr = float[];
	longArr = float[];
	coordinatesSet1 = bmql("SELECT Latitude, Longitude FROM Coordinates WHERE PolygonId = $eachId");
	py = 34.1652; //This is latitude,  his y is our latitude, his x is our longitude
	px = -84.7999; //This is longitude
	
	//gowdon 33.5378745, -85.2533038 = 4 (green)
	//barnesville 33.053090, -84.156217 = 3
	//trenton : 34.875609, -85.508644 = 5 
//Gainesville 34.30444, -83.83389 = 2 (Red), crawfordville
//winterville ; 33.9667, -83.2817
//locust grove = 33.3456, -84.1050 = 3 (yellow)

//Landfills

//Oak Grove LF 33.96258, -83.773876 = 2 (Red)
//Pine Ridge Regional LF = 3 (yellow)
//Newnan Transfer Station = 4 (green)
//richland creek, 34.128756, -84.045658; = 2 //This is latitude,  his y is our latitude, his x is our longitude


	for eachRec in coordinatesSet1{
		rowLat = getfloat(eachRec, "Longitude");
		append(latArr, rowLat);
		rowLong = getfloat(eachRec, "Latitude");
		append(longArr, rowLong);
	}
	
	minLat = min(latArr);
	maxLat = max(latArr);

	minLong = min(longArr);
	maxLong = max(longArr);
	
	print "px " + string(px);
	print "py " + string(py);
	print "minLat " + string(minLat);
	print "maxLat " + string(maxLat);
	print "minLong " + string(minLong);
	print "maxLong " + string(maxLong);
	
	
	if(px < minLong OR px > maxLong OR py < minLat OR py > maxLat){
		print "here?";
		continue;
	}

	sizeCtr = 0;
	for eachRec in coordinatesSet1{
		polygonArr[sizeCtr][0] = getfloat(eachRec, "Longitude");
		polygonArr[sizeCtr][1] = getfloat(eachRec, "Latitude");
		sizeCtr = sizeCtr + 1;
	}

	latIndx = 0;
	longIndx = 1;
	intArr = range(sizeCtr);
	j = sizeCtr - 1;
	for i in intArr{
		if((polygonArr[i][latIndx] > py) <> (polygonArr[j][latIndx] > py) AND px < (polygonArr[j][longIndx] - polygonArr[i][longIndx]) * (py - polygonArr[i][latIndx]) / (polygonArr[j][latIndx] - polygonArr[i][latIndx]) + polygonArr[i][longIndx]){
			isInside = true;
			resultPolygon = eachId;
		}
		j = i;
	}
	//his y is our latitude, his x is our longitude py = 34.2462; //This is latitude, 
	
}
return resultPolygon;