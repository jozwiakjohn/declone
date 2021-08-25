// john jozwiak @ motorola 20010817


import java.io.*;


class Lexer
{
  PushbackReader in;
  BufferedWriter out;
  String         name;
  public int     linenumber;


  public Lexer( File file )
  {
    int k;
    this.name = name;
    try 
    {
      in = new PushbackReader(
                                new BufferedReader( 
                                                    new FileReader( file )
                                                  )
                              , 10
                             ) ;
    } catch (IOException e) { System.out.println( "could not open the file " + name + " for reading, damn it." ); }

    out = new BufferedWriter( new PrintWriter( System.out ) );

    linenumber = 1;
  }


  public Lexer( String string )
  {
    int k;
    this.name = name;
    in = new PushbackReader(
                              new BufferedReader( 
                                                  new StringReader( string )
                                                )
                            , 10
                           ) ;

    out = new BufferedWriter( new PrintWriter( System.out ) );

    linenumber = 1;
  }


  public int getc()
  {
    int k;
    try
    { k = in.read();
    } catch (IOException e) { k='\0'; System.out.println( "read on " + name + " failed.  Oh shit." ); }
    return k;
  }


  public void ungetc( int k )
  {
    try
    { in.unread( k );
    } catch (IOException e) { System.out.println( "ungetc( " + ((char)k) + " ) on an exhausted pushback buffer." );
                              System.exit(1);
                            }
  }


  public char peekaboo()
  { 
    int k = getc();
    ungetc(k);  
    if (k == (-1)) System.out.print("Lexer detected EOF on character '"+((char)k)+"' = integer "+k+"\n");
    return ((char)k); 
  }


  void yum()    // used in reading gsl and in reading phonetic assembly.
  {             // eats blankspace, which is not whitespace on screens like mine which are black.
    int k;
    while ((k=getc()) == ' ' | k == '\t' | k == '\n' | k == '\r' | k == '\f' | k == ';' )
          {
             if (k=='\n') ++linenumber;
             if (k==';' ) {
                            while ((k=getc()) != '\n');   // just ate a comment.  yum.
                            ++linenumber;
                          };
          };
    ungetc(k);  // put back the character which ended the loop above.
  }


  public void cat()
  {
    int k;
    k = getc();
    while (k != (-1))
    {
      System.out.print( ((char)k) );
      k = getc();
    }
  }


  int line()
  {
    return linenumber;
  }


  void yum_abnf()
  {             // eats blankspace, which is not whitespace on screens like mine which are black.
    int k;
    while ((k=getc()) == ' ' | k == '\t' | k == '\n' | k == '\r' | k == '\f' | k == '/' )
          {
             if (k=='\n') ++linenumber;
             if (k=='/' ) { // this could start a comment, or could start a weight like /5/ .
                            k = getc();
                            if      (k == '/')  // this is a C++ comment.
                               {
                                 while ((k=getc()) != '\n') ;
                                 ++linenumber;
                               }
                            else if (k == '*')  // this is a C (could also be javadoc) comment.
                               {
                                 boolean done = false;
                                 if (peekaboo() == '*') 
                                    {
                                       getc();     //     * ... */ = javadoc
                                                    // OR  */       = C 
                                       if (peekaboo() == '/')
                                          {
                                            getc();  // ... the first asterisk from the end of C-style comment.
                                            done = true;
                                          }
                                    }
                                 if (!done) // ... */  = a C-style comment to eat to the end.
                                    {
                                      while (!done)
                                        {
                                            while ((k=getc()) != '*')  if (k=='\n') ++linenumber;  // just ate a comment.  yum.
                                            if ((k=getc())== '/')
                                               done = true;
                                               else
                                               if (k=='\n') ++linenumber;
                                        }
                                    }
                               }
                            else 
                               {
                                 // fprintf( out , "/* at line %d i see an abnf weight start \"/%c\"*/\n" , linenumber , k );
                                 ungetc( k );
                                 ungetc('/');
                                 return;
                               };
                          };
          };
    ungetc(k); // put back the character which ended the loop above.
  }
};
