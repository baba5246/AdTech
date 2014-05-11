

import io.BadFileNameException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import constants.Debug;
import dao.AppDao;
import preprocessing.Mecab;
import runner.NBEvaluation;
import runner.NBTrain;

public class Main {

	private static final int METHOD_NAIVE_BAYS = 1;
	private static final int METHOD_LDA = 2;
	private static final int METHOD_MECAB = 3;
	
	private static final String ARGS_ERROR_MESSAGE = "引数に誤りがあります";
	
	public static void main(String[] args) {
		
		int method = checkMethodArgs(args);
		Debug.console("実行時の引数は正しい形式です。");
		
		switch (method) {
		case METHOD_NAIVE_BAYS: // ナイーブベイズ
			Debug.console("ナイーブベイズが選択されました。");
			nb(args[1]);
			break;
		case METHOD_LDA: 		// LDA
			Debug.console("LDAトピックモデルが選択されました。（準備中）");
			break;
		case METHOD_MECAB: 		// Mecab動作テスト
			Debug.console("Mecab動作テストが選択されました。");
			mecab(args[1]);
			break;
		}
	}
	
	private static int checkMethodArgs(String[] args) {

		if (args.length < 1) {
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
		
		String methodOption = args[0];
		switch (methodOption) {
		case Debug.NB_METHOD:
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return METHOD_NAIVE_BAYS;
		case "_mcb":
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return METHOD_MECAB;
		default:
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
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
			case Debug.NB_APP_STORE_METHOD:
				Debug.console("App Store Mode が選択されました。");
				learner.train(AppDao.APP_STORE_TYPE, serFilePath);
				break;
			case Debug.NB_GOOGLE_PLAY_METHOD:
				Debug.console("Google Play Mode が選択されました。");
				learner.train(AppDao.GOOGLE_PLAY_TYPE, serFilePath);
				break;
			default:
				if (arg.contains(Debug.NB_CSV_METHOD)) {
					Debug.console("CSV Mode が選択されました。");
					learner.train(arg, serFilePath);
				}
			}
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		// 評価
		NBEvaluation eval = new NBEvaluation();
		try {
			switch (arg) {
			case Debug.NB_APP_STORE_METHOD:
				eval.test(AppDao.APP_STORE_TYPE, serFilePath);
				break;
			case Debug.NB_GOOGLE_PLAY_METHOD:
				eval.test(AppDao.GOOGLE_PLAY_TYPE, serFilePath);
				break;
			default:
				eval.test(arg, serFilePath);
			}
		} catch (BadFileNameException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * MeCabの動作テスト
	 * @param str 形態素解析したい文字列
	 */
	private static void mecab(String str) {

		Debug.console("解析する文字列は " + str + "です。");
		
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
