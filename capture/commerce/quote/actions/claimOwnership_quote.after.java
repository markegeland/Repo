/*=======================================================================
Updates: 20150217 - Mike Boylan - set compOwnerLogin_quote when ownership is established or changed.
		 20150224 - Gaurav Dawar - set preparedByPhone_quote when ownership is established or changed.
=======================================================================*/
result="";
result = result + "1~preparedByEmail_quote~" + _system_user_email + "|";
result = result + "1~quoteOwner_quote~" + _system_user_first_name + " " + _system_user_last_name + "|";
result = result + "1~compOwnerLogin_quote~" + _system_user_login + "|";
result = result + "1~preparedByPhone_quote~" + _system_user_phone+ "|";
return result + commerce.setStatus("claim");