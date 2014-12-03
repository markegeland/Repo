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

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $q = CGI->new();

if (defined($q->param('Download CSV'))) {
  print $q->header(
    -type    => 'text/csv',
    -charset => 'iso-8859-1',
    -attachment => 'githubissues.csv',
  );
}
else {
  print $q->header;
  print $q->start_html(
    '-title' =>'Get Capture GitHub Issues',
    '-style' => {'-code' => $css}
  );
  print $q->h1('Get Capture GitHub Issues');
  print $q->start_form;
  print $q->submit(
    '-name' => 'View as Table',
    '-value' => 'View as Table',
  );
  print $q->submit(
    '-name' => 'Download CSV',
    '-value' => 'Download CSV',
  );
  print $q->end_form;
}

my $view_as_table = $q->param('View as Table');
my $download_csv  = $q->param('Download CSV');

exit unless $view_as_table or $download_csv;

my $github = Net::GitHub->new(
  version => 3,
  login => 'blabes',
  access_token => '60c251c33994594a22836be03cb9767fe23cf6bd',
);


my @issues = $github->issue->repos_issues(undef,'RepublicServicesRepository/Capture',{state=>'all'});
while($github->issue->has_next_page) {
  push @issues, $github->issue->next_page;
}

#print STDERR Data::Dumper::Dumper(\@issues);

my $table = HTML::Table->new;
$table->setClass('gridtable');

my $csv = Text::CSV_XS->new({
  'eol'          => $/,
  'binary'       => 1,
  'auto_diag'    => 2,
  'diag_verbose' => 2,
});

my @header = ('Number','User','Title','Last Updated','Assignee','Body','State','Closed At','Milestone','Labels');
my @rows;
push(@rows, \@header);

$table->addRow(@header);
$table->setRowHead(-1);

foreach my $issue (sort {$a->{number} <=> $b->{number}} @issues) {
  my @labelArray;
  foreach my $labelHash (@{$issue->{labels}}) {
    push(@labelArray, $labelHash->{name})
  }
  $issue->{body} =~ s&\!\[image\]\((.*)\)&<a href="$1">image</a>&g;
  $issue->{updated_at} =~ s/[TZ]/ /g;
  $issue->{closed_at} =~ s/[TZ]/ /g;
  my @row = ($issue->{number},
	     $issue->{user}->{login},
	     $issue->{title},
	     $issue->{updated_at},
	     $issue->{assignee}->{login},
	     $issue->{body},
	     $issue->{state},
	     $issue->{closed_at},
	     $issue->{milestone}->{title},
	     join(' ',@labelArray));

  $table->addRow(@row);
  push(@rows, \@row);
}

if ($view_as_table) {
  print $table->getTable;
  print $q->end_html;
}
else {
  foreach my $row (@rows) {
    $csv->print(*STDOUT, $row);
  }
}
