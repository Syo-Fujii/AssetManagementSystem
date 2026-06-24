package application.java.window;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.ApplicationUserModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppSession;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.manager.HashConvertManager;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.WebServiceManager;
import application.resources.mapper.ApplicationUserMapper;
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
 * ログイン画面
 * 認証は[PassKey]を用いた認証と、ID/Passを検証する２種類を用意
 * ① [PassKey]は、外部システム(Blazor:exe)にてPassKey専用のサーバー(LocalHost：URL)を呼び出す
 * JavaFx側ではPassKey専用のサーバーより結果(Response)を受信するサーバー(LocalHost)を起動し、
 * 結果(Response)を受信して、内容に応じて画面を遷移する。
 * ② ID/Pass認証では、LOGIN ID(登録メールアドレス)を条件にDBに登録されているPasswordを検証する
 * Passwordは[PBKDF2-SHA256]形式のHASH値で登録されている
 */
public class LoginController extends BaseFormPage {

	private final String FORM_NAME = "ログイン画面";
	
	private WebServiceManager receiveSvr = null;
	private Process qrAuthProcess = null;
	private double xOffset = 0;
    private double yOffset = 0;	
	
    private int loginUserNo = -1;
    
	
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
    @SuppressWarnings("unused")
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
     * [Id・Pass認証] ログインボタン押下時の処理
     */
    @FXML
    public void onLoginButtonClicked() {
        var userId = txt_userId.getText();
        var pass = txt_password.getText();

        // 入力チェック
        if (userId == null || userId.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
        	MessageBox.ShowWarnig(
        			"注意",
        			null,
        			"ID(メールアドレス)とパスワードを入力してください。");
            return;
        }

        try {
            LogManager.writeInfo("[" + FORM_NAME + "] ： ログインIDとパスワードを検証中...");

            // クエリ用にアカウントIDをセットし、MySQLからユーザー情報を1件取得
            var appUserEntity = this.getLoginUserEntity(userId);

            if (appUserEntity == null) {
                var msg = "[" + FORM_NAME + "] : アカウントのログインIDが正しくありません。又は登録されていません。";
            	
            	super.showMessageException("注意", msg);	

                return;
            }

            // アカウントのロックアウト状態をチェック (UTC時間で比較判定)
            Instant now = Instant.now();
            var lockoutTime = appUserEntity.getLockOutDateTime();

            if (lockoutTime != null && lockoutTime.toInstant(ZoneOffset.UTC).isAfter(now)) {
            	var minutesLeft = Duration.between(now, lockoutTime.toInstant(ZoneOffset.UTC)).toMinutes() + 1;
            	var msg = "[" + FORM_NAME + "] : このアカウントは現在ロックアウトされています。"
                		+ "あと " + minutesLeft + "分ほど時間をおいてからお試しください。";
            	
            	super.showMessageException("警告", msg);	         
                return;
            }

            // パスワードの検証 (PBKDF2-SHA256 を検証)
            if (!new HashConvertManager().verifyPasswordSHA256PBKDF2(pass, appUserEntity.getPassword())) {
                
                // 失敗カウントを1増やす
                int failedCount = appUserEntity.getFailedCount() + 1;
                appUserEntity.setFailedCount(failedCount);

                String msg;
                if (failedCount >= AppConst.LOGIN_FAILED_MAX) {
                    
                	// 10回以上失敗：現在時刻から指定分のロックアウトをUTC時間で設定
                    appUserEntity.setLockOutDateTime(LocalDateTime.now(ZoneOffset.UTC).
                    		plusMinutes(AppConst.LOGIN_LOCKOUT_WAIT_TIME));
                
                    msg = "パスワードが " + AppConst.LOGIN_FAILED_MAX + " 回連続で間違っているため、アカウントがロックアウトされました。" + 
                    		AppConst.LOGIN_LOCKOUT_WAIT_TIME + "分後にお試しください。";
                } else {
                    // あと何回間違えられるかを通知
                    int remainingAttempts = AppConst.LOGIN_FAILED_MAX - failedCount;
                    msg = "パスワードが正しくありません。（ロックアウトまであと " + remainingAttempts + " 回）";
                }

                // データベース（MySQL）へ状態を更新保存
                this.executeCudQuery(List.of(appUserEntity));
            	super.showMessageException("警告", "[" + FORM_NAME + "] : " + msg);	         
                
                return;
            }

            // パスワード検証成功時の処理
            LogManager.writeInfo("[" + FORM_NAME + "] ： パスワード認証に成功しました。");

            // 失敗カウントが残っていれば、リセットしてDBを更新
            if (appUserEntity.getFailedCount() > 0) {
                
            	appUserEntity.setFailedCount(0);
                appUserEntity.setLockOutDateTime(null);
                
                this.executeCudQuery(List.of(appUserEntity));
            }

            // ログインに成功したEntityを保持
            AppSession.setLoginUser(appUserEntity);
            LogManager.writeInfo("[" + FORM_NAME + "] ： ログインセッションを確立しました。操作者: " + appUserEntity.getStaffName());

            // メニュー画面へ遷移
            LogManager.writeInfo("[" + FORM_NAME + "] ： メニュー画面へ切替"); 
            this.showMenuPage();

        } catch (Exception e) {
            String title = "[" + FORM_NAME + "] ： ID・PASS認証処理中にエラーが発生しました";
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
     * クエリ発行処理(Mapper) 社員マスター取得処理
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
     * 社員番号を条件にデータを取得<br>
     * PassKey認証成功の際、[社員番号]を条件にログインユーザー情報を取得する
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員 + 権限 取得処理");
    	
    	ApplicationUserMapper mapper = session.getMapper(ApplicationUserMapper.class);
	    
	    // 社員マスタ データ取得
	    return (List<T>) mapper.getLoginUserByNo(loginUserNo);
    }    

    /**
     * 社員認証マスター更新クエリ発行(ロックアウト終了日時 / ログイン失敗回数)
     * クエリ発行処理(Mapper:トランザクション処理)
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
	@SuppressWarnings("unused")
	@Override
	protected <T extends BaseTableViewModel> Boolean executeCudMapperFunction(SqlSession session, List<T> listData) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員認証マスター更新(ロックアウト終了日時 / ログイン失敗回数)");
		
		if (listData == null || listData.isEmpty()) { return false; }
		
		var row = (ApplicationUserModel)listData.getFirst();
		ApplicationUserMapper mapper = session.getMapper(ApplicationUserMapper.class);
	    
	    // 更新処理
	    Integer updCount =  mapper.updStaffAuthOnes(row);

	    return true;
    }	 
    
    /**
     * DBクエリ(CUD)発行結果に応じた処理
     * @param status ExcuteQueryResultStatus 実行結果のステータス
     * @brief DBクエリを発行した際の結果処理<br>
     * クエリの発行結果に対するメソッド(処理)がある場合に用いる。<br>
     * Exceptionが発生している場合は、当該メソッドの後で、Exceptionがthrowされる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	@Override
	protected void excuteQueryResult(ExcuteQueryResultStatus status){
		// 更新件数が0件のクエリが存在していた場合、例外MSGを表示
		if (status ==  ExcuteQueryResultStatus.NO_ROWS_AFFECTED) {
			
			// 各ボタン無効化
			loginFieldsEnabled(false);
			
			String title = "DB クエリ発行結果エラー";
			
			StringBuilder sb = new StringBuilder();
			sb.append("更新(社員認証マスター)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
        	
			super.showMessageException(title, sb.toString());		
		}
	}
	
    /**
     * ログインユーザ情報の取得
     * @param userId ログインユーザID(メールアドレス)
     * @throws Exception
     * @return [ApplicationUserModel] 取得結果(ログインユーザ情報：単一データ)
     * @brief controller内で用いるクエリ発行処理<br>
     * ログインID(アカウントID：メールアドレス)を条件にデータを取得する
     */
	private ApplicationUserModel getLoginUserEntity(String userId)
    {
		LogManager.writeTrace("検索・ログインユーザ情報 取得処理");
    	
		var rows = MySqlManager.<ApplicationUserModel>Fill(
				(SqlSession session) -> {
					try	{
						return this.getLoginUserData(session, userId);
						
					} catch (Exception e){
						LogManager.writeError("検索・ログインユーザ情報 取得失敗");
						throw new RuntimeException(e);
					}});
		
	    if (rows != null && !rows.isEmpty()) { return rows.get(0);}
	    
	    return null;
    }    
    
	/**
     * ログインユーザ情報の取得
     * @param session
     * @param userId ログインユーザID(メールアドレス)
     * @throws Exception
     * @return List<ApplicationUserModel> 取得結果(行データ:ApplicationUserModel のList)
     */
    private List<ApplicationUserModel> getLoginUserData(SqlSession session, String userId) throws Exception
    {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： ログインユーザ情報(DB 社員 + 社員認証 マスタ取得処理");
    	try {
    		ApplicationUserMapper mapper = session.getMapper(ApplicationUserMapper.class);
    	    
    	    // ログインユーザ情報 取得
    	    return mapper.getLoginUserData(userId);

    	} catch(Exception e){
    		throw new Exception(e);
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
            
            // 社員番号取得・ログインユーザ情報取得処理 
            this.loginUserNo = AppUtil.parseInt(token, -1);
            AppSession.setLoginUser(super.<ApplicationUserModel>getEntity());
            
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
     * タイトルバー用画面移動イベント
     * @brief Window枠を無効(不可視)化しているため、コンテンツ枠の移動をWindow枠に偽装する
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
