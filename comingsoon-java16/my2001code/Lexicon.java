// john jozwiak @ motorola 20010830.
// john jozwiak @ motorola 20010716noonish.
// john jozwiak @ motorola 20010517


import java.util.Vector;
import java.util.Hashtable;
import java.lang.String;


class Pronunciation
{
  String dialect ;  // known example is "en-us".
  VectoR phonemes;  // each element in the VectoR is a String
};


abstract class Lexicon
{
  abstract VectoR pronounce( String word , String accent );  // a VectoR of VectoR of String

  public static String phonesbyint[] =
  {
    "aa"  , //   0
    "ae"  , //   1
    "ah"  , //   2
    "ao"  , //   3
    "aw"  , //   4
    "ay"  , //   5
    " b"  , //   6
    "ch"  , //   7
    " d"  , //   8
    "dh"  , //   9
    "eh"  , //  10
    "er"  , //  11
    "ey"  , //  12
    " f"  , //  13
    " g"  , //  14
    "hh"  , //  15
    "ih"  , //  16
    "iy"  , //  17
    "jh"  , //  18
    " k"  , //  19
    " l"  , //  20
    " m"  , //  21
    " n"  , //  22
    "ng"  , //  23
    "ow"  , //  24
    "oy"  , //  25
    " p"  , //  26
    " r"  , //  27
    " s"  , //  28
    "sh"  , //  29
    " t"  , //  30
    "th"  , //  31
    "uh"  , //  32
    "uw"  , //  33
    " v"  , //  34
    " w"  , //  35
    " y"  , //  36
    " z"  , //  37
    "zh"  , //  38
    "  "  , //  39
    "??"  , //  40
    "__"    //  41
  };


  public static Hashtable intsbyphone;


  {   // no name associated with this code block:  in java this is an initializer for a single copy
      // of global data associated with the class Lexicon, a so-called class variable, rather than instance variable,
      // and hence so-called class initializer, rather than instance initializer (aka constructor).

      intsbyphone = new Hashtable();
      intsbyphone.put ( "aa"    , new Integer(  0) ) ;
      intsbyphone.put ( "aa0"   , new Integer(  0) ) ;
      intsbyphone.put ( "aa1"   , new Integer(  0) ) ;
      intsbyphone.put ( "aa2"   , new Integer(  0) ) ;
      intsbyphone.put ( "ae"    , new Integer(  1) ) ;
      intsbyphone.put ( "ae0"   , new Integer(  1) ) ;
      intsbyphone.put ( "ae1"   , new Integer(  1) ) ;
      intsbyphone.put ( "ae2"   , new Integer(  1) ) ;
      intsbyphone.put ( "ah"    , new Integer(  2) ) ;
      intsbyphone.put ( "ah0"   , new Integer(  2) ) ;
      intsbyphone.put ( "ah1"   , new Integer(  2) ) ;
      intsbyphone.put ( "ah2"   , new Integer(  2) ) ;
      intsbyphone.put ( "ao"    , new Integer(  3) ) ;
      intsbyphone.put ( "ao0"   , new Integer(  3) ) ;
      intsbyphone.put ( "ao1"   , new Integer(  3) ) ;
      intsbyphone.put ( "ao2"   , new Integer(  3) ) ;
      intsbyphone.put ( "aw"    , new Integer(  4) ) ;
      intsbyphone.put ( "aw0"   , new Integer(  4) ) ;
      intsbyphone.put ( "aw1"   , new Integer(  4) ) ;
      intsbyphone.put ( "aw2"   , new Integer(  4) ) ;
      intsbyphone.put ( "ay"    , new Integer(  5) ) ;
      intsbyphone.put ( "ay0"   , new Integer(  5) ) ;
      intsbyphone.put ( "ay1"   , new Integer(  5) ) ;
      intsbyphone.put ( "ay2"   , new Integer(  5) ) ;
      intsbyphone.put ( "b"     , new Integer(  6) ) ;
      intsbyphone.put ( "ch"    , new Integer(  7) ) ;
      intsbyphone.put ( "d"     , new Integer(  8) ) ;
      intsbyphone.put ( "dh"    , new Integer(  9) ) ;
      intsbyphone.put ( "eh"    , new Integer( 10) ) ;
      intsbyphone.put ( "eh0"   , new Integer( 10) ) ;
      intsbyphone.put ( "eh1"   , new Integer( 10) ) ;
      intsbyphone.put ( "eh2"   , new Integer( 10) ) ;
      intsbyphone.put ( "er"    , new Integer( 11) ) ;
      intsbyphone.put ( "er0"   , new Integer( 11) ) ;
      intsbyphone.put ( "er1"   , new Integer( 11) ) ;
      intsbyphone.put ( "er2"   , new Integer( 11) ) ;
      intsbyphone.put ( "ey"    , new Integer( 12) ) ;
      intsbyphone.put ( "ey0"   , new Integer( 12) ) ;
      intsbyphone.put ( "ey1"   , new Integer( 12) ) ;
      intsbyphone.put ( "ey2"   , new Integer( 12) ) ;
      intsbyphone.put ( "f"     , new Integer( 13) ) ;
      intsbyphone.put ( "g"     , new Integer( 14) ) ;
      intsbyphone.put ( "hh"    , new Integer( 15) ) ;
      intsbyphone.put ( "ih"    , new Integer( 16) ) ;
      intsbyphone.put ( "ih0"   , new Integer( 16) ) ;
      intsbyphone.put ( "ih1"   , new Integer( 16) ) ;
      intsbyphone.put ( "ih2"   , new Integer( 16) ) ;
      intsbyphone.put ( "iy"    , new Integer( 17) ) ;
      intsbyphone.put ( "iy0"   , new Integer( 17) ) ;
      intsbyphone.put ( "iy1"   , new Integer( 17) ) ;
      intsbyphone.put ( "iy2"   , new Integer( 17) ) ;
      intsbyphone.put ( "jh"    , new Integer( 18) ) ;
      intsbyphone.put ( "k"     , new Integer( 19) ) ;
      intsbyphone.put ( "l"     , new Integer( 20) ) ;
      intsbyphone.put ( "m"     , new Integer( 21) ) ;
      intsbyphone.put ( "n"     , new Integer( 22) ) ;
      intsbyphone.put ( "ng"    , new Integer( 23) ) ;
      intsbyphone.put ( "ow"    , new Integer( 24) ) ;
      intsbyphone.put ( "ow0"   , new Integer( 24) ) ;
      intsbyphone.put ( "ow1"   , new Integer( 24) ) ;
      intsbyphone.put ( "ow2"   , new Integer( 24) ) ;
      intsbyphone.put ( "oy"    , new Integer( 25) ) ;
      intsbyphone.put ( "oy0"   , new Integer( 25) ) ;
      intsbyphone.put ( "oy1"   , new Integer( 25) ) ;
      intsbyphone.put ( "oy2"   , new Integer( 25) ) ;
      intsbyphone.put ( "p"     , new Integer( 26) ) ;
      intsbyphone.put ( "r"     , new Integer( 27) ) ;
      intsbyphone.put ( "s"     , new Integer( 28) ) ;
      intsbyphone.put ( "sh"    , new Integer( 29) ) ;
      intsbyphone.put ( "t"     , new Integer( 30) ) ;
      intsbyphone.put ( "th"    , new Integer( 31) ) ;
      intsbyphone.put ( "uh"    , new Integer( 32) ) ;
      intsbyphone.put ( "uh0"   , new Integer( 32) ) ;
      intsbyphone.put ( "uh1"   , new Integer( 32) ) ;
      intsbyphone.put ( "uh2"   , new Integer( 32) ) ;
      intsbyphone.put ( "uw"    , new Integer( 33) ) ;
      intsbyphone.put ( "uw0"   , new Integer( 33) ) ;
      intsbyphone.put ( "uw1"   , new Integer( 33) ) ;
      intsbyphone.put ( "uw2"   , new Integer( 33) ) ;
      intsbyphone.put ( "v"     , new Integer( 34) ) ;
      intsbyphone.put ( "w"     , new Integer( 35) ) ;
      intsbyphone.put ( "y"     , new Integer( 36) ) ;
      intsbyphone.put ( "z"     , new Integer( 37) ) ;
      intsbyphone.put ( "zh"    , new Integer( 38) ) ;
      intsbyphone.put ( "sil"   , new Integer( 39) ) ;
      intsbyphone.put ( "pau"   , new Integer( 40) ) ;
      intsbyphone.put ( "sil0a" , new Integer(301) ) ;
      intsbyphone.put ( "sil0b" , new Integer(302) ) ;
      intsbyphone.put ( "sil0c" , new Integer(303) ) ;
      intsbyphone.put ( "sil0d" , new Integer(304) ) ;
      intsbyphone.put ( "sil0e" , new Integer(305) ) ;
      intsbyphone.put ( "sil0f" , new Integer(306) ) ;
      intsbyphone.put ( "sil0g" , new Integer(307) ) ;
      intsbyphone.put ( "sil0h" , new Integer(308) ) ;
      intsbyphone.put ( "sil0i" , new Integer(309) ) ;
      intsbyphone.put ( "sil0j" , new Integer(310) ) ;
      intsbyphone.put ( "sil0k" , new Integer(311) ) ;
      intsbyphone.put ( "sil0l" , new Integer(312) ) ;
      intsbyphone.put ( "sil0m" , new Integer(313) ) ;
      intsbyphone.put ( "sil0n" , new Integer(314) ) ;
      intsbyphone.put ( "sil0o" , new Integer(315) ) ;
      intsbyphone.put ( "sil0p" , new Integer(316) ) ;
      intsbyphone.put ( "sil0q" , new Integer(317) ) ;
      intsbyphone.put ( "sil0r" , new Integer(318) ) ;
      intsbyphone.put ( "sil0s" , new Integer(319) ) ;
      intsbyphone.put ( "sil0t" , new Integer(320) ) ;
      intsbyphone.put ( "sil0u" , new Integer(321) ) ;
      intsbyphone.put ( "sil0v" , new Integer(322) ) ;
      intsbyphone.put ( "sil0w" , new Integer(323) ) ;
      intsbyphone.put ( "sil0x" , new Integer(324) ) ;
      intsbyphone.put ( "sil0y" , new Integer(325) ) ;
      intsbyphone.put ( "sil0z" , new Integer(326) ) ;
      intsbyphone.put ( "sil1a" , new Integer(327) ) ;
      intsbyphone.put ( "sil1b" , new Integer(328) ) ;
      intsbyphone.put ( "sil1c" , new Integer(329) ) ;
      intsbyphone.put ( "sil1d" , new Integer(330) ) ;
      intsbyphone.put ( "sil1e" , new Integer(331) ) ;
      intsbyphone.put ( "sil1f" , new Integer(332) ) ;
      intsbyphone.put ( "sil1g" , new Integer(333) ) ;
      intsbyphone.put ( "sil1h" , new Integer(334) ) ;
      intsbyphone.put ( "sil1i" , new Integer(335) ) ;
      intsbyphone.put ( "sil1j" , new Integer(336) ) ;
      intsbyphone.put ( "sil1k" , new Integer(337) ) ;
      intsbyphone.put ( "sil1l" , new Integer(338) ) ;
      intsbyphone.put ( "sil1m" , new Integer(339) ) ;
      intsbyphone.put ( "sil1n" , new Integer(340) ) ;
      intsbyphone.put ( "sil1o" , new Integer(341) ) ;
      intsbyphone.put ( "sil1p" , new Integer(342) ) ;
      intsbyphone.put ( "sil1q" , new Integer(343) ) ;
      intsbyphone.put ( "sil1r" , new Integer(344) ) ;
      intsbyphone.put ( "sil1s" , new Integer(345) ) ;
      intsbyphone.put ( "sil1t" , new Integer(346) ) ;
      intsbyphone.put ( "sil1u" , new Integer(347) ) ;
      intsbyphone.put ( "sil1v" , new Integer(348) ) ;
      intsbyphone.put ( "sil1w" , new Integer(349) ) ;
      intsbyphone.put ( "sil1x" , new Integer(350) ) ;
      intsbyphone.put ( "sil1y" , new Integer(351) ) ;
      intsbyphone.put ( "sil1z" , new Integer(352) ) ;
      intsbyphone.put ( "sil2a" , new Integer(353) ) ;
      intsbyphone.put ( "sil2b" , new Integer(354) ) ;
      intsbyphone.put ( "sil2c" , new Integer(355) ) ;
      intsbyphone.put ( "sil2d" , new Integer(356) ) ;
      intsbyphone.put ( "sil2e" , new Integer(357) ) ;
      intsbyphone.put ( "sil2f" , new Integer(358) ) ;
      intsbyphone.put ( "sil2g" , new Integer(359) ) ;
      intsbyphone.put ( "sil2h" , new Integer(360) ) ;
      intsbyphone.put ( "sil2i" , new Integer(361) ) ;
      intsbyphone.put ( "sil2j" , new Integer(362) ) ;
      intsbyphone.put ( "sil2k" , new Integer(363) ) ;
      intsbyphone.put ( "sil2l" , new Integer(364) ) ;
      intsbyphone.put ( "sil2m" , new Integer(365) ) ;
      intsbyphone.put ( "sil2n" , new Integer(366) ) ;
      intsbyphone.put ( "sil2o" , new Integer(367) ) ;
      intsbyphone.put ( "sil2p" , new Integer(368) ) ;
      intsbyphone.put ( "sil2q" , new Integer(369) ) ;
      intsbyphone.put ( "sil2r" , new Integer(370) ) ;
      intsbyphone.put ( "sil2s" , new Integer(371) ) ;
      intsbyphone.put ( "sil2t" , new Integer(372) ) ;
      intsbyphone.put ( "sil2u" , new Integer(373) ) ;
      intsbyphone.put ( "sil2v" , new Integer(374) ) ;
      intsbyphone.put ( "sil2w" , new Integer(375) ) ;
      intsbyphone.put ( "sil2x" , new Integer(376) ) ;
      intsbyphone.put ( "sil2y" , new Integer(377) ) ;
      intsbyphone.put ( "sil2z" , new Integer(378) ) ;
      intsbyphone.put ( "sil3a" , new Integer(379) ) ;
      intsbyphone.put ( "sil3b" , new Integer(380) ) ;
      intsbyphone.put ( "sil3c" , new Integer(381) ) ;
      intsbyphone.put ( "sil3d" , new Integer(382) ) ;
      intsbyphone.put ( "sil3e" , new Integer(383) ) ;
      intsbyphone.put ( "sil3f" , new Integer(384) ) ;
      intsbyphone.put ( "sil3g" , new Integer(385) ) ;
      intsbyphone.put ( "sil3h" , new Integer(386) ) ;
      intsbyphone.put ( "sil3i" , new Integer(387) ) ;
      intsbyphone.put ( "sil3j" , new Integer(388) ) ;
      intsbyphone.put ( "sil3k" , new Integer(389) ) ;
      intsbyphone.put ( "sil3l" , new Integer(390) ) ;
      intsbyphone.put ( "sil3m" , new Integer(391) ) ;
      intsbyphone.put ( "sil3n" , new Integer(392) ) ;
      intsbyphone.put ( "sil3o" , new Integer(393) ) ;
      intsbyphone.put ( "sil3p" , new Integer(394) ) ;
      intsbyphone.put ( "sil3q" , new Integer(395) ) ;
      intsbyphone.put ( "sil3r" , new Integer(396) ) ;
      intsbyphone.put ( "sil3s" , new Integer(397) ) ;
      intsbyphone.put ( "sil3t" , new Integer(398) ) ;
      intsbyphone.put ( "sil3u" , new Integer(399) ) ;
      intsbyphone.put ( "sil3v" , new Integer(400) ) ;
      intsbyphone.put ( "sil3w" , new Integer(401) ) ;
      intsbyphone.put ( "sil3x" , new Integer(402) ) ;
      intsbyphone.put ( "sil3y" , new Integer(403) ) ;
      intsbyphone.put ( "sil3z" , new Integer(404) ) ;
      intsbyphone.put ( "sil4a" , new Integer(405) ) ;
      intsbyphone.put ( "sil4b" , new Integer(406) ) ;
      intsbyphone.put ( "sil4c" , new Integer(407) ) ;
      intsbyphone.put ( "sil4d" , new Integer(408) ) ;
      intsbyphone.put ( "sil4e" , new Integer(409) ) ;
      intsbyphone.put ( "sil4f" , new Integer(410) ) ;
      intsbyphone.put ( "sil4g" , new Integer(411) ) ;
      intsbyphone.put ( "sil4h" , new Integer(412) ) ;
      intsbyphone.put ( "sil4i" , new Integer(413) ) ;
      intsbyphone.put ( "sil4j" , new Integer(414) ) ;
      intsbyphone.put ( "sil4k" , new Integer(415) ) ;
      intsbyphone.put ( "sil4l" , new Integer(416) ) ;
      intsbyphone.put ( "sil4m" , new Integer(417) ) ;
      intsbyphone.put ( "sil4n" , new Integer(418) ) ;
      intsbyphone.put ( "sil4o" , new Integer(419) ) ;
      intsbyphone.put ( "sil4p" , new Integer(420) ) ;
      intsbyphone.put ( "sil4q" , new Integer(421) ) ;
      intsbyphone.put ( "sil4r" , new Integer(422) ) ;
      intsbyphone.put ( "sil4s" , new Integer(423) ) ;
      intsbyphone.put ( "sil4t" , new Integer(424) ) ;
      intsbyphone.put ( "sil4u" , new Integer(425) ) ;
      intsbyphone.put ( "sil4v" , new Integer(426) ) ;
      intsbyphone.put ( "sil4w" , new Integer(427) ) ;
      intsbyphone.put ( "sil4x" , new Integer(428) ) ;
      intsbyphone.put ( "sil4y" , new Integer(429) ) ;
      intsbyphone.put ( "sil4z" , new Integer(430) ) ;
      intsbyphone.put ( "sil5a" , new Integer(431) ) ;
      intsbyphone.put ( "sil5b" , new Integer(432) ) ;
      intsbyphone.put ( "sil5c" , new Integer(433) ) ;
      intsbyphone.put ( "sil5d" , new Integer(434) ) ;
      intsbyphone.put ( "sil5e" , new Integer(435) ) ;
      intsbyphone.put ( "sil5f" , new Integer(436) ) ;
      intsbyphone.put ( "sil5g" , new Integer(437) ) ;
      intsbyphone.put ( "sil5h" , new Integer(438) ) ;
      intsbyphone.put ( "sil5i" , new Integer(439) ) ;
      intsbyphone.put ( "sil5j" , new Integer(440) ) ;
      intsbyphone.put ( "sil5k" , new Integer(441) ) ;
      intsbyphone.put ( "sil5l" , new Integer(442) ) ;
      intsbyphone.put ( "sil5m" , new Integer(443) ) ;
      intsbyphone.put ( "sil5n" , new Integer(444) ) ;
      intsbyphone.put ( "sil5o" , new Integer(445) ) ;
      intsbyphone.put ( "sil5p" , new Integer(446) ) ;
      intsbyphone.put ( "sil5q" , new Integer(447) ) ;
      intsbyphone.put ( "sil5r" , new Integer(448) ) ;
      intsbyphone.put ( "sil5s" , new Integer(449) ) ;
      intsbyphone.put ( "sil5t" , new Integer(450) ) ;
      intsbyphone.put ( "sil5u" , new Integer(451) ) ;
      intsbyphone.put ( "sil5v" , new Integer(452) ) ;
      intsbyphone.put ( "sil5w" , new Integer(453) ) ;
      intsbyphone.put ( "sil5x" , new Integer(454) ) ;
      intsbyphone.put ( "sil5y" , new Integer(455) ) ;
      intsbyphone.put ( "sil5z" , new Integer(456) ) ;
      intsbyphone.put ( "sil6a" , new Integer(457) ) ;
      intsbyphone.put ( "sil6b" , new Integer(458) ) ;
      intsbyphone.put ( "sil6c" , new Integer(459) ) ;
      intsbyphone.put ( "sil6d" , new Integer(460) ) ;
      intsbyphone.put ( "sil6e" , new Integer(461) ) ;
      intsbyphone.put ( "sil6f" , new Integer(462) ) ;
      intsbyphone.put ( "sil6g" , new Integer(463) ) ;
      intsbyphone.put ( "sil6h" , new Integer(464) ) ;
      intsbyphone.put ( "sil6i" , new Integer(465) ) ;
      intsbyphone.put ( "sil6j" , new Integer(466) ) ;
      intsbyphone.put ( "sil6k" , new Integer(467) ) ;
      intsbyphone.put ( "sil6l" , new Integer(468) ) ;
      intsbyphone.put ( "sil6m" , new Integer(469) ) ;
      intsbyphone.put ( "sil6n" , new Integer(470) ) ;
      intsbyphone.put ( "sil6o" , new Integer(471) ) ;
      intsbyphone.put ( "sil6p" , new Integer(472) ) ;
      intsbyphone.put ( "sil6q" , new Integer(473) ) ;
      intsbyphone.put ( "sil6r" , new Integer(474) ) ;
      intsbyphone.put ( "sil6s" , new Integer(475) ) ;
      intsbyphone.put ( "sil6t" , new Integer(476) ) ;
      intsbyphone.put ( "sil6u" , new Integer(477) ) ;
      intsbyphone.put ( "sil6v" , new Integer(478) ) ;
      intsbyphone.put ( "sil6w" , new Integer(479) ) ;
      intsbyphone.put ( "sil6x" , new Integer(480) ) ;
      intsbyphone.put ( "sil6y" , new Integer(481) ) ;
      intsbyphone.put ( "sil6z" , new Integer(482) ) ;
      intsbyphone.put ( "sil7a" , new Integer(483) ) ;
      intsbyphone.put ( "sil7b" , new Integer(484) ) ;
      intsbyphone.put ( "sil7c" , new Integer(485) ) ;
      intsbyphone.put ( "sil7d" , new Integer(486) ) ;
      intsbyphone.put ( "sil7e" , new Integer(487) ) ;
      intsbyphone.put ( "sil7f" , new Integer(488) ) ;
      intsbyphone.put ( "sil7g" , new Integer(489) ) ;
      intsbyphone.put ( "sil7h" , new Integer(490) ) ;
      intsbyphone.put ( "sil7i" , new Integer(491) ) ;
      intsbyphone.put ( "sil7j" , new Integer(492) ) ;
      intsbyphone.put ( "sil7k" , new Integer(493) ) ;
      intsbyphone.put ( "sil7l" , new Integer(494) ) ;
      intsbyphone.put ( "sil7m" , new Integer(495) ) ;
      intsbyphone.put ( "sil7n" , new Integer(496) ) ;
      intsbyphone.put ( "sil7o" , new Integer(497) ) ;
      intsbyphone.put ( "sil7p" , new Integer(498) ) ;
      intsbyphone.put ( "sil7q" , new Integer(499) ) ;
      intsbyphone.put ( "sil7r" , new Integer(500) ) ;
      intsbyphone.put ( "sil7s" , new Integer(501) ) ;
      intsbyphone.put ( "sil7t" , new Integer(502) ) ;
      intsbyphone.put ( "sil7u" , new Integer(503) ) ;
      intsbyphone.put ( "sil7v" , new Integer(504) ) ;
      intsbyphone.put ( "sil7w" , new Integer(505) ) ;
      intsbyphone.put ( "sil7x" , new Integer(506) ) ;
      intsbyphone.put ( "sil7y" , new Integer(507) ) ;
      intsbyphone.put ( "sil7z" , new Integer(508) ) ;
      intsbyphone.put ( "sil8a" , new Integer(509) ) ;
      intsbyphone.put ( "sil8b" , new Integer(510) ) ;
      intsbyphone.put ( "sil8c" , new Integer(511) ) ;
      intsbyphone.put ( "sil8d" , new Integer(512) ) ;
      intsbyphone.put ( "sil8e" , new Integer(513) ) ;
      intsbyphone.put ( "sil8f" , new Integer(514) ) ;
      intsbyphone.put ( "sil8g" , new Integer(515) ) ;
      intsbyphone.put ( "sil8h" , new Integer(516) ) ;
      intsbyphone.put ( "sil8i" , new Integer(517) ) ;
      intsbyphone.put ( "sil8j" , new Integer(518) ) ;
      intsbyphone.put ( "sil8k" , new Integer(519) ) ;
      intsbyphone.put ( "sil8l" , new Integer(520) ) ;
      intsbyphone.put ( "sil8m" , new Integer(521) ) ;
      intsbyphone.put ( "sil8n" , new Integer(522) ) ;
      intsbyphone.put ( "sil8o" , new Integer(523) ) ;
      intsbyphone.put ( "sil8p" , new Integer(524) ) ;
      intsbyphone.put ( "sil8q" , new Integer(525) ) ;
      intsbyphone.put ( "sil8r" , new Integer(526) ) ;
      intsbyphone.put ( "sil8s" , new Integer(527) ) ;
      intsbyphone.put ( "sil8t" , new Integer(528) ) ;
      intsbyphone.put ( "sil8u" , new Integer(529) ) ;
      intsbyphone.put ( "sil8v" , new Integer(530) ) ;
      intsbyphone.put ( "sil8w" , new Integer(531) ) ;
      intsbyphone.put ( "sil8x" , new Integer(532) ) ;
      intsbyphone.put ( "sil8y" , new Integer(533) ) ;
      intsbyphone.put ( "sil8z" , new Integer(534) ) ;
      intsbyphone.put ( "sil9a" , new Integer(535) ) ;
      intsbyphone.put ( "sil9b" , new Integer(536) ) ;
      intsbyphone.put ( "sil9c" , new Integer(537) ) ;
      intsbyphone.put ( "sil9d" , new Integer(538) ) ;
      intsbyphone.put ( "sil9e" , new Integer(539) ) ;
      intsbyphone.put ( "sil9f" , new Integer(540) ) ;
      intsbyphone.put ( "sil9g" , new Integer(541) ) ;
      intsbyphone.put ( "sil9h" , new Integer(542) ) ;
      intsbyphone.put ( "sil9i" , new Integer(543) ) ;
      intsbyphone.put ( "sil9j" , new Integer(544) ) ;
      intsbyphone.put ( "sil9k" , new Integer(545) ) ;
      intsbyphone.put ( "sil9l" , new Integer(546) ) ;
      intsbyphone.put ( "sil9m" , new Integer(547) ) ;
      intsbyphone.put ( "sil9n" , new Integer(548) ) ;
      intsbyphone.put ( "sil9o" , new Integer(549) ) ;
      intsbyphone.put ( "sil9p" , new Integer(550) ) ;
      intsbyphone.put ( "sil9q" , new Integer(551) ) ;
      intsbyphone.put ( "sil9r" , new Integer(552) ) ;
      intsbyphone.put ( "sil9s" , new Integer(553) ) ;
      intsbyphone.put ( "sil9t" , new Integer(554) ) ;
      intsbyphone.put ( "sil9u" , new Integer(555) ) ;
      intsbyphone.put ( "sil9v" , new Integer(556) ) ;
      intsbyphone.put ( "sil9w" , new Integer(557) ) ;
      intsbyphone.put ( "sil9x" , new Integer(558) ) ;
      intsbyphone.put ( "sil9y" , new Integer(559) ) ;
      intsbyphone.put ( "sil9z" , new Integer(560) ) ;
  };
}
