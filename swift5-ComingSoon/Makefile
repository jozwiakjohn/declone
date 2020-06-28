# john jozwiak on 2020 Jun 27

PathDigest.swift PathDigest_test.swift PathNormalization_test.swift SetOfStrings.swift SetOfStrings_test.swift main.swift

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


detectclones:	detectclones.swift  #      src/uniqueWords/tokenize.swift
	-@mkdir -p built
	swiftc  detectclones.swift -o built/detectclones

test:	detectclones
	built/detectclones ../testFolder
