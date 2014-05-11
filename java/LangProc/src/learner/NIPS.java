package learner;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class PComp implements Comparable<PComp> {
	
	int id;
	double prob;

	@Override
	public int compareTo(PComp o) {
		return Double.compare(prob, o.prob);
	}
}

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
				tlist.add(new Token(did, wid));
			}
		}
		
		String words[] = new String[W];
		sc = new Scanner(new File("data/vocab.nips.txt"));
		for (int i = 0; i < W; ++i) {
			words[i] = sc.nextLine();
		}
		
		int K = 50;
		LDA lda = new LDA(D, K, W, tlist);
		for (int i = 0; i <= 200; ++i) {
			lda.update();
			if (i % 10 == 0) {
				PrintWriter out = new PrintWriter("output/wordtopic" + i
						+ ".txt");
				double phi[][] = lda.getPhi();
				outputWordTopicProb(phi, words, out);
				out.close();
			}
		}
		sc.close();
	}

	private static void outputWordTopicProb(double phi[][], String[] words,	PrintWriter out) {
		
		int K = phi.length;
		int W = phi[0].length;
		for (int k = 0; k < K; ++k) {
			out.println("topic : " + k);
			PComp ps[] = new PComp[W];
			for (int w = 0; w < W; ++w) {
				PComp pc = new PComp();
				pc.id = w;
				pc.prob = phi[k][w];
				ps[w] = pc;
			}
			Arrays.sort(ps);
			for (int i = 0; i < 10; ++i) {
				// output related word
				PComp p = ps[W - 1 - i];
				out.println(words[p.id] + " " + p.prob);
			}
		}
	}
}
