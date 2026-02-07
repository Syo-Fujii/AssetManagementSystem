package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			/* 画面ファイル(FXML)の呼び出し
			 * ⇒　生成した画面のパネルで受けること
			 * クラス名と同名のFXMLファイルを呼び出し
			 */
			//Pane root = (Pane)FXMLLoader.load(getClass().getResource(getClass().getSimpleName() + ".fxml"));
			
			
			FXMLLoader loader = new FXMLLoader();

			//loader.setController(cls);
			
			 //Parent root = FXMLLoader.load(getClass().getResource(getClass().getSimpleName() + ".fxml"));
			 Parent root = (Parent)loader.load(getClass().getResourceAsStream(getClass().getSimpleName() + ".fxml"));
			
			 Object page = loader.getController();
			 
			 
			 
			// シーン生成(引数の画面サイズを設定した場合、優先される)
			// Scene scene = new Scene(root,400,400);
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
