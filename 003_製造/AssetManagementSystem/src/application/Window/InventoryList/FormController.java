package application.Window.InventoryList;

import java.nio.file.Paths;

import application.Class.BaseFormPage;
import application.Class.TableViewListModel.InventoryListDataModel;
import application.Window.TestNextWindow.Form2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 備品一覧画面
 * @brief 画面操作メソッド(Controller)
 */
public class FormController extends BaseFormPage{
	
	@FXML private Button NextPage;
	@FXML private Label label_TEST;
	@FXML private TableView<InventoryListDataModel> tableListView;
	@FXML private TableColumn<InventoryListDataModel, String> col_itemName;
	
	
	private int count = 0;
	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setfxmlFilePath("/application/Window/InventoryList/InventoryList.fxml");
		this.setWindowTitle("備品管理システム");
		
		String a = Paths.get("").toAbsolutePath().toString();
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
    	this.testTableView();
    }	
    
    @FXML
    public void onButtonClicked() {

    	super.setPage(new Form2());
    }
    
    
    private void testTableView()
    {
    	// col_itemName = new TableColumn<> ("ItemName");
    	col_itemName.setCellValueFactory( new PropertyValueFactory<>("itemName") );
    	// tableListView.getColumns().add(col_itemName);
    	
    	// tableListView.getColumns().add(col_itemName);
    	
    	
    	
    	// 初期設定
    	// データを追加
        ObservableList<InventoryListDataModel> data = FXCollections.observableArrayList(
                new InventoryListDataModel( "１行１列"  )
        );
         
        // データを登録
        tableListView.setItems( data );
         
        // 複数セル選択可能に設定
        //tableListView.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        // tableListView.getSelectionModel().setCellSelectionEnabled( true );
 
        // 選択を検知するバインディングを設定
        /*tableListView.getSelectionModel().selectedItemProperty().addListener( 
                ( ov , old , current) ->
                {
                    // 標準出力にヘッダ文字出力
                    System.out.println( "選択セル（TableView）" );
                     
                    // 選択したセル位置を取得
                    for( TablePosition<TableData, ?> pos : tableListView.getSelectionModel().getSelectedCells() )
                    {
                        // 選択行・列の情報を取得
                        int row = pos.getRow();
                        TableColumn<TableData, ?> col = pos.getTableColumn();
                         
                        // 選択行を取得
                        TableData item = tableListView.getItems().get( row );
 
                        // 選択セルを取得
                        String selected = (String)col.getCellObservableValue(item).getValue();
                         
                        // 標準出力に出力
                        System.out.println( "　" + selected );
                         
                    }
                }
                );*/
    	
    	//tableListView.setItems( FXCollections.observableArrayList( "1st" , "2nd" , "3rd" ) );
    	// tableListView.getSelectionModel().selectFirst();
    }
}

