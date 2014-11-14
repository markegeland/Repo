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

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $q = CGI->new;

my $dbh = DBI->connect(
  "DBI:ODBC:prodbmisql01_BMIDM",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

print $q->header,
      $q->start_html(
        '-title' => 'BMIDM Account Info',
	'-style' => {'-code' => $css}
      ),
      $q->h1('BMIDM Account Info'),
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
my $acct_key = "$div-$acct" if $div && $acct;

exit unless $div && $acct;
my $sql;
my $cgiLink = $q->url();

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
    FROM account_status
   WHERE acct_key=?
   ORDER
      BY infopro_div_nbr,
         acct_nbr,
         site_nbr,
         CAST(container_grp_nbr AS INT)
};
printTable($dbh, $sql, [$acct_key]);

if ($div && $acct) {
  print $q->h2('account_status');
  my @bindVars = ($acct_key);
  my $predicate = '';
  if ($site) {
    $predicate .= ' AND site_nbr=? ';
    push(@bindVars, $site);
  }
  if ($cg) {
    $predicate .= ' AND container_grp_nbr=? ';
    push(@bindVars, $cg);
  }
  $sql = qq{
    SELECT *
      FROM account_status
     WHERE acct_key=? $predicate
  };
  printTable($dbh, $sql, \@bindVars);

  print $q->h2('accounts_master');
  $sql = qq{
    SELECT *
      FROM accounts_master
     WHERE customer_id=?
  };
  printTable($dbh, $sql, [$acct_key]);

  print $q->h2('accounts_addresses');
  @bindVars = ($acct_key);
  $predicate = '';
  if ($site) {
    $predicate .= ' AND billing_id=? ';
    push(@bindVars, $site);
  }
  $sql = qq{
    SELECT *
      FROM accounts_addresses
     WHERE customer_id=? $predicate
  };
  printTable($dbh, $sql, \@bindVars);

  print $q->h2('account_sales_hist');
  @bindVars = ($acct_key);
  $predicate = '';
  if ($site) {
    $predicate .= ' AND site_nbr=? ';
    push(@bindVars, $site);
  }
  if ($cg) {
    $predicate .= ' AND container_grp_nbr=? ';
    push(@bindVars, $cg);
  }
  $sql = qq{
    SELECT *
      FROM account_sales_hist
     WHERE infopro_acct_nbr=? $predicate
     ORDER
        BY infopro_div_nbr,
           acct_nbr,
           site_nbr,
           container_grp_nbr,
           eff_dt_sk
  };
  printTable($dbh, $sql, \@bindVars);

  print $q->h2('account_rates');
  @bindVars = ($div,$acct);
  $predicate = '';
  if ($site) {
    $predicate .= ' AND site_nbr=? ';
    push(@bindVars, $site);
  }
  if ($cg) {
    $predicate .= ' AND container_grp_nbr=? ';
    push(@bindVars, $cg);
  }
  $sql = qq{
    SELECT *
      FROM account_rates
     WHERE infpro_div_nbr=? 
       AND acct_nbr=? $predicate
     ORDER
        BY infpro_div_nbr,
           acct_nbr,
           site_nbr,
           container_grp_nbr,
           charge_cd,
           charge_typ,
           charge_method,
           rate_eff_dt
  };
  printTable($dbh, $sql, \@bindVars);


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
