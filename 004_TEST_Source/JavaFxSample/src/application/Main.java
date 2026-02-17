package application;
	
import java.io.InputStream;
import java.util.List;

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
	          // 【重要】MyBatisに、現在のプロジェクトのクラスローダーを使うよう強制する
	        // Resources.setDefaultClassLoader(this.getClass().getClassLoader());
			
			// String resource = "mybatis-config.xml";
	        /*
			// ↓ 自分のクラスのクラスローダーを使って確実に取得する
	        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
			//InputStream inputStream = Resources.getResourceAsStream(resource);
			
	        if (inputStream == null) {
	            throw new RuntimeException("設定ファイルが見つかりません: " + resource);
	        }*/
	        
	        /*
	        // 1.5 厳密な指定 -------------------------------------------------------------------------
	           // 自分のクラス（Mainなど）のクラスローダーを使って読み込む
	        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);

	        if (is == null) {
	            throw new RuntimeException("XMLが見つかりません。パスを確認してください。");
	        }
	        
	        // XMLConfigBuilderを直接使い、検証(Validation)をfalseにする
	        
	        // 1. 検証(validation)を false に設定して XML をパースする
	        // 第2引数: validation, 第3引数: variables, 第4引数: EntityResolver
	        //XPathParser xpathParser = new XPathParser(is, false, null, null);
	        	        	        
	        // 第1引数: InputStream, 第2引数: Environment(nullでOK), 第3引数: Properties(nullでOK)
	        XMLConfigBuilder parser = new XMLConfigBuilder(is, null, null);
	        
	        // ここがポイント：MyBatis内部の検証をスキップした状態でFactoryを作る
	        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(parser.parse());
	        // SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);        
	        
	        Configuration config = factory.getConfiguration();
	        // 手動でXMLを読み込ませる（クラスパスのルートからのパス）
	        try (InputStream mapperIs = this.getClass().getClassLoader().
	        		getResourceAsStream("application/StockTypeMasterMapper.xml")) {
	            XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(
	            		mapperIs, 
	            		config, 
	            		"application/StockTypeMasterMapper.xml", 
	            		config.getSqlFragments());
	            mapperBuilder.parse();
	        }
	        
	        //SqlSessionFactory sqlSessionFactory =new SqlSessionFactoryBuilder().build(inputStream);
	        //SqlSession ss = sqlSessionFactory.openSession();*/
	        
	        
	        // 2 -----------------------------------------------------------------------------	        
			/*try (SqlSession session = MySqlManager.getSqlSessionFactory().openSession()) {
				IMySqlMapper mapper = session.getMapper(IMySqlMapper.class);
			    
			    // 全件取得の実行
			    List<StockTypeMaster> userList = mapper.selectAll();
			    // userList.removeIf(Objects::isNull);
			    
			    int count = userList.size();
			    
			    List<StockTypeMaster> B = userList;
			    // JavaFXのListViewなどに反映（UIスレッドで実行）
			}*/

			// 3 HikariCP(コネクションPOOLの使用)----------------------------------------------
			/*
			 // 1. HikariCPの設定
			HikariConfig hConfig = new HikariConfig();
			hConfig.setJdbcUrl("jdbc:mysql://localhost:3306/test");
			hConfig.setUsername("app2");
			hConfig.setPassword("app_user_1234");
			hConfig.setMaximumPoolSize(20); // 並列数に合わせて調整
			hConfig.setConnectionTimeout(30000);
			HikariDataSource dataSource = new HikariDataSource(hConfig);
			
	          // 2. MyBatisのEnvironment構築
	        Environment environment = new Environment(
	            "development", 
	            new JdbcTransactionFactory(), 
	            dataSource
	        );
	        
	          // 3.
	        InputStream is = this.getClass().getClassLoader().getResourceAsStream("mybatis-config_notuse-datasource.xml");
	        if (is == null) {
	            throw new RuntimeException("XMLが見つかりません。パスを確認してください。");
	        }
	        
	        // Configuration conf = new SqlSessionFactoryBuilder().build(is).getConfiguration();
	        	        
	        SqlSessionFactory xmlSqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
	        Configuration config = xmlSqlSessionFactory.getConfiguration();
	        
	        config.setEnvironment(environment);
	      	
	        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
	        

	        try (SqlSession session = sqlSessionFactory.openSession()) {
			IMySqlMapper mapper = session.getMapper(IMySqlMapper.class);
		    
		    // 全件取得の実行
		    List<StockTypeMaster> userList = mapper.selectAll();
		    // userList.removeIf(Objects::isNull);
		    
		    int count = userList.size();
		    
		    List<StockTypeMaster> B = userList;
		    // JavaFXのListViewなどに反映（UIスレッドで実行）
		    }*/
	        
	        // 4 HikariCP + mybatis-config記述----------------------------------------------
			String resource = "mybatis-config_add-hikaricp.xml";
	        
			// ↓ 自分のクラスのクラスローダーを使って確実に取得する
	        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resource);
			//InputStream inputStream = Resources.getResourceAsStream(resource);
			
	        if (inputStream == null) {
	            throw new RuntimeException("設定ファイルが見つかりません: " + resource);
	        }
			
	        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
	        

	        try (SqlSession session = sqlSessionFactory.openSession()) {
			IMySqlMapper mapper = session.getMapper(IMySqlMapper.class);
		    
		    // 全件取得の実行
		    List<StockTypeMaster> userList = mapper.selectAll();
		    // userList.removeIf(Objects::isNull);
		    
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
