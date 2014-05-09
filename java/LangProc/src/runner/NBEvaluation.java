package runner;

import io.BadFileNameException;
import io.Reader;
import io.Serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import learner.NaiveBays;
import preprocessing.Mecab;

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

		// Naive Bays
		// 分類器の学習結果を読み込み
		NaiveBays nb = (NaiveBays)Serializer.readObject(serFilePath);

		// 評価を算出し、標準出力に吐き出す
		evaluate(allCateDescWords, categories, nb);
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
	
	/**
	 * 評価を算出し、結果を標準出力に出す
	 * @param allCateDescWords 全カテゴリの各説明文の単語たち
	 * @param categories カテゴリ名配列
	 * @param nb ナイーブベイズ分類器
	 */
	private void evaluate(List<List<String[]>> allCateDescWords, String[] categories, NaiveBays nb) {
		
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
