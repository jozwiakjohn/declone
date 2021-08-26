// john jozwiak @ motorola 20011024


import java.io.*           ;
import java.util.Hashtable ;


public class TwoGigArrayOfint
{
  public static Hashtable ints   ;
  public int              length ;

  public TwoGigArrayOfint()
  {
    ints   = new Hashtable();
    length = 0;
  }

  int length()
  {
    return (this.length);
  }

  int get( int index )
  {
    return ((Integer)(ints.get( (new Integer(index)) ))).intValue();
  }

  void set( int index , int v )
  {
    ints.put( (new Integer(index)) , (new Integer(v)) );
  }

  void append( int v )
  {
    set( this.length , v );
    ++length;
  }
};
