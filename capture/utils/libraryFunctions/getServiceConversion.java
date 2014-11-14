/* 	UTIL : getServiceConversion
	INPUT PARAMS	: 	conversionType (string) [Contains either 'liftsPerCont_to_freq'(to convert lifts to Freq) OR freq_to_liftsPerCont'(to convert freq to lifts)]
					:	convertValue (string)
					
	RETURN 			: 	string 

*/
returnValue = "";

liftsToFreqdict = dict("string");
freqToLiftsdict = dict("string");


// Convert Lifts Per Container to Frequency
if(conversionType == "liftsPerCont_to_freq"){ 
	//Lifts To Frequency Dict
	
	/*put(liftsToFreqdict, "EOW", "0.5");
	put(liftsToFreqdict, "Every 4 Weeks", "0.25");
	put(liftsToFreqdict, "1/Week", "1");
	put(liftsToFreqdict, "2/Week", "2");
	put(liftsToFreqdict, "3/Week", "3");
	put(liftsToFreqdict, "4/Week", "4");
	put(liftsToFreqdict, "5/Week", "5");
	put(liftsToFreqdict, "6/Week", "6");
	put(liftsToFreqdict, "7/Week", "7");
	
	if(containskey(liftsToFreqdict, convertValue)){
		returnValue = get(liftsToFreqdict, convertValue);
	}*/
	resultset = bmql("SELECT conversionFactor, Frequency FROM Frequency_Conversion");
	for eachRec in resultset{	
		frequency = get(eachRec, "Frequency");
		if(lower(frequency) == lower(convertValue)){
			returnValue = get(eachRec, "conversionFactor");
		}
	}
}


// Convert Frequency to Lifts Per Container
if(conversionType == "freq_to_liftsPerCont"){

	/*
	//Frequency To Lifts Dict
	put(freqToLiftsDict, "0.5", "EOW");
	put(freqToLiftsDict, "0.25", "Every 4 Weeks");
	put(freqToLiftsDict, "1", "1/Week");
	put(freqToLiftsDict, "2", "2/Week");
	put(freqToLiftsDict, "3", "3/Week");
	put(freqToLiftsDict, "4", "4/Week");
	put(freqToLiftsDict, "5", "5/Week");
	put(freqToLiftsDict, "6", "6/Week");
	put(freqToLiftsDict, "7", "7/Week");
	
	if(containskey(freqToLiftsdict, convertValue)){
		returnValue = get(freqToLiftsdict, convertValue);
	}
	*/
	temp = atof(convertValue);
	resultset = bmql("SELECT Frequency FROM Frequency_Conversion WHERE conversionFactor = $temp");
	for eachRec in resultset{
		returnValue = get(eachRec, "Frequency");
	}
}

return returnValue;