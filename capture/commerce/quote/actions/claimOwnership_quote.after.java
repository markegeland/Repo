result="";
result = result + "1~preparedByEmail_quote~" + _system_user_email + "|";
result = result + "1~quoteOwner_quote~" + _system_user_first_name + " " + _system_user_last_name + "|";
return result + commerce.setStatus("claim");