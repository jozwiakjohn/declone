// john jozwiak @ motorola 20010816


import java.io.*           ;
import java.util.Hashtable ;


public class LexiconEnglish extends Lexicon
{
  PushbackReader          in         ;
  public static Hashtable words      ;
  public int              linenumber ;


  public int getc()
  {
    int k;
    try { k = in.read();
        } catch (IOException e) { k='\0'; System.out.println( "read of english lexicon failed.  Oh shit." ); }
    return k;
  }


  public void ungetc( int k )
  {
    try { in.unread( k );
        } catch (IOException e) { System.out.println( "ungetc( " + ((char)k) + " ) on an exhausted pushback buffer." );
                                  System.exit(1);
                                }
  }


  public int peekaboo()
  {
    int k;
    ungetc( (k = getc()) );  
    return (k); 
  }


  void chew()  // eats nonnewline blankspace, which is not whitespace on screens like mine which are black.
  {
    int k;
    while ( (k=getc()) == ' ' );
    ungetc(k);  // put back the character which ended the loop above.
  }


  String readastring ()
  {
    int k;
    StringBuffer buffy = new StringBuffer();
    if ('(' != (k = getc())) { System.out.print( "string left delimiter wrong on character '" + k + "'.\n" );
                               System.exit(1);
                             };
    while ((k=getc()) != ')') buffy.append(((char)k));
    return (buffy.toString());
  }
  
  
  VectoR readaline()
  {
    VectoR v = new VectoR();
    chew();
    while ( peekaboo() == '(' ) /* ')' later */
       {
          v.add( readastring() ); 
          chew();
       }
    if (peekaboo() == '\n') { getc(); ++linenumber; }
    // System.out.println(v);
    if (v.size() > 0)
       {
         byte[] phonemes = new byte[v.size()-1];
         for ( int i = 1 ; i<v.size() ; ++i )  // replace the phonemes by their corresponding integers
             phonemes[i-1] = (byte)(((Integer)(intsbyphone.get( v.get(i) ))).intValue());
         // System.out.println(v);
         VectoR w = new VectoR();
         w.add( v.get(0) );
         w.add( phonemes );
         v = w;
       }
    // System.out.println(v);
    return v;
  }


  void loadlexicon()
  {
    boolean done = false;
    chew();
    while ( !done && ( peekaboo() != (65535) ) )
          {
            VectoR e = readaline();
            if (e.size() > 0)
               {
                  // we have a real word pronunciation, which may or may not have an associated VectoR of such in 'words' already. 
                  VectoR w = (VectoR)(words.get(e.get(0)));
                  if (w==null) w = new VectoR();
                  w.add(e.get(1));
                  words.put(e.get(0),w);
               }
            else done = true;
          }
    // System.out.print("read "+linenumber+" lines.\n");
  }


  public LexiconEnglish( String file )
  {
    try 
    { in = new PushbackReader(   new BufferedReader( 
                                                     new FileReader( file )
                                                   )
                               , 10
                             ) ;
    } catch (IOException e) { System.out.println( "lexicon (english) could not open the file " + file + " for reading, damn it." ); }
    linenumber = 0;
    words = new Hashtable();
    loadlexicon();
  }


  VectoR lookup    ( String word , String accent )
  {
    VectoR w = (VectoR)(words.get(word));
    if (w==null) return new VectoR();
       else      return w;
  }


  VectoR pronounce ( String word , String accent )
  {
    // System.out.println( "; diag lexicon pronounce : '" + word + "'" );
    VectoR w  = lookup( word , "" );
    VectoR ww = new VectoR();
    for (int v=0 ; v<w.size() ; ++v)
        {
          byte[] s = (byte[])(w.get(v));
          VectoR ss = new VectoR();
          for (int i=0 ; i<s.length ; ++i)
              {
                String t = phonesbyint[ (int)s[i] ]; // System.out.println( "; diag lexicon pronounce : '" + t + "'" );
                while (t.charAt(0) == ' ') t = t.substring(1);
                ss.add( t );
              }
          ww.add(ss);
        }
    return ww;
  }


  VectoR pronounce  ( String word , boolean verbosity )
  {
    if (verbosity) System.out.print( "lexicon: " + word + "\t" );
    VectoR w = pronounce( word , "en-us" );
    if (verbosity) System.out.print( w    + "\n" );
    return w;
  }


  public static void main ( String args[] )
  {
    System.out.print  ( "\nnow instantiating and reading the static english lexicon..." );
    LexiconEnglish funkwagnalls = new LexiconEnglish( "dictionary-english-raw" );
    System.out.println( "done.\n" );
    funkwagnalls.pronounce( "alicia" , true );
    funkwagnalls.pronounce( "was"    , true );
    funkwagnalls.pronounce( "lovely" , true );
    funkwagnalls.pronounce( "years"  , true );
    funkwagnalls.pronounce( "ago"    , true );
    System.out.print  ( "\nnow silently looking up the above sequence two hundred thousand times (= a million pronunciation generations)..." );
    for (int i=0 ; i<200000 ; ++i)
        {
          funkwagnalls.pronounce( "alicia" , false );
          funkwagnalls.pronounce( "was"    , false );
          funkwagnalls.pronounce( "lovely" , false );
          funkwagnalls.pronounce( "years"  , false );
          funkwagnalls.pronounce( "ago"    , false );
        }
    System.out.println( "done." );
  }
};
