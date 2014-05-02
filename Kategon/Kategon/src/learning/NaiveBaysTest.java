package learning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NaiveBaysTest {

	private static List<String[]> data = new ArrayList<String[]>();
	
	static {
		data.add(new String[]{"yes", "Chinese", "Beijing", "Hongkong"});
		data.add(new String[]{"yes", "Shanghai", "Nanjing", "Hongkong"});
		data.add(new String[]{"yes", "Xuzhou", "Beijing", "Luoyang"});
		data.add(new String[]{"no", "Japan", "Tokyo", "Fukuoka"});
		data.add(new String[]{"no", "Tokyo", "Shibuya", "Ikejiri"});
	}

	@Test
	public void trainは分類器を学習してChinese_Hongkong_Japanをyesと判断する() {
		// 学習
		NaiveBays nb = new NaiveBays();
		nb.train(data);
		// テスト1
		String[] test = {"Chinese", "Hongkong", "Japan"};
		String actual = nb.classify(test);
		assertEquals(actual, "yes");
	}

	@Test
	public void trainは分類器を学習してTokyo_Hongkong_Japanをnoと判断する() {
		// 学習
		NaiveBays nb = new NaiveBays();
		nb.train(data);
		// テスト2
		String[] test = new String[]{"Tokyo", "Hongkong", "Japan"};
		String actual = nb.classify(test);
		assertEquals(actual, "no");
	}
}
