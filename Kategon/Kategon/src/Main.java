

import io.BadFileNameException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import dao.MongoDao;
import preprocessing.Mecab;
import runner.NBEvaluation;
import runner.NBTrain;

public class Main {

	private static final int METHOD_NAIVE_BAYS = 1;
	private static final int METHOD_MECAB = 2;
	
	public static void main(String[] args) {
		
		int method = checkArgs(args);
		switch (method) {
		case METHOD_NAIVE_BAYS:
			String csvFilePath = args[1];
			nb(csvFilePath);
			break;
		case METHOD_MECAB:
			String str = args[1];
			mecab(str);
			break;
		default:
			throw new BadArgsException("引数に誤りがあります");
		}
	}
	
	private static int checkArgs(String[] args) {

		if (args.length != 2) {
			throw new BadArgsException("引数に誤りがあります");
		}
		
		String methodOption = args[0];
		switch (methodOption) {
		case "_nb":
			return METHOD_NAIVE_BAYS;
		case "_m":
			return METHOD_MECAB;
		}
		return -1;
	}

	private static void nb(String csvFilePath) {

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
	
	private static void mecab(String str) {
		
		MongoDao mongo = new MongoDao();
		mongo.getServerMongo();
		
		Mecab mecab = new Mecab();
		String[] words = mecab.extractWordsFromDoc(str);
		for (String w : words) System.out.println(w);
	}
	
}

@SuppressWarnings("serial")
class BadArgsException extends RuntimeException {
	public BadArgsException(String message) {
		super(message);
	}
}
