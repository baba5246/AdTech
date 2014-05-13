package constants;

public class Debug {
	
	// コンストラクタは使わせない
	private Debug() {}
	
	// デバッグモード切り替え
	public static final boolean DEBUG_MODE = true;
	
	// デバッグメッセージ出力
	private static final String DEBUG_PREFIX = "DEBUG: ";
	public static void console(Object object) {
		if (DEBUG_MODE) System.out.println(DEBUG_PREFIX + object.toString());
	}
	
	// 実行時引数に指定されるべき文字列
	public static final String NB_METHOD = "_nb";
	public static final String LDA_METHOD = "_lda";
	public static final String APP_STORE_OPTION = "as";
	public static final String GOOGLE_PLAY_OPTION = "gp";
	public static final String CSV_OPTION = "csv";
	
	// メソッドの指定
	public static final int METHOD_NAIVE_BAYS = 1;
	public static final int METHOD_LDA = 2;
	public static final int METHOD_MECAB = 3;

}
