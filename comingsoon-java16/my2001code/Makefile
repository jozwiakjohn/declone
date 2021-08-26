# john jozwiak @ motorola speech labs 20010816.

OFILES = *.class
NUKEM  = core $(OFILES) a.out *exe ${TMP}/*.binary ${TMP}/*.assembly
TMP    = tmp

all:			Converter.class Lexer.class \
			ErrorParsing.class ErrorGeneratingCode.class \
			TwoGigArrayOfint.class VectoR.class \
			Lexicon.class LexiconEnglish.class \
			VirtualMachine.class testabnftogsl.class Wrapper.class

Converter.class:	Converter.java  Lexer.class
			javac Converter.java

Lexer.class:		Lexer.java
			javac Lexer.java

ErrorGeneratingCode.class:	ErrorGeneratingCode.java
			javac ErrorGeneratingCode.java

ErrorParsing.class:	ErrorParsing.java
			javac ErrorParsing.java

Lexicon.class:		Lexicon.java
			javac Lexicon.java

LexiconEnglish.class:	LexiconEnglish.java
			javac LexiconEnglish.java

TwoGigArrayOfint.class:	TwoGigArrayOfint.java
			javac TwoGigArrayOfint.java

VectoR.class:		VectoR.java
			javac VectoR.java

VirtualMachine.class:	VirtualMachine.java
			javac VirtualMachine.java

Wrapper.class:	        Wrapper.java
			javac Wrapper.java

testabnftogsl.class:	testabnftogsl.java
			javac testabnftogsl.java

test:			test0

test0:			Converter.class Lexer.class
			time java -Xms25m -Xmx25m Converter syntaxes/hayiris.gsl       ${TMP}

test1:			Converter.class Lexer.class
			time java -Xms25m -Xmx25m Converter syntaxes/tom.gsl           ${TMP}

test2:			Converter.class Lexer.class
			time java -Xms25m -Xmx25m Converter syntaxes/r2vui_newname.gsl ${TMP}

test3:			Converter.class Lexer.class
			time java -Xms25m -Xmx25m Converter syntaxes/Atis.gsl          ${TMP}

test4:			Converter.class Lexer.class
			time java -Xms25m -Xmx25m Converter syntaxes/Recursion.gsl     ${TMP}

testlexicon:		LexiconEnglish.class Lexicon.class
			time java -Xms25m -Xmx25m LexiconEnglish

testabnftogsl:		all
			time java -Xms16m -Xmx16m testabnftogsl

clean:	
			/bin/rm -fr ${NUKEM}
			pwd
			ls -F
			date

refresh:		clean
			(clear ; date ; pwd ; ls -F)
			make all

instructions:			
			@cat INSTRUCTIONS
