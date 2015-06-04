/*
20150604 - Gaurav Dawar - #444 pulling the number of lifts from account status.
*/
liftsPerContainer = "";
pickupPeriodUnit = "week";
 

accountStatusRecs = bmql("SELECT container_cnt, Pickup_Per_Tot_Lifts, Pickup_Period_Length, period FROM Account_Status WHERE Container_Grp_Nbr = $containerGroup_config AND  infopro_acct_nbr = $accountNumber AND Site_Nbr = $siteNumber_config");
/*Pickup_Period_Unit column has been removed from Account_statu table because it will always contain the value "week", so just hard-coding it here */
for rec in accountStatusRecs{
	pickupPeriodTotalLifts = getFloat(rec, "Pickup_Per_Tot_Lifts");
	pickupPeriodLength = getInt(rec, "Pickup_Period_Length");
	container_cnt = getFloat(rec, "container_cnt");
	period = getFloat(rec, "period");
	//pickupPeriodUnit = get(rec, "Pickup_Period_Unit");
	
	if(pickupPeriodLength == 10 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 10 weeks";
	}
	elif(pickupPeriodLength == 9 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 9 weeks";
	}
	elif(pickupPeriodLength == 8 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 8 weeks";
	}
	elif(pickupPeriodLength == 7 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 7 weeks";
	}
	elif(pickupPeriodLength == 6 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 6 weeks";
	}
	elif(pickupPeriodLength == 5 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 5 weeks";
	}
	elif(pickupPeriodLength == 4 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 4 weeks";
	}
	elif(pickupPeriodLength == 3 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "Every 3 weeks";
	}
	elif(pickupPeriodLength == 2 AND lower(pickupPeriodUnit) == "week"){
		liftsPerContainer = "EOW";
	}
	elif(pickupPeriodLength == 1 AND lower(pickupPeriodUnit) == "week"){
		frequency = pickupPeriodTotalLifts/(container_cnt/* * period*/);
		frequencyInt = integer(frequency);
		liftsPerContainer = string(frequencyInt) + "/week";
		/*if(frequencyInt == 1 OR frequencyInt == 0 ){
			liftsPerContainer = "1/week";
		}
		elif(frequencyInt == 2){
			liftsPerContainer = "2/week";
		}
		elif(frequencyInt == 3){
			liftsPerContainer = "3/week";
		}
		elif(frequencyInt == 4){
			liftsPerContainer = "4/week";
		}
		elif(frequencyInt == 5){
			liftsPerContainer = "5/week";
		}
		elif(frequencyInt == 6){
			liftsPerContainer = "6/week";
		}
		elif(frequencyInt == 7){
			liftsPerContainer = "7/week";
		}*/
	}	
}

return liftsPerContainer;