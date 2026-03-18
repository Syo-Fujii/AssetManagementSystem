package application.java.window.verifyLocation.performInventory;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.PerformInventoryDataModel;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.PerformInventoryMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class FormController  extends BaseFormPage {
	
	private final String FORM_NAME = "棚卸画面";

	@FXML private Label lbl_title;	
	
	@FXML private TableViewManager<PerformInventoryDataModel> tableListView;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_serial;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_type_name;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_model;	
	@FXML private TableColumn<PerformInventoryDataModel, String> col_rent_flg;	
	@FXML private TableColumn<PerformInventoryDataModel, String> col_staff_name;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_start_date;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_limit_date;
	@FXML private TableColumn<PerformInventoryDataModel, String> col_confirmed_date;
	@FXML private TableColumnManager<PerformInventoryDataModel, String> col_remarks;
	@FXML private TableColumnManager<PerformInventoryDataModel, String> col_inventory_date;
	@FXML private TableColumnManager<PerformInventoryDataModel, Boolean> col_is_inventory;

	@FXML private Button inventory_button;
	@FXML private Button back_button;
	
	private LocalDate dateNow = null;
	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("PerformInventory"));
		this.setCssFile(AppUtil.MakeCssFilePath("PerformInventoryStyle"));
		
		this.setPageTitle(FORM_NAME);
		
    	/* カレンダー(DatePicker)の初期値 = 本日 */
     	dateNow = LocalDate.now();		
	}

    /**
     * 画面(scene)初期化イベント
     * .NET FormLoad & Shown相当 
     * 画面の表示前、ノードが配置された段階で実行
     * @brief 画面(scene)の遷移には、FXMLLoaderでFXMLを読み込み、新しいControllerを生成しているので<br>
     * 当該が各画面(scene)の呼び出しイベント(FormLoad/FormShown)相当となる。
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
    	super.<PerformInventoryDataModel>fillTableAsync();
    }	
 
    
    @FXML
    public void onInventoryButtonClicked() {
    	try {
    		// 選択行取得
    		PerformInventoryDataModel row = tableListView.
        			getSelectionModel().
        			getSelectedItem();

        	if (row == null) {
        	    // なにもしない
        	    return;
        	} 

        	LogManager.writeTrace("[モデル内値]"); 
        	LogManager.writeTrace("シリアルNo：[" + row.getSerialNo() + "]"); 
        	LogManager.writeTrace("備考：[" + row.getRemarks() + "]");
        	LogManager.writeTrace("棚卸日：[" + row.getInventoryDate() + "]");
        	LogManager.writeTrace("棚卸CHECK：[" + row.getIsInventory().toString() + "]");
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： メニュー画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
    }	   
   
    
    
    /**
     * [メニューに戻る]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
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
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 棚卸データ取得処理");
    	
    	String dateText = AppUtil.convertToString(this.dateNow, "yyyy/MM/dd");
    	
    	PerformInventoryMapper mapper = session.getMapper(PerformInventoryMapper.class);
    	
	    // 棚卸データ取得
	    return (List<T>) mapper.getTableInventoryRecords( dateText );
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 棚卸データ連携(BIND)処理");
    		
    		// データ設定(BIND・SELL設定値)を初期化
    		tableListView.dataSourceClear();        	
    		
    		List<PerformInventoryDataModel> rows = (List<PerformInventoryDataModel>) listData;
        	
        	tableListView.setList( rows );

    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 棚卸データの連携中にエラーが発生しました";
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
		LogManager.writeError("[" + FORM_NAME + "] ： DB 棚卸データ取得失敗");
		super.exceptionResult(exception);
    }
    
    /**
     * TableView設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
	private void tableViewSettings()
    {
		LogManager.writeTrace("[" + FORM_NAME + "] ： カラム・セル設定/定義");
    	
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// カラム列移動(順番)可否
    	tableListView.setIsReorderabled(false);
    	
    	// TableView 編集可否設定
    	tableListView.setEditable(true);
   
    	// Cell 有効化制御
    	tableListView.cellIsEnabled(col_serial, false);
    	tableListView.cellIsEnabled(col_type_name, false);
    	tableListView.cellIsEnabled(col_model, false);
    	tableListView.cellIsEnabled(col_staff_name, false);
    	tableListView.cellIsEnabled(col_start_date, false);
    	tableListView.cellIsEnabled(col_limit_date, false);
    	tableListView.cellIsEnabled(col_confirmed_date, false);
      	
    	// 無効Cell Focus SKIP設定 
    	tableListView.onDisabledCellsFocusSkipEvent();
    	
    	// 項目(カスタムセル)型設定
    	col_remarks.setCellTypeCustomInputText(true, true);
		col_inventory_date.setCellTypeCustomDatePicker(true, true, dateNow, null, dateNow);
		col_is_inventory.setCellTypeCustomCheckBox(true, true);    	

    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(true);
    	
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
    	col_model.setCellValueFactory( new PropertyValueFactory<>("model"));
    	col_rent_flg.setCellValueFactory( new PropertyValueFactory<>("rentFlg"));
    	col_staff_name.setCellValueFactory( new PropertyValueFactory<>("staffName"));
     	col_start_date.setCellValueFactory( new PropertyValueFactory<>("startDate"));
    	col_limit_date.setCellValueFactory( new PropertyValueFactory<>("limitDate"));
    	col_confirmed_date.setCellValueFactory( new PropertyValueFactory<>("confirmedDate"));
    	
    	// カラム設定(Cell.valueとmodelのBIND)
    	col_remarks.setCellValueFactory(  data -> data.getValue().remarksProperty());
    	col_inventory_date.setCellValueFactory(  data -> data.getValue().inventoryDateProperty());
    	col_is_inventory.setCellValueFactory( data -> data.getValue().isInventoryProperty());
    }	

    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize");
    	
    	lbl_title.setText(this.getPageTitle());
    	// lbl_title.getStyleClass().add("titletext");
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
