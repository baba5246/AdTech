package io;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.IOException;

import net.sf.json.JSONObject;

/**
 * 外部ファイルを読み込むReaderクラス
 * @author a13553
 */
public class Reader {

	public Reader() {
		
	}
	
	/**
	 * CSVファイルを読み込むメソッド
	 * @param filepath ファイルパス
	 * @return String配列のリスト
	 */
	public ArrayList<String[]> readCSV(String filepath) throws IOException {
		
		// 結果インスタンス作成
		ArrayList<String[]> result = new ArrayList<String[]>();
		// ファイルを読み込む
		FileReader fr = new FileReader(filepath);
		BufferedReader br = new BufferedReader(fr);
		try {
			// 読み込んだファイルを１行ずつ処理する
			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				// 区切り文字","で分割する
				token = new StringTokenizer(line, ",");
				// 分割した文字を画面出力する
				String[] app = new String[token.countTokens()];
				for (int i = 0; i < token.countTokens(); i++)
					app[i] = token.nextToken();
				// 格納
				result.add(app);
			}
			// 終了処理
			br.close();

		} catch (IOException e) {
			// 例外発生時処理
			throw e;
		}

		return result;
	}
    
	
    /**
     * JSONファイルを読み込むメソッド
     * @param filepath ファイルパス
     * @return HashMap
     */
    @SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> readJSON(String filepath) throws IOException {
    	
    	Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

		// ファイルを読み込む
		FileReader fr = new FileReader(filepath);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb = new StringBuilder();
    	try {
    		String line = "";    		
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			// 終了処理
			br.close();
    	} catch (IOException e) {
			// 例外発生時処理
    		throw e;
    	}
    	// JSONを読み込む
    	String data = new String(sb);
    	JSONObject jsonData = JSONObject.fromObject(data);
    	result = jsonData;
    	
    	return result;
    }

}
