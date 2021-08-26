///////////////////////////////////////////////////////////////
//                                                           //
// john jozwiak @ motorola speech labs july 30 2001.         //
// this takes .o files from the assembler and reconciles     //
// references among them, yielding a relocatable single .o   //
// which has no external references and starts at address 0. //
//                                                           //
///////////////////////////////////////////////////////////////

#include <stdio.h>
#include <math.h>
#include "Vektor.h"    // template of dynamically expanding vector classes.
#include "pronunciations/pronunciations.h"


FILE * binaryfile ;
FILE * f          ;


void binaryprintint   ( int   x )  { fwrite(&x,sizeof(int  ),1,binaryfile); };
void binaryprintfloat ( float x )  { fwrite(&x,sizeof(float),1,binaryfile); };
void binaryprintchar  ( char  x )  { fwrite(&x,sizeof(char ),1,binaryfile); };


int main( int argc , char ** argv )
{
  if  (argc > 1)
  {
    char * s = argv[1],
         * ss= NULL   ;
    int    l = strlen(s);
    f = fopen(s,"r");
    if ( 
         ('.' == s[l-2]) &&
         ('s' == s[l-1])
       )
       {
         s[l-2] = '\0';
         l = strlen(s);
       }
    ss = (char *)calloc(l + 20,sizeof(char));

    snprintf( ss , l+20 , "%s.o"       , s );
    binaryfile = fopen( ss , "wb" );

    fclose(binaryfile);
    fclose(f);

    return 0;
  } else
  {
    printf( "it invokes the assembler with a filename.  it does this whenever it is told.\n" );
    return 1;
  }
}
