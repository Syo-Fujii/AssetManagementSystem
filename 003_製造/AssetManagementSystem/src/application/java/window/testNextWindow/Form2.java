package application.java.window.testNextWindow;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.dbTablesModel.StockTypeMasterModel;
import application.java.base.tableViewListModel.InventoryListDataModel;
import application.java.manager.MySqlManager;
import application.java.window.inventoryLoans.inventoryList.FormController;
import application.resources.mapper.StockTypeMasterMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Form2 extends BaseFormPage{
	
	@FXML private Button BackPage;
	@FXML private Label label_TEST;
	
	private int count = 0;
	
	public Form2() 
	{
		this.setfxmlFilePath("/application/resources/fxml/TestWindow.fxml");
		this.setWindowTitle("備品管理システム");
	}	
	
    @FXML
    public void onButtonClicked() {
    	count += 1;
    	
    	label_TEST.setText("押下回数：" + count + "回");

    	super.setPage(new FormController());
    }	

    /**
     * (テスト用)TableViewデータ取得・表示処理
     */
    private void ShowTest()
    {
    	MySqlManager.ExecuteQueryOnParallel(
    			(SqlSession session) -> {
					try {
						return this.Test(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			isSccess -> { /*tableListView.setItems( makeTastDatas() );*/ },
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
