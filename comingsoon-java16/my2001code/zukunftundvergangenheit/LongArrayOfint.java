// john jozwiak @ motorola 20011005 afternoon.


class LongArrayOfint extends LongArray
{
  LongArrayOfint()
  {
    super();
  }
  LongArrayOfint( Object nil )
  {
    super(nil);
  }

  int  getint( int i )
  {
    return ((Integer)(super.get(i))).intValue();
  }

  void setint( int i , int o )
  {
    super.set( i, new Integer(o) );
  }

  void append( int i )
  {
    append( new Integer(i) );
  }

  void add( int i )
  {
    append( new Integer(i) );
  }
}
