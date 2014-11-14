#!/usr/bin/perl -w

use strict;
use LWP::UserAgent;
use XML::Twig;
use CGI;
use CGI::Carp qw(fatalsToBrowser);
use DBI;
use Text::CSV_XS;
use HTML::Table;


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
  'ReadOnly',
  'to*B3ws#d8'
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

exit unless $username && $password && $since;

$since =~ s/-//g;		# turn yyyy-mm-dd into yyyymmdd
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
our $ua = LWP::UserAgent->new;

# instantiate an XML::Twig object to parse the SOAP responses
our $twig = XML::Twig->new();

# get a sessionId to be used on future Capture SOAP calls
my $sessionId = getCaptureSessionId($username, $password);
warn "sessionId=$sessionId\n";

my $userCount = scalar(keys %termDate);
print "<p>Checking $userCount terminated users\n";

my $i=0;
my @problemUsers;
foreach my $eeuid (sort keys %termDate) {
  my ($status,$bm_login) = getCaptureUserStatus($eeuid, $sessionId);

  $i++;
  my $pct = $i / $userCount * 100;
  printf("%5.2f%%", $pct) if (($i % 10) == 0);
  print ".";

  if ($status eq 'Active') {
    push(@problemUsers, $bm_login);
    print "<p>User $bm_login, terminated on $termDate{$bm_login} shows as Active in Capture!<br>";
  }
}
printf("%5.2f%%", 100);

my $table = HTML::Table->new;
$table->setClass('gridtable');

$table->addRow('Login','Termination Date','Capture Status','In hierarchy_exceptions?');
$table->setRowHead(-1);

foreach my $bm_login (@problemUsers) {
  # check as-is case, upper case, and lower case versions of the login against hierarchy_exceptions
  my $he_record_count=0;

  foreach my $login ($bm_login, uc $bm_login, lc $bm_login) {
    foreach my $fieldName ('User_Login','Level_1_Approver','Level_2_Approver','Level_3_Approver') {
      $he_record_count += getHierarchyExceptionsRow($login,$sessionId,$fieldName);
    }
  }
  
  warn "he_record_count=$he_record_count\n";
  $table->addRow($bm_login, $termDate{$bm_login}, 'Active', $he_record_count ? 'Yes' : 'No');
}

print $table->getTable;

print $q->h3('Done.');

print $q->end_html;

##
## Subroutines
##

sub getCaptureUserStatus {
  my $login = shift or die;
  my $sessionId = shift or die;

  getCaptureUserStatusViaSOAP($login, $sessionId);
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
  my $bm_login;
  if ($response->is_success) {
    $twig->parse($response->decoded_content);
    $status   = $twig->first_elt('bm:userInfo')->first_child_text('bm:status');
    $bm_login = $twig->first_elt('bm:userInfo')->first_child_text('bm:login');
    warn "status=$status bm_login=$bm_login\n";
  }
  else {
    ($status, $bm_login) = ('Unknown','Unknown');
  }

  return ($status, $bm_login);
}

sub getCaptureSessionId {
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

sub getHierarchyExceptionsRow {
  my $login     = shift or die;
  my $sessionId = shift or die;
  my $fieldName = shift or die;

  my $getHierarchyExceptionsRowMessage = qq{<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
      <soapenv:Header>
        <bm:userInfo xmlns:bm="urn:soap.bigmachines.com">
          <bm:sessionId>$sessionId</bm:sessionId>
        </bm:userInfo>
        <bm:category xmlns:bm="urn:soap.bigmachines.com">Data Tables</bm:category>
        <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
          <bm:schemaLocation>$captureBase/bmfsweb/republicservices/schema/v1_0/datatables/Hierarchy_Exceptions.xsd</bm:schemaLocation>
        </bm:xsdInfo>
      </soapenv:Header>
      <soapenv:Body>
        <bm:get xmlns:bm="urn:soap.bigmachines.com">
          <bm:DataTables bm:table_name="Hierarchy_Exceptions">
            <bm:criteria>
              <bm:field>$fieldName</bm:field>
              <bm:value>$login</bm:value>
              <bm:comparator>=</bm:comparator>
            </bm:criteria>
          </bm:DataTables>
        </bm:get>
      </soapenv:Body>
    </soapenv:Envelope>
  };

  my $soapUrl="$captureBase/v1_0/receiver";

  my $response = $ua->post(
    $soapUrl,
    Content_Type => 'text/xml;charset=utf-8',
    SOAPAction => 'urn:soap.bigmachines.com#get',
    Content => $getHierarchyExceptionsRowMessage
  );

  my $records_returned;
  if ($response->is_success) {
    warn $response->decoded_content, "\n";
    $twig->parse($response->decoded_content);
    $records_returned = $twig->first_elt('bm:records_returned')->text;
  }

  return $records_returned;
}
