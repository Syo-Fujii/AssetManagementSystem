package application.java.window;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import application.java.base.BaseFormPage;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.WebServiceManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/*
 * 
 * 
 * 
 * 
 * */
public class LoginController extends BaseFormPage {

	private final String FORM_NAME = "ログイン画面";
	
	private WebServiceManager receiveSvr = null;
	private Process qrAuthProcess = null;
	private double xOffset = 0;
    private double yOffset = 0;	
	
	
	@FXML private VBox titleBar;
	@FXML private TextField txt_userId;
    @FXML private PasswordField txt_password;

    @FXML private Button passkeyAuth_Button;
    @FXML private Button login_Button;
    @FXML private Button close_Button;
    


    // 最小化ボタンのアクション
    @FXML
    public void handleMinimize(MouseEvent event) {
        // ウィンドウを最小化する
        ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true);
    }

    
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public LoginController() {
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		this.setWindowColor(Color.TRANSPARENT);
		this.setIsUseWindowFrame(false);
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("Login"));
		this.setCssFile(AppUtil.MakeCssFilePath("LoginStyle"));

		this.setPageTitle(FORM_NAME);
	}
	
    /**
     * 画面(scene)初期化イベント
     * .NET FormLoad & Shown相当 
     * 画面の表示前、ノードが配置された段階で実行
     * @brief 画面(scene)の遷移には、FXMLLoaderでFXMLを読み込み、新しいControllerを生成しているので<br>
     * 当該が各画面(scene)の呼び出しイベント(FormLoad/FormShown)相当となる。<br>
	 * エラーハンドリングは[setPage](FXMLLoader.load)となる。
     */
    @FXML
	void initialize() {
     	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize"); 
        try {
        	// 最初の画面起動として、[SQL Session]を生成・保持する。
    		MySqlManager.getSqlSessionFactory();
    		
            // ✨【追加】UserID と Password の入力状態をリアルタイム監視するリスナーを登録
            txt_userId.textProperty().addListener((observable, oldValue, newValue) -> handleInputFieldChanged());
            txt_password.textProperty().addListener((observable, oldValue, newValue) -> handleInputFieldChanged());
    		
    		onTitleBarMousePressEvent();

        } catch (Exception e) {
            String title = "[" + FORM_NAME + "] ： WebView2の初期化中にエラーが発生しました";
            LogManager.showAndWriteError(title, e);
        }	
    }	 
    
    /**
     * [QRコード認証] パスキーボタン押下時の処理
     */
    @FXML
    public void onPassKeyAuthButtonClicked() {
        try {
            LogManager.writeInfo("[" + FORM_NAME + "] ： [QRコード認証]ボタン押下"); 

            loginFieldsEnabled(false);
            
            // サーバーマネージャーを生成し、「成功した時の処理」をラムダ式で渡す
            this.receiveSvr = new WebServiceManager(
            		() -> { this.handleAuthenticationSuccess(); },
            		() -> { this.loginFieldsEnabled(true); });

            // 認証結果受信用ローカルサーバー(localhost)起動
            this.receiveSvr.startLocalServer();

            // PassKey認証サーバー(Web:localhost)起動
            LogManager.writeInfo("[" + FORM_NAME + "] ： 認証サーバーexeを起動します。");
            var authExePath = AppConst.QR_AUTH_EXE_FULL_PATH + "AssetManagementPassKeyLogIn.exe";
            var pb = new ProcessBuilder(authExePath);            
          
            this.qrAuthProcess = pb.directory(new File(authExePath).getParentFile()).start();
            
            // ブラウザ(OS標準)起動/PassKey認証サイト表示
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            	var authServerUrl = this.receiveSvr.getAuthServerUrl();
            	
            	LogManager.writeDebug("[" + FORM_NAME + "] ： 標準ブラウザを起動します URL: " + authServerUrl);

                Desktop.getDesktop().browse(new URI(authServerUrl));

            } else {
                throw new UnsupportedOperationException("標準ブラウザの起動に対応していません。");
            }
     
        } catch (Exception e) {
        	loginFieldsEnabled(true);
        	
        	String title = "[" + FORM_NAME + "] ： 認証処理中にエラーが発生しました";
            LogManager.showAndWriteError(title, e);
        }
    }

    /**
     * [QRコード認証] パスキーボタン押下時の処理
     */
    @FXML
    public void onLoginButtonClicked() {
        try {
         
     
        } catch (Exception e) {
            String title = "[" + FORM_NAME + "] ： 認証処理中にエラーが発生しました";
            LogManager.showAndWriteError(title, e);
        }
    }

    /**
     * 「×」ボタン押下時の処理
     */
    @FXML
    private void onExitButtonClicked() {
        try {
            LogManager.writeInfo("[" + FORM_NAME + "] ： [システム終了]ボタン押下により、アプリを終了します"); 
            
            // 受信用ローカルサーバーの停止
            if (this.receiveSvr != null) 
            { 
            	shutdownAuthComponents(); 
            	this.receiveSvr = null;
            }
            
            this.qrAuthProcess = null;
            
            // JavaFXプラットフォーム（画面スレッド）の終了
            javafx.application.Platform.exit(); 
            
        } catch (Exception e) {
            String title = "[" + FORM_NAME + "] ： システム終了処理中にエラーが発生しました";
            LogManager.showAndWriteError(title, e);
            
            // 万が一の致命的な例外時のみ、強制終了させる
            System.exit(1);
        }
    }    
    
    /**
     * 「認証成功後」処理
     * 認証結果受信用ローカルサーバー受信成功Event
     */
    private void handleAuthenticationSuccess() {
        try {
            LogManager.writeInfo("[" + FORM_NAME + "] ： ログイン(PassKey)成功");

            // 受信トークン取得
            var token = this.receiveSvr.getAuthenticatedToken();
            LogManager.writeDebug("[" + FORM_NAME + "] ： 受信したトークン情報: " + token);

         
            // サーバー終了処理
            this.shutdownAuthComponents();
            
            LogManager.writeInfo("[" + FORM_NAME + "] ： メニュー画面へ切替"); 
            this.showMenuPage();
            
        } catch (Exception e) {
            String title = "[" + FORM_NAME + "] ： PassKey認証後の処理中にエラーが発生しました";
            LogManager.showAndWriteError(title, e);
        }
    }

    /**
     * 認証サーバー・受信サーバー終了処理
     */
    private void shutdownAuthComponents() {
        
        // 受信用ローカルサーバーの停止
        if (this.receiveSvr != null) { this.receiveSvr.stopLocalServer(); }
        
        // バックグラウンドで起動したexeプロセスの強制終了
        if (this.qrAuthProcess != null && this.qrAuthProcess.isAlive()) 
        {
        	LogManager.writeTrace("[" + FORM_NAME + "] ： PassKey認証サーバープロセスを終了します");

        	this.qrAuthProcess.destroy();
            this.qrAuthProcess = null;
        }
    }    

    /**
     * 
     * @brief 入力フィールドの文字変更を検知し、パスキーボタンの有効/無効を切り替えます
     */
    private void handleInputFieldChanged() {
        // IDまたはパスワードのどちらかに1文字でも入力されているかチェック
        boolean isUserIdEntered = txt_userId.getText() != null && !txt_userId.getText().trim().isEmpty();
        boolean isPasswordEntered = txt_password.getText() != null && !txt_password.getText().trim().isEmpty();

        // どちらか一方が入力されていれば、パスキーボタンを無効化（disable = true）
        // 両方とも空白（未入力）であれば、有効化（disable = false）
        passkeyAuth_Button.setDisable((isUserIdEntered || isPasswordEntered));
    }    
    
    /**
     * 認証失敗時やキャンセル時に、入力を再度有効化するための共通メソッド
     */
    private void loginFieldsEnabled(boolean IsEnabled ) {
        javafx.application.Platform.runLater(() -> {
        	passkeyAuth_Button.setDisable(!IsEnabled);
        	
        	txt_userId.setDisable(!IsEnabled);
            txt_password.setDisable(!IsEnabled);
            
            login_Button.setDisable(!IsEnabled);
        });
    }   

    /**
     * finder風タイトルバー用画面移動イベント
     */
    private void onTitleBarMousePressEvent() {
    	// タイトルバーをマウスで押したときの処理
        titleBar.setOnMousePressed(
        		( event ) -> 
        		{
        			xOffset = event.getSceneX();
        			yOffset = event.getSceneY();
        		});

        // マウスをドラッグしたときの処理
        titleBar.setOnMouseDragged(
        		( event ) -> 
        		{
        			Stage stage = (Stage) titleBar.getScene().getWindow();
        			stage.setX(event.getScreenX() - xOffset);
        			stage.setY(event.getScreenY() - yOffset);
        		});
    }        
    
    
    /**
     * 遷移先画面呼び出し
     * @brief 遷移先画面(メニュー：Menu)を呼出す。<br>
     */
    private void showMenuPage() throws Exception {
        // メニュー画面へ遷移
        super.setPage(new application.java.window.MenuController(true));
    }   

}
