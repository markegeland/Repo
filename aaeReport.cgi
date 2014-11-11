#!/usr/bin/perl -w
#
# List all completed Automated Account Entry quotes

use strict;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;

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

my $q = CGI->new();

print $q->header;
print $q->start_html(
  '-head'   => $q->Link({
    '-rel'  => 'stylesheet',
    '-href' => '//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/smoothness/jquery-ui.css'
  }),
  '-title'  =>'InfoPro AAE Completions',
  '-style'  => {'-code' => $css},
  '-script' => [
    {'-type' => 'JAVASCRIPT',
     '-src'  => '//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js'},
    {'-type' => 'JAVASCRIPT',
     '-src'  => '//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js'},
    {'-type' => 'JAVASCRIPT',
     '-code' => q<
       $(function() {
         $( "#datepicker" ).datepicker({
           dateFormat: "yy-mm-dd",
           showButtonPanel: true,
           changeMonth: true,
           changeYear: true,
           minDate: "2014-01-01"
         });
       });
     >}
  ]
);

my ($year,$mon,$day) = (localtime)[5,4,3];
my $today = sprintf("%0.4d-%0.2d-%0.2d", 1900+$year, ++$mon, $day);

warn "today=$today\n";

print $q->h1('InfoPro AAE Completions');
print $q->start_form;
print $q->p('Show quotes completed on or after: ');
print $q->textfield(
  '-name'  => 'since',
  '-id'    => 'datepicker',
  '-value' => $today
);
print $q->submit('submit');
print $q->end_form;


my $since = $q->param('since');
my $submit = $q->param('submit');

exit unless defined $since and $submit;

my $dbh = DBI->connect(
  "DBI:ODBC:srazphx12_devr1_DWCORE",
  'DWCORE',
  'manunited20'
) or die("DBI Connect Error - $DBI::errstr\n");

$dbh->do(qq{
  set CURSOR_CLOSE_ON_COMMIT off
  set ANSI_NULL_DFLT_ON on
  set ANSI_NULLS on
  set ANSI_WARNINGS on
  set CONCAT_NULL_YIELDS_NULL on
  set QUOTED_IDENTIFIER on
});

# Find completed Automated Account Entry requests
my $sth = $dbh->prepare(qq{
  SELECT * FROM OPENQUERY(infopro,
  'SELECT RMSQUTID AS quote_number,
	  SRCINID  AS bs_id,
	  CUCO     AS infopro_div_nbr,
	  CUCUNO   AS acct_nbr,
	  added_time_stamp
     FROM allied.cufile.CUPAASAC
    WHERE statuscode=''CMP''
      AND CAST(added_time_stamp AS DATE) >= ''$since''
    ORDER
       BY added_time_stamp DESC
   FOR FETCH ONLY WITH UR'
  )
});

$sth->execute;

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('Quote Number','BS_ID','InfoPro Div','Account','Added TS');
$table->setRowHead(-1);

my $count=0;
while (my @row = $sth->fetchrow_array) {
  $count++;
  $table->addRow(@row);
}
print $q->p;

if ($count) {
  print $table->getTable;
}
else {
  print "No quotes found since $since"
}

print $q->end_html;
