/*
Created by: Srikar Mamillapalli
Purpose: Parses key value paired delimited string and returns the query value
Delimited String Format: key@value^_^
Example: geo@SEANOR^_^
updates : 01/21/15 - Gaurav (Republic) - added 20150119 - GD - #322 - making delivery and removal "Per Service compared to "One time"
*/
str = "";
key = "";
valDelim = "**";
fieldDelim = "^_^";
result = "";
if(containskey(inputDict,"inputStr")){
	str = get(inputDict,"inputStr");
}
if(containskey(inputDict,"key")){
	key = get(inputDict,"key");
}
if(containskey(inputDict,"COMM_VALUE_DELIM")){
	valDelim = get(inputDict,"COMM_VALUE_DELIM");
}
if(containskey(inputDict,"FIELD_DELIM")){
	fieldDelim = get(inputDict,"FIELD_DELIM");
}

if(str <> ""){
	//Split on each record
	recArray = split(str,fieldDelim);
	//Iterate through the records to find the key
	for eachRow in recArray{
		//Matching the key
		if(find(eachRow,key+valDelim) > -1){
			//split string to separate key & value
			valArray = split(eachRow,valDelim);
			//Compare key with the zero'th index and return value if matched
			if(valArray[0] == key){
				result = valArray[1];
			}
		}
	}
}
if(result == "One-Time"){//added 20150119 - GD - #322 - making delivery and removal "Per Service compared to "One time"
	result = "Per Service";
}
return result;