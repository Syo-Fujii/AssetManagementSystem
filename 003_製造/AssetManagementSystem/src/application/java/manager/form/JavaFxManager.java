package application.java.manager.form;

import java.util.Objects;

import application.java.base.BaseFormPage;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/** 画面生成マネージャ(JavaFx ※OpenJFx) 
 * @brief 継承したアプリケーション(JavaFx)クラス
 * <p>
 * すべてのJavaFXアプリケーションは、Applicationクラス(javafx.application.Application) のサブクラスとして実装します。<br>
 * アプリケーションのソースコードは以下のような構造になります。<br>
 * アプリケーションの中身は抽象メソッドのstartをオーバライドすることにより記述します。<br>
 * mainメソッドからApplication.launchメソッドを呼び出すと、アプリケーションのインスタンスが生成され、<br>
 * 続いてinitメソッドでメインのウインドウ（プライマリステージ）が生成され、さらにそのウインドウを引数として
 * startメソッドに制御が渡されます。<br>
 * つまり、以下の流れでアプリケーションが起動されることになります。<br>
 * [main] ⇒ [launch] ⇒ [init] ⇒ [start]<br>
 * このうち、利用者が実装するメソッドは、mainとstartです。<br>
 * 通常、mainメソッドはlaunchメソッドを呼び出すだけで、他にすることは特にありません。<br>
 * なお、launchメソッドはアプリケーションのすべての処理が完了するまで待機します。
 */
public class JavaFxManager extends Application{

	/** JavaFx操作アプリ(Main) */
	public static JavaFxManager application;
	
	/** アプリケーションWindow(起動時に1つ自動生成される window枠 基底コンテナ) */
	public static Stage stage;

	/** 中身(scene)のサイズに合わせてウィンドウ枠を変更するか */
	public static Boolean isSizeToScene = true;

	
	/**
	 * JavaFx[start]メソッド制御
	 * @param primaryStage アプリケーションWindow
	 */
	@Override
	public void start(Stage primaryStage) throws Exception{
	      stage = primaryStage;
	}
	
	/**　
	* 画面設定・表示処理
	* @param cls BaseFormPage 遷移先画面のclass
	* @param params Object 遷移先画面用パラメータ
	* @brief 表示枠(Stage)にコンテンツ内容(Scene)を設定・表示する。<br>
	* ExceptionはApplication.startに投げられる。<br>
	* BasePage を継承した Controller は遷移先画面の class と fxml名、任意に渡すパラメータで遷移を実行する。<br>
	* インスタンス化したController を引数で用いる場合はfxmlファイルの
	* [fx:controller="application.java.window.inventoryList.FormController"]を記述せず<br>
	* メソッド内で[loader.setController(cls);]を設定する。
	*/
	@SuppressWarnings("unused")
	public void setPage(BaseFormPage cls, Object...params) throws Exception{
    	try {
    		LogManager.writeInfo("画面展開 [setPage] ： 画面設定・表示処理 開始"); 
    		
    		// ウィンドウ枠を切替る場合、[stage]を再生成する
    		setWindowFrame(cls.getIsUseWindowFrame());
    		
    		FXMLLoader loader = new FXMLLoader();
    		
    		loader.setController(cls);
    		
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
    		
    		// CSSファイルの読込
    		if (!cls.getCssFile().isEmpty()) {
    			scene.getStylesheets().add(getClass().getResource(cls.getCssFile()).toExternalForm());
    		}
    		
    		System.out.print(cls.getCssFile() + AppUtil.newLine());
    		
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
    		
    		// 画面内容(パラメータ設定)の展開
    		cls.loadParameter(params);
    		
    		// Event 付与(Form Shown相当)
    		// [Window]の描画が完全に完了した時点で呼出されるイベント
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            	this.WindowShown(cls);
            });		
    		
            // ウィンドウ背景色の設定(設定した場合のみ)
            if (cls.getWindowColor() != null) {
        		scene.setFill(cls.getWindowColor());
            }
            
            stage.setScene(scene);
    		
    		if (isSizeToScene)
    		{
    			// 中身(scene)のサイズに合わせてウィンドウ枠をフィットさせる
    			stage.sizeToScene();
    		}
    		
    		LogManager.writeInfo("画面展開 [setPage] : " + cls.getPageTitle());
    		stage.show();
    	
    	} catch ( Exception e) {
    		String title = "画面展開 [setPage] : 画面展開中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    		throw e;
    	}
	}
	
	/**
	 * (stage)画面[Window]表示Event 
     * @brief .NET Shown相当 
     * Shownイベントは 、フォームが初めて表示されたときにのみ発生します。
     * その後、最小化、最大化、復元、非表示、表示、無効化、再描画は、このイベントを発生させません。
     * Window(Stage)の呼び出しは初回(primaryStage start)のみのため、１回しか呼び出されない
	 */
	private void WindowShown(BaseFormPage cls){
		
		// Windowタイトルの定義
		stage.setTitle(cls.getWindowTitle());

	    // ウィンドウサイズを固定（最大化ボタンも無効になります）
	    stage.setResizable(false);	

		// Page(scene) で設定したWindowShownEvent呼出
		cls.windowShown();
	}
	
	/**
	 * ウィンドウ(枠)設定
	 * @param isUseFlame
	 * @brief ウィンドウ(枠)設定は生成時に一回しかできない為、<br>
	 * 切り替える場合はウィンドウ(stage)を生成しなおす。
	 */
	private void setWindowFrame(boolean isUseFlame) {
        
		AppConst.WindowStyle targetStyle = 
				!isUseFlame ? AppConst.WindowStyle.TRANSPARENT : AppConst.WindowStyle.DECORATED;	
		
		if ( (stage.getStyle().equals(targetStyle.getStyle()))) { return; }
		
		LogManager.writeDebug("Window切替 [setWindowFrame] : " + targetStyle.getlabel());
		
		// 表示中の場合は閉じる
		if ( stage.isShowing() ) { stage.close(); }
		
        stage = new Stage(); // 新しいStageを生成
        stage.initStyle(targetStyle.getStyle());
	}
}
