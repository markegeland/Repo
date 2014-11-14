// Both Fixed and default fees can't be selected at a go
res=false;
feeArray = split(feesToCharge_quote, "~");
fixedFRF = false;
frf = false;
fixedERF = false;
erf = false;
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
		}
	}
}
if(fixedFRF AND frf){
	res=true;
}
if(fixedERF AND erf){
	res=true;
}
/* No Fixed fee for Admin
if(find(feesToCharge_quote,"Fixed Administrative Fee") <> -1 AND find(feesToCharge_quote,"Admin Fee") <> -1){
	res=true;
}*/

return res;