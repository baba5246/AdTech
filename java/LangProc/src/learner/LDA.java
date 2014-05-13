package learner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LDA implements Serializable {

	// LDA variables
//	private long D; // number of document
	private int K; // number of topic
	private long W; // number of unique word
	private Map<String, int[]> wordCnt;
	private Map<String, int[]> docCnt;
	private int[] topicCount;
	
	// hyper parameter
	private double alpha, beta;
	private Token[] tokens;
	private double[] P;
	
	// topic assignment
	private int[] z;

	/** SerializableのID */
	private static final long serialVersionUID = 1L;

	public LDA(long documentNum, int topicNum, long wordNum, List<Token> tlist) {
		this(documentNum, topicNum, wordNum, tlist, 50.0 / topicNum, 0.1);
	}
	
	public LDA(long documentNum, int topicNum, long wordNum, List<Token> tlist, double alpha, double beta) {

		wordCnt = new HashMap<String, int[]>();
		docCnt = new HashMap<String, int[]>();
		topicCount = new int[topicNum];
//		D = documentNum;
		K = topicNum;
		W = wordNum;
		tokens = new Token[tlist.size()];
		tlist.toArray(tokens);
		z = new int[tokens.length];
		this.alpha = alpha;
		this.beta = beta;
		P = new double[K];
		init();
	}
	
	private void init() {
		for (int i = 0; i < z.length; ++i) {
			Token t = tokens[i];
			String docId = t.getDocId();
			String wordId = t.getWordId();
			int assign = (int)(Math.random() % K);
			
			if (wordCnt.containsKey(wordId) == false) {
				wordCnt.put(wordId, new int[K]);
			}
			wordCnt.get(wordId)[assign]++;
			
			if (docCnt.containsKey(docId) == false) {
				docCnt.put(docId, new int[K]);
			}
			docCnt.get(docId)[assign]++;
			
			topicCount[assign]++;
			z[i] = assign;
		}
	}
	
	private int selectNextTopic(Token t) {
		for (int k = 0; k < P.length; ++k) {
			P[k] = (wordCnt.get(t.getWordId())[k] + beta)
					* (docCnt.get(t.getDocId())[k] + alpha)
					/ (topicCount[k] + W * beta);
			if (k != 0) {
				P[k] += P[k - 1];
			}
		}
		double u = Math.random() * P[K - 1];
		for (int k = 0; k < P.length; ++k) {
			if (u < P[k]) {
				return k;
			}
		}
		return K - 1;
	}
	
	private void resample(int tokenId) {
		Token t = tokens[tokenId];
		int assign = z[tokenId];
		// remove from current topic
		wordCnt.get(t.getWordId())[assign]--;
		docCnt.get(t.getDocId())[assign]--;
		topicCount[assign]--;
		// select a next topic
		assign = selectNextTopic(t);
		wordCnt.get(t.getWordId())[assign]++;
		docCnt.get(t.getDocId())[assign]++;
		topicCount[assign]++;
		z[tokenId] = assign;
	}
	
	public void update(int maxCount) {
		for (int c = 0; c < maxCount; ++c) {
			for (int i = 0; i < z.length; ++i) {
				resample(i);
			}
		}
	}
	
	public Map<String, double[]> getTheta() {
		Map<String, double[]> Theta = new HashMap<String, double[]>();
		for (String key : docCnt.keySet()) {
			Theta.put(key, new double[K]);
			double sum = 0.0;
			for (int j = 0; j < K; ++j) {
				Theta.get(key)[j] = alpha + docCnt.get(key)[j];
				sum += Theta.get(key)[j];
			}
			// normalize
			double sinv = 1.0 / sum;
			for (int j = 0; j < K; ++j) {
				Theta.get(key)[j] *= sinv;
			}
		}
		return Theta;
	}

	public List<Map<String, Double>> getPhi() {
		List<Map<String, Double>> Phi = new ArrayList<Map<String, Double>>();
		for (int i = 0; i < K; ++i) {
			Map<String, Double> wordProbs = new HashMap<String, Double>();
			double sum = 0.0;
			for (String key : wordCnt.keySet()) {
				wordProbs.put(key, beta + wordCnt.get(key)[i]);
				sum += wordProbs.get(key);
			}
			
			// normalize
			double sinv = 1.0 / sum;
			for (String key : wordProbs.keySet()) {
				wordProbs.put(key, wordProbs.get(key)*sinv);
			}
			Phi.add(wordProbs);
		}
		return Phi;
	}
		
	public String getTopicWords() {
		
		StringBuilder b = new StringBuilder();
		
		// 各トピックごとの単語出現確率を取得
		List<Map<String, Double>> Phi = getPhi();
		
		for (int k = 0; k < Phi.size(); k++) {
			b.append("*********  Topic " + k + " ************\n");
			Map<String, Double> phis = Phi.get(k);
			List<WordComp> wcs = new ArrayList<WordComp>();
			for (String w : phis.keySet()) {
				WordComp wc = new WordComp(w, phis.get(w));
				wcs.add(wc);
			}
			Collections.sort(wcs);
			for (int i = 0; i < 10; ++i) {
				// output related word
				WordComp wc = wcs.get(wcs.size() - 1 - i);
				b.append(wc.getId() + ": " + wc.getProb() + "\n");
			}
		}
		
		return new String(b);
	}
}

class WordComp implements Comparable<WordComp> {
	
	private String id;
	private Double prob;
	
	public WordComp(String id, Double prob) {
		this.id = id;
		this.prob = prob;
	}
	
	public String getId() { return id; }
	public Double getProb() { return prob; }

	@Override
	public int compareTo(WordComp other) {
		return Double.compare(prob, other.prob);
	}


}
