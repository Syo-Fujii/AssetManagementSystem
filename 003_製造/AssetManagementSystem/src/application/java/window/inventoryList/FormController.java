package application.java.window.inventoryList;

import application.java.base.BaseFormPage;
import application.java.base.tableViewListModel.InventoryListDataModel;
import application.java.manager.TableViewManager;
import application.java.window.testNextWindow.Form2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 備品一覧画面
 * @brief [inventoryList]画面操作メソッド(Controller)<br>
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
	
	@FXML private Label lbl_title;

	@FXML private TableViewManager<InventoryListDataModel> tableListView;
	@FXML private TableColumn<InventoryListDataModel, String> col_itemName;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_loanCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_retCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_unknownCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_totalCnt;
	
	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath("/application/resources/fxml/InventoryList.fxml");
		this.setCssFile("/application/resources/css/InventoryListStyle.css");
		this.setPageTitle("備品一覧画面");
	}
	
    @FXML
    /**
     * 画面(scene)初期化イベント
     * .NET Load相当 
     * 画面の表示前、ノードが配置された段階で実行
     */
    void initialize() {
 
    	System.out.println("inventoryList controller initialize");
    	
    	this.tableViewSettings();
    
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
    	
    	
        // データを登録
    	tableListView.setItems( makeTastDatas() ); 
    }	

    
    /**
     * TableView設定
     */
    private void tableViewSettings()
    {
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// カラム項目移動可否
    	tableListView.setIsReorderabled(false);
    	
    	// 選択動作 設定
    	tableListView.setIsRowsMultiSelected(false);
    	tableListView.setIsCellSelected(false);   	
    	tableListView.onSelectedRowEvent(
    			bef    -> { bef = null; },
    			result -> { this.callbackTableSelectedRow( (InventoryListDataModel)result ); });

    	// TableView[列]文字設定 
    	col_itemName.getStyleClass().add("cell-itemname");
    	col_loanCnt.getStyleClass().add("number-aligned");
    	col_retCnt.getStyleClass().add("number-aligned");
    	col_unknownCnt.getStyleClass().add("number-aligned");
    	col_totalCnt.getStyleClass().add("number-aligned"); 
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
    	col_itemName.setCellValueFactory( new PropertyValueFactory<>("itemName"));
    	col_loanCnt.setCellValueFactory( new PropertyValueFactory<>("loanCount"));
    	col_retCnt.setCellValueFactory( new PropertyValueFactory<>("returnCount"));
    	col_unknownCnt.setCellValueFactory( new PropertyValueFactory<>("unknownCount"));
    	col_totalCnt.setCellValueFactory( new PropertyValueFactory<>("totalCount"));
    }
    
    /**
     * TableView 項目(column-Data)Bind設定
     */
    private void callbackTableSelectedRow(InventoryListDataModel row) {
    	
    	 System.out.println("継承先　選択された行のデータ: " + row.getItemName());
    	 super.setPage(new Form2());
    }     
    
    
    /**
     * テストデータ生成
     * @return ObservableList<InventoryListDataModel> 備品一覧データ(1行分のデータ)のリスト
     */
    private ObservableList<InventoryListDataModel> makeTastDatas(){
    	return FXCollections.observableArrayList(
    			new InventoryListDataModel( "1行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "2行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "3行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "4行１列" , 1234 , 1234, 1234 ),

    			new InventoryListDataModel( "5行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "6行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "7行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "8行１列" , 1234 , 1234, 1234 ),
                
    			new InventoryListDataModel( "9行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "10行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "11行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "12行１列" , 1234 , 1234, 1234 ),

    			new InventoryListDataModel( "13行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "14行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "15行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "16行１列" , 1234 , 1234, 1234 ),

    			new InventoryListDataModel( "17行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "18行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "19行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "20行１列" , 1234 , 1234, 1234 ),
                
    			new InventoryListDataModel( "21行１列" , 100 , 100 , 100 ,0),
                new InventoryListDataModel( "22行１列" , 0 , 0 , 0 , 0),
                new InventoryListDataModel( "23行１列" , 9999 , 9999 , 9999 , 9999 ),
                new InventoryListDataModel( "24行１列" , 1234 , 1234, 1234 )                
    			);                
    }
}