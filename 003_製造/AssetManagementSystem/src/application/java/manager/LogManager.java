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
     * 情報ログを出力する
     */
    public static void writeInfo(String message) {
        logger.info(message);
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
    public static void showAndWriteError(Throwable t) {
        logger.error(t.getMessage(), t);
        MessageBox.ShowErrorMessage(
        		"例外エラー",
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
}
