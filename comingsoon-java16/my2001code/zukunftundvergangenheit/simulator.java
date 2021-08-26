// john jozwiak at motorola:  the phonetics processor (eventually could get laid (out in hardware, that is)).
// 20010601 14:04pm
// 20010511 16:48pm
// 20010517 12:31pm

// todo : deC++ify -> send to eric 
//        assembler labels correct
//        agree on some 'api' with ying
//        semantics hack
//        get gsl->assembly->binary invocable as a single invocation, not curried two invocations.

#include <stdio.h>
#include <stdlib.h>

// #include "theoutput/z-handcodeds/iris.program.C"  // this is an ad-hoc explicit program being loaded.
#include "theoutput/z-handcodeds/hayiris.program.C"  // this is an ad-hoc explicit program being loaded.
// #include "theoutput/z-handcodeds/hithere.program.C"  // this is an ad-hoc explicit program being loaded.

#include "pronunciations/pronunciations.h"
#define STACKSIZE (100)
#define WINDOWLENGTH (3)
#define gimmeanew( type ) ((type*)calloc(1,sizeof(type)))
#define recycle(   pntr ) (free(pntr))
#define SIMULATOR


typedef struct intcactus
        {
          int                i   ;
          struct intcactus * pop ;
        }
        Cactus;


int globalintarray           [ 16384 ] ;
int globalintarrayfirstfree            ;


#ifdef SIMULATOR
int          globalthreadcount ;
#endif SIMULATOR


typedef struct thread
        {
          int              id                     ,
                           ip                     , // instruction pointer = address of an integer.
                           live                   ,
                           tp    [ WINDOWLENGTH ] , // triphone (started at 3 as the hardcoded windowlength).
                           window[ WINDOWLENGTH ] , // words seen lately, useful for indexing language statistics.
                           ev    [ WINDOWLENGTH ] , // nodetype 
                           hc    [ WINDOWLENGTH ] , // handicap = penalty register.
                           semantics              ; // offset into globalintarray of sequence of ints ending with 0.
          Cactus         * stack                  ;
          Cactus         * rogue                  ; // push sets to stack, ear opcode sets to NULL, pop with rogue==stack sets live to 0 to forbid '[ () () ] {return "bogus input"}'.
          struct hmmshell* origin                 ;
          struct thread  * nextinhmm              ; // not used here, used externally when this code is used in erics code.
        } Thread;


Thread * clonethread( Thread * th )
{
  Thread * k;
  k            = gimmeanew(Thread);
  k->id        = th->id        ;
  k->ip        = th->ip        ;
  k->live      = 0             ;

  for (register int i=0 ; i < WINDOWLENGTH ; ++i )
    {
      k->tp[i] = th->tp[i]     ;
      k->ev[i] = th->ev[i]     ;
      k->hc[i] = th->hc[i]     ;
    }

  k->semantics = th->semantics ;
  k->stack     = th->stack     ;
  k->rogue     = th->rogue     ;
  k->origin    = th->origin    ;
  k->nextinhmm = NULL          ;
  return k;
}


Thread * createthread( int startataddress )
{
  Thread * k;
  k            = gimmeanew(Thread);
  k->id        = (globalthreadcount)++;
  k->ip        = startataddress;
  k->live      = 1             ;

  for (register int i=0 ; i < WINDOWLENGTH ; ++i )
    {
      k->tp[i] = 39 ;
      k->ev[i] =  0 ;
      k->hc[i] =  0 ;
    }

  k->semantics = (-1) ; // nothing yet.
  k->stack     = NULL ;
  k->rogue     = NULL ; // i.e., k->stack
  k->origin    = NULL ;
  k->nextinhmm = NULL ;
  return k;
}


typedef struct threadlist
        { 
          Thread            * th   ;
          struct threadlist * next ;
        }
        ThreadList;


ThreadList * threads ;


void push( Thread * th , int v )
{
  Cactus * tos;
  tos = gimmeanew(Cactus);
  tos->i    = v;
  tos->pop  = th->stack;
  th->stack = tos;
  th->rogue = th->stack;
}


int  pop ( Thread * th         ) 
{
  Cactus * poop;
  int r;
  poop = th->stack;
  if (   (poop  != NULL)
      && (th->rogue == NULL)
     )
     {
       th->stack = poop->pop;
       r         = poop->i;
       // we do NOT recycle(poop) because this stack element could be shared;
       // we could  detect sharing if the stack elements had reference counts in them.
       return r;
     } else
     {
       if (th->rogue == poop)
          {
            printf( "ROGUE thread %d detected, CANCELLED.\n" , th->id );
            // almost could get the opcode 41 stuck in here for the end of network hard silence.
          }
       th->live = 0;
       return (-1);
     }
}


void showstack( Thread * th )
{
  Cactus * t = th->stack;
  printf(" [ ");
  while (t != NULL)
    {
      printf( " ( %d : %d ) , " , ((int)(t)) , t->i ); 
      t=t->pop;
    }
  printf(" ] ");
}


int stacksarethesame( Cactus * s1 , Cactus * s2 )
{
    Cactus * a = s1 , 
           * b = s2 ;
    // this tail-recursion could be rewritten to iteration.
    return    
    (
         (a == b)
      || (
              (a != NULL) 
           && (b != NULL) 
           && (a->i == b->i) 
           && stacksarethesame( a->pop , b->pop )
         )
    ) ;
}


void queuedump( Thread * th , char * blurb )
{
  ThreadList * t = threads;
  return;
  printf("queue @ (%s):" , blurb);
  while( t!=NULL)
    {
      printf(" %d " , t->th->id );
      t = t->next;
    }
  printf("\n");
}


void threadontoqueue( Thread * th )
{
  // printf(   "thread %d onto queue\n" , (th == NULL? (-1) : th->id ));
  queuedump( th , "onto start" );
  ThreadList * shell = gimmeanew(ThreadList);
  shell->th                  = th;
  shell->next                = threads;
  threads                    = shell;
  queuedump( th , "onto end  " );
}


Thread * threadfromqueue( Thread * th )
{
  ThreadList * curr 
                   , * prev
                   , * retu
                   ;
  Thread     * rv;

  queuedump( th , "from start" );
  prev = NULL   ;
  curr = threads;

  if (threads       == NULL){
                              retu    = NULL;
                              rv      = NULL;
                            }
  else
  if (threads->next == NULL){
                              retu    = threads;
                              threads = threads->next;
                              rv      = retu->th;
                            }
  else                      {
                              prev = curr = threads;
                              while (curr->next != NULL)
                                {
                                  prev = curr;
                                  curr = curr->next;
                                }
                              retu       = prev->next;
                              prev->next = NULL;
                              rv         = retu->th;
                            }
  recycle(retu);
  queuedump( th , "from end  " );
  return rv;
}


typedef struct hmmshell
        {
          int              id            ; // the birth certificate.
          int              tp0           ;
          int              tp1           ;
          int              tp2           ;
          int              ip            ;
          Cactus         * stack         ;
          Thread         * th            ;

          int              semantics     ; // set by thread to successor hmm by reaching back, offset into globalintarray of sequence of ints ending with a 0.
      //  structhmmshell * successor[40] ; // set by thread to successor hmm by reaching back.

        } HmmShell;


typedef struct structvoidstarlist
        {
          void                      * item;
          struct structvoidstarlist * next;
        } List;


// List * neue ; // EEEK C++
// List * hmms ; // EEEK C++


#ifdef SIMULATOR

#include "Vektor.h"          // EEEK C++
Vektor<HmmShell*> * neue, // wavefronthmms      // EEEK C++
             * hmms; // nonwavefronthmms;  // EEEK C++

int          globalhmmcount    ;


HmmShell * createhmmshell( Thread * th )
{
  printf( "thread %d popped an ear, setting origin hmm %d semantics.  "
         ,th->id , ( (th->origin == NULL)?(-1):(th->origin->id) )
        );
  if (th->origin != NULL)
     {
      th->origin->semantics = th->semantics = (globalintarrayfirstfree)++;
      globalintarray[ globalintarrayfirstfree ] = 0;
     }
  printf(  "...seeking hmm [%s,%s,%s] with stack@%d :\t"
         , phonesbyint[ th->tp[0] ]
         , phonesbyint[ th->tp[1] ]
         , phonesbyint[ th->tp[2] ]
         , ((int)(th->stack))
        );
  showstack( th );
  printf("\t:\t");
  for (int i=0 ; i<hmms->length() ; ++i) // EEEK C++
      {
        if 
           (
             (
                 ( hmms->get(i)->tp0   == th->tp[0] ) // EEEK C++
              && ( hmms->get(i)->tp1   == th->tp[1] ) // EEEK C++
              && ( hmms->get(i)->tp2   == th->tp[2] ) // EEEK C++
              && ( hmms->get(i)->ip    == th->ip    )
              && stacksarethesame( hmms->get(i)->stack , th->stack )
             )
         //  ||
         //  (   ( hmms->get(i)->th->tp[1] == th->tp[1] ) // EEEK C++
         //   && ( th->tp[1] == 39 )
         //  )
           )
           {
             printf("EXTANT hmm %d found by thread %d\n", hmms->get(i)->id , th->id);  // EEEK C++
             th->live = 0;  // coalesce.
             return hmms->get(i); // EEEK C++
           }
      }
  for (int i=0 ; i<neue->length() ; ++i) // EEEK C++
      {
        if 
           (
             (
                 ( neue->get(i)->tp0   == th->tp[0] ) // EEEK C++
              && ( neue->get(i)->tp1   == th->tp[1] ) // EEEK C++
              && ( neue->get(i)->tp2   == th->tp[2] ) // EEEK C++
              && ( neue->get(i)->ip    == th->ip    )
              && stacksarethesame( neue->get(i)->stack , th->stack )
             )
         //  ||
         //  (   ( neue->get(i)->th->tp[1] == th->tp[1] ) // EEEK C++
         //   && ( th->tp[1] == 39 )
         //  )
           )
           {
             printf("EXTANT hmm %d found by thread %d\n", neue->get(i)->id , th->id);  // EEEK C++
             th->live = 0;  // coalesce.
             return neue->get(i); // EEEK C++
           }
      }

  // this is a genuinely novel hmm, otherwise the code would have returned above.

  printf("NEW     hmm %d created by thread %d.\n", globalhmmcount , th->id);
  HmmShell * h  = gimmeanew(HmmShell);
  h->th    = th;
  h->id    = (globalhmmcount)++; // hmm birth certificate
  h->tp0   = th->tp[0];          // hmm birth certificate
  h->tp1   = th->tp[1];          // hmm birth certificate
  h->tp2   = th->tp[2];          // hmm birth certificate
  h->ip    = th->ip   ;
  h->stack = th->stack;
  neue->append(h);   // will get dumped into the global hmms when this thread sleeps.
  return h;
}


void showsemantics( int startingat )
{
  printf( "semantics [ " );
  int s = startingat;
  while (globalintarray[ s ] != 0)
    {
      printf( " %d " , globalintarray[s] );
      s++;
    }
  printf( "] :  " );
}


void showregisters( Thread * th )
{
  if (th == NULL) printf("WHAT IS UP WITH THIS?\n"); else
  printf (   "thread %4d  tp={%2s,%2s,%2s}  ev={%d,%d,%d}  ip=%4d  rogue@%d stack@%d  "
           , th->id
           , inttophone ( (th->tp)[0] )
           , inttophone ( (th->tp)[1] )
           , inttophone ( (th->tp)[2] )
           ,              (th->ev)[0]
           ,              (th->ev)[1]
           ,              (th->ev)[2]
           , th->ip
           , ((int)(th->rogue))
           , ((int)(th->stack))
         );
  showstack( th );
  showsemantics( th->semantics );
}


#endif SIMULATOR


void phone( Thread * th , int phonenumber )
{
  (th->tp)[0] = (th->tp)[1];
  (th->tp)[1] = (th->tp)[2];
  (th->tp)[2] = phonenumber;

  (th->ev)[0] = (th->ev)[1];
  (th->ev)[1] = (th->ev)[2];
  (th->ev)[2] = 0;

  th->rogue = NULL;

  globalintarray[ (globalintarrayfirstfree)++ ] = 0;
  showregisters( th );
  printf( "[%s]\n" , phonesbyint[phonenumber] );
  createhmmshell( th );  //  share an existing hmm?
  th->origin = NULL   ;  //  WRONG ?
  th->live   =    0   ;  //  i think threads need more than just live/nonlive states.  really should be in a waning state here, live=0 at next ear or visit or explore.

  ++(th->ip);
}
void oc00( Thread * th ) { phone(th, 0); }
void oc01( Thread * th ) { phone(th, 1); }
void oc02( Thread * th ) { phone(th, 2); }
void oc03( Thread * th ) { phone(th, 3); }
void oc04( Thread * th ) { phone(th, 4); }
void oc05( Thread * th ) { phone(th, 5); }
void oc06( Thread * th ) { phone(th, 6); }
void oc07( Thread * th ) { phone(th, 7); }
void oc08( Thread * th ) { phone(th, 8); }
void oc09( Thread * th ) { phone(th, 9); }
void oc10( Thread * th ) { phone(th,10); }
void oc11( Thread * th ) { phone(th,11); }
void oc12( Thread * th ) { phone(th,12); }
void oc13( Thread * th ) { phone(th,13); }
void oc14( Thread * th ) { phone(th,14); }
void oc15( Thread * th ) { phone(th,15); }
void oc16( Thread * th ) { phone(th,16); }
void oc17( Thread * th ) { phone(th,17); }
void oc18( Thread * th ) { phone(th,18); }
void oc19( Thread * th ) { phone(th,19); }
void oc20( Thread * th ) { phone(th,20); }
void oc21( Thread * th ) { phone(th,21); }
void oc22( Thread * th ) { phone(th,22); }
void oc23( Thread * th ) { phone(th,23); }
void oc24( Thread * th ) { phone(th,24); }
void oc25( Thread * th ) { phone(th,25); }
void oc26( Thread * th ) { phone(th,26); }
void oc27( Thread * th ) { phone(th,27); }
void oc28( Thread * th ) { phone(th,28); }
void oc29( Thread * th ) { phone(th,29); }
void oc30( Thread * th ) { phone(th,30); }
void oc31( Thread * th ) { phone(th,31); }
void oc32( Thread * th ) { phone(th,32); }
void oc33( Thread * th ) { phone(th,33); }
void oc34( Thread * th ) { phone(th,34); }
void oc35( Thread * th ) { phone(th,35); }
void oc36( Thread * th ) { phone(th,36); }
void oc37( Thread * th ) { phone(th,37); }
void oc38( Thread * th ) { phone(th,38); }
void oc39( Thread * th ) // silence
{
  // spawn a thread, alias its stack to this thread's stack.
  // and get it to skip this "sil" whose microcode this is.

  th->rogue = NULL;

  Thread * ed = createthread( th->ip+1 );
  ed->stack      = th->stack;
  ed->tp[0]      = th->tp[0];
  ed->tp[1]      = th->tp[1];
  ed->tp[2]      = th->tp[2];
  ed->nextinhmm  = NULL;
  ed->rogue      = NULL;
  threadontoqueue( ed );

  phone(th,39);
}
void oc40( Thread * th ) { phone(th,39); }
void oc41( Thread * th ) { phone(th,41); }
void oc42( Thread * th ) // noop
            {
              showregisters(th);
              printf("noop\n");
              ++(th->ip);
            }
void oc43( Thread * th ) // return N
            {
              showregisters(th);
              if (th->semantics == (-1))
                 {
                   th->semantics = globalintarrayfirstfree;
                 }
              globalintarray[ (th->semantics)++ ] = instructions[1+th->ip];
              globalintarray[ (th->semantics)   ] = 0;
              globalintarrayfirstfree = (th->semantics)+1;
              printf("return %d\n" , instructions[1+th->ip] );
              th->ip = pop(th);
            }
void oc44( Thread * th ) // semantics N
            {
              showregisters( th );
              if (th->semantics == (-1))
                 {
                   th->semantics = globalintarrayfirstfree;
                 }
              globalintarray[ (th->semantics)++ ] = instructions[1+th->ip];
              globalintarray[ (th->semantics)   ] = 0;
              globalintarrayfirstfree = (th->semantics)+1;
              printf("semantics %d\n" , instructions[1+(th->ip)] );
              th->ip +=2;
            }
void oc45( Thread * th ) // stone N = shiftin N
            {
              showregisters( th );
              printf("stone %d\n" ,     instructions[1+(th->ip)] );
              for (int i = 0 ; i<WINDOWLENGTH-1 ; ++i)
                  th->window[i] = th->window[i+1];
              th->window[WINDOWLENGTH-1] = instructions[1+(th->ip)];
              th->ip +=2;
            }
// void oc46( Thread * th ) // explore N label1 weight1 ... labelN weightN
//             {
//               globalintarray[ (globalintarrayfirstfree)++ ] = 0;
//               showregisters( th );
//               // spawn the N threads, sending each along a path from the instructions[]
//               int ways = instructions[1+th->ip];
//               printf("explore %d " , instructions[1+th->ip] );
//               for (int i = 0 ; i < ways ; ++i)
//                   {
//                     printf( "%d %d " , instructions[th->ip + 2*(i+1) + 0]
//                                      , instructions[th->ip + 2*(i+1) + 1]
//                           );
//                     Thread  * jed = createthread( instructions[th->ip + 2*(i+1) + 0] );
//                     jed->stack      = th->stack;
//                     jed->tp[0]      = th->tp[0];
//                     jed->tp[1]      = th->tp[1];
//                     jed->tp[2]      = th->tp[2];
//                     jed->hc[3]      = instructions[th->ip + 2*(i+1) + 1];
//                     push( jed , th->ip + 1 + 1 + 2*ways ) ;
//                     threadontoqueue( jed );
//                   };
//               th->live = 0;
//               printf("\n");
//               th->ip += (1 + 2*ways);
//             }
void oc46( Thread * th ) // explore N label1 weight1 ... labelN weightN
            {
              globalintarray[ (globalintarrayfirstfree)++ ] = 0;
              showregisters( th );
              // spawn N-1 threads, send the N-1 spawned to labels 1,...,N-1 and send th to label N, all from the instructions[]
              int ways = instructions[1+th->ip];
              printf("explore %d " , instructions[1+th->ip] );
              for (int i = 0 ; i < ways ; ++i)
                  {
                    printf( "%d %d " , instructions[th->ip + 2*(i+1) + 0]
                                     , instructions[th->ip + 2*(i+1) + 1]
                          );
                    if (i<ways-1)
                    {
                    Thread  * jed = createthread( instructions[th->ip + 2*(i+1) + 0] );
                    jed->stack      = th->stack;
                    jed->tp[0]      = th->tp[0];
                    jed->tp[1]      = th->tp[1];
                    jed->tp[2]      = th->tp[2];
                    jed->hc[3]      = instructions[th->ip + 2*(i+1) + 1];
                    push( jed , th->ip + 1 + 1 + 2*ways ) ;
                    threadontoqueue( jed );
                    } else
                    { // redeploy th
                    push( th  , th->ip + 1 + 1 + 2*ways ) ;
                    th->ip = instructions[th->ip + 2*ways] ; // with weight instructions[th->ip + 2*ways + 1]
                    }
                  };
              th->live = 0;
              printf("\n");
              th->ip += (1 + 2*ways);
            }
void oc47( Thread * th ) // visit label
            {
              showregisters( th );
              int dest = instructions[1+(th->ip)];
              printf("visit %d\n" , dest );
              push(th , (th->ip) + 3);
              (th->ip) = dest;
            }
void oc48( Thread * th ) // nodetype N (or event N) // event 1 = word entry ; event -1 = word exit ; event 0 = nothing interesting.
            {
              // events (integers) slide through the thread ev register, sliding at each phonetic (sound) instruction.
              th->ev[0] = th->ev[1];
              th->ev[1] = th->ev[2];
              th->ev[2] = instructions[1+(th->ip)];
              showregisters( th );
              printf("nodetype %d\n" , instructions[1+(th->ip)] );
              th->ip += 2;
            }
void oc49( Thread * th ) // stop
            {
              showregisters( th );
              printf ("\tstop\n" );
              th->live = 0;
            }
void oc50( Thread * th ) // goto label
            {
              showregisters( th );
              int dest = instructions[1+(th->ip)];
              printf("goto %d\n" , dest );
              (th->ip) = dest;
            }
void oc51( Thread * th ) // visittodeath label = a special use goto, so no weight on this, unlike 'visit label weight'.
            {
              showregisters( th );
              int dest = instructions[1+(th->ip)];
              push(th , (th->ip));
              printf("visittodeath %d\n" , dest );
              (th->ip) = dest;
            }



typedef void (*microcodeinstruction)( Thread * );


microcodeinstruction opcodedecoder[52] =
{
    oc00 , oc01 , oc02 , oc03 , oc04 , oc05 , oc06 , oc07 , oc08 , oc09
  , oc10 , oc11 , oc12 , oc13 , oc14 , oc15 , oc16 , oc17 , oc18 , oc19
  , oc20 , oc21 , oc22 , oc23 , oc24 , oc25 , oc26 , oc27 , oc28 , oc29
  , oc30 , oc31 , oc32 , oc33 , oc34 , oc35 , oc36 , oc37 , oc38 , oc39
  , oc40 , oc41 , oc42 , oc43 , oc44 , oc45 , oc46 , oc47 , oc48 , oc49 , oc50 , oc51
};


void runthread( Thread * th )
{
  Thread * in = th;
  th->live = 1;
  threadontoqueue( th );       // why?
  th = threadfromqueue( th );
  if ( in != th )
     printf( "\n\n\nBUTTHEAD:  justify the lines above %d in %s.\n\n\n" , __LINE__ , __FILE__ );
  while(th != NULL)
    {
       while(th->live)
         {
            (*(opcodedecoder[instructions[(th->ip)]]))( th );
         }
       th = threadfromqueue( th );
    }
}


#ifdef SIMULATOR


void showhmms()
{
     for(int h=0 ; h < hmms->length() ; ++h)
        {

           printf( "hmm[%2s,%2s,%2s] hmmid=%d born at ip=%d stack@%d\t"
                  , phonesbyint[ hmms->get(h)->tp0 ]
                  , phonesbyint[ hmms->get(h)->tp1 ]
                  , phonesbyint[ hmms->get(h)->tp2 ]
                  ,              hmms->get(h)->id          
                  ,              hmms->get(h)->ip
                  ,       ((int)(hmms->get(h)->stack))
                 );
           if (hmms->get(h)->th)
              {
                printf(" ACTIVE : ");
                showregisters(hmms->get(h)->th);           
              }
           if (hmms->get(h)->semantics)
              {
                showsemantics( hmms->get(h)->semantics );
              }
           printf("\n");
        }
}


void gangstarapper()
{
   int wallclock           = 0;
   globalthreadcount       = 0;
   globalhmmcount          = 0;
   threads                 = NULL;
   globalintarrayfirstfree = 0;

   hmms       = new Vektor<HmmShell*>;  // NULL;  

   // this initial thread is the only explorer not sent exploring by an HmmShell on the frontier.

   Thread * ted = createthread( startataddress );

   neue       = new Vektor<HmmShell*>;  // NULL;  
   printf("\nROUND %d:\n" , wallclock++ );
   runthread( ted );  // only one thread runs at a given time, caching new hmms in neue as it does.
   for(int h=0 ; h < neue->length() ; ++h)   
      hmms->append( neue->get(h) );          
   showhmms();
   recycle(neue);

   int    activethread = 1;
   while (activethread)
   {
     printf("\nROUND %d:\n" , wallclock++ );

     neue = new Vektor<HmmShell*>;                                                       
     for(int h=0 ; h < hmms->length() ; ++h)    // this is like eric's beam.
        {
          if (hmms->get(h)->th != NULL)                                                     
             {
               hmms->get(h)->th->origin = hmms->get(h);
               hmms->get(h)->th->semantics = hmms->get(h)->th->origin->semantics = (globalintarrayfirstfree)++;
               globalintarray[ globalintarrayfirstfree ] = 0;
               runthread( hmms->get(h)->th );
               hmms->get(h)->th = NULL;
             }
        }

     for(int h=0 ; h < neue->length() ; ++h) 
        hmms->append( neue->get(h) );        
     recycle(neue);

     activethread = 0;
     for(int h=0 ; h < hmms->length() ; ++h)
        {
          if (hmms->get(h)->th != NULL)
             activethread = 1;
        }
     showhmms();
   }
   recycle(ted );
   recycle(hmms);
}


int main() 
{
  system("date");
  printf( "simulator starting...\n" );
  gangstarapper();
  printf( "\nsimulator done.\n"     );
  return 0;
}


#endif SIMULATOR
