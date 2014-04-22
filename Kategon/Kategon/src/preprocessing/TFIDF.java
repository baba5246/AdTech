package preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDF {
	
	private double threshold;
	
	public TFIDF(double threshold) {
		this.threshold = threshold;
	}
	
	/**
	 * TF-IDFの計算
	 * @param allCateDescs 全説明文
	 * @param categories カテゴリ配列
	 * @return カテゴリごとの単語配列
	 */
	public List<String[]> tfidf(List<List<String[]>> allCateDescs, List<String> categories) {
		
		// カテゴリごとの単語配列
		List<String[]> cateWords = new ArrayList<String[]>();
		// 総文書数のカウント
		double N = countAllDescs(allCateDescs);
		// DFの計算
		Map<String, Integer> df = computeDF(allCateDescs);
		// TFの計算としきい値処理
		for (int i = 0; i < allCateDescs.size(); i++) {
			List<String> filteredWords = new ArrayList<String>();
			for (String[] desc : allCateDescs.get(i)) {
				Map<String, Integer> tf = computeTF(desc);
				for (String w : desc) {
					double tfidf = (double)tf.get(w) / tf.keySet().size() * Math.log(N/(double)df.get(w));
					if (tfidf > threshold) filteredWords.add(w);
				}
			}
			filteredWords.add(0, categories.get(i));
			cateWords.add((String[])filteredWords.toArray());
		}
		
		return  cateWords;
	}
	
	/**
	 * 総文書数のカウント
	 * @param allCateDescs 全説明文
	 * @return 総数
	 */
	private double countAllDescs(List<List<String[]>> allCateDescs) {
		double N = 0;
		for (List<String[]> cateDescs : allCateDescs) {
			N += cateDescs.size();
		}
		return N;
	}
	
	/**
	 * DFの計算を行う
	 * @param allCateDescs 全説明文
	 * @return 単語ごとのカウント
	 */
	private Map<String, Integer> computeDF(List<List<String[]>> allCateDescs) {

		// DFの計算
		Map<String, Integer> df = new HashMap<String, Integer>();
		for (List<String[]> cateDescs : allCateDescs) {
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
