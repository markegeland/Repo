#!/usr/bin/perl -w
#
# Display the job_code_mapping table

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
  '-title' =>'Job Code Mapping',
  '-style' => {'-code' => $css}
);
print $q->h1('Job Code Mapping');
print $q->h2('Employees with these job codes are automatically added to Capture');

my $dbh = DBI->connect(
  "DBI:ODBC:prodbmisql01_BMIDM",
  'ReadOnly',
  'to*B3ws#d8'
) or die("DBI Connect Error - $DBI::errstr\n");

# Find dim_corp_hier info for this division
my $sth = $dbh->prepare(qq{
  SELECT job_code,
         job_desc,
         bmi_profile
    FROM job_code_mapping
   WHERE GETDATE() BETWEEN eff_dt and disc_dt
   ORDER
      BY job_code
});

$sth->execute;

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('Job Code','Job Description','BMI Profile');
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
  print "No rows found in job_code_mapping"
}

print $q->end_html;
