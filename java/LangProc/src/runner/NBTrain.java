package runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.Debug;
import dao.AppDao;
import entity.App;
import io.BadFileNameException;
import io.Reader;
import io.Serializer;
import preprocessing.Mecab;
import preprocessing.TFIDF;
import learner.NaiveBays;

public class NBTrain {
	
	/**
	 * 特徴語抽出で用いるtf-idfの閾値
	 */
	public static final double threshold = 0.20;
	
	
	/**
	 * 分類器の学習を行って、分類器を保存する
	 * @param csvFilePath カテゴリ情報とJSONファイルパスが記載されたCSVファイルのパス
	 * @param serFilePath 学習結果を保存するSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に例外を投げる
	 * @throws IOException IOException 各種ファイルの読み込みに失敗したら例外を投げる
	 */
	public void train(String csvFilePath, String serFilePath) throws BadFileNameException, IOException {
		
		// CSVから説明文取得
		Map<String, List<String>> allCateDescs = getAllDescsFromCsv(csvFilePath);
		
		// 実行
		train(allCateDescs, serFilePath);
	}
	
	/**
	 * 分類器の学習を行って、分類器を保存する
	 * @param dbType 使用するDBを指定する（1: App Store, 2: Google Play）
	 * @param serFilePath 学習結果を保存するSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に例外を投げる
	 * @throws IOException 各種ファイルの読み込みに失敗したら例外を投げる
	 */
	public void train(int dbType, String serFilePath) throws BadFileNameException, IOException {

		// DBから説明文取得
		Map<String, List<String>> allCateDescs = getAllDescsFromDB(dbType);
		
		// 実行
		train(allCateDescs, serFilePath);
	}
	
	/**
	 * 分類器の学習を行って、分類器を保存する
	 * @param allCateDescs 全カテゴリの全説明文
	 * @param serFilePath 学習結果を保存するSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に例外を投げる
	 * @throws IOException 各種ファイルの読み込みに失敗したら例外を投げる
	 */
	private void train(Map<String, List<String>> allCateDescs, String serFilePath) throws BadFileNameException, IOException {

		// 単語抽出
		Map<String, List<String[]>> allCateDescWords = getAllCateWords(allCateDescs);
		
		// TFIDF
		List<String[]> cateWords = extractFeatureWordsWithTFIDF(allCateDescWords, threshold);
		
		// Naive Bays
		NaiveBays nb = runNaiveBays(cateWords);
		
		// 保存
		saveNaiveBays(nb, serFilePath);
	}
	

	/**
	 * JSONのファイルバス配列から全説明文を取得してくる
	 * @param paths JSONのファイルバス配列
	 * @return List<List<String>>型の各カテゴリの説明文リスト
	 * @throws IOException JSONファイル読み込みに失敗したら投げる例外
	 */
	private Map<String, List<String>> getAllDescsFromCsv(String csvFilePath) throws IOException {

		Debug.console("説明文を取得します。");
		
		// 教師データのcsv読み込み、nullなら終了。
		Reader reader = new Reader();
		List<String[]> csvData = reader.readCSV(csvFilePath);
				
		// カテゴリ情報とjsonパス情報を取得
		String[] categories = csvData.get(0);
		String[] jsonPaths = csvData.get(1);

		Map<String, List<String>> allCateDescs = new HashMap<String, List<String>>();
		for (int i = 0; i < jsonPaths.length; i++) {
			
			// jsonデータ取得、nullなら終了。
			reader = new Reader();
			Map<String, Map<String, Object>> jsonData = reader.readJSON(jsonPaths[i]);
			
			// 説明文取得
			List<String> cateDescs = new ArrayList<String>();
			for (String key : jsonData.keySet()) {
				String desc = (String)jsonData.get(key).get("description");
				cateDescs.add(desc);
			}
			// 保存
			allCateDescs.put(categories[i], cateDescs);
		}

		// 結果出力
		StringBuilder b = new StringBuilder("説明文取得が完了しました（");
		for (String cate : allCateDescs.keySet()) b.append(cate + ":" +allCateDescs.get(cate).size() + "件  "); 
		b.append("）");
		Debug.console(b);
		
		return allCateDescs;
	}
	
	/**
	 * DBから全説明文を取得してくる
	 * @param dbType 使用するDBを指定する（1: App Store, 2: Google Play）
	 * @return 全カテゴリの全説明文（カテゴリがkey, 説明文リストがvalue）
	 */
	private Map<String, List<String>> getAllDescsFromDB(int dbType) {

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

		Map<String, List<String>> allCateDescs = new HashMap<String, List<String>>();
		for (App app: data.values()) {
			
			String desc = app.getDescription();
			String cate = app.getCategory();
			
			if (allCateDescs.containsKey(cate) == false) allCateDescs.put(cate, new ArrayList<String>());
			List<String> cateDescs = allCateDescs.get(cate);
			cateDescs.add(desc);
			allCateDescs.put(cate, cateDescs);
		}
		
		// 結果出力
		StringBuilder b = new StringBuilder("説明文取得が完了しました（");
		for (String cate : allCateDescs.keySet()) b.append(cate + ":" +allCateDescs.get(cate).size() + "件  "); 
		b.append("）");
		Debug.console(b);
		
		return allCateDescs;
		
	}

	/**
	 * 説明文から単語を抽出する
	 * @param allCateDescs 全カテゴリの全説明文
	 * @return 全カテゴリの全単語（カテゴリがkey, 単語配列のリストがvalue）
	 */
	private Map<String, List<String[]>> getAllCateWords(Map<String, List<String>> allCateDescs) {

		Debug.console("説明文からMecabで単語を抽出します。");
		
		Map<String, List<String[]>> allCateDescWords = new HashMap<String, List<String[]>>();
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		Mecab mecab = new Mecab();
		for (String cate : allCateDescs.keySet()) {
			List<String> cateDescs = allCateDescs.get(cate);
			List<String[]> cateDescWords = mecab.extractWordsFromDocs(cateDescs);
			allCateDescWords.put(cate, cateDescWords);

			Integer count = 0;
			for (String[] words : cateDescWords) count += words.length;
			countMap.put(cate, count);
		}
		
		StringBuilder b = new StringBuilder("単語抽出が完了しました（");
		for (String cate : allCateDescWords.keySet()) b.append(cate + ":" +countMap.get(cate) + "語  "); 
		b.append("）");
		Debug.console(b);
		
		return allCateDescWords;
	}

	/**
	 * tf-idfで共通語を削除して特徴語を抽出する
	 * @param allCateDescWords 全カテゴリの全単語
	 * @param threshold 閾値
	 * @return 各カテゴリの特徴語配列
	 */
	private List<String[]> extractFeatureWordsWithTFIDF(Map<String, List<String[]>> allCateDescWords, double threshold) {

		Debug.console("tf-idfで単語を削減します。");
		
		TFIDF tfidf = new TFIDF(threshold);
		List<String[]> cateWords = tfidf.selectWordsWithTFIDF(allCateDescWords);

		StringBuilder b = new StringBuilder("tf-idf単語削減が完了しました（");
		for (int ci = 0; ci < cateWords.size(); ci++) {
			b.append(cateWords.get(ci)[0] + ":" + cateWords.get(ci).length + "語  ");
		}
		b.append("）");
		Debug.console(b);
		
		return cateWords;
	}
	
	/**
	 * ナイーブベイズで分類器を学習する
	 * @param cateWords 各カテゴリの単語配列
	 * @return ナイーブベイズ分類器
	 */
	private NaiveBays runNaiveBays(List<String[]> cateWords) {

		Debug.console("ナイーブベイズの学習を開始します。");
		
		NaiveBays nb = new NaiveBays();
		nb.train(cateWords);
		
		Debug.console("ナイーブベイズの学習が完了しました（ " + nb.toString() + " ）");
		
		return nb;
	}

	/**
	 * ナイーブベイズの分類器を保存する
	 * @param nb ナイーブベイズ分類器
	 * @param serFilePath 保存先のSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に投げる例外
	 * @throws IOException 各種ファイルの読み込みに失敗したら投げる例外
	 */
	private void saveNaiveBays(NaiveBays nb, String serFilePath) throws BadFileNameException, IOException {
		
		Debug.console("結果の保存を開始します。");
		
		Serializer.wirteObject(nb, serFilePath);
		
		Debug.console("結果の保存が完了しました（ 保存先:" + Serializer.getFilePath(serFilePath) + " ）");
	}

}
