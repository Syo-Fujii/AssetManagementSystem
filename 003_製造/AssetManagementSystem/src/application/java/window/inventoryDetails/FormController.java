package application.java.window.inventoryDetails;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.InventoryDetailsDataModel;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.MySqlManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryDetailsMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	
	@FXML private AnchorPane pane_form;
	
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
	@FXML private Button return_button;
	
	private Integer stockType = 0; 
	private String stockCode = "";
	private Integer windowSizeType = 1;

	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryDetails"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryDetailsStyle"));		
		
		this.setPageTitle("備品詳細画面");
	}
	public FormController(Integer type, String code, String windowSize) 
	{
		this();

		this.stockType = type;
		this.stockCode = code;
		this.windowSizeType = AppUtil.parseInt(windowSize, 1);
	}
	
    @FXML
    /**
     * 画面(scene)初期化イベント
     * .NET Load相当 
     * 画面の表示前、ノードが配置された段階で実行
     */
    void initialize() {
 
    	System.out.println("inventoryDetails controller initialize");
    	
    	// 最初の画面起動として、[SQL Session]を生成・保持する。
		MySqlManager.getSqlSessionFactory();
		
		// TableView起動設定
		this.tableViewSettings();
		
		// 画面起動設定
		this.formInitialize();
    	
    	System.out.println("備品詳細 リスト表示処理");
    	super.<InventoryDetailsDataModel>fillTableAsync();

    }	

    @FXML
    /**
     * [所在確認]ボタン 押下イベント
     */
    public void onCountingButtonClicked() {
    	
    	// 選択行取得
    	InventoryDetailsDataModel row = tableListView.
    			getSelectionModel().
    			getSelectedItem();

    	if (row == null) {
    	    // なにもしない
    	    return;
    	} 

    	// 選択されている場合の処理
    	System.out.println("選択されたシリアル番号: " + row.getSerialNo());
    	
    	Optional<ButtonType> result = MessageBox.Show(
    			Alert.AlertType.CONFIRMATION,
    			ShowButtonType.YES_NO,
    			"確認",
    			"削除の確認",
    			"選択した備品を削除してもよろしいですか？");

    	if (result.isPresent() && result.get() == ButtonType.OK) {
    	    // 「OK」が押された時の処理
    	    System.out.println("削除を実行します");
    	} else {
    	    // 「キャンセル」や「×」が押された時の処理
    	    System.out.println("キャンセルされました");
    	}    	
    }
    
    @FXML
    /**
     * [一覧に戻る]ボタン 押下イベント 
     */
    public void onReturnButtonClicked() {

    	// 遷移元画面に切替
    	super.setPage(new application.java.window.inventoryList.FormController());
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
    protected <T extends BaseTableViewModel> List<T> excuteMapperFunction(SqlSession session) {
		InventoryDetailsMapper mapper = session.getMapper(InventoryDetailsMapper.class);
	    
	    // 備品詳細データ取得
	    return (List<T>) mapper.getTableDetailRecords(this.stockType, this.stockCode);
    }

    @SuppressWarnings("unchecked")
	@Override
	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
		tableListView.setList( (List<InventoryDetailsDataModel>)listData );
		
		if (listData != null && listData.size() >= 0) {
			lbl_stock_name.setText(((InventoryDetailsDataModel)listData.getFirst()).getTypeName());
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
    	
    	// カラム項目移動可否
    	tableListView.setIsReorderabled(false);
    	
    	// 選択動作 設定
    	tableListView.setIsRowsMultiSelected(false);
    	tableListView.setIsCellSelected(false);   	
    	tableListView.onSelectedRowEvent(
    			bef    -> { bef = null; },
    			result -> { this.callbackTableSelectedRow( (InventoryDetailsDataModel)result ); });

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
    	//col_remarks.getStyleClass().add("number-aligned");
    	
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
    	
    	// 標準サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.NOMAL.getId()) {
    		tableListView.setPrefWidth(860);
    		pane_form.setPrefWidth(910);
    		lbl_stock_name.setPrefWidth(910);
    		return;
    	}
    	
    	// 拡大サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.WIDE.getId()) {
    		tableListView.setPrefWidth(1350);
    		pane_form.setPrefWidth(1400);
    		lbl_stock_name.setPrefWidth(1400);
    		return;
    	}
    	
    	// 周辺機器サイズの場合
    	if(this.windowSizeType == AppConst.WindowSize.PERIPHERAL.getId()) {
    		tableListView.setPrefWidth(1200);
    		pane_form.setPrefWidth(1250);
    		lbl_stock_name.setPrefWidth(1250);
    		return;
    	}
    }  
}