

import io.BadFileNameException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import dao.AppDao;
import entity.App;
import preprocessing.Mecab;
import runner.NBEvaluation;
import runner.NBTrain;

public class Main {

	private static final String MONGO_APP_STORE = "as";
	private static final String MONGO_GOOGLE_PLAY = "gp";

	private static final int METHOD_NAIVE_BAYS = 1;
	private static final int METHOD_MECAB = 2;
	private static final int METHOD_MONGO = 3;
	
	private static final String ARGS_ERROR_MESSAGE = "引数に誤りがあります";
	
	public static void main(String[] args) {
		
		int method = checkArgs(args);
		switch (method) {
		case METHOD_NAIVE_BAYS: // ナイーブベイズ
			nb(args[1]);
			break;
		case METHOD_MECAB: // Mecab動作テスト
			String str = args[1];
			mecab(str);
			break;
		default:
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
	}
	
	private static int checkArgs(String[] args) {

		if (args.length < 1) {
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
		
		String methodOption = args[0];
		switch (methodOption) {
		case "_nb":
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return METHOD_NAIVE_BAYS;
		case "_mcb":
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return METHOD_MECAB;
		case "_mng":
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return METHOD_MONGO;
		}
		return -1;
	}

	private static void nb(String arg) {

		// 保存するファイル名を作成
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String serFilePath = "nb" + "_" + sdf.format(cal.getTime());
		
        // 学習
		NBTrain learner = new NBTrain();
		try {
			switch (arg) {
			case MONGO_APP_STORE:
				learner.train(1, serFilePath);
				break;
			case MONGO_GOOGLE_PLAY:
				learner.train(2, serFilePath);
				break;
			default:
				learner.train(arg, serFilePath);
			}
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		// 評価
		NBEvaluation eval = new NBEvaluation();
		try {
			switch (arg) {
			case MONGO_APP_STORE:
				eval.test(arg, serFilePath);
				break;
			case MONGO_GOOGLE_PLAY:
				eval.test(arg, serFilePath);
				break;
			default:
				eval.test(arg, serFilePath);
			}
		} catch (BadFileNameException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	private static void mecab(String str) {
		
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
