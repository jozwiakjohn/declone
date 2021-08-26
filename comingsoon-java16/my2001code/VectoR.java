// john jozwiak at motorola speech fiasco october 12 2001 ; this lets code run on older JREs.

import java.util.Vector;  

public class VectoR extends Vector
{
   public Object  get( int index )        { return super.elementAt( index );      };
   public boolean add( Object e  )        { super.addElement( e );   return true; };
   public Object  set( int i , Object e ) { super.setElementAt(e,i); return null; };
};
