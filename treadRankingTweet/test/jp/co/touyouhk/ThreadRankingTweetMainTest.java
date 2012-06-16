package jp.co.touyouhk;

import java.util.ArrayList;
import java.util.List;

import jp.co.touyouhk.nichannel.subjecttext.SubjectTextEntity;
import jp.co.touyouhk.nichannel.subjecttext.SubjectTextUtil;
import junit.framework.TestCase;


public class ThreadRankingTweetMainTest  extends TestCase{
	
	/**
	 * parseArgumentのテスト パターン1 -- 正常系
	 * 
	 * 後方のメソッドでstatic変数を後でいじるのでこのメソッドは一番最初に配置し試験すること
	 */
	public void testParseArgumentPatarn01() {
		
		//01-A 最小引数構成
		String[] arg1 = {"http://kamome.2ch.net/anime/"};
		assertTrue( ThreadRankingTweetMain.parseArgument(arg1));
		
		assertEquals("http://kamome.2ch.net/anime/" , ThreadRankingTweetMain.itaUrl);
		assertEquals(30, ThreadRankingTweetMain.loopTime);
		assertEquals(3, ThreadRankingTweetMain.rankingBestCount);
		assertEquals("", ThreadRankingTweetMain.tweetPreString);
		assertEquals("", ThreadRankingTweetMain.tweetAfterString);
		
		
		//01-B 最大引数構成
		String[] arg2 = {"http://kamome.2ch.net/anime/", "10" , "5" , "勢いランキング [ニュース速報+]板", "#2ch"};
		assertTrue( ThreadRankingTweetMain.parseArgument(arg2));
		
		assertEquals("http://kamome.2ch.net/anime/" , ThreadRankingTweetMain.itaUrl);
		assertEquals(10, ThreadRankingTweetMain.loopTime);
		assertEquals(5, ThreadRankingTweetMain.rankingBestCount);
		assertEquals("勢いランキング [ニュース速報+]板", ThreadRankingTweetMain.tweetPreString);
		assertEquals("#2ch", ThreadRankingTweetMain.tweetAfterString);
		
		//01-C 引数調整確認
		String[] arg3 = {"http://kamome.2ch.net/anime/", "60" , "5" , "", "#2ch"};
		assertTrue( ThreadRankingTweetMain.parseArgument(arg3));
		
		assertEquals("http://kamome.2ch.net/anime/" , ThreadRankingTweetMain.itaUrl);
		assertEquals(60, ThreadRankingTweetMain.loopTime);
		assertEquals(5, ThreadRankingTweetMain.rankingBestCount);
		assertEquals("", ThreadRankingTweetMain.tweetPreString);
		assertEquals("#2ch", ThreadRankingTweetMain.tweetAfterString);
		
	}
	
	/**
	 * parseArgumentのテスト パターン2 -- エラー系
	 */
	public void testParseArgumentPatarn02() {
		
		// 引数なし
		String [] arg = new String[0];
		assertFalse( ThreadRankingTweetMain.parseArgument(arg));
		
		// URL異常
		String[] arg2 = {"xttp:/\\dummyProtokol"};
		assertFalse( ThreadRankingTweetMain.parseArgument(arg2));
		
		// ループタイム異常
		String[] arg3 = {"http://kamome.2ch.net/anime/", "ThisIsErr"};
		assertFalse( ThreadRankingTweetMain.parseArgument(arg3));
		
		// ランキング数異常
		String[] arg4 = {"http://kamome.2ch.net/anime/", "30" , "ThisIsError"};
		assertFalse( ThreadRankingTweetMain.parseArgument(arg4));
		
	}
	
	// treadRanking2tweetメソッドのテスト　目視
	public void testTreadRanking2tweet(){
		
		String oldSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectOld.txt";
		String newSubject = "./test/jp/co/touyouhk/niChannel/subjecttext/testdata/subjectNew.txt";
		
		List<SubjectTextEntity> oldList = SubjectTextUtil
				.subjectText2Entity(oldSubject);
		
		List<SubjectTextEntity> newList = SubjectTextUtil
		.subjectText2Entity(newSubject);
		
		
		List<SubjectTextEntity> rankList = SubjectTextUtil.generateTreadRanking(oldList, newList);
	
		/*
		 * 例
		 * スレッド勢いランキング ～～～板   ■1位    日本も徴兵制度を導入するべきだよな。。もし今戦争になってまともに戦える奴が何人いるのか・・・ http://hatsukari.2ch.net/test/read.cgi/news/1316775795/ 10レス/30分    #2ch ID:1234567890
		 * スレッド勢いランキング ～～～板   ■2位    auの秋冬モデルのリークきたああああ　公式ページがおもらし　EVO 3DもPhoton 4Gもあるぞおお  http://hatsukari.2ch.net/test/read.cgi/news/1316873414/ 10レス/30分 #2ch ID:1234567890
		 * スレッド勢いランキング ～～～板   ■3位    【世界通貨危機前夜か!?】 各国こぞって自国通貨買い･ドル売りの為替介入実施 一方､日本はやっぱり円高 http://hatsukari.2ch.net/test/read.cgi/news/1316878450/ 10レス/30分  #2ch ID:1234567890
		 * 
		 */
		
		ThreadRankingTweetMain.itaUrl = "http://raicho.2ch.net/newsplus/";
		ThreadRankingTweetMain.rankingBestCount = 3;
		ThreadRankingTweetMain.loopTime = 30;
		ThreadRankingTweetMain.tweetPreString = "スレッド勢いランキング [ニュース速報+]板";
		ThreadRankingTweetMain.tweetAfterString = "#2ch";
		
		
		List<String> twList = ThreadRankingTweetMain.treadRanking2tweet(rankList);
		
		for(String tw: twList){
			System.out.println(tw);
		}
		
		
		//スレッド勢いランキング [ニュース速報+]板　■1位　【フジ韓流】全国に波及するフジテレビ抗議デモ　「木を見て森を見ず」「狭いナショナリズム」と一部で疑問の声も★２１ 　http://raicho.2ch.net/test/read.cgi/newsplus/1316967387/　184レス/30分　#2ch　ID:1316977700082
		//スレッド勢いランキング [ニュース速報+]板　■2位　【政治】所得税の増税、２０１３年１月から１０年間行う方針　民主党税調 　http://raicho.2ch.net/test/read.cgi/newsplus/1316959035/　107レス/30分　#2ch　ID:1316977700082
		//スレッド勢いランキング [ニュース速報+]板　■3位　【科学】 光速超えるニュートリノ　実現不可能とされた“タイムマシン”も可能？　専門家ら驚き「検証を」★10 　http://raicho.2ch.net/test/read.cgi/newsplus/1316963462/　104レス/30分　#2ch　ID:1316977700082

		 
		
	}
	
	
	/**
	 * tweetStingCheackのテスト
	 */
	public void testTweetStingCheack(){
		String st1 = "スレッド勢いランキング [ニュース速報+]板　■1位　【フジ韓流】全国に波及するフジテレビ抗議デモ　「木を見て森を見ず」「狭いナショナリズム」と一部で疑問の声も★２１ 　http://raicho.2ch.net/test/read.cgi/newsplus/1316967387/　184レス/30分　#2ch　ID:1316977700082";
		String st2 = "スレッド勢いランキング [ニュース速報+]板　■2位　【政治】所得税の増税、２０１３年１月から１０年間行う方針　民主党税調 　http://raicho.2ch.net/test/read.cgi/newsplus/1316959035/　107レス/30分　#2ch　ID:1316977700082";
		String st3 = "スレッド勢いランキング [ニュース速報+]板　■3位　【科学】 光速超えるニュートリノ　実現不可能とされた“タイムマシン”も可能？　専門家ら驚き「検証を」★10 　http://raicho.2ch.net/test/read.cgi/newsplus/1316963462/　104レス/30分　#2ch　ID:1316977700082";

		ThreadRankingTweetMain.itaUrl = "http://raicho.2ch.net/newsplus/";
		
		List<String> list = new ArrayList<String>();
		
		list.add(st1);
		list.add(st2);
		list.add(st3);
		
		
		for(String st: list){
			System.out.println(st);
			assertTrue(ThreadRankingTweetMain.tweetStingCheack(st));
		}
	}


	

	
}
