package preprocessing;

import java.util.ArrayList;
import java.util.List;

import org.chasen.mecab.Tagger;
import org.chasen.mecab.Node;

/**
 * Mecabを扱うクラス
 * @author baba
 */
public class Mecab {
	
	private static String[] parts = {"名詞", "動詞", "形容詞", "副詞"}; 
	
	// 静的初期化子でMeCabライブラリの準備
	static {
		try {
			System.loadLibrary("MeCab");
		} catch (UnsatisfiedLinkError e) {
			System.err.println("Cannot load the example native code.\nMake sure your DLYD_LIBRARY_PATH contains \'.\'\n" + e);
			System.exit(1);
		}
	}
	
	/**
	 * 文章から単語リストを抽出する
	 * @param doc 文章を表すString
	 * @return 単語のリスト
	 */
	public String[] extractWordsFromDoc(String doc) {
		
		Tagger tagger = new Tagger();
		Node node = tagger.parseToNode(doc);
		
		List<String> wordList = new ArrayList<String>();
		for (; node != null; node = node.getNext()) {
			String fstr = node.getFeature();
			String[] features = fstr.split(",");
			for (String part : parts) {
				if (features[0].equals(part) == true) {
					if (features[6].equals("*") == true) continue;
					wordList.add(features[6]);
				}
			}
		}
		String[] words = new String[wordList.size()];
		wordList.toArray(words);
		return words;
	}
	
	/**
	 * 文章リストから、各文章ごとに単語リストを抽出する
	 * @param docs 文章を表すStringのリスト
	 * @return 各文章の単語のリスト
	 */
	public List<String[]> extractWordsFromDocs(List<String> docs) {
		
		List<String[]> wordsOfDocs = new ArrayList<String[]>();
		for (String doc : docs) {
			String[] words = extractWordsFromDoc(doc);
			wordsOfDocs.add(words);
		}
		return wordsOfDocs;
	}
}