//if the estTonsHaul_l is less than 1 or not an integer, constrain the value
result = 0.0;

if (estTonsHaul_l == integer(estTonsHaul_l)){
	if(estTonsHaul_l < 1){
		result = estTonsHaul_l;
	}
}
else{
	result = estTonsHaul_l;
}



return result;