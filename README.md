2chThreadRankingTweet
=====================

2channel Thread Ranking (2TRT)  
#説明
2chの特定の板のスレッド一覧を取得し勢いランキングを算出しツイートするボットになります。

Twitterの開発用のアカウント（コンシューマーキー、シークレットトークン）が必要になります。

詳しくはグーグル先生に聞いてください。

#ビルド方法
Eclipse等で展開してコンパイルしてjarに固めます。(2TRTexe.jar とする)

#実行方法
必要なもの。

JavaとTiwitterキー設定ファイル。

jarと同じフォルダに以下を4行に並べたtwitterKey.iniファイルを用意してください。

consumerKey  
comsumerSecret  
accessToken  
accessTokenSecret  

ファイル名はデフォルトはtwitterKey.ini。

それ以外を使用する場合は実行引数-iで任意のファイルを指定できます。

コマンドラインで以下のように実行します

##例1
`java -jar 2TRTexe.jar http://hayabusa3.2ch.net/news/ 30 5 -h "スレ勢いランキング/ニュース速報" -f "#2ch"`

-jar 2TRTexe.jar 以下が2TRTが認識できるオプションになります。

http://hayabusa3.2ch.net/news/ スレのURL

30 →30分に1回スレッド一覧を取得

5 →ランキングの数。5ならTOP5をツイート

-h "スレ勢いランキング/ニュース速報" →　ツイート文章の先頭ヘッダ

-f "#2ch" →　ツイート文章の後方フッタ

##例2
`java -jar 2TRTexe.jar http://engawa.2ch.net/poverty/ 30 5 -h "スレ勢いランキング/ニュー速(嫌儲)板" -f #2ch -i twitterKey_poverty.ini`

-i デフォルト名以外のTwitterの設定ファイルを指定

通常はWindowsならバッチファイル

Unix系はシェルを書いて起動しましょう。
