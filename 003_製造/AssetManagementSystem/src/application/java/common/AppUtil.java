package application.java.common;

/**
 * 共通関数クラス
 */
public class AppUtil {
	
    // privateコンストラクタでインスタンス化を禁止
    private AppUtil() {}

    /**
     * 文字列を数値(int型)に変換する（失敗時はデフォルト値を返す）
     * @param val 対象文字列
     * @param defaultValue 変換出来ない場合の値(int)
     * @return 数値(int型)
     */
    public static int parseInt(String val, int defaultValue) {
        try {
        	
        	if (val == null || val.isEmpty()) return defaultValue;
            
        	return Integer.parseInt(val);
        
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 文字列を数値(int型)に変換可能か判定
     * @param str 対象文字列
     * @return 変換可能判定
     */
    public static boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }    
}
