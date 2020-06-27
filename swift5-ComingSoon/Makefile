# john jozwiak on 2020 Jun 19

all:	parseOED  uniqueWords  wordsToDefs # xcodebuild -alltargets -quiet
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


parseOED:	src/parseOED/main.swift
	-@mkdir -p built
	swiftc  src/parseOED/main.swift -o built/parseOED

uniqueWords:	src/uniqueWords/main.swift src/uniqueWords/tokenize.swift
	-@mkdir -p built
	swiftc  src/uniqueWords/*.swift -o built/uniqueWords

wordsToDefs:	src/wordsToDefs/main.swift
	-@mkdir -p built
	swiftc  src/wordsToDefs/*.swift -o built/wordsToDefs

test:	all
	built/uniqueWords < data/fourLinesOf.txt

testlong:	all
	xzcat data/gutenberg/HoundBaskervilles.txt.xz | built/uniqueWords

testverbose:	all
	xzcat data/gutenberg/WarAndPeace.txt.xz       | built/uniqueWords
