package application;

import application.Manager.Form.JavaFxManager;
import application.Window.InventoryList.FormController;
import javafx.stage.Stage;


/* エントリポイント */
public final class Main extends JavaFxManager {
	
	public static void main(String[] args) {
		// Application.launch(JavaFxManager.class, args);
		JavaFxManager.launch(args);
	}

	@Override
	/**
	* 指定した商品IDで商品マスタを検索し、該当する商品データを返す
	* @param　productId 商品ID
	*/
	public void start(Stage primaryStage) throws Exception{
	      super.start(primaryStage);
	      
	      // 最初に表示する画面を設定
	      this.setPage(new FormController(), "/application/Window/InventoryList/InventoryList.fxml");
	}

}
