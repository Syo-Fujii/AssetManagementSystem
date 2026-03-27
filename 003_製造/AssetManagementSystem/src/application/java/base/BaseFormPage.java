package application.java.base;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.form.JavaFxManager;
import javafx.scene.paint.Color;

/** 画面生成基底クラス(Controller基底クラス) 
 *  画面設定(Controller)に対するクラス
 *  DB操作については、MySqlManagerの各メソッド・機能をstaticにて定義しているので
 *  継承[ extends]しない。
 */
public abstract class BaseFormPage {
	private Integer windowWidth = null;
	private Integer windowHeight = null;
	private String fxmlFilePath = "";
	private String cssFilePath = "";
	
	private String windowTitle  = "";
	private String pageTitle  = "";

	/* ウィンドウ枠使用設定 */
	private boolean useframe = true;
	
	/* 背景ウィンドウ色 */
	private Color windowColor = null;
	
	/* 内部クラス */
	public record keyValuePairItem<V>(Integer key, V value) {};	
	
	
	/**
	 * 参照する画面ファイル(FXML)を取得する。
	 * @return fxmlFilePath
	 */	
	public String getfxmlFilePath() {
		return fxmlFilePath;
	}

	/**
	 * 参照する画面ファイル(FXML)を設定する。
	 * @param fxmlFilePath 設定する画面コントローラ
	 */	
	public void setfxmlFilePath(String path) {
		this.fxmlFilePath = path;
	}

	/**
	 * CSSを取得する。
	 * @return CSS
	 */
	public String getCssFile() {
		return this.cssFilePath;
	}

	/**
	 * CSSを設定する。
	 * @param cssName 設定するCSS
	 */
	public void setCssFile(String css) {
		this.cssFilePath = css;
	}
	
	/**
	 * 画面.横幅を取得する。
	 * @return windowWidth
	 */
	public Integer getWindowWidth() {
		return windowWidth;
	}

	/**
	 * 画面.横幅を設定する。
	 * @param windowWidth 設定する幅
	 */
	public void setWindowWidth(Integer windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 * 画面.縦高を取得する。
	 * @return windowHeight
	 */
	public Integer getWindowHeight() {
		return windowHeight;
	}

	/**
	 * 画面.縦高を設定する。
	 * @param windowHeight 設定する高さ
	 */
	public void setWindowHeight(Integer windowHeight) {
		this.windowHeight = windowHeight;
	}	

	/**
	 * Window(枠)背景色を取得する。
	 * @return backGroundColor javafx.scene.paint.Color
	 */	
	public Color getWindowColor() {
		return this.windowColor;
	}

	/**
	 * Window(枠)背景色を設定する。
	 * @param backGroundColor javafx.scene.paint.Color 設定する背景色
	 * @brief 説定した場合、Window枠の背景色を変更する。
	 */	
	public void setWindowColor(Color backGroundColor) {
		this.windowColor = backGroundColor;
	}	

	/**
	 * Window枠有効判定を取得する。
	 * @return useframe boolean Window枠有効判定
	 */		
	public boolean getIsUseWindowFrame() { 
		return this.useframe; 
	}

	/**
	 * Window(枠)背景色を設定する。
	 * @param isUseFrame boolean Window枠有効判定
	 * @brief 無効とした場合、Window枠を非表示にする
	 */	
	public void setIsUseWindowFrame(boolean isUseFrame) { 
		this.useframe = isUseFrame; 
	}
	
	/**
	 * Window(枠)名称を取得する。
	 * @return windowTitle
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * Window(枠)名称を設定する。
	 * @param windowTitle 設定する画面名称
	 * @brief 説定した場合、Window枠の名称を変更する。
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}	

	/**
	 * 画面(page)名称を取得する。
	 * @return pageTitle
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	/**
	 * 画面(page)名称を設定する。
	 * @param pageTitle 
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	
	/**
	 * 遷移先画面の設定
	 * @param cls 遷移先画面
	 * @param params 遷移先画面に受け渡す値
	 */
	public void setPage(BaseFormPage cls, Object...params){
		try{
			JavaFxManager.application.setPage(cls, params);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * (stage= Window)画面表示完了Event 
	 * 初回表示の際に設定する場合、使用する。
     * @brief [scene]の描画が完全に完了した時点で呼出されるイベント<br>
     * JavaFxでは、生成された[Window](Stage)のPanel(scene)を切り替えることで、画面の遷移を行っているので
     * [Window](Stage)の生成は初回の１回きりとなる<br>
     * [.NET]でのForm_Shownイベント相当であるが、Windowを切り替えないので同じようには使えない。<br>
     * 継承先では[sceneShown]メソッドを書き換えることで、WindowEvent.WINDOW_SHOWN イベントでの内容を定義する。<br>
     * OSレベルでウィンドウが表示され、全コンポーネントのレイアウト計算や描画が完全に終わった状態での表示処理	 
	 */
	public void windowShown(){
		stageShown();
	}
	
	/**
	 * 起動パラメータ読込
	 * @param params パラメータの配列
	 * @brief 起動の際にパラーメータとして受けった値を設定する場合に用いるメソッド
	 */
	public void loadParameter(Object[] params){
	}

    /**
     * DB取得処理(非同期処理)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @brief controller内で用いる取得(Fill)処理<br>
     * 画面内にTableViewなどのDB取得を要するメソッド(処理)がある場合に用いる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected final <T extends BaseTableViewModel> void fillTableAsync()
    {
    	MySqlManager.<T>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return executeSelectQuery(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}},
    			listData -> { successResult( listData ); },
    			exception -> { exceptionResult( exception ); });
    }

    /**
     * DB操作(登録・更新・削除)処理(同期処理)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param List<T> DB操作の条件となるデータ(行データ:T のList)
     * @return DB操作結果
     * @brief controller内で用いるDB操作(INS・UPD・DEL)処理<br>
     * 画面内にDBの操作(INS・UPD・DELなどのトランザクション処理を行う操作)がある場合に用いる<br>
     * トランザクション内に複数のクエリを発行する場合は[excuteCudMapperFunction]内で複数のMapperを呼出す<br>
     * DB操作完了まで画面処理(動作)を待機させたいため、同期処理にて行う<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected final <T extends BaseTableViewModel> Boolean executeCudQuery(
			List<T> listData) throws Exception
    {
    	return MySqlManager.ExecuteQuery_UseTransaction(
    			(SqlSession session, List<T> data) -> {
    				try {
    					return CudQueryUseTran(session, data);
    				} catch (Exception e) {
    					// ExecuteQuery_UseTransactionへ通知
    					throw new RuntimeException(e);
     			}}, 
    			(status) -> {
    				excuteQueryResult(status);
    			},
    			listData);
    }

    /**
     * DB操作(登録・更新・削除)処理(同期処理)：Bulk処理
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param listData List[T] DB操作の条件となるデータ(行データ:T のList)
     * @return DB操作結果
     * @brief controller内で用いるDB操作(INS・UPD・DEL)処理<br>
     * 画面内にDBの操作(INS・UPD・DELなどのトランザクション処理を行う操作)がある場合に用いる<br>
     * トランザクション内に複数のクエリを発行する場合は[excuteCudMapperFunction]内で複数のMapperを呼出す<br>
     * DB操作完了まで画面処理(動作)を待機させたいため、同期処理にて行う<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected final <T extends BaseTableViewModel> Boolean executeBulkQuery(
			List<T> listData) throws Exception
    {
    	return MySqlManager.ExecuteBulk_UseTransaction(
    			(SqlSession session, List<T> data) -> {
    				try {
    					return CudQueryUseTran(session, data);
    				} catch (Exception e) {
    					// ExecuteBulk_UseTransactionへ通知
     					throw new RuntimeException(e);
     			}},
    			(status) -> {
    				excuteQueryResult(status);
    			},
    			listData);
    }	
	
    /**
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
     * 画面内にTableViewなどのDB取得を要するメソッド(処理)がある場合に用いる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。<br>
     * 例：<br>
     * 		InventoryDetailsMapper mapper = session.getMapper(InventoryDetailsMapper.class);<br>
     *      // 備品詳細データ取得<br>
     *      return mapper.getTableDetailRecords(this.stockType, this.stockCode);<br>
     */
	protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
		return null;
    }
	
    /**
     * DBクエリ(CUD)発行結果に応じた処理
     * @param status ExcuteQueryResultStatus 実行結果のステータス
     * @brief DBクエリを発行した際の結果処理<br>
     * クエリの発行結果に対するメソッド(処理)がある場合に用いる。<br>
     * Exceptionが発生している場合は、当該メソッドの後で、Exceptionがthrowされる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected void excuteQueryResult(ExcuteQueryResultStatus status){
    }
	
	/**
     * DB取得成功時の処理(非同期処理)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param List 取得結果(行データ:T のList)
     * @brief controller内で用いる取得成功時の処理<br>
     * 画面内にTableViewなどのDB取得を要するメソッド(処理)がある場合に用いる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected <T extends BaseTableViewModel> void successResult(List<T> listData){
    }
	
    /**
     * DB取得失敗(例外発生)時の処理(非同期処理)
     * @brief controller内で用いる取得例外処理<br>
     * 画面内にTableViewなどのDB取得を要するメソッド(処理)がある場合に用いる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	protected void exceptionResult(Throwable e)
    {
		// 例外をMSG表示 + LOG出力
        LogManager.showAndWriteError(LogManager.getExceptionTitle(e), e);
    }		

    /**
     * DB操作クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @param List<T> DB操作の条件となるデータ(行データ:T のList)
     * @return DB操作結果
     * @brief controller内で用いるクエリ(INS・UPD・DEL)発行処理<br>
     * 画面内にDBの操作(INS・UPD・DELなどのトランザクション処理を行う操作)がある場合に用いる<br>
     * 当該メソッド内がトランザクションの範囲とし、複数のクエリを発行する場合は、対応した複数のMapperを呼出す。<br>
     * DB操作完了まで画面処理(動作)を待機させたいため、同期処理にて行う<br>    
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。<br>
     * 例：<br>
     *     // 連続更新<br>
     *     mapper.updateA(data1);<br>
     *     mapper.updateB(data2);<br>
     */
	protected <T extends BaseTableViewModel> Boolean executeCudMapperFunction(SqlSession session, List<T> listData) {
		return true;
    }
 	
	/**
	 * (stage)画面表示Shown(継承) 
	 * 初回表示の際に設定する場合、使用する。
     * @brief [stage]の描画が完全に完了した時点で呼出されるイベントの内容を定義する<br>
     * Window(Stage)の生成は、初回の1回しか行われない為、初回Windowを呼出す際に設定することがある場合用いる。<br>
     * 継承先画面にて、WindowShown([Stage]の描画が完全に完了した時点)を呼出す場合に用いる。<br>
     * OSレベルでウィンドウが表示され、全コンポーネントのレイアウト計算や描画が完全に終わった状態での表示処理	 
	 */
	protected void stageShown(){}

    /**
     * DBクエリ発行処理
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session　DBセッション
       * @return List<T> 取得結果(行データ:T のList)
     * @throws Exception 例外処理
     */
    private <T extends BaseTableViewModel> List<T> executeSelectQuery(SqlSession session) throws Exception
    {
    	try {
    		// クエリ発行(Mapperにて発行)
    		return executeMapperFunction(session);

    	} catch(Exception e){
    		throw new Exception(e); 
    	}
    }
    
    /**
     * DBクエリ(INS・UPD・DEL/トランザクション使用)発行処理
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session DBセッション
     * @param List<T> DB操作の条件となるデータ(行データ:T のList)
     * @throws Exception 例外処理
     */
    private <T extends BaseTableViewModel> Boolean CudQueryUseTran(SqlSession session, List<T> listData) throws Exception
    {
    	try {
    		// クエリ発行(Mapperにて発行)
    		return executeCudMapperFunction(session, listData);

    	} catch(Exception e){
    		throw new Exception(e);}
    } 
}
