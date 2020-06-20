# john jozwiak on 2020 Jun 20

all:	
	( cd go/declone ; make )
	-@echo
	-@/bin/ls
	-@echo

test:	
	( cd go/declone ; make test )
	-@echo
	-@/bin/ls
	-@echo

clean:	
	( cd go/declone ; make clean )
	-@echo
	-@/bin/ls
	-@echo
