package preprocessing;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class MecabTest {

	@Test
	public void extractWordsFromDocは文章から単語のリストを抽出する() {
		
		String doc = "太郎は次郎にこの本を手渡した。";
		
		Mecab mecab = new Mecab();
		List<String> words = mecab.extractWordsFromDoc(doc);
		String[] actual = new String[words.size()];
		words.toArray(actual);

		String[] matcher = { "太郎", "次郎", "本", "手渡す" };
		assertArrayEquals(actual, matcher);
	}

	@Test
	public void extractWordsFromDocsは文章リストから各文章の単語リストを抽出する() {
		
		List<String> docs = Arrays.asList(	"太郎は次郎にこの本を手渡した。", 
											"次郎はお前にその本を投げた。", 
											"お前は俺にその本を受け流した。");
		
		Mecab mecab = new Mecab();
		List<List<String>> wordsList = mecab.extractWordsFromDocs(docs);

		String[][] matcher = { 	{ "太郎", "次郎", "本", "手渡す" },
								{ "次郎", "お前", "本", "投げる" },
								{ "お前", "俺", "本", "受け流す" } };
		
		for (int i = 0; i < wordsList.size(); i++) {
			List<String> words = wordsList.get(i);
			String[] actual = new String[words.size()];
			words.toArray(actual);
			
			assertArrayEquals(actual, matcher[i]);
		}

	}
}
