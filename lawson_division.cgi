#!/usr/bin/perl -w
#
# Display the lawson_division table

use strict;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;

my $tableDesc = 'Lawson Division';
my $tableName = 'lawson_division';

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
