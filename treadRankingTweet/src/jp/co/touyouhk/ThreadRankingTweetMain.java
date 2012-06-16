package jp.co.touyouhk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.touyouhk.nichannel.subjecttext.SubjectTextEntity;
import jp.co.touyouhk.nichannel.subjecttext.SubjectTextUtil;
import jp.co.touyouhk.twitter.TweetSimple;
import jp.co.touyouhk.util.UniversalUtil;

/**
 * スレッドランキングツイート(仮)
 * 2chThreadRankingTweet (2chTRT)
 * Version 0.1.1
 * @author Wizard1
 * 2chのスレッドランキングをツイートするメインクラス
 * 
 * テストケースのため全体的にアクセス権はpublicにしてます。
 */
public class ThreadRankingTweetMain {

	public static final String subjectTextDirectory = "subjectText";
	
	//TODO 後でConstant(変数定義)クラスに移動
	// ツイッターの文字制限数
	public static final int tweetCharCountLimit = 140;
	
	// 分割ツイートの時に一時待機する秒数
	public static final int separateTweetWaitTime = 30;
	
	// 板のURL http://kamome.2ch.net/anime/
	public static String itaUrl = null;
	// ループする時間間隔(分単位)
	public static int loopTime = 30;
	// ランキングをツイートするスレの数、ベスト5なら5
	public static int rankingBestCount = 3;
	// ランキングツイート文の前に載せたい文字列
	public static String tweetPreString = "";
	// ランキングツイート文の後に載せたい文字列
	public static String  tweetAfterString = "";
	
	// ループのカウンタ
	private static int loopConter;
	
	/**
	 * @param args　コマンドライン引数
	 */
	public static void main(String[] args) {

		// ■コマンドライン引数解釈
		if(!parseArgument(args)){
			//失敗ならアプリを終了
			System.exit(-1);
		}

		// ■Twitterキーの読み込み
		if(!TweetSimple.init()){
			//失敗ならアプリを終了
			System.exit(-1);
		}

		// ■subject.txt保存フォルダの生成
		String saveFolder = saveFolderCreate();
		if( saveFolder == null){
			//失敗ならアプリを終了
			System.exit(-1);
		}

		// 旧スレ一覧 をエンティティリストにしたもの
		List<SubjectTextEntity> oldList = null;
		// 新スレ一覧をエンティティリストにしたもの
		List<SubjectTextEntity> newList = null; 
		
		while (true) {
			loopConter++;

			System.out.println("------------ ループ " + loopConter + "回目");

			// ■subjectTextダウンロード
			String subjectFileNameFullPath = SubjectTextUtil.download(itaUrl,
					saveFolder);

			if (subjectFileNameFullPath == null) {
				System.out.println("subject.txtのダウンロードに失敗");
				endLoopWorkAndSleep();
				continue;
			}

			// ■subjectTextのモデルの作成
			newList = SubjectTextUtil
					.subjectText2Entity(subjectFileNameFullPath);

			// ■ループ回数診断
			if (loopConter == 1) {
				System.out.println("初回起動のためツイートしません。");
				// ループ2回目のためにoldListへnewListを格納
				oldList = newList;
				endLoopWorkAndSleep();
				continue;
			}

			// ■ランキングの算出
			List<SubjectTextEntity> rankingList = SubjectTextUtil
					.generateTreadRanking(oldList, newList);

			// 次回ループのためにoldListにnewListを格納
			oldList = newList;

			// ■ランキング上位から指定したTOP数だけ抜き出しツイート文を作成
			List <String> tweetList = treadRanking2tweet(rankingList);

			for (String tweetText : tweetList) {
				// ■ツイート文の整合性チェック(140文字以内かどうか）
				if (!tweetStingCheack(tweetText)) {
					System.out.println("ツイート文章が異常です、スキップします");
					System.out.println(tweetText);
					continue;
				}

				// ■ツイート
				TweetSimple.tweet(tweetText);

				/*
				 * TODO ツイートに失敗しても処理は続行されループへ処理が流れる。
				 * ツイートの認証失敗などの、致命的なエラーの時はプロセスを終了したい。
				 */
				
				// 連続でツイートするとBANされそうなので少し待つ
				try {
					Thread.sleep(separateTweetWaitTime * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// ■スリープ
			endLoopWorkAndSleep();
		}

	}
	
	
	/**
	 * メインクラスに渡された引数を解釈して変数に格納します。
	 * 引数が足りない場合false、それ以外true
	 * TODO 渡された引数の異常検出、1分とかベスト100とか
	 * @param argment メインクラスに渡された引数
	 * @return 成功したらtrue、失敗したらfalse
	 */
	public static boolean parseArgument(String [] argment){
		
		if (argment.length < 1) {
			System.out.println("★引数が足りません。");
			System.out.println(" ");
			System.out.println("java treadRankingTweet [引数1] [引数2] [引数3] [引数4] [引数5]");
			System.out.println("引数1 必須、板のURLを指定");
			System.out.println("引数2 準必須、[Default 30] 繰り返す時間の間隔を分で指定 (10分以上を推奨、あまり短いと2chからBANされる)");
			System.out.println("引数3 準必須、[Default 3] ランキング数を出すスレの数 (1～3を推奨)");
			System.out.println("引数4 オプション、ランキングのツイート文の前に入れたい文字列を入れる(～板スレッドランク)");
			System.out.println("引数5 オプション、ランキングのツイート文の後に入れたい文字列を入れる(#2chとか#akibaとか)");
			System.out.println(" ");
			System.out.println("例1, treadRankingTweet http://raicho.2ch.net/newsplus/");
			System.out.println("例2, treadRankingTweet http://raicho.2ch.net/newsplus/ 20 3");
			System.out.println("例3, treadRankingTweet http://raicho.2ch.net/newsplus/ 20 3 \"N速プラス板スレッドランキング\" \"#2ch\"");
			return false;
		}
		
		itaUrl = argment[0];
		
		// URLの検証
		try {
			new URL(itaUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		}
		
		try {
			if (argment.length >= 2)
				loopTime = Integer.valueOf(argment[1]);
			if (argment.length >= 3)
				rankingBestCount = Integer.valueOf(argment[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		if (argment.length >= 4)
			tweetPreString = argment[3];
		if (argment.length >= 5)
			tweetAfterString = argment[4];

		// TODO 異常検出
		
		return true;
	}
	

	/**
	 * Subject.txtフォルダを生成します
	 * すでにフォルダが生成されている場合は処理をせずに成功とみなします。
	 * @return 成功したら、フォルダのフルパスを返却、失敗したらNULL
	 */
	public static String saveFolderCreate() {
		// ファイル区切り文字 (UNIX では「/」,Windowsでは「\」)
		String fileSeparator = System.getProperties().getProperty(
				"file.separator");

		// カレントディレクトリの取得 eg: C:\work
		String currentDirectory = System.getProperties().getProperty("user.dir");

		//URLから板の英名抽出
		String [] itaUrls = itaUrl.split("/");
		String itaName = (itaUrls[itaUrls.length -1 ]);
		
		// subjectフォルダーのパス名の生成
		// subjectTextフォルダの中に板名でフォルダを作成します
		String createDirectoryPath = currentDirectory + fileSeparator
				+ subjectTextDirectory + fileSeparator + itaName;

		 if(UniversalUtil.createDirectryExistCheak(createDirectoryPath)){
			 return createDirectoryPath;
		 } else {
			 return null;
		 }

	}
	

	/**
	 * スレッドランキングからツイート文を作成します
	 * 
	 * @param rankList ランキングリストのエンティティリスト(降順)
	 * @return ツイートする文章の配列
	 */
	public static List<String> treadRanking2tweet(List<SubjectTextEntity> rankList) {
		
		List <String> tweetList = new ArrayList<String>();
		
		/*
		 * 例
		 * スレッド勢いランキング ～～～板   ■1位    日本も徴兵制度を導入するべきだよな。。もし今戦争になってまともに戦える奴が何人いるのか・・・ http://hatsukari.2ch.net/test/read.cgi/news/1316775795/ 10レス/30分    #2ch
		 * スレッド勢いランキング ～～～板   ■2位    auの秋冬モデルのリークきたああああ　公式ページがおもらし　EVO 3DもPhoton 4Gもあるぞおお  http://hatsukari.2ch.net/test/read.cgi/news/1316873414/ 10レス/30分 #2ch
		 * スレッド勢いランキング ～～～板   ■3位    【世界通貨危機前夜か!?】 各国こぞって自国通貨買い･ドル売りの為替介入実施 一方､日本はやっぱり円高 http://hatsukari.2ch.net/test/read.cgi/news/1316878450/ 10レス/30分  #2ch
		 * 
		 */
		
		for(int i=0; i < rankingBestCount ; i++){
			String tweetText = "";
			tweetText += tweetPreString + "　";
			
			// 順位
			int rankingNum = i + 1;
			
			tweetText += "■"+ rankingNum +"位　";			
			tweetText += rankList.get(i).getTitle() + "　";
			tweetText += SubjectTextUtil.treadNumber2datURL(itaUrl, rankList.get(i).getThreadNumber()) + "　";
			tweetText += rankList.get(i).getResInterval() + "レス/" + loopTime + "分" + "　";
			
			tweetText += tweetAfterString + "　"; 

			//1970年からの13桁の秒数　botIdとして使用。重複ツイート防止
			//tweetText += "ID" + UniversalUtil.getTimeString();
			
			tweetList.add(tweetText);
		}
		
		return tweetList;
	}

	/**
	 * ツイート文章を検証します。 TODO/検証で異常を検知したらアラートを出したり、修正したりします。
	 * 
	 * @param tweetString
	 * @return 正常ならtrue、異常ならfalse
	 */
	public static boolean tweetStingCheack(String tweetString) {

		// URL短縮の考慮
		/* [57] http://raicho.2ch.net/test/read.cgi/newsplus/1316967387/
		 * ↓
		 * [21] http://t.co/ydW8IUBC
		 * 
		 * 57-21 = 36文字余裕ができる;
		 * に変換される。
		 * 
		 * 板名は可変なので 
		 * http://raicho.2ch.net/newsplus/ + test/read.cgi/1316967387/
		 * 可変 + 25
		 * (可変 + 25) - 21 = 余剰 
		 */
		
		// 余剰(文字数のあまりのスペース)
		int surplus = (itaUrl.length() + 25) - 21;
		// URL短縮後文字列(シミュレート)
		int simLength = tweetString.length() - surplus;
				
		System.out.print("文字数制限チェック // 文字数:" + tweetString.length());
		System.out.println("  /URL短縮後文字数(シミュレート):" + simLength);
		
		// 140文字オーバーの時エラー
		if (simLength > tweetCharCountLimit ) {
			System.out.println("文字制限オーバー ");
			return false;
		}
		
		System.out.println("文字数制限チェックOK");
		
		// TODO 140文字以上の場合、いくらか文字を削る

		// TODO 140文字オーバーの場合、アラートを鳴らす(デスクトップウィンドorメール送信等)

		return true;
	}
	
	
	/**
	 * 1ループ終了時の共通的な終了処理と、スレッドスリープを呼び出します。
	 * スレッドスリープでの失敗は異常ルートなのでプロセスを終了させます。
	 * メソッド化するコード量はないですがメイン関数の複数のIF入れ子の防止で作成しました。
	 * @return true
	 */
	public static boolean endLoopWorkAndSleep() {
		// ■スリープ
		System.out.println("スリープモードに移行します " + loopTime + "分");
		try {
			Thread.sleep(loopTime * 1000 * 60);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// スレッドの割り込み例外・・異常ルート
			System.exit(-1);
		}
		return true;
	}

}
