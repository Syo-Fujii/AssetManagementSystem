package application.java.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import application.java.base.BaseTableViewModel;
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
	// 並列実行用の Thread(ExecutorService)
	private static final ExecutorService executor;
	
	// 実行中のタスク一覧（キャンセル指定用）
	private static final Set<Task<?>> runningTasks;	
	
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
	 *  @brief final(singleton) 並列実行用スレッドの生成を行う<br>
	 *  ThreadPoolは、どのような呼び出しをされたとしても単一にて制御する
	 */
	static {
		System.out.println("MySQL : 非同期処理 スレッドプール作成");
		try {
			executor = Executors.newFixedThreadPool(
	    		    Runtime.getRuntime().availableProcessors(), // CPUコア数分だけ同時に実行可能
	    		    r -> {
	    		        Thread t = new Thread(r);
	    		        t.setDaemon(true); // アプリ終了時にスレッドも強制終了させる設定
	    		        return t;
	    		    });
			
			runningTasks = Collections.synchronizedSet(new HashSet<>());
			
		} catch(Exception e) {
	        // ログ出力などを行い、致命的なエラーとしてスロー
	        System.err.println("MySQL 非同期処理:スレッドプールの初期化に失敗");
	        
	        throw new ExceptionInInitializerError(e);
		}
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
	 * @brief 保持しているSqlSession。ない(null)の場合はSessionを生成する。<br>
	 * DBの切り替えを考慮し、SqlSessionFactoryを[final]ではなく、synchronizedで生成する。
	 */
	public static synchronized SqlSessionFactory getSqlSessionFactory() {
        
    	if (sqlSessionFactory == null) {
    		(new MySqlManager()).SqlSessionSettings();
    	}
    	return sqlSessionFactory;
    }
	
	/**
	 * SQL Sessionの再設定
	 * @param configPath 新たなSqlSession用のMyBatis設定ファイル
	 * @brief SqlSessionを終了し、設定ファイルを元に、新たなSqlSessionを生成する。<br>
	 * DBの切り替えを考慮し、SqlSessionFactoryを[final]ではなく、synchronizedで生成する。
	 */
	public static synchronized void ResetSqlSessionFactory(String configPath) {
 		System.out.println("MySQL : SQL Session再設定");
 
 		if (sqlSessionFactory == null) {
    		// returnせずに、新しいパスの設定と初期化に進む
    		// return;
    	}		
		
		cancelAllTasks();
		
	    try {
	        HikariCpClose();
	    } finally {
	        sqlSessionFactory = null;
	    }
		
		MySqlManager.setMybatisConfigXmlPath(configPath);
		
		new MySqlManager().SqlSessionSettings();

    }
	
	/**
	 * クエリ発行処理
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義)
	 * @return Boolean 処理結果(呼び出し元にて定義)
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。 
	 */
	public static Boolean ExecuteQuery(Function<SqlSession, Boolean> callback) {
		
		if(sqlSessionFactory == null)
		{
			return false;
		}		
		
		System.out.println("MySQL : クエリ発行処理");
		try (SqlSession session = sqlSessionFactory.openSession()) {
        	return callback.apply(session);
	    }		
	}   
    
	/**
	 * クエリ発行処理(並列実行)
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義)
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。 
	 */
	@SuppressWarnings("unused")
	public static void ExecuteQueryOnParallel(
			Function<SqlSession, Boolean> callback, 
			Consumer<Boolean> successCallBack,
			Consumer<Throwable> exceptionCallback) {

		if(sqlSessionFactory == null)
		{
			return;
		}			
		
		System.out.println("MySQL : クエリ発行処理(非同期処理)");
		Task<Boolean> task = new Task<Boolean>() {
		    @Override
		    protected Boolean call() throws Exception {
	            try {
	                runningTasks.add(this);
	                return MySqlManager.ExecuteQuery(callback);
	            } finally {
	                runningTasks.remove(this); // 終了時に必ず削除(this = Task)
	            }
		    }
		};

		// --- 2. UIスレッドで実行されるイベント ---
		task.setOnSucceeded(e -> {
		    // 成功時：
			System.out.println("MySQL : 非同期処終了[成功]");
			successCallBack.accept(task.getValue());
		});

		task.setOnFailed(e -> {
		    // 失敗時(Exceptionが発生した場合)：
			System.out.println("MySQL : 非同期処終了[失敗]");
			exceptionCallback.accept(task.getException());
		});

		// --- 3. 実行 ---
		executor.execute(task); 	
	}  	

	/**
	 * クエリ発行処理(トランザクションの開始)
	 * @param <T> 取得テーブルのMODEL
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義) 戻り値：Boolean
	 * @param dataList 条件とするデータの値(取得テーブルのMODEのリスト)
	 * @return Boolean 処理結果
	 * @throws Exception
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。 
	 */
	public static <T extends BaseTableViewModel> Boolean ExecuteQuery_UseTransaction(
			BiFunction<SqlSession, List<T>, Boolean> callback, List<T> dataList)  throws Exception
	{
		
		if(sqlSessionFactory == null)
		{
			return false;
		}		
		
		System.out.println("MySQL : トランザクション使用 クエリ発行・操作処理");
		// 自動コミットをOFF(openSession(false)) トランザクション処理
		try (SqlSession session = sqlSessionFactory.openSession(false)) {
        	try {
        		
        		if( callback.apply(session, dataList)) {
            		// コミット処理
            		session.commit(); 
            		return true;
            	}
        		// ロールバック処理
        		session.rollback(); 
        		return false;
        	} catch (Exception ex) {
        		session.rollback();
        		
        		throw ex;
        	}
    	}
	}
	
	/**
	 * クエリ発行処理(トランザクションの開始) 
	 * @param <T> 取得テーブルのMODEL
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義) 戻り値：Boolean
	 * @param dataList 条件とするデータの値(取得テーブルのMODEのリスト)
	 * @return Boolean 処理結果
	 * @throws Exception
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。<br>
	 *  トランザクション管理を行い、メソッド(callback)が成功時Commit、失敗時にRollbackを行う。<br>
	 *  Callbackメソッド内で連続してクエリ発行処理を行う場合などのBulk処理を行う。 
	 */
	public static <T extends BaseTableViewModel> Boolean ExecuteBulk_UseTransaction(
			BiFunction<SqlSession, List<T>, Boolean> callback, List<T> dataList)  throws Exception
	{
		
		if(sqlSessionFactory == null)
		{
			return false;
		}		
		
		System.out.println("MySQL : トランザクション使用 Bulk処理");
		// 自動コミットをOFF(openSession(false)) トランザクション処理
		try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
        	try {
        		
        		if( callback.apply(session, dataList)) {
            		
        			// SQLクエリ一括送信
        			session.flushStatements(); 
        			
        			// コミット処理
            		session.commit(); 
            		return true;
            	}
        		// ロールバック処理
        		session.rollback(); 
        		return false;
        	} catch (Exception ex) {
        		session.rollback();
        		throw ex;
        	}
    	}
	}
	
	/**
	 * クエリ発行処理(並列実行・トランザクションの開始) 
	 * @param <T> 取得テーブルのMODEL
	 * @param IsBulk 連続クエリ一括発行(Bulk)処理を行うかどうか
	 * @param callback クエリ発行・取得に関するメソッド(呼び出し元にて定義)
	 * @param dataList 条件とするデータの値(取得テーブルのMODEのリスト)
	 * @param successCallBack クエリ発行成功時のメソッド(呼び出し元にて定義)
	 * @param exceptionCallback 例外発生時のメソッド
	 * @brief クエリ発行・処理をCallBackにて設定する。<br>
	 *  ⇒ 呼び出し元にて、Mapperなどを用いてクエリ発行・処理を定義する。<br>
	 *  トランザクション管理を行い、メソッド(callback)が成功時Commit、失敗時にRollbackを行う。<br>
	 */
	@SuppressWarnings("unused")
	public static <T extends BaseTableViewModel> void ExecuteQueryOnParallel_UseTran(
			Boolean IsBulk,
			BiFunction<SqlSession, List<T>, Boolean> callback, List<T> dataList,
			Consumer<Boolean> successCallBack,
			Consumer<Throwable> exceptionCallback) {

		if(sqlSessionFactory == null)
		{
			return;
		}			
		
		System.out.println("MySQL : クエリ発行処理(非同期・トランザクション処理)");
		Task<Boolean> task = new Task<Boolean>() {
		    @Override
		    protected Boolean call() throws Exception {
	            try {
	                runningTasks.add(this);
	                if (IsBulk) {
	                	return MySqlManager.ExecuteBulk_UseTransaction(callback, dataList);
	                }
	                return MySqlManager.ExecuteQuery_UseTransaction(callback, dataList);
	            } finally {
	                runningTasks.remove(this); // 終了時に必ず削除(this = Task)
	            }
		    }
		};

		// --- 2. UIスレッドで実行されるイベント ---
		task.setOnSucceeded(e -> {
		    // 成功時：
			System.out.println("MySQL : 非同期・トランザクション処終了[成功]");
			successCallBack.accept(task.getValue());
		});

		task.setOnFailed(e -> {
		    // 失敗時(Exceptionが発生した場合)：
			System.out.println("MySQL : 非同期・トランザクション処終了[失敗]");
			exceptionCallback.accept(task.getException());
		});

		// --- 3. 実行 ---
		executor.execute(task); 	
	}  

	/**
	 * 一覧データ取得処理
	 * @param <T> 取得するテーブルのMODEL
	 * @param callback クエリ発行処理メソッド(呼び出し元で定義) 戻り値:テーブルMODELのリスト
	 * @return テーブルMODELのリスト
	 */
	public static <T extends BaseTableViewModel> List<T> Fill(Function <SqlSession, List<T>> callback) {
		
		if(sqlSessionFactory == null)
		{
			return null;
		}		
		
		System.out.println("MySQL : SELECT句発行処理");
		try (SqlSession session = sqlSessionFactory.openSession()) {
        	return callback.apply(session);
	    }		
	}  	
	
	/**
	 * 一覧データ取得・設定処理(並列実行)
	 * @param <T> 取得するテーブルのMODEL
	 * @param callback <br> 
	 *        クエリ発行処理メソッド(呼び出し元で定義) 戻り値:テーブルMODELのリスト
	 * @param successCallBack DB取得時の処理(呼び出し元で定義 引数:テーブルMODELのリスト)
	 * @param exceptionCallback 例外時の処理(呼び出し元で定義 引数: Throwableクラス)
	 */
	@SuppressWarnings("unused")
	public static <T extends BaseTableViewModel> void FillOnParallel( 
			Function<SqlSession, List<T>> callback, 
			Consumer<List<T>> successCallBack,
			Consumer<Throwable> exceptionCallback) {
		
		if(sqlSessionFactory == null)
		{
			return;
		}		
		
		System.out.println("MySQL : SELECT句発行処理(非同期処理)");
		Task<List<T>> task = new Task<List<T>>() {
		    @Override
		    protected List<T> call() throws Exception {
	            try {
	                runningTasks.add(this);
	                return MySqlManager.<T>Fill(callback);
	            } finally {
	                runningTasks.remove(this); // 終了時に必ず削除(this = Task)
	            }
		    }
		};
			
		// --- 2. UIスレッドで実行されるイベント ---
		task.setOnSucceeded(event -> {
			// 成功時：
			System.out.println("MySQL : 非同期処終了[成功]");
			successCallBack.accept((List<T>)task.getValue());
		});

		task.setOnFailed(event -> {
		    // 失敗時(Exceptionが発生した場合)：
			System.out.println("MySQL : 非同期処終了[失敗]");
			exceptionCallback.accept(task.getException());
		});

		// --- 3. 実行 ---
		executor.execute(task); 
	}
	
	/**
	 * MySqlManager 終了処理
	 */
	public static synchronized void Close() {
		
		cancelAllTasks();
		shutdownExecutor();
		
		if(sqlSessionFactory == null)
		{
			return;
		}		

	    try {
	        HikariCpClose();
	    } finally {
	        sqlSessionFactory = null;
	    }
	}

	/**
	 * 実行中のすべての非同期タスクを中断する
	 */
	private static void cancelAllTasks() {
	    synchronized (runningTasks) {
	        for (Task<?> task : runningTasks) {
	            if (task.isRunning()) {
	                // trueを渡すと実行中のスレッドに interrupt() を送る
	                task.cancel(true); 
	            }
	        }
	        runningTasks.clear();
	    }
	}	
	
	/**
	 * スレッドプール終了(解放処理)
	 * @brief 並列実行用のスレッドプールを解放する
	 */
	private static void shutdownExecutor() {
		if (!executor.isShutdown()) {
			System.out.println("MySQL : 非同期処理 スレッドプール解放");

			executor.shutdown(); // 新しいタスクを受け付けない
	        try {
	            // 5秒間だけ、現在実行中のタスクが終わるのを待つ
	            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
	            	executor.shutdownNow(); // 終わらなければ強制終了
	            }
	        } catch (InterruptedException e) {
	        	// 継続処理：例外をthrowしない
	        	executor.shutdownNow();

	        	// 割り込みステータスを復旧（お作法）
	        	Thread.currentThread().interrupt(); 
	        }
	    }
	}
	
	/**
     * 終了処理(HikariCP)
	 * @brief HikariCPを用いたConnectionを確立していた場合、Closeする。
     */
    private static void HikariCpClose() {
        
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
        	System.out.println("MySQL : HikariCP DataSource Closeing");
          	hikari.close();
        }   
    }
    
    /**
     * MySQL コネクションの確立(Sessionの生成)
     */
    private void SqlSessionSettings() {
    	System.out.println("MySQL : SQL Session生成");
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
        	System.err.println("MySQL : SQL Session生成失敗");
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
        try (InputStream inputStream = this.
        		getClass().
        		getClassLoader().
        		getResourceAsStream(mybatisConfigXmlPath)) {
        	
        	if (inputStream == null) {
                throw new RuntimeException("設定ファイルが見つかりません: " + mybatisConfigXmlPath);
            }
    		
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("設定ファイルの読み込みに失敗", e);
        }
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
        try (InputStream inputStream = this.
        		getClass().
        		getClassLoader().
        		getResourceAsStream(mybatisConfigXmlPath)) {
        	
        	if (inputStream == null) {
                throw new RuntimeException("設定ファイルが見つかりません: " + mybatisConfigXmlPath);
            }
    		
        	XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
   
    		Configuration config = parser.parse(); // XMLの内容をロード
    		config.setEnvironment(environment);   // プログラムで生成したHikariCP環境をセット
    		sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);      	
        } catch (IOException e) {
            throw new RuntimeException("設定ファイルの読み込みに失敗", e);
        }
	}
}
