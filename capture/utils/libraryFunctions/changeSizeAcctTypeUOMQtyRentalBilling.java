/*Large Existing - Change Size Acct Type UOM Qty Rental Billing*/
returnDict = dict("string");

//Begin Guardrails - variables
haul_base = 0.0;
haul_target = 0.0;
haul_stretch = 0.0;
dsp_base = 0.0;
dsp_target = 0.0;
dsp_stretch = 0.0;
ovr_base = 0.0;
ovr_target = 0.0;
ovr_stretch = 0.0;
ren_base = 0.0;
ren_target = 0.0;
ren_stretch = 0.0;
nb_haul_cost = 0.0;
nb_dsp_cost = 0.0;
nb_ovr_cost = 0.0;
nb_ren_cost = 0.0;
haul_rate = 0.0;
dsp_rate = 0.0;
ovr_rate = 0.0;
ren_rate = 0.0;
nb_haul_cost_str = get(stringDict, "nb_haul_cost_LE");
nb_dsp_cost_str = get(stringDict, "nb_dsp_cost_LE");
nb_ovr_cost_str = get(stringDict, "nb_ovr_cost_LE");
nb_ren_cost_str = get(stringDict, "nb_ren_cost_LE");
haul_rate_str = get(stringDict, "haul_rate_LE");
dsp_rate_str = get(stringDict, "dsp_rate_LE");
ovr_rate_str = get(stringDict, "ovr_rate_LE");
ren_rate_str = get(stringDict, "ren_rate_LE");
//Schedule, Size and accountType Change - variables
salesActivity = get(stringDict, "salesActivity_LE");
//DSP UOM Change - variables
market_dsp_rate = 0.0;
floorAvgSpread = 0.0;
avgTargetSpread = 0.0;
dsp_change_flag = 0;
dsp_change_loc_flag = 0;
market_dsp_rate_str = get(stringDict, "market_dsp_rate_LE");
floorAvgSpread_str = get(stringDict, "floorAvgSpread_LE");
avgTargetSpread_str = get(stringDict, "avgTargetSpread_LE");
dsp_change_flag_str = get(stringDict, "dsp_change_flag_LE");
dsp_change_loc_flag_str = get(stringDict, "dsp_change_loc_flag_LE");
unitOfMeasure = get(stringDict, "unitOfMeasure_lc");
//Quantity Change - variables
old_quantity = 0;
new_quantity = 0;
old_quantity_str = get(stringDict, "quantity_lc_readonly");
new_quantity_str = get(stringDict, "quantity_lc");
//Rental Flag Change - variables
container_roi = 0.0;
container_depr = 0.0;
alloc_rental = 0;
hauls_per_month = 0.0;
market_rental_rate = 0.0;
has_compactor = 0;
rental_factor = 0.0;
old_rental_factor = 0.0;
rental_change_flag = 0;
ren_fac_change_flag = 0;
container_roi_str = get(stringDict, "container_roi_LE");
container_depr_str = get(stringDict, "container_depr_LE");
alloc_rental_str = get(stringDict, "alloc_rental_LE");
totalEstimatedHaulsMonth_str = get(stringDict, "LE_totalEstimatedHaulsMonth");
market_rental_rate_str = get(stringDict, "market_rental_rate_LE");
has_compactor_str = get(stringDict, "has_compactor_LE");
rental_factor_str = get(stringDict, "rental_factor_LE");
old_rental_factor_str = get(stringDict, "old_rental_factor_LE");
rental_change_flag_str = get(stringDict, "rental_change_flag_LE");
ren_fac_change_flag_str = get(stringDict, "ren_fac_change_flag_LE");
//Change Billing Type
svc_base_marg_prem = 0.0;
svc_targ_marg_prem = 0.0;
svc_str_marg_prem = 0.0;
units_per_haul = 0.0;
minimum_tons_flag = 0;
min_units_per_haul = 0.0;
new_minimum_tons = 0.0;
flat_rate_incl_tons = 0.0;
minimum_tons_to_bill = 0.0;
new_included_tons = 0.0;
old_included_tons = 0.0;
included_tons = 0.0;
svc_base_marg_prem_str = get(stringDict, "svc_base_marg_prem_LE");
svc_targ_marg_prem_str = get(stringDict, "svc_targ_marg_prem_LE");
svc_str_marg_prem_str = get(stringDict, "svc_str_marg_prem_LE");
unitsPerHaul_str = get(stringDict, "LE_unitsPerHaul");
old_billingType = get(stringDict, "billingType_lc_readOnly");
new_billingType = get(stringDict, "billingType_lc");
billingType = get(stringDict, "LE_billingType");
min_units_per_haul_str = get(stringDict, "min_units_per_haul_LE");
minimumTonsPerHaul_str = get(stringDict, "minimumTonsPerHaul_lc");
Flat_Rate_Incl_Tons_str = get(stringDict, "Flat_Rate_Incl_Tons_LE");
old_included_tons_str = get(stringDict, "tonsIncludedInHaulRate_lc_readOnly");
new_included_tons_str = get(stringDict, "tonsIncludedInHaulRate_lc");
haul_cost_components_str = get(stringDict, "haul_cost_components_LE");

//variables - Conversion
	//Begin Guardrails
if(isnumber(nb_haul_cost_str)){
	nb_haul_cost = atof(nb_haul_cost_str);
}
if(isnumber(nb_dsp_cost_str)){
	nb_dsp_cost = atof(nb_dsp_cost_str);
}
if(isnumber(nb_ovr_cost_str)){
	nb_ovr_cost = atof(nb_ovr_cost_str);
}
if(isnumber(nb_ren_cost_str)){
	nb_ren_cost = atof(nb_ren_cost_str);
}
if(isnumber(haul_rate_str)){
	haul_rate = atof(haul_rate_str);
}
if(isnumber(dsp_rate_str)){
	dsp_rate = atof(dsp_rate_str);
}
if(isnumber(ovr_rate_str)){
	ovr_rate = atof(ovr_rate_str);
}
if(isnumber(ren_rate_str)){
	ren_rate = atof(ren_rate_str);
}
	//DSP UOM Change
if(isnumber(market_dsp_rate_str)){
	market_dsp_rate = atof(market_dsp_rate_str);
}
if(isnumber(floorAvgSpread_str)){
	floorAvgSpread = atof(floorAvgSpread_str);
}
if(isnumber(avgTargetSpread_str)){
	avgTargetSpread = atof(avgTargetSpread_str);
}
if(isnumber(dsp_change_flag_str)){
	dsp_change_flag = atoi(dsp_change_flag_str);
}
if(isnumber(dsp_change_loc_flag_str)){
	dsp_change_loc_flag = atoi(dsp_change_loc_flag_str);
}
	//Quantity Change
if(isnumber(old_quantity_str)){
	old_quantity = atoi(old_quantity_str);
}
if(isnumber(new_quantity_str)){
	new_quantity = atoi(new_quantity_str);
}
	//Rental Flag Change
if(isnumber(container_roi_str)){
	container_roi = atof(container_roi_str);
}
if(isnumber(container_depr_str)){
	container_depr = atof(container_depr_str);
}
if(isnumber(alloc_rental_str)){
	alloc_rental = atoi(alloc_rental_str);
}
if(isnumber(totalEstimatedHaulsMonth_str)){
	hauls_per_month = atof(totalEstimatedHaulsMonth_str);
}
if(isnumber(market_rental_rate_str)){
	market_rental_rate = atof(market_rental_rate_str);
}
if(isnumber(has_compactor_str)){
	has_compactor = atoi(has_compactor_str);
}
if(isnumber(rental_factor_str)){
	rental_factor = atof(rental_factor_str);
}
if(isnumber(old_rental_factor_str)){
	old_rental_factor = atof(old_rental_factor_str);
}
if(isnumber(rental_change_flag_str)){
	rental_change_flag = atoi(rental_change_flag_str);
}
if(isnumber(ren_fac_change_flag_str)){
	ren_fac_change_flag = atoi(ren_fac_change_flag_str);
}
	//Change Billing Type
if(isnumber(svc_base_marg_prem_str)){
	svc_base_marg_prem = atof(svc_base_marg_prem_str);
}
if(isnumber(svc_targ_marg_prem_str)){
	svc_targ_marg_prem = atof(svc_targ_marg_prem_str);
}
if(isnumber(svc_str_marg_prem_str)){
	svc_str_marg_prem = atof(svc_str_marg_prem_str);
}
if(isnumber(unitsPerHaul_str)){
	units_per_haul = atof(unitsPerHaul_str);
}
if(isnumber(min_units_per_haul_str)){
	min_units_per_haul = atof(min_units_per_haul_str);
}
if(isnumber(minimumTonsPerHaul_str)){
	new_minimum_tons = atof(minimumTonsPerHaul_str);
}
if(isnumber(Flat_Rate_Incl_Tons_str)){
	flat_rate_incl_tons = atof(Flat_Rate_Incl_Tons_str);
}
if(isnumber(new_included_tons_str)){
	new_included_tons = atof(new_included_tons_str);
}
if(isnumber(old_included_tons_str)){
	old_included_tons = atof(old_included_tons_str);
}
if(isnumber(haul_cost_components_str)){
	haul_cost_components = atof(haul_cost_components_str);
}

//Begin Guardrails - code
tempArray = float[];
append(tempArray, nb_haul_cost);
append(tempArray, haul_rate);
haul_base = max(tempArray);
haul_target = max(tempArray);
haul_stretch = max(tempArray);

tempArray = float[];
append(tempArray, nb_dsp_cost);
append(tempArray, dsp_rate);
dsp_base = max(tempArray);
dsp_target = max(tempArray);
dsp_stretch = max(tempArray);

tempArray = float[];
append(tempArray, nb_ovr_cost);
append(tempArray, ovr_rate);
ovr_base = max(tempArray);
ovr_target = max(tempArray);
ovr_stretch = max(tempArray);

tempArray = float[];
append(tempArray, nb_ren_cost);
append(tempArray, ren_rate);
ren_base = max(tempArray);
ren_target = max(tempArray);
ren_stretch = max(tempArray);

//Schedule, Size and accountType Change - code
if(not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")){
	haul_target=haul_base;
    dsp_target=dsp_base;
    ovr_target=ovr_base;
    ren_target=ren_base;
    haul_stretch=haul_base;
    dsp_stretch=dsp_base;
    ovr_stretch=ovr_base;
    ren_stretch=ren_base;
}

//DSP UOM Change - code
if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND dsp_change_flag == 1 AND dsp_change_loc_flag == 0 AND unitOfMeasure <> "No Change"){
	dsp_base=market_dsp_rate;
    ovr_base=market_dsp_rate;
    dsp_target=market_dsp_rate * floorAvgSpread;
    ovr_target=market_dsp_rate * floorAvgSpread;
    dsp_stretch=market_dsp_rate * avgTargetSpread;
    ovr_stretch=market_dsp_rate * avgTargetSpread;
}

//Quantity Change - code
if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND new_quantity <> old_quantity){
	ren_base=new_quantity * ren_base / old_quantity;
    ren_target=new_quantity * ren_target / old_quantity;
    ren_stretch=new_quantity * ren_stretch / old_quantity;
}

//Rental Flag Change - code
if(rental_change_flag == 1 AND has_compactor == 0 AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	tempArray = float[];
	append(tempArray, ren_base / hauls_per_month);
	append(tempArray, container_roi + container_depr);
	haul_base=haul_base - (container_roi + container_depr) * alloc_rental + (1 - alloc_rental) * rental_factor * max(tempArray);
	haul_stretch=haul_stretch + (1 - alloc_rental) * ren_base * rental_factor / hauls_per_month;
	tempArray = float[];
	append(tempArray, market_rental_rate * new_quantity);
	append(tempArray, (container_roi + container_depr) * hauls_per_month);
	ren_base=max(tempArray) * alloc_rental / rental_factor;
	ren_stretch=max(tempArray) * alloc_rental / rental_factor;
}
if(ren_fac_change_flag == 1 AND has_compactor == 0 AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	ren_base=ren_base*old_rental_factor/rental_factor;
    ren_stretch=ren_stretch*old_rental_factor/rental_factor;
}
if((rental_change_flag == 1 OR ren_fac_change_flag == 1) AND has_compactor == 0 AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	haul_target=(haul_base + haul_stretch)/2;
    ren_target=(ren_base + ren_stretch)/2;
}

//Change Billing Type - code
if(billingType == "Haul + Minimum Tonnage"){
	minimum_tons_flag = 1;
	if(new_minimum_tons == 0.0){
		if(min_units_per_haul == 0.0){
			minimum_tons_to_bill = flat_rate_incl_tons;
		}else{
			minimum_tons_to_bill = min_units_per_haul;
		}
	}else{
		minimum_tons_to_bill = new_minimum_tons;
	}		
}
if(billingType == "Flat Rate + Overage"){
	if(new_included_tons == 0.0){
		if(old_included_tons == 0.0){
			included_tons = flat_rate_incl_tons;
		}else{
			included_tons = old_included_tons;
		}
	}else{
		included_tons = new_included_tons;
	}		
}
	// Flat Rate --> Haul + Disposal or Haul + Minimum
	  // Haul: subtract market disposal; add premium improvement; safety: haul cost components
	  // Disposal: market rate with spread
if(old_billingType == "Flat Rate + Overage" AND (new_billingType == "Haul + Disposal" OR new_billingType == "Haul + Minimum Tonnage") AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	tempArray = float[];
	append(tempArray, haul_base - market_dsp_rate * units_per_haul);
	append(tempArray, haul_cost_components);
	haul_base=max(tempArray) / (1 - svc_base_marg_prem);
	tempArray = float[];
	append(tempArray, haul_target - market_dsp_rate * units_per_haul);
	append(tempArray, haul_cost_components);
	haul_target=max(tempArray) / (1 - svc_targ_marg_prem);
	tempArray = float[];
	append(tempArray, haul_stretch - market_dsp_rate * units_per_haul);
	append(tempArray, haul_cost_components);
	haul_stretch=max(tempArray) / (1 - svc_str_marg_prem);
	dsp_base=market_dsp_rate;
	dsp_target=market_dsp_rate * floorAvgSpread;
	dsp_stretch=market_dsp_rate * avgTargetSpread;
	if(new_billingType == "Haul + Minimum Tonnage"){
		ovr_base = market_dsp_rate;
		ovr_target = market_dsp_rate * floorAvgSpread;
		ovr_stretch = market_dsp_rate * avgTargetSpread;
	}else{
		ovr_base = 0.0;
		ovr_target = 0.0;
		ovr_stretch = 0.0;
	}
}
	// Haul + Disposal --> Flat Rate
	  // Haul: guardrail rate + dsp_rate * included tons
	  // Disposal: zero
	  // Overage at max of cost or current rate
if(old_billingType == "Haul + Disposal" AND new_billingType == "Flat Rate + Overage" AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	haul_base=haul_base + market_dsp_rate * included_tons;
	haul_target=haul_target + market_dsp_rate * floorAvgSpread * included_tons;
	haul_stretch=haul_stretch + market_dsp_rate * avgTargetSpread * included_tons;
	dsp_base=0.0;
    dsp_target=0.0;
    dsp_stretch=0.0;
    ovr_base=market_dsp_rate;
    ovr_target=market_dsp_rate * floorAvgSpread;
    ovr_stretch=market_dsp_rate * avgTargetSpread;
}
	// Haul + Disposal --> Haul + Minimum
	  // Haul: same; safety: haul cost components (might not be needed)
	  // Disposal: market rate with spread (should be sending DIS with proposed * minimum tons)
	  // TODO shouldn't need to do anything as rates won't change
	  // but contingent on current overage rate being read in as DSP (instead of zero)
if(old_billingType == "Haul + Disposal" AND new_billingType == "Haul + Minimum Tonnage" AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	tempArray = float[];
	append(tempArray, haul_base);
	append(tempArray, haul_cost_components);
	haul_base=max(tempArray);
	tempArray = float[];
	append(tempArray, haul_target);
	append(tempArray, haul_cost_components);
	haul_target=max(tempArray);
	tempArray = float[];
	append(tempArray, haul_stretch);
	append(tempArray, haul_cost_components);
	haul_stretch=max(tempArray);
	dsp_base=market_dsp_rate;
    dsp_target=market_dsp_rate * floorAvgSpread;
    dsp_stretch=market_dsp_rate * avgTargetSpread;
    ovr_base=market_dsp_rate;
    ovr_target=market_dsp_rate * floorAvgSpread;
    ovr_stretch=market_dsp_rate * avgTargetSpread;
}
	// Haul + Minimum --> Haul + Disposal
	  // Haul: same; safety: haul cost components
	  // Disposal: DISAQ Rate / Excess ton Amt; safety: market dsp rate
if(old_billingType == "Haul + Minimum Tonnage" AND new_billingType == "Haul + Disposal" AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	tempArray = float[];
	append(tempArray, haul_base);
	append(tempArray, haul_cost_components);
	haul_base=max(tempArray);
	tempArray = float[];
	append(tempArray, haul_target);
	append(tempArray, haul_cost_components);
	haul_target=max(tempArray);
	tempArray = float[];
	append(tempArray, haul_stretch);
	append(tempArray, haul_cost_components);
	haul_stretch=max(tempArray);
	dsp_base=market_dsp_rate;
    dsp_target=market_dsp_rate * floorAvgSpread;
    dsp_stretch=market_dsp_rate * avgTargetSpread;
    ovr_base=0.0;
    ovr_target=0.0;
    ovr_stretch=0.0;
}

	// Haul + Minimum --> Flat Rate
		// Haul: current rate + DISAQ rate; safety: haul components + market dsp rate with spread * included_tons
		// Disposal: zero
		// Overage: market dsp rate with spread
if(old_billingType == "Haul + Minimum Tonnage" AND new_billingType == "Flat Rate + Overage" AND (not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner"))){
	haul_base=haul_base + market_dsp_rate * min_units_per_haul;
	haul_target=haul_target + market_dsp_rate * floorAvgSpread * min_units_per_haul;
	haul_stretch=haul_stretch + market_dsp_rate * avgTargetSpread * min_units_per_haul;
	dsp_base=0.0;
	dsp_target=0.0;
	dsp_stretch=0.0;
	ovr_base = market_dsp_rate;
	ovr_target = market_dsp_rate * floorAvgSpread;
	ovr_stretch = market_dsp_rate * avgTargetSpread;
}

put(returnDict, "haul_base", string(haul_base));
put(returnDict, "haul_target", string(haul_target));
put(returnDict, "haul_stretch", string(haul_stretch));

put(returnDict, "dsp_base", string(dsp_base));
put(returnDict, "dsp_target", string(dsp_target));
put(returnDict, "dsp_stretch", string(dsp_stretch));

put(returnDict, "ovr_base", string(ovr_base));
put(returnDict, "ovr_target", string(ovr_target));
put(returnDict, "ovr_stretch", string(ovr_stretch));

put(returnDict, "ren_base", string(ren_base));
put(returnDict, "ren_target", string(ren_target));
put(returnDict, "ren_stretch", string(ren_stretch));

return returnDict;