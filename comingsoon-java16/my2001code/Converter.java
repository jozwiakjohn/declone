//////////////////////////////////////////////
//                                          //
// john jozwiak @ motorola july 30&31 2001. //
// john jozwiak @ motorola 20010816         //
//                                          //
//////////////////////////////////////////////

// john jozwiak at motorola april 10 2001.

import java.util.Vector;
import java.io.*;
import java.net.URLEncoder;

// john jozwiak uiuc ccso ews January 18 1999, being reincarnated at motorola april 2001.

abstract class ParseNode
{
  int              type             ,
                   weight           ,
                   semantics        ;

  static final int INCLUSION  =  0  ,
                   DEFINITION =  1  ,
                   ID         =  2  ,
                   CHOICE     =  3  ,
                   SEQUENCE   =  4  ,
                   COUNT      =  5  ,
                   META       =  6  ,
                   LEXICA     =  7  ,
                   ALTERNATE  =  8  ,
                   EXPRESSION =  9  ,
                   COUNTABNF  = 10  ,
                   HEADER     = 11  ,
                   TAG        = 12  ;
  
  abstract String  string();
};


class ParseNodeInclusion  extends ParseNode 
{
  int name,
      where;

  ParseNodeInclusion ( int namen , int wo )
  { type  = ParseNode.INCLUSION ;
    name  = namen;
    where = wo;
  }

  String string() { return "ParseNodeInclusion"; }
}


class ParseNodeDefinition extends ParseNode 
{
  int       name,
            nameextended;
  ParseNode node;
  boolean   visibility;

  ParseNodeDefinition( int n , int scopeorpackageorsomething , ParseNode x , boolean viz )
  { type         = ParseNode.DEFINITION;
    name         = n;
    nameextended = scopeorpackageorsomething;
    node         = x;
    visibility   = viz;
  }
  String string() { return "ParseNodeDefinition"; }
}


class ParseNodeIdentifier extends ParseNode 
{
  int name,
      weight,
      semantics;

  ParseNodeIdentifier( int n , int w , int s )
  { type        = ParseNode.ID        ;
    name        = n;
    weight      = w;
    semantics   = s;
  }
  String string() { return "ParseNodeIdentifier"; }
}


abstract class ParseNodeChoiceOrSequence extends ParseNode
{
  VectoR list;
  int    weight,
         semantics;
}


class ParseNodeChoice     extends ParseNodeChoiceOrSequence
{
  ParseNodeChoice    ( VectoR l , int w , int s )
  { type       = ParseNode.CHOICE    ;
    list       = l;
    weight     = w;
    semantics  = s;
  }
  String string() { return "ParseNodeChoice"; }
}


class ParseNodeSequence   extends ParseNodeChoiceOrSequence
{
  ParseNodeSequence  ( VectoR l , int w , int s )
  { type      = ParseNode.SEQUENCE  ;
    list      = l;
    weight    = w;
    semantics = s;
  }
  String string() { return "ParseNodeSequence"; }
}


class ParseNodeCount      extends ParseNode 
{
  ParseNode node;
  int       op,
            weight,
            semantics;

  ParseNodeCount     ( int o , ParseNode x , int w , int s )
  { type      = ParseNode.COUNT     ;
    op        = o;
    node      = x;
    weight    = w;
    semantics = s;
  }
  String string() { return "ParseNodeCount"; }
}


class ParseNodeMetaBlurb  extends ParseNode 
{
  int name         ,
      nameextended ,
      op           ;

  ParseNodeMetaBlurb ( int a , int b , int c )
  { type         = ParseNode.META      ;
    name         = a;
    nameextended = b;
    op           = c;
  }
  String string() { return "ParseNodeMetaBlurb"; }
}


class ParseNodeLexica     extends ParseNode 
{
  int name;

  ParseNodeLexica    ( int lexiconstring )
  { type = ParseNode.LEXICA    ;
    name = lexiconstring;
  }
  String string() { return "ParseNodeLexica"; }
}


class ParseNodeAlternate  extends ParseNode 
{
  ParseNode node,
            rest;

  ParseNodeAlternate ( ParseNode a , ParseNode b )
  { type = ParseNode.ALTERNATE ;
    node = a;
    rest = b;
  }
  String string() { return "ParseNodeAlternate"; }
}


class ParseNodeExpression extends ParseNode 
{
  ParseNode node,
            rest;

  ParseNodeExpression( ParseNode a , ParseNode b )
  { type = ParseNode.EXPRESSION; 
    node = a; 
    rest = b;
  }
  String string() { return "ParseNodeExpression"; }
}


class ParseNodeCountABNF  extends ParseNode 
{
  ParseNode node         ;
  int       name         ,
            nameextended ,
            weight       ;

  ParseNodeCountABNF ( int low , int high , int w )
  { type = ParseNode.COUNTABNF ; 
    name         = low; 
    nameextended = high; 
    weight       = w; 
  }
  String string() { return "ParseNodeCountABNF"; }
}


class ParseNodeHeader     extends ParseNode 
{
  int name;

  ParseNodeHeader    ( int index )
  { type = ParseNode.HEADER    ; 
    name = index;
  }
  String string() { return "ParseNodeHeader"; }
}


class ParseNodeTag        extends ParseNode 
{
  int name;

  ParseNodeTag       ( int index )
  { type = ParseNode.TAG       ;
    name = index;
  }
  String string() { return "ParseNodeTag"; }
}


public class Converter
{
  static byte    DEFAULT  = 0  ,  // default turns into one of the latter choices.
                 ASSEMBLY = 1  ,
                 GSL      = 2  ,
                 BINARY   = 3  ;

  Lexer          in            ;
  PrintStream    out           ;
  StringBuffer   buffy         ;  // the vampire slayer, which was a funny movie.
  LexiconEnglish lexicon       ;
  VectoR         forest        ,  // the parse trees of the top level constructions.
                 symbols       ,
                 strings       ,
                 labels        ,  // Strings 
                 addresses     ,  // Integers : the ith element of addresses corresponds with that of labels.
                 references    ,  // Integers
                 refaddress    ,  // Integers : the ith element of addresses corresponds with that of labels.
                 image         ,  // Integers
                 namesandlabels,  // Strings  : this line and the next     correspond.
                 labelsandnames,  // Strings  : this line and the previous correspond.
                 visibilities  ,  // Chars    : this line and the previous correspond.
                 binaryfile    ;  // of Byte
  String         currentstring ;
  String         abnfrootrule  ;
  int            labelnumber   ;  // for assembly code generation, actually is the # of the current label.

  void initialize()
  {
    in             = null; // new Lexer( new File( name ) );
    out            = null;
    buffy          = new StringBuffer();
    forest         = new VectoR();
    symbols        = new VectoR();
    strings        = new VectoR();
    labels         = new VectoR();
    addresses      = new VectoR();
    references     = new VectoR();
    refaddress     = new VectoR();
    image          = new VectoR();
    namesandlabels = new VectoR();
    labelsandnames = new VectoR();
    visibilities   = new VectoR();
    binaryfile     = null;
    currentstring  = "";
    abnfrootrule   = "";
    labelnumber    = 0;
  }

  public Converter()
  {
    initialize();
    lexicon = null;
  }

  int    integer  ( Object o ) { return ((Integer)  o).   intValue() ; }
  char   character( Object o ) { return ((Character)o).  charValue() ; }
  String string   ( Object o ) { return ((String   )o)               ; }

  int  findandmaybeinsert( VectoR v , String s )   // this returns the index of the item in the VectoR
  {
    int i, alreadythere=0;
    i = v.indexOf( s );
    if (i == (-1))
       { v.add( new String( s ) );
         return v.size()-1; 
       } else
         return i;
  };

  void yum_abnf() { in.yum_abnf(); }
  void yum     () { in.yum     (); }


  void parseerror( String message , int k ) throws ErrorParsing
  {
    throw new ErrorParsing( ( "PARSE ERROR at input line number " + in.linenumber + ": " + message + " with lookahead '" + ((char)k) + "'.\n" ) );
  }
  
 
  String stringoid (char ldelimiter, char rdelimiter) throws ErrorParsing
  {
    int k;
    buffy = new StringBuffer();
    if (ldelimiter != (k = in.getc())) parseerror( "stringoid left delimiter wrong" , k );
    while ((k=in.getc()) != rdelimiter) 
          {
            buffy.append(((char)k));
          };
    return (currentstring = buffy.toString());
  }
  
  
  int string () throws ErrorParsing
  { stringoid('"','"');   // a filename with explicit directory in which it may reside.
    return findandmaybeinsert( strings , currentstring );
  }
   
  
  int string2 () throws ErrorParsing
  { stringoid('<','>');   // a filename with implied multiple directories in which it may reside.
    return findandmaybeinsert( strings , currentstring );
  }
  
  
  int string3 () throws ErrorParsing
  { stringoid('{','}');  // a semantics string, a so-called tag.
    return findandmaybeinsert( symbols , "{" + currentstring + "}" );
  }
  
  
  boolean isidentifieroid ( int k )
  {
    return (
               (    'a' <= k && k <=    'z' )
            || (    'A' <= k && k <=    'Z' )
            || (    '0' <= k && k <=    '9' )
            || (   144  <= k && k <=   255  )
            || (    '_' == k )
            || (    '-' == k )
            || (   '\'' == k )
           );
  }
  
  
  int identifier () throws ErrorParsing
  {
    int k;
    if (isidentifieroid( k = in.peekaboo() ))
       {
         buffy = new StringBuffer();
         if ('0' <= k && k <= '9') buffy.append( '_' );
         while ( isidentifieroid( k = in.peekaboo() ) || k == '.' ) buffy.append( (char)(in.getc()) );
       } else
       {
         buffy = null;
         parseerror("something other than an identifier is in the pipe.", k );
       };
    return findandmaybeinsert( symbols , buffy.toString() );
  }
  
  
  int literal ()  throws ErrorParsing // same as identifier except does not stick an underscore in front.
  {
    int k,rv;
    if (isidentifieroid( k = in.peekaboo() ))
       {
         buffy = new StringBuffer();

         while ( isidentifieroid( k = in.peekaboo() ) || k == '.' ) buffy.append( (char)(in.getc()) );
       } else
       {
         buffy = null;
         parseerror("something other than a literal is in the pipe.", k );
       };
    return findandmaybeinsert( symbols , buffy.toString() );
  }


  ///////////////////////////////////////////////////////////////
  //                                                           //
  // john jozwiak @ motorola speech labs june 12 2001.         //
  // this assembles code like the below into an integer array  //
  // of the opcodes in simulator.C .  yeah, baby.              //
  //                                                           //
  //////////////////////////////////////////////////////////////////////////////////////
  //                                                                                  //
  // here is the syntax for motorola phonetic assembly, 20010612:                     //
  // where int   N;                                                                   //
  //       float Q;                                                                   //
  // blankspace is ' ','\t','\n', and ';' through '\n'.                               //
  //                                                                                  //
  // Phoneticassembly =  (Label or Instruction)*                                      //
  // Label            =  L[not blankspace]*:                                          //
  // Instruction      =  'ears'                            {0,...,39}                 //
  //                     sil                               (39)                       //
  //                     noop                              {40,41,42}                 //
  //                     return                            (43)                       //
  //                     stone N                           (45 N)                     //
  //                     explore N label1 Q1 ... labelN QN (46 N L_1 Q_1 ... L_N Q_N) // 
  //                     visit label weight                (47 N)                     //
  //                     clue N                            (48 N)                     //
  //                     stop , exit                       (49)                       //
  //                                                                                  //
  //////////////////////////////////////////////////////////////////////////////////////
  //                                                                                  //
  // GSL input "HayIris (hay iris)" compiles into assembly code something like below. //
  //                                                                                  //
  //////////////////////////////////////////////////////////////////////////////////////
  //                                                    //
  // L0:		; wrapup code                   //
  // 	clue 32	        ; network end                   //
  // 	sil                                             //
  // 	exit                                            //
  // L1:	clue 2                                  //
  // 	15	;  hh                                   //
  // 	clue     1                                      //
  // 	12	;  ey1                                  //
  // 	return                                          //
  // L2:		; "hay" has 1 pronunciation.    //
  // 	visit L1 0.0                                    //
  // 	stone  0                                        //
  // 	sil                                             //
  // 	return                                          //
  // L3:	clue 2                                  //
  // 	5	;  ay1                                  //
  // 	27	;  r                                    //
  // 	2	;  ah0                                  //
  // 	clue 1                                          //
  // 	28	;  s                                    //
  // 	return                                          //
  // L4:	clue 2                                  //
  // 	5	;  ay1                                  //
  // 	27	;  r                                    //
  // 	16	;  ih0                                  //
  // 	clue 1                                          //
  // 	28	;  s                                    //
  // 	return                                          //
  // L5:		; "iris" has 2 pronunciations.  //
  // 	explore 2 L3 0.00 L4 0.00                       //
  // 	stone  0                                        //
  // 	sil                                             //
  // 	return                                          //
  // L6:		; sequence                      //
  // LHayIris:                                          //
  // 	visit L2 0.0                                    //
  // 	visit L5 0.0                                    //
  // 	return                                          //
  // end:                                               //
  //                                                    //
  //                                                    //
  // ;symboltable :                                     //
  // ;  L2	"hay"                                   //
  // ;  L5	"iris"                                  //
  // ;  L6	"HayIris"                               //
  //                                                    //
  ////////////////////////////////////////////////////////


  int addressoflabel( int name )
  {
    String s = string(symbols.get(name));
    for (int i = 0; i < labels.size() ; ++i)
        {
          if (s.equals( string(symbols.get( integer(labels.get(i))) )))
             return ( integer(addresses.get(i)) );
        }
    out.print("; ERROR:  addressoflabel for label='"+ string(symbols.get(name)) +"' not found.\n" );
    return (-1);
  }


  void label() throws ErrorParsing // 'L'literal blankspace ':'
  {
    int k;
    if ((k=in.peekaboo()) == 'L')
       {
         int label;
         in.getc();
         label = literal();  // no blankspace between 'L' and the literal (can be an integer)
         yum();
         if ((k=in.peekaboo()) == ':')
            in.getc();
            else
            parseerror("label lacked ':'",k);
         labels   . add ( new Integer(label)        );
         addresses. add ( new Integer(image.size()) );
       } else
       parseerror("label illegible",k);
    yum();
  }


  void instruction() throws ErrorParsing
  {
    int    k;
    String code;
    k    = literal();
    code = string(symbols.get(k));
  
    if      ( ('0' <= code.charAt(0)) && ( code.charAt(0) <= '9') )
            {
              image.add   ( new Integer(code) );
            }
    else if (code.equals("sil"))
            {
              image.add   (new Integer(39));
            }
    else if (code.equals("noop"))
            {
              image.add   (new Integer(40));
            }
    else if (code.equals("return"))
            {
              yum();
              image.add   (new Integer(43));
            }
    else if (   (code.equals("semantics"))
             || (code.equals("curly"    ))
            )
            {
              yum();
              int N = (new Integer( string(symbols.get( literal() ) ) )).intValue();  
              yum();
              image.add   (new Integer(44));
              image.add   (new Integer( N));
            }
    else if (code.equals("stone"))
            {
              yum();
              int N = (new Integer( string(symbols.get( literal() ) ) )).intValue();  
              yum();
              image.add   (new Integer(45));
              image.add   (new Integer( N));
            }
    else if (code.equals("explore"))
            {
              yum();
              int N = (new Integer( string(symbols.get( literal() ) ) )).intValue();  
              image.add   (new Integer(46));
              image.add   (new Integer( N));
              for( int i=0 ; i<N ; ++i )
                 {
                   yum();
                   int label = literal();
                   yum();
                   int weight= literal();
                   references.add (new Integer( label        ));
                   refaddress.add (new Integer( image.size() ));
                   image     .add (new Integer(-1));
                   image     .add (new Integer( (weight = 0) )); // (-2)
                 }
            }
    else if (code.equals("visit"))
            {
              yum();
              int N      = (new Integer( string(symbols.get( literal() ) ) )).intValue();
              yum();
              int weight = literal();   // (new Integer( string(symbols.get( literal() ) ) )).intValue();
              yum();
              image      . add (new Integer(47));
              image      . add (new Integer( N));
              image      . add (new Integer( 0));
              for( int i=0 ; i<N ; ++i )
                 {
                   yum();
                   int label = literal();
                   references.add (new Integer( label        ));
                   refaddress.add (new Integer( image.size() ));
                   image     .add (new Integer(-1));
                 }
            }
    else if (code.equals("goto"))
            {
              yum();
              int label  = literal();  
              yum();
              image     .add(new Integer(50));
              references.add(new Integer( label        ));
              refaddress.add(new Integer( image.size() ));
              image     .add(new Integer(-1));
            }
    else if (code.equals("visittodeath"))
            {
              yum();
              int label  = literal();  
              yum();
              image     .add(new Integer(51));
              references.add(new Integer( label        ));
              refaddress.add(new Integer( image.size() ));
              image     .add(new Integer(-1));
            }
    else if (code.equals("clue"))
            {
              yum();
              int N = (new Integer( string( symbols.get( literal() ) ) )).intValue();  
              yum();
              image.add(new Integer(48));
              image.add(new Integer( N));
            }
    else if (   (code.equals("stop" ) )
             || (code.equals("exit" ) )
            )
            { image.add(new Integer(49));
            }
    else    { parseerror( "unknown instruction '" + code + "'" , '\0' );
            }
  }


  void assembly() throws ErrorParsing // (label | instruction)* EOF
  {
    int k;
    in.linenumber = 1;
    if ((char)(k = in.peekaboo()) == '@') in.getc();
    yum();
    while ((k = in.peekaboo()) != 65535)
       {
              if (  k == 'L'   ) label();
         else if (  k != 65535 ) instruction();
         yum();
       };
  }


  void binaryprintint   ( int x   , OutputStream s )  throws IOException
  { s.write( ( x >>> (24) ) & 255 );
    s.write( ( x >>> (16) ) & 255 );
    s.write( ( x >>> ( 8) ) & 255 );
    s.write( ( x >>> ( 0) ) & 255 );
  }


  void binaryprintfloat ( float x , OutputStream s ) throws IOException
  { binaryprintint( 0 , s );
  }


  void binaryprintchar  ( char  x , OutputStream s ) throws IOException
  { s.write( (((byte)x) & (byte)255)) ;
  }


  boolean stringisalldigits( String s)
  {
    // System.err.println( "stringisalldigits checking \"" + s + "\"" );
    int     sl        = s.length();
    boolean alldigits = true;
    for (int i=0 ; i<sl && alldigits ; ++i)
        alldigits &= ('0' <= s.charAt(i) && s.charAt(i) <= '9');
    // System.err.println( "okey doke." );
    return alldigits;
  }


  void generateBinary( String objectfilename )  // starts at address 0.
  {
    try 
    {
    OutputStream binary = out;    //   new FileOutputStream( objectfilename );

    for (int i=0,n=references.size(); i<n ; ++i)
        {
          String s = string(symbols.get( integer(references.get(i)) ) );
          String s1= s.substring(1);
          int    a = (-1);
          int    l = labels.size();
          for (int j=0 ; j<l ; ++j)
              {
                 if (s1.equals( (string(symbols.get( integer(labels.get(j)) ) ) ) ) )
                    {
                      a = j;
                      j = l; // to exit the inner iteration.
                    }
              }
          int address = ( (a==(-1)) ? -1 : integer(addresses.get(a)) );
          if (a==(-1)) System.out.print( "unresolved : " + s + "\n" );
          image.set( integer(refaddress.get(i)) , new Integer(address) );  // fill in otherwise forward reference.
        }
 
    // now internal references are resolved, so spew code and header (trailer actually) information.
    // the object file format is (something like)
    //
    // char objectfilename[100];
    // int codelength;
    // int code[ codelength ];
    // int numberofaddresslabelnamepairs;
    // struct {
    //          int  address;        // if 0 then unresolved (= external) reference.  address 0 is a valid address for internal jumps, not externally visible jumps.
    //          char labelname[32];
    //        }
    //        labels[ numberofaddresslabelnamepairs ];
    // int numberofweightstoadjust;
    // struct {
    //          int address;
    //          int dialect;
    //        }
    //        weights[ numberofweightstoadjust ];

    StringBuffer beauregard = new StringBuffer();

    // snprintf( beauregard, 99 , "%s" , objectfilename );
    // for (int i=0; i<100; ++i)
    //     binaryprintchar( beauregard[i] , binary );                    // the object file name

    /*
     *    binaryprintint ( 0x1234 ,binary ); // eric's swapid
     *    binaryprintchar( 'b'    ,binary );
     *    binaryprintchar( 'i'    ,binary );
     *    binaryprintchar( 'n'    ,binary );
     *    binaryprintchar( 'p'    ,binary );
     *    binaryprintchar( 'h'    ,binary );
     *    binaryprintchar( 'o'    ,binary );
     *    binaryprintchar( 'n'    ,binary );
     *    binaryprintchar( 'e'    ,binary );
     *    binaryprintchar( ' '    ,binary );
     *    binaryprintchar( '1'    ,binary );
     *    binaryprintchar( '.'    ,binary );
     *    binaryprintchar( '0'    ,binary );
     *    binaryprintchar( '\0'   ,binary );
     *    binaryprintchar( '\0'   ,binary );
     *    binaryprintchar( '\0'   ,binary );
     *    binaryprintchar( '\0'   ,binary );
     */
  
    int numvisiblelabels = 0;
  
    binaryprintint( image.size() , binary );                                  // the length of the code
    for ( int i=0 , n=image.size() ; i<n ; ++i )
        binaryprintint( integer(image.get(i)) , binary );                     // the code itself
    for (int i=0,n=labels.size(); i<n ; ++i)
        {
          String d , 
                 el = string(symbols.get( integer(labels.get(i))) );
          if (!stringisalldigits(el)) ++numvisiblelabels;
        }
    binaryprintint( numvisiblelabels , binary );                              // the # of (address,labelname) pairs
    int n = labels.size();
    for (int i=0; i<n ; ++i)
        {
          String el = string(symbols.get( integer(labels.get(i)) ) ) ;
          if (!stringisalldigits(el))
          {
            binaryprintint( integer(addresses.get(i)) , binary );             // the address
            beauregard = new StringBuffer();
            beauregard.append( el );
            while (beauregard.length() < 32) beauregard.append( "\0" );
            for (int j=0; j<32 ; ++j)
                {
                  // System.err.println( "now appending the " + j + "(th) character." );
                  binaryprintchar( beauregard.charAt(j) ,binary );             // the label (without the initial 'L')
                }
          }
        }
 
    n = symbols.size();
    binaryprintint( n , binary );
    for (int i=0; i<n ; ++i)
        {
          String el = string(symbols.get( i ));
          if (!stringisalldigits(el))
          {
            binaryprintint( i , binary );
            beauregard = new StringBuffer();
            beauregard.append( el );
            while (beauregard.length() < 32) beauregard.append( "\0" );
            for (int j=0; j<32 ; ++j)
                  binaryprintchar( beauregard.charAt(j) ,binary );
          }
        }
 
    int numberoflabs = 0,
        numberofrefs = 0;
  
    n = labels.size();

    for (int i=0; i<n ; ++i) ++numberoflabs;
  
    System.out.print( objectfilename + " defines " + numberoflabs + " targets\n" );
  
    n = labels.size();

    for (int i=0; i<n ; ++i)
        {
          String s = string(symbols.get( integer(labels.get(i)) ) );
          System.out.print( "  " + (s) + "@" + ((Integer)(addresses.get(i))) + "\n"  );
        }
  
    n = references.size();

    for (int i=0; i<n ; ++i) numberofrefs++;
  
    System.out.print( objectfilename + " uses " + (numberofrefs) + " references\n" );
  
    n = references.size();

    for (int i=0; i<n ; ++i)
        {
          String s = string(symbols.get( integer(references.get(i)) ) );
          String s1= s.substring(1);
          int    a = (-1);
          int    l = labels.size();
          for (int j=0 ; j<l ; ++j)
              if (s1.equals( string(symbols.get( integer(labels.get(j)) ))))
                 {
                   a = j;
                   j = l; // to exit the inner iteration.
                 }
          int address = ( (a==(-1)) ? (-1) : integer(addresses.get(a)) );
          // if (! stringisalldigits(s+1))
             System.out.print( "  *(address " + (refaddress.get(i)) + ") = " + (address)  + " = &" + (s+1) + "\n" );
        }

    binaryprintint( 0 , binary );       // the # of weights to adjust
    // loop and print all weights to adjust in terms of their addresses and corresponding dialects.
    binary.flush();
    binary.close();
    } catch (Exception e)
    { System.out.println( "Code generation error during binary code generation." );
    }
  }


  // john jozwiak at motorola 20010621.


  ///////////////////////////////////////////////////////////////////
  //                                                               //
  // gsl recursive descent parser from the nuance 7 specs.         //
  // entered by john jozwiak @ motorola speech labs february 2001. //
  // conversion from antlr-2.7.1 to C               march    2001. //
  //                                                               //
  //  Nuance 7 GSL has the following grammar, where                //
  //  terminals use big letters, nonterminals small letters.       //
  //                                                               //
  //  gsl         : (inclusion | definition)* EOF                  //
  //  inclusion   : "#include"  ( STRING | STRING2 )               //
  //  definition  : (".")?  ID  (":" ID)?  expr                    //
  //  expr        : ID  (":" ID)?         weight  semantics        //
  //              | "[" ((expr)*) "]"     weight  semantics        //
  //              | "(" ((expr)*) ")"     weight  semantics        //
  //              | ("?"|"*"|"+")   expr  weight  semantics        //
  //  weight      : ( "~" WEIGHT )?                                //
  //  semantics   : ( "{"  [.]*   "}" )?                           //
  //                                                               //
  ///////////////////////////////////////////////////////////////////
  //                                                               //
  // nuance7 reserves these identifiers: we ignore this.           //
  // "NULL" , ( ("AND" | "OR" | "OP" | "KC" | "PC") '-' [0..9]+ )  //
  //                                                               //
  ///////////////////////////////////////////////////////////////////
 

  ///////////////////////////////////////////////////////////////////
  //                                                               //
  // ABNF from the retardedly named w3c.org voice group uses the   //
  // syntax below.                                                 //
  //                                                               //
  // comments   : // ... '\n'                                      //
  //              /* ...  */                                       //
  //              /** ... */                                       //
  //                                                               //
  // header     : '#' [not '\n']*   ';' '\n'                       //
  //                                                               //
  // metastuff  : "language" id                    ';'             //
  //              "mode"     ( "speech" | "dtmf" ) ';'             //
  //              "root"     id                    ';'             //
  //              "import"   id "as" id            ';'             //
  //                                                               //
  // expr       : $id                           // invocations     //
  //              $(id#id)                                         //
  //              $(id)                                            //
  //              $$id#id                                          //
  //              $$id                                             //
  //              $NULL                                            //
  //              $VOID                                            //
  //              $GARBAGE                                         //
  //              id                            // word            //
  //              expr expr                     // sequence        //
  //              weight expr '|' weight expr   // choice          //
  //              '[' expr ']'                  // optional        //
  //              expr '*'                      // 0 or more       //
  //              expr '+'                      // 1 or more       //
  //              expr '!' id                   // id a language   //
  //              '(' expr ')'                  // same as expr    //
  //                                                               //
  // weight     : '/' nonnegativefloat '/'                         //
  // tag        : '{' [.]* '}'                                     //
  //                                                               //
  // definition : ("public" | "private ")? $id '=' expr ';'        //
  //                                                               //
  // abnf       : preamble (context | definition)* EOF             //
  //                                                               //
  ///////////////////////////////////////////////////////////////////


  String labelforname( int name )
  {
    String s = string( symbols.get(name) );
    for (int i = 0; i < namesandlabels.size() ; ++i)
        {
          if (s.equals( string( namesandlabels.get(i) )))
             return string( labelsandnames.get(i) );
        }
    out.print( "; ERROR:  labelforname for name='" + string(symbols.get(name)) + "' not found.\n" );
    return "L-COMPILER-ERROR";
  }


  String makelabelfromint( int i )
  { return "L"+i ;
  }


  String makelabelfromstring( String s )
  {
    return "L"+s;
  }


  void shownamesandlabels()
  {
    for ( int i = 0 ; i<namesandlabels.size() ; ++i )
        out.print( ";= symbol  " + string(labelsandnames.get(i)) + "\t\"" + string(namesandlabels.get(i)) + "\"\n"); 
  }


  int readpositiveinteger()
  {
    int k, r;
    buffy = new StringBuffer();
    while ('0' <= (k=in.peekaboo()) && k <= '9')
          {
            buffy.append( (char)(in.getc()) );
          }
    r = (new Integer((new String(buffy)))).intValue();
    return r;
  }


  float readpositivefloat()
  {
    int wholepart=0, fractionpart=0;
    buffy     = new StringBuffer();
    wholepart = readpositiveinteger();
    if ('.'==in.peekaboo())
    {
      in.getc();
      fractionpart = readpositiveinteger();
    }
    return (float)(0.0);
  }


  int everythingbutblankspaceorsemicolon()
  {
    int k;
    buffy = new StringBuffer();
    while (   ( ';'  != (k = in.peekaboo()))
            &&( ' '  != k                     )
            &&( '\t' != k                     )
            &&( '\n' != k                     )
            &&( (-1) != k                     )
          )
          buffy.append( (char)(in.getc()) );
    currentstring = new String(buffy);
    return findandmaybeinsert( symbols , currentstring );
  }


  ParseNode header_abnf() throws ErrorParsing // '#' [not ';']* ';' '\n'
  {
    stringoid( '#' , ';' );
    return new ParseNodeHeader( findandmaybeinsert( strings , currentstring ) );
  }


  int weight_abnf() throws ErrorParsing //  '/' nonnegativefloat '/'
  {
    yum_abnf();
    if (in.peekaboo() == '/')
       stringoid( '/' , '/' );
    return findandmaybeinsert( strings , currentstring ) ;
  }


  ParseNode tag_abnf() throws ErrorParsing
  {
    int k;
    int nestlevel=0;
    buffy = new StringBuffer();
    if ('{' != (k=in.getc())) parseerror( "tag ill-formed" , k );
    while (   ((k=in.getc()) != '}')
           || (nestlevel-- > 0)
          )
          {
            if ('{' == k) ++nestlevel;
            buffy.append((char)k);
          }
    currentstring = new String( buffy );
    return new ParseNodeTag( findandmaybeinsert( symbols , currentstring ) );
  }


  int  nonterminal_abnf() throws ErrorParsing
  {
    //  $id       //  nonterminal     (NULL,VOID,GARBAGE are reserved values for id)
    //  $(id#id)  //  nonterminal,offset 
    //  $(id)     //  nonterminal     
    //  $$id#id   //  alias,nonterminal
    //  $$id      //  alias           
  
    int k=0;
    yum_abnf();
    if ('$' != (k=in.peekaboo())) parseerror( "this is no nonterminal" , ((char)k) );
    in.getc();  // = '$'
    buffy = new StringBuffer();
    buffy.append( '$' );
  
    switch( k=in.peekaboo() )
    {
      case '$' : 
                 buffy.append( (char)(in.getc()) );
                 identifier();
                 while ( isidentifieroid( in.peekaboo() ) ) buffy.append( (char)(in.getc()) );
                 if ('#' == in.peekaboo()) 
                    {
                      buffy.append( (char)(in.getc()) ); // the '#'
                      while ( isidentifieroid( in.peekaboo() ) ) buffy.append( (char)(in.getc()) );
                    };
                 break;
      case '(' : 
                 buffy.append( (char)(in.getc()) );
                 while ( (k=in.getc()) != ')' ) buffy.append( (char)k );
                 buffy.append( ')' );
                 break;
      default  : 
                 while ( isidentifieroid( in.peekaboo() ) ) buffy.append( (char)(in.getc()) );
                 break;
    }
    return findandmaybeinsert( symbols , new String(buffy) );
  }


  // iterator      = '<' positiveinteger ('-' positiveinteger)? weight? '>'
  //
  // subexpression = tag                                                // starts '{' and ends with '}'
  //               | nonterminal                                        // starts with '$'
  //               | token              ( '!' languagecodelist )?       // alphabetic, can occur quoted
  //               | '(' expression ')' ( '!' languagecode     )?
  //               | '[' expression ']' ( '!' languagecode     )?
  //
  // alternative   = ('/' number '/')? (subexpression (iterator)? )*    // ends with ';' or ']' or ')' or '|'
  //
  // expression    = alternative ( '|' alternative )*                   // ends with ';' or ']' or ')'


  ParseNodeCountABNF iterator_abnf() throws ErrorParsing
  {
    int low=0,high=0,weight=(-1);
    switch(in.peekaboo())
    {
      case '*': in.getc(); low=0;high=(-8); /*infinity*/     break;
      case '+': in.getc(); low=1;high=(-8); /*infinity*/     break;
      case '?': in.getc(); low=0;high=(1);                   break;
      case '<': 
                {
                  int k;
                  in.getc(); // <
                  yum_abnf();
                  k = in.peekaboo();
                  if ('0' <= k && k <= '9')
                     low = readpositiveinteger();
                     else
                     parseerror("yo, the abnf iterator starts with a positive integer.\n",k);
                  yum_abnf();
                  if ('-' == in.peekaboo())
                     {
                       in.getc();
                       yum_abnf();
                       k = in.peekaboo();
                       if ('0' <= k && k <= '9')
                          high = readpositiveinteger();
                          else
                          high = (-8);
                     }
                     else high=low;
                  yum_abnf();
                  if ('/' == in.peekaboo())
                     {
                       weight = weight_abnf();
                       yum_abnf();
                     }
                  if ('>' == in.peekaboo())
                     in.getc();
                     else
                     parseerror("yo, the abnf iterator ends with '>'.\n" , in.peekaboo() );
                };
                break;
      default : parseerror("count operator 'screwy'" ,  in.peekaboo() );
                break;
    }
    return new ParseNodeCountABNF( low , high , weight );
  }


  ParseNode subexpression_abnf() throws ErrorParsing
  {
    int  k;
    ParseNode r = null;
    yum_abnf();
    switch( k = in.peekaboo() )
    {
      case '{':                       // ends later with '}'
                 r = tag_abnf();
                 break;
  
      case '$':  r = new ParseNodeIdentifier( nonterminal_abnf() , 0 , 0 );
                 break;
  
      case '(':
                 in.getc(); 
                 yum_abnf();
                 r = expression_abnf();
                 yum_abnf();
                 if (')' != (k=in.getc())) parseerror( "the syntax is hosed" , k );
                 yum_abnf();
                 if ('!' == in.peekaboo()) { in.getc(); everythingbutblankspaceorsemicolon(); }
                 break;
  
      case '[':
                 in.getc(); 
                 yum_abnf();
                 r = new ParseNodeCount( '?' ,
                                expression_abnf() ,
                                0 , 0
                              );
                 yum_abnf();
                 if (']' != (k=in.getc())) parseerror( "the syntax is hosed" , k );
                 yum_abnf();
                 if ('!' == in.peekaboo()) { in.getc(); everythingbutblankspaceorsemicolon(); }
                 break;
  
      case '"':  r = new ParseNodeIdentifier( findandmaybeinsert( symbols , stringoid('"','"') ) // a token (wearing doublequotes)
                                            , 0
                                            , 0
                                            );
                 yum_abnf();
                 if ('!' == in.peekaboo()) { in.getc(); everythingbutblankspaceorsemicolon(); }
                 break;
  
      default :  r = new ParseNodeIdentifier( identifier() // a token (nude)
                                            , 0
                                            , 0
                                            );
                 yum_abnf();
                 if ('!' == in.peekaboo()) { in.getc(); everythingbutblankspaceorsemicolon(); }
                 break;
    }
    yum_abnf();
    return r;
  }


  ParseNode alternative_abnf() throws ErrorParsing // an optional weight followed by a sequence
  {
    int k,w=(-1);
    VectoR list = new VectoR();
    ParseNode x       ;
    ParseNodeCountABNF iterator;
    yum_abnf();
    if ('/' == (k=in.peekaboo())) 
       {
         w = weight_abnf();
         yum_abnf();
       }
    while (
               (';' != (k=in.peekaboo()))
            && (']' !=  k            )
            && (')' !=  k            )
            && ('|' !=  k            )
          )
       {
         x        = subexpression_abnf();
         iterator = null;
         yum_abnf();
         k = in.peekaboo();
         if (    ('<' == k)
              || ('*' == k)
              || ('+' == k)
              || ('?' == k)
            )
            {
              iterator = iterator_abnf();
              yum_abnf();
            }
         if (iterator == null)
            list . add( x );
            else
            {
              iterator . node = x;
              list     . add( iterator );
            }
       }
    return new ParseNodeSequence( list,0,0 );
  }


  ParseNode expression_abnf() throws ErrorParsing // a choice 
  {
    int       k;
    ParseNode s = null ;
    VectoR list = new VectoR();
    yum_abnf();
    if (
            (';' != (k=in.peekaboo())) 
         && (']' !=  k            )
         && (')' !=  k            )
       )
       {
         s = alternative_abnf();
         list.add( s );
         while ('|' == (k=in.peekaboo())) 
            {
               in.getc();
               yum_abnf();
               s = alternative_abnf();
               list.add( s );
               yum_abnf();
            }
         yum_abnf();
       }
    return new ParseNodeChoice( list,0,0 );
  }


  ParseNode definition_abnf() throws ErrorParsing  //  (public | private)? $nonterminal = expr ;
  {
    int a,b;
    ParseNode x;
    boolean visible = true;
    yum_abnf();
  
    // optional "public" or "private"

    if ('$' != in.peekaboo())
       {
         a = identifier();
         if      ("public" .equals( symbols.get(a) ) ) {}
         else if ("private".equals( symbols.get(a) ) ) { visible = false; }
         else                                          parseerror( "abnf rules are '[public|private] rulename = expr ;' ", in.peekaboo() );
         yum_abnf();
       }
  
    a = nonterminal_abnf();                                                                     yum_abnf();
    if ('=' != (b=in.getc())) parseerror( "$id needs an = after it in this situation" , b ); yum_abnf();
    x = expression_abnf();                                                                      yum_abnf();
    if (';' != (b=in.getc())) parseerror( "$id = expr _ " , b );
    return new ParseNodeDefinition( a , -1 , x , visible );
  }


  // abnf = 
  //        header_abnf          
  //
  //        ( 'language'   LanguageCode                            ';' )?
  //        ( 'mode'       ('voice' | 'dtmf')                      ';' )?
  //        ( 'root'       RuleName                                ';' )?
  //        ( 'tag-format' id                                      ';' )?
  //
  //        (
  //           'alias'      GrammarURI       'as' AliasName        ';'
  //         | 'lexicon'    LexiconURI                             ';'
  //         | 'meta'       QuotedCharacters 'is' QuotedCharacters ';'
  //         | 'http-equiv' QuotedCharacters 'is' QuotedCharacters ';'
  //        )*
  //
  //        ( 
  //          ('private' | 'public')?
  //          RuleName '=' expression    ';'
  //        )*


  void abnf() throws ErrorParsing
  {
    ParseNode r;
    int       a,b,c,d;
    boolean   inpreamble = true;
  
    r = header_abnf();
    forest.add( r );
    in.yum_abnf();
    while ( (inpreamble) && ((a = in.peekaboo()) != (-1)) && ( ((char)a) != '$' ) )
      {
        a = identifier();
        String token = string( symbols.get(a) );
        if (    ("private".equals(token))
             || ("public" .equals(token))
           ) inpreamble = false;
           else
           {
              b = c = (-1);
                   if (token.equals("language"))
                      {
                        yum_abnf();
                        b = identifier();
                      }
              else if (token.equals("mode"    ))
                      {
                        yum_abnf();
                        b = identifier();
                      }
              else if (token.equals("root"    ))
                      {
                        yum_abnf();
                        if ('$' != (b=in.getc())) parseerror( "root <rulename> where rulename starts with '$'" , b );
                        b = identifier();
                        abnfrootrule = string(symbols.get(b));
                      }
              else if (token.equals("tag-format"))
                      {
                        yum_abnf();
                        b = everythingbutblankspaceorsemicolon();
                        yum_abnf();
                      }
              else if (token.equals("alias"     ))
                      {
                        yum_abnf();
                        b = everythingbutblankspaceorsemicolon();
                        yum_abnf();
                        if ('a' == in.peekaboo())
                        if (! "as".equals( symbols.get( identifier() ) ) )  parseerror( "alias <internetaddress> _as_ <aliasname>" , in.peekaboo() );
                        yum_abnf();
                        c = nonterminal_abnf();
                      }
              else if (token.equals("lexicon"   ))
                      {
                        yum_abnf();
                        b = everythingbutblankspaceorsemicolon();
                      }
              else if (token.equals("meta"      ))
                      {
                        yum_abnf();
                        b = findandmaybeinsert( symbols , string( strings.get( string() ) ) );
                        yum_abnf();
                        if (
                                ('i' == in.peekaboo())
                             && (! "is".equals( symbols.get( identifier() ) ) )
                           ) parseerror( "meta <string> is <string>" , in.peekaboo() );
                        yum_abnf();
                        c = findandmaybeinsert( symbols , string( strings.get( string() ) ) );
                      }
              else if (token.equals("http-equiv"))
                      {
                        yum_abnf();
                        b = findandmaybeinsert( symbols , string( strings.get( string() ) ) );
                        yum_abnf();
                        if (! "is".equals( symbols.get( identifier() ) ) )  parseerror( "http-equiv <string> is <string>" , in.peekaboo() );
                        yum_abnf();
                        c = findandmaybeinsert( symbols , string( strings.get( string() ) ) );
                      }
              yum_abnf();
              if (';' != ((char)(d=in.getc()))) parseerror( "vanished semicolon" , d );
              forest.add( new ParseNodeMetaBlurb( a , b , c ) );
           }
        in.yum_abnf();
      }
    in.yum_abnf();
    while ( ((a = in.peekaboo()) != (65535)) )
      {
        // out.println( "Looking on line " + in.linenumber + " for a definition with lookahead = " + ((char)a) + "\n" );
        r = definition_abnf();
        forest.add( r );
        in.yum_abnf();
      }
  }


  //  GSL is exclusively below this line.


  int weight () throws ErrorParsing     // ( "~" floatingpointnumber )?
  {
    int r,k;
    yum();
    if (in.peekaboo() == '~')
       {
         in.getc(); yum();     // the tilde
         buffy = new StringBuffer();
         buffy.append('0');
         buffy.append('.');
         switch (k=in.getc())  // optionally a 0, then a dot, then some digits
         {
           case '0': if((k=in.getc()) != '.')
                       {
                         parseerror("bogus floating point weight",k);
                         break;
                       };
           case '.': while ('0' <= (k=in.getc()) && k <= '9') 
                           buffy.append(k);
                     break;
           default:  parseerror("tilde introduces a floating point number",k);
         };
         in.ungetc(k);  // we recognized the end by grabbing too far.
         // buffy . append( '\0' );
         r = findandmaybeinsert( strings , new String(buffy) );
       } else
       {
         r = findandmaybeinsert( strings , "1.0" );
       }
    return r;
  }


  int tag_gsl () throws ErrorParsing
  {
    yum();
    if (in.peekaboo() == '{')  // a matching '}' bounds the other side
         return string3();
    else return (0);        // the index of "{}" in the semantics table.
  }


  ParseNode  expr_gsl ( int level ) throws ErrorParsing
  {
    // expr : ID ( ":"  ID )?         weight semantics
    //      | "[" ((expr)*) "]"       weight semantics 
    //      | "(" ((expr)*) ")"       weight semantics 
    //      | ("?"|"*"|"+")     expr  weight semantics 
  
    ParseNode r;
    int       k;
  
    yum();
    if ( isidentifieroid( k = in.peekaboo() ) )
       {
         int i,j;
         i = identifier(); yum();
         if (in.peekaboo() == ':')
            {
              in.getc();          yum(); // get the colon
              if (! isidentifieroid( j = in.peekaboo() ) )
                 parseerror("broken nuance id:?? syntax",j);
              j = identifier(); yum();
            } else
              j = (-1);
         // System.out.print( "; expr identifier ( '%s' : '%s' )\n", symbols->get(i) , (j == (-1) ? "" : symbols->get(j) ) );
         r = new ParseNodeIdentifier( i, /* j, */ 0,0 );
       } else
    if ( k == '[' || k == '(')  // then choice or sequence, so later will come matching ] or ).
       {
         int           o,
                       bound = (k == '[' ? ']' : /*(*/ ')');
         VectoR list = new VectoR();
         o = in.getc(); yum(); // get that left delimiter
         while ((k=in.peekaboo()) != bound)
               {
                  r = expr_gsl( level+1 );
                  list.add(r); yum();
               };
         in.getc(); yum(); // get the matching delimiter on the right
         r = ( o == '[' /* ] */ 
               ? (ParseNode)(new ParseNodeChoice  (list , 0 , 0) )
               : (ParseNode)(new ParseNodeSequence(list , 0 , 0) )
             );
       } else
    if ( k == '?' || k == '*' || k == '+' )
       {
         in.getc(); yum(); // get that operator
         r = expr_gsl( level+1 );  yum();
         r = new ParseNodeCount(k,r,0,0);
       } else
       {
         parseerror( "trying to get an expr", k );
         r = null;
       };
  
    r.weight    = weight();  yum();
    r.semantics = tag_gsl(); yum();
    return r;
  }


  void inclusion_gsl () throws ErrorParsing
  {
    int       i,
              k;
    ParseNode r;
    if (
           ('#'== (k=in.getc()))
        && ('i'== (k=in.getc()))
        && ('n'== (k=in.getc()))
        && ('c'== (k=in.getc()))
        && ('l'== (k=in.getc()))
        && ('u'== (k=in.getc()))
        && ('d'== (k=in.getc()))
        && ('e'== (k=in.getc()))
       )
       {
         yum();
         k = in.peekaboo();
         if      (k == '"') { i = string();  r = new ParseNodeInclusion(0,i); }
         else if (k == '<') { i = string2(); r = new ParseNodeInclusion(1,i); }
         else               { r = null;
                              in.ungetc(k);
                              parseerror("unstrung by un-string after #include",k);
                            };
       } else
       {
         r = null;
         in.ungetc(k);
         parseerror("inclusion",k); 
       };
    forest.add(r);
  }


  void definition_gsl () throws ErrorParsing     // (".")?  ID  (":" ID)?  expr
  {
    int        k         ,
               i         ,
               j         ;
    boolean    globalviz ;
    ParseNode  r         ;
    ParseNode  x         ;
  
    if ((globalviz = ((k = in.peekaboo()) == '.'))) in.getc(); // '.' means global identifier visibility.
    yum();
    i = identifier(); yum();
    if ((k=in.peekaboo())==':')
       {
         in.getc();      yum(); // get the colon.
         j = identifier();  yum(); // get the elaborator.
       } else
         j = (-1);
    x = expr_gsl( 0 );
    // fprintf( out ,  "; about to assign the identifier '%s' as a definition name (forests length = %d beforehand)\n" , symbols->get(i) , forest->length() );
    r = new ParseNodeDefinition( i, j , x , globalviz );
    // out.print( "; definition:  forest[" + forest.size() + "] = definition of the symbol '" + (string(symbols.get(i))) + "'\n" );
    forest.add(r);
  }


  void gsl() throws ErrorParsing     // (inclusion | definition)*  EOF
  {
    ParseNode t;
    int       k;
    yum();
    while ((k = in.peekaboo()) != 65535)
       {
         t = null;
              if (  k == '#' )                        inclusion_gsl () ;
         else if ( (k == '.') || isidentifieroid(k) ) definition_gsl() ;
         else if (  k != 65535 )                      parseerror("invalid nuance gsl input",k);
         yum();
       }
  }


  double logify( double w ) { return Math.log( w ); }


  boolean containsaspace( String s ) { return (-1) != s.indexOf( ' ' ); }


  String gslencodeabnfidentifier ( String s )
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(s);
    if  ( buffer.charAt(0) != '$' ) // then this is a nonterminal so start it with a lower case letter as GSL requires.
        {
          buffer.setCharAt( 0 , Character.toLowerCase( buffer.charAt(0) ) );
        }
    // turn each '$' into a 'D', so nonterminals in abnf turn into nonterminals in gsl.
    for ( int i = 0 ; i < buffer.length() ; ++i)   if (buffer.charAt(i) == '$') buffer.setCharAt(i,'D');
    return new String(buffer);
  }


  ParseNode simplification( ParseNode r )  // look for simplifications,
  {                                        // such as flattening choice of choice, 
    ParseNode v = null;                    //         flattening sequence of sequence, and
    switch( r.type )                       //         expanding ABNF iterators.
    {
       case ParseNode.CHOICE     :
       case ParseNode.SEQUENCE   :
                         {
                           VectoR list = ((ParseNodeChoiceOrSequence)r).list;
                           int n = list.size();
                           if (n==1)
                              {
                                v = simplification( (ParseNode)(list.get(0)) );
                              } else
                              {
                                for (int i = 0 ; i < n ; ++i)
                                    if (list.get(i) != null) list.set(i , simplification( ((ParseNode)(list.get(i))) ) );
                                v = r;
                              }
                         }
                         break;
  
       case ParseNode.COUNT      :
                         ((ParseNodeCount)r).node = simplification( ((ParseNodeCount)r).node );
                         v = r;
                         break;
  
       case ParseNode.EXPRESSION : 
                         if (((ParseNodeExpression)r).node != null) ((ParseNodeExpression)r).node = simplification( ((ParseNodeExpression)r).node );
                         if (((ParseNodeExpression)r).rest != null) ((ParseNodeExpression)r).rest = simplification( ((ParseNodeExpression)r).rest );
                         v = r;
                         break;
  
       case ParseNode.COUNTABNF  : 
                         {
                           int low   = ((ParseNodeCountABNF)r).name;
                           int high  = ((ParseNodeCountABNF)r).nameextended;
                           ((ParseNodeCountABNF)r).node = simplification( ((ParseNodeCountABNF)r).node ); 
                           if      (low==0 && high==(1 )) v = new ParseNodeCount('?',((ParseNodeCountABNF)r).node,-1,-1);
                           else if (low==0 && high==(-8)) v = new ParseNodeCount('*',((ParseNodeCountABNF)r).node,-1,-1);
                           else if (low==1 && high==(-8)) v = new ParseNodeCount('+',((ParseNodeCountABNF)r).node,-1,-1);
                           else if (low==0)
                                                        {
                                                          // generate a sequence of high instances of optionals.
  
                                                          VectoR list = new VectoR();
                                                          for (int j=0;j<high;++j) 
                                                              list.add( new ParseNodeCount( '?', ((ParseNodeCountABNF)r).node , (-1) , (-1) ) );
                                                          v =         ( new ParseNodeSequence( list , (-1) , (-1) ) );
                                                        }
                           else                         {
                                                          int i = low;
                                                          low = low - i;
                                                          if (high != (-8))  // -8 is the notation for infinity.
                                                             high = high - i;
                                                          // now generate an i+1 element sequence of i instances followed by a revised iterator from low..high.
                                                          VectoR list = new VectoR();
                                                          for (int j=0;j<i;++j) list.add( ((ParseNodeCountABNF)r).node );         // i instances
                                                          ParseNode temp = new ParseNodeCountABNF( low , high , (-1) );  // revised iterator
                                                          ((ParseNodeCountABNF)temp).node = ((ParseNodeCountABNF)r).node;
                                                          temp = simplification( temp );
                                                          list.add( (temp) );
                                                          v = ( new ParseNodeSequence( list , (-1) , (-1) ) );
                                                        }
                         }
                         break;
  
       default         : v = r; 
                         break;
    }
    return v;
  }


  void generateexpr2GSL( ParseNode r , String slotname )
  {
    VectoR  list       = null;
    int     n          = (-1);
    boolean spacecadet = false;
    String  s          = "";
    switch( r.type )
    {
       case ParseNode.ID         :
            // semantics slot name is in r->slotname, symbol name in r->string.
            s          = string(symbols.get( ((ParseNodeIdentifier)r).name ) );
            spacecadet = containsaspace( s );
            if (spacecadet) out.print( "(" );
            out.print( gslencodeabnfidentifier( s ) ); 
            if (spacecadet) out.print( ")" );
            break;
    
       case ParseNode.CHOICE     :

            out.print( "[" );
            list = ((ParseNodeChoice)r).list;
            n    = list.size();
            for (int i = 0 ; i < n ; ++i)
                if (list.get(i) != null) 
                generateexpr2GSL( (ParseNode)(list.get(i)) , ( "s"+i ) );
            out.print( "]" );
            break;
  
       case ParseNode.SEQUENCE   :

            out.print( "(" );
            list = ((ParseNodeSequence)r).list;
            n    = list.size();
            for (int i = 0 ; i < n ; ++i)
                if (list.get(i) != null) 
                generateexpr2GSL( (ParseNode)(list.get(i)) , ( "s"+i) );
            out.print( ")" );
            break;
  
       case ParseNode.COUNT      :

            ParseNode x  = ((ParseNodeCount)r).node ;
            int       op = ((ParseNodeCount)r).op   ;
            out.print( (char)op );
            generateexpr2GSL( x , "scount" );
            break;
  
       case ParseNode.LEXICA     : 
            // out.print( "\n; IGNORED ABNF LEXICON ANNOTATION" );
            break;

       case ParseNode.EXPRESSION : 
            if (((ParseNodeExpression)r).node != null) 
               generateexpr2GSL( ((ParseNodeExpression)r).node , "sexpr" );
            if (((ParseNodeExpression)r).rest != null) 
               generateexpr2GSL( ((ParseNodeExpression)r).rest , "sexprrest" );
            break;

       case ParseNode.COUNTABNF  : 
            out.print( "<iterator " + 
                       ((ParseNodeCountABNF)r).name + ".." + 
                       ((ParseNodeCountABNF)r).nameextended + " "
                     );
            generateexpr2GSL( ((ParseNodeCountABNF)r).node , "snull" ); 
            out.print( ">" );
            break;

       case ParseNode.TAG        : 
            out.print( " { return strcat( $schmlot , (\"" +
                       URLEncoder.encode( string(symbols.get(((ParseNodeTag)r).name) ) ) +
                       "\") " + ") "  + "}" 
                     );
            break;

       default         : 
            out.print( "big error\n" );
            System.exit(-10);
    }
    out.print( ":" + slotname + " " );
  }


  void generateGSL( String rootrule )
  {
    out.print( "\n" +
                      "; Motorola : generated GSL\n" +
                      "; version 20011010 : jozwiak and menarek\n" +
                      "\n"
                    );
  
    for (int i = 0 ; i < forest.size() ; ++i)
        {
          ParseNode node = (ParseNode)( forest.get(i) );
          switch (node.type)
          {
             case ParseNode.INCLUSION :
                                        out.print(   "; (include \"" + strings.get( ((ParseNodeInclusion)(forest.get(i))). name  )
                                                            + "\" "+
                                                                        ( ( ((ParseNodeInclusion)(forest.get(i))).where == 1 )
                                                                          ? "usingsearchpath"
                                                                          : "fromcurrentdirectory"
                                                                        )
                                                            +")\n"
                                                        );
                                        break;
    
             case ParseNode.DEFINITION:
                                        if (("$"+rootrule).equals( string(symbols.get( ((ParseNodeDefinition)node).name ) ) ) )
                                           out.print(".");
                                        out.print( gslencodeabnfidentifier( string(symbols.get( ((ParseNodeDefinition)node).name ) ) ) + " " );
                                        if (((ParseNodeDefinition)node).node != null)
                                           generateexpr2GSL( simplification( ((ParseNodeDefinition)node).node ) , "sdef" );   // abnf allows 'nonterminal = ;'
                                           else
                                           out.print( "()" );
                                        break;
  
             case ParseNode.META      :
                                        out.print( "; abnfism : <"+
                                                          string( symbols.get( ((ParseNodeMetaBlurb)node).name ) )
                                                          +"> "
                                                        );
                                        if ( ((ParseNodeMetaBlurb)node).nameextended != (-1) ) out.print( "<" +  symbols.get( ((ParseNodeMetaBlurb)node).nameextended ) + "> " );
                                        if ( ((ParseNodeMetaBlurb)node).op           != (-1) ) out.print( "<" +  symbols.get( ((ParseNodeMetaBlurb)node).op           ) + "> " );
                                        break;
  
             case ParseNode.HEADER    :
                                        out.print( "; abnfism : <" + string( strings.get( ((ParseNodeHeader)node).name) ) + ">" );
                                        break;
  
             default        :           out.print( "internal coding error: very 'novel' top level parse tree node (in the forest).\n" );
                                        System.exit(1);
          }
          out.print( "\n" );
        }
    // out.print( "\n; " + forest.size() + " dialog expression" + ((forest.size() > 1) ? "s" : "") + " ok.  " + (in.linenumber-1) + " line"+ ((in.linenumber-1 > 1) ? "s" : "") +".\n" );
  }


  void curly( int c )
  { if (c != 0) out.print( "\tstone " + c + "\n" );
  }


  String generateexpr2Assembly( ParseNode r , String explicitlabel ) throws ErrorGeneratingCode // the int is the label number of the beast in assembly code.
  {
    String label;
    if ( r.type == ParseNode.ID       )
       {
         ParseNodeIdentifier rr = (ParseNodeIdentifier)r;
         char k = (string(symbols.get( rr.name ))).charAt(0);
         if (('a' <= k) && (k <= 'z')) 
                 label = labelforname( rr.name );                                // this is a word
            else label = makelabelfromstring( string(symbols.get( rr.name ) ) ); // this is a toplevel syntax name, so the name of a tree in the forest.
         if (explicitlabel != null)
            if (r.semantics != 0)
               {
                 out.print( (explicitlabel) + ": \n" +
                            "\tvisit 1 0.0 " + label + "\n"
                          );
                 curly( r.semantics );
                 out.print( "\treturn\n" );
               } else
               {
                 out.print( (explicitlabel) + ": \n" +
                            "\tgoto " + label + "\n"
                          );
               }
       } else
    if ( r.type == ParseNode.CHOICE   )
       {
         // out.print( "; CHOICE\n");
         ParseNodeChoice rr = (ParseNodeChoice)r;
  
         VectoR labels = new VectoR(); // of String
         VectoR rappas = new VectoR(); // of String
         int n = rr.list.size();
         for (int i = 0 ; i < n ; ++i) labels.add ( 
                                                    generateexpr2Assembly( ((ParseNode)(rr.list.get(i))) , null )
                                                  );
         boolean easy = true;
         for (int i = 0 ; i < n && easy ; ++i)
             easy &= (0 == ((ParseNode)(rr.list.get(i))).semantics );
         if ((n==1) && easy)  
            {
              out.print( "\t; EXPLORE HAS ONLY 1 AVENUE AND NO SEMANTICS\n" );
              out.print( (label = makelabelfromint( labelnumber++ ) ) + ":\n"  );
              out.print( "\tgoto " + labels.get(0) + "\n" );
            } else
            {
              if (easy) 
                 {
                   out.print( "\n\t; EASY DONE\n" );
                   out.print( (label = makelabelfromint( labelnumber++ ) ) + ":\n"  );
                   if (explicitlabel != null)
                      out.print( (explicitlabel) + ":\n" );
                   out.print( "\texplore " + n + " " );
                   for (int i = 0 ; i < n ; ++i)
                       out.print(
                                  labels.get(i) + " " +
                                  logify(1.0) + " "
                                );
                   out.print( "\n" );
                 } else
                 {
                   for (int i = 0 ; i < n ; ++i)
                       {
                         rappas.add ( makelabelfromint( labelnumber++ ) );
                         out.print( rappas.get(i) + ":\n" );
                         int s = ((ParseNode)(rr.list.get(i))).semantics ;
                         if (s != 0)
                            {
                              out.print( "\tvisit 1 0.0 " + (labels.get(i)) + "\n" );
                              curly ( s );
                              out.print( "\treturn\n" );
                            } else
                            {
                              out.print( "\tgoto " + (labels.get(i)) + "\n" );
                            }
                       }
       
                   out.print( (label = makelabelfromint( labelnumber++ ) ) + ":\n"  );
                   if (explicitlabel != null)
                      out.print( (explicitlabel) + ":\n" );
                   out.print( "\texplore " + n + " " );
                   for (int i = 0 ; i < n ; ++i)
                       out.print(
                                  rappas.get(i) + " " +
                                  logify(1.0) + " "
                                );
                   out.print( "\n" );
                 }
              curly( rr.semantics );
              out.print( "\treturn" );
            }
         if (n==1) out.print( "\t; EXPLORE HAD ONLY 1 AVENUE\n" );
         out.print( "\n" );
       } else
    if ( r.type == ParseNode.SEQUENCE )
       {
         // out.print( "; SEQUENCE\n");
         ParseNodeSequence rr = (ParseNodeSequence)r;
         VectoR labels = new VectoR(); // String
         int n = rr.list.size();
         for (int i = 0 ; i < n ; ++i) labels.add( generateexpr2Assembly( ((ParseNode)(rr.list.get(i))) , null ) );
         out.print( (label = makelabelfromint( labelnumber++ ) ) + ":\n" );
         if (explicitlabel != null)
            out.print( (explicitlabel) + ":\n" );
         boolean easy = true;
         for (int i = 0 ; (i < n) && easy ; ++i)
             {
               if (
                    ( ((ParseNode)(rr.list.get(i))).semantics ) != 0
                  ) easy = false;
             }
         if ((n==1) && easy)
            {
              out.print( "\t; VISIT HAS ONLY 1 AVENUE AND NO SEMANTICS\n" );
              out.print( "\tgoto " + (labels.get(0)) + "\n" );
            } else
            {
              if (! easy)
              {
                 for (int i = 0 ; i < n ; ++i)
                     {
                       out.print( "\tvisit 1 0.0 " + (labels.get(i)) + "\n" );
                       curly( ((ParseNode)(rr.list.get(i))).semantics );
                     }
              } else
              {
                 out.print( "\tvisit " + n + " 0.0 " );
                 for (int i = 0 ; i < n ; ++i)
                     {
                       out.print( (labels.get(i)) + " " );
                     //int c = ( ((ParseNode)(rr.list.get(i))).semantics );
                     //if (c != 0) out.print( "\tEEK stone " + c + "\n" );
                     }
                 out.print( "\n" );
              }
              curly( rr.semantics );
              out.print( "\treturn" );
            }
         if (n==1) out.print( "\t; VISIT HAD ONLY 1 AVENUE\n" );
         out.print( "\n" );
       } else
    if ( r.type == ParseNode.COUNT    )
       {
         // out.print( "; COUNT\n" );
         ParseNodeCount rr = (ParseNodeCount)r;
         ParseNode   x  = rr.node ;
         int         op = rr.op   ;
         String      sub;
         sub = generateexpr2Assembly( x , null );
   
         label = makelabelfromint( labelnumber++ );
         out.print( label + ":\t\t; count " + op + "\n" );
         if (explicitlabel != null)
            out.print( explicitlabel + ":\n" );
  
         // now 'char * label' is the start of this count in assembly code
         // and 'char * sub'   is the start of the assembly for the thing counted.
         // ? : 0 or 1    time
         // + : 1 or more times
         // * : 0 or more times
  
         switch(op)
           {
             case '?' : 
                        out.print( "\texplore 2 " + sub + " 0.0 " + " L" + labelnumber + " 0.0 " + "\n" );
                        out.print( "L" + (labelnumber++) + ":" );
                        break;
       
             case '+' : out.print( "\tvisit 1 0.0 " + sub + "\n" );
  
             case '*' : out.print( "\texplore 2 L" + labelnumber + " 0.0 " +"L" + (labelnumber + 1) + " 0.0 " + "\n");
                        out.print( "L" + (labelnumber++) + ":"  );
                        out.print( "\tvisittodeath " + sub + "\n" );
                        out.print( "L" + (labelnumber++) + ":"  );
                        break;
           }
  
         curly( rr.semantics   );
         out.print( "\treturn\n" );
       } else
    if ( r.type == ParseNode.TAG )
       {
         label = "TAG";
       } else
       {
         throw new ErrorGeneratingCode( ( "big error (node of type '" + r.string() + "')\n" ) ); 
       }
    return label;
  }


  // john jozwiak @ motorola 20010716afternoon


  void generateAssembly() throws ErrorGeneratingCode
  {
    namesandlabels = new VectoR(); // of String
    labelsandnames = new VectoR(); // of String
    visibilities   = new VectoR(); // of Boolean
    
    out.print( "@ ; assembly files as of 20010918 start with an 'ear' to clue in the compiler.\n" +
               "; Motorola Phonetics Processor assembly code\n" +
               "; version 20010918 : jozwiak and buhrke\n" + // "; version 20010612 : jozwiak and buhrke\n" +
               "\n" +
               "; code below is invoked at a start label 'startsymbol' by a driver which does:\n" +
               ";   clue NS\n" +
               ";   call startsymbol\n" +
               ";   clue NE\n" +
               ";   sil\n" +
               ";   exit\n\n" +
               "L0:\tclue 32\t; wrapup code\n" + // the unique thread that starts running the phonetic bytecode has 0 on its stack,
               "\t40\n"  +                       // so its descendent (or self) that finishes does the wrapupcode at L0 = address 0.
               "\t40\n"  +
               "\texit\n" 
             );
    
    ++(labelnumber);  // we always use L0 for the wrapup code (the inverse of crt0 in C, essentially).
    
    // for all words seen, generate their pronunciations.
    
    for ( int i = 0 ; i < symbols.size() ; ++i )
        {
          String word      = string(symbols.get(i));
          char   firstchar = word.charAt(0);
          if (('a' <= firstchar) && (firstchar <= 'z'))  // lowercase first letter is a word=terminal, not a nonterminal.
          {
             VectoR v;
             if (lexicon != null)
                v = lexicon.pronounce( word , false );
                else
                v = new VectoR();

             // limitation in the current java version is that only one pronunciations is used.

             int n = v.size();
             if (n == 0) System.out.println( "unpronounced : " + word );

             for ( int j = 0 ; j<n ; ++j )  // for each pronunciation
             {
               out.print( "L" + (labelnumber++) + ":" 
                              + "\tclue  2\t;" + word + "\n"
                        );
               VectoR vv = (VectoR)(v.get(j));  

               for ( int s = 0 ; s < vv.size() ; ++s )
                   {
                     String o = string(vv.get(s));
                     if (s+1 == vv.size())
                        out.print( "\tclue  1\n" + "\tstone " + i + "\n" );
                     out.print(   "\t" + Lexicon.intsbyphone.get( o ) + "\t; " + o + "\n");
                   }
 
               findandmaybeinsert( symbols , new String("L"+(new Integer(labelnumber-1)))
                                         );
               out.print( "\tsil\n"    );
               out.print( "\treturn\n" );
             }


             if (n == 1)
                {
                  namesandlabels . add ( word                              );
                  labelsandnames . add ( makelabelfromint( labelnumber-1 ) );
                } else
             if (n > 1)
                {
                  out.print( "L"
                             + (labelnumber++)
                             + ":\t\t; \""
                             + word 
                             + "\" has " 
                             + n  
                             + " pronunciation" 
                             + ( n>1 ? "s" : "")
                             + ".\n"
                           );
                  out.print( "\texplore " + n + " " );
                  for (int k = 1 ; k<=n ; ++k )
                      out.print( "L" + (labelnumber-n-2+k) + " " + 0.0 + " " );
                  out.print( "\nreturn\n" );
                  namesandlabels . add ( word                                );
                  labelsandnames . add ( makelabelfromint( labelnumber-1 ) );
                }
             else
                {
                  out.print( "; WORD WITH UNKNOWN PRONUNCIATION ENCOUNTERED: \"" + symbols.get(i) + "\"\n" );
                }
          }
        }
    // out.print( "\n;;;;;;;;\n\n" );
    
    // now print all the external syntax references.
    
    for (int i = 0 ; i < forest.size() ; ++i)
        {
          if (((ParseNode)(forest.get(i))).type == ParseNode.INCLUSION)
             out.print(   "; (include \""
                        + ( string( strings.get( ( (ParseNodeInclusion)(forest.get(i) )) . name  ) ) )
                        + "\" "
                        + ( ( ( (ParseNodeInclusion)(forest.get(i) )) . where ) == 1
                            ? "usingsearchpath"
                            : "fromcurrentdirectory"
                          )
                        + ")\n"
                      );
        }
    
    // now generate code for each top level named syntax.
    
    out.print( "\n; # of trees in the forest is " + forest.size() + ".\n\n" );

    for (int i = 0 ; i < forest.size() ; ++i)
        {
          ParseNode tree = (ParseNode)(forest.get(i));
          // out.print( "; forest[" + i + "] is a " + (tree.string()) + ".\n" );
          if (tree.type == ParseNode.DEFINITION)
             {
               ParseNodeDefinition def    = (ParseNodeDefinition)tree;
               String     s      = string( symbols.get( def.name ) );
               String     label;
    
               label = generateexpr2Assembly( 
                                              simplification( def.node )
                                            , makelabelfromstring( s )
                                            );
  
               // out.print( "\n; end of definition # " + i + " : '" + s + "' @ " + label + "\n\n" );

               namesandlabels . add( s                             );
               labelsandnames . add( label                         );
               visibilities   . add( new Boolean( def.visibility ) );
             }
        }
  
    out.print( "\texit\n" );  // this makes it extremely clear to the disassembler where the code ends.
    
    shownamesandlabels();
    
    for ( int i = 0 ; i < symbols.size() ; ++i )
          out.print( ";= stone " + i + " \"" + (string(symbols.get(i))) + "\"\n" );

    out.print( "\n; " + forest.size() + " dialog expression" + ((forest.size() > 1) ? "s" : "") + " ok.  " + (in.linenumber-1) + " line"+ ((in.linenumber-1 > 1) ? "s" : "") +".\n" );
  }


  String compilestringtostring( String source , byte target ) throws ErrorParsing , ErrorGeneratingCode
  {
    initialize();
    in                          = new Lexer( source );
    ByteArrayOutputStream outin = new ByteArrayOutputStream();
    out                         = new PrintStream( outin , true );
    docompile( target , "" );
    String s = 
           outin.toString()
           + "\n; " + forest.size() + " dialog expression" + ((forest.size() > 1) ? "s" : "") + " ok.  "
           + (in.linenumber-1) + " line"+ ((in.linenumber-1 > 1) ? "s" : "") +".\n" ;
    out.close();
    try {
    outin.close();
    } catch (IOException e) { System.out.println( "damned ByteArrayOutputStream is stuck:" + e ); };
    return s;
  }


  String compilefiletostring( String filename , byte target ) throws ErrorParsing , ErrorGeneratingCode
  {
    initialize();
    in                          = new Lexer( new File( filename ) );
    ByteArrayOutputStream outin = new ByteArrayOutputStream();
    out                         = new PrintStream( outin , true );
    docompile( target , "" );
    String ass = 
           outin.toString()
           + "\n; " + forest.size() + " dialog expression" + ((forest.size() > 1) ? "s" : "") + " ok.  "
           + (in.linenumber-1) + " line"+ ((in.linenumber-1 > 1) ? "s" : "") +".\n" ;
    out.close();
    try {
    outin.close();
    } catch (IOException e) { System.out.println( "damned ByteArrayOutputStream is stuck:" + e ); };
    return ass;
  }


  void compilefiletostdout( String filename , byte target ) throws ErrorParsing , ErrorGeneratingCode
  {
    initialize();
    in  = new Lexer( new File( filename ) );
    out = System.out;
    docompile( target , "" ); 
    out.print( "\n; " + forest.size() + " dialog expression" + ((forest.size() > 1) ? "s" : "") + " ok.  " + (in.linenumber-1) + " line"+ ((in.linenumber-1 > 1) ? "s" : "") +".\n" );
  }

  
  void docompile( byte target , String targetfilename ) throws ErrorParsing , ErrorGeneratingCode
  {
    if ((target == Converter.ASSEMBLY) && (lexicon == null))
       {
         // out.print( "; instantiating a new english dictionary, a single static instance.\n" );
         lexicon = new LexiconEnglish( "dictionary-english-raw" );
       }
    if      ('#' == in.peekaboo()) {
                                     abnf();
                                     if (target == Converter.DEFAULT) target =  Converter.ASSEMBLY;
                                   }
    else if ('@' == in.peekaboo()) {
                                     assembly();
                                     if (target == Converter.DEFAULT) target =  Converter.BINARY  ;
                                   }
    else                           {
                                     gsl();
                                     if (target == Converter.DEFAULT) target =  Converter.ASSEMBLY;
                                   }
         if (Converter.GSL      == target) generateGSL     ( abnfrootrule   );
    else if (Converter.ASSEMBLY == target) generateAssembly(                );
    else if (Converter.BINARY   == target) generateBinary  ( targetfilename );
  }


  void compilefiletofile( String filein , String fileout , byte target ) throws ErrorParsing , ErrorGeneratingCode
  {
    initialize();
    OutputStream outlet = null;
    try {
          in     = new Lexer( new File( filein ) );
          outlet = new FileOutputStream( fileout);
          out    = new PrintStream( outlet , true );
        } catch (IOException e) { System.out.println( "Yo, compilefiletofile failed. : " + e ); };
    docompile( target , fileout ); 
    try {
          outlet.flush();
          outlet.close();
        } catch (IOException e) { System.out.println( e ); };
    out.flush();
    out.close();
  }


  public void compileandassemble( String filename , String directory ) throws ErrorParsing , ErrorGeneratingCode
  {
    String o1 = Converter.createfilename( filename , directory , "assembly" );
    String o2 = Converter.createfilename( filename , directory , "binary"   );
    System.out.println( "compiling..." );         compilefiletofile ( filename , o1 , Converter.ASSEMBLY );
    System.out.println( "done.\nassembling..." ); compilefiletofile ( o1       , o2 , Converter.BINARY   );
    System.out.println( "done." );
  }


  public static String abnftogsl ( String s , String rootrule ) throws ErrorParsing , ErrorGeneratingCode
  { 
    Converter c = new Converter();
    return c.compilestringtostring( s , Converter.GSL );
  };

 
  static VectoR directoryslashfile( String  s )
  {
    VectoR r = new VectoR();
    int i;
    i = s.lastIndexOf( '/' );
    if ( i == s.length() )
       {
         r.add( s  );
         r.add( "" );
       } else
    if ( i == (-1) )
       {
         r.add( "" );
         r.add( s  );
       } else
       {
         r.add( s.substring( 0   , i          ) );
         r.add( s.substring( i+1 , s.length() ) );
       }

    return r;
  }

  static String createfilename( String old , String targetdirectory , String ending )
  {
    VectoR v = Converter.directoryslashfile( old );
    return targetdirectory + "/" + ((String)(v.get(1))) + "." + ending;
  }

  public static void main( String[] a )
  {
    if (a.length != 2)
       { System.out.println( "try: java Converter file targetdirectory" );
       } else
       {
         try {
               Converter c = new Converter();
               c.compileandassemble( a[0] , a[1] );

               // c.compilefiletostdout( a[0] , Converter.DEFAULT );
               // c.compilefiletofile( "/tmp/converter.assembly" , "/tmp/converter.binary"  , Converter.BINARY );
             } catch (Exception e) { System.err.println( e ); };
         System.exit(0);
       }
  }
};
