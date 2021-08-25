// john jozwiak at motorola speech labs 20010925 in the morning.

import java.io.*   ;
import java.util.* ;

class VirtualMachine
{
  static int unsign( byte i )
  {
    return ((i < 0) ? 128 : 0) + (i&127);
  }


  static int readint( InputStream binary )
  { 
    try
    {
      byte i[] = {0,0,0,0};
      binary.read( i );
      int eye = ( VirtualMachine.unsign(i[0]) * 256 * 256 * 256 )
              + ( VirtualMachine.unsign(i[1]) * 256 * 256       )
              + ( VirtualMachine.unsign(i[2]) * 256             )
              + ( VirtualMachine.unsign(i[3])                   )
              ;
      return eye;
    } catch (IOException e) { System.out.println( e ); return (-1); }
  }


  static int readchar( InputStream binary )
  {
    try
    {
      byte i[] = {0};
      binary.read( i );
      char eye = ((char)(i[0]));
      return eye;
    } catch (IOException e) { System.out.println( e ); return (-1); }
  }


  static VectoR  //  =  [ TwoGigArrayOfint code , Hashtable symboltable , Hashtable stonestrings ]
  readbinary( String filename )
  {
    try
    {
      FileInputStream binary  = new FileInputStream( filename );
      TwoGigArrayOfint  code  = new TwoGigArrayOfint();
      Hashtable       symbols = new Hashtable();
      Hashtable       strings = new Hashtable();

      // System.out.print( "reading '" + filename + "'" );

      int codesize = readint(binary);
      // System.out.println( "( " + codesize + " integers in code section ) :" );
      boolean keepontruckin = true;
      for (int i = 0 ; i<codesize ; ++i)
          {
            code.append ( readint(binary) );
          }

      int numberofsymbols = readint(binary);
      for (int i = 0 ; i<numberofsymbols ; ++i)
          {
            StringBuffer s = new StringBuffer();
            int where      = readint(binary);
            // System.out.print( "@ " + where + " is '" );
            for (int k=0 ; k<32 ; ++k)
                {
                  char c = (char)(readchar(binary));
                  if (c != 0) s.append(c);
                }
            symbols.put( s.toString() , new Integer(where) );
            // System.out.print( (s.toString()) + "'\n" );
          }

      int numberofstrings = readint(binary);
      for (int i = 0 ; i<numberofstrings ; ++i)
          {
            StringBuffer s = new StringBuffer();
            int where      = readint(binary);
            // System.out.print( "@ " + where + " is '" );
            for (int k=0 ; k<32 ; ++k)
                {
                  char c = (char)(readchar(binary));
                  if (c != 0) s.append(c);
                }
            strings.put( s.toString() , new Integer(where) );
            // System.out.print( (s.toString()) + "'\n" );
          }

      VectoR rv = new VectoR();
      rv.add( code    );
      rv.add( symbols );
      rv.add( strings );
      return rv;
    }
    catch (IOException e) { System.out.println( e );  return null;}
  }


  static void disassemble( String filename )
  {
    try
    {
      FileInputStream binary = new FileInputStream( filename );

      System.out.print( "disassembling '" + filename + "'" );

      int a        = 0;
      int codesize = readint(binary);
      System.out.println( "( " + codesize + " integers in code section ) :" );
      boolean keepontruckin = true;
      while ( keepontruckin )
      {
        int opcode = readint(binary); 
        if (a != 4 && opcode == 49) { System.out.println( a + ":\t" + "exit" ); a +=1; keepontruckin = false; continue; }
        if ((0      <= opcode) && 
            (opcode <= 42    )){ System.out.println( a + ":\t" + "(" + Lexicon.phonesbyint[opcode] + ")\t\t; " + opcode    ); a += 1; }
        else if (43 == opcode) { System.out.println( a + ":\t" + "return "                                                 ); a += 1; }
        else if (45 == opcode) { System.out.println( a + ":\t" + "stone "     + readint(binary)                            ); a += 2; }
        else if (46 == opcode) {
                                 int ways = readint(binary);
                                 System.out.print( a + ":\t" + "explore " + ways );
                                 for (int w=0;w<ways;++w) 
                                     System.out.print( " " + readint(binary) + " " + readint(binary) );
                                 a += (2*ways + 2);
                                 System.out.print("\n");
                               }
        else if (47 == opcode) {
                                 int ways = readint(binary);
                                 int cost = readint(binary);
                                 System.out.print( a + ":\t" + "visit " + ways + " " + cost );
                                 for (int w=0;w<ways;++w) 
                                     System.out.print( " " + readint(binary) );
                                 a += (ways + 3);
                                 System.out.print("\n");
                               }
  //old else if (47 == opcode) { System.out.println( a + ":\t" + "visit "        + readint(binary) + " " + readint(binary) );  a += 3; }
        else if (48 == opcode) { System.out.println( a + ":\t" + "clue  "        + readint(binary)                         );  a += 2; }
        else if (49 == opcode) { System.out.println( a + ":\t" + "exit"                                                    );  a += 1; }
        else if (50 == opcode) { System.out.println( a + ":\t" + "goto  "        + readint(binary)                         );  a += 2; }
        else if (51 == opcode) { System.out.println( a + ":\t" + "visittodeath " + readint(binary)                         );  a += 2; }
        else                   { System.out.println( a + ":\t" + opcode + " is erroneous:  this binary is thus nonsense."  );  keepontruckin = false; }
      }
      int symbols = readint(binary);
      for (int i = 0 ; i<symbols ; ++i)
      {
        System.out.print( "@ " + readint(binary) + " is '" );
        for (int k=0 ; k<32 ; ++k)
            {
              char c = (char)(readchar(binary));
              if (c != 0) System.out.print( c );
            }
        System.out.print( "'\n" );
      }
    }
    catch (IOException e) { System.out.println( e ); }
  }


  static VectoR forensics( String filename , int[] ips , String startlabel )
  {
    // System.out.print( "forensics analysis on '" + filename + "'" + " (" + startlabel + ") " );
    // for (int i=0 ; i<ips.length ; ++i)
    //     System.out.print( ips[i] + " " );
    // System.out.print( "\n" );

    Stack   stack  = new Stack () ;
    VectoR  stones = new VectoR() ;
    boolean live   = true         ;
    int     ip     = 46           ;
    int     kip    = 1            ;
    int     i                     ;

    VectoR           binary       = readbinary( filename );
    TwoGigArrayOfint instructions = (TwoGigArrayOfint)(binary.get(0));
    Hashtable        symboltable  = (Hashtable       )(binary.get(1));
    stack.push( new Integer(0) );
    // System.out.print("\nforensics abstract interpretation to recover stones and curlies:\n " );
    while(live)
    {
      if(ip<0 ) {
                  live = false;
                  ip   = (-ip);
                }
      int code = instructions.get( ip );
      if (   (0    <= code)
          && (code <= 42  )
         )                {
                            ++(ip);
                          }
      else if (43 == code){ // return N
                            ip = ((Integer)(stack.pop())).intValue(); 
                          }
      else if (45 == code){ // stone N = shiftin N
                            int x = instructions.get( 1+ip );
                            // System.out.print( "stone " + x + "\n" );
                            stones.add( new Integer(x) );
                            ip +=2;
                          }
      else if (46 == code){ // explore N label1 weight1 ... labelN weightN
                            int N = instructions.get(1+ip);
                            // System.out.print("explore ");
                            // for (i=0;i<N;++i) 
                            //     System.out.print( " " + instructions.get(1+ip + 1+2*(i)) );
                            // System.out.print("\n");
                            stack.push( new Integer( ip + 2*(N+1) ) );
                            boolean ok = false;
                            for (i=0 ; i<N && !ok ; ++i) 
                                ok |= ( instructions.get(1+ip+1+2*i) == ips[kip] );
                            if (!ok) 
                               System.out.print("^^^^FORENSIC FAILURE @ explore^^^^\n");
                            ip = ips[kip++];
                          }
      else if (47 == code){ // visit N weight label1 ... labelN
                            int N    = instructions.get(1+ip);
                            int cost = instructions.get(2+ip);

                            // System.out.print( "visit " + N + " " + cost + " ");
                            // now push the following on the stack in this order:  
                            //    (address of next instruction) 
                            //    labelN 
                            //    .
                            //    .
                            //    .
                            //    label2
                            // and then jump to label1, so the return from label1 does a goto to label2,
                            // and so on through labelN.  
                            // (eric thought of that nice way of realizing this version of this instruction,
                            //  rather than a brute force pushing and popping of stacks thing.)

                            for (i=N;i>1;--i)
                                {
                                  // System.out.print( "(pushing " + instructions.get(ip + 2 + i) + " ) " );
                                  stack.push( 
                                               new Integer( instructions.get(ip + 2 + i) )
                                            );
                                }
                            // System.out.print( "(pushing " + (ip + 2 + N + 1) + " ) " );
                            stack.push( new Integer( ip + 2 + N + 1 ) );

                            // System.out.print( "jumping to " + instructions.get( ip + 3) + "\n" );
                            ip = instructions.get( ip + 3 );
                          }
      else if (48 == code){ // clue N  (used for anything, currently as clues for the recognizer's use)
                            ip += 2;
                          }
      else if (49 == code){ // exit
                            live = false;
                          }
      else if (50 == code){ // goto label
                            ip = instructions.get(1+(ip));
                          }
      else if (51 == code){ // visit to death
                            System.out.print("VISITTODEATH FORENSICS IS NOT YET TESTED, HOWEVER IS ONLY 1 LINE LONG\n");
                            stack.push( new Integer(ip = instructions.get(1+(ip))) );
                          }
    }
    return stones;
  }


  static String symbolataddress ( Hashtable symboltable , int address )
  {
    Enumeration symbols = symboltable.keys();
    while (symbols.hasMoreElements())
      {
        Object o = symbols.nextElement();
        if (
             ((Integer)(symboltable.get(o))).intValue() 
             ==
             address
           ) return (String)o;
      }
    return "";
  }


  static boolean isthissymboltoplevel ( Hashtable symboltable , int address )
  {
    // the symboltable only holds the toplevel symbols, which arose as named syntaxes in GSL (or whatever).
    return (symbolataddress( symboltable , address ) != "");
  }


  static VectoR reallycloneIntegerVectoR( VectoR v )
  {
    VectoR r = new VectoR();
    for (int i = 0 ; i<v.size() ; ++i)
        r.add( new Integer( ((Integer)(v.get(i))).intValue() ) );
    return r;
  }


  static boolean nonterminalsilentlyreachable( TwoGigArrayOfint instructions , VectoR context , int ip )
  {
    VectoR c = reallycloneIntegerVectoR(context);
    int       symboladdress = ((Integer)(c.get(0))).intValue();
    int       startaddress  = ip ; // ((Integer)(c.get(c.size()-1))).intValue();
    System.out.print( "symbolseenbeforesound( instructions , " + c + " )\n" );
    boolean   live          = true            ;
    boolean   lr            = false           ;
    Hashtable rez           = new Hashtable() ;
    while(live && (ip > 0) && !lr)
    {
      int code = instructions.get( ip );
      if (    (0    <= code)
           && (code <= 42  )
         )                 /* sound   */        { live = false;                             }
      else if (43 == code) /* return  */        { live = false;                             }
      else if (45 == code) /* stone N */        { ip += 2;                                  }
      else if (48 == code) /* clue  N */        { ip += 2;                                  }
      else if (49 == code) /* exit    */        { live = false;                             }
      else if (50 == code) /* goto  N */        { ip = instructions.get(1+(ip));            
                                                  if (c.indexOf( new Integer(ip) ) != (-1))
                                                     {
                                                       System.out.print( "loop detected (to " + ip + "), so truncating search with context = " + c + " .\n" );
                                                       live = false;
                                                     }
                                                  // c.add( new Integer(ip) );
                                                }
      else if (51 == code) /* visittodeath N */ { live = false;
                                                  // c.add( new Integer(ip+2) );
                                                  lr   = nonterminalsilentlyreachable( instructions , c , ip+2 );
                                                }
      else if((46 == code) // explore N        label1 weight1 ... labelN weightN
            ||(47 == code) // visit   N weight label1         ... labelN
             )             { int        N = instructions.get(1+ip);
                             int[] labels = new int[N];
                             for (int i=0 ; i<N ; ++i) 
                                 labels[i] = (46 == code) ? instructions.get(1+ip+1+2*i) // explore
                                                          : instructions.get(2+ip+1+i)   // visit
                                                          ;
                             for (int i=0 ; i<N && live ; ++i) 
                                 {
                                   int s = labels[i];
                                   if (c.indexOf( new Integer(s) ) != (-1))
                                      {
                                        System.out.print( "loop detected to " + s + " in context " + c + " .\n" );
                                        live = false;
                                        lr = true;
                                      } else
                                      {
                                        // c.add( new Integer(s) );
                                        boolean r = nonterminalsilentlyreachable( instructions , c , s);
                                        // System.out.print( "blah blah " + s + " .\n" );
                                        if (r) { c.remove(c.size()-1) ;  lr = r; live = false; };
                                      }
                                 }
                             if (46 == code) ip = instructions.get( ip + 1 + 2*N + 1 );
                                else         ip = instructions.get( ip + 2 + N + 1 );
                             lr = false;
                           }
    }
    return lr;
  }


  static boolean containsanundecided( Hashtable h )
  {
    boolean     r       = false;
    Enumeration symbols = h.keys() ; // Strings
    while (!r && symbols.hasMoreElements())
          {
            Object o = symbols.nextElement();
            if ("undecided" == ((String)(h.get(o))))
               r = true;
          }
    return r;
  }


  static void leftrecursive ( String filename )
  {
    VectoR           binary          = readbinary( filename )            ;
    TwoGigArrayOfint instructions    = (TwoGigArrayOfint)(binary.get(0)) ;
    Hashtable        symboltable     = (Hashtable       )(binary.get(1)) ; // pairs of ( String symbol , Integer address )

    Hashtable        recursivenesses = new Hashtable()                   ; // pairs of ( String symbol , Boolean leftrecursive )
    Enumeration      symbols         = symboltable.keys()                ; // Strings
    while (symbols.hasMoreElements())
      {
        Object o = symbols.nextElement();
        recursivenesses.put( o , "undecided" );
      }

    // now for every symbol s 
    // symboltable     holds (s , address of binary code for s)
    // recursivenesses holds (s , "undecided"                 )

    System.out.print( containsanundecided(recursivenesses) + " is the value of 'recursivenesses contains an undecided'.\n" );

    while (containsanundecided(recursivenesses))
      {
        symbols = recursivenesses.keys() ;
        while (symbols.hasMoreElements())
          {
            Object o = symbols.nextElement();
            if (((String)(recursivenesses.get(o))).equals("undecided"))
               {
                 System.out.print( "'" + o + "' ...\n" );
                 VectoR  context = new VectoR();
                 context.add( symboltable.get(o) );
                 boolean yesorno = nonterminalsilentlyreachable ( instructions , context , ((Integer)(symboltable.get(o))).intValue() );
                 recursivenesses.put( o , (yesorno ? "yes" : "no") );
               }
          }
      }
  
    symbols = recursivenesses.keys();
    while (symbols.hasMoreElements())
      {
        Object o = symbols.nextElement();
        if (((Boolean)(recursivenesses.get(o))).booleanValue())
             System.out.print( "true  '" + o + "'\n" );
        else System.out.print( "false '" + o + "'\n" );
      }
  }


  static void main( String[] args )
  {
    if      (args.length > 0  && args[0].equals( "forensics"     )) {
                                                  System.out.print( "\n  VirtualMachine.forensics( String filename , int[] ips , String startlabel )\n\nis only callable as a static call.\n" );
                                                  System.out.print( "\nHOWEVER,\n" );
                                                  int ips[] = {46,15};
                                                  VectoR ef = forensics( "tmp/hayiris.gsl.binary" , ips , "HayIris" );
                                                  System.out.print( ef + " = VirtualMachine.forensics( \"tmp/hayiris.gsl.binary\" , {46,15} , \"HayIris\" ) .\n" );
                                                }
    else if (args.length == 2 && args[0].equals( "leftrecursive" )) {
                                                  VirtualMachine.leftrecursive ( args[1] );
                                                }
    else if (args.length == 2 && args[0].equals( "disassemble"   )) {
                                                  VirtualMachine.disassemble   ( args[1] );
                                                }
    else System.out.println( "What?  Try one of the following, replacing filename with the name of a file.\n" + 
                             "\n  java VirtualMachine forensics     filename\n" +
                             "\n  java VirtualMachine disassemble   filename\n" +
                             "\n  java VirtualMachine leftrecursive filename\n" 
                           );
  }
};
