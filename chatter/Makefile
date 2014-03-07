# $Id: Makefile,v 1.1 2013-08-08 14:10:22-07 - - $

JAVASRC      = chatter.java client.java server.java
SOURCES      = ${JAVASRC} Makefile README
MAINCLASS    = chatter
CLASSES      = ${JAVASRC:.java=.class}
INNERCLASSES = chatter\$$options.class \
               client\$$receiver.class client\$$sender.class \
               server\$$queue_manager.class \
               server\$$receiver.class server\$$sender.class
JARCLASSES   = ${CLASSES} ${INNERCLASSES}
JARFILE      = chatter
LISTING      = Listing.ps

all : ${JARFILE}

${JARFILE} : ${CLASSES}
	echo Main-class: ${MAINCLASS} >Manifest
	jar cvfm ${JARFILE} Manifest ${JARCLASSES}
	- rm Manifest
	chmod +x ${JARFILE}

%.class : %.java
	javac $<

lis : ${SOURCES} all
	mkpspdf ${LISTING} ${SOURCES}

clean :
	- rm ${JARCLASSES}

spotless : clean
	- rm ${JARFILE}

ci : ${SOURCES}
	checksource ${SOURCES}
	cid + ${SOURCES}

submit : ${SOURCES}
	submit cmps109-wm.u13 asg5 ${SOURCES}

again :
	${MAKE} spotless ci all lis

	
