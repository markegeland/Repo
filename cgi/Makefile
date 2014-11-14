SHELL = /bin/sh

TARGET  = $(shell echo *.cgi)

PREFIX = $(DESTDIR)/usr/lib
BINDIR = $(PREFIX)/cgi-bin/capture


all: $(TARGET)

install: $(TARGET)
	install --compare --group=capture --mode=775 -D $(TARGET) $(BINDIR)

uninstall:
	-rm $(BINDIR)/$(TARGET)


distclean: clean
	-rm -f $(TARGET)

.PHONY : all \
	install install-strip uninstall clean distclean
