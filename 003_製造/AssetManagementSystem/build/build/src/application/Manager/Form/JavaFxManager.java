package application.Manager.Form;

import java.util.Objects;

import application.Class.BaseFormPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** 画面生成マネージャ(JavaFx ※OpenJFx) 
 * 継承したアプリケーション(JavaFx)クラス
 */
public class JavaFxManager extends Application{

	/* アプリケーションWindow(起動時に1つ自動生成される) */
	public static Stage stage;

	private Integer windowWidth = null;
	private Integer windowHeight = null;	
	
	
	@Override
	/**
	* 指定した商品IDで商品マスタを検索し、該当する商品データを返す
	* @param　productId 商品ID
	*/
	public void start(Stage primaryStage) throws Exception{
	      stage = primaryStage;
	}
	
	/**
	* 画面設定・表示処理
	* @param　cls 遷移先画面のclass
	* @param　fxml 遷移先画面のfxml
	* @param　params 遷移先画面用パラメータ
	*/
	/* BasePage を継承した Controller は遷移先画面の class と fxml名、任意に渡すパラメータで遷移を実行する */
	public void setPage(BaseFormPage cls, String fxml, Object...params) throws Exception{
		FXMLLoader loader = new FXMLLoader();

		//loader.setController(cls);
		//Parent root = FXMLLoader.load(getClass().getResource(fxml));
		//Parent root = (Parent)loader.load(application.Window.InventoryList.Form.class.getResourceAsStream(fxml));
		Parent root = (Parent)loader.load(cls.getClass().getResourceAsStream(fxml));
		Object page = loader.getController();
		
		// シーン(ステージに表示する内容)の生成
		// FXMLドキュメントからオブジェクト階層をロード
		Scene scene = null;
		if (!Objects.isNull(cls.getWindowWidth()) && !Objects.isNull(cls.getWindowHeight())) {
			// 画面クラスで横幅・高さを設定した場合は用いる
			scene = new Scene(root, cls.getWindowWidth(), cls.getWindowHeight());
		} else {
			scene = new Scene(root);
		}
		
		// 継承したJavaFxアプリの設定
		cls.setApp(this);
		// 画面内容(パラメータ設定)の展開
		cls.loadParameter(params);

		stage.setTitle(cls.getWindowTitle());
		stage.setScene(scene);
		stage.show();
	}
}
