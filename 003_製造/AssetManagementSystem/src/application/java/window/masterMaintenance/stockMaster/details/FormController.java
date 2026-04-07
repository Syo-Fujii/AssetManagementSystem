package application.java.window.masterMaintenance.stockMaster.details;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.StockMasterModel;
import application.java.base.tableViewListModel.StockMasterDataModel;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableViewManager;
import application.java.manager.customControl.CustomComboBoxControlManager;
import application.java.manager.customControl.CustomTextFieldControlManager;
import application.resources.mapper.StockMasterMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * 備品マスタ 明細(一覧)画面
 * @brief [StockMasterMaintenanceDetalis]画面操作メソッド(Controller)<br>
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

	private final String FORM_NAME = "備品マスタ メンテナンス 明細(一覧)";
	
	private StockMasterModel editMasterData = null;
	private Boolean isEditMode = false;
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	
	@FXML private CustomComboBoxControlManager<String> cbo_search_type;
	@FXML private CustomTextFieldControlManager txt_search_serial;
	@FXML private CustomTextFieldControlManager txt_search_name;
	@FXML private CustomTextFieldControlManager txt_search_model;
	@FXML private CustomComboBoxControlManager<String> cbo_search_Status;
	@FXML private CheckBox chb_search_del;
	
	@FXML private Button search_button;
	
	
	@FXML private TableViewManager<StockMasterDataModel> tableListView;
	@FXML private TableColumn<StockMasterDataModel, String> col_serial;
	@FXML private TableColumn<StockMasterDataModel, String> col_type_name;
	@FXML private TableColumn<StockMasterDataModel, String> col_name;
	@FXML private TableColumn<StockMasterDataModel, String> col_model;	
	@FXML private TableColumn<StockMasterDataModel, String> col_maker;
	@FXML private TableColumn<StockMasterDataModel, String> col_asset;
	@FXML private TableColumn<StockMasterDataModel, String> col_type;
	@FXML private TableColumn<StockMasterDataModel, String> col_remarks;
	@FXML private TableColumn<StockMasterDataModel, String> col_rent;
	@FXML private TableColumnManager<StockMasterDataModel, Boolean> col_del;

	
	@FXML private Button insert_button;
	@FXML private Button copy_insert_button;
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

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("StockMasterMaintenanceDetalis"));
		this.setCssFile(AppUtil.MakeCssFilePath("StockMasterMaintenanceDetalisStyle"));
		
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
    	super.<StockMasterDataModel>fillTableAsync();
    }	
	
	

    /**
     * [検索]ボタン 押下イベント
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onSearchButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [検索]ボタン押下");

    		// 検索条件初期化
    		/*clearSearchValues();
    		
    		this.searchStockType = this.cbo_Search_Type.getSelectedKey();
    		this.searchSerialNo = Optional.ofNullable(this.txt_Search_Serial.getText()).orElse(""); 
    		this.searchRentStatus = this.cbo_Search_Status.getSelectedKey();
    		this.searchStaffNo = this.cbo_Search_Staff.getSelectedKey();
    		this.searchLimitDate = this.dp_Search_Limit.getValue();
    		this.searchConfirmedDate = this.dp_Search_Confirmed.getValue();
    		
    		testSarchContrlSelectedValues();
    		
    		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト検索表示処理");
        	super.<PerformInventoryDataModel>fillTableAsync();*/

    	} catch (Exception ex) {
    		// 棚卸ボタン無効化
    		/*inventory_button.setDisable(true);*/
    		
    		String title = "[" + FORM_NAME + "] ： [検索]ボタン押下でエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
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
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品マスタ明細(一覧)取得処理");
    	
    	StockMasterMapper mapper = session.getMapper(StockMasterMapper.class);
	    
	    // 備品マスタ明細(一覧) データ取得
	    return (List<T>) mapper.getTableStockMasterDetailRecords(null,null,null,null,null,null);
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品マスタ明細(一覧)連携(BIND)処理");
    		
    		// データ設定(BIND・SELL設定値)を初期化
    		tableListView.dataSourceClear();        	
    		List<StockMasterDataModel> rows = (List<StockMasterDataModel>) listData;
    		tableListView.setList( rows );
    	
    	} catch ( Exception e) {
    		setButtonControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： 備品マスタ明細(一覧)の連携中にエラーが発生しました";
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
		setButtonControlsAllDisabled();
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品マスタ明細(一覧)取得失敗");
		super.exceptionResult(exception);
    }    
    
    /**
     * TableView設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
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
    		    result -> { this.callbackTableSelectedRow(result); });
    	tableListView.onTableViewFocusEvent(
    			() -> 
    			{
                   	this.isEditMode = false;
                   	setupEditModeButtonsEnabled(isEditMode);  				
    			},
    			null);
    	
    	// TableView[列]文字設定 
    	col_serial.getStyleClass().add("text-aligned");
    	col_type_name.getStyleClass().add("text-aligned");
    	col_name.getStyleClass().add("text-aligned");
    	col_model.getStyleClass().add("text-aligned");	
    	col_maker.getStyleClass().add("text-aligned");
    	col_asset.getStyleClass().add("center-aligned");
    	col_type.getStyleClass().add("center-aligned");
    	col_remarks.getStyleClass().add("text-aligned");
    	col_rent.getStyleClass().add("center-aligned");
    	
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

    	col_serial.setCellValueFactory( new PropertyValueFactory<>("serialNo"));
    	col_type_name.setCellValueFactory( new PropertyValueFactory<>("typeName"));
    	col_name.setCellValueFactory( new PropertyValueFactory<>("stockName"));
    	col_model.setCellValueFactory( new PropertyValueFactory<>("model"));	
    	col_maker.setCellValueFactory( new PropertyValueFactory<>("maker"));
    	col_asset.setCellValueFactory( new PropertyValueFactory<>("assetStatus"));
    	col_type.setCellValueFactory( new PropertyValueFactory<>("paymentStatus"));
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));
    	col_rent.setCellValueFactory( new PropertyValueFactory<>("rentStatus"));
    	col_del.setCellValueFactory( new PropertyValueFactory<>("delFlg"));	
    }    

    /**
     * TableView 行選択イベント
     */
    private void callbackTableSelectedRow(StockMasterDataModel row) {
    	if (row == null) 
    	{ 
    		// 行選択をやめた場合
           	this.isEditMode = false;
           	setupEditModeButtonsEnabled(isEditMode);
    		return; 
    	}
    	
    	LogManager.writeTrace("選択行 シリアルNo: [ " + row.getSerialNo() + " ]");

       	this.isEditMode = true;
    	this.editMasterData = row.extractStockMasterModel();
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
    	this.txt_search_serial.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, 20);
    	this.txt_search_model.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, null);
    	
		// 新規登録モード
		setupNewRecordMode();
    }        

    /**
     * 各ボタン 有効化制御
     * @param isEditMode Boolean 更新モード判定
     * @brief 明細行の選択・非選択に合わせて、各ボタンの有効化設定を行う<br>
     */
    private void setupEditModeButtonsEnabled(Boolean isEditMode) {
       	// ボタン制御
     	copy_insert_button.setDisable( !isEditMode );
    	update_button.setDisable( !isEditMode );
    }    


    /**
     * ボタン項目 無効化処理
     * @brief ボタンの全コントロールを無効化を行う<br>
     */
    private void setButtonControlsAllDisabled() {
     	
       	// ボタン制御
    	insert_button.setDisable( true );
    	copy_insert_button.setDisable( true );	
    	update_button.setDisable( true ); 	
    }         
    
    /**
     * 新規モード設定
     */
    private void setupNewRecordMode()
    {
		this.isEditMode = false; 

    	// 登録用データ初期化
		this.editMasterData = new StockMasterDataModel();
    	
    	// ボタン有効化制御(新規状態)
    	setupEditModeButtonsEnabled( isEditMode );
    	
    	Platform.runLater(() -> insert_button.requestFocus());
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
