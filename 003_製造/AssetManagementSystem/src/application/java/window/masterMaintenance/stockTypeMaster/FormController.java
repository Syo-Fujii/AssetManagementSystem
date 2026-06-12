package application.java.window.masterMaintenance.stockTypeMaster;

import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.StockTypeMasterModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableViewManager;
import application.java.manager.customControl.CustomTextFieldControlManager;
import application.resources.mapper.StockTypeMasterMapper;
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
 * 備品分類マスタ メンテナンス画面
 * @brief [StockTypeMasterMaintenance]画面操作メソッド(Controller)<br>
 * <p>
 * TableViewを継承した[TableViewManager](カスタムControl)を用いる場合、
 * 画目デザイン(Screen Builder)では正しく操作できない。<br>
 * ⇒ Screen Builderでは、カスタムControlはブラックボックス化されTableViewの操作(変更や項目追加など)が行えない。<br>
 * なので画面レイアウトを変更・操作(ableViewManagerの配置・変更など)する場合は、<br>
 * 手動で、Source上の[TableViewManager]を[TableView]に書き換えてScreen Builderを起動・デザインの変更を行う。<br>
 * デザインを変更・確定後にControlを[TableViewManager]の戻すことで編集を行う。<br>
 * ※ Screen Builderでは、カスタムControlの継承元に関する各機能は実行できない。<br>
 * ※ [TableViewManager]を用いても、Build・動作は正常におこなわれる。
 */
public class FormController extends BaseFormPage {
	private final String FORM_NAME = "備品分類マスタ メンテナンス";
	
	private StockTypeMasterModel editMasterData = null;
	private Boolean isEditMode = false;
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	
	@FXML private TableViewManager<StockTypeMasterModel> tableListView;
	@FXML private TableColumn<StockTypeMasterModel, Integer> col_type;
	@FXML private TableColumn<StockTypeMasterModel, String> col_code;
	@FXML private TableColumn<StockTypeMasterModel, String> col_name;
	@FXML private TableColumnManager<StockTypeMasterModel, Boolean> col_del;
	
	@FXML private Button new_button;
	
	@FXML private CustomTextFieldControlManager txt_Edit_Type;
	@FXML private CustomTextFieldControlManager txt_Edit_Code;
	@FXML private TextField txt_Edit_Name;
	@FXML private CheckBox check_Edit_DelFlg;
	
	@FXML private Button insert_button;
	@FXML private Button update_button;
	@FXML private Button back_button;

	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理");
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("StockTypeMasterMaintenance"));
		this.setCssFile(AppUtil.MakeCssFilePath("StockTypeMasterStyle"));
		
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
		
		// 画面起動設定
		this.formInitialize();

		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト表示処理");
    	super.<StockTypeMasterModel>fillTableAsync();
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
    		setEditControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： [新規]ボタン押下にて、エラーが発生しました";
    		LogManager.showAndWriteError(title, e);
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
        			super.showMessageInputError(checkResult.message());
        		}
        		setupErrorInputControlsFocus( checkResult.colNo() );
        		return;
        	}
        	
        	// 登録実行確認
        	if( !showMessageIsExecuting( "登録", this.editMasterData.getDelFlg()) ) { return; }
    		
        	// 登録処理(備品分類マスタ登録処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 備品分類マスタ登録処理");
        	super.executeCudQuery( List.of(editMasterData) );  		   		

    		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<StockTypeMasterModel>fillTableAsync(); 		

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
    		
        	// 更新処理(備品分類マスタ更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 備品分類マスタ更新処理");
        	super.executeCudQuery( List.of(editMasterData) );  		   		
    		
    		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<StockTypeMasterModel>fillTableAsync(); 		

    		// 新規登録モード
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
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品分類マスタ取得処理");
    	
    	StockTypeMasterMapper mapper = session.getMapper(StockTypeMasterMapper.class);
	    
	    // 備品分類マスタ データ取得
	    return (List<T>) mapper.selectMaintenance();
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品分類マスタ連携(BIND)処理");
    		
    		// データ設定(BIND・SELL設定値)を初期化
    		tableListView.dataSourceClear();        	
    		List<StockTypeMasterModel> rows = (List<StockTypeMasterModel>) listData;
    		tableListView.setList( rows );
    	
    	} catch ( Exception e) {
    		setEditControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： 備品分類マスタの連携中にエラーが発生しました";
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
		setEditControlsAllDisabled();
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品分類マスタ 操作失敗");
		super.exceptionResult(exception);
    }

    /**
     * 備品分類マスタ登録・更新クエリ発行
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
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品分類マスタ" + ExecuteTitle + "処理");
		
		StockTypeMasterMapper mapper = session.getMapper(StockTypeMasterMapper.class);

		Integer resultCount = AppConst.UNSET_NUMBER_VALUE;
		StockTypeMasterModel data = (StockTypeMasterModel) listData.getFirst();
		
		if (this.isEditMode)
		{
			// 更新処理
			resultCount = mapper.updStockTypeMasterOnes( data );
		} else {
			// 登録処理
			resultCount = mapper.insStockTypeMasterOnes( data );
		}

		return ( resultCount == AppConst.DB_EXECUTE_ONES ); 
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
    		setEditControlsAllDisabled();
			
			String title = "DB クエリ発行結果エラー";
			
			StringBuilder sb = new StringBuilder();
			sb.append(this.isEditMode ? "更新" : "登録");
			sb.append("(備品分類マスタ)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
			
			super.showMessageException(title, sb.toString());
		}
	}	

	/**
	 * 備品分類マスタ存在確認用クエリ発行処理(Mapper)
	 * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
	 * @param session 継承元より渡される[SQLSession]
	 * @param data 発行するクエリの条件の値( 行データのクラス )
	 * @return 存在確認の結果
	 */
	private <T extends BaseTableViewModel> Boolean isExistsMasterData(SqlSession session, T data) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品分類マスタ存在確認");
		
		StockTypeMasterMapper mapper = session.getMapper(StockTypeMasterMapper.class);
		
		// 存在確認
		return  mapper.existsStockType( (StockTypeMasterModel) data );
    }

	/**
	 * 登録項目 値変更チェック処理
	 * @return boolean 判定結果
     * @brief 登録用の各Controlの値が、初期と違う(値を編集した)場合は[真]<br>
	 */
	private boolean isEditControlsChanged() {
    	Integer type = this.editMasterData.getStockType();
    	String code = this.editMasterData.getStockCode();
    	String name = this.editMasterData.getStockTypeName();
    	Boolean del = this.editMasterData.getDelFlg();
    	
    	String typeString = "";
    	if ( !AppUtil.IsNull(type) && type > 0 ) { typeString = type.toString(); }
		
		if ( !Objects.equals(txt_Edit_Type.getText(), typeString) ) { return true; }
		if ( !Objects.equals(txt_Edit_Code.getText(), code) ) { return true; }
		if ( !Objects.equals(txt_Edit_Name.getText(), name) ) { return true; }	
		
		Boolean rowDataDelFlg = del != null ? del : false;
		if ( !Objects.equals(check_Edit_DelFlg.isSelected(), rowDataDelFlg) ) { return true; }	

		return false;
	}

	/**
	 *DB処理確認Message
	 * @return 確認結果
	 */
    private Boolean showMessageIsExecuting(String execute, boolean delFlg) {
    	Integer type = this.editMasterData.getStockType();
    	String code = this.editMasterData.getStockCode();

    	StringBuilder sb = new StringBuilder();
		
    	if ( delFlg ) 
    	{
    		sb.append("※ 削除フラグを有効にした場合、貸出業務の対象外となります。");
    		sb.append(AppUtil.newLine());
    		sb.append("   現在貸出中の備品も対象外となります。よろしいですか？");
    		sb.append(AppUtil.newLine());
    		sb.append(AppUtil.newLine());
    	}
    	
    	sb.append("備品分類 :[").append(type.toString()).append("] ");
		sb.append("備品コード :[").append(code).append("] ");
		sb.append(AppUtil.newLine());

    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			false,
    			execute + "確認",
    			"下記の備品分類マスタの" + execute + "を行います。よろしいですか？",
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
    	col_type.getStyleClass().add("center-aligned");
    	col_code.getStyleClass().add("center-aligned");
    	col_name.getStyleClass().add("text-aligned");
    	
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

    	col_type.setCellValueFactory( new PropertyValueFactory<>("stockType"));
    	col_code.setCellValueFactory( new PropertyValueFactory<>("stockCode"));
    	col_name.setCellValueFactory( new PropertyValueFactory<>("stockTypeName"));
    	col_del.setCellValueFactory( new PropertyValueFactory<>("delFlg"));
    }    

    /**
     * TableView 行選択イベント
     */
    private void callbackTableSelectedRow(StockTypeMasterModel row) {
    	if (row == null) { return; }
    	
    	LogManager.writeTrace("選択行 id: [ " + row.getStockTypeId() + " ]");

       	this.isEditMode = true;
    	
    	this.editMasterData = new StockTypeMasterModel( row );
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
    	this.txt_Edit_Type.setValidInput(AppConst.REGEX_NUMERIC, null);
    	this.txt_Edit_Code.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, 4);
    	
		// 新規登録モード
		setupNewRecordMode();
    }    
    
    /**
     * 登録項目設定
     * @brief 編集用 備品分類マスタMODELを各登録項目の値に設定する。<br>
     */
    private void setupEditControls()
    {   
    	Integer type = this.editMasterData.getStockType();
    	String code = this.editMasterData.getStockCode();
    	String name = this.editMasterData.getStockTypeName();
    	Boolean del = this.editMasterData.getDelFlg();
    	
    	String typeString = "";
    	if ( !AppUtil.IsNull(type) && type > 0 ) { typeString = type.toString(); }

    	txt_Edit_Type.setText( typeString );
    	txt_Edit_Code.setText( !AppUtil.StringIsNullOrEmpty( code) ? code : "" ); 
    	txt_Edit_Name.setText( !AppUtil.StringIsNullOrEmpty(name) ? name : "" ); 
    	check_Edit_DelFlg.setSelected( del != null ? del : false );
    }       

    /**
     * 登録項目 値取得処理
     * @brief [新規](登録)・[更新](明細行データの更新)に合わせて、各登録項目の値設定を行う<br>
     */
    private void getEditControlsValue() {
    	
    	if (!isEditMode) 
    	{
    		Integer type = AppUtil.parseInt( txt_Edit_Type.getText(), AppConst.UNSET_NUMBER_VALUE );
    		this.editMasterData.setStockType( type );
    		this.editMasterData.setStockCode( txt_Edit_Code.getText() );
    	}
    	
    	this.editMasterData.setStockTypeName( txt_Edit_Name.getText() );
    	this.editMasterData.setDelFlg( check_Edit_DelFlg.isSelected() );
    }    
    
    /**
     * 登録項目 有効化制御
     * @param isEditMode Boolean 更新モード判定
     * @brief [新規](登録)・[更新](明細行データの更新)に合わせて、各ボタンの有効化設定を行う<br>
     */
    private void setupEditModeButtonsEnabled(Boolean isEditMode) {
    	txt_Edit_Type.setDisable( isEditMode );
    	txt_Edit_Code.setDisable( isEditMode );
    	
       	// ボタン制御
    	insert_button.setDisable( isEditMode );	
    	update_button.setDisable( !isEditMode );
    }
 
    /**
     * 登録項目 無効化処理
     * @brief 登録項目の全コントロールを無効化を行う<br>
     */
    private void setEditControlsAllDisabled() {
    	txt_Edit_Type.setDisable( true );	
    	txt_Edit_Code.setDisable( true ); 
    	txt_Edit_Name.setDisable( true ); 
    	check_Edit_DelFlg.setDisable( true );
    	
       	// ボタン制御
    	new_button.setDisable( true );
    	insert_button.setDisable( true );	
    	update_button.setDisable( true ); 	
    }        
    
    /**
     * 登録項目 新規モード設定
     */
    private void setupNewRecordMode()
    {
		this.isEditMode = false; 

    	// マスター登録用データ初期化
		this.editMasterData = new StockTypeMasterModel();
    	setupEditControls();
    	
    	// ボタン有効化制御(新規状態)
    	setupEditModeButtonsEnabled( isEditMode );
    	
    	Platform.runLater(() -> txt_Edit_Type.requestFocus());
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
    	    	txt_Edit_Type.requestFocus();
    	        break;
    	     case 2:
    	    	txt_Edit_Code.requestFocus();
    	        break;
    	     default:
    	        break;
    	    }
    	});
    }    
    
    /**
     * 登録前チェック処理
     * @return AppConst.rowCheckResultData チェック結果
     */
    private AppConst.rowCheckResultData isMasterRowsCheck() throws Exception {
		StringBuilder sb = new StringBuilder();    	
    	
		// 必須確認：分類種別
		if ( this.editMasterData.getStockType() < 1 ) {
			sb.append("[分類種別]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
		}	
    	
		// 必須確認：備品コード
		if ( AppUtil.StringIsNullOrWhiteSpace(this.editMasterData.getStockCode()) ) {
			sb.append("[分類コード]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 2, sb.toString());
		}	
    	
    	// 存在確認
		if( super.executeNonQuery( this::isExistsMasterData, this.editMasterData)) {
			sb.append("既に同じ備品分類が登録されています。");
    		sb.append(AppUtil.newLine());
    		sb.append("同一の備品分類は登録できません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
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
