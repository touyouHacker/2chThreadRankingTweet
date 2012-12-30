package jp.co.touyouhk.twitter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TweetSimple {

	/**
	 * Twitterのアクセスキーファイル名
	 */
	public static String twitterKeyFile = "twitterKey.ini";

	private static Twitter twitter = null;
	private static String consumerKey = null;
	private static String comsumerSecret = null;
	private static String accessToken = null;
	private static String accessTokenSecret = null;

	private TweetSimple() {
		// Twitterのファクトリー形式をそのまま使用
	}

	/**
	 * Twitterの設定ファイルを読み込みTwitterFactoryクラスのインスタンスを取得します
	 * 
	 * @return 成功ならtrue,失敗ならfalse
	 */
	public static boolean init() {

		// ■Twitterキー設定ファイル読み込み
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new FileInputStream(twitterKeyFile),
					"UTF-8");
			BufferedReader br = new BufferedReader(isr);

			consumerKey = br.readLine();
			comsumerSecret = br.readLine();
			accessToken = br.readLine();
			accessTokenSecret = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		twitter = new TwitterFactory().getInstance();
		if (twitter == null) {
			System.out.println("twitterクラスの初期化失敗");

			// 異常ルート
			return false;
		}

		twitter.setOAuthConsumer(consumerKey, comsumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));
		return true;
	}

	/**
	 * 指定されたメッセージ文章をツイートします
	 * @param tweetMsg ツイートするメッセージ文章
	 * @return 成功ならtrue,失敗ならfalse
	 */
	public static boolean tweet(String tweetMsg) {

		try {
			twitter.updateStatus(tweetMsg);
			System.out.println("ツイートしました  →" + tweetMsg);
		} catch (TwitterException te) {
			System.out.println("ツイートに失敗しました。" + te.getMessage());
			return false;
		}
		
		return true;
	}
}
