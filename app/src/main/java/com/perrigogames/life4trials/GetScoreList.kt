package com.perrigogames.life4trials

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.perrigogames.life4.enums.PlayStyle
import com.perrigogames.life4.enums.PlayStyle.SINGLE

class GetScoreList: ActivityResultContract<PlayStyle, List<String>>() {
    override fun createIntent(context: Context, playStyle: PlayStyle?) =
        Intent(ACTION_RETRIEVE_SCORE).apply {
            putExtra(EXTRA_PLAY_STYLE, (playStyle ?: SINGLE).stableId)
        }

    override fun parseResult(resultCode: Int, result: Intent?): List<String>? {
        resultCode == Activity.RESULT_OK || return null
        val count = result?.getIntExtra("SET_COUNT", 0) ?: 0
        return (0..count).flatMap { result?.getStringExtra("SCORE_DATA_$it")?.split("\n") ?: emptyList() }.filterNot { it.isEmpty() }
    }

    open class DummyGetScoreActivity: AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            intent.putExtra(EXTRA_RESULT, "ESP;14;0999760;AAA;pfc;6;6;Dead Heat\n" +
                    "ESP;14;0999690;AAA;pfc;9;9;ありふれたせかいせいふく\n" +
                    "ESP;14;0999690;AAA;pfc;9;9;Shiny World\n" +
                    "DSP;14;0999680;AAA;pfc;10;12;POSSESSION\n" +
                    "ESP;14;0999650;AAA;pfc;7;9;Romancing Layer\n" +
                    "ESP;14;0999640;AAA;pfc;10;10;Poseidon(kors k mix)\n" +
                    "ESP;14;0999610;AAA;pfc;3;3;Right on time (Ryu☆Remix)\n" +
                    "ESP;14;0999590;AAA;pfc;7;7;Sakura Sunrise\n" +
                    "ESP;14;0999560;AAA;pfc;5;5;ロールプレイングゲーム\n" +
                    "ESP;14;0999550;AAA;pfc;14;17;Adularia\n" +
                    "ESP;14;0999540;AAA;pfc;19;19;London EVOLVED ver.C\n" +
                    "ESP;14;0999530;AAA;pfc;6;6;Ace out\n" +
                    "ESP;14;0999530;AAA;pfc;25;25;F4SH10N\n" +
                    "CSP;14;0999520;AAA;pfc;11;11;Show me your moves\n" +
                    "ESP;14;0999510;AAA;pfc;19;20;Ishtar\n" +
                    "ESP;14;0999510;AAA;pfc;3;3;ようこそジャパリパークへ\n" +
                    "ESP;14;0999500;AAA;pfc;7;7;FUNKY SUMMER BEACH\n" +
                    "DSP;14;0999490;AAA;pfc;;;New Century\n" +
                    "ESP;14;0999480;AAA;pfc;8;9;IRON HEART\n" +
                    "DSP;14;0999470;AAA;pfc;29;44;POSSESSION (20th Anniversary Mix)\n" +
                    "ESP;14;0999450;AAA;pfc;8;8;夏色DIARY -DDR mix-\n" +
                    "ESP;14;0999450;AAA;pfc;15;21;on the bounce\n" +
                    "ESP;14;0999440;AAA;pfc;10;10;FIRE FIRE\n" +
                    "CSP;14;0999420;AAA;pfc;4;5;TRIP MACHINE CLIMAX(X-Special)\n" +
                    "ESP;14;0999410;AAA;pfc;13;13;Bounce Trippy\n" +
                    "ESP;14;0999390;AAA;pfc;12;12;不沈艦CANDY\n" +
                    "ESP;14;0999390;AAA;pfc;14;14;Right Time Right Way\n" +
                    "ESP;14;0999380;AAA;pfc;5;5;マインド・ゲーム\n" +
                    "ESP;14;0999250;AAA;pfc;18;20;Electric Dance System Music\n" +
                    "CSP;14;0998710;AAA;gfc;5;6;PARANOiA Rebirth(X-Special)\n" +
                    "ESP;14;0998600;AAA;gfc;9;10;Sakura Mirage\n" +
                    "ESP;14;0998500;AAA;gfc;;;RЁVOLUTIФN\n" +
                    "ESP;14;0998480;AAA;gfc;;;Starlight Fantasia\n" +
                    "ESP;14;0997940;AAA;gfc;;;Daily Lunch Special\n" +
                    "ESP;14;0997890;AAA;gfc;;;Chronos (walk with you remix)\n" +
                    "ESP;14;0997740;AAA;gfc;;;New Generation\n" +
                    "ESP;14;0997280;AAA;gfc;;;VEGA\n" +
                    "CSP;14;0996850;AAA;gfc;;;La libertad\n" +
                    "DSP;14;0996410;AAA;gfc;;;Triple Counter\n" +
                    "ESP;14;0996310;AAA;gfc;;;Pierce The Sky\n" +
                    "ESP;14;0996070;AAA;gfc;;;The Wind of Gold\n" +
                    "CSP;14;0995820;AAA;gfc;;;Confession\n" +
                    "DSP;14;0995750;AAA;gfc;1;2;ORCA\n" +
                    "DSP;14;0995590;AAA;gfc;;;CARTOON HEROES (20th Anniversary Mix)\n" +
                    "CSP;14;0995340;AAA;gfc;;;Healing Vision(X-Special)\n" +
                    "CSP;14;0995260;AAA;gfc;;;PARANOIA EVOLUTION(X-Special)\n" +
                    "ESP;14;0995200;AAA;gfc;3;3;黒髪乱れし修羅となりて～凛 edition～\n" +
                    "ESP;14;0995200;AAA;gfc;;;STERLING SILVER (U1 overground mix)\n" +
                    "CSP;14;0994980;AAA;gfc;;;Top The Charts\n" +
                    "DSP;14;0994820;AAA;gfc;10;11;Pluto Relinquish\n" +
                    "ESP;14;0994580;AAA;gfc;4;4;Drop The Bounce\n" +
                    "ESP;14;0994080;AAA;fc;;;Synergy For Angels\n" +
                    "ESP;14;0993950;AAA;fc;4;4;妖隠し -あやかしかくし-\n" +
                    "DSP;14;0993620;AAA;gfc;;;ÆTHER\n" +
                    "ESP;14;0993490;AAA;gfc;;;Toy Box Factory\n" +
                    "ESP;14;0993480;AAA;fc;;;Samurai Shogun vs. Master Ninja\n" +
                    "DSP;14;0993070;AAA;gfc;;;Avenger\n" +
                    "ESP;14;0992930;AAA;fc;;;London EVOLVED ver.B\n" +
                    "ESP;14;0992340;AAA;gfc;;;Plan 8\n" +
                    "ESP;14;0992300;AAA;gfc;3;3;ホーンテッド★メイドランチ\n" +
                    "CSP;14;0992100;AAA;gfc;;;革命(X-Special)\n" +
                    "CSP;14;0991870;AAA;fc;;;Summer Fairytale\n" +
                    "CSP;14;0991860;AAA;gfc;2;2;ちくわパフェだよ☆CKP\n" +
                    "DSP;14;0991180;AAA;gfc;;;New Decade\n" +
                    "ESP;14;0991080;AAA;fc;;;PUNISHER\n" +
                    "ESP;14;0991010;AAA;gfc;;;nightbird lost wing\n" +
                    "DSP;14;0990900;AAA;fc;;;PARANOiA Revolution\n" +
                    "DSP;14;0990800;AAA;gfc;;;Reach The Sky, Without you\n" +
                    "ESP;14;0990580;AAA;gfc;;;IMANOGUILTS\n" +
                    "DSP;14;0990330;AAA;gfc;4;7;Pursuer\n" +
                    "DSP;14;0990210;AAA;fc;2;4;最小三倍完全数\n" +
                    "ESP;14;0989870;AA+;gfc;;;8000000\n" +
                    "ESP;14;0989550;AA+;gfc;8;10;CHAOS\n" +
                    "CSP;14;0989510;AA+;gfc;;;Love♡Shine わんだふるmix\n" +
                    "DSP;14;0989290;AA+;fc;;;The History of the Future\n" +
                    "DSP;14;0988550;AA+;gfc;;;IX\n" +
                    "ESP;14;0988270;AA+;gfc;4;4;Skywalking\n" +
                    "CSP;14;0988220;AA+;gfc;6;7;ACROSS WORLD\n" +
                    "ESP;14;0988030;AA+;gfc;;;50th Memorial Songs -Beginning Story-\n" +
                    "ESP;14;0987450;AA+;gfc;;;セツナトリップ\n" +
                    "DSP;14;0987390;AA+;fc;;;Anti-Matter\n" +
                    "DSP;14;0986940;AA+;gfc;;;Elemental Creation\n" +
                    "ESP;14;0986720;AA+;fc;;;Empathetic\n" +
                    "ESP;14;0986380;AA+;gfc;;;恋する☆宇宙戦争っ!!\n" +
                    "ESP;14;0985880;AA+;fc;1;1;KEEP ON MOVIN' (Y&Co. DJ BOSS remix)\n" +
                    "ESP;14;0985770;AA+;gfc;;;888\n" +
                    "CSP;14;0985770;AA+;fc;;;New Gravity\n" +
                    "CSP;14;0985530;AA+;gfc;2;2;neko＊neko\n" +
                    "CSP;14;0985150;AA+;gfc;;;B4U (\"VOLTAGE\" Special)\n" +
                    "ESP;14;0984830;AA+;fc;;;NEPHILIM DELTA\n" +
                    "DSP;14;0983850;AA+;gfc;;;Boss Rush\n" +
                    "ESP;14;0982170;AA+;fc;;;HYENA\n" +
                    "ESP;14;0981060;AA+;fc;;;London EVOLVED ver.A\n" +
                    "CSP;14;0980050;AA+;gfc;;;Diamond Night\n" +
                    "ESP;14;0979710;AA+;gfc;;;PRANA\n" +
                    "ESP;14;0979640;AA+;clear;;;Starlight in the Snow\n" +
                    "CSP;14;0978720;AA+;fc;4;4;AM-3P (\"CHAOS\" Special)\n" +
                    "CSP;14;0978220;AA+;gfc;;;Tell me what to do\n" +
                    "ESP;14;0978130;AA+;fc;8;9;Pluto\n" +
                    "ESP;14;0977800;AA+;fc;;;阿波おどり -Awaodori- やっぱり踊りはやめられない\n" +
                    "DSP;14;0976640;AA+;fc;;;Tohoku EVOLVED\n" +
                    "ESP;14;0976280;AA+;gfc;;;野球の遊び方　そしてその歴史　～決定版～\n" +
                    "ESP;14;0973710;AA+;fc;;;突撃！ガラスのニーソ姫！\n" +
                    "CSP;14;0973480;AA+;fc;;;ヤマトなでなで♡かぐや姫\n" +
                    "CSP;14;0973270;AA+;gfc;;;Second Heaven\n" +
                    "ESP;14;0972860;AA+;gfc;;;Healing-D-Vision\n" +
                    "CSP;14;0970430;AA+;gfc;;;JOKER\n" +
                    "ESP;14;0968390;AA+;fc;;;Determination\n" +
                    "ESP;14;0968320;AA+;fc;;;恋はどう？モロ◎波動OK☆方程式！！\n" +
                    "DSP;14;0966200;AA+;fc;;;Pluto The First\n" +
                    "ESP;14;0965640;AA+;fc;4;4;Cytokinesis\n" +
                    "DSP;14;0965390;AA+;gfc;;;MAX.(period)\n" +
                    "ESP;14;0960170;AA+;life4;1;1;Afterimage d'automne\n" +
                    "ESP;14;0951250;AA+;fc;;;この子の七つのお祝いに \n" +
                    "ESP;14;0000000;-;clear;;;ドキドキ☆流星トラップガール!!\n" +
                    "ESP;14;0000000;-;clear;;;ミッドナイト☆WAR\n" +
                    "CSP;14;0000000;-;clear;;;脳漿炸裂ガール\n" +
                    "ESP;14;0000000;-;clear;;;second spring storm\n" +
                    "ESP;15;0999590;AAA;pfc;24;30;未来（ダ）FUTURE\n" +
                    "ESP;15;0999530;AAA;pfc;25;25;ナイト・オブ・ナイツ\n" +
                    "ESP;15;0999470;AAA;pfc;;;starmine\n" +
                    "ESP;15;0999450;AAA;pfc;;;Start a New Day\n" +
                    "ESP;15;0999430;AAA;pfc;6;6;Re:GENERATION\n" +
                    "CSP;15;0999410;AAA;pfc;;;Our Soul\n" +
                    "ESP;15;0999340;AAA;pfc;13;17;SUPER SUMMER SALE\n" +
                    "ESP;15;0998840;AAA;gfc;10;10;Sakura Reflection\n" +
                    "ESP;15;0998830;AAA;gfc;;;ZEPHYRANTHES\n" +
                    "CSP;15;0998650;AAA;gfc;6;7;PARANOiA KCET ～clean mix～\n" +
                    "ESP;15;0998580;AAA;gfc;6;6;Hunny Bunny\n" +
                    "DSP;15;0998580;AAA;gfc;;;PARANOiA ～HADES～\n" +
                    "ESP;15;0997810;AAA;gfc;;;BLACK JACKAL\n" +
                    "ESP;15;0997710;AAA;gfc;;;Air Heroes\n" +
                    "ESP;15;0997450;AAA;gfc;;;The legend of MAX\n" +
                    "CSP;15;0997440;AAA;gfc;4;4;チルノのパーフェクトさんすう教室\n" +
                    "CSP;15;0997400;AAA;gfc;4;4;Cosmic Hurricane\n" +
                    "ESP;15;0997000;AAA;gfc;6;7;Neverland\n" +
                    "ESP;15;0996810;AAA;gfc;4;4;Another Phase\n" +
                    "ESP;15;0996320;AAA;gfc;5;5;Arrabbiata\n" +
                    "ESP;15;0996260;AAA;gfc;;;UNBELIEVABLE (Sparky remix)\n" +
                    "ESP;15;0995830;AAA;gfc;3;4;放課後ストライド\n" +
                    "ESP;15;0995780;AAA;gfc;;;RISING FIRE HAWK\n" +
                    "ESP;15;0995730;AAA;gfc;;;シュレーディンガーの猫\n" +
                    "ESP;15;0995050;AAA;gfc;30;34;Astrogazer\n" +
                    "ESP;15;0995030;AAA;gfc;;;おーまい！らぶりー！すうぃーてぃ！だーりん！\n" +
                    "ESP;15;0994890;AAA;gfc;;;BRILLIANT 2U (AKBK MIX)\n" +
                    "ESP;15;0994460;AAA;gfc;;;灼熱Beach Side Bunny\n" +
                    "ESP;15;0994110;AAA;gfc;;;SPECIAL SUMMER CAMPAIGN!\n" +
                    "ESP;15;0993520;AAA;gfc;;;out of focus\n" +
                    "ESP;15;0993390;AAA;gfc;;;Sand Blow\n" +
                    "ESP;15;0992890;AAA;gfc;5;5;DDR MEGAMIX\n" +
                    "CSP;15;0992520;AAA;gfc;;;無頼ック自己ライザー\n" +
                    "ESP;15;0992430;AAA;gfc;;;Procyon\n" +
                    "CSP;15;0992370;AAA;gfc;1;1;ずっとみつめていて (Ryu☆Remix)\n" +
                    "CSP;15;0991840;AAA;gfc;;;Desert Journey\n" +
                    "CSP;15;0991120;AAA;gfc;;;Chronos\n" +
                    "ESP;15;0991120;AAA;gfc;;;Magnetic\n" +
                    "ESP;15;0991050;AAA;fc;;;CRAZY LOVE\n" +
                    "ESP;15;0990840;AAA;gfc;13;13;MAX 300\n" +
                    "CSP;15;0990790;AAA;gfc;;;Xepher\n" +
                    "ESP;15;0990730;AAA;fc;;;Go For The Top\n" +
                    "ESP;15;0990660;AAA;gfc;4;4;Unreal\n" +
                    "ESP;15;0990640;AAA;life4;3;4;ZETA～素数の世界と超越者～\n" +
                    "ESP;15;0990090;AAA;fc;;;IN BETWEEN\n" +
                    "ESP;15;0990040;AAA;gfc;;;Emera\n" +
                    "CSP;15;0989680;AA+;gfc;2;2;TRIP MACHINE PhoeniX\n" +
                    "CSP;15;0989450;AA+;gfc;1;1;ロストワンの号哭\n" +
                    "ESP;15;0989270;AA+;fc;3;3;バンブーソード・ガール\n" +
                    "ESP;15;0988940;AA+;fc;3;5;Cleopatrysm\n" +
                    "ESP;15;0988660;AA+;gfc;;;エンドルフィン\n" +
                    "ESP;15;0988520;AA+;gfc;;;Windy Fairy\n" +
                    "ESP;15;0987740;AA+;gfc;;;Six String Proof\n" +
                    "ESP;15;0986770;AA+;fc;1;1;Give Me\n" +
                    "ESP;15;0986360;AA+;gfc;7;7;roppongi EVOLVED ver.C\n" +
                    "ESP;15;0985850;AA+;gfc;3;3;PARANOIA survivor\n" +
                    "ESP;15;0985820;AA+;gfc;1;1;腐れ外道とチョコレゐト\n" +
                    "ESP;15;0985540;AA+;fc;2;3;天空の華\n" +
                    "CSP;15;0985440;AA+;gfc;4;4;恋閃繚乱\n" +
                    "CSP;15;0985390;AA+;gfc;;;IMANOGUILTS\n" +
                    "CSP;15;0985140;AA+;life4;;;Amalgamation\n" +
                    "ESP;15;0984950;AA+;fc;8;10;Squeeze\n" +
                    "ESP;15;0984400;AA+;fc;4;4;First Time\n" +
                    "ESP;15;0983500;AA+;life4;;;SILVER☆DREAM\n" +
                    "CSP;15;0983240;AA+;gfc;2;2;SABER WING (satellite silhouette remix)\n" +
                    "CSP;15;0983200;AA+;gfc;8;8;Condor\n" +
                    "ESP;15;0981780;AA+;life4;10;11;osaka EVOLVED -毎度、おおきに！- (TYPE3)\n" +
                    "CSP;15;0981610;AA+;gfc;3;4;紅焔\n" +
                    "ESP;15;0981480;AA+;fc;5;5;Stella Sinistra\n" +
                    "DSP;15;0981090;AA+;fc;14;15;Over The “Period”\n" +
                    "ESP;15;0980750;AA+;life4;5;9;Engraved Mark\n" +
                    "ESP;15;0980640;AA+;fc;4;5;Electronic or Treat!\n" +
                    "DSP;15;0980210;AA+;life4;;;Prey\n" +
                    "CSP;15;0980110;AA+;life4;2;2;Beautiful Dream\n" +
                    "ESP;15;0980060;AA+;fc;;;roppongi EVOLVED ver.B\n" +
                    "ESP;15;0979740;AA+;life4;3;4;MAX 300 (Super-Max-Me Mix)\n" +
                    "ESP;15;0979650;AA+;fc;3;3;御千手メディテーション\n" +
                    "ESP;15;0979480;AA+;gfc;;;roppongi EVOLVED ver.A\n" +
                    "CSP;15;0979330;AA+;life4;5;5;Horatio \n" +
                    "DSP;15;0978160;AA+;fc;;;MAX 360\n" +
                    "CSP;15;0978150;AA+;fc;7;7;Straight Oath\n" +
                    "ESP;15;0977840;AA+;life4;2;3;春風ブローインウィンド\n" +
                    "ESP;15;0977700;AA+;fc;8;8;SUPER SAMURAI\n" +
                    "ESP;15;0977550;AA+;life4;5;5;PARANOIA survivor MAX\n" +
                    "DSP;15;0977090;AA+;gfc;7;7;Valkyrie dimension\n" +
                    "ESP;15;0976780;AA+;life4;;;KIMONO PRINCESS\n" +
                    "ESP;15;0976460;AA+;fc;;;SABER WING (Akira Ishihara Headshot mix)\n" +
                    "ESP;15;0976150;AA+;fc;7;10;Destination\n" +
                    "ESP;15;0975530;AA+;fc;6;6;osaka EVOLVED -毎度、おおきに！- (TYPE2)\n" +
                    "ESP;15;0974530;AA+;fc;13;14;roppongi EVOLVED ver.D\n" +
                    "ESP;15;0974470;AA+;fc;8;10;STULTI\n" +
                    "ESP;15;0973940;AA+;fc;8;10;JOMANDA\n" +
                    "ESP;15;0973720;AA+;life4;9;9;Starlight Fantasia (Endorphins Mix)\n" +
                    "CSP;15;0973490;AA+;life4;7;7;THE REASON\n" +
                    "ESP;15;0973370;AA+;fc;;;osaka EVOLVED -毎度、おおきに！- (TYPE1)\n" +
                    "ESP;15;0973090;AA+;life4;9;9;Horatio \n" +
                    "ESP;15;0973050;AA+;life4;7;8;Spanish Snowy Dance\n" +
                    "ESP;15;0973010;AA+;life4;5;5;TRIP MACHINE PhoeniX\n" +
                    "ESP;15;0972620;AA+;life4;;;ACE FOR ACES\n" +
                    "CSP;15;0972410;AA+;fc;;;FLOWER\n" +
                    "ESP;15;0970950;AA+;fc;4;6;Nostalgia Is Lost\n" +
                    "ESP;15;0970710;AA+;life4;4;4;Helios\n" +
                    "ESP;15;0970660;AA+;gfc;7;7;Remain\n" +
                    "DSP;15;0970030;AA+;life4;;;ENDYMION\n" +
                    "CSP;15;0964700;AA+;clear;2;2;初音ミクの消失\n" +
                    "ESP;15;0954360;AA+;fc;9;9;NGO\n" +
                    "DSP;15;0935980;AA;clear;;;New Era\n" +
                    "CSP;16;0996800;AAA;gfc;;;on the bounce\n" +
                    "ESP;16;0996680;AAA;gfc;;;Life is beautiful\n" +
                    "ESP;16;0995300;AAA;gfc;;;POSSESSION (20th Anniversary Mix)\n" +
                    "ESP;16;0994090;AAA;gfc;9;9;ANNIVERSARY ∴∵∴ ←↓↑→\n" +
                    "ESP;16;0993700;AAA;gfc;;;海神\n" +
                    "ESP;16;0992340;AAA;gfc;;;POSSESSION(EDP Live Mix)\n" +
                    "ESP;16;0991830;AAA;fc;;;Come to Life\n" +
                    "CSP;16;0991740;AAA;gfc;;;打打打打打打打打打打\n" +
                    "CSP;16;0990460;AAA;gfc;;;out of focus\n" +
                    "ESP;16;0989650;AA+;fc;;;PARANOiA ～HADES～\n" +
                    "ESP;16;0989560;AA+;gfc;3;3;MAXX UNLIMITED\n" +
                    "ESP;16;0985990;AA+;fc;3;3;Idola\n" +
                    "ESP;16;0985860;AA+;gfc;;;嘆きの樹\n" +
                    "ESP;16;0985550;AA+;clear;;;Love You More\n" +
                    "ESP;16;0983930;AA+;fc;;;Vanquish The Ghost\n" +
                    "CSP;16;0983390;AA+;clear;;;ナイト・オブ・ナイツ\n" +
                    "ESP;16;0982950;AA+;fc;;;CHAOS Terror-Tech Mix\n" +
                    "ESP;16;0982440;AA+;clear;;;KHAMEN BREAK\n" +
                    "ESP;16;0982280;AA+;gfc;;;Cosy Catastrophe\n" +
                    "ESP;16;0980670;AA+;clear;;;True Blue\n" +
                    "ESP;16;0980160;AA+;fc;1;1;I Love You\n" +
                    "CSP;16;0979190;AA+;fc;;;AWAKE\n" +
                    "ESP;16;0977660;AA+;fc;;;Fascination ～eternal love mix～\n" +
                    "ESP;16;0977340;AA+;gfc;;;Boss Rush\n" +
                    "ESP;16;0976650;AA+;clear;7;7;VANESSA\n" +
                    "CSP;16;0975960;AA+;clear;;;Start a New Day\n" +
                    "ESP;16;0975560;AA+;fc;;;冥\n" +
                    "ESP;16;0974270;AA+;clear;;;New York EVOLVED (Type C)\n" +
                    "CSP;16;0973630;AA+;gfc;;;I'm so Happy\n" +
                    "ESP;16;0973610;AA+;clear;;;The World Ends Now\n" +
                    "ESP;16;0973490;AA+;fc;;;Chinese Snowy Dance\n" +
                    "ESP;16;0973480;AA+;clear;;;BLSTR\n" +
                    "ESP;16;0972750;AA+;fc;7;7;お米の美味しい炊き方、そしてお米を食べることによるその効果。\n" +
                    "CSP;16;0972360;AA+;clear;;;Somehow You Found Me\n" +
                    "CSP;16;0972220;AA+;clear;;;DDR MEGAMIX\n" +
                    "ESP;16;0971480;AA+;clear;;;toy boxer\n" +
                    "CSP;16;0969400;AA+;clear;;;六兆年と一夜物語\n" +
                    "CSP;16;0969280;AA+;gfc;;;Monkey Business\n" +
                    "ESP;16;0967750;AA+;clear;;;New York EVOLVED (Type B)\n" +
                    "ESP;16;0967500;AA+;clear;;;轟け！恋のビーンボール！！\n" +
                    "ESP;16;0966860;AA+;clear;2;2;District of the Shadows\n" +
                    "ESP;16;0966320;AA+;clear;;;CARTOON HEROES (20th Anniversary Mix)\n" +
                    "ESP;16;0966280;AA+;fc;;;Rampage Hero\n" +
                    "ESP;16;0963950;AA+;clear;;;S!ck\n" +
                    "CSP;16;0962980;AA+;clear;;;REVOLUTIONARY ADDICT\n" +
                    "CSP;16;0962740;AA+;clear;3;3;PARANOIA survivor MAX\n" +
                    "ESP;16;0962180;AA+;clear;;;ALPACORE\n" +
                    "ESP;16;0960180;AA+;clear;;;GAIA\n" +
                    "ESP;16;0959440;AA+;clear;;;Trill auf G\n" +
                    "CSP;16;0959080;AA+;clear;7;8;Ishtar\n" +
                    "CSP;16;0958960;AA+;clear;;;Another Phase\n" +
                    "ESP;16;0958740;AA+;clear;5;5;HAPPY☆LUCKY☆YEAPPY\n" +
                    "ESP;16;0956690;AA+;clear;;;Trigger\n" +
                    "CSP;16;0952830;AA+;clear;;;めうめうぺったんたん！！\n" +
                    "CSP;16;0951610;AA+;clear;;;MAX 300 (Super-Max-Me Mix)\n" +
                    "ESP;16;0951240;AA+;clear;;;Blew My Mind\n" +
                    "ESP;16;0950960;AA+;clear;2;2;Truare!\n" +
                    "ESP;16;0950800;AA+;clear;4;4;TRIP MACHINE (xac nanoglide mix)\n" +
                    "ESP;16;0948730;AA;life4;;;Neutrino\n" +
                    "ESP;16;0947010;AA;clear;;;Poochie\n" +
                    "ESP;16;0946630;AA;clear;;;New York EVOLVED (Type A)\n" +
                    "ESP;16;0946510;AA;clear;3;3;Trim\n" +
                    "ESP;16;0945110;AA;clear;;;tokyoEVOLVED (TYPE1)\n" +
                    "ESP;16;0944970;AA;clear;;;PARANOiA-Respect-\n" +
                    "CSP;16;0944740;AA;clear;;;PRANA\n" +
                    "CSP;16;0944010;AA;clear;;;輪廻転生\n" +
                    "ESP;16;0943920;AA;clear;3;3;tokyoEVOLVED (TYPE3)\n" +
                    "CSP;16;0942710;AA;clear;4;4;Pluto\n" +
                    "ESP;16;0941960;AA;clear;;;†渚の小悪魔ラヴリィ～レイディオ†\n" +
                    "ESP;16;0939630;AA;clear;;;SWEET HOME PARTY\n" +
                    "ESP;16;0939270;AA;clear;2;2;tokyoEVOLVED (TYPE2)\n" +
                    "CSP;16;0939220;AA;clear;;;隅田川夏恋歌\n" +
                    "CSP;16;0939140;AA;clear;;;snow prism\n" +
                    "ESP;16;0936010;AA;clear;;;Triple Journey -TAG EDITION-\n" +
                    "ESP;16;0935430;AA;clear;;;MAX.(period)\n" +
                    "ESP;16;0935190;AA;clear;;;Illegal Function Call\n" +
                    "ESP;16;0933400;AA;clear;5;6;TRIP MACHINE EVOLUTION\n" +
                    "ESP;16;0932960;AA;clear;3;3;chaos eater\n" +
                    "CSP;16;0931310;AA;clear;;;CHAOS\n" +
                    "CSP;16;0903930;AA;clear;;;ラクガキスト\n" +
                    "ESP;16;0456410;E;clear;;;Splash Gold\n" +
                    "ESP;17;0992490;AAA;gfc;;;POSSESSION\n" +
                    "CSP;17;0990530;AAA;gfc;;;タイガーランペイジ\n" +
                    "ESP;17;0982160;AA+;gfc;;;ÆTHER\n" +
                    "ESP;17;0975580;AA+;fc;;;Elemental Creation\n" +
                    "ESP;17;0974400;AA+;clear;14;18;ドーパミン\n" +
                    "ESP;17;0973910;AA+;life4;12;16;Reach The Sky, Without you\n" +
                    "CSP;17;0966730;AA+;clear;5;5;MAX 300(X-Special)\n" +
                    "CSP;17;0963730;AA+;clear;;;RISING FIRE HAWK\n" +
                    "CSP;17;0951250;AA+;clear;2;2;London EVOLVED ver.C\n" +
                    "CSP;17;0950640;AA+;clear;2;2;Magnetic\n" +
                    "CSP;17;0947040;AA;clear;;;SABER WING (Akira Ishihara Headshot mix)\n" +
                    "ESP;17;0945020;AA;clear;;;Glitch Angel\n" +
                    "CSP;17;0940990;AA;clear;;;The legend of MAX(X-Special)\n" +
                    "ESP;17;0938780;AA;clear;;;New Century\n" +
                    "ESP;17;0938720;AA;clear;;;New Decade\n" +
                    "CSP;17;0938340;AA;clear;2;2;ΔMAX\n" +
                    "ESP;17;0935530;AA;clear;;;Pluto Relinquish\n" +
                    "ESP;17;0934670;AA;clear;2;4;Triple Counter\n" +
                    "CSP;17;0934400;AA;clear;3;5;JOMANDA\n" +
                    "CSP;17;0933780;AA;clear;2;2;RЁVOLUTIФN\n" +
                    "ESP;17;0931580;AA;clear;;;PRANA＋REVOLUTIONARY ADDICT (U1 DJ Mix)\n" +
                    "ESP;17;0930350;AA;clear;;;PARANOiA (kskst mix)\n" +
                    "CSP;17;0928060;AA;clear;;;恋する☆宇宙戦争っ!!\n" +
                    "ESP;17;0925550;AA;clear;;;Puberty Dysthymia\n" +
                    "ESP;17;0925430;AA;clear;1;1;New Era\n" +
                    "ESP;17;0920140;AA;clear;;;Avenger\n" +
                    "CSP;17;0919730;AA;clear;;;Air Heroes\n" +
                    "CSP;17;0918350;AA;clear;1;1;腐れ外道とチョコレゐト\n" +
                    "ESP;17;0917260;AA;clear;;;Anti-Matter\n" +
                    "ESP;17;0915840;E;clear;;;The History of the Future\n" +
                    "ESP;17;0909390;AA;clear;2;3;ΔMAX\n" +
                    "ESP;17;0909080;AA;clear;1;2;未完成ノ蒸氣驅動乙女 (DDR Edition)\n" +
                    "CSP;17;0905360;AA;clear;;;Emera\n" +
                    "ESP;17;0902900;AA;clear;3;3;Tohoku EVOLVED\n" +
                    "ESP;17;0901560;AA;clear;2;2;Catch Our Fire!\n" +
                    "ESP;17;0901150;AA;clear;5;5;Pluto The First\n" +
                    "ESP;17;0900830;AA;clear;;;Be a Hero!\n" +
                    "ESP;17;0897830;AA-;clear;2;3;最小三倍完全数\n" +
                    "ESP;17;0896480;AA-;clear;2;2;Pursuer\n" +
                    "CSP;17;0892310;AA-;clear;2;3;London EVOLVED ver.A\n" +
                    "ESP;17;0889830;A+;clear;;;Fascination MAXX\n" +
                    "CSP;17;0889400;A+;clear;;;London EVOLVED ver.B\n" +
                    "ESP;17;0888060;A+;clear;;;IX\n" +
                    "CSP;17;0887660;A+;clear;;;Spanish Snowy Dance\n" +
                    "CSP;17;0877700;A+;clear;;;New Generation\n" +
                    "CSP;17;0858920;A+;clear;;;Idola\n" +
                    "CSP;17;0855370;A+;clear;1;1;ホーンテッド★メイドランチ\n" +
                    "CSP;17;0780670;B+;clear;;;CHAOS Terror-Tech Mix\n" +
                    "ESP;17;0707810;E;clear;;5;ORCA\n" +
                    "ESP;17;0000000;-;clear;;;ランカーキラーガール\n" +
                    "ESP;17;0000000;-;clear;;;voltississimo\n" +
                    "ESP;18;0969430;AA+;clear;3;4;MAX 360\n" +
                    "CSP;18;0936810;AA;life4;12;14;Astrogazer\n" +
                    "CSP;18;0922730;AA;clear;;;Fascination ～eternal love mix～\n" +
                    "CSP;18;0922210;AA;clear;3;3;Come to Life\n" +
                    "CSP;18;0917920;AA;clear;;;冥\n" +
                    "CSP;18;0912770;AA;clear;;1;ACE FOR ACES\n" +
                    "CSP;18;0910290;AA;clear;1;2;嘆きの樹\n" +
                    "CSP;18;0909560;AA;clear;1;3;シュレーディンガーの猫\n" +
                    "ESP;18;0905160;AA;clear;;;PARANOiA Revolution\n" +
                    "CSP;18;0904120;AA;clear;2;2;Trigger\n" +
                    "CSP;18;0898990;AA-;clear;;;Fascination MAXX\n" +
                    "ESP;18;0894210;AA-;clear;1;2;Prey\n" +
                    "CSP;18;0891930;AA-;clear;;;Prey\n" +
                    "CSP;18;0885440;A+;clear;;;Elemental Creation\n" +
                    "CSP;18;0877250;A+;clear;;;Go For The Top\n" +
                    "CSP;18;0870570;A+;clear;2;3;Cosy Catastrophe\n" +
                    "CSP;18;0867120;A+;clear;2;3;Pluto The First\n" +
                    "CSP;18;0864900;A+;clear;;;POSSESSION\n" +
                    "ESP;18;0864550;A+;clear;9;9;Over The “Period”\n" +
                    "CSP;18;0860320;A+;clear;1;1;Blew My Mind\n" +
                    "ESP;18;0858890;A+;clear;10;10;EGOISM 440\n" +
                    "CSP;18;0856860;A+;clear;1;1;Neutrino\n" +
                    "CSP;18;0855830;A+;clear;2;2;New Decade\n" +
                    "CSP;18;0845460;A;clear;;;Triple Journey -TAG EDITION-\n" +
                    "CSP;18;0842710;A;clear;;;TRIP MACHINE EVOLUTION\n" +
                    "CSP;18;0840700;A;clear;2;2;Tohoku EVOLVED\n" +
                    "CSP;18;0838700;A;clear;2;2;DEAD END (\"GROOVE RADAR\" Special)\n" +
                    "CSP;18;0838050;A;clear;2;2;PARANOiA ～HADES～\n" +
                    "CSP;18;0836690;A;clear;2;3;New Century\n" +
                    "CSP;18;0836190;A;clear;;;Pluto Relinquish\n" +
                    "ESP;18;0835900;A;clear;2;2;Valkyrie dimension\n" +
                    "ESP;18;0833490;A;clear;2;7;ENDYMION\n" +
                    "CSP;18;0824500;A;clear;;;NGO\n" +
                    "CSP;18;0823660;A;clear;;;First Time\n" +
                    "CSP;18;0819700;A;clear;;;Healing-D-Vision\n" +
                    "CSP;18;0814480;A;clear;;;MAXX UNLIMITED(X-Special)\n" +
                    "CSP;18;0814250;A;clear;;;POSSESSION (20th Anniversary Mix)\n" +
                    "CSP;18;0796100;A-;clear;;1;IX\n" +
                    "CSP;18;0787730;B+;clear;2;2;888\n" +
                    "CSP;18;0761720;B+;clear;1;1;MAX.(period)\n" +
                    "CSP;18;0755590;B+;clear;;;Anti-Matter\n" +
                    "CSP;19;0805020;A;clear;1;7;PARANOiA Revolution\n" +
                    "CSP;19;0718490;E;clear;;;EGOISM 440\n" +
                    "CSP;19;0669200;E;clear;;;Over The “Period”\n" +
                    "CSP;19;0639970;E;clear;;;MAX 360\n" +
                    "CSP;19;0000000;-;clear;;;ENDYMION\n" +
                    "CSP;19;0000000;-;clear;;;Valkyrie dimension".split("\n"))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        const val ACTION_RETRIEVE_SCORE = "jp.linanfine.dsma.GET_SCORE_DATA"
        const val EXTRA_PLAY_STYLE = "life4.intent.extra.score.PLAY_STYLE"
        const val EXTRA_RESULT = "life4.intent.extra.score.RESULT"
    }
}
