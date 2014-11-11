#!/usr/bin/perl -w
#
# Do a "DESCRIBE" on an AS/400 library.file
#

use strict;
use Getopt::Long;
use Data::Dumper;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);

my $q = CGI->new;

my %opt;
$opt{prod} = 1;
$opt{dev} = 0;

die "$0: Can't set both -prod and -dev\n" if $opt{dev} and $opt{prod};

my $dbh = DBI->connect(
  "DBI:ODBC:srazphx12_devr1_DWCORE",
  'DWCORE',
  ''
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
  '-title' =>'iSeries Table Information'
);
print $q->h1('iSeries Table Information');
print $q->start_form;
print $q->p('Enter iSeries library (ex: CUFILE, BIDBFM, ARDBFM, BIDBFM999, LAWAPP9DBP) and file:');
print $q->popup_menu('system', ['InfoPro','Lawson']);
print ' ';
print $q->textfield('library','BIDBFM999');
print ' . ';
print $q->textfield('file','BIPSQ');
print ' ';
print $q->submit('submit');
print $q->end_form;

my $library = uc $q->param('library');
my $file = uc $q->param('file');
my $submit = $q->param('submit');
my $system = $q->param('system');

my $linkedServer = ($system eq 'InfoPro') ? 'infopro' : 'dev_lawson';


exit unless $library and $file and $submit;

#warn "library=$library\nfile=$file\n";

# look for a table description on our specific library.file
my $tabSql = qq{
SELECT * FROM OPENQUERY($linkedServer,
 'SELECT table_text
    FROM qsys2.systables
   WHERE table_text != ''''
     AND system_table_schema=''$library''
     AND system_table_name=''$file''
   FETCH FIRST ROW ONLY WITH UR')
};
my $table_text = $dbh->selectrow_array($tabSql);
$table_text = '' unless defined $table_text;

if ($table_text eq '') {
  #warn "No table_text found... trying other libraries\n";
  # get the first non-empty table description for our table in any
  # library; lots of libraries have blank descriptions for a given
  # table, but sometimes one or two will have something helpful
  $tabSql = qq{
  SELECT * FROM OPENQUERY($linkedServer,
   'SELECT table_text
      FROM qsys2.systables
     WHERE table_text != ''''
       AND system_table_name=''$file''
     FETCH FIRST ROW ONLY WITH UR')
  };
  $table_text = $dbh->selectrow_array($tabSql);
  $table_text = '' unless defined $table_text;
}

# get the column details for this table
my $descSql = qq{
SELECT * FROM OPENQUERY($linkedServer,
  'SELECT system_table_name,
          system_column_name,
          TRIM(data_type) || ''('' || length || COALESCE('','' || numeric_scale, '''') || '')'' AS type,
          COALESCE(column_text, column_heading) AS column_text,
          storage,
          is_nullable
    FROM qsys2.syscolumns
   WHERE system_table_schema=''$library''
     AND system_table_name=''$file''
   ORDER
      BY ordinal_position
    WITH UR')
};

my $descSth = $dbh->prepare($descSql);
$descSth->execute;

print "<pre>\n";
print "--\n";
print "-- $table_text\n";
print "--\n";
print "CREATE TABLE $library.$file(\n";
my $table_name;
my $column_name;
my $type;
my $column_text;
my $is_nullable;
my $null_text; 
my $storage;
my $colCount=0;
my $storageTotal=0;
my @cols;
while (($table_name, $column_name, $type, $column_text, $storage, $is_nullable) = $descSth->fetchrow_array) {
  push(@cols, $column_name);
  $colCount++;
  $storageTotal += $storage;
  $column_text = defined $column_text ? $column_text : '';
  $null_text = $is_nullable eq 'Y' ? 'NULL,' : 'NOT NULL,';
  write;
}
print ")\n";
print "$colCount columns\n";
print "$storageTotal bytes/row\n";
print"</pre>";

print join("\t",@cols), "\n" if $opt{coldump};

format STDOUT=
    @<<<<<<<<<<<<<<<<<<<<<<< @<<<<<<<<<<<<<<<<<<<<<< @<<<<<<<< -- @<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    $column_name,            $type,                  $null_text,  $column_text
.
