#!/usr/bin/perl -w
#
# List all completed Automated Account Entry quotes

use strict;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

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

print $q->h1('InfoPro AAE Completions'),
      $q->start_form,
      'InfoPro Division: ',
      $q->textfield('div'),
      $q->p,
      'Show quotes completed on or after: ',
      $q->textfield(
	'-name'  => 'since',
	'-id'    => 'datepicker',
	'-value' => ''
      ),
      $q->p,
      $q->checkbox(
	'-name'    => 'cmpOnly',
        '-checked' => 0,
        '-value'   => 'ON',
        '-label'   => 'Only show completed quotes'),
      $q->p,
      $q->submit('submit'),
      $q->end_form;


my $since   = $q->param('since');
my $div     = $q->param('div');
my $cmpOnly = $q->param('cmpOnly');
my $submit  = $q->param('submit');

exit unless defined $submit;

my $dbh = DBI->connect(
  "DBI:ODBC:srazphx12_devr1_DWCORE",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

$dbh->do(qq{
  set CURSOR_CLOSE_ON_COMMIT off
  set ANSI_NULL_DFLT_ON on
  set ANSI_NULLS on
  set ANSI_WARNINGS on
  set CONCAT_NULL_YIELDS_NULL on
  set QUOTED_IDENTIFIER on
});

my $filter = '';
warn "cmpOnly=$cmpOnly\n";
$filter .= " AND statuscode=''CMP'' "       if length($cmpOnly);
$filter .= " AND createdate >= ''$since'' " if length($since);
$filter .= " AND cuco = ''  $div'' "        if length($div);

warn "filter=$filter\n";

# Find completed Automated Account Entry requests
my $sth = $dbh->prepare(qq{
  SELECT * FROM OPENQUERY(infopro,
  'SELECT RMSQUTID   AS quote_number,
	  SRCINID    AS bs_id,
	  CUCO       AS infopro_div_nbr,
	  CUCUNO     AS acct_nbr,
          STATUSCODE,
          CREATEDATE,
          CREATETIME
     FROM allied.cufile.CUPAASAC
    WHERE 1=1 $filter
    ORDER
       BY createdate DESC, createtime DESC
   FOR FETCH ONLY WITH UR'
  )
});

$sth->execute;

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('Quote Number','BS_ID','InfoPro Div','Account','Status','Create Dt','Create Time');
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
