
import io.BadFileNameException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import constants.Debug;
import dao.AppDao;
import preprocessing.Mecab;
import runner.LDARunner;
import runner.NBRunner;


/**
 * 
 */
public class Main {
	
	private static final String ARGS_ERROR_MESSAGE = "引数に誤りがあります";
	
	/**
	 * 全ての始まり
	 * 引数をチェックして、選択されたメソッドを実行する
	 * @param args 実行時引数
	 */
	public static void main(String[] args) {
		
		// 引数チェック
		int method = checkMethodArgs(args);
		Debug.console("実行時の引数は正しい形式です。");
		
		// メソッドを選択
		switch (method) {
		case Debug.METHOD_NAIVE_BAYS: // ナイーブベイズ
			Debug.console("ナイーブベイズが選択されました。");
			nb(args[1]);
			break;
		case Debug.METHOD_LDA: 		// LDA
			Debug.console("LDAトピックモデルが選択されました。");
			lda(args[1], Integer.parseInt(args[2]));
			break;
		case Debug.METHOD_MECAB: 		// Mecab動作テスト
			Debug.console("Mecab動作テストが選択されました。");
			mecab(args[1]);
			break;
		}
	}
	
	/**
	 * 引数をチェックする
	 * 正しければ、選択されたメソッドのid番号を返す
	 * 誤りであれば、例外を投げて終了 TODO: ここUsageを表示するようにしよう
	 * @param args 実行時引数
	 * @return 選択されたメソッドのid番号
	 */
	private static int checkMethodArgs(String[] args) {

		if (args.length < 1) {
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
		
		String methodOption = args[0];
		switch (methodOption) {
		case Debug.NB_METHOD:
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return Debug.METHOD_NAIVE_BAYS;
		case Debug.LDA_METHOD:
			if (args.length != 3 && args[2].matches("^[0-9].[0-9]+$")) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return Debug.METHOD_LDA;
		case "_mcb":
			if (args.length != 2) throw new BadArgsException(ARGS_ERROR_MESSAGE);
			return Debug.METHOD_MECAB;
		default:
			throw new BadArgsException(ARGS_ERROR_MESSAGE);
		}
	}

	
	/******** Methods to Run the Selected Algorithm ************/
	
	/**
	 * ナイーブベイズを実行する
	 * カテゴリと説明文の情報からナイーブベイズ分類器の学習を行い、同じデータで分類器を評価する
	 * @param arg 引数（ as: AppStoreのDBを指定 gp:GooglePlayのDBを指定 csvのパス:csvのファイルを指定 ）
	 */
	private static void nb(String arg) {

		// 保存するファイル名を作成
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String serFilePath = "nb" + "_" + sdf.format(cal.getTime());
		
        // 学習
		NBRunner runner = new NBRunner();
		try {
			switch (arg) {
			case Debug.APP_STORE_OPTION:
				Debug.console("App Store Mode が選択されました。");
				runner.train(AppDao.APP_STORE_TYPE, serFilePath);
				break;
			case Debug.GOOGLE_PLAY_OPTION:
				Debug.console("Google Play Mode が選択されました。");
				runner.train(AppDao.GOOGLE_PLAY_TYPE, serFilePath);
				break;
			default:
				if (arg.contains(Debug.CSV_OPTION)) {
					Debug.console("CSV Mode が選択されました。");
					runner.train(arg, serFilePath);
				}
			}
		} catch (BadFileNameException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		// 評価
		try {
			switch (arg) {
			case Debug.APP_STORE_OPTION:
				runner.test(AppDao.APP_STORE_TYPE, serFilePath);
				break;
			case Debug.GOOGLE_PLAY_OPTION:
				runner.test(AppDao.GOOGLE_PLAY_TYPE, serFilePath);
				break;
			default:
				runner.test(arg, serFilePath);
			}
		} catch (BadFileNameException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * LDAを実行する
	 * 指定されたDBからアプリの説明文を取得し、指定されたトピック数にクラスタリングする
	 * @param arg 
	 */
	private static void lda(String arg, int topicNum) {
		
		LDARunner runner = new LDARunner();
		switch (arg) {
		case Debug.APP_STORE_OPTION:
			runner.runLDA(AppDao.APP_STORE_TYPE, topicNum);
			break;
		case Debug.GOOGLE_PLAY_OPTION:
			runner.runLDA(AppDao.GOOGLE_PLAY_TYPE, topicNum);
			break;
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
