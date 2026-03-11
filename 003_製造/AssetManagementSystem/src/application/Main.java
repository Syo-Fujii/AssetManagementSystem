package application;

import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.form.JavaFxManager;
import application.java.window.inventoryLoans.inventoryList.FormController;
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
	    	// JavaFxの各メソッドを呼び出すため、継承したMainを保持する
	    	JavaFxManager.application = this;
	        
	    	LogManager.writeInfo("開始処理 : 画面ウィンドウ枠生成");

	    	// Window枠の保持
	    	super.start(primaryStage);
			
	    	// 最初に表示する画面を設定
	    	this.setPage(new FormController());

	    } catch (Exception e) {
	        LogManager.showAndWriteError(e);
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
	    	MySqlManager.Close();
	        super.stop();
	        
	        LogManager.writeInfo("終了処理 : 終了");
	    } catch (Exception e) {
	        LogManager.writeError("終了処理でエラーが発生しました", e);
	        // システム(JavaFx)にExceptionを通知する
	        throw e;
	    }
    }
}
