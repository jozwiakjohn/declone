// john jozwiak @ motorola 20011004 afternoon.

import java.util.*;

class LongArray
{
  int    numberofchunks ;  // each chunk is a Vector of Object
  Object undefined      ;  // the thing to use for undefined elements
  VectoR v              ;  // a VectoR of Vectors of Object

  LongArray()
  {
    numberofchunks = 0   ;
    undefined      = null;
    v              = new VectoR();
    for (int i=0;i<65535;++i) v.add(undefined);
  }
  LongArray( Object nil )
  {
    numberofchunks = 0   ;
    undefined      = nil ;
    v              = new VectoR();
    for (int i=0;i<65535;++i) v.add(undefined);
  }

  void initializeachunk( int c )
  {
    v.set(c, new VectoR() );
    for (int i=0;i<65535;++i) ((VectoR)(v.get(c))).add(undefined);
  }

  void ensureexistenceofchunksthrough( int c )
  {
    for( int i = 0 ; i<=c ; ++i ) initializeachunk( i ); 
  }

  Object get( int i )
  {
    int y = i % 65535;
    int x = (i - y) / 65535;
    ensureexistenceofchunksthrough( x );
    // System.out.println( "retrieving element " + i + " at (" + x + "," + (y) + ")" );
    return ((VectoR)(v.get(x))).get(y);
  }

  void   set( int i , Object o )
  {
    int y = i % 65535;
    int x = (i - y) / 65535;
    ensureexistenceofchunksthrough( x );
    ((VectoR)(v.get(x))).set(y,o);
  }

  void  append( Object o )
  {
    set( length() , o );
  }

  void add( Object o )
  {
    append( o );
  }

  int length()
  {
    return   65536 * (numberofchunks - 1) 
           + ((VectoR)(v.get(numberofchunks-1))).size();
  }


  static void main( String[] a )
  {
    int i;
    LongArray l = new LongArray();
    for (i=0     ; i<10   ; i++) l.get(i); 
    for (i=65530 ; i<65540; i++) l.get(i); 
  };
}
