package application.java.window.masterMaintenance.staffMaster;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.AuthMasterModel;
import application.java.base.dbTablesModel.StaffAuthModel;
import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.base.tableViewListModel.ApplicationUserModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.HashConvertManager;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableViewManager;
import application.java.manager.customControl.CustomComboBoxControlManager;
import application.java.manager.customControl.CustomTextFieldControlManager;
import application.resources.mapper.ApplicationUserMapper;
import application.resources.mapper.AuthMasterMapper;
import application.resources.mapper.StaffAuthMapper;
import application.resources.mapper.StaffMasterMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;


/**
 * 社員マスタ( + 社員認証マスタ) メンテナンス画面
 * @brief [inventoryReturn]画面操作メソッド(Controller)<br>
 * <p>
 * TableViewを継承した[TableViewManager](カスタムControl)を用いる場合、
 * 画目デザイン(Screen Builder)では正しく操作できない。<br>
 * ⇒ Screen Builderでは、カスタムControlはブラックボックス化されTableViewの操作(変更や項目追加など)が行えない。<br>
 * なので画面レイアウトを変更・操作(ableViewManagerの配置・変更など)する場合は、<br>
 * 手動で、Source上の[TableViewManager]を[TableView]に書き換えてScreen Builderを起動・デザインの変更を行う。<br>
 * デザインを変更・確定後にControlを[TableViewManager]の戻すことで編集を行う。<br>
 * ※ Screen Builderでは、カスタムControlの継承元に関する各機能は実行できない。<br>
 * ※ [TableViewManager]を用いても、Build・動作は正常におこなわれる。<br>
 * <br>
 * 
 */
public class FormController extends BaseFormPage {
	private final String FORM_NAME = "社員マスタ メンテナンス";
	
	private StaffMasterModel editMasterData = null;
	private StaffAuthModel editAuthData = null;
	
	private Boolean isEditMode = false;
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	
	@FXML private TableViewManager<ApplicationUserModel> tableListView;
	@FXML private TableColumn<StaffMasterModel, Integer> col_no;
	@FXML private TableColumn<StaffMasterModel, String> col_name;
	@FXML private TableColumn<StaffMasterModel, Integer> col_auth;
	@FXML private TableColumnManager<StaffMasterModel, Boolean> col_del;
	
	@FXML private Button new_button;
	
	@FXML private CustomTextFieldControlManager txt_Edit_No;
	@FXML private TextField txt_Edit_Name;
	@FXML private CustomComboBoxControlManager<String> cbo_Edit_Auth;
	@FXML private CheckBox check_Edit_DelFlg;
	@FXML private CustomTextFieldControlManager txt_Edit_Email;
	@FXML private CustomTextFieldControlManager txt_Edit_Password;
	
	@FXML private Button insert_button;
	@FXML private Button update_button;
	@FXML private Button back_button;

	// E-Mail形式(コンパイル済、形式)
	private  Pattern emailPattern = Pattern.compile(AppConst.REGEX_EMAIL_ADDR);
	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理");
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("StaffMasterMaintenance"));
		this.setCssFile(AppUtil.MakeCssFilePath("StaffMasterStyle"));
		
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
	public void initialize() {
     	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize"); 
    	
    	// 最初の画面起動として、[SQL Session]を生成・保持する。
		MySqlManager.getSqlSessionFactory();

		// TableView起動設定
		this.tableViewSettings();

		// コンボボックスの生成(DB)
		makeEditComboBox_AuthMembers();
		
		// 画面起動設定
		this.formInitialize();

		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト表示処理");
    	super.<ApplicationUserModel>fillTableAsync();
    }

    /**
     * [新規]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onNewButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [新規]ボタン押下"); 
    		
    		if( isEditMode && isEditControlsChanged() && !showMessageEditControlsValue()) {
    			return;
    		}
    		
    		// 新規登録モード
    		setupNewRecordMode();

    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： [新規]ボタン押下にて、エラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    		setEditControlsAllDisabled();    	
    	}
    }

    /**
     * [登録]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onInsertButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [登録]ボタン押下"); 
    		
    		if( isEditMode || !isEditControlsChanged() ) { return; }
    		
    		getEditControlsValue();
    		
        	AppConst.rowCheckResultData checkResult = isMasterRowsCheck();
        	if (!checkResult.result()) {
        		if( checkResult.isShowMsgBox()) {
        			this.showMessageInputError(checkResult.message());
        		}
        		setupErrorInputControlsFocus( checkResult.colNo() );
        		return;
        	}
        	
        	// 登録実行確認
        	if( !showMessageIsExecuting("登録", this.editMasterData.getDelFlg()) ) { return; }
    		
        	// 登録処理(社員マスタ登録処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 社員マスタ登録処理");
        	super.executeCudQuery( List.of(new ApplicationUserModel(editMasterData, editAuthData )));

    		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<StaffMasterModel>fillTableAsync();

    		// 新規登録モード
    		setupNewRecordMode();

    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： [登録]ボタン押下にて、エラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    		setEditControlsAllDisabled();
    	}
    }

    /**
     * [更新]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onUpdateButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [更新]ボタン押下"); 
    		
    		if( !isEditMode || 
    			!isEditControlsChanged() ||
    			!showMessageIsExecuting("更新", check_Edit_DelFlg.isSelected()) ) { return; }

    		getEditControlsValue();
    
        	AppConst.rowCheckResultData checkResult = isMasterRowsCheck();
        	if (!checkResult.result()) {
        		if( checkResult.isShowMsgBox()) {
        			this.showMessageInputError(checkResult.message());
        		}
        		setupErrorInputControlsFocus( checkResult.colNo() );
        		return;
        	}
    		
        	// 更新処理(社員マスタ更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 社員マスタ更新処理");
        	super.executeCudQuery( List.of(new ApplicationUserModel(editMasterData, editAuthData )) );

    		// 新規登録モード
        	this.isEditMode = false;
        	this.editMasterData.setStatus(AppConst.DataRowState.UNCHANGED);
        	this.editAuthData.setStatus(AppConst.DataRowState.UNCHANGED);
        	
        	LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<ApplicationUserModel>fillTableAsync();

    		setupNewRecordMode();
        	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： [更新]ボタン押下にて、エラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    		setEditControlsAllDisabled();
    	}
    }
    
    /**
     * [メニューに戻る]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onBackButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [メニューに戻る]ボタン押下"); 
    		
    		// 遷移元画面に切替
    		this.showOwnerPage();
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： メニュー画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
    }

	/**
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員マスタ・社員認証マスタ取得処理");
    	
    	ApplicationUserMapper mapper = session.getMapper(ApplicationUserMapper.class);
	    
	    // 社員マスタ + 社員認証マスタ データ取得
	    return (List<T>) mapper.getAllUsers();
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 社員マスタ・社員認証マスタ連携(BIND)処理");
    		
    		// データ設定(BIND・SELL設定値)を初期化
    		tableListView.dataSourceClear();
    		List<ApplicationUserModel> rows = (List<ApplicationUserModel>) listData;
    		rows.forEach((row) -> row.setStatus( AppConst.DataRowState.UNCHANGED) );
    		
    		tableListView.setList( rows );
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 社員マスタ・社員認証マスタの連携中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e); 
    	}
    }
    
    /**
     * DB取得失敗(例外発生)時の処理(非同期処理)
     * @brief controller内で用いる取得例外処理
     */
	@Override
    protected void exceptionResult(Throwable exception)
    {
		LogManager.writeError("[" + FORM_NAME + "] ： DB 社員マスタ取得失敗");
		setEditControlsAllDisabled();
		
		super.exceptionResult(exception);
    }

    /**
     * 社員マスタ登録・更新クエリ発行
     * クエリ発行処理(Mapper:トランザクション処理：executeCudQueryのMapper処理)
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
	@Override
	protected <T extends BaseTableViewModel> Boolean executeCudMapperFunction(SqlSession session, List<T> listData) {
		String ExecuteTitle = this.isEditMode ? "更新" : "登録";
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員マスタ" + ExecuteTitle + "処理");
		
		var masterMapper = session.getMapper(StaffMasterMapper.class);
		var authMapper = session.getMapper(StaffAuthMapper.class);
		
		var data = (ApplicationUserModel) listData.getFirst();
		
		if (this.isEditMode)
		{
			// 更新処理
			if (data.MasterRowStatus().equals(AppConst.DataRowState.MODIFIED))
			{
				if ( masterMapper.updStaffMasterOnes( data.GetStaffMasterData()) != AppConst.DB_EXECUTE_ONES ) 
				{ 
					return false; 
				}
			}
			
			if (data.AuthRowStatus().equals(AppConst.DataRowState.MODIFIED))
			{
				if ( authMapper.updStaffAuthOnes( data.GetStaffAuthData()) != AppConst.DB_EXECUTE_ONES ) 
				{ 
					return false; 
				}
			}
		} else {
			// 登録処理
			if ( masterMapper.insStaffMasterOnes( data.GetStaffMasterData()) != AppConst.DB_EXECUTE_ONES ) 
			{ 
				return false; 
			}
			
			if ( authMapper.insStaffAuthOnes( data.GetStaffAuthData()) != AppConst.DB_EXECUTE_ONES ) 
			{ 
				return false; 
			}
		}
		return true;
    }    	
	
    /**
     * DBクエリ(CUD)発行結果に応じた処理(executeBulkQueryの処理結果)
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
			String title = "DB クエリ発行結果エラー";
			
			StringBuilder sb = new StringBuilder();
			sb.append("更新(社員マスタ・社員認証マスタ)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
			
			super.showMessageException(title, sb.toString());
			
    		setEditControlsAllDisabled();
		}
	}	

    /**
     * 登録項目：権限ComboBox 選択リスト取得処理
     * @brief コンボBOXのSource更新・差し替えのため、予めSourceとして設定したListを引数で受ける。<br>
     */
	private void fetchAuthMembers()
    {
		LogManager.writeTrace("登録項目・権限(ComboBox) 選択リスト取得処理");
    	
    	MySqlManager.<AuthMasterModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getAuthMasterData(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> { 
    				setupComboBox_EditAuth( listData ); },
    			exception -> {
    				LogManager.writeError("登録項目・権限(ComboBox)  選択リスト取得失敗");
    				super.exceptionResult(exception);
    			}
    	); 
    }	

    /**
     * 権限一覧の取得
     * @param session
     * @throws Exception
     * @return List<StaffMasterModel> 取得結果(行データ:AuthMasterModel のList)
     */
    private List<AuthMasterModel> getAuthMasterData(SqlSession session) throws Exception
    {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 権限マスタ取得処理");
    	try {
    		AuthMasterMapper mapper = session.getMapper(AuthMasterMapper.class);
    	    
    	    // 権限マスター取得
    	    return mapper.getAuthMasterData();

    	} catch(Exception e){
    		throw new Exception(e);
    	}
    }
	
	/**
	 * 社員マスタ存在確認用クエリ発行処理(Mapper)
	 * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
	 * @param session 継承元より渡される[SQLSession]
	 * @param data 発行するクエリの条件の値( 行データのクラス )
	 * @return 存在確認の結果
	 */
	private <T extends BaseTableViewModel> Boolean isExistsMasterData(SqlSession session, T data) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員マスタ存在確認");
		
		StaffMasterMapper mapper = session.getMapper(StaffMasterMapper.class);
		
		// 存在確認
		return  mapper.existsStaffNo( (StaffMasterModel) data );
    }

	/**
	 * 社員認証マスタメール重複登録確認用クエリ発行処理(Mapper)
	 * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
	 * @param session 継承元より渡される[SQLSession]
	 * @param data 発行するクエリの条件の値( 行データのクラス )
	 * @return 存在確認の結果
     * @brief 登録の場合は、全マスタデータより重複を検索する<br>
     *         更新の場合は、自メールアドレス以外で重複を検索する<br>
	 */
	private <T extends BaseTableViewModel> Boolean isExistsMailAddress(SqlSession session,  T data) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB メール重複確認");
		
		var email = ((StaffAuthModel) data).getEmailAddr();
		Integer no = null;
		if (isEditMode) { no = ((StaffAuthModel) data).getStaffNo(); }
		
		StaffAuthMapper mapper = session.getMapper(StaffAuthMapper.class);
		
		// 存在確認
		return  mapper.existsEmail( email, no );
    }	
	
	/**
	 * 登録項目 値変更チェック処理
	 * @return boolean 判定結果
     * @brief 登録用の各Controlの値が、初期と違う(値を編集した)場合は[真]<br>
	 */
	private boolean isEditControlsChanged() {
		
		if(this.editMasterData.RowStatus().equals(AppConst.DataRowState.MODIFIED))
    	{
			System.err.println("isEditControlsChanged: TracePoint : 1");
			return true;
    	}
		
		if(this.editAuthData.RowStatus().equals(AppConst.DataRowState.MODIFIED))
    	{
			System.err.println("isEditControlsChanged: TracePoint : 1");
			return true;
    	}
		
		Integer no = this.editMasterData.getStaffNo();
    	String name = this.editMasterData.getStaffName();
    	Integer auth = this.editMasterData.getAuthNo();
    	Boolean del = this.editMasterData.getDelFlg();
    	
    	String email = this.editAuthData.getEmailAddr();
    	
    	String noString = "";
    	if ( !AppUtil.IsNull(no) && no > 0 ) { noString = no.toString(); }
		if ( !Objects.equals(txt_Edit_No.getText(), noString) ) 
		{ 
			this.editMasterData.setStatus(AppConst.DataRowState.MODIFIED);
			return true; 
		}

		if ( !Objects.equals(txt_Edit_Name.getText(), name) ) 
		{ 
			this.editMasterData.setStatus(AppConst.DataRowState.MODIFIED);
			return true; 
		}	

		if ( !Objects.equals(this.cbo_Edit_Auth.getSelectedKey(), auth) ) 
		{ 
			this.editMasterData.setStatus(AppConst.DataRowState.MODIFIED);
			return true; 
		}
		
		Boolean rowDataDelFlg = del != null ? del : false;
		if ( !Objects.equals(check_Edit_DelFlg.isSelected(), rowDataDelFlg) ) 
		{ 
			this.editMasterData.setStatus(AppConst.DataRowState.MODIFIED);
			return true; 
		}	

		if ( !Objects.equals(this.txt_Edit_Email.getText(), email) ) 
		{ 
			this.editAuthData.setStatus(AppConst.DataRowState.MODIFIED);
			return true; 
		}	
		
    	var inputPassword = this.txt_Edit_Password.getText();
    	if ( isEditMode && !inputPassword.equals(this.editAuthData.getPass()))
    	{
    		this.editAuthData.setStatus(AppConst.DataRowState.MODIFIED);
			return true;
    	}
    	else if ( isEditMode && !inputPassword.equals(AppConst.PASSWORD_MASK) )
    	{
    		this.editAuthData.setStatus(AppConst.DataRowState.MODIFIED);
			return true;
    	}
		
		return false;
	}
    
	/**
	 * DB処理確認Message
	 * @return 確認結果
	 */
    private Boolean showMessageIsExecuting(String execute, boolean delFlg) {
    	Integer no = this.editMasterData.getStaffNo();
    	String name = this.editMasterData.getStaffName();

    	StringBuilder sb = new StringBuilder();
		
    	if ( delFlg ) 
    	{
    		sb.append("※ 削除フラグを有効にした場合、貸出業務の対象外となります。");
    		sb.append(AppUtil.newLine());
    		sb.append("   よろしいですか？");
    		sb.append(AppUtil.newLine());
    		sb.append(AppUtil.newLine());
    	}
    	
    	sb.append("社員番号 :[").append(no.toString()).append("] ");
		sb.append("氏名 :[").append(name).append("] ");
		sb.append(AppUtil.newLine());

    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			false,
    			execute + "確認",
    			"下記の社員マスタの" + execute + "を行います。よろしいですか？",
    			sb.toString());
    }
    
    /**
     * TableView設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
    @SuppressWarnings("unused")
	private void tableViewSettings() {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： カラム・セル設定/定義");
    	
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// カラム列移動(順番)可否
    	tableListView.setIsReorderabled(false);

    	// 項目(カスタムセル)型設定
    	col_del.setCellTypeCustomCheckBox(false, true);
    	
    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(false);
    	tableListView.onSelectedRowLeaveEvent(
    		    bef -> { return (isEditControlsChanged() && !showMessageEditControlsValue()); },
    		    result -> { this.callbackTableSelectedRow(result); });

    	// TableView[列]文字設定 
    	col_no.getStyleClass().add("center-aligned");
    	col_name.getStyleClass().add("text-aligned");
    	col_auth.getStyleClass().add("center-aligned");
    	
    	// 0件の場合のCaptionを削除(「データがありません」非表示)
    	tableListView.setPlaceholder(new Label(""));
    }    

    /**
     * [TableViewManager] 項目(column-Data)Bind設定(CallBack関数)
     * @brief TableViewのColumnが当該クラス内で定義(Bind)されているため、
     * Columnとデータクラスのプロパティの紐づけを当該メソッドで行う。<br>
     * <p>
     *  [FXML 画面.TableViewのColumn変数].<br>
     *            setCellValueFactory( new PropertyValueFactory<>([データModelのプロパティ名]:String文字列))<br>
     *  入力項目の場合：プロパティをそのまま渡すゲッター<br>
     *  (StringProperty staffNameProperty() { return this.staffName; })がMODELに必要<br>
     *            setCellValueFactory( data -> data.getValue().staffNameProperty());
     */
    private void callbackBindTableColumnSource() {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： カラムBIND設定");

    	col_no.setCellValueFactory( new PropertyValueFactory<>("staffNo"));
    	col_name.setCellValueFactory( new PropertyValueFactory<>("staffName"));
    	col_auth.setCellValueFactory( new PropertyValueFactory<>("authNo"));
    	col_del.setCellValueFactory( new PropertyValueFactory<>("delFlg"));
    }    

    /**
     * TableView 行選択イベント
     */
    private void callbackTableSelectedRow(ApplicationUserModel row) {
    	if (row == null) { return; }
    	
    	LogManager.writeTrace("選択行 no: [ " + row.getStaffNo() + " ]");

       	this.isEditMode = true;
    	
       	// アップキャスト
    	this.editMasterData = new ApplicationUserModel( row );
       	this.editAuthData = new StaffAuthModel();
    	
    	setupEditControls();
    	
    	// ボタン有効化制御(更新状態)
    	setupEditModeButtonsEnabled(isEditMode);
    }    
 
    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： 画面初期化処理");
		
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
 
    	// 登録項目 入力制限　設定
    	this.txt_Edit_No.setValidInput(AppConst.REGEX_NUMERIC, null);
    	
    	this.txt_Edit_Email.setValidInput(AppConst.REGEX_EMAIL_INPUT_LIMIT, null);
    	this.txt_Edit_Password.setValidInput(AppConst.REGEX_PASSWORD, null);
    	
		// 新規登録モード
		setupNewRecordMode();
    }    

    /**
     * コンボボックス:権限　生成処理
     * @brief 登録項目の[権限]を設定する 
     */   
    private void makeEditComboBox_AuthMembers() {
     	// データ取得・設定
     	fetchAuthMembers();
    }   

    /**
     * コンボボックス:権限 データ設定処理
     * @brief 登録項目の[権限]を設定する 
     */   
    private void setupComboBox_EditAuth( List<AuthMasterModel> datas ) 
    {
    	cbo_Edit_Auth.setIsAddBlankRow(true);
    	cbo_Edit_Auth.setDataSource_ModelList( datas, "id", "authName", String.class );

    	// コンボボックスの初期位置(先頭 ≒ 空欄)    	
    	cbo_Edit_Auth.getSelectionModel().selectFirst();
    }    
    
    /**
     * 登録項目設定
     * @brief 編集用 社員マスタMODELを各登録項目の値に設定する。<br>
     */
    private void setupEditControls()
    {   
    	Integer no = this.editMasterData.getStaffNo();
    	String name = this.editMasterData.getStaffName();
    	Boolean del = this.editMasterData.getDelFlg();
    	
    	String noString = "";
    	if ( !AppUtil.IsNull(no) && no > 0 ) { noString = no.toString(); }
    	this.txt_Edit_No.setText( noString );

    	this.txt_Edit_Name.setText( !AppUtil.StringIsNullOrEmpty(name) ? name : "" ); 

    	if (!isEditMode) 
    	{
        	this.cbo_Edit_Auth.setSelectedItem( AppConst.UNSET_NUMBER_VALUE );
    	} 
    	else
    	{
        	this.cbo_Edit_Auth.setSelectedItem( this.editMasterData.getAuthNo() );
    	}
    	
    	this.check_Edit_DelFlg.setSelected( del != null ? del : false );
    	

    	this.editAuthData.setStaffNo(no);
    	// DownCast(更新の場合は、選択明細行より設定する)
    	String email = "";
    	if (this.editMasterData instanceof ApplicationUserModel) {
            email = ((ApplicationUserModel) this.editMasterData).getLoginId();
    	} else {
            email = ""; 
    	}
    	this.txt_Edit_Email.setText(email);
        // 認証マスタMODELに複製
        this.editAuthData.setEmailAddr(email);
    	
    	if (!isEditMode) 
    	{
    		this.txt_Edit_Password.setText("");
    	}
    	else
    	{
    		// 更新の場合、取得値はHASH値の(平文には複合できない)為、マスクを表示する
    		this.txt_Edit_Password.setText(AppConst.PASSWORD_MASK); 
    		this.txt_Edit_Password.setPromptText("変更する場合のみ入力してください");

    		this.editAuthData.setPass(AppConst.PASSWORD_MASK);
    	}
    }

    /**
     * 登録項目 値取得処理
     * @throws Exception 
     * @brief [新規](登録)・[更新](明細行データの更新)に合わせて、各登録項目の値設定を行う<br>
     * パスワードを登録(更新)する場合は、HASH値を設定する
     */
    private void getEditControlsValue() throws Exception {
    	if (!isEditMode) 
    	{
    		Integer no = AppUtil.parseInt( txt_Edit_No.getText(), AppConst.UNSET_NUMBER_VALUE );
    		this.editMasterData.setStaffNo( no );
    	}
    	
    	this.editMasterData.setName( txt_Edit_Name.getText() );
		this.editMasterData.setAuthNo( this.cbo_Edit_Auth.getSelectedKey() );
    	this.editMasterData.setDelFlg( check_Edit_DelFlg.isSelected() );
    	
    	this.editAuthData.setEmailAddr(this.txt_Edit_Email.getText());
    	
    	var inputPassword = this.txt_Edit_Password.getText();
    	// 登録　又は　パスワードを変更(パスワードを入力⇒MASKを変更)した場合
    	if (!isEditMode || !inputPassword.equals(AppConst.PASSWORD_MASK) ) 
    	{
    		// HASH変換した値を格納する
    		this.editAuthData.setPass(new HashConvertManager().convertWordsToSHA256PBKDF2(inputPassword));
    	}
    }    
    
    /**
     * 登録項目 有効化制御
     * @param isEditMode Boolean 更新モード判定
     * @brief [新規](登録)・[更新](明細行データの更新)に合わせて、各ボタンの有効化設定を行う<br>
     */
    private void setupEditModeButtonsEnabled(Boolean isEditMode) {
    	this.txt_Edit_No.setDisable( isEditMode );
    	
       	// ボタン制御
    	this.insert_button.setDisable( isEditMode );	
    	this.update_button.setDisable( !isEditMode );
    }
 
    /**
     * 登録項目 無効化処理
     * @brief 登録項目の全コントロールを無効化を行う<br>
     */
    private void setEditControlsAllDisabled() {
    	this.txt_Edit_No.setDisable( true );	
    	this.txt_Edit_Name.setDisable( true ); 
    	this.cbo_Edit_Auth.setDisable( true ); 
    	this.check_Edit_DelFlg.setDisable( true );
    	this.txt_Edit_Email.setDisable( true );
    	this.txt_Edit_Password.setDisable( true );
    	
       	// ボタン制御
    	this.new_button.setDisable( true );
    	this.insert_button.setDisable( true );	
    	this.update_button.setDisable( true ); 	
    }        
    
    /**
     * 登録項目 新規モード設定
     */
    private void setupNewRecordMode()
    {
		this.isEditMode = false;

    	// マスター登録用データ初期化
		this.editMasterData = new StaffMasterModel();
		this.editAuthData = new StaffAuthModel(); 
		
    	setupEditControls();
    	
    	// ボタン有効化制御(新規状態)
    	setupEditModeButtonsEnabled( isEditMode );
    	
    	Platform.runLater(() -> txt_Edit_No.requestFocus());
    }

    /**
     * 入力エラー項目 Focus指定処理
     * @param colNo 対象Focus番号
     * @brief 登録項目でエラーがあった場合、登録前チェック処理:isMasterRowsCheckで<br>
     * 設定した項目番号(列番号)に一致するControlにFocusを遷移する。
     */
    private void setupErrorInputControlsFocus(Integer colNo)
    {
    	Platform.runLater(() -> 
    	{ 
    		switch (colNo)
    		{
    		 case 1:
    	    	this.txt_Edit_No.requestFocus();
    	        break;
    	     case 2:
    	    	this.txt_Edit_Name.requestFocus();
    	        break;
    	     case 3:
    	    	 this.cbo_Edit_Auth.requestFocus();
    	        break;
    	     case 4:
    	    	 this.txt_Edit_Email.requestFocus();
    	        break;
    	     case 5:
    	    	 this.txt_Edit_Password.requestFocus();
    	        break;
    	     default:
    	        break;
    	    }
    	});
    }    
    
    /**
     * 登録前チェック処理
     * @return AppConst.rowCheckResultData チェック結果
     * @brief 入力項目チェック処理(必須・重複確認)<br>
     */
    private AppConst.rowCheckResultData isMasterRowsCheck() throws Exception {
		StringBuilder sb = new StringBuilder();    	
    	
		// 必須確認：社員番号
		if ( this.editMasterData.getStaffNo() < 1 ) {
			sb.append("[社員番号]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
		}	
    	
		// 必須確認：氏名
		if ( AppUtil.StringIsNullOrWhiteSpace(this.editMasterData.getStaffName()) ) {
			sb.append("[氏名]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 2, sb.toString());
		}	

		// 必須確認：権限
		if ( this.editMasterData.getAuthNo() < 1 ) {
			sb.append("[権限]が選択されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 3, sb.toString());
		}
		
		// 必須確認：Email(LogInID)
		if ( AppUtil.StringIsNullOrWhiteSpace(this.editAuthData.getEmailAddr()) ) {
			sb.append("[メールアドレス(ログインID)]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 4, sb.toString());
		}	
		
		// 必須確認：Password(登録/更新含む)
		if ( AppUtil.StringIsNullOrWhiteSpace(this.editAuthData.getPass()) ) {
			sb.append("[パスワード]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 5, sb.toString());
		}	
		
		// メールアドレス形式確認
	    if (!emailPattern.matcher(this.editAuthData.getEmailAddr()).matches()) {
			sb.append("メールアドレス(ログインID)の形式が正しくありません。");
    		sb.append(AppUtil.newLine());
    		sb.append("下記の形式が許可されています。");
    		sb.append(AppUtil.newLine());
    		sb.append("ローカル部：半角英数字(大文字含む)、");
    		sb.append(AppUtil.newLine());
    		sb.append("            記号(. ! # \\$ % & ' * + - / = ? ^ _  { | } ~`)、");
    		sb.append(AppUtil.newLine());
    		sb.append("            ドット（.）の連続禁止、");
    		sb.append(AppUtil.newLine());
    		sb.append("            ドット（.）の配置場所を制限(先頭及び＠前は禁止)");
    		sb.append(AppUtil.newLine());
    		sb.append("ドメイン部：角英数字(大文字含む)、");
    		sb.append(AppUtil.newLine());
    		sb.append("            記号(. -)、");
    		sb.append(AppUtil.newLine());
    		sb.append("            (-)の連続禁止、");
    		sb.append(AppUtil.newLine());
    		sb.append("            (-）の配置場所を制限(各ドメインの先頭及び末尾は禁止)");
    		sb.append(AppUtil.newLine());	
    		sb.append("            一番右端（トップレベルドメイン）は2文字以上の英字のみ");
    		sb.append(AppUtil.newLine());	  		
    		return new AppConst.rowCheckResultData(false, true, -1, 4, sb.toString());
	    }
		
		// 存在確認
		if( !isEditMode && super.executeNonQuery( this::isExistsMasterData, this.editMasterData)) {
			sb.append("既に同じ社員番号が登録されています。");
    		sb.append(AppUtil.newLine());
    		sb.append("同一の社員番号は登録できません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
		}
		
    	// メールアドレス重複確認
		if( super.executeNonQuery( this::isExistsMailAddress, this.editAuthData)) {
			sb.append("既に同じメールアドレス(ログインID)が登録されています。");
    		sb.append(AppUtil.newLine());
    		sb.append("同一のメールアドレスは登録できません。");
			return new AppConst.rowCheckResultData(false, true, -1, 4, sb.toString());
		}
		
    	return new AppConst.rowCheckResultData(
    			true, 
    			false, 
    			AppConst.UNSET_NUMBER_VALUE,
    			AppConst.UNSET_NUMBER_VALUE,
    			"") ;
    }    
    
    /**
     * 遷移元画面呼び出し
     * @brief 遷移元画面(メニュー：Menu)を呼出す。<br>
     */
    private void showOwnerPage() throws Exception {
    	// 遷移元画面に切替
    	super.setPage(new application.
    			java.
    			window.
    			MenuController(true));
    }   
}

