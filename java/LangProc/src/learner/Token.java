package learner;

/**
 * 文書IDと単語IDのペアを保持するクラス（LDA用）
 */
public class Token {
	
	private String docId;
	private String wordId;

	public Token(String d, String w) {
		setDocId(d);
		setWordId(w);
	}

	public String getDocId() { return String.valueOf(docId); }
	public void setDocId(String docId) { this.docId = docId; }

	public String getWordId() { return String.valueOf(wordId); }
	public void setWordId(String wordId) {	this.wordId = wordId; }
	
}
