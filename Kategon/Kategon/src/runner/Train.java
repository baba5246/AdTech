package runner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import io.BadFileNameException;
import io.CsvBadFormatException;
import io.Reader;
import io.Serializer;
import preprocessing.Mecab;
import preprocessing.TFIDF;
import learning.NaiveBays;

public class Train {
	
	/**
	 * 分類器の学習を行って、分類器を保存する
	 * @param csvFilePath カテゴリ情報とJSONファイルパスが記載されたCSVファイルのパス
	 */
	public void train(String csvFilePath) {
		
		// 教師データのcsv読み込み、nullなら終了。
		List<String[]> csvData = readCsv(csvFilePath);
		if (csvData == null) return;
		
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
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String filename = "nb" + "_" + sdf.format(cal.getTime()) + "_th" + (int)(threshold*10000);
		try {
			Serializer.wirteObject(nb, filename);
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * CSV読み込み
	 * @param filepath CSVファイルのパス
	 * @return List型のCSVのデータ
	 */
	private List<String[]> readCsv(String filepath) {
		
		List<String[]> csvData = null;
		
		Reader reader = new Reader();
		try {
			csvData = reader.readCSV(filepath);
		} catch (CsvBadFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return csvData;
	}
	
	/**
	 * JSON読み込み
	 * @param filepath JSONファイルのパス
	 * @return Map型のJSONデータ
	 */
	private Map<String, Map<String, Object>> readJson(String filepath) {
		
		Map<String, Map<String, Object>> jsonData = null;
		
		Reader reader = new Reader();
		try {
			jsonData = reader.readJSON(filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonData;
		
	}
	
	/**
	 * JSONから説明文を取得する
	 * @param paths JSONデータのパスの配列
	 * @return 各カテゴリの説明文リスト
	 */
	private List<List<String>> getAllCateDescs(String[] paths) {
		
		List<List<String>> allCateDescs = new ArrayList<List<String>>();
		
		for (int i = 0; i < paths.length; i++) {
			
			// jsonデータ取得、nullなら終了。
			Map<String, Map<String, Object>> jsonData = readJson(paths[i]);
			if (jsonData == null) break;
			
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
