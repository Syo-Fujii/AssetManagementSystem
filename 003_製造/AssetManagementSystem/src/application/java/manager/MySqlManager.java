package application.java.manager;

import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javafx.concurrent.Task;   

/**
 *  
 * 
 * final を使わずにシングルトン（唯一のインスタンス）を構成する場合、「遅延初期化（Lazy Initialization）」 
 * 並列処理はJavaFx.Task処理にて行う。 
 * MyBatisと」JavaFxが共存している場合、Configファイルで格納先PATHをしていしても認識しない。
 * そのため、インターフェースクラスを指定することで対応する。
 * 上記のため、Mapper.xmlとインターフェースは同階層・同名でそんざいさせること
 */
public class MySqlManager {
    // private static final SqlSessionFactory sqlSessionFactory = null;
	private static SqlSessionFactory sqlSessionFactory = null;

    // デフォルトは[src]直下とする
    private static String mybatisConfigXmlPath  = "mybatis-config.xml";
    private static boolean isConnectionPooling = true;
    private static boolean isParallel = false;
    

    private static HikariConfig hConfig = null;
    
    /**
     * 
     * @return
     */
    public boolean isConnectionPooling() {
		return isConnectionPooling;
	}

	/**
     * 
     * @param isConnectionPooling
     */
	public void setConnectionPooling(boolean isConnectionPooling) {
		this.isConnectionPooling = isConnectionPooling;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isParallel() {
		return MySqlManager.isParallel;
	}

	/**
	 * 
	 * @param isParallel
	 */
	public void setParallel(boolean isParallel) {
		MySqlManager.isParallel = isParallel;
	}


	/**
	 * MyBatis　設定ファイルパス　取得
	 * @return mybatisConfigXmlPath
	 * @brief MyBatisの設定ファイル(mybatis-config.xml)の格納場所
	 */
    public static String getMybatisConfigXmlPath() {
		return MySqlManager.mybatisConfigXmlPath;
	}

	/**
	 * MyBatis　設定ファイルパス　設定
	 * @param path 設定ファイル(mybatis-config.xml)の格納場所を指定
	 * @brief MyBatisの設定ファイル(mybatis-config.xml)の格納場所
     */
	public static void setMybatisConfigXmlPath(String path) {
		MySqlManager.mybatisConfigXmlPath = path;
	}
	
	/**
	 * HikariCP 設定(HikariConfigクラスを用いた定義)
	 * @return hConfig HikariConfigクラス
	 * @brief HikariConfigを設定した場合、当該を用いて HikariCPの定義を行う。<br>
	 * mybatisconfig.xml の修正 <br>
	 *   <<!-- DataSourceは空のまま、またはUNPOOLEDにしておく（後で上書きする） -->> <br>
     *       <<dataSource type="UNPOOLED">> <br>
     *   <<!-- ここに直接書かず、Javaコード側でHikariCPをセットします -->> <br>
     *        <</dataSource>> <br>
     */
	public static HikariConfig gethConfig() {
		return MySqlManager.hConfig;
	}

	/**
	 * HikariCP 設定(HikariConfigクラスを用いた定義)
	 * @param hConfig HikariConfigクラスを指定
	 * @brief HikariConfigを設定した場合、当該を用いて HikariCPの定義を行う。<br>
	 * mybatisconfig.xml の修正 <br>
	 *   //!-- DataSourceは空のまま、またはUNPOOLEDにしておく（後で上書きする） <br>
     *       //dataSource type="UNPOOLED" <br>
     *   //!-- ここに直接書かず、Javaコード側でHikariCPをセットします  <br>
     *        ///dataSource <br>
     */	
	public static void sethConfig(HikariConfig hConfig) {
		MySqlManager.hConfig = hConfig;
	}

	
	/**
	 * 「静的初期化ブロック（Static Initializer）
	 *  final化しない為、未実装
	 */
	static {
	}
	
	/**
	 * プライベートコンストラクタ（newを禁止）
	 */
	private MySqlManager() {
		// throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * SQL Sessionの取得
	 * @return SqlSessionFactory 保持しているSqlSession
	 * @brief 保持しているSqlSession。ない(null)の場合はSessionを生成する。
	 */
	public static synchronized SqlSessionFactory getSqlSessionFactory() {
        
    	if (sqlSessionFactory == null) {
    		(new MySqlManager()).SqlSessionSettings();
    	}
    	return sqlSessionFactory;
    }
	
	/**
	 * クエリ発行処理
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義)
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。 
	 */
	public static void ExcuteQuery(Consumer<SqlSession> callback) {
		
		if(sqlSessionFactory == null)
		{
			return;
		}		
		
		try (SqlSession session = sqlSessionFactory.openSession()) {
        	callback.accept(session);
	    }		
	}   
    
	/**
	 * クエリ発行処理(並列実行)
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義)
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。 
	 */
	public static void ExcuteQueryOnParallel(Consumer<SqlSession> callback) {

		if(sqlSessionFactory == null)
		{
			return;
		}			
		
		Task<List<User>> task = new Task<>() {
		    @Override
		    protected List<User> call() throws Exception {
		    	MySqlManager.ExcuteQuery(callback);
		    }
		};

		// --- 2. UIスレッドで実行されるイベント ---
		task.setOnSucceeded(e -> {
		    // 成功時：結果をテーブルに表示
		    tableView.getItems().setAll(task.getValue());
		});

		task.setOnFailed(e -> {
		    // 失敗時：エラーダイアログを表示
		    task.getException().printStackTrace();
		});

		// --- 3. 実行 ---
		//Thread thread = new Thread(task);
		//thread.setDaemon(true); // アプリ終了時にこのスレッドも閉じる
		//thread.start();	
	}  	
	
    /**
     * 終了処理(HikariCP)
	 * @brief HikariCPを用いたConnectionを確立していた場合、Closeする。
     */
    public static void HikariCpClose() {
        
    	if (!isConnectionPooling || sqlSessionFactory == null)
    	{
    		return;
    	}
    	
    	// 1. FactoryからConfigurationを取得
        // 2. ConfigurationからEnvironment内のDataSourceを取得
        var dataSource = sqlSessionFactory.
        		getConfiguration().getEnvironment().getDataSource();

        if (dataSource == null) {
            return;
        }      
        
        // 3. HikariDataSourceにキャストしてClose
        if (dataSource instanceof HikariDataSource hikari) {
        	System.out.println("HikariCP DataSource Closeing");
  
        	hikari.close();
        }   
    }

    /**
     * MySQL コネクションの確立(Sessionの生成)
     */
    private void SqlSessionSettings() {
        try {
        	if (MySqlManager.hConfig != null) {
        		// MyBatis 設定ファイル(mybatis-config.xml)+ 
        		// HikariCP[hConfig]クラスを用いたSessionの生成	
        		this.BuildSqlSessionUseHikariCP_DataSource();
        		return;
        	}
        	
        	// MyBatis 設定ファイル(mybatis-config.xml)を用いたSessionの生成	
        	this.BuildSqlSession();	

        } catch (Exception e) {
           	System.out.println(e.getMessage());
        	throw new RuntimeException("SQL Session生成失敗", e);
        }
    }
    
	/**
	 * MyBatisを用いた、SqlSessionの生成
	 * @brief HikariCPの使用は問わない。<br>
	 * HikariCPを使用する場合は、Custom DataSourceFactory(ラッパークラス)を用いて設定する。<br>
	 * // HikariCP用のファクトリを作成
	 * public class HikariDataSourceFactory extends UnpooledDataSourceFactory {
	 * mybatisconfig.xml の修正 <br>
	 *   //!-- 自作した Factory クラスをフルパッケージ名で指定  <br>
     *       //dataSource type="com.example.HikariDataSourceFactory" <br>
	 */
	private void BuildSqlSession() {
        // HikariCP + mybatis-config記述
		if(sqlSessionFactory != null)
		{
			return;
		}
		
		// ↓ 自分のクラスのクラスローダーを使って確実に取得する
        InputStream inputStream = this.getClass().
        		getClassLoader()
        		.getResourceAsStream(mybatisConfigXmlPath);
		
        if (inputStream == null) {
            throw new RuntimeException("設定ファイルが見つかりません: " + mybatisConfigXmlPath);
        }
		
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);		
	}

	/**
	 * [hConfig]クラス/MyBatisを用いた、SqlSessionの生成
	 * @brief HikariCP + mybatis-config記述。<br>
	 * HikariConfigを設定した場合、外部で定義した[hConfig]クラスを用いて、HikariCPの定義を行う。<br>
	 * mybatisconfig.xml の修正 <br>
	 *   //!-- DataSourceは空のまま、またはUNPOOLEDにしておく（後で上書きする） <br>
     *       //dataSource type="UNPOOLED" <br>
     *   //!-- ここに直接書かず、Javaコード側でHikariCPをセットします  <br>
     *        ///dataSource <br>
	 */
	private void BuildSqlSessionUseHikariCP_DataSource() {
        // HikariCP + mybatis-config記述
		if(sqlSessionFactory != null || !isConnectionPooling)
		{
			return;
		}
	
		// 1. HikariCPの設定(外部にて定義)
        
		// 2. MyBatisのEnvironment構築
		Environment environment = new Environment(
           "development", 
           new JdbcTransactionFactory(), 
           new HikariDataSource(MySqlManager.hConfig)
           );
       
		// 3. MyBatis 設定ファイルの読込
		InputStream is = this.getClass().
    		   getClassLoader().getResourceAsStream(mybatisConfigXmlPath);
		if (is == null) {
			throw new RuntimeException("XMLが見つかりません。パスを確認してください。");
			}
		// 4. SqlSessionの生成
		SqlSessionFactory xmlSqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
		Configuration config = xmlSqlSessionFactory.getConfiguration();
		config.setEnvironment(environment);
		
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);	
	}
}
