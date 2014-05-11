package learner;

import java.io.Serializable;
import java.util.List;

public class LDA implements Serializable {

	private int D; // number of document
	private int K; // number of topic
	private int W; // number of unique word
	private int[][] wordCount;
	private int[][] docCount;
	private int[] topicCount;
	// hyper parameter
	private double alpha, beta;
	private Token[] tokens;
	private double[] P;
	// topic assignment
	private int[] z;

	/** SerializableのID */
	private static final long serialVersionUID = 1L;

	public LDA(int documentNum, int topicNum, int wordNum, List<Token> tlist) {
		this(documentNum, topicNum, wordNum, tlist, 50.0 / topicNum, 0.1);
	}
	
	public LDA(int documentNum, int topicNum, int wordNum, List<Token> tlist, double alpha, double beta) {
		wordCount = new int[wordNum][topicNum];
		topicCount = new int[topicNum];
		docCount = new int[documentNum][topicNum];
		D = documentNum;
		K = topicNum;
		W = wordNum;
		tokens = tlist.toArray(new Token[0]);
		z = new int[tokens.length];
		this.alpha = alpha;
		this.beta = beta;
		P = new double[K];
		init();
	}
	
	private void init() {
		for (int i = 0; i < z.length; ++i) {
			Token t = tokens[i];
			int assign = (int)(Math.random() % K);
			wordCount[t.getWordId()][assign]++;
			docCount[t.getDocId()][assign]++;
			topicCount[assign]++;
			z[i] = assign;
		}
	}
	
	private int selectNextTopic(Token t) {
		for (int k = 0; k < P.length; ++k) {
			P[k] = (wordCount[t.getWordId()][k] + beta)
					* (docCount[t.getDocId()][k] + alpha)
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
		wordCount[t.getWordId()][assign]--;
		docCount[t.getDocId()][assign]--;
		topicCount[assign]--;
		assign = selectNextTopic(t);
		wordCount[t.getWordId()][assign]++;
		docCount[t.getDocId()][assign]++;
		topicCount[assign]++;
		z[tokenId] = assign;
	}
	
	public void update() {
		for (int i = 0; i < z.length; ++i) {
			resample(i);
		}
	}
	
	public double[][] getTheta() {
		double theta[][] = new double[D][K];
		for (int i = 0; i < D; ++i) {
			double sum = 0.0;
			for (int j = 0; j < K; ++j) {
				theta[i][j] = alpha + docCount[i][j];
				sum += theta[i][j];
			}
			// normalize
			double sinv = 1.0 / sum;
			for (int j = 0; j < K; ++j) {
				theta[i][j] *= sinv;
			}
		}
		return theta;
	}

	public double[][] getPhi() {
		double phi[][] = new double[K][W];
		for (int i = 0; i < K; ++i) {
			double sum = 0.0;
			for (int j = 0; j < W; ++j) {
				phi[i][j] = beta + wordCount[j][i];
				sum += phi[i][j];
			}
			// normalize
			double sinv = 1.0 / sum;
			for (int j = 0; j < W; ++j) {
				phi[i][j] *= sinv;
			}
		}
		return phi;
	}
		
}

class Token {
	private int docId;
	private int wordId;

	public Token(int d, int w) {
		setDocId(d);
		setWordId(w);
	}

	public int getDocId() {	return docId; }
	public void setDocId(int docId) { this.docId = docId; }

	public int getWordId() { return wordId; }
	public void setWordId(int wordId) {	this.wordId = wordId; }
	
}
