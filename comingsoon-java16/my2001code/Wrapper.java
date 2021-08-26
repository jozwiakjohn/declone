// john jozwiak at motorola speech labs 20011022 in the afternoon

import java.io.*   ;
import java.util.* ;

class Wrapper
{
  static int[] ipsforhayiris = {46,15};

  static void main( String[] args )
  {
    if (args.length > 0)
         {
           System.out.print( "\n  Wrapper.forensics( String filename , int[] ips , String startlabel )\n\nis only callable as a static call.\n" );
         }
    else {
           System.out.print( "\nWhat?  Try \"java Wrapper filename\" where filename is the name of the syntax to use in recognition.\n\n" );
         }
  }
};
