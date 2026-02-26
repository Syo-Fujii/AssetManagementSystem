package application.java.window.inventoryLoan;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.InventoryDetailsDataModel;
import application.java.base.tableViewListModel.InventoryLoanDataModel;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.CustomComboBoxTableCellManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryDetailsMapper;
import application.resources.mapper.InventoryLoanMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * 備品貸出画面
 * @brief [inventoryLoan]画面操作メソッド(Controller)<br>
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
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	@FXML private Label lbl_stock_name;
	
	@FXML private TableViewManager<InventoryLoanDataModel> tableListView;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_serial;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_staff_name;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_start_date;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_limit_date;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_remarks;
	@FXML private TableColumn<InventoryLoanDataModel, Boolean> col_is_checkout;
	
	@FXML private Button submit_button;
	@FXML private Button back_button;
	
	private Integer stockType = 0;
	private String stockCode = "";
	private Integer previousPageWindowSize = 1;

	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryLoan"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryDetailsStyle"));
		
		this.setPageTitle("備品貸出画面");
	}
	public FormController(Integer type, String code, Integer size) 
	{
		this();

		this.stockType = type;
		this.stockCode = code;
		this.previousPageWindowSize = size;
	}
	
    @FXML
    /**
     * 画面(scene)初期化イベント
     * .NET Load相当 
     * 画面の表示前、ノードが配置された段階で実行
     */
    void initialize() {
 
    	System.out.println("inventoryLoan controller initialize");
    	
    	// 最初の画面起動として、[SQL Session]を生成・保持する。
		MySqlManager.getSqlSessionFactory();
		
		// TableView起動設定
		this.tableViewSettings();
		
		// 画面起動設定
		this.formInitialize();
    	
    	System.out.println("備品貸出 リスト表示処理");
    	super.<InventoryDetailsDataModel>fillTableAsync();
    	
    	//loan_button.setDisable(true);

    }	
 
    @FXML
    /**
     * [貸出]ボタン 押下イベント 
     */
    public void onLoanButtonClicked() {

    	// 遷移元画面に切替
    	super.setPage(new application.java.window.inventoryList.FormController());
    }    
    
    @FXML
    /**
     * [戻る]ボタン 押下イベント 
     */
    public void onBackButtonClicked() {

    	// 遷移元画面に切替
    	super.setPage(new application.
    			java.
    			window.
    			inventoryDetails.
    			FormController(this.stockType, this.stockCode, this.previousPageWindowSize.toString()));
    }

    @SuppressWarnings("unchecked")
	@Override
	/**
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
	 */
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
		InventoryLoanMapper mapper = session.getMapper(InventoryLoanMapper.class);
	    
	    // 備品詳細データ取得
	    return (List<T>) mapper.getTableLoanableData(this.stockType, this.stockCode);
    }

    @SuppressWarnings("unchecked")
	@Override
	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
		
    	List<InventoryLoanDataModel> rows = (List<InventoryLoanDataModel>) listData;
    	
    	tableListView.setList( rows );

    	// 備品分類の表示
		if (listData != null && !rows.isEmpty()) {
			lbl_stock_name.setText(rows.getFirst().getTypeName());
		}
    }
    
	@Override
    /**
     * DB取得失敗(例外発生)時の処理(非同期処理)
     * @brief controller内で用いる取得例外処理>
     */
	protected void exceptionResult(Throwable exception)
    {
		System.err.println("備品詳細データ取得失敗");
		super.exceptionResult(exception);
    }

	@Override
    /**
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
	protected <T extends BaseTableViewModel> Boolean executeCudMapperFunction(SqlSession session, List<T> listData) {
		
		InventoryDetailsDataModel row = (InventoryDetailsDataModel)listData.getFirst();
		
		// 本日を設定(文字列型)
		row.setConfirmedDate(LocalDate.now().toString());

		InventoryDetailsMapper mapper = session.getMapper(InventoryDetailsMapper.class);
	    
	    // 備品詳細データ取得
	    Integer updCount =  mapper.updConfirmedDate(row);

	    return true;
    }	
	
	/**
	 *所在確認日 更新確認Message
	 * @param serialNo
	 * @return 確認結果
	 */
    private Boolean showMessageUpdConfimedDate(String serialNo) {
    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			"確認",
    			null,
    			"備品[シリアルNo: "+ serialNo + " ]の所在確認日を本日に更新します。" + AppUtil.newLine() +
    			"よろしいですか？");
    }
	
    /**
     * 警告Message：備品データなし
     * @param serialNo
     */
    private void showMessageEmptyStockData(String serialNo) {
    	MessageBox.ShowWarnig(
    			"警告",
    			"データ異常：備品データが存在しません。",
    			"備品[シリアルNo: "+ serialNo + " ]の備品データ(stock_data)が存在しません。" + AppUtil.newLine() +
    			"");
    	
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
    	
    	// TableView 編集可否設定
    	tableListView.setEditable(true);
   
    	// Cell 有効化制御
    	tableListView.cellIsEnabled(col_serial, false);
    	tableListView.cellIsEnabled(col_remarks, false);
    	tableListView.onDisabledCellsFocusSkipEvent();
    	
    	
    	
    	tableListView.cellIsEnabled(col_staff_name, true);
    	// 選択肢のリスト
    	ObservableList<String> options = FXCollections.observableArrayList("Apple", "Banana", "Cherry");

    	// カラムにコンボボックスのセルファクトリを設定
    	//col_staff_name.setCellValueFactory(new PropertyValueFactory<>("staffName"));
    	//col_staff_name.setCellFactory(ComboBoxTableCell.forTableColumn(options));
    	
    	//col_staff_name.setCellValueFactory(new PropertyValueFactory<>("staffName"));
    	/*
    	col_staff_name.setCellFactory(
    			col -> new InputAutoCompleteComboBoxCellManager<InventoryLoanDataModel, String>(options));
    	
    	// 3. 編集完了時の反映処理（これを書かないとモデルに値が入りません）
    	col_staff_name.setOnEditCommit(event -> {
    		InventoryLoanDataModel rowData = event.getRowValue();
    	    rowData.setStaffName(event.getNewValue());
    	});*/
    	
    	col_staff_name.setCellFactory(
    			col -> new CustomComboBoxTableCellManager<InventoryLoanDataModel, String>(options));	
    	
    	col_staff_name.setOnEditCommit(event -> {
    		InventoryLoanDataModel rowData = event.getRowValue();
    	    rowData.setStaffName(event.getNewValue());
    	}); 
    	
    	
    	
    	// 選択動作 設定
    	tableListView.setIsRowsMultiSelected(false);
    	tableListView.setIsCellSelected(true);
    	
    	tableListView.onSelectedCellsEvent(
    			bef    -> { bef = null; },
    			result -> { /*this.callbackTableSelectedRow( (InventoryDetailsDataModel)result );*/ });
    	
        	
    	/*
    	// TableView[列]文字設定 
    	//col_serial.getStyleClass().add("cell-itemname");
    	col_staff_name.getStyleClass().add("number-aligned");
    	col_rent_flg.getStyleClass().add("center-aligned");
    	col_start_date.getStyleClass().add("center-aligned");
    	col_limit_date.getStyleClass().add("center-aligned"); 
    	col_confirmed_date.getStyleClass().add("center-aligned"); 
    	col_model.getStyleClass().add("center-aligned");
    	col_maker.getStyleClass().add("center-aligned");
    	//col_destination_serial_no.getStyleClass().add("number-aligned");
    	col_type.getStyleClass().add("center-aligned");
    	col_lease_date.getStyleClass().add("center-aligned");    	
    	//col_remarks.getStyleClass().add("number-aligned");*/
    	
    	// 0件の場合のCaptionを削除(「データがありません」非表示)
    	tableListView.setPlaceholder(new Label("")); 
    }

    /**
     * [TableViewManager] 項目(column-Data)Bind設定(CallBack関数)
     * @brief TableViewのColumnが当該クラス内で定義(Bind)されているため、
     * Columnとデータクラスのプロパティの紐づけを当該メソッドで行う。<br>
     * <p>
     *  [FXML 画面.TableViewのColumn変数].<br>
     *            setCellValueFactory( new PropertyValueFactory<>([データModelのプロパティ名]:String文字列))
     */
    private void callbackBindTableColumnSource() {
    	col_serial.setCellValueFactory( new PropertyValueFactory<>("serialNo"));
    	col_staff_name.setCellValueFactory( new PropertyValueFactory<>("staffName"));
     	col_start_date.setCellValueFactory( new PropertyValueFactory<>("startDate"));
    	col_limit_date.setCellValueFactory( new PropertyValueFactory<>("limitDate"));
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));
    	col_is_checkout.setCellValueFactory( new PropertyValueFactory<>("isCheckOut"));
    	
    	// 対象項目の入力を可能にする
    	col_staff_name.setEditable(true);
    	col_start_date.setEditable(true);
    	col_limit_date.setEditable(true);
    	col_is_checkout.setEditable(true);
    	

    	// tableListView.setColumnReorderable(col_remarks, false);
    }
    
    /**
     * TableView 行選択イベント
     */
    private void callbackTableSelectedRow(InventoryDetailsDataModel row) {
    	 System.out.println("選択行 シリアルNo: [ " + row.getSerialNo() + " ]");
    }

    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	System.out.println("備品詳細 画面初期化処理");
    	
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
    }
 }