# john jozwiak on 2020 Jun 20

all:	
	( cd go/detectclones ; make )
	-@echo
	-@/bin/ls
	-@echo

test:	
	( cd go/detectclones ; make test )
	-@echo
	-@/bin/ls
	-@echo

clean:	
	( cd go/detectclones ; make clean )
	-@echo
	-@/bin/ls
	-@echo
