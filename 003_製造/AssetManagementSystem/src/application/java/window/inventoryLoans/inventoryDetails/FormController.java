package application.java.window.inventoryLoans.inventoryDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.InventoryDetailsDataModel;
import application.java.common.AppConst;
import application.java.common.AppConst.PermissionMask;
import application.java.common.AppSession;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryDetailsMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * 備品詳細画面
 * @brief [inventoryDetails]画面操作メソッド(Controller)<br>
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
	
	private final String FORM_NAME = "備品詳細画面";
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_LoginUser;
	@FXML private Label lbl_title;
	@FXML private Label lbl_stock_name;
	
	@FXML private TableViewManager<InventoryDetailsDataModel> tableListView;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_serial;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_staff_name;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_rent_flg;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_start_date;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_limit_date;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_confirmed_date;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_model;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_maker;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_destination_serial_no;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_type;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_lease_date;
	@FXML private TableColumn<InventoryDetailsDataModel, String> col_remarks;
	
	@FXML private Button inventoryCounting_button;
	@FXML private Button loan_button;
	@FXML private Button return_button;
	@FXML private Button back_button;
	
	private Integer stockType = 0; 
	private String stockCode = "";
	private Integer windowSizeType = 1;

	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryDetails"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryDetailsStyle"));
		
		this.setPageTitle(FORM_NAME);
	}
	public FormController(Integer type, String code, String windowSize) 
	{
		this();

		this.stockType = type;
		this.stockCode = code;
		this.windowSizeType = AppUtil.parseInt(windowSize, 1);
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
		
    	// ログインユーザの権限による制御
    	setUserPermissionControls( AppSession.getUserPermission() );    	

		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト表示処理");
    	super.<InventoryDetailsDataModel>fillTableAsync();
    }
     
     /**
     * [所在確認]ボタン 押下イベント
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onCountingButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [所在確認]ボタン押下"); 
    		
    		// 選択行取得
        	InventoryDetailsDataModel row = tableListView.
        			getSelectionModel().
        			getSelectedItem();

        	if (row == null) {
        	    // なにもしない
        	    return;
        	} 

        	String serialNo = row.getSerialNo();
        	Integer dataId = row.getStockDataId();
        	
        	if (AppUtil.IsNull(dataId)) {
        		showMessageEmptyStockData(serialNo);
        		return;
        	}
        	
        	LogManager.writeInfo(
        			"最終所在確認日更新 シリアル番号: [" + serialNo + "] " +
        			"備品データ ID: ["+ dataId.toString() + "]"); 
       	
        	if (showMessageUpdConfimedDate(serialNo)) {
        	    
        		LogManager.writeDebug("[" + FORM_NAME + "] ： 最終所在確認日更新処理");
        		
        		// 更新値設定
        		this.setUpdModelData(row);
        		super.<InventoryDetailsDataModel>executeCudQuery(new ArrayList<>(List.of(row)));

        		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
             	super.<InventoryDetailsDataModel>fillTableAsync();
        		
        	} else {
        	    // 「キャンセル」や「×」が押された時の処理
            	LogManager.writeDebug("[" + FORM_NAME + "] ： 処理がキャンセルされました。");
        	} 
    		
    	} catch (Exception ex) {
    		setButtonControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： [所在確認]ボタン押下でエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
   }
 
    /**
     * [貸出]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onLoanButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [貸出]ボタン押下"); 
    		
        	// 備品貸出画面に切替
        	super.setPage(new application.
        			java.
        			window.
        			inventoryLoans.
        			inventoryLoan.
        			FormController(this.stockType, this.stockCode, this.windowSizeType));
    		
    	} catch (Exception ex) {
    		setButtonControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： 貸出画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }    
  
    /**
     * [返却]ボタン 押下イベント
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onReturnButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [返却]ボタン押下"); 
    		
        	// 備品返却画面に切替
        	super.setPage(new application.
        			java.
        			window.
        			inventoryLoans.
        			inventoryReturn.
        			FormController(this.stockType, this.stockCode, this.windowSizeType));		
    	
    	} catch ( Exception e) {
    		setButtonControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： 返却画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
    }        
    
    /**
     * [一覧に戻る]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onBackButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [一覧に戻る]ボタン押下"); 
    		
    		// 遷移元画面に切替
    		this.showOwnerPage();
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 一覧画面遷移中にエラーが発生しました";
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
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品詳細データ取得処理");
    	
    	var staffNo = Objects.equals(AppSession.getLoginAuthId(), AppConst.STOCK_USER_AUTH) ? AppSession.getLoginStaffCode() : 0; 
    	
    	InventoryDetailsMapper mapper = session.getMapper(InventoryDetailsMapper.class);
	    // 備品詳細データ取得
	    return (List<T>) mapper.getTableDetailRecords(this.stockType, this.stockCode, staffNo);
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品詳細データ連携(BIND)処理");
    		
    		// データ設定(BIND・SELL設定値)を初期化
    		tableListView.dataSourceClear();        	
    		
    		List<InventoryDetailsDataModel> rows = (List<InventoryDetailsDataModel>) listData;
        	
        	tableListView.setList( rows );

        	// 備品分類の表示
    		if (listData != null && !rows.isEmpty()) {
    			lbl_stock_name.setText(rows.getFirst().getTypeName());
    		}
    		
    		// [貸出]ボタン有効化制御([可]が一つでも存在する場合有効)
    		loan_button.setDisable(!rows.stream().
    				anyMatch(r -> r.getRentValue() == AppConst.LoanStatus.AVAILABLE.getState()));

    		// [返却]ボタン有効化制御([貸出中]が一つでも存在する場合有効)
    		return_button.setDisable(!rows.stream().
    				anyMatch(r -> r.getRentValue() == AppConst.LoanStatus.CHECKED_OUT.getState() ||
    						      r.getRentValue() == AppConst.LoanStatus.CHECKOUT_AND_UNKNOWN.getState()));	
    	
    	} catch ( Exception e) {
    		setButtonControlsAllDisabled();
    		String title = "[" + FORM_NAME + "] ： 備品詳細データの連携中にエラーが発生しました";
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
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品詳細データ取得失敗");
		super.exceptionResult(exception);
    }

    /**
     * 備品データ更新クエリ発行(最終所在確認日更新処理)
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
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品データ更新(confirmedDate更新)");
		
		if (listData == null || listData.isEmpty()) { return false; }
		
		InventoryDetailsDataModel row = (InventoryDetailsDataModel)listData.getFirst();
		InventoryDetailsMapper mapper = session.getMapper(InventoryDetailsMapper.class);
	    
	    // 最終所在確認日更新処理
	    Integer updCount =  mapper.updConfirmedDate(row);

	    return true;
    }	
	
	/**
	 *所在確認日 更新確認Message
	 * @param serialNo String 対象シリアルNO
	 * @return 確認結果
	 */
    private Boolean showMessageUpdConfimedDate(String serialNo) {
    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			false,
    			"確認",
    			null,
    			"備品[シリアルNo: "+ serialNo + " ]の所在確認日を本日に更新します。" + AppUtil.newLine() +
    			"よろしいですか？");
    }
	
    /**
     * 警告Message：備品データなし
     * @param serialNo String 対象シリアルNO
     */
    private void showMessageEmptyStockData(String serialNo) {
    	String content = "備品[シリアルNo: "+ serialNo + " ]の備品データ(stock_data)が存在しません。";
    	
    	LogManager.writeWarnig("データ異常：データ不整合 システム管理者に連絡してください。");
    	LogManager.writeWarnig(content);
    	
    	MessageBox.ShowWarnig(
    			"警告",
    			"データ異常：データ不整合 システム管理者に連絡してください。",
    			content);
    }
    
    /**
     * TableView設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
	private void tableViewSettings()
    {
    	System.out.println("備品詳細 カラム・セル設定/定義");
    	
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// カラム列移動(順番)可否
    	tableListView.setIsReorderabled(false);
    	
    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(false);
    	tableListView.onSelectedRowLeaveEvent(result -> {
    	    this.callbackTableSelectedRow((InventoryDetailsDataModel) result);
    	});

    	// 標準サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.NOMAL.getId()) {
        	col_model.setVisible(false);
        	col_maker.setVisible(false);
    		col_destination_serial_no.setVisible(false);
        	col_type.setVisible(false);
        	col_lease_date.setVisible(false);
    	}
    	
    	// 拡大サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.WIDE.getId()) {
    		col_destination_serial_no.setVisible(false);
    	}
    	
    	// 周辺機器サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.PERIPHERAL.getId()) {
        	col_model.setVisible(false);
        	col_maker.setVisible(false);
    	}    	
    	
    	// TableView[列]文字設定 
    	col_staff_name.getStyleClass().add("center-aligned");
    	col_rent_flg.getStyleClass().add("center-aligned");
    	col_start_date.getStyleClass().add("center-aligned");
    	col_limit_date.getStyleClass().add("center-aligned"); 
    	col_confirmed_date.getStyleClass().add("center-aligned"); 
    	col_model.getStyleClass().add("center-aligned");
    	col_maker.getStyleClass().add("center-aligned");
    	col_type.getStyleClass().add("center-aligned");
    	col_lease_date.getStyleClass().add("center-aligned");
    	
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
    	col_staff_name.setCellValueFactory( new PropertyValueFactory<>("staffName"));
    	col_rent_flg.setCellValueFactory( new PropertyValueFactory<>("rentFlg"));
    	col_start_date.setCellValueFactory( new PropertyValueFactory<>("startDate"));
    	col_limit_date.setCellValueFactory( new PropertyValueFactory<>("limitDate"));
    	col_confirmed_date.setCellValueFactory( new PropertyValueFactory<>("confirmedDate"));
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));
    	
    	// 標準サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.NOMAL.getId()) {
    		return;
    	}
    	
    	col_type.setCellValueFactory( new PropertyValueFactory<>("type"));
    	col_lease_date.setCellValueFactory( new PropertyValueFactory<>("leaseDate"));
    	
    	// 拡大サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.WIDE.getId()) {
        	col_model.setCellValueFactory( new PropertyValueFactory<>("model"));
        	col_maker.setCellValueFactory( new PropertyValueFactory<>("maker"));
    	}
    	
    	// 周辺機器サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.PERIPHERAL.getId()) {
    		col_destination_serial_no.setCellValueFactory( new PropertyValueFactory<>("destinationSerialNo"));
    	}
    }
    
    /**
     * TableView 行選択イベント
     */
    private void callbackTableSelectedRow(InventoryDetailsDataModel row) {
    	LogManager.writeTrace("選択行 シリアルNo: [ " + row.getSerialNo() + " ]");
    }

    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： 画面初期化処理");
    	
    	lbl_LoginUser.setText(AppSession.getLoginUserInfo());
    	
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
    	
    	// 貸出ボタン初期化(無効)
    	loan_button.setDisable(true);
    	
    	// 標準サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.NOMAL.getId()) {
    		tableListView.setPrefWidth(860);
    		pane_form.setPrefWidth(910);
    		lbl_stock_name.setPrefWidth(910);
    		
    		lbl_LoginUser.setPrefWidth(pane_form.getPrefWidth());
    		return;
    	}
    	
    	// 拡大サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.WIDE.getId()) {
    		tableListView.setPrefWidth(1350);
    		pane_form.setPrefWidth(1400);
    		lbl_stock_name.setPrefWidth(1400);
    		
    		lbl_LoginUser.setPrefWidth(pane_form.getPrefWidth());
    		return;
    	}
    	
    	// 周辺機器サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.PERIPHERAL.getId()) {
    		tableListView.setPrefWidth(1200);
    		pane_form.setPrefWidth(1250);
    		lbl_stock_name.setPrefWidth(1250);
    		
    		lbl_LoginUser.setPrefWidth(pane_form.getPrefWidth());
    		return;
    	}
    }
    
    /**
     * 更新データ値設定(最終所在確認日)
     * @param row InventoryDetailsDataModel 対象データの行クラス
     */
    private void setUpdModelData(InventoryDetailsDataModel row) {
		// 本日を設定(文字列型)
		row.setConfirmedDate(LocalDate.now().toString());
    }

    /**
     * ログインユーザ権限による画面制御
     * @param userMask ログインしたユーザの権限(Permission Mask)
     * @brief ユーザの権限に応じた各Controlの有効化制御を行う<br>
     */
    private void setUserPermissionControls(int userMask) {
        
    	// システム管理者(31)は無条件で全ボタンを有効化する
        if (userMask == PermissionMask.MASK_MASTER) {
            this.inventoryCounting_button.setDisable(false);
            this.loan_button.setDisable(false);
            
            return;
        }

        // 「登録(CREATE)」と「更新(UPDATE)」の両方のビットが立っているか判定 (00110)
        int requiredMask = PermissionMask.CREATE.getBit() | PermissionMask.UPDATE.getBit(); // 2 + 4 = 6
        
        // ユーザーの権限（userMask）に、requiredMaskのビットがすべて含まれているか
        var isEnabled = (userMask & requiredMask) == requiredMask;
        
        // ボタン有効化制御
        this.inventoryCounting_button.setDisable( !isEnabled );
        this.loan_button.setDisable( !isEnabled );
        this.return_button.setDisable( !isEnabled );
    }
    
    /**
     * ボタン項目 無効化処理
     * @brief ボタンの全コントロールを無効化を行う<br>
     */
    private void setButtonControlsAllDisabled() {
     	
       	// ボタン制御
    	inventoryCounting_button.setDisable( true );
    	loan_button.setDisable( true );
    	return_button.setDisable( true );
    }      
    
    /**
     * 遷移元画面呼び出し
     * @brief 遷移元画面(備品一覧：inventoryList)を呼出す。<br>
     */
    private void showOwnerPage() {
    	// 遷移元画面に切替
    	super.setPage(new application.
    			java.
    			window.
    			inventoryLoans.
    			inventoryList.
    			FormController());
    }
 }