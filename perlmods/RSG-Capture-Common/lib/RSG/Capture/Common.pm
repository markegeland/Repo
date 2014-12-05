package RSG::Capture::Common;

use 5.018002;
use strict;
use warnings;

require Exporter;

our @ISA = qw(Exporter);

# Items to export into callers namespace by default. Note: do not export
# names by default without a very good reason. Use EXPORT_OK instead.
# Do not simply export all your public functions/methods/constants.

# This allows declaration	use RSG::Capture::Common ':all';
# If you do not need this, moving things directly into @EXPORT or @EXPORT_OK
# will save memory.
our %EXPORT_TAGS = ( 'all' => [ qw(
	
) ] );

our @EXPORT_OK = ( @{ $EXPORT_TAGS{'all'} } );

our @EXPORT = qw(
	
);

our $VERSION = '0.01';


sub new {
  my $class = shift;
  my $self = {};
  bless $self, $class;
  $self->_initialize();
  return $self;
}

sub _initialize {
}

sub getGridTableCss {
  my $self = shift;

  return qq[
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
  ];
}

sub getCapturegetSessionIdMessageTemplate {
  my $self = shift;

  return q(<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
      <soapenv:Header>
	<bm:category xmlns:bm="urn:soap.bigmachines.com">Security</bm:category>
	<bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
	  <bm:schemaLocation>[% captureBase %]/bmfsweb/republicservices/schema/v1_0/security/Security.xsd</bm:schemaLocation>
	</bm:xsdInfo>
      </soapenv:Header>
      <soapenv:Body>
	<bm:login xmlns:bm="urn:soap.bigmachines.com">
	  <bm:userInfo>
	    <bm:username>[% username %]</bm:username>
	    <bm:password>[% password %]</bm:password>
	    <bm:sessionCurrency/>
	  </bm:userInfo>
	</bm:login>
      </soapenv:Body>
    </soapenv:Envelope>
  );
}

sub getCapturegetUserMessageTemplate {
  my $self = shift;

  return q(<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
      <soapenv:Header>
	<bm:userInfo xmlns:bm="urn:soap.bigmachines.com">
	  <bm:sessionId>[% sessionId %]</bm:sessionId>
	</bm:userInfo>
	<bm:category xmlns:bm="urn:soap.bigmachines.com">Users</bm:category>
	<bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
	  <bm:schemaLocation>[% captureBase %]/bmfsweb/republicservices/schema/v1_0/users/Users.xsd</bm:schemaLocation>
	</bm:xsdInfo>
      </soapenv:Header>
      <soapenv:Body>
	<bm:getUser xmlns:bm="urn:soap.bigmachines.com">
	  <bm:userInfo>
	    <bm:login>[% login %]</bm:login>
	  </bm:userInfo>
	</bm:getUser>
      </soapenv:Body>
    </soapenv:Envelope>
  );

}


sub getCapturegetHierarchyExceptionsMessageTemplate {
  my $self = shift;

  return q(<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
      <soapenv:Header>
        <bm:userInfo xmlns:bm="urn:soap.bigmachines.com">
          <bm:sessionId>[% sessionId %]</bm:sessionId>
        </bm:userInfo>
        <bm:category xmlns:bm="urn:soap.bigmachines.com">Data Tables</bm:category>
        <bm:xsdInfo xmlns:bm="urn:soap.bigmachines.com">
          <bm:schemaLocation>[% captureBase %]/bmfsweb/republicservices/schema/v1_0/datatables/Hierarchy_Exceptions.xsd</bm:schemaLocation>
        </bm:xsdInfo>
      </soapenv:Header>
      <soapenv:Body>
        <bm:get xmlns:bm="urn:soap.bigmachines.com">
          <bm:DataTables bm:table_name="Hierarchy_Exceptions">
            <bm:criteria>
              <bm:field>[% fieldName %]</bm:field>
              <bm:value>[% login %]</bm:value>
              <bm:comparator>=</bm:comparator>
            </bm:criteria>
          </bm:DataTables>
        </bm:get>
      </soapenv:Body>
    </soapenv:Envelope>
  );
}

# Preloaded methods go here.

1;
__END__
# Below is stub documentation for your module. You'd better edit it!

=head1 NAME

RSG::Capture::Common - Perl module housing common utilities for the Capture project

=head1 SYNOPSIS

  use RSG::Capture::Common;

  my $cc = RSG::Capture::Common->new();

  my $css = $cc->getGridTableCss();

=head1 DESCRIPTION

This module houses common utility subroutines to be used for the Capture project.

=head2 EXPORT

None by default.



=head1 SEE ALSO

=head1 AUTHOR

Doug Bloebaum, E<lt>dbloebaum@sysev.com<gt>

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2014 by Doug Bloebaum

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl itself, either Perl version 5.18.2 or,
at your option, any later version of Perl 5 you may have available.

=cut
