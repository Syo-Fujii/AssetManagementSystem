package application.java.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    /**
     * 文字列を日付(LocalDate型)に変換可能か判定
     * @param str 対象文字列
     * @return 変換可能判定
     */
    public static boolean isDate(String str) {
        if (str == null) return false;
        try {
        	LocalDate.parse(
        			str.toString(),
            		DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        	return true;
        } catch (Exception e) {
            return false;
        }
    }        
    
    /**
     * ファイル名からFXMLファイルPATHを生成する
     * @param filename 
     * @return ファイルパス
     */
    public static String MakeFxmlFilePath(String filename) {
    	return AppConst.FXML_PATH +  filename + ".fxml";
    }

    /**
     * ファイル名からCSSファイルPATHを生成する
     * @param filename 
     * @return ファイルパス
     */
    public static String MakeCssFilePath(String filename) {
    	return AppConst.CSS_PATH +  filename + ".css";
    }
    
    /**
     * 改行コード
     * @return 改行コード
     */
    public static String newLine() {
    	return System.lineSeparator();
    }
 
    /**
     * 文字列 NULL・空文字判定
     * @param value 対象の値(String型)
     * @return 判定結果
     */
    public static boolean StringIsNullOrEmpty(String value) {
        if (value == null || value.isEmpty()) return true;
            return false;
    }       
    
    /**
     * 文字列 NULL・空文字・BLANK判定  
    
    /**
     * 数値(Integer) NULL判定
     * @param value 対象の値(Integer型)
     * @return 判定結果
     */
    public static boolean IsNull(Integer value) {
        if (value == null) return true;
            return false;
    }
}
