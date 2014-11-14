// Hide Fee Exception Reason Code drop down if all chargeable fee are selected
flag=false; 
feeArray = split(feesToCharge_quote, "~");
fixedFRF = false;
frf = false;
fixedERF = false;
erf = false;
adminFee = false;
for eachFee in feeArray{
	if(eachFee <> ""){
		if(eachFee == "Fixed Fuel Recovery Fee (FRF)"){ //Find will not work 
			fixedFRF = true;	
		}elif(eachFee == "FRF"){
			frf = true;
		}elif(eachFee == "Fixed Environment Recovery Fee (ERF)"){
			fixedERF = true;
		}elif(eachFee == "ERF"){
			erf = true;
		}elif(eachFee == "Admin Fee"){
			adminFee = true;
		}
	}
}
if((fixedFRF OR frf) AND (fixedERF OR erf) AND adminFee){
	flag=true;
}

return flag;