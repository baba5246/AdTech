package learning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author a13553
 *
 */
public class NaiveBays implements Serializable {

	/**
	 * SerializableのID
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> categories = new ArrayList<String>();
	private List<String> vocabularies = new ArrayList<String>();
	private Map<String, Map<String, Integer>> wordcount = new HashMap<String, Map<String, Integer>>();
	private Map<String, Integer> catecount = new HashMap<String, Integer>();
	private Map<String, Integer> denominator = new HashMap<String, Integer>();
	
	/**
	 * ナイーブベイズ分類器の Training メソッド
	 * @param data 各カテゴリに出現する文字列リスト
	 */
	public void train(List<String[]> data) {
		// 文書集合からカテゴリを抽出して辞書を初期化
		for (String[] d : data) {
			String cate = d[0];
			if (categories.contains(cate) == false) categories.add(cate);
		}
		for (String cate : categories) {
			if (wordcount.containsKey(cate) == false) 
				wordcount.put(cate, new HashMap<String, Integer>());
			if (catecount.containsKey(cate) == false)
				catecount.put(cate, 0);
		}
		// 文書集合からカテゴリと単語をカウント
		for (String[] d : data) {
			String cate = d[0];
			String[] doc = new String[d.length-1];
			System.arraycopy(d, 1, doc, 0, doc.length);
			catecount.put(cate, catecount.get(cate) + 1);
			for (String word : doc) {
				vocabularies.add(word);
				if (wordcount.get(cate).containsKey(word) == false)
					wordcount.get(cate).put(word, 1);
				else
					wordcount.get(cate).put(word, wordcount.get(cate).get(word) + 1);
			}
		}
		// 単語の条件付き確率の分母の値をあらかじめ一括で計算しておく
		for (String cate : categories) {
			Integer sum = sum(wordcount.get(cate).values());
			denominator.put(cate, sum + vocabularies.size());
		}
	}

	/**
	 * 事後確率の対数 log(P(cate|doc)) がもっとも大きなカテゴリを返す
	 */
	public String classify(String[] doc) {
		String best = "";
		double max = -Double.MAX_VALUE;
		for (String cate : catecount.keySet()) {
			double p = score(doc, cate);
			if (p > max) {
				max = p;
				best = cate;
			}
		}
		return best;
	}

	/**
	 * 単語の条件付き確率 P(word|cat) を求める
	 * @param word 単語
	 * @param cate カテゴリ
	 * @return 確率値
	 */
	public double wordProb(String word, String cate) {
		double wcount = (wordcount.get(cate).get(word) == null) ? 0 : (double)wordcount.get(cate).get(word);
		return (wcount + 1) / denominator.get(cate);
	}

	/**
	 * 文書が与えられたときのカテゴリの事後確率の対数 log(P(cat|doc)) を求める
	 * @param doc 文書（単語配列）
	 * @param cate カテゴリ
	 * @return 事後確率の対数値
	 */
	public double score(String[] doc, String cate) {
		
		double total = sum(catecount.values());
		double score = Math.log((float)(catecount.get(cate)) / total);
		for (String word : doc) {
			score += Math.log(wordProb(word, cate));
		}
		return score;		
	}
	
	@Override
	public String toString() {
		int total = sum(catecount.values());
		return "vocabularies:" + vocabularies + "\ndocuments:" + total + 
				", vocabularies:" + vocabularies.size() + ", categories:" + categories.size();
	}
	
	/**
	 * 数値配列の合計を算出するメソッド
	 * @param ints Collection<Integer>で表される数値配列
	 * @return 合計値
	 */
	private Integer sum(Collection<Integer> ints) {
		Integer sum = 0;
		for (Iterator<Integer> i = ints.iterator(); i.hasNext(); ) sum += i.next();
		return sum;
	}
	
}
