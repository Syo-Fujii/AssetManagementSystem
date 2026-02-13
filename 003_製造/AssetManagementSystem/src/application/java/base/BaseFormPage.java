package application.java.base;

import application.java.manager.form.JavaFxManager;

/** 画面生成基底クラス(Controller基底クラス) 
 *  画面設定(Controller)に対するクラス
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
}
