if(chooseCSA_quote){
	region = "";
	regionRecordSet = bmql("SELECT Region FROM CSACombo WHERE Division = $division_quote");
	for eachRec in regionRecordSet{
		thisRegion = get(eachRec, "Region");
		if(thisRegion <> ""){
			region = region + "~" + get(eachRec, "Region");
		}	
	}
	If(region <> ""){
		return true;
	}
}	
return false;