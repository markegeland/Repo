To build modules so that they install into the Capture CGI area:

  perl Makefile.PL PREFIX=/usr/lib/cgi-bin/capture/perlmods (one time)
  make test
  make install
