package runner;

import io.BadFileNameException;
import io.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import learning.NaiveBays;
import preprocessing.Mecab;

public class Evaluation {

	public void test(String csvFilePath, String serFilePath) {
		
		// 教師データのcsv読み込み、nullなら終了。
		List<String[]> csvData = DataManager.readCsv(csvFilePath);
		if (csvData == null) return;
		
		// カテゴリ情報とjsonパス情報を取得
		String[] categories = csvData.get(0);
		String[] jsonPaths = csvData.get(1);
		
		// jsonから全説明文取得
		List<List<String>> allCateDescs = DataManager.getAllCateDescs(jsonPaths);
		
		// 単語抽出
		List<List<String[]>> allCateDescWords = new ArrayList<List<String[]>>();
		Mecab mecab = new Mecab();
		for (List<String> cateDescs : allCateDescs) {
			List<String[]> cateDescWords = mecab.extractWordsFromDocs(cateDescs);
			allCateDescWords.add(cateDescWords);
		}

		// Naive Bays
		// 分類器の学習結果を読み込み
		NaiveBays nb = null;
		try {
			nb = (NaiveBays)Serializer.readObject(serFilePath);
		} catch (BadFileNameException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		// 評価		
		double aveAccuracy = 0;
		for (int ai = 0; ai < allCateDescWords.size(); ai++) {
			double count = 0;
			List<String[]> cateWords = allCateDescWords.get(ai);
			
			for (int ci = 0; ci < cateWords.size(); ci++) {
				String[] words = cateWords.get(ci);
				
				String actual = nb.classify(words);
				if (categories[ai].equals(actual)) {
					count++;
					System.out.println("category:" + categories[ai] + ", actual:" + actual);
				}
			}
			double accuracy = count / cateWords.size();
			System.out.println(categories[ai] + ": " + accuracy);
			
			aveAccuracy += accuracy;
		}

		aveAccuracy /= (double) allCateDescWords.size();
		System.out.println("Average accuracy: " + aveAccuracy);
		
		
	}
	
	
	
}
