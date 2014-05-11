package preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDF {

	private double threshold = 0;
//	private double thresholdMin = 0;
//	private double thresholdMax = 0;
//	private double thresholdDelta = 0;
	
	public TFIDF(double threshold) {
		this.threshold = threshold;
	}
	
//	public TFIDF(double thresholdMin, double thresholdMax, double thresholdDelta) {
//		this.thresholdMin = thresholdMin;
//		this.thresholdMax = thresholdMax;
//		this.thresholdDelta = thresholdDelta;
//	}
	
	/**
	 * TF-IDFの計算
	 * @param allCateDescWords 全説明文の単語リスト
	 * @param categories カテゴリ配列 (必ず allCateDescWords.size() == categories.size() であること)
	 * @return カテゴリごとの単語配列
	 */
	public List<String[]> selectWordsWithTFIDF(Map<String, List<String[]>> allCateDescWords) {
		
		// カテゴリごとの単語配列
		List<String[]> cateWords = new ArrayList<String[]>();
		
		// 総文書数のカウント
		double N = countAllDescs(allCateDescWords);
		
		// DFの計算
		Map<String, Integer> df = computeDF(allCateDescWords);
		
		// TFの計算としきい値処理
		for (String cate : allCateDescWords.keySet()) {
			List<String> filteredWords = new ArrayList<String>();
			for (String[] desc : allCateDescWords.get(cate)) {
				Map<String, Integer> tf = computeTF(desc);
				for (String w : desc) {
					double tfidf = (double)tf.get(w) / tf.keySet().size() * Math.log(N/(double)df.get(w));
					if (tfidf > threshold) filteredWords.add(w);
				}
			}
			filteredWords.add(0, cate);
			
			String[] words = new String[filteredWords.size()];
			filteredWords.toArray(words);
			
			cateWords.add(words);
		}
		
		return  cateWords;
	}
	
	/**
	 * 総文書数のカウント
	 * @param allCateDescWords 全説明文の単語リスト
	 * @return 総数
	 */
	private double countAllDescs(Map<String, List<String[]>> allCateDescWords) {
		double N = 0;
		for (List<String[]> cateDescs : allCateDescWords.values()) {
			N += cateDescs.size();
		}
		return N;
	}
	
	/**
	 * DFの計算を行う
	 * @param allCateDescsWords 全説明文の単語リスト
	 * @return 単語ごとのカウント
	 */
	private Map<String, Integer> computeDF(Map<String, List<String[]>> allCateDescsWords) {

		// DFの計算
		Map<String, Integer> df = new HashMap<String, Integer>();
		for (List<String[]> cateDescs : allCateDescsWords.values()) {
			for (String[] desc : cateDescs) {
				List<String> dfContains = new ArrayList<String>();
				for (String w : desc) {
					if (dfContains.contains(w) == true) continue;
					if (df.containsKey(w) == false) df.put(w, 1);
					else df.put(w, df.get(w) + 1);
					dfContains.add(w);
				}
			}
		}
		return df;
	}
	
	/**
	 * TFの計算を行う
	 * @param allCateDescs 全説明文
	 * @return 各単語ごとの出現率
	 */
	private Map<String, Integer> computeTF(String[] desc) {
		// TFの計算
		Map<String, Integer> tf = new HashMap<String, Integer>();
		for (String w : desc) {
			if (tf.containsKey(w) == false) tf.put(w, 1);
			else tf.put(w, tf.get(w) + 1);
		}
		return tf;
	}

}
