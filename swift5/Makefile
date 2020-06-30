# john jozwiak on 2020 Jun 27

all:	detectclones # xcodebuild -alltargets -quiet
	-@mkdir -p built
	-@/bin/echo
	-@/bin/ls -F
	-@/bin/echo

clean:	
	-@/bin/rm -fr built
	-@make showfiles

showfiles:	
	-@echo
	-@/bin/ls
	-@echo


detectclones:	main.swift  PathDigest.swift
	-@mkdir -p built
	swiftc  *.swift -o built/detectclones

test:	detectclones
	built/detectclones ../testFolder
