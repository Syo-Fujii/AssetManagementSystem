package application.Manager.Form;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/** 画面生成マネージャ(JavaFx ※OpenJFx) */
public class JavaFxManager extends Application{
	
	
	private Stage stage;
	
	private Object windowBasePanel;
	private String cssName;
	
	
	
	
	/**
	 * CSSを取得する。
	 * @return CSS
	 */
	public String getCssName() {
		return cssName;
	}

	/**
	 * CSSを設定する。
	 * @param cssName 設定するCSS
	 */
	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	
	public JavaFxManager() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	@Override
	/**
	* 指定した商品IDで商品マスタを検索し、該当する商品データを返す
	* @param　productId 商品ID
	* @return　商品IDに該当する商品データ。該当する商品が存在しない場合は null
	*/
	public void start(Stage primaryStage) throws Exception{
		try {

			/* 画面ファイル(FXML)の呼び出し
			 * ⇒　生成した画面のパネルで受けること
			 * クラス名と同名のFXMLファイルを呼び出し
			 */
			Pane root = (Pane)FXMLLoader.load(getClass().getResource(getClass().getSimpleName() + ".fxml"));
			
			// シーン生成(引数の画面サイズを設定した場合、優先される)
			// Scene scene = new Scene(root,400,400);
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource(cssName).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	   /* BasePage を継承した Controller は遷移先画面の class と fxml名、任意に渡すパラメータで遷移を実行する */
	   public void setPage(Class<?> cls, String fxml, Object...params) throws Exception{
	      FXMLLoader loader = new FXMLLoader();
	      Scene scene = new Scene((Parent)loader.load(cls.getResourceAsStream(fxml)), WINDOW_WIDTH, WINDOW_HEIGHT);

	      BasePage page = (BasePage)loader.getController();
	      page.setApp(this);
	      page.loadParameter(params);
	      
	      stage.setTitle(WINDOW_TITLE);
	      stage.setScene(scene);
	      stage.show();
	   }	
	
	
	
}
