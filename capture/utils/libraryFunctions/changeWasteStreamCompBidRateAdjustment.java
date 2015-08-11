/*Large Existing - Change waste stream comp bid rate adjustment*/
returnDict = dict("string");

//Change Waste Stream - variables
fee_Pct = 0.0;
frf_flag = 0;
erf_flag = 0;
frfRate = 0.0;
erfRate = 0.0;
fee_flag_change = 0;
is_erf_on_frf_charged = 0;
haul_base_frf_premium = 0.0;
haul_stretch_frf_premium = 0.0;
haul_target_frf_premium = 0.0;
market_dsp_rate = 0.0;
floorAvgSpread = 0.0;
avgTargetSpread = 0.0;
dsp_change_flag = 0;
dsp_change_loc_flag = 0;
svc_base_marg_prem = 0.0;
svc_targ_marg_prem = 0.0;
svc_str_marg_prem = 0.0;
svc_gap_recovery_pct = 0.0;
alloc_disposal = 0;
nb_haul_floor = 0.0;
nb_haul_avg = 0.0;
nb_haul_target = 0.0;
nb_dsp_floor = 0.0;
nb_dsp_avg = 0.0;
nb_dsp_target = 0.0;
nb_ovr_floor = 0.0;
nb_ovr_avg = 0.0;
nb_ovr_target = 0.0;
nb_ren_floor = 0.0;
nb_ren_avg = 0.0;
nb_ren_target = 0.0;
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
haul_frf = 0.0;
haul_erf = 0.0;
dsp_frf = 0.0;
dsp_erf = 0.0;
ovr_frf = 0.0;
ovr_erf = 0.0;
ren_frf = 0.0;
ren_erf = 0.0;
feePct_str = get(stringDict, "feePct");
frfRate_str = get(stringDict, "frfRate");
erfRate_str = get(stringDict, "erfRate");
fee_flag_change_str = get(stringDict, "fee_flag_change_LE");
haulbaseFRFPremium_str = get(stringDict, "haulbaseFRFPremium");
haulStretchFRFPremium_str = get(stringDict, "haulStretchFRFPremium");
haulTargetFRFPremium_str = get(stringDict, "haulTargetFRFPremium");
includeFRF = get(stringDict, "includeFRF");
includeERF = get(stringDict, "includeERF");
is_erf_on_frf_charged_str = get(stringDict, "is_erf_on_frf_charged_LE");
market_dsp_rate_str = get(stringDict, "market_dsp_rate_LE");
floorAvgSpread_str = get(stringDict, "floorAvgSpread_LE");
avgTargetSpread_str = get(stringDict, "avgTargetSpread_LE");
dsp_change_flag_str = get(stringDict, "dsp_change_flag_LE");
dsp_change_loc_flag_str = get(stringDict, "dsp_change_loc_flag_LE");
svc_base_marg_prem_str = get(stringDict, "svc_base_marg_prem_LE");
svc_targ_marg_prem_str = get(stringDict, "svc_targ_marg_prem_LE");
svc_str_marg_prem_str = get(stringDict, "svc_str_marg_prem_LE");
svc_gap_recovery_pct_str = get(stringDict, "svc_gap_recovery_pct_LE");
alloc_disposal_str = get(stringDict, "alloc_disposal_LE");
salesActivity = get(stringDict, "salesActivity_LE");
nb_haul_floor_str = get(stringDict, "nb_haul_floor");
nb_haul_avg_str = get(stringDict, "nb_haul_avg");
nb_haul_target_str = get(stringDict, "nb_haul_target");
nb_dsp_floor_str = get(stringDict, "nb_dsp_floor");
nb_dsp_avg_str = get(stringDict, "nb_dsp_avg");
nb_dsp_target_str = get(stringDict, "nb_dsp_target");
nb_ovr_floor_str = get(stringDict, "nb_ovr_floor");
nb_ovr_avg_str = get(stringDict, "nb_ovr_avg");
nb_ovr_target_str = get(stringDict, "nb_ovr_target");
nb_ren_floor_str = get(stringDict, "nb_ren_floor");
nb_ren_avg_str = get(stringDict, "nb_ren_avg");
nb_ren_target_str = get(stringDict, "nb_ren_target");
haul_base_str = get(stringDict, "haul_base");
haul_target_str = get(stringDict, "haul_target");
haul_stretch_str = get(stringDict, "haul_stretch");
dsp_base_str = get(stringDict, "dsp_base");
dsp_target_str = get(stringDict, "dsp_target");
dsp_stretch_str = get(stringDict, "dsp_stretch");
ovr_base_str = get(stringDict, "ovr_base");
ovr_target_str = get(stringDict, "ovr_target");
ovr_stretch_str = get(stringDict, "ovr_stretch");
ren_base_str = get(stringDict, "ren_base");
ren_target_str = get(stringDict, "ren_target");
ren_stretch_str = get(stringDict, "ren_stretch");
billingType = get(stringDict, "LE_billingType");
//Change DSP Location - variables
nb_haul_cost = 0.0;
nb_dsp_cost = 0.0;
nb_ovr_cost = 0.0;
nb_ren_cost = 0.0;
curr_haul_margin_pct = 0.0;
nb_haul_cost_str = get(stringDict, "nb_haul_cost_LE");
nb_dsp_cost_str = get(stringDict, "nb_dsp_cost_LE");
nb_ovr_cost_str = get(stringDict, "nb_ovr_cost_LE");
nb_ren_cost_str = get(stringDict, "nb_ren_cost_LE");
curr_haul_margin_pct_str = get(stringDict, "curr_haul_margin_pct");
//Competitive Bid Rate Adjustment - variables
comp_targ_pct_retain = 0.0;
comp_str_pct_retain = 0.0;
comp_bid_markup = 0.0;
cb_haul_amt = 0.0;
cb_dsp_amt = 0.0;
cb_ren_amt = 0.0;
haul_rate = 0.0;
dsp_rate = 0.0;
ovr_rate = 0.0;
ren_rate = 0.0;
serviceChangeQuote = false;
competitiveBidQuote = false;
rollbackOfPIQuote = false;
generalSaveQuote = false;
serviceChangeQuoteStr = get(stringDict, "serviceChangeQuoteStr");
competitiveBidQuoteStr = get(stringDict, "competitiveBidQuoteStr");
rollbackOfPIQuoteStr = get(stringDict, "rollbackOfPIQuoteStr");
generalSaveQuoteStr = get(stringDict, "generalSaveQuoteStr");
comp_targ_pct_retain_str = get(stringDict, "comp_targ_pct_retain_LE");
comp_str_pct_retain_str = get(stringDict, "comp_str_pct_retain_LE");
comp_bid_markup_str = get(stringDict, "comp_bid_markup_LE");
competitiveBidAmountHaul_str = get(stringDict, "competitiveBidAmountHaul_lc");
competitiveBidAmountDisposal_str = get(stringDict, "competitiveBidAmountDisposal_lc");
competitiveBidAmountRental_str = get(stringDict, "competitiveBidAmountRental_lc");
haul_rate_str = get(stringDict, "haul_rate_LE");
dsp_rate_str = get(stringDict, "dsp_rate_LE");
ovr_rate_str = get(stringDict, "ovr_rate_LE");
ren_rate_str = get(stringDict, "ren_rate_LE");
//PI Rollback - variables
last_pi_haul_amt = 0.0;
last_pi_dsp_amt = 0.0;
last_pi_ovr_amt = 0.0;
last_pi_ren_amt = 0.0;
pi_retain_base = 0.0;
pi_retain_target = 0.0;
pi_retain_stretch = 0.0;
last_pi_haul_amt_str = get(stringDict, "last_pi_haul_amt_LE");
last_pi_dsp_amt_str = get(stringDict, "last_pi_dsp_amt_LE");
last_pi_ovr_amt_str = get(stringDict, "last_pi_ovr_amt_LE");
last_pi_ren_amt_str = get(stringDict, "last_pi_ren_amt_LE");
pi_retain_base_str = get(stringDict, "pi_retain_base_LE");
pi_retain_target_str = get(stringDict, "pi_retain_target_LE");
pi_retain_stretch_str = get(stringDict, "pi_retain_stretch_LE");
//Rate Rollback - variables
save_base_margin_adj = 0.0;
save_targ_pct_retain = 0.0;
save_str_pct_retain = 0.0;
curr_haul_margin_dol = 0.0;
curr_dsp_margin_dol = 0.0;
curr_ovr_margin_dol = 0.0;
curr_ren_margin_dol = 0.0;
save_base_margin_adj_str = get(stringDict, "save_base_margin_adj_LE");
save_targ_pct_retain_str = get(stringDict, "save_targ_pct_retain_LE");
save_str_pct_retain_str = get(stringDict, "save_str_pct_retain_LE");
curr_haul_margin_dol_str = get(stringDict, "curr_haul_margin_dol_LE");
curr_dsp_margin_dol_str = get(stringDict, "curr_dsp_margin_dol_LE");
curr_ovr_margin_dol_str = get(stringDict, "curr_ovr_margin_dol_LE");
curr_ren_margin_dol_str = get(stringDict, "curr_ren_margin_dol_LE");

//variables - Conversion
if(isnumber(feePct_str)){
	fee_Pct = atof(feePct_str);
}
if(isnumber(frfRate_str)){
	frfRate = atof(frfRate_str);
}
if(isnumber(erfRate_str)){
	erfRate = atof(erfRate_str);
}
if(isnumber(fee_flag_change_str)){
	fee_flag_change = atoi(fee_flag_change_str);
}
if(isnumber(haulbaseFRFPremium_str)){
	haul_base_frf_premium = atof(haulbaseFRFPremium_str);
}
if(isnumber(haulStretchFRFPremium_str)){
	haul_stretch_frf_premium = atof(haulStretchFRFPremium_str);
}
if(isnumber(haulTargetFRFPremium_str)){
	haul_target_frf_premium = atof(haulTargetFRFPremium_str);
}
if(includeFRF == "Yes"){
	frf_flag = 1;
}
if(includeERF == "Yes"){
	erf_flag = 1;
}
if(is_erf_on_frf_charged_str == "Yes"){
	is_erf_on_frf_charged = 1;
}
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
if(isnumber(svc_base_marg_prem_str)){
	svc_base_marg_prem = atof(svc_base_marg_prem_str);
}
if(isnumber(svc_targ_marg_prem_str)){
	svc_targ_marg_prem = atof(svc_targ_marg_prem_str);
}
if(isnumber(svc_str_marg_prem_str)){
	svc_str_marg_prem = atof(svc_str_marg_prem_str);
}
if(isnumber(svc_gap_recovery_pct_str)){
	svc_gap_recovery_pct = atof(svc_gap_recovery_pct_str);
}
if(isnumber(alloc_disposal_str)){
	alloc_disposal = atoi(alloc_disposal_str);
}
if(isnumber(nb_haul_floor_str)){
	nb_haul_floor = atof(nb_haul_floor_str);
}
if(isnumber(nb_haul_avg_str)){
	nb_haul_avg = atof(nb_haul_avg_str);
}
if(isnumber(nb_haul_target_str)){
	nb_haul_target = atof(nb_haul_target_str);
}
if(isnumber(nb_dsp_floor_str)){
	nb_dsp_floor = atof(nb_dsp_floor_str);
}
if(isnumber(nb_dsp_avg_str)){
	nb_dsp_avg = atof(nb_dsp_avg_str);
}
if(isnumber(nb_dsp_target_str)){
	nb_dsp_target = atof(nb_dsp_target_str);
}
if(isnumber(nb_ovr_floor_str)){
	nb_ovr_floor = atof(nb_ovr_floor_str);
}
if(isnumber(nb_ovr_avg_str)){
	nb_ovr_avg = atof(nb_ovr_avg_str);
}
if(isnumber(nb_ovr_target_str)){
	nb_ovr_target = atof(nb_ovr_target_str);
}
if(isnumber(nb_ren_floor_str)){
	nb_ren_floor = atof(nb_ren_floor_str);
}
if(isnumber(nb_ren_avg_str)){
	nb_ren_avg = atof(nb_ren_avg_str);
}
if(isnumber(nb_ren_target_str)){
	nb_ren_target = atof(nb_ren_target_str);
}
if(isnumber(haul_base_str)){
	haul_base = atof(haul_base_str);
}
if(isnumber(haul_target_str)){
	haul_target = atof(haul_target_str);
}
if(isnumber(haul_stretch_str)){
	haul_stretch = atof(haul_stretch_str);
}
if(isnumber(dsp_base_str)){
	dsp_base = atof(dsp_base_str);
}
if(isnumber(dsp_target_str)){
	dsp_target = atof(dsp_target_str);
}
if(isnumber(dsp_stretch_str)){
	dsp_stretch = atof(dsp_stretch_str);
}
if(isnumber(ovr_base_str)){
	ovr_base = atof(ovr_base_str);
}
if(isnumber(ovr_target_str)){
	ovr_target = atof(ovr_target_str);
}
if(isnumber(ovr_stretch_str)){
	ovr_stretch = atof(ovr_stretch_str);
}
if(isnumber(ren_base_str)){
	ren_base = atof(ren_base_str);
}
if(isnumber(ren_target_str)){
	ren_target = atof(ren_target_str);
}
if(isnumber(ren_stretch_str)){
	ren_stretch = atof(ren_stretch_str);
}
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
if(isnumber(curr_haul_margin_pct_str)){
	curr_haul_margin_pct = atof(curr_haul_margin_pct_str);
}
if(isnumber(comp_targ_pct_retain_str)){
	comp_targ_pct_retain = atof(comp_targ_pct_retain_str);
}
if(isnumber(comp_str_pct_retain_str)){
	comp_str_pct_retain = atof(comp_str_pct_retain_str);
}
if(isnumber(comp_bid_markup_str)){
	comp_bid_markup = atof(comp_bid_markup_str);
}
if(isnumber(competitiveBidAmountHaul_str)){
	cb_haul_amt = atof(competitiveBidAmountHaul_str);
}
if(isnumber(competitiveBidAmountDisposal_str)){
	cb_dsp_amt = atof(competitiveBidAmountDisposal_str);
}
if(isnumber(competitiveBidAmountRental_str)){
	cb_ren_amt = atof(competitiveBidAmountRental_str);
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
if(serviceChangeQuoteStr == "true"){
	serviceChangeQuote = true;
}
if(competitiveBidQuoteStr == "true"){
	competitiveBidQuote = true;
}
if(rollbackOfPIQuoteStr == "true"){
	rollbackOfPIQuote = true;
}
if(generalSaveQuoteStr == "true"){
	generalSaveQuote = true;
}
if(isnumber(last_pi_haul_amt_str)){
	last_pi_haul_amt = atof(last_pi_haul_amt_str);
}
if(isnumber(last_pi_dsp_amt_str)){
	last_pi_dsp_amt = atof(last_pi_dsp_amt_str);
}
if(isnumber(last_pi_ovr_amt_str)){
	last_pi_ovr_amt = atof(last_pi_ovr_amt_str);
}
if(isnumber(last_pi_ren_amt_str)){
	last_pi_ren_amt = atof(last_pi_ren_amt_str);
}
if(isnumber(pi_retain_base_str)){
	pi_retain_base = atof(pi_retain_base_str);
}
if(isnumber(pi_retain_target_str)){
	pi_retain_target = atof(pi_retain_target_str);
}
if(isnumber(pi_retain_stretch_str)){
	pi_retain_stretch = atof(pi_retain_stretch_str);
}
if(isnumber(save_base_margin_adj_str)){
	save_base_margin_adj = atof(save_base_margin_adj_str);
}
if(isnumber(save_targ_pct_retain_str)){
	save_targ_pct_retain = atof(save_targ_pct_retain_str);
}
if(isnumber(save_str_pct_retain_str)){
	save_str_pct_retain = atof(save_str_pct_retain_str);
}
if(isnumber(curr_haul_margin_dol_str)){
	curr_haul_margin_dol = atof(curr_haul_margin_dol_str);
}
if(isnumber(curr_dsp_margin_dol_str)){
	curr_dsp_margin_dol = atof(curr_dsp_margin_dol_str);
}
if(isnumber(curr_ovr_margin_dol_str)){
	curr_ovr_margin_dol = atof(curr_ovr_margin_dol_str);
}
if(isnumber(curr_ren_margin_dol_str)){
	curr_ren_margin_dol = atof(curr_ren_margin_dol_str);
}

//Change Waste Stream - Code
if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND dsp_change_flag == 1 AND dsp_change_loc_flag == 0){
	haul_base=market_dsp_rate * (1 - alloc_disposal) + haul_base / (1 - svc_base_marg_prem);
	haul_target=market_dsp_rate * (1 - alloc_disposal) * floorAvgSpread + haul_target / (1 - svc_targ_marg_prem);
	haul_stretch=market_dsp_rate * (1 - alloc_disposal) * avgTargetSpread + haul_stretch / (1 - svc_str_marg_prem);
	dsp_base=market_dsp_rate * alloc_disposal;
	dsp_target=market_dsp_rate * floorAvgSpread * alloc_disposal;
	dsp_stretch=market_dsp_rate * avgTargetSpread * alloc_disposal;
	if(billingType <> "Haul + Disposal"){
		ovr_base=market_dsp_rate;
		ovr_target=market_dsp_rate * floorAvgSpread;
		ovr_stretch=market_dsp_rate * avgTargetSpread;
	}else{
		ovr_base=0.0;
		ovr_target=0.0;
		ovr_stretch=0.0;
	}
	tempArray = float[];
	append(tempArray, nb_haul_floor - haul_base);
	append(tempArray, 0.0);
	haul_base=haul_base + max(tempArray) * svc_gap_recovery_pct;
	tempArray = float[];
	append(tempArray, nb_haul_avg - haul_target);
	append(tempArray, 0.0);
	haul_target=haul_target + max(tempArray) * svc_gap_recovery_pct;
	tempArray = float[];
	append(tempArray, nb_haul_target - haul_stretch);
	append(tempArray, 0.0);
	haul_stretch=haul_stretch + max(tempArray) * svc_gap_recovery_pct;
}

//Change DSP Location - code
if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND dsp_change_loc_flag == 1){
	haul_base=market_dsp_rate * (1 - alloc_disposal) + nb_haul_cost / (1 - (curr_haul_margin_pct + svc_base_marg_prem));
	haul_target=market_dsp_rate * (1 - alloc_disposal) * floorAvgSpread + nb_haul_cost / (1 - (curr_haul_margin_pct + svc_targ_marg_prem));
	haul_stretch=market_dsp_rate * (1 - alloc_disposal) * avgTargetSpread + nb_haul_cost / (1 - (curr_haul_margin_pct + svc_str_marg_prem));
	dsp_base=market_dsp_rate * alloc_disposal;
	dsp_target=market_dsp_rate * floorAvgSpread * alloc_disposal;
	dsp_stretch=market_dsp_rate * avgTargetSpread * alloc_disposal;
	if(billingType <> "Haul + Disposal"){
		ovr_base=market_dsp_rate;
		ovr_target=market_dsp_rate * floorAvgSpread;
		ovr_stretch=market_dsp_rate * avgTargetSpread;
	}else{
		ovr_base=0.0;
		ovr_target=0.0;
		ovr_stretch=0.0;
	}
	tempArray = float[];
	append(tempArray, nb_haul_floor - haul_base);
	append(tempArray, 0.0);
	haul_base=haul_base + max(tempArray) * svc_gap_recovery_pct;
	tempArray = float[];
	append(tempArray, nb_haul_avg - haul_target);
	append(tempArray, 0.0);
	haul_target=haul_target + max(tempArray) * svc_gap_recovery_pct;
	tempArray = float[];
	append(tempArray, nb_haul_target - haul_stretch);
	append(tempArray, 0.0);
	haul_stretch=haul_stretch + max(tempArray) * svc_gap_recovery_pct;
}

//Competitive Bid Rate Adjustment - code
if(competitiveBidQuote == true){
	if(not(isnull(cb_dsp_amt)) AND cb_dsp_amt <> 0.0){
		tempArray = float[];
		append(tempArray, nb_dsp_floor);
		append(tempArray, cb_dsp_amt * comp_bid_markup);
		dsp_base = max(tempArray);
		if(billingType <> "Haul + Disposal"){
			tempArray = float[];
			append(tempArray, nb_ovr_floor);
			append(tempArray, cb_dsp_amt * comp_bid_markup);
			ovr_base = max(tempArray);
		}else{
			ovr_base = 0.0;
		}
	}
	if(not(isnull(cb_ren_amt)) AND cb_ren_amt <> 0.0){
		tempArray = float[];
		append(tempArray, nb_ren_floor);
		append(tempArray, cb_ren_amt * comp_bid_markup);
		ren_base = max(tempArray);
	}
	haul_target=haul_base + (haul_target - haul_base) * comp_targ_pct_retain;
	haul_stretch=haul_base + (haul_stretch - haul_base) * comp_str_pct_retain;
	if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND dsp_change_flag == 1){
		dsp_target=dsp_base + (market_dsp_rate * floorAvgSpread - dsp_base) * comp_targ_pct_retain;
		dsp_stretch=dsp_base + (market_dsp_rate * avgTargetSpread - dsp_base) * comp_str_pct_retain;			
	}else{
		dsp_target=dsp_base + (dsp_target - dsp_base) * comp_targ_pct_retain;
		dsp_stretch=dsp_base + (dsp_stretch - dsp_base) * comp_str_pct_retain;
	}
	if(billingType <> "Haul + Disposal"){
		if((not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")) AND dsp_change_flag == 1){
			ovr_target=ovr_base + (market_dsp_rate * floorAvgSpread - ovr_base) * comp_targ_pct_retain;
			ovr_stretch=ovr_base + (market_dsp_rate * avgTargetSpread - ovr_base) * comp_str_pct_retain;
		}else{
			ovr_target=ovr_base + (ovr_target - ovr_base) * comp_targ_pct_retain;
			ovr_stretch=ovr_base + (ovr_stretch - ovr_base) * comp_str_pct_retain;
		}
	}else{
		ovr_target = 0.0;
		ovr_stretch = 0.0;
	}
	ren_target=ren_base + (ren_target - ren_base) * comp_targ_pct_retain;
	ren_stretch=ren_base + (ren_stretch - ren_base) * comp_str_pct_retain;
}

//Rollback PI - code
if(rollbackOfPIQuote == true){
	haul_base=haul_rate - last_pi_haul_amt + last_pi_haul_amt * pi_retain_base;
    haul_target=haul_rate - last_pi_haul_amt + last_pi_haul_amt * pi_retain_target;
    haul_stretch=haul_rate - last_pi_haul_amt + last_pi_haul_amt * pi_retain_stretch;
    dsp_base=dsp_rate - last_pi_dsp_amt + last_pi_dsp_amt * pi_retain_base;
    dsp_target=dsp_rate - last_pi_dsp_amt + last_pi_dsp_amt * pi_retain_target;
    dsp_stretch=dsp_rate - last_pi_dsp_amt + last_pi_dsp_amt * pi_retain_stretch;
    ovr_base=ovr_rate - last_pi_ovr_amt + last_pi_ovr_amt * pi_retain_base;
    ovr_target=ovr_rate - last_pi_ovr_amt + last_pi_ovr_amt * pi_retain_target;
    ovr_stretch=ovr_rate - last_pi_ovr_amt + last_pi_ovr_amt * pi_retain_stretch;
    ren_base=ren_rate - last_pi_ren_amt + last_pi_ren_amt * pi_retain_base;
    ren_target=ren_rate - last_pi_ren_amt + last_pi_ren_amt * pi_retain_target;
    ren_stretch=ren_rate - last_pi_ren_amt + last_pi_ren_amt * pi_retain_stretch;
}

//Rate Rollback - code
if(rollbackOfPIQuote == true){
	tempArray = float[];
	if(not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")){
		append(tempArray, haul_base);
	}else{
		tempArray2 = float[];
		append(tempArray2, haul_rate);
		append(tempArray2, nb_haul_floor);
		append(tempArray, max(tempArray2));
	}
	append(tempArray, nb_haul_cost + curr_haul_margin_dol * save_base_margin_adj);
	haul_base=min(tempArray);	
	tempArray = float[];
	if(not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")){
		append(tempArray, dsp_base);
	}else{
		tempArray2 = float[];
		append(tempArray2, dsp_rate);
		append(tempArray2, nb_dsp_floor);
		append(tempArray, max(tempArray2));
	}
	append(tempArray, nb_dsp_cost + curr_dsp_margin_dol * save_base_margin_adj);
	dsp_base=min(tempArray);	
	tempArray = float[];
	if(not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")){
		append(tempArray, ovr_base);
	}else{
		tempArray2 = float[];
		append(tempArray2, ovr_rate);
		append(tempArray2, nb_ovr_floor);
		append(tempArray, max(tempArray2));
	}
	append(tempArray, nb_ovr_cost + curr_ovr_margin_dol * save_base_margin_adj);
	ovr_base=min(tempArray);	
	tempArray = float[];
	if(not(isnull(salesActivity)) AND (salesActivity == "Service level change" OR salesActivity == "Change of Owner")){
		append(tempArray, ren_base);
	}else{
		tempArray2 = float[];
		append(tempArray2, ren_rate);
		append(tempArray2, nb_ren_floor);
		append(tempArray, max(tempArray2));
	}
	append(tempArray, nb_ren_cost + curr_ren_margin_dol * save_base_margin_adj);
	ren_base=min(tempArray);	
	tempArray = float[];
	append(tempArray, nb_haul_floor);
	append(tempArray, haul_base + curr_haul_margin_dol * save_str_pct_retain);
	haul_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_dsp_floor);
	append(tempArray, dsp_base + curr_dsp_margin_dol * save_str_pct_retain);
	dsp_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_ovr_floor);
	append(tempArray, ovr_base + curr_ovr_margin_dol * save_str_pct_retain);
	ovr_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_ren_floor);
	append(tempArray, ren_base + curr_ren_margin_dol * save_str_pct_retain);
	ren_stretch = max(tempArray);	
	haul_target=haul_base + (haul_stretch - haul_base) * save_targ_pct_retain;
    dsp_target=dsp_base + (dsp_stretch - dsp_base) * save_targ_pct_retain;
    ovr_target=ovr_base + (ovr_stretch - ovr_base) * save_targ_pct_retain;
    ren_target=ren_base + (ren_stretch - ren_base) * save_targ_pct_retain;
}	
		
//Change Fees - code
if(fee_flag_change == 1){
	haul_base=(1 + (1 - frf_flag) * haul_base_frf_premium) * haul_base;
	haul_target=(1 + (1 - frf_flag) * haul_target_frf_premium) * haul_target;
	haul_stretch=(1 + (1 - frf_flag) * haul_stretch_frf_premium) * haul_stretch;
    dsp_base=(1 + (1 - frf_flag) * haul_base_frf_premium) * dsp_base;
    dsp_target=(1 + (1 - frf_flag) * haul_target_frf_premium) * dsp_target;
    dsp_stretch=(1 + (1 - frf_flag) * haul_stretch_frf_premium) * dsp_stretch;
    ovr_base=(1 + (1 - frf_flag) * haul_base_frf_premium) * ovr_base;
    ovr_target=(1 + (1 - frf_flag) * haul_target_frf_premium) * ovr_target;
    ovr_stretch=(1 + (1 - frf_flag) * haul_stretch_frf_premium) * ovr_stretch;
    ren_base=(1 + (1 - frf_flag) * haul_base_frf_premium) * ren_base;
    ren_target=(1 + (1 - frf_flag) * haul_target_frf_premium) * ren_target;
    ren_stretch=(1 + (1 - frf_flag) * haul_stretch_frf_premium) * ren_stretch;
}

//Allocate Fees - code
haul_cost=nb_haul_cost / (1 + fee_pct);
haul_base=haul_base / (1 + fee_pct);
haul_target=haul_target / (1 + fee_pct);
haul_stretch=haul_stretch / (1 + fee_pct);
dsp_cost=nb_dsp_cost / (1 + fee_pct);
dsp_base=dsp_base / (1 + fee_pct);
dsp_target=dsp_target / (1 + fee_pct);
dsp_stretch=dsp_stretch / (1 + fee_pct);
ovr_cost=nb_ovr_cost / (1 + fee_pct);
ovr_base=ovr_base / (1 + fee_pct);
ovr_target=ovr_target / (1 + fee_pct);
ovr_stretch=ovr_stretch / (1 + fee_pct);
ren_cost=nb_ren_cost / (1 + fee_pct);
ren_base=ren_base / (1 + fee_pct);
ren_target=ren_target / (1 + fee_pct);
ren_stretch=ren_stretch / (1 + fee_pct);

//Safety - Prior Guardrails - code
if(rollbackOfPIQuote <> true){
	tempArray = float[];
	append(tempArray, nb_haul_floor);
	append(tempArray, haul_base);
	haul_base = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_dsp_floor);
	append(tempArray, dsp_base);
	dsp_base = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_ovr_floor);
	append(tempArray, ovr_base);
	ovr_base = max(tempArray);	
	tempArray = float[];
	append(tempArray, nb_ren_floor);
	append(tempArray, ren_base);
	ren_base = max(tempArray);	
	tempArray = float[];
	append(tempArray, haul_base);
	append(tempArray, haul_target);
	haul_target = max(tempArray);	
	tempArray = float[];
	append(tempArray, dsp_base);
	append(tempArray, dsp_target);
	dsp_target = max(tempArray);	
	tempArray = float[];
	append(tempArray, ovr_base);
	append(tempArray, ovr_target);
	ovr_target = max(tempArray);	
	tempArray = float[];
	append(tempArray, ren_base);
	append(tempArray, ren_target);
	ren_target = max(tempArray);	
	tempArray = float[];
	append(tempArray, haul_target);
	append(tempArray, haul_stretch);
	haul_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, dsp_target);
	append(tempArray, dsp_stretch);
	dsp_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, ovr_target);
	append(tempArray, ovr_stretch);
	ovr_stretch = max(tempArray);	
	tempArray = float[];
	append(tempArray, ren_target);
	append(tempArray, ren_stretch);
	ren_stretch = max(tempArray);
}

//round to the nearest penny - code
haul_base=round(haul_base,2);
haul_target=round(haul_target,2);
haul_stretch=round(haul_stretch,2);
haul_frf = haul_target * frfRate * frf_flag;
haul_erf = (haul_target + (haul_frf * is_erf_on_frf_charged)) * erfRate * erf_flag;

dsp_base=round(dsp_base,2);
dsp_target=round(dsp_target,2);
dsp_stretch=round(dsp_stretch,2);
dsp_frf = dsp_target * frfRate * frf_flag;
dsp_erf = (dsp_target + (dsp_frf * is_erf_on_frf_charged)) * erfRate * erf_flag;

ovr_base=round(ovr_base,2);
ovr_target=round(ovr_target,2);
ovr_stretch=round(ovr_stretch,2);
ovr_frf = ovr_target * frfRate * frf_flag;
ovr_erf = (ovr_target + (ovr_frf * is_erf_on_frf_charged)) * erfRate * erf_flag;

ren_base=round(ren_base,2);
ren_target=round(ren_target,2);
ren_stretch=round(ren_stretch,2);
ren_frf = ren_target * frfRate * frf_flag;
ren_erf = (ren_target + (ren_frf * is_erf_on_frf_charged)) * erfRate * erf_flag;

put(returnDict, "haul_base", string(haul_base));
put(returnDict, "haul_target", string(haul_target));
put(returnDict, "haul_stretch", string(haul_stretch));
put(returnDict, "haul_frf", string(haul_frf));
put(returnDict, "haul_erf", string(haul_erf));

put(returnDict, "dsp_base", string(dsp_base));
put(returnDict, "dsp_target", string(dsp_target));
put(returnDict, "dsp_stretch", string(dsp_stretch));
put(returnDict, "dsp_frf", string(dsp_frf));
put(returnDict, "dsp_erf", string(dsp_erf));

put(returnDict, "ovr_base", string(ovr_base));
put(returnDict, "ovr_target", string(ovr_target));
put(returnDict, "ovr_stretch", string(ovr_stretch));
put(returnDict, "ovr_frf", string(ovr_frf));
put(returnDict, "ovr_erf", string(ovr_erf));

put(returnDict, "ren_base", string(ren_base));
put(returnDict, "ren_target", string(ren_target));
put(returnDict, "ren_stretch", string(ren_stretch));
put(returnDict, "ren_frf", string(ren_frf));
put(returnDict, "ren_erf", string(ren_erf));

return returnDict;