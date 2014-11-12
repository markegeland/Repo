#!/usr/bin/perl -w

use strict;
use LWP::UserAgent;
use XML::Twig;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use DBI;
use Text::CSV_XS;

use lib '/usr/lib/cgi-bin/capture/perlmods/share/perl';
use RSG::Capture::Common;

$|=1;				# turn off stdout buffering

my $cc = RSG::Capture::Common->new();
my $css = $cc->getGridTableCss();

my $q = CGI->new;

our $captureBase = 'https://republicservices.bigmachines.com';
my ($yy,$mm) = (localtime)[5,4];
my $firstOfMonth = sprintf("%0.4d-%0.2d-%0.2d",1900+$yy, ++$mm, 1);
warn "firstOfMonth=$firstOfMonth\n";

print
    $q->header,
    $q->start_html(
      '-head'   => $q->Link({
	'-rel'  => 'stylesheet',
	'-href' => '//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/smoothness/jquery-ui.css'
      }),
      '-title'  =>'Capture User Check',
      '-style'  => {'-code' => $css},
      '-script' => [
	{'-type' => 'JAVASCRIPT',
	 '-src'  => '//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js'},
	{'-type' => 'JAVASCRIPT',
	 '-src'  => '//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js'},
	{'-type' => 'JAVASCRIPT',
	 '-code' => q<
	   $(function() {
	     $( "#datepicker" ).datepicker({
	       dateFormat: "yy-mm-dd",
	       showButtonPanel: true,
	       changeMonth: true,
	       changeYear: true,
	       minDate: "2014-01-01"
	     });
	   });
	 >}
      ]
    ),
    $q->h1('Capture User Check'),
    $q->h3($captureBase),
    $q->start_form,
    "Your Capture Login: ",    $q->textfield('username'),      $q->p,
    "Your Capture Password: ", $q->password_field('password'), $q->p,
    "Check users terminated since: ",
    $q->textfield(
      '-name'  => 'since',
      '-id'    => 'datepicker',
      '-value' => $firstOfMonth
    ),
    $q->p,
    $q->submit,
    $q->end_form,
    $q->hr;

my $dbh = DBI->connect(
  "DBI:ODBC:srazphx12_devr1_DWCORE",
  'DWCORE',
  'manunited20'
) or die("DBI Connect Error - $DBI::errstr\n");

$dbh->do(qq{
  set CURSOR_CLOSE_ON_COMMIT off
  set ANSI_NULL_DFLT_ON on
  set ANSI_NULLS on
  set ANSI_WARNINGS on
  set CONCAT_NULL_YIELDS_NULL on
  set QUOTED_IDENTIFIER on
});

my $username = $q->param('username');
my $password = $q->param('password');
my $since    = $q->param('since');

exit unless $username && $password;

$since =~ s/-//g;
warn "since=$since\n";

# find logins in the InfoPro RS513 file who have a non-zero termination date
my $sql = qq{
SELECT * FROM OPENQUERY(infopro,
 'SELECT eeuid,
         eeted
    FROM cufile.rs513wf\@a
   WHERE eeuid != ''''
     AND eeted > $since
   FOR FETCH ONLY WITH UR')
};

my $sth = $dbh->prepare($sql);
$sth->execute;

my %termDate;
while (my ($eeuid,$eeted) = $sth->fetchrow_array) {
  $termDate{$eeuid} = $eeted;
}

# instantiate a UserAgent object for our Capture SOAP calls
my $ua = LWP::UserAgent->new;

# instantiate an XML::Twig object to parse the SOAP responses
my $twig = XML::Twig->new();

# get a sessionId to be used on future Capture SOAP calls
my $sessionId = getCaptureSessionId($ua, $twig, $username, $password);
warn "sessionId=$sessionId\n";

my $userCount = scalar(keys %termDate);
print "<p>Checking $userCount terminated users\n";

my $i=0;
foreach my $eeuid (sort keys %termDate) {
  my $status = getCaptureUserStatus($ua, $twig, $eeuid, $sessionId);

  $i++;
  my $pct = $i / $userCount * 100;
  printf("%5.2f%%\r", $pct) if (($i % 10) == 0);
  print ".";

  if ($status eq 'Active') {
    print "<p>User $eeuid, terminated on $termDate{$eeuid} shows as Active in Capture!<br>";
  }
}
printf("%5.2f%%\r", 100);

##
## Subroutines
##

sub getCaptureUserStatus {
  my $ua = shift or die;
  my $twig = shift or die;
  my $login = shift or die;
  my $sessionId = shift or die;

  getCaptureUserStatusViaSOAP($ua, $twig, $login, $sessionId);
  #getCaptureUserStatusViaFile($login);
}

our %status;
our $statusHashLoaded = 0;

sub getCaptureUserStatusViaFile {
  my $login = shift;

  if (!$statusHashLoaded) {
    my $csv = Text::CSV_XS->new;

    my $file = 'CaptureUsers.csv';
    open(my $fh, "<", $file) or die "$0: Can't open $file for input -- $!\n";

    my $row;
    $row = <$fh>;			# disregard the first two header lines
    $row = <$fh>;

    while($row = $csv->getline($fh)) {
      # the user's login is in position 2, status is in position 46
      $status{$row->[2]} = $row->[46];
      warn "login=", $row->[2], " status=", $row->[46], "\n";
    }

    $statusHashLoaded=1;
  }

  return exists($status{$login}) ? $status{$login} : 'Unknown';
  
}

sub getCaptureUserStatusViaSOAP {
  my $ua = shift or die;
  my $twig = shift or die;
  my $login = shift or die;
  my $sessionId = shift or die;
  
  my $getUserMessage = qq{<?xml version="1.0" encoding="UTF-8"?>
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
      <bm:userInfo xmlns:bm="urn:soap.bigmachines.com">
	<bm:sessionId>$sessionId</bm:sessionId>
      </bm:userInfo>
      <bm:category xmlns:bm="urn:soap.bigmachines.com">Users</bm:category>
      <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
	<bm:schemaLocation>$captureBase/bmfsweb/republicservices/schema/v1_0/users/Users.xsd</bm:schemaLocation>
      </bm:xsdInfo>
    </soapenv:Header>
    <soapenv:Body>
      <bm:getUser xmlns:bm="urn:soap.bigmachines.com">
	<bm:userInfo>
	  <bm:login>$login</bm:login>
	</bm:userInfo>
      </bm:getUser>
    </soapenv:Body>
  </soapenv:Envelope>
  };

  my $soapUrl="$captureBase/v1_0/receiver";

  my $response = $ua->post(
    $soapUrl,
    Content_Type => 'text/xml;charset=utf-8',
    SOAPAction => 'urn:soap.bigmachines.com#getUser',
    Content => $getUserMessage
  );

  my $status;
  if ($response->is_success) {
    $twig->parse($response->decoded_content);
    $status = $twig->first_elt('bm:userInfo')->first_child_text('bm:status');
  }
  else {
    $status = 'Unknown';
  }

  return $status;
}

sub getCaptureSessionId {
  my $ua = shift or die;
  my $twig = shift or die;
  my $username = shift or die;
  my $password = shift or die;

  my $loginMessage = qq{<?xml version="1.0" encoding="UTF-8"?>
  <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
      <bm:category xmlns:bm="urn:soap.bigmachines.com">Security</bm:category>
      <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
	<bm:schemaLocation>$captureBase/bmfsweb/republicservices/schema/v1_0/security/Security.xsd</bm:schemaLocation>
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

  my $response = $ua->post(
    $soapUrl,
    Content_Type => 'text/xml;charset=utf-8',
    SOAPAction => 'urn:soap.bigmachines.com#login',
    Content => $loginMessage
  );

  die $response->status_line unless $response->is_success;

  $twig->parse($response->decoded_content);

  return $sessionId = $twig->first_elt('bm:sessionId')->text;

}
