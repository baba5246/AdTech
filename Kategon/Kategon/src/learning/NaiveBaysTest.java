package learning;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NaiveBaysTest {

	@Test
	public void trainは分類器を学習する() {
		
		List<String[]> data = new ArrayList<String[]>();
		data.add(new String[]{"yes", "Chinese", "Beijing", "Hongkong"});
		data.add(new String[]{"yes", "Shanghai", "Nanjing", "Hongkong"});
		data.add(new String[]{"yes", "Xuzhou", "Beijing", "Luoyang"});
		data.add(new String[]{"no", "Japan", "Tokyo", "Fukuoka"});
		data.add(new String[]{"no", "Tokyo", "Shibuya", "Ikejiri"});
		
		NaiveBays nb = new NaiveBays();
		nb.train(data);
		System.out.println(nb);
		
		fail("とりあえず失敗");
		
	}

}
