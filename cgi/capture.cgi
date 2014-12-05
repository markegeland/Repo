#!/usr/bin/perl -w

use strict;
use LWP::UserAgent;
use XML::Twig;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use HTML::Table;
use Template;

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $q = CGI->new;
my $sessionId;
my $captureBase = 'https://testrepublicservices.bigmachines.com';
$captureBase = 'https://republicservices.bigmachines.com';

print
    $q->header,
    $q->start_html(
      '-title' =>'Capture User Lookup',
      '-style' => {'-code' => $css}
    ),
    $q->h1('Capture User Lookup'),
    $q->h3($captureBase),
    '<p>This utility uses Web Service calls against Capture to print information about a given Capture login',
    $q->start_form,
    "User: ", $q->textfield('username'), $q->p,
    "Password: ", $q->password_field('password'), $q->p,
    "Lookup User: ", $q->textfield('lookup'), $q->p,
    $q->submit,
    $q->end_form,
    $q->hr,"\n";

my $username = $q->param('username');
my $password = $q->param('password');
my $lookup   = $q->param('lookup');

exit unless $username && $password && $lookup;

my $tt = Template->new;

my $templateText = $cc->getCapturegetSessionIdMessageTemplate();
my $loginMessage;
  
$tt->process(
  \$templateText,
  {
    captureBase => $captureBase,
    username    => $username,
    password    => $password,
  },
  \$loginMessage
);

my $soapUrl="$captureBase/v1_0/receiver";

my $ua = LWP::UserAgent->new;

my $response = $ua->post(
  $soapUrl,
  Content_Type => 'text/xml;charset=utf-8',
  SOAPAction => 'urn:soap.bigmachines.com#login',
  Content => $loginMessage
);

die $response->status_line unless $response->is_success;

my $twig = XML::Twig->new();
$twig->set_pretty_print('indented');
$twig->parse($response->decoded_content);

$sessionId = $twig->first_elt('bm:sessionId')->text;

$templateText = $cc->getCapturegetUserMessageTemplate();
my $getUserMessage;
  
$tt->process(
  \$templateText,
  {
    sessionId   => $sessionId,
    captureBase => $captureBase,
    login       => $lookup,
  },
  \$getUserMessage
);

$response = $ua->post(
  $soapUrl,
  Content_Type => 'text/xml;charset=utf-8',
  SOAPAction => 'urn:soap.bigmachines.com#getUser',
  Content => $getUserMessage
);

if ($response->is_success) {
  $twig->parse($response->decoded_content);
  my $bm_userInfo = $twig->first_elt('bm:userInfo');
  my @row;

  push(@row, $bm_userInfo->first_child_text('bm:login'));
  push(@row, $bm_userInfo->first_child_text('bm:status'));
  push(@row, $bm_userInfo->first_child_text('bm:type'));
  push(@row, $bm_userInfo->first_child_text('bm:first_name'));
  push(@row, $bm_userInfo->first_child_text('bm:last_name'));
  push(@row, $bm_userInfo->first_child_text('bm:job_title'));
  push(@row, $bm_userInfo->first_child_text('bm:email'));
  push(@row, $bm_userInfo->first_child_text('bm:super_user_access_perm'));
  push(@row, $bm_userInfo->first_child_text('bm:approval_delegate'));

  my @groups;
  foreach my $group ($bm_userInfo->first_child('bm:group_list')->children) {
    push (@groups, $group->first_child_text('bm:variable_name'));
  }
  my $groupCount = @groups;

  # If user has more than 100 groups, cap the display at the first 100
  my $groupStringEnd = '';
  if ($groupCount > 100) {
    @groups = @groups[0 .. 99];
    $groupStringEnd = " + " . ($groupCount-100) . " more...";
  }
  
  my $groupString = "$groupCount: " . join(' ', @groups) . $groupStringEnd;

  push(@row, $groupString);

  my $table = HTML::Table->new;
  $table->setClass('gridtable');

  $table->addRow('Login','Status','Type','First','Last','Job Title','Email','Super User?','Approval Delegate','Groups');
  $table->setRowHead(-1);

  $table->addRow(@row);
  print $table->getTable;

}
else {
  print "<p>Login '$lookup' not found\n";
}  
print $q->end_html;
