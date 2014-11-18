#!/usr/bin/perl -w
#
# Display the row in the InfoPro table allied.cufile.rs513wf@a for a given eeuid
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

print $q->header;
print $q->start_html(
  '-title' => 'InfoPro RS513 Query',
  '-style' => {'-code' => $css}
);
print $q->h1('InfoPro RS513 Query');
print $q->start_form;
print $q->p('Enter EEUID:');
print $q->textfield('eeuid');
print $q->submit('submit');
print $q->end_form;

my $submit = $q->param('submit');
my $eeuid = $q->param('eeuid');

my $linkedServer = 'infopro';

exit unless $submit;

$eeuid = uc $eeuid;

# look for a table description on our specific library.file
my $sql = qq{
SELECT * FROM OPENQUERY($linkedServer,
 'SELECT eeen    AS een,
         eel01   AS Region,
         eel02   AS Area,
         eel03   AS BU,
         eeprcl  AS Division,
         eelnm   AS Last,
         eefnm   AS First,
         eejbcd  AS Job_Code,
         eettl   AS Job_Title,
         eeuid   AS Login,
         eeemail AS Email,
         eedoh   AS Hire_Date,
         eeted   AS Term_Date,
         eesta   AS Status
    FROM cufile.rs513wf\@a
   WHERE eeuid=''$eeuid''
   FOR FETCH ONLY WITH UR')
};

my $sth = $dbh->prepare($sql);

my $table = DBIx::XHTML_Table->new($dbh);

$table->exec_query($sth);
$table->modify(table => { class => 'gridtable' });
print $q->hr;
print $table->output();
