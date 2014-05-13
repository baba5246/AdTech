package learner;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class PComp implements Comparable<PComp> {
	
	String id;
	double prob;

	@Override
	public int compareTo(PComp o) {
		return Double.compare(prob, o.prob);
	}
}

/****************************
 *  いまちゃんと動きません！   *
 ****************************/

public class NIPS {
	
	public static void main(String[] args) throws Exception {
		
		Scanner sc = new Scanner(new File("data/docword.nips.txt"));
		int D = sc.nextInt();
		int W = sc.nextInt();
		int N = sc.nextInt();
		
		List<Token> tlist = new ArrayList<Token>();
		for (int i = 0; i < N; ++i) {
			int did = sc.nextInt() - 1;
			int wid = sc.nextInt() - 1;
			int count = sc.nextInt();
			for (int c = 0; c < count; ++c) {
				tlist.add(new Token(String.valueOf(did), String.valueOf(wid)));
			}
		}
		
		String words[] = new String[W];
		sc = new Scanner(new File("data/vocab.nips.txt"));
		for (int i = 0; i < W; ++i) {
			words[i] = sc.nextLine();
		}
		
		int K = 50;
		LDA lda = new LDA(D, K, W, tlist);
		for (int i = 0; i <= 20; ++i) {
			lda.update(1);
			if (i % 10 == 0) {
				PrintWriter out = new PrintWriter("output/wordtopic" + i
						+ ".txt");
				List<Map<String, Double>> phi = lda.getPhi();
				outputWordTopicProb(phi, words, out);
				out.close();
			}
		}
		sc.close();
	}

	private static void outputWordTopicProb(List<Map<String, Double>> phi, String[] words,	PrintWriter out) {
		
		int K = phi.size();
		for (int k = 0; k < K; ++k) {
			out.println("topic : " + k);
			List<PComp> ps = new ArrayList<PComp>();
			for (String key : phi.get(k).keySet()) {
				PComp pc = new PComp();
				pc.id = key;
				pc.prob = phi.get(k).get(key);
				ps.add(pc);
			}
			Collections.sort(ps);
			for (int i = 0; i < 10; ++i) {
				// output related word
				PComp p = ps.get(ps.size() - 1 - i);
				out.println(p.id + " " + p.prob);
			}
		}
	}
}
