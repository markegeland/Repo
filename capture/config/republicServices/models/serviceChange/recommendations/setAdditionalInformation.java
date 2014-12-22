/**********************************************************************************************
 *
 * 20141219 JPalubinskas - 2014R1 Upgrade 6 fix for split function change.
 *                         Also added pulling the contId F/O flag from the Account Status call.
 *
 **********************************************************************************************/

accountType = "";
sharedCont = "";
franchise = 0;
contId = "O"; // default to Open Market 'O'
serviceDays = "";
finalServDays = ""; 
contractGroup = "";

ROW_START = "<tr>";
ROW_END = "</tr>";
COL_START = "<td>";
COL_END = "</td>";
 
accountStatusRecs = bmql("SELECT Acct_Type, Shared_Cont_Grp_Nbr, is_franchise, Lift_Days, Contract_Grp_Nbr, period FROM Account_Status WHERE Container_Grp_Nbr = $containerGroup_config AND  infopro_acct_nbr = $accountNumber AND Site_Nbr = $siteNumber_config");

for rec in accountStatusRecs{
    accountType = get(rec, "Acct_Type");
    sharedCont = get(rec, "Shared_Cont_Grp_Nbr");
    franchise = getInt(rec, "is_franchise");

    if (franchise == 1) {
        contId = "F"; // Franchise 'F'
    } else {
        contId = "O";
    }

    serviceDays = get(rec, "Lift_Days"); //0111111
    contractGroup = get(rec, "Contract_Grp_Nbr");
}

//Converting bit array to Mon,Tue,Wed,Thu,Fri,Sat,Sun
if(serviceDays <> ""){
    serviceDaysArr = split(serviceDays, "");
    //print "serviceDaysArr: "; print serviceDaysArr;
    i = 0;

    for each in serviceDaysArr{
        if(i == 0 AND each == "1"){
            finalServDays = finalServDays + "Mon";
        }elif(i == 1 AND each == "1"){
            finalServDays = finalServDays + "Tue";
        }elif(i == 2 AND each == "1"){
            finalServDays = finalServDays + "Wed";
        }elif(i == 3 AND each == "1"){
            finalServDays = finalServDays + "Thu";
        }elif(i == 4 AND each == "1"){
            finalServDays = finalServDays + "Fri";
        }elif(i == 5 AND each == "1"){
            finalServDays = finalServDays + "Sat";
        }elif(i == 6 AND each == "1"){
            finalServDays = finalServDays + "Sun";
        }else{
            finalServDays = finalServDays + "-";
        }
        i = i + 1;
    }
}else{
    finalServDays = "-------";
}

if(sharedCont == "''" OR sharedCont == "" OR sharedCont == "\"") {
    sharedCont = "No";
}

retStr = "<style type='text/css'>.addInfoTable tr:nth-child(even){background: #C0D9D9;} .addInfoTable td {width: 200px;padding: 5px;}</style>";
    
retStr = retStr + "<table class='addInfoTable' border='1'><tbody>" 
        + ROW_START + COL_START + "Container Group" + COL_END + COL_START + containerGroup_config + COL_END + ROW_END
        + ROW_START + COL_START + "Account Type" + COL_END + COL_START + accountType + COL_END + ROW_END 
        + ROW_START + COL_START + "Shared Cont" + COL_END + COL_START + sharedCont + COL_END + ROW_END
        + ROW_START + COL_START + "Cont. ID (F/O)" + COL_END + COL_START + contId + COL_END + ROW_END
        + ROW_START + COL_START + "Service Days" + COL_END + COL_START + finalServDays + COL_END + ROW_END
        + ROW_START + COL_START + "Contract/Group" + COL_END + COL_START + contractGroup + COL_END + ROW_END
        + "</tbody></table>";
        
return retStr;