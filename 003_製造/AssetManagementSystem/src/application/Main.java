package application;

import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.form.JavaFxManager;
import application.java.window.LoginController;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * エントリポイント
 * @note JavaFxを用いたWindowアプリケーションの為、JavaFxを継承する
 */
public final class Main extends JavaFxManager {
	
	/**
	 * コンストラクタ
	 * @param args 起動引数
	 */
	public static void main(String[] args) {
        LogManager.writeInfo("備品管理システム : 起動"); 
        
		JavaFxManager.launch(args);
	}

	/**
	 * JavaFx [start] メソッド(継承)
	 */
	@SuppressWarnings("exports")
	@Override
	public void start(Stage primaryStage) throws Exception{
	    try {
	    	onExceptionUiThreadHandler();
	    	
	    	// JavaFxの各メソッドを呼び出すため、継承したMainを保持する
	    	JavaFxManager.application = this;
	        
	    	LogManager.writeInfo("開始処理 : 画面ウィンドウ枠生成");

	    	// Window枠の保持
	    	super.start(primaryStage);
			
	    	// 最初に表示する画面を設定
	    	// this.setPage(new MenuController(true));
	    	this.setPage(new LoginController());

	    	
	    } catch (Exception e) {
	    	// システム(JavaFx)にExceptionを通知する
	        throw e;
	    }
	}
	
    /**
     * JavaFx [stop] メソッド(継承)
     */
    @Override
	public void stop()  throws Exception {
        try {
            // DB切断
            MySqlManager.Close();

        } catch (Exception e) {
            LogManager.writeError("終了処理（DB切断）でエラーが発生しました", e);
            // システム(JavaFx)にExceptionを通知する
            throw e; 
 
        } finally {
            // エラーの有無に関わらず、JavaFX基盤の終了処理は必ず呼ぶ
            super.stop();
            LogManager.writeInfo("終了処理 : 完了");
        }
    }
    
	/**
	 * JavaFXのUIスレッド例外処理
	 * @brief 各イベントなどのJavaFxの UIスレッド（Event Dispatch Thread）]で<br>
	 * 発生するExceptionを処理する。
	 */
	@SuppressWarnings("unused")
	private void onExceptionUiThreadHandler() {
    	Thread.setDefaultUncaughtExceptionHandler(
    			(thread, throwable) -> {
    			    Platform.runLater(() -> {
    			    	LogManager.showAndWriteError(LogManager.getExceptionTitle(throwable), throwable);
    			    	
    			        try {
    			            // DB切断
    			            MySqlManager.Close();
    			        } catch (Exception e) {
    			            LogManager.writeError("終了処理（DB切断）でエラーが発生しました", e);
    			        }
    			    	
    			    	// システムを終了する
    			    	System.exit(1);
    			    });
    	});
	}	    
}
