

import io.BadFileNameException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import preprocessing.Mecab;
import runner.NBEvaluation;
import runner.NBTrain;

public class Main {

	public static void tempmain(String[] args) {
		
		if (args.length != 2) {
			for (String s : args) System.out.println(s);
			System.out.println("引数に誤りがあります。");
			return;
		}
		// 引数からCSVファイルパスを取得
		String csvFilePath = args[1];

		// 保存するファイル名を作成
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String serFilePath = "nb" + "_" + sdf.format(cal.getTime());
		
        // 学習
		NBTrain learner = new NBTrain();
		try {
			learner.train(csvFilePath, serFilePath);
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		// 評価
		NBEvaluation eval = new NBEvaluation();
		try {
			eval.test(csvFilePath, serFilePath);
		} catch (BadFileNameException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("引数に誤りがあります。");
			return;
		}
		
		Mecab mecab = new Mecab();
		String[] words = mecab.extractWordsFromDoc(args[0]);
		for (String w : words) System.out.println(w);
	}
}
