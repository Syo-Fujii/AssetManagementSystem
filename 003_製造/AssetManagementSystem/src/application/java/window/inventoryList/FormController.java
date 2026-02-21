package application.java.window.inventoryList;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.dbTablesModel.StockTypeMasterModel;
import application.java.base.tableViewListModel.InventoryListDataModel;
import application.java.common.AppUtil;
import application.java.manager.MySqlManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryListMapper;
import application.resources.mapper.StockTypeMasterMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

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
	
	@FXML private AnchorPane pane_form;
	
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

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryList"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryListStyle"));		

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
    	
    	if(!tableListView.getIsColumnSettingCompleted()) {
        	// 最初の画面起動として、[SQL Session]を生成・保持する。
    		MySqlManager.getSqlSessionFactory();
    		
    		// 画面起動設定
    		this.tableViewSettings();   		
        	tableListView.setIsColumnSettingCompleted(true);
        	
        	lbl_title.setText(this.getPageTitle());
        	lbl_title.getStyleClass().add("titletext");  
    	}
    	
    	this.ShowInventoryList();
    }	
    
    /**
     * TableView設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
    private void tableViewSettings()
    {
    	System.out.println("備品一覧 カラム・セル設定/定義");
    	
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
    	
    	 System.out.println("選択行 分類種別: " + row.getType() + 
    			            " 分類コード: " + row.getCode() +
    			            " 遷移先画面サイズ : [" + row.getWindowSize() + "]");
    	 super.setPage(new application.
    			 java.window.
    			 inventoryDetails.
    			 FormController(row.getType(), row.getCode(), row.getWindowSize()));
    }     

    /**
     * 備品一覧 リスト表示処理
     * 
     * 
     * 
     * 
     */
    private void ShowInventoryList()
    {
    	System.out.println("備品一覧 リスト表示処理");
    	
    	MySqlManager.<InventoryListDataModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getInventoryListData(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> { 
    				tableListView.setList( listData );},
    			exception -> {
    				System.err.println("備品一覧データ取得失敗");
    				System.err.println(exception.getMessage());
    	            // JavaFXのアラートを表示
    	            Alert alert = new Alert(Alert.AlertType.ERROR);
    	            alert.setTitle("データベースエラー");
    	            alert.setHeaderText("データの取得に失敗しました");
    	            alert.setContentText(exception.getCause() != null ? 
    	                                 exception.getCause().getMessage() : exception.getMessage());
    	            alert.showAndWait();     				
    			}
    			); 
    }

    /**
     * DBクエリ発行処理
     * @param session
     * @throws Exception 
     */
    private List<InventoryListDataModel> getInventoryListData(SqlSession session) throws Exception
    {
    	try {
    		
    		InventoryListMapper mapper = session.getMapper(InventoryListMapper.class);
    	    
    	    // 備品一覧データ取得
    	    return mapper.getTableRecords();

    	} catch(Exception e){
    		throw new Exception(e); 
    	}
      }     
 
    /**
     * (テスト用)TableViewデータ取得・表示処理
     */
    private void ShowTest()
    {
    	MySqlManager.ExcuteQueryOnParallel(
    			(SqlSession session) -> {
					try {
						return this.Test(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			isSccess -> { tableListView.setItems( makeTastDatas() );},
    			exception -> 
    			{
    				System.err.println("備品一覧データ取得失敗");
    				System.err.println(exception.getMessage());
    	            // JavaFXのアラートを表示
    	            Alert alert = new Alert(Alert.AlertType.ERROR);
    	            alert.setTitle("データベースエラー");
    	            alert.setHeaderText("データの取得に失敗しました");
    	            alert.setContentText(exception.getCause() != null ? 
    	                                 exception.getCause().getMessage() : exception.getMessage());
    	            alert.showAndWait();   				
    			}
    			); 
    }    
    
    /**
     * (テスト用)TableViewデータ取得処理
     * @param session
     * @throws Exception 
     */
    private Boolean Test(SqlSession session) throws Exception
    {
    	try {
        	StockTypeMasterMapper mapper = session.getMapper(StockTypeMasterMapper.class);
    	    
    	    // 全件取得の実行
    	    List<StockTypeMasterModel> userList = mapper.selectAll();
    	    // userList.removeIf(Objects::isNull);
    	    
    	    int count = userList.size();
    	    
    	    List<StockTypeMasterModel> B = userList;
    	    
    	    return true; 		

    	} catch(Exception e){
    		throw new Exception(e); 
    	}
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