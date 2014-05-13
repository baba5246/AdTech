package runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import preprocessing.Mecab;
//import preprocessing.TFIDF;
import learner.LDA;
import learner.Token;
import constants.Debug;
import dao.AppDao;
import entity.App;

public class LDARunner {

	/** 特徴語抽出で用いるtf-idfの閾値 */
	public static final double THRESHOLD = 0.20;
	public static final int MAX_ITERATION = 1000;
	
	public void runLDA(int dbType, int topicNum) {

		// DBから説明文取得
		Map<String, String> allAppDocs = getAllDescsFromDB(dbType);
		
		// 単語の抽出
		Map<String, String[]> allDocWords = getDocWords(allAppDocs);
				
		// TFIDF
//		Map<String, String[]> cateWords = extractFeatureWordsWithTFIDF(allCateDescWords, threshold);
		
		// 単語と文書のペア作成
		List<Token> tokens = makeTokens(allDocWords);
		
		// LDAを実行する
		LDA lda = new LDA( allDocWords.size(), topicNum, countAllWords(allDocWords), tokens);
		lda.update(MAX_ITERATION);

		// 文書に含まれる単語たちが属するトピック（重複あり）を
		// θの確率で線形結合したもののうち最大のものをその文書のトピックとする	
		Debug.console(lda.getTopicWords()); // とりあえず出力
		
	}
	
	/**
	 * DBから全説明文を取得してくる
	 * @param dbType 使用するDBを指定する（1: App Store, 2: Google Play）
	 * @return 全カテゴリの全説明文（カテゴリがkey, 説明文リストがvalue）
	 */
	private Map<String, String> getAllDescsFromDB(int dbType) {

		Debug.console("説明文を取得します。");
		
		AppDao mongo = new AppDao();
		Map<String, App> data = null;
		switch (dbType) {
		case AppDao.APP_STORE_TYPE:
			data = mongo.getLocalAppStoreApps();
			break;
		case AppDao.GOOGLE_PLAY_TYPE:
			data = mongo.getLocalGooglePlayApps();
			break;
		}

		Map<String, String> allDocs = new HashMap<String, String>();
		for (App app: data.values()) {
			String id = app.getStoreId();
			String desc = app.getDescription();
			allDocs.put(id, desc);
		}
		
		// 結果出力
		StringBuilder b = new StringBuilder("説明文取得が完了しました（");
		b.append("取得件数:" +allDocs.size() + "件  "); 
		b.append("）");
		Debug.console(b);
		
		return allDocs;
	}
	
	/**
	 * 説明文から単語を抽出する
	 * @param allCateDescs 全カテゴリの全説明文
	 * @return 全カテゴリの全単語（カテゴリがkey, 単語配列のリストがvalue）
	 */
	private Map<String, String[]> getDocWords(Map<String, String> allAppDocs) {

		Debug.console("説明文からMecabで単語を抽出します。");
		
		Map<String, String[]> docWords = new HashMap<String, String[]>();
		Mecab mecab = new Mecab();
		long count = 0;
		for (String id : allAppDocs.keySet()) {
			String[] words = mecab.extractWordsFromDoc(allAppDocs.get(id));
			docWords.put(id, words);
			count += words.length;
		}
		
		StringBuilder b = new StringBuilder("単語抽出が完了しました（");
		b.append("抽出語数:" +count + "語  )"); 
		Debug.console(b);
		
		return docWords;
	}

	/**
	 * トークン（説明文と単語のペア）のリストを作成する
	 * @param allDocWords 説明文ごとの単語配列
	 * @return　トークンのリスト
	 */
	private List<Token> makeTokens(Map<String, String[]> allDocWords) {
		
		List<Token> tokens = new ArrayList<Token>();
		
		for (String docId: allDocWords.keySet()) {
			String[] words = allDocWords.get(docId);
			for (String word : words) tokens.add(new Token(docId, word));
		}
		
		return tokens;
	}
	
	/**
	 * 単語の総数を数える
	 * @param allDocWords 説明文ごとの単語配列
	 * @return トークンのリスト
	 */
	private long countAllWords(Map<String, String[]> allDocWords) {
		long count = 0;
		for (String[] words : allDocWords.values()) count += words.length;
		return count;
	}

}
