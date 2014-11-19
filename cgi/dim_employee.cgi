#!/usr/bin/perl -w
#
# Display information from the DWCORE Dim_Employee table about a given login
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
  "DBI:ODBC:prodbirpt_DWCORE",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

print $q->header,
      $q->start_html(
        '-title' => 'Historical DWCORE Dim_Employee Info',
	'-style' => {'-code' => $css}
      ),
      $q->h1('Historical DWCORE Dim_Employee Info'),
      $q->start_form,
      'Enter employee network id: ', $q->textfield('login'), $q->p,
      $q->submit('submit'),
      $q->end_form;

my $submit = $q->param('submit');
my $login  = $q->param('login');

exit unless $submit && $login;
my $sql;

print $q->h2('Dim_Employee Info');
$sql = qq{
  SELECT Network_User_ID,
	 Employee_EIN,
	 Last_Name,
	 First_Name,
	 Job_Cd,
	 Job_Desc,
	 lov.Code_Value AS Division,
	 Email_Addr,
	 CAST(Hire_Dt AS DATE) AS Hire_Dt,
	 CAST(Term_Dt AS DATE) AS Term_Dt,
	 Emp_Status,
	 City,
	 State,
	 CAST(Eff_Dt  AS DATE) AS Eff_Dt,
	 CAST(Disc_Dt AS DATE) AS Disc_Dt
    FROM Dim_Employee e
	 INNER JOIN Code_LOV lov
	    ON e.Div_SK=lov.CODE_SK
   WHERE Network_User_ID=?
   ORDER
      BY Eff_Dt
};
printTable($dbh, $sql, [$login]);

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
