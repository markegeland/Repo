#!/usr/bin/perl -w
#
# Display information from the BIDW database about a given account/container_group
#

use strict;
use Getopt::Long;
use Data::Dumper;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use DBIx::XHTML_Table;

my $q = CGI->new;

my $dbh = DBI->connect(
  "DBI:ODBC:prodbirpt_DWCORE",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

my $css=qq{
      table.gridtable {
        font-family: verdana,arial,sans-serif;
        font-size:11px;
        color:#333333;
        border-width: 1px;
        border-color: #666666;
        border-collapse: collapse;
      }
      table.gridtable th {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #dedede;
      }
      table.gridtable td {
        border-width: 1px;
        padding: 8px;
        border-style: solid;
        border-color: #666666;
        background-color: #ffffff;
      }
};

print $q->header,
      $q->start_html(
        '-title' => 'Account Info',
	'-style' => {'-code' => $css}
      ),
      $q->h1('Account Info'),
      $q->start_form,
      'Enter InfoPro division: ', $q->textfield('div'),  ' (required)', $q->p,
      'Enter InfoPro account: ',  $q->textfield('acct'), ' (required)', $q->p,
      'Enter InfoPro site: ',     $q->textfield('site'), ' (optional)', $q->p,
      'Enter InfoPro cg: ',       $q->textfield('cg'),   ' (optional)', $q->p,
      $q->submit('submit'),
      $q->end_form;

my $submit = $q->param('submit');
my $div    = $q->param('div');
my $acct   = $q->param('acct');
my $site   = $q->param('site');
$site = sprintf("%0.5d",$site) if $site;
my $cg     = $q->param('cg');

exit unless $div && $acct;
my $sql;
my $cgiLink = '/cgi-bin/capture/account_info.cgi';

print $q->h2('Account Overview');
$sql = qq{
  SELECT infopro_div_nbr,
         '<a href="$cgiLink?div=' + infopro_div_nbr + 
                                    '&acct=' + acct_nbr + '">' + acct_nbr + '</a>' AS acct_nbr,
         '<a href="$cgiLink?div=' + infopro_div_nbr +
                                    '&acct=' + acct_nbr +
                                    '&site=' + site_nbr + '">' + site_nbr + '</a>' AS site_nbr,
         '<a href="$cgiLink?div=' + infopro_div_nbr +
                                    '&acct=' + acct_nbr +
                                    '&site=' + site_nbr + 
                                    '&cg=' + CAST(container_grp_nbr AS VARCHAR) + '">' + 
                                             CAST(container_grp_nbr AS VARCHAR) + '</a>' AS container_grp_nbr
    FROM Dim_Container_Grp
   WHERE infopro_div_nbr=?
     AND acct_nbr=?
     AND is_current=1
   ORDER
      BY infopro_div_nbr,
         acct_nbr,
         site_nbr,
         CAST(container_grp_nbr AS INT)
};
printTable($dbh, $sql, [$div,$acct]);

if ($div && $acct) {
  print $q->h2('Dim_Acct');
  $sql = qq{
    SELECT Infopro_Div_Nbr,
	   Acct_Nbr,
	   Acct_Aquisition,
	   Acct_Nm,
	   Addr_Line_1,
	   Addr_Line_2,
	   City,
	   State,
	   Postal_Cd,
	   Postal_Cd_Plus_4,
	   Country,
	   Phone,
	   Phone_ext,
	   Fax,
	   Acct_Cat,
	   Acct_Major_Class_Cd,
	   Broker_Nm,
	   is_CBS,
	   Acct_Open_Dt,
	   Acct_Close_Dt,
	   is_Active,
	   Source_System,
	   Next_Review_Dt,
	   Acct_Type,
	   is_Franchise,
	   is_Nat_Acct,
	   is_Centrally_Billed,
	   Customer_Nbr,
	   CBS_Nbr,
	   is_National_Account,
	   Cur_Customer_Nbr,
	   Cur_CBS_Nbr,
	   Risk_Cd
      FROM Dim_Acct
     WHERE infopro_div_nbr=?
      AND acct_nbr=?
      AND is_current=1
  };
  printTable($dbh, $sql, [$div,$acct]);
}

if ($div && $acct && $site) {
  print $q->h2('Dim_Site');
  $sql = qq{
    SELECT Infopro_Div_Nbr,
	   Acct_Nbr,
	   Site_Nbr,
	   Site_Nm,
	   Addr_Line_1,
	   Addr_Line_2,
	   City,
	   State,
	   Postal_Cd,
	   Postal_Cd_Plus4,
	   Country,
	   Phone_Area_Cd,
	   Phone_Nbr,
	   Phone_Ext,
	   Service_Contact_Nm,
	   Contact_Title,
	   Auth_Nm,
	   Auth_Title,
	   Territory,
	   Sales_Rep_Id,
	   Contract_Nbr,
	   Contract_Term,
	   Contract_Status,
	   Original_Open_Dt,
	   Effective_Dt,
	   Close_Dt,
	   Review_Dt,
	   Expiration_Dt,
	   SIC_Cd,
	   Tax_Cd,
	   is_Suspended,
	   is_Shared,
	   Source_System,
	   is_Active_CDH_Location,
	   Service_Location_Nbr,
	   Site_SFDC_Nbr,
	   Customer_Store_Nbr,
	   Purchase_Order_Nbr,
	   LATITUDE,
	   LONGITUDE,
	   PARCEL_ID,
	   Fax
      FROM Dim_Site
     WHERE infopro_div_nbr=?
       AND acct_nbr=?
       AND site_nbr=?
       AND is_current=1
  };
  printTable($dbh, $sql, [$div,$acct,$site]);
}

if ($div && $acct && $site && $cg) {
  print $q->h2('Dim_Container_Grp');
  $sql = qq{
    SELECT *
      FROM Dim_Container_Grp
     WHERE infopro_div_nbr=?
       AND acct_nbr=?
       AND site_nbr=?
       AND container_grp_nbr=?
       AND is_current=1
  };
  printTable($dbh, $sql, [$div,$acct,$site,$cg]);

  print $q->h2('Rpt_Rate_Hist');
  print $q->h3('(REG/R/F, REC/R/F, and REN/R/F only)');
  $sql = qq{
    SELECT dcc.Charge_Cd,
	   dcc.Charge_Typ,
	   dcc.Charge_Method,
	   rrh.Rate_Amt,
	   CONVERT(DATE,rrh.Rate_Eff_Dt) AS Rate_Eff_Dt,
	   CONVERT(DATE,rrh.Rate_Disc_Dt) AS Rate_Disc_Ct,
	   rrh.Disposal_UOM,
	   rrh.is_Revenue,
	   rrh.is_Residential,
	   rrh.is_Disposal,
	   rrh.is_Rebate,
	   rrh.Infopro_Div_Nbr,
	   rrh.Acct_Nbr,
	   rrh.Site_Nbr,
	   rrh.Container_Grp_Nbr,
	   rrh.Revenue_Distribution_Cd,
	   rrh.Tax_Application_Cd,
	   rrh.is_Locked,
	   rrh.is_Charged,
	   rrh.is_Fee,
	   rrh.is_ERF_on_FRF,
	   rrh.Waste_Material_Type
      FROM Rpt_Rate_Hist rrh
	   INNER JOIN Dim_Charge_Cd dcc
	      ON rrh.Charge_Cd_SK=dcc.Charge_Cd_SK
     WHERE rrh.Infopro_Div_Nbr=?
       AND rrh.Acct_Nbr=?
       AND rrh.Site_Nbr=?
       AND rrh.Container_Grp_Nbr=?
       AND dcc.Charge_Cd IN ('REG','REC','REN')
       AND dcc.Charge_Typ = 'R'
       AND dcc.Charge_Method  = 'F'
       AND rrh.is_current=1
     ORDER
	BY rrh.rate_eff_dt
  };
  printTable($dbh, $sql, [$div,$acct,$site,$cg]);

  print $q->h2('Fact_Sales_Activity');
  $sql = qq{
    SELECT trc.Txn_Cd,
	   trc.Reason_Cd,
	   trc.Txn_Reason_Desc,
	   fsa.Eff_Dt_SK,
	   fsa.Txn_Keyed_Dt_SK,
	   fsa.Monthly_Sales_Amt,
	   fsa.Monthly_Sales_Change_Amt,
	   fsa.Monthly_Lift_Cnt,
	   fsa.Monthly_Lift_Change_Cnt,
	   fsa.Monthly_Yard_Cnt,
	   fsa.Monthly_Yard_Change_Cnt,
	   fsa.container_cnt,
	   fsa.Container_Change_Cnt,
	   fsa.Revenue_Expect_Disposal_Amt,
	   fsa.Revenue_Expect_Disposal_Change_Amt,
	   fsa.Unit_Cnt,
	   fsa.Unit_Change_Cnt,
	   fsa.Average_Lifts,
	   fsa.Average_Disposal_Tons,
	   fsa.Estimated_Lift_Cnt,
	   fsa.Source_Revenue_Expect_Disposal_Amt,
	   fsa.Source_Revenue_Expect_Disposal_Change_Amt,
	   fsa.is_Txn_Franchise,
	   fsa.FRF_Pct,
	   fsa.FRF_Is_Locked,
	   fsa.FRF_Calc_Fee_Amt,
	   fsa.Is_FRF_On,
	   fsa.ERF_Pct,
	   fsa.ERF_Is_Locked,
	   fsa.ERF_Calc_Fee_Amt,
	   fsa.Is_ERF_on_FRF,
	   fsa.Is_ERF_On
      FROM fact_sales_activity fsa
	   INNER JOIN dim_container_grp cg
	      ON fsa.Container_Grp_SK=cg.Container_Grp_SK
	   INNER join dim_txn_reason_cd trc
	      ON fsa.Txn_Reason_Cd_SK=trc.Txn_Reason_Cd_SK
     WHERE cg.Infopro_Div_Nbr=?
       AND cg.Acct_Nbr=?
       AND cg.Site_Nbr=?
       AND cg.Container_Grp_Nbr=?
     ORDER
	BY fsa.eff_dt_sk
  };
  printTable($dbh, $sql, [$div,$acct,$site,$cg]);
}

# subroutines

sub printTable {
  my $dbh = shift;
  my $sql = shift;
  my $bindAref = shift;

  my $table = DBIx::XHTML_Table->new($dbh);
  my $sth = $dbh->prepare($sql);

  $table->exec_query($sth, $bindAref);
  $table->modify(table => { class => 'gridtable' });

  print $table->output();
}
