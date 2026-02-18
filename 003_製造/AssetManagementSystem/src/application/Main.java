package application;

import application.java.manager.MySqlManager;
import application.java.manager.form.JavaFxManager;
import application.java.window.inventoryList.FormController;
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
		JavaFxManager.launch(args);
	}

	@SuppressWarnings("exports")
	@Override
	/**
	 * JavaFx[start]メソッド制御 
	 */
	public void start(Stage primaryStage) throws Exception{
		// JavaFxの各メソッドを呼び出すため、継承したMainを保持する
		JavaFxManager.application = this;	      
		
		// Window枠の保持
		super.start(primaryStage);
		
		// 最初に表示する画面を設定
		this.setPage(new FormController());
	}
	
    @Override
    /**
     * JavaFx[stop]メソッド制御 
     */
    public void stop()  throws Exception {
        MySqlManager.Close();
        super.stop();
    }
}
