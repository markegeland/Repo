#!/usr/bin/perl -w
#
# Given an InfoPro division, return its place in the corp hierarchy
# TODO: adjust timestamps from GMT to local

use strict;
use DBI;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;
use Net::GitHub;
use Data::Dumper;
use Text::CSV_XS;
use DateTime::Format::Strptime;
use Excel::Writer::XLSX;

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $q = CGI->new();

if (defined($q->param('Download CSV'))) {
  print $q->header(
    '-type'       => 'text/csv',
    '-charset'    => 'iso-8859-1',
    '-attachment' => 'githubissues.csv',
  );
}
elsif (defined($q->param('Download XLSX'))) {
  print $q->header(
    '-type'       => 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    '-attachment' => 'githubissues.xlsx',
  );
}
else {
  print $q->header,
        $q->start_html(
	  '-title' =>'Get Capture GitHub Issues',
	  '-style' => {'-code' => $css}
	),
        $q->h1('Get Capture GitHub Issues'),
        $q->start_form,
        $q->submit(
	  '-name' => 'View as Table',
	  '-value' => 'View as Table',
        ),
        $q->submit(
	  '-name' => 'Download CSV',
	  '-value' => 'Download CSV',
        ),
        $q->submit(
	  '-name' => 'Download XLSX',
	  '-value' => 'Download XLSX',
        ),
	$q->end_form,
	$q->p;
}

my $view_as_table = $q->param('View as Table');
my $download_csv  = $q->param('Download CSV');
my $download_xlsx = $q->param('Download XLSX');

exit unless $view_as_table or $download_csv or $download_xlsx;

my $github = Net::GitHub->new(
  version => 3,
  login => 'blabes',
  access_token => '60c251c33994594a22836be03cb9767fe23cf6bd',
);

my @issues = $github->issue->repos_issues(undef,'RepublicServicesRepository/Capture',{state=>'all'});
while($github->issue->has_next_page) {
  push @issues, $github->issue->next_page;
}

my @labels = $github->issue->all_labels('RepublicServicesRepository/Capture');
while($github->issue->has_next_page) {
  push @labels, $github->issue->next_page;
}

my @allLabelNames;
foreach my $labelHash (@labels) {
  push (@allLabelNames, $labelHash->{name});
}
s/\A(\d)\Z/oow:$1/ for @allLabelNames;
@allLabelNames = sort(@allLabelNames);


print STDERR Data::Dumper::Dumper(\@issues);
#print STDERR Data::Dumper::Dumper(\@labels);

my $table = HTML::Table->new;
$table->setClass('gridtable');

my $csv = Text::CSV_XS->new({
  'eol'          => $/,
  'binary'       => 1,
  'auto_diag'    => 2,
  'diag_verbose' => 2,
});

my @header = (
  'Number',
  'User',
  'Title',
  'Last Updated',
  'Current Assignee',
  'Body',
  'State',
  'Closed At',
  'Milestone',
  'First Assignee',
  'Labels',
  @allLabelNames
);

my @rows;
push(@rows, \@header);

$table->addRow(@header);
$table->setRowHead(-1);

# timestamp format from GitHub is: 2014-11-24T19:23:06Z
our $strpIn = DateTime::Format::Strptime->new(
  pattern => '%Y-%m-%dT%H:%M:%SZ',
  locale  => 'en_US',
  time_zone => 'UTC',
);

foreach my $issue (sort {$a->{number} <=> $b->{number}} @issues) {
  # make an array of all label names applied to this issue
  my @labelArray;
  foreach my $labelHash (@{$issue->{labels}}) {
    push(@labelArray, $labelHash->{name});
  }

  # call the "events" API to get all events associated with this issue
  # https://api.github.com/repos/RepublicServicesRepository/Capture/issues/255/events
  my @events = $github->issue->event(undef,'RepublicServicesRepository/Capture',$issue->{number});
  while($github->issue->has_next_page) {
    push @events, $github->issue->next_page;
  }
  
  my $firstAssignee;
  foreach my $event (sort {$a->{created_at} cmp $b->{created_at}} @events) {
    next unless $event->{event} eq 'assigned';
    $firstAssignee = $event->{assignee}->{login};
    print STDERR "firstAssignee=$firstAssignee\n";
    last;
  }

  # change any image references to HTML format
  $issue->{body} =~ s&\!\[image\]\((.*)\)&<a href="$1">image</a>&g;

  # change timestamps to local timezone from UTC and format for Excel
  $issue->{updated_at} = fixTimestamp($issue->{updated_at});
  $issue->{closed_at}  = fixTimestamp($issue->{closed_at});

  # build an array of fields we want to output
  my @row = ($issue->{number},
	     $issue->{user}->{login},
	     $issue->{title},
	     $issue->{updated_at},
	     $issue->{assignee}->{login},
	     $issue->{body},
	     $issue->{state},
	     $issue->{closed_at},
	     $issue->{milestone}->{title},
	     $firstAssignee,
	     join(' ',sort(@labelArray)));

  foreach my $label (@allLabelNames) {
    push(@row, scalar(grep(/\A$label\Z/, @labelArray)));
  }

  $table->addRow(@row);
  push(@rows, \@row);
}

if ($view_as_table) {
  print $table->getTable;
  print $q->end_html;
}
elsif ($download_csv) {
  foreach my $row (@rows) {
    $csv->print(*STDOUT, $row);
  }
}
elsif ($download_xlsx) {
  my $workbook = Excel::Writer::XLSX->new(\*STDOUT);
  my $worksheet = $workbook->add_worksheet();

  my $format_wrap  = $workbook->add_format();
  my $format_align = $workbook->add_format();
  $format_wrap->set_text_wrap();
  $format_wrap->set_align('left');
  $format_wrap->set_align('top');

  $format_align->set_align('left');
  $format_align->set_align('top');

  $worksheet->freeze_panes(1,0);
  $worksheet->autofilter(0,0,0,scalar(@header)-1);

  $worksheet->set_column('A:A',  10, $format_align); # Number
  $worksheet->set_column('B:B',  10, $format_align); # User
  $worksheet->set_column('C:C',  50, $format_wrap);  # Title
  $worksheet->set_column('D:D',  18, $format_align); # Last Updated
  $worksheet->set_column('E:E',  16, $format_align); # Assignee
  $worksheet->set_column('F:F',  60, $format_wrap);  # Body
  $worksheet->set_column('G:G',   8, $format_align); # State
  $worksheet->set_column('H:H',  18, $format_align); # Closed At
  $worksheet->set_column('I:I',  30, $format_align); # Milestone
  $worksheet->set_column('J:J',  18, $format_wrap);  # First Assignee
  $worksheet->set_column('K:K',  20, $format_wrap);  # Labels

  $worksheet->set_column('L:AY', 21, $format_align); # Label bit columns
  
  $worksheet->write_col('A1',\@rows);
  $workbook->close();
}
else {
  warn "Um, something went wrong; no SUBMIT was pressed!\n";
}

## 
## Subroutines
##

sub fixTimestamp {
  my $ts = shift;
  return '' unless defined $ts;

  my $dt = $strpIn->parse_datetime($ts);
  $dt->set_time_zone('America/Phoenix');
  return $dt->strftime('%Y-%m-%d %H:%M:%S');
}

