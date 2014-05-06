package runner;

import io.CsvBadFormatException;
import io.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataManager {

	/**
	 * CSV読み込み
	 * @param filepath CSVファイルのパス
	 * @return List型のCSVのデータ (失敗時はnullを返す)
	 */
	public static List<String[]> readCsv(String filepath) {
		
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
	 * @return Map型のJSONデータ (失敗時はnullを返す)
	 */
	public static Map<String, Map<String, Object>> readJson(String filepath) {
		
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
	 * @return 各カテゴリの説明文リスト (失敗時は空リストを返す)
	 */
	public static List<List<String>> getAllCateDescs(String[] paths) {
		
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
