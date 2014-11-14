#!/usr/bin/perl -w

print "Content-Type: text/html\n\n";
print "<html><head><title>Environment Dump</title></head>\n";
print "<body>\n";
print "<h1>Environment Dump</h1>\n";
print "<ul>\n";

foreach my $var (keys %ENV) {
  print "<li>$var: $ENV{$var}</li>\n";
}
print "</ul></body></html>\n";
