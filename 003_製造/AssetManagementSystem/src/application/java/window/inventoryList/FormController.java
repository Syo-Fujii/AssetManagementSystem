package application.java.window.inventoryList;

import application.java.base.BaseFormPage;
import application.java.base.tableViewListModel.InventoryListDataModel;
import application.java.manager.TableViewManager;
import application.java.window.testNextWindow.Form2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 備品一覧画面
 * @brief 画面操作メソッド(Controller)<br>
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
	
	@FXML private Button NextPage;
	@FXML private Label label_TEST;

	@FXML private TableViewManager<InventoryListDataModel> tableListView;
	//@FXML private TableView<InventoryListDataModel> tableListView;
	@FXML private TableColumn<InventoryListDataModel, String> col_itemName;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_loanCnt;
	
	private int count = 0;
	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setfxmlFilePath("/application/resources/fxml/InventoryList.fxml");
		this.setWindowTitle("備品管理システム");

	}
	
    @FXML
    /**
     * 画面(scene)初期化イベント
     * .NET Load相当 
     * 画面の表示前、ノードが配置された段階で実行
     */
    void initialize() {
    	count += 1;
    	
    	label_TEST.setText("押下回数：" + count + "回");
    	System.out.println("initialize");
    	
    	this.tableViewSettings();
    }	
    
    @FXML
    public void onButtonClicked() {

    	super.setPage(new Form2());
    }

 
    
    
    /**
     * TableView設定
     */
    private void tableViewSettings()
    {
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// 選択動作　設定
    	tableListView.setIsRowsMultiSelected(false);
    	tableListView.setIsCellSelected(false);   	
    	tableListView.onSelectedRowEvent(
    			bef  -> { bef = null; },
    			result -> { this.callbackTableSelectedRow( (InventoryListDataModel)result ); });

    	
    	

    	
    	
    	// 初期設定
    	// データを追加
        ObservableList<InventoryListDataModel> data = FXCollections.observableArrayList(
                new InventoryListDataModel( "1行１列" , 100 ),
                new InventoryListDataModel( "2行１列" , 0 ),
                new InventoryListDataModel( "3行１列" , 9999 ),
                new InventoryListDataModel( "4行１列" , 1234 )
        );

        
        //tableListView.test(new InventoryListDataModel( "１行１列" , 100 ));
        // データを登録
        tableListView.setItems( data );
         

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
    }
    
    /**
     * TableView 項目(column-Data)Bind設定
     */
    private void callbackTableSelectedRow(InventoryListDataModel row) {
    	
    	 System.out.println("継承先　選択された行のデータ: " + row.getItemName());
    }     
    
    
}

