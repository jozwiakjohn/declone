# john jozwiak on 2020 Jun 23

all:	DetectClones.class 
	-@echo
	-@/bin/ls
	-@echo

DetectClones.class:	DetectClones.java
	javac DetectClones.java

test:	DetectClones.class
	java -Xms100m -Xmx1g DetectClones ../testFolder

clean:	
	-@/bin/rm fr core *.class
	-@echo
	-@/bin/ls
	-@echo
