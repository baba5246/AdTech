package runner;

import io.BadFileNameException;
import io.Reader;
import io.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import constants.Debug;
import dao.AppDao;
import entity.App;
import learner.NaiveBays;
import preprocessing.Mecab;
import preprocessing.TFIDF;

public class NBEvaluation {
	
	/**
	 * 学習した分類器を評価する
	 * @param csvFilePath CSVファイルのパス
	 * @param serFilePath 分類器を保存したSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に投げる例外
	 * @throws ClassNotFoundException クラスが見つからないときに投げる例外
	 * @throws IOException 各種ファイルの読み込みに失敗したら投げる例外
	 */
	public void test(String csvFilePath, String serFilePath) throws BadFileNameException, ClassNotFoundException, IOException {
		
		// jsonから説明文取得
		Map<String, List<String>> allCateDescs = getAllDescsFromCsv(csvFilePath);

		// 評価
		test(allCateDescs, serFilePath);
	}

	/**
	 * 学習した分類器を評価する
	 * @param dbType 使用するDBを指定する（1: App Store, 2: Google Play）
	 * @param serFilePath 分類器を保存したSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に投げる例外
	 * @throws ClassNotFoundException クラスが見つからないときに投げる例外
	 * @throws IOException 各種ファイルの読み込みに失敗したら投げる例外
	 */
	public void test(int dbType, String serFilePath) throws BadFileNameException, ClassNotFoundException, IOException {

		// DBから説明文取得
		Map<String, List<String>> allCateDescs = getAllDescsFromDB(dbType);
		
		// 評価
		test(allCateDescs, serFilePath);
	}
	
	/**
	 * 学習した分類器を評価する
	 * @param allCateDescs 全カテゴリの全説明文
	 * @param serFilePath 分類器を保存したSERファイルのパス
	 * @throws BadFileNameException 指定したSERファイル名が正しくない場合に投げる例外
	 * @throws ClassNotFoundException クラスが見つからないときに投げる例外
	 * @throws IOException 各種ファイルの読み込みに失敗したら投げる例外
	 */
	private void test(Map<String, List<String>> allCateDescs, String serFilePath) throws BadFileNameException, ClassNotFoundException, IOException {

		// 単語抽出
		Map<String, List<String[]>> allCateDescWords = getAllCateWords(allCateDescs);

		// 分類器の学習結果を読み込み
		NaiveBays nb = savedNaiveBays(serFilePath);

		// 評価を算出し、標準出力に吐き出す
		evaluate(allCateDescWords, nb);
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

	private NaiveBays savedNaiveBays(String serFilePath) throws BadFileNameException, ClassNotFoundException, IOException {

		Debug.console("分類器の読み込みを開始します（" + Serializer.getFilePath(serFilePath) + "）");
		
		NaiveBays nb = (NaiveBays)Serializer.readObject(serFilePath);
		
		Debug.console("読み込みが完了しました（ " + nb + " ）");
		
		return nb;
	}
	
	/**
	 * 評価を算出し、結果を標準出力に出す
	 * @param allCateDescWords 全カテゴリの各説明文の単語たち
	 * @param categories カテゴリ名配列
	 * @param nb ナイーブベイズ分類器
	 */
	private void evaluate(Map<String, List<String[]>> allCateDescWords, NaiveBays nb) {

		Debug.console("精度の算出を開始します。");
		
		double aveAccuracy = 0;
		StringBuilder b = new StringBuilder();
		for (String category : allCateDescWords.keySet()) {
			double count = 0;
			List<String[]> cateWords = allCateDescWords.get(category);
			
			for (int ci = 0; ci < cateWords.size(); ci++) {
				String[] words = cateWords.get(ci);
				
				String actual = nb.classify(words);
				if (category.equals(actual)) {
					count++;
				}
			}
			double accuracy = count / cateWords.size();
			b.append(category + ":" + Math.round(accuracy*1000)/10000 + " ");
			
			aveAccuracy += accuracy;
		}

		aveAccuracy /= (double) allCateDescWords.size();
		b.append("\nAverage accuracy: " + Math.round(aveAccuracy*1000)/10000);
		Debug.console(b);
	}
	
}
