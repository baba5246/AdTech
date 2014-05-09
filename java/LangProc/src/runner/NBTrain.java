package runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public void train(int dbType, String serFilePath) throws BadFileNameException, IOException {

		// DBから説明文取得
		Map<String, List<String>> allCateDescs = getAllDescsFromDB(dbType);
		
		// 実行
		train(allCateDescs, serFilePath);
	}
	
	private void train(Map<String, List<String>> allCateDescs, String serFilePath) throws BadFileNameException, IOException {
		
		// 単語抽出
		List<List<String[]>> allCateDescWords = new ArrayList<List<String[]>>();
		Mecab mecab = new Mecab();
		for (String cate : allCateDescs.keySet()) {
			List<String> cateDescs = allCateDescs.get(cate);
			List<String[]> cateDescWords = mecab.extractWordsFromDocs(cateDescs);
			allCateDescWords.add(cateDescWords);
		}
		
		// TFIDF
		String[] categories = new String[allCateDescs.keySet().size()];
		double threshold = 0.20;
		TFIDF tfidf = new TFIDF(threshold);
		List<String[]> cateWords = tfidf.selectWordsWithTFIDF(allCateDescWords, categories);
		
		// Naive Bays
		NaiveBays nb = new NaiveBays();
		nb.train(cateWords);
		System.out.println(nb);
		
		// 保存
		Serializer.wirteObject(nb, serFilePath);
	}
	

	/**
	 * JSONのファイルバス配列から全説明文を取得してくる
	 * @param paths JSONのファイルバス配列
	 * @return List<List<String>>型の各カテゴリの説明文リスト
	 * @throws IOException JSONファイル読み込みに失敗したら投げる例外
	 */
	private Map<String, List<String>> getAllDescsFromCsv(String csvFilePath) throws IOException {
		
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
		return allCateDescs;
	}
	
	private Map<String, List<String>> getAllDescsFromDB(int dbType) {
		
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
		for (String key: data.keySet()) {
			
			App app = data.get(key);
			String desc = app.getDescription();
			String cate = app.getCategory();
			
			if (allCateDescs.containsKey(cate) == false) allCateDescs.put(key, new ArrayList<String>());
			List<String> cateDescs = allCateDescs.get(key);
			cateDescs.add(desc);
			allCateDescs.put(key, cateDescs);
		}
		return allCateDescs;
		
	}
}
