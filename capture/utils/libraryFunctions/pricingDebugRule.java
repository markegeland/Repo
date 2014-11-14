debugDict = dict("string");
priceType = "";
if(containskey(guardrailDebugInputDict, "priceType")){
	priceType = get(guardrailDebugInputDict, "priceType");	
}

if(priceType == "Containers"){
//Small container input
put(debugDict, "equipment_size", get(pricingDebugInputDict, "containerSize"));
put(debugDict, "division", get(pricingDebugInputDict, "division_quote"));
put(debugDict, "hauls_per_period", get(pricingDebugInputDict, "frequency"));
put(debugDict, "customer_owned", get(pricingDebugInputDict, "isCustomerOwned"));
put(debugDict, "container_cd", get(pricingDebugInputDict, "routeTypeDerived"));
put(debugDict, "has_compactor", get(pricingDebugInputDict, "compactor"));
put(debugDict, "new_customer_cg_flag", get(pricingDebugInputDict, "newCustomerConfig"));
put(debugDict, "waste_type", get(pricingDebugInputDict, "wasteCategory"));
put(debugDict, "account_type", get(pricingDebugInputDict, "accountType"));
put(debugDict, "contract_term", get(pricingDebugInputDict, "initialTerm_quote"));
put(debugDict, "industry", get(pricingDebugInputDict, "industry"));
put(debugDict, "container_key", get(pricingDebugInputDict, "partNumber"));
put(debugDict, "dsp_cost_per_ton", get(pricingDebugInputDict, "disposalCostPerTon"));
put(debugDict, "quantity", get(pricingDebugInputDict, "containerQuantity"));

//Small container output
put(debugDict, "cost_oper_site_time", get(pricingDebugOutputDict, "operatingCost"));
put(debugDict, "truck_ops_cost_per_minute", get(pricingDebugOutputDict, "truckCostPerMinute"));
put(debugDict, "div_minutes_per_lift", get(pricingDebugOutputDict, "minutesPerLift"));
put(debugDict, "truck_alloc", get(pricingDebugOutputDict, "truckAllocatedValue"));
put(debugDict, "yards_per_month", get(pricingDebugOutputDict, "yardsPerMonth"));
put(debugDict, "commission", get(pricingDebugOutputDict, "commission"));
put(debugDict, "share_of_truck", get(pricingDebugOutputDict, "customerSharePct"));
put(debugDict, "working_capital", get(pricingDebugOutputDict, "workingCapital"));
put(debugDict, "cost_roi", get(pricingDebugOutputDict, "ROI"));
put(debugDict, "cost_disposal_processing", get(pricingDebugOutputDict, "disposalProcessingCost"));
put(debugDict, "cost_disposal_trip", get(pricingDebugOutputDict, "disposalTripCost"));
put(debugDict, "container_depr", get(pricingDebugOutputDict, "totalContainerDepreciation"));
put(debugDict, "tons_per_month", get(pricingDebugOutputDict, "customerTonsPerMonth"));
put(debugDict, "site_time", get(pricingDebugOutputDict, "siteTime"));
put(debugDict, "compactor_maint", get(pricingDebugOutputDict, "compactor_maint"));
put(debugDict, "container_factor", get(pricingDebugOutputDict, "containerFactor"));
put(debugDict, "compactor_life", get(pricingDebugOutputDict, "compactorLife"));
put(debugDict, "compactor_depr_maint", get(pricingDebugOutputDict, "compactor_depr_maint"));
put(debugDict, "truck_depr", get(pricingDebugOutputDict, "totalTruckDepreciation"));
put(debugDict, "cost_assets", get(pricingDebugOutputDict, "assetCost"));
put(debugDict, "cost_to_serve_month", get(pricingDebugOutputDict, "costToServeMonth"));
put(debugDict, "lifts_per_month", get(pricingDebugOutputDict, "liftsPerMonth"));
put(debugDict, "truck_hours_per_month", get(pricingDebugOutputDict, "truckHoursPerMonth"));
put(debugDict, "pounds_per_yard", get(pricingDebugOutputDict, "divisionPoundsPerYard"));
put(debugDict, "roi", get(pricingDebugOutputDict, "floorROI"));
put(debugDict, "p_y", get(pricingDebugOutputDict, "averagePricePerYard"));
put(debugDict, "weight_industry_factor", get(pricingDebugOutputDict, "weightIndustryFactor"));
put(debugDict, "disposal_minutes_per_ton", get(pricingDebugOutputDict, "disposalMinutesPerTon"));
put(debugDict, "site_trips_per_month", get(pricingDebugOutputDict, "tripsPerMonth"));
put(debugDict, "container_maint_per_lift", get(pricingDebugOutputDict, "containerMaintPerLift"));
put(debugDict, "labor_cost_per_minute", get(pricingDebugOutputDict, "laborCostPerMinute"));
put(debugDict, "compactor_depr", get(pricingDebugOutputDict, "compactor_depr"));
}
elif(priceType == "Large Containers"){
	//Large container input
	put(debugDict, "segment", get(guardrailDebugInputDict, "segment"));
	put(debugDict, "total_time", get(guardrailDebugInputDict, "totalTimePerHaul"));
	put(debugDict, "hauls_per_month", get(guardrailDebugInputDict, "estHaulsPerMonth"));
	put(debugDict, "container_cd", get(guardrailDebugInputDict, "routeType"));
	put(debugDict, "alloc_rental", get(guardrailDebugInputDict, "alloc_rental"));
	put(debugDict, "contract_term", get(guardrailDebugInputDict, "initialTerm_quote"));
	put(debugDict, "industry", get(guardrailDebugInputDict, "industry"));
	put(debugDict, "disposal_site", get(guardrailDebugInputDict, "siteName"));
	put(debugDict, "container_key", get(guardrailDebugInputDict, "partNumber"));
	put(debugDict, "billing_type", get(guardrailDebugInputDict, "billingType_l"));
	put(debugDict, "quantity", get(guardrailDebugInputDict, "containerQuantity"));
	put(debugDict, "compactor_amount", get(guardrailDebugInputDict, "compactorValueConfig"));
	put(debugDict, "waste_type", get(guardrailDebugInputDict, "wasteType"));
	put(debugDict, "division", get(guardrailDebugInputDict, "division_quote"));
	put(debugDict, "compactor_cust_owned_", get(guardrailDebugInputDict, "isCompactorCustomerOwned"));
	put(debugDict, "customer_owned", get(guardrailDebugInputDict, "isContainerCustomerOwned"));
	put(debugDict, "est_hauls_per_month", get(guardrailDebugInputDict, "haulsPerPeriod"));
	put(debugDict, "rental_", get(guardrailDebugInputDict, "rental"));
	put(debugDict, "sales_activity", get(guardrailDebugInputDict, "salesActivity_quote"));
	put(debugDict, "contained_type", get(guardrailDebugInputDict, "containerType_l"));
	put(debugDict, "line_of_business", get(guardrailDebugInputDict, "LOB"));
	put(debugDict, "waste_type", get(guardrailDebugInputDict, "wasteCategory"));
	put(debugDict, "account_type", get(guardrailDebugInputDict, "accountType"));
	put(debugDict, "included_tons/minimum_tons_haul/est_tons_haul", get(guardrailDebugInputDict, "estTonsPerHaul"));

	//Large container output	
	put(debugDict, "cost_operating", get(pricingDebugOutputDict, "operatingCost"));
	put(debugDict, "truck_cost_per_minute", get(pricingDebugOutputDict, "truckCostPerMinute"));
	put(debugDict, "container_depr", get(pricingDebugOutputDict, "containerDepreciationPerContainer"));
	put(debugDict, "roa_compactor", get(pricingDebugOutputDict, "compactorROA"));
	put(debugDict, "cost_disposal_per_haul", get(pricingDebugOutputDict, "disposalPerHaul"));
	put(debugDict, "truck_alloc", get(pricingDebugOutputDict, "truckAllocatedValue"));
	put(debugDict, "oper_cont_depr", get(pricingDebugOutputDict, "containerDepreciation"));
	put(debugDict, "cost_to_serve_haul", get(pricingDebugOutputDict, "costToServeHaul"));
	put(debugDict, "cost_roa", get(pricingDebugOutputDict, "ROA"));
	put(debugDict, "oper_driver_cost", get(pricingDebugOutputDict, "driverCost"));
	put(debugDict, "roa_commission", get(pricingDebugOutputDict, "commission"));
	put(debugDict, "roa_working_capital", get(pricingDebugOutputDict, "workingCapital"));
	put(debugDict, "oper_comp_depr", get(pricingDebugOutputDict, "compactorDepreciation"));
	put(debugDict, "has_compactor", get(pricingDebugOutputDict, "hasCompactor"));
	put(debugDict, "minutes_per_haul", get(pricingDebugOutputDict, "haulMinutesPerMonth"));
	put(debugDict, "oper_truck_depr", get(pricingDebugOutputDict, "truckDepreciation"));
	put(debugDict, "price_disposal_rate_per_ton", get(pricingDebugOutputDict, "marketRate"));
	put(debugDict, "included_tons/minimum_tons_haul/est_tons_haul", get(pricingDebugOutputDict, "customerTonsPerMonth"));
	put(debugDict, "container_factor", get(pricingDebugOutputDict, "containerFactor"));
	put(debugDict, "est_hauls_per_month", get(pricingDebugOutputDict, "haulsPerMonthPerContainer"));
	put(debugDict, "compactor_life", get(pricingDebugOutputDict, "compactorLife"));
	put(debugDict, "revenue_per_ton", get(pricingDebugOutputDict, "averagePricePerTon"));
	put(debugDict, "container_maint_per_haul", get(pricingDebugOutputDict, "containerMntPerHaul"));
	put(debugDict, "cost_disposal_per_haul", get(pricingDebugOutputDict, "disposal"));
	put(debugDict, "roa_container", get(pricingDebugOutputDict, "containerROA"));
	put(debugDict, "cost_to_serve_month", get(pricingDebugOutputDict, "costToServeMonth"));
	put(debugDict, "cost_to_serve_month", get(pricingDebugOutputDict, "costToServeMonth"));
	put(debugDict, "compactor_depr", get(pricingDebugOutputDict, "compactorDepreciationPerContainer"));
	put(debugDict, "compactor_depr", get(pricingDebugOutputDict, "compactorDepreciationPerContainer"));
	put(debugDict, "roi", get(pricingDebugOutputDict, "floorROI"));
	put(debugDict, "truck_depr", get(pricingDebugOutputDict, "truckDepreciationPerMonth"));
	put(debugDict, "oper_truck_cost", get(pricingDebugOutputDict, "truckCost"));
	put(debugDict, "cust_compactor_value", get(pricingDebugOutputDict, "compactorValue"));
	put(debugDict, "labor_cost_per_minute", get(pricingDebugOutputDict, "laborCostPerMinute"));
	put(debugDict, "container_cost", get(pricingDebugOutputDict, "containerValue"));
	put(debugDict, "compactor_depr", get(pricingDebugOutputDict, "compactor_depr"));
	put(debugDict, "roa_truck", get(pricingDebugOutputDict, "truckROA"));
	put(debugDict, "dsp_cost_per_ton", get(pricingDebugOutputDict, "disposalCostPerTon"));
}

//Guardrail input
put(debugDict, "segment", get(guardrailDebugInputDict, "segment"));
put(debugDict, "competitor_adj_factor", get(guardrailDebugInputDict, "competitorFactor"));
put(debugDict, "erf_on_frf", get(guardrailDebugInputDict, "erfOnFrfRate"));
put(debugDict, "include_frf", get(guardrailDebugInputDict, "includeFRF"));
put(debugDict, "include_erf", get(guardrailDebugInputDict, "includeERF"));
put(debugDict, "sales_activity", get(guardrailDebugInputDict, "salesActivity_quote"));
put(debugDict, "sales_activity", get(guardrailDebugInputDict, "salesActivity_quote"));
put(debugDict, "infopro_div_nbr", get(guardrailDebugInputDict, "infoProDivNumber"));
put(debugDict, "zip", get(guardrailDebugInputDict, "customer_zip"));


//Guardrail output
put(debugDict, "price_base", get(guardrailDebugOutputDict, "new_business_base"));
put(debugDict, "contract_rate_adj_pct", get(guardrailDebugOutputDict, "contract_dur_adj_factor"));
put(debugDict, "peak_adj_factor", get(guardrailDebugOutputDict, "peak_rate_factor1"));
put(debugDict, "haul_stretch", get(guardrailDebugOutputDict, "haulStretch"));
put(debugDict, "haul_floor", get(guardrailDebugOutputDict, "haulFloor"));
put(debugDict, "haul_base", get(guardrailDebugOutputDict, "haulBase"));
put(debugDict, "haul_target", get(guardrailDebugOutputDict, "haulTarget"));
put(debugDict, "industry_adj_pct", get(guardrailDebugOutputDict, "industry_adj_factor"));
put(debugDict, "segment_adj_pct", get(guardrailDebugOutputDict, "segment_adj_factor"));
put(debugDict, "frf_rate", get(guardrailDebugOutputDict, "frfRate"));
put(debugDict, "erf_rate", get(guardrailDebugOutputDict, "erfRate"));
put(debugDict, "fee_pct", get(guardrailDebugOutputDict, "feePct"));
put(debugDict, "cat_yards_per_month", get(guardrailDebugOutputDict, "cat_yards_per_month"));
put(debugDict, "base_margin_new", get(guardrailDebugOutputDict, "baseMargin"));
put(debugDict, "target_margin_new", get(guardrailDebugOutputDict, "targetMargin"));
put(debugDict, "stretch_margin_new", get(guardrailDebugOutputDict, "stretchMargin"));
put(debugDict, "price_stretch_frf_premium", get(guardrailDebugOutputDict, "stretchFRFPremium"));
put(debugDict, "price_base_frf_premium", get(guardrailDebugOutputDict, "baseFRFPremium"));
put(debugDict, "price_target_frf_premium", get(guardrailDebugOutputDict, "targetFRFPremium"));
if(priceType == "Large Containers"){
	put(debugDict, "rental_target", get(guardrailDebugOutputDict, "rental_target"));
	put(debugDict, "rental_stretch", get(guardrailDebugOutputDict, "rental_stretch"));
	put(debugDict, "rental_base", get(guardrailDebugOutputDict, "rental_base"));
	put(debugDict, "rental_floor", get(guardrailDebugOutputDict, "rental_floor"));
	put(debugDict, "minimum_haul_rate", get(guardrailDebugOutputDict, "minimum_haul_rate"));
	put(debugDict, "comp_rental_floor", get(guardrailDebugOutputDict, "comp_rental_floor"));
	put(debugDict, "comp_rental_rate", get(guardrailDebugOutputDict, "comp_rental_rate"));
}


print debugDict;
return "";