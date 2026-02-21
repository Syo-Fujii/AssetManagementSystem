package application.java.base;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.manager.MySqlManager;
import application.java.manager.form.JavaFxManager;
import javafx.scene.control.Alert;

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
	 * (stage)画面表示Event 
	 * 初回表示の際に設定する場合、使用する。
	 */
	public void FormShown(){
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
						return excuteSelectQuery(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}},
    			listData -> { successResult( listData ); },
    			exception -> { exceptionResult( exception ); });
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
	protected <T extends BaseTableViewModel> List<T> excuteMapperFunction(SqlSession session) {
		return null;
    }

    /**
     * DB取得成功時の処理(非同期処理)
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
	protected void exceptionResult(Throwable exception)
    {
		System.err.println(exception.getMessage());
        // JavaFXのアラートを表示
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("データベースエラー");
        alert.setHeaderText("データの取得に失敗しました");
        alert.setContentText(exception.getCause() != null ? 
                             exception.getCause().getMessage() : exception.getMessage());
        alert.showAndWait();  
    }		
	
    /**
     * DBクエリ発行処理
     * @param session　DBセッション
     * @throws Exception 例外処理
     */
    private <T extends BaseTableViewModel> List<T> excuteSelectQuery(SqlSession session) throws Exception
    {
    	try {
    		// クエリ発行(Mapperにて発行)
    		return excuteMapperFunction(session);

    	} catch(Exception e){
    		throw new Exception(e); 
    	}
      }
}
