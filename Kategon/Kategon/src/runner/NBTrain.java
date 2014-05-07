package runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		
		// 教師データのcsv読み込み、nullなら終了。
		Reader reader = new Reader();
		List<String[]> csvData = reader.readCSV(csvFilePath);
		
		// カテゴリ情報とjsonパス情報を取得
		String[] categories = csvData.get(0);
		String[] jsonPaths = csvData.get(1);

		// jsonから説明文取得
		List<List<String>> allCateDescs = getAllCateDescs(jsonPaths);
		
		// 単語抽出
		List<List<String[]>> allCateDescWords = new ArrayList<List<String[]>>();
		Mecab mecab = new Mecab();
		for (List<String> cateDescs : allCateDescs) {
			List<String[]> cateDescWords = mecab.extractWordsFromDocs(cateDescs);
			allCateDescWords.add(cateDescWords);
		}
		
		// TFIDF
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
	private List<List<String>> getAllCateDescs(String[] paths) throws IOException {
		
		List<List<String>> allCateDescs = new ArrayList<List<String>>();
		for (int i = 0; i < paths.length; i++) {
			
			// jsonデータ取得、nullなら終了。
			Reader reader = new Reader();
			Map<String, Map<String, Object>> jsonData = reader.readJSON(paths[i]);
			
			// 説明文取得
			List<String> cateDescs = new ArrayList<String>();
			for (String key : jsonData.keySet()) {
				String desc = (String)jsonData.get(key).get("description");
				cateDescs.add(desc);
			}
			// 保存
			allCateDescs.add(cateDescs);
		}
		return allCateDescs;
	}
}
