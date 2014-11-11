#!/usr/bin/perl -w

use strict;
use LWP::UserAgent;
use XML::Twig;
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

my $loginMessage = qq{<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Header>
    <bm:category xmlns:bm="urn:soap.bigmachines.com">Security</bm:category>
    <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
      <bm:schemaLocation>$captureBase/bmfsweb/testrepublicservices/schema/v1_0/security/Security.xsd</bm:schemaLocation>
    </bm:xsdInfo>
  </soapenv:Header>
  <soapenv:Body>
    <bm:login xmlns:bm="urn:soap.bigmachines.com">
      <bm:userInfo>
        <bm:username>$username</bm:username>
        <bm:password>$password</bm:password>
        <bm:sessionCurrency/>
      </bm:userInfo>
    </bm:login>
  </soapenv:Body>
</soapenv:Envelope>
};

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

my $getUserMessage = qq{<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Header>
    <bm:userInfo xmlns:bm="urn:soap.bigmachines.com">
      <bm:sessionId>$sessionId</bm:sessionId>
    </bm:userInfo>
    <bm:category xmlns:bm="urn:soap.bigmachines.com">Users</bm:category>
    <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
      <bm:schemaLocation>$captureBase/bmfsweb/testrepublicservices/schema/v1_0/users/Users.xsd</bm:schemaLocation>
    </bm:xsdInfo>
  </soapenv:Header>
  <soapenv:Body>
    <bm:getUser xmlns:bm="urn:soap.bigmachines.com">
      <bm:userInfo>
        <bm:login>$lookup</bm:login>
      </bm:userInfo>
    </bm:getUser>
  </soapenv:Body>
</soapenv:Envelope>
};

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
