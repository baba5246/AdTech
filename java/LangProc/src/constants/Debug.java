package constants;

public class Debug {
	
	// コンストラクタは使わせない
	private Debug() {}
	
	// デバッグモード切り替え
	public static final boolean DEBUG_MODE = true;
	
	// デバッグメッセージ出力
	private static final String DEBUG_PREFIX = "DEBUG: ";
	public static void console(Object object) {
		if (DEBUG_MODE == true) System.out.println(DEBUG_PREFIX + object.toString());
	}
	
	// 実行時引数に指定されるべき文字列
	public static final String NB_METHOD = "_nb";
	public static final String NB_APP_STORE_METHOD = "as";
	public static final String NB_GOOGLE_PLAY_METHOD = "gp";
	public static final String NB_CSV_METHOD = "csv";

}
