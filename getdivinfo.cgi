#!/usr/bin/perl -w
#
# Given an InfoPro division, return its place in the corp hierarchy

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
  '-title' =>'InfoPro/Lawson Division Information',
  '-style' => {'-code' => $css}
);
print $q->h1('InfoPro/Lawson Division Information');
print $q->start_form;
print $q->textfield('div');
print $q->submit('submit');
print $q->end_form;


my $div = $q->param('div');
my $submit = $q->param('submit');

exit unless $div and $submit;

my $predicate;
if (length($div) == 4) {
  $predicate = " h.Cur_Div_Nbr=? ";
}
else {
  $div = sprintf("%0.3d", $div);
  $predicate = " h.Cur_Infopro_Div_Nbr=? ";
}


my $dbh = DBI->connect(
  "DBI:ODBC:prodbirpt_DWCORE",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

# Find dim_corp_hier info for this division
my $sth = $dbh->prepare(qq{
  SELECT DISTINCT
         h.Cur_Infopro_Div_Nbr,
         did.Infopro_Reg,
         h.cur_Region_Nbr + ' - ' + region_nm,
         h.Cur_Area_Nbr + ' - ' + Area_Nm,
         h.Cur_BU_Nbr + ' - ' + BU_Desc,
         h.Cur_Div_Nbr
    FROM Dim_Corp_Hier h
         INNER JOIN Dim_Infopro_Div did
            ON h.Cur_Infopro_Div_Nbr=did.Infopro_Div_Nbr
           AND did.is_Current=1
   WHERE $predicate
     AND h.Rev_Distrib_Cd!=''
     AND h.is_Current=1

});

$sth->execute($div);

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('IFP Div','IFP Reg','Region','Area','BU','Lawson Div');
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
  print "No division info found for $div"
}

print $q->end_html;
