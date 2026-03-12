package application.java.manager;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import application.java.common.MessageBox;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;

public class LogManager {
    private static final String CONFIG_PATH = "src/application/resources/xml/logback.xml";
	
	/** 静的初期化ブロック（Static Initializer Block） */
    static {
        // 指定した格納先より、[logback.xml]ファイルを取得
        File configFile = new File(CONFIG_PATH);

        if (configFile.exists()) {
        	LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        	try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(context);
                
                // 現在の設定（デフォルト）をクリア
                context.reset();
                configurator.doConfigure(configFile);
  
        	} catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("logback.xml が見つかりません: " + configFile.getAbsolutePath());
        }
    }
    
    /** 共通のロガーインスタンス */
    private static final Logger logger = LoggerFactory.getLogger(LogManager.class);    
    

    /**
     * トレースログを出力する
     */
    public static void writeTrace(String message) {
        logger.trace(message);
    }    
    
    /**
     * 情報ログを出力する
     */
    public static void writeInfo(String message) {
        logger.info(message);
    }

    /**
     * 警告ログを出力する
     */
    public static void writeWarnig(String message) {
        logger.warn(message);
    }   
    
    /**
     * エラーログを出力する
     */
    public static void writeError(String message) {
        logger.error(message);
    }
    
    /**
     * エラーログを出力する（例外情報付き）
     */
    public static void writeError(String message, Throwable t) {
        logger.error(message, t);
    }

    /**
     * エラーログを出力し、例外MSGを表示する（例外情報付き）
     */
    public static void showAndWriteError(String title, Throwable t) {
    	logger.error(title);
    	logger.error(t.getMessage(), t);
        MessageBox.ShowErrorMessage(
        		title,
        		"システムでエラーが発生しました。詳細はLOGを確認してください。",
        		t);
    }    
    
    /**
     * デバッグログを出力する
     */
    public static void writeDebug(String message) {
        logger.debug(message);
    }
    
    /**
     * 呼び出し元のクラスに応じたロガーを取得したい場合
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Exceptionよりタイトル(例外発生クラス・メソッド・行番号)を生成する
     * @param t Throwable Exception(Throwable)
     * @return String 生成したタイトル
     */
    public static String getExceptionTitle(Throwable t) {
        
    	if (t == null || t.getStackTrace() == null || t.getStackTrace().length == 0) {
            return "例外発生: [不明な場所]";
        }
    	
    	StackTraceElement element = t.getStackTrace()[0];

    	String className  = element.getClassName();  // クラス名（パッケージ含む）
        int lastDot = className.lastIndexOf('.');
        String simpleClassName = (lastDot != -1) ? className.substring(lastDot + 1) : className;
    	
        String methodName = element.getMethodName(); // メソッド名
    	int lineNumber    = element.getLineNumber(); // 行番号

    	return String.format("例外発生: [%s].[%s](行番号: %d)", 
    			simpleClassName, 
    			methodName, 
    			lineNumber);
    }
 
}
