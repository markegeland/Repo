/*updated for Large Existing*/
if(chooseCSA_quote AND tempExists){
	region = "";
	accTypeLine = "Temporary";
	regionRecordSet = bmql("SELECT Region FROM CSACombo WHERE Division = $division_quote AND AccountType = $accTypeLine");
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