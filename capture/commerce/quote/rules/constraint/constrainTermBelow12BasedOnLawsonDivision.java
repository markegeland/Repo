check_var = 0;
account_type = "";
if(_system_current_step_var == "adjustPricing"){
	if(division_quote == "3607" OR division_quote == "3639"){
		for line in line_process{
			if(line._model_name == "Service Change"){
				account_type = getconfigattrvalue(line._document_number, "accountType_current_readonly");
				print account_type;
			}else{
				account_type = getconfigattrvalue(line._document_number, "accountType");
				print account_type;
			}
			if(account_type == "Permanent"){
				check_var = 1;
			}
		}
		if(check_var == 1){
			return true;
		}else{
			return false;
		}	
	}else{
		return false;
	}
}
else{
	return false;
}