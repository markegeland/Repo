#!/usr/bin/perl -w
#
# Display the lawson_division table

use strict;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $tableDesc = 'Lawson Division';
my $tableName = 'lawson_division';

my $q = CGI->new();

print $q->header;
print $q->start_html(
  '-title' => $tableDesc,
  '-style' => {'-code' => $css}
);
print $q->h1($tableDesc);

my $dbh = DBI->connect(
  "DBI:ODBC:prodbmisql01_BMIDM",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

# Find dim_corp_hier info for this division
my $sth = $dbh->prepare(qq{
  SELECT division,
         division_nm,
         support_phone,
         is_bmi_active
    FROM lawson_division
   WHERE GETDATE() BETWEEN eff_dt and disc_dt
   ORDER
      BY is_bmi_active desc,
         division
});

$sth->execute;

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('Division','Division Name','Support Phone','Is Capture Active?');
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
  print "No rows found in $tableName"
}

print $q->end_html;
