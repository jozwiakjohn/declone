// john jozwiak @ motorola testing thing for my java shit, man.


// import java.lang.*;


class testabnftogsl
{
  static String acidtestdotabnf = 
  "#ABNF 1.0 ISO8859-1; \n" +
  "language en;\n" +
  "root $wet;\n" +
  "tag-format semantics/1.0 ;\n" +
  "alias   $(http://www.mygrammars.com/cities-states.gram) as $$places;\n" +
  "alias   $(http://www.mygrammars.com/cities-states.gram)    $$places;\n" +
  "lexicon $(http://www.example.com/lexicon.file);\n" +
  "lexicon $(http://www.example.com/strange-city-names.file);\n" +
  "meta \"Author\"     is \"Dilbert\"; \n" +
  "meta \"maintainer\"    \"dilbert@dilbert.com\" ;\n" +
  "http-equiv \"Expires\" is \"0\" ;\n" +
  "http-equiv \"Date\"    is \"Thu, 12 Dec 2000 23:27:21 GMT\"; \n" +
  "mode voice;\n" +
  "mode dtmf ;\n" +
  "\n" +
  "/**\n" +
  " * Basic command.\n" +
  " * @example please move the window\n" +
  " * @example open a file\n" +
  " */\n" +
  "\n" +
  "public $basicCmd = $$polite#startPolite $command $$polite#endPolite;\n" +
  "$command = $action $object;\n" +
  "$action = /10/ open {OPEN} | /2/ close {CLOSE} | /1/ delete {DELETE} | /1/ move {DELETE};\n" +
  "$object = [the | a] (window | file | menu);\n" +
  "public $city  = Boston | Philadelphia | Fargo;\n" +
  "public $state = Florida | North Dakota | New York;\n" +
  "public $city_state = $city $state;\n" +
  "$flight = I want to fly to $(http://www.example.com/places.xml#city);\n" +
  "$exercise = I want to walk to $$someplaces#state;\n" +
  "$wet = I want to swim to $$someplaces;\n" +
  "$digit = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9;\n" +
  "public $pin = $digit $digit $digit $digit \"#\" | \"*9\";\n" +
  "$sanfran =\n" +
  "         \"San Francisco\"\n" +
  "         \" San Francisco \"\n" +
  "         \"San \n" +
  "         Francisco\"\n" +
  "         \"   San    Francisco    \"\n" +
  "         ;\n" +
  "$counts  =\n" +
  "         [very]         // the token \"very\" is optional\n" +
  "         very   <0-1>   // the token \"very\" is optional\n" +
  "         $digit *       // >= 0 repetitions  //  DEPRECATED SYNTAX\n" +
  "         $digit <0->\n" +
  "         $digit +       // >= 1 repetitions  //  DEPRECATED SYNTAX\n" +
  "         $digit <1->\n" +
  "         $digit <4-6>   // {4,5,6} repetitions\n" +
  "         $digit <10->   // {10,...} repetitions\n" +
  "\n" +
  "         // Examples of the following expansion\n" +
  "\n" +
  "         \"pizza\"\n" +
  "         \"big pizza with pepperoni\"\n" +
  "         \"very big pizza with cheese and pepperoni\"\n" +
  "         [[very] big] pizza ([with | and] $topping)*   //  DEPRECATED SYNTAX\n" +
  "         ;\n" +
  "\n" +
  "$repeatprobabilities =\n" +
  "         very   <0-1 /0.6/>   // the token \"very\" is optional and is 60% likely to occur and 40% likely to be absent in input\n" +
  "         $digit <2-4 /.8/>    // the rule reference $digit must occur two to four times with 80% recurrence\n" +
  "         ;\n" +
  "\n" +
  "$empty = ;\n" +
  "$empty = /1/ ;  // this is nonsense however it is allowed.\n" +
  "$empty = {tag} ;  // this is sensibly allowed.\n" +
  "$rule0 = {content};\n" +
  "$rule1 = this is a {tag1} test {tag2};\n" +
  "$rule2 = open {action='open';} | $close {action='shut';};\n" +
  "$rule3 = {!{ a simple tag containing { and } without escaping }!};\n" +
  "\n" +
  "$eclipsinglanguages =\n" +
  "         ( oui!fr-CA | yes!en-US )\n" +
  "         ( May I speak to (Michel Tremblay | André Roy)!fr-CA )\n" +
  "         ( Robert!en-US,fr-CA )\n" +
  "         ( Robert!en-US | Robert!fr-CA )\n" +
  "         ;\n" +
  "$town = Townsville | Beantown;\n" +
  "private $city = Boston | \"New York\" | Madrid;\n" +
  "public $command = $action $object;\n" +
  "public $public = public $public | $$public;\n" ;
   
  static String theyear =
  "#ABNF null ISO8859-1;" +
  "$month = (january | february | march | april | may | june | july | august | september | october | november | december);" ;

  static String menarek = 
  "#ABNF 1.0 ISO8859-1;" + "\n" + "language en;" + "\n" +
  "mode voice;" + "\n" + "root $city_state;" + "\n" +
  "public $city = (Boston | Philadelphia | Fargo);" + "\n" +
  "public $state = (Florida | North Dakota | New York);" + "\n" +
  "public $city_state = $city $state ;" ;

  public static void main( String[] a )
  {
    try {
          System.out.print( Converter.abnftogsl( menarek , "rule0" ) );
        } catch (Exception e) { System.out.println( e ); }
  }
}
