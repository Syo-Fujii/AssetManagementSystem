package application.Class;

import application.Manager.Form.JavaFxManager;


public abstract class BaseFormPage {
	private JavaFxManager application;
	
	private String cssName;
	private Integer windowWidth = null;
	private Integer windowHeight = null;
	private String windowTitle  = "";

	
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
	 * 画面名称を取得する。
	 * @return windowTitle
	 */
	public String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * 画面名称を設定する。
	 * @param windowTitle 設定する画面名称
	 */
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}	


	
	public void setApp(JavaFxManager application){
		this.application = application;
	}
	

	public void setPage(BaseFormPage cls, String fxml, Object...params){
		try{
			application.setPage(cls, fxml, params);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void loadParameter(Object[] params){
	}
}
