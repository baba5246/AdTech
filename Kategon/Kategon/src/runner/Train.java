package runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.BadFileNameException;
import io.Serializer;
import preprocessing.Mecab;
import preprocessing.TFIDF;
import learning.NaiveBays;

public class Train {
	
	/**
	 * 分類器の学習を行って、分類器を保存する
	 * @param csvFilePath カテゴリ情報とJSONファイルパスが記載されたCSVファイルのパス
	 */
	public void train(String csvFilePath, String serFilePath) {
		
		// 教師データのcsv読み込み、nullなら終了。
		List<String[]> csvData = DataManager.readCsv(csvFilePath);
		if (csvData == null) return;
		
		// カテゴリ情報とjsonパス情報を取得
		String[] categories = csvData.get(0);
		String[] jsonPaths = csvData.get(1);
		
		// jsonから説明文取得
		List<List<String>> allCateDescs = DataManager.getAllCateDescs(jsonPaths);
		
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
		try {
			Serializer.wirteObject(nb, serFilePath);
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
