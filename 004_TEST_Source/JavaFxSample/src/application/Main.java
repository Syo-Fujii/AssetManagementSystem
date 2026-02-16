package application;
	
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import application.entity.StockTypeMaster;
import application.mapper.IMySqlMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		
		
		/** MySQL アクセス検証 */
		try {
			// 1 ----------------------------------------------------------------------------------
	          // 3. 【重要】MyBatisに、現在のプロジェクトのクラスローダーを使うよう強制する
	        Resources.setDefaultClassLoader(Main.class.getClassLoader());
			
			String resource = "mybatis-config.xml";
	        // ↓ 自分のクラスのクラスローダーを使って確実に取得する
	        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(resource);
			//InputStream inputStream = Resources.getResourceAsStream(resource);
			
	        if (inputStream == null) {
	            throw new RuntimeException("設定ファイルが見つかりません: " + resource);
	        }
	        
	        SqlSessionFactory sqlSessionFactory =new SqlSessionFactoryBuilder().build(inputStream);
	        SqlSession ss = sqlSessionFactory.openSession();
	        
	        // 2 -----------------------------------------------------------------------------	        
			try (SqlSession session = MySqlManager.getSqlSessionFactory().openSession()) {
				IMySqlMapper mapper = session.getMapper(IMySqlMapper.class);
			    
			    // 全件取得の実行
			    List<StockTypeMaster> userList = mapper.selectAll();
			    userList.removeIf(Objects::isNull);
			    
			    int count = userList.size();
			    
			    List<StockTypeMaster> B = userList;
			    // JavaFXのListViewなどに反映（UIスレッドで実行）
			}

			
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
