package application.manager.form;

import java.util.Objects;

import application.java.base.BaseFormPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/** 画面生成マネージャ(JavaFx ※OpenJFx) 
 * @brief 継承したアプリケーション(JavaFx)クラス
 * すべてのJavaFXアプリケーションは、Applicationクラス(javafx.application.Application) のサブクラスとして実装します。
 * アプリケーションのソースコードは以下のような構造になります。
 * アプリケーションの中身は抽象メソッドのstartをオーバライドすることにより記述します。
 * mainメソッドからApplication.launchメソッドを呼び出すと、アプリケーションのインスタンスが生成され、
 * 続いてinitメソッドでメインのウインドウ（プライマリステージ）が生成され、さらにそのウインドウを引数として
 * startメソッドに制御が渡されます。
 * つまり、以下の流れでアプリケーションが起動されることになります。
 * [main] ⇒ [launch] ⇒ [init] ⇒ [start]
 * このうち、利用者が実装するメソッドは、mainとstartです。
 * 通常、mainメソッドはlaunchメソッドを呼び出すだけで、他にすることは特にありません。
 * なお、launchメソッドはアプリケーションのすべての処理が完了するまで待機します。
 */
public class JavaFxManager extends Application{

	/* JavaFx操作アプリ(Main) */
	public static JavaFxManager application;
	
	/* アプリケーションWindow(起動時に1つ自動生成される window枠 基底コンテナ) */
	public static Stage stage;
	
	@Override
	/**
	 * JavaFx[start]メソッド制御
	 * @param primaryStage アプリケーションWindow
	 */
	public void start(Stage primaryStage) throws Exception{
	      stage = primaryStage;
	}
	
	/**
	* 画面設定・表示処理
	* @param　cls 遷移先画面のclass
	* @param　fxml 遷移先画面のfxml
	* @param　params 遷移先画面用パラメータ
	* @brief 表示枠(Stage)にコンテンツ内容(Scene)を設定・表示する
	* BasePage を継承した Controller は遷移先画面の class と fxml名、任意に渡すパラメータで遷移を実行する
	*/
	public void setPage(BaseFormPage cls, Object...params) throws Exception{
		FXMLLoader loader = new FXMLLoader();
		
		Parent root = (Parent)loader.load(cls.getClass().getResourceAsStream(cls.getfxmlFilePath()));
		
		// シーン(ステージに表示する内容)の生成
		// FXMLドキュメントからオブジェクト階層をロード
		Scene scene = null;
		if (!Objects.isNull(cls.getWindowWidth()) && !Objects.isNull(cls.getWindowHeight())) {
			// 画面クラスで横幅・高さを設定した場合は用いる
			scene = new Scene(root, cls.getWindowWidth(), cls.getWindowHeight());
		} else {
			scene = new Scene(root);
		}
		
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		/*
		BaseFormPage page = (BaseFormPage)loader.getController();
		page.setApp(this);		
		page.loadParameter(params);
		*/
		
		// Event 付与(Control.VisibleChanged Form.Activated相当)
		stage.sceneProperty().addListener((observable,oldScene,newScene) -> {
		    if (newScene == null) {
		        // ノード(stage)よりシーンから削除された
		    } else {
		        // ノード(stage)にシーンが追加(変更)された
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        // Window (Stage) が確定した時の処理
                    }       
                });
            }
		});
		
		// Event 付与(Form Shown相当)
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
        	this.FormShown();
        });		
		
		
		// 継承したJavaFxアプリの設定
		//cls.setApp(this);
		// 画面内容(パラメータ設定)の展開
		cls.loadParameter(params);

		stage.setTitle(cls.getWindowTitle());
		stage.setScene(scene);
		stage.show();
		
		System.out.println("setPage");
		
	}
	
	/**
	 * (stage)画面表示Event 
     * @brief .NET Shown相当 
     * Shownイベントは 、フォームが初めて表示されたときにのみ発生します。
     * その後、最小化、最大化、復元、非表示、表示、無効化、再描画は、このイベントを発生させません。
     * Window(Stage)の呼び出しは初回(primaryStage start)のみのため、１回しか呼び出されない
	 */
	private void FormShown(){
    	System.out.println("FormShown");
	}	
}
