package application.java.window.inventoryLoans.inventoryList;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.InventoryListDataModel;
import application.java.common.AppSession;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryListMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

	private final String FORM_NAME = "備品一覧画面";
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_LoginUser;
	@FXML private Label lbl_title;

	@FXML private TableViewManager<InventoryListDataModel> tableListView;
	@FXML private TableColumn<InventoryListDataModel, String> col_itemName;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_loanCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_retCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_unknownCnt;
	@FXML private TableColumn<InventoryListDataModel, Integer> col_totalCnt;
	
	@FXML private Button back_button;
	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() {
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryList"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryListStyle"));

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
	void initialize() {
     	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize"); 
    	
    	if(!tableListView.getIsColumnSettingCompleted()) {
        	// 最初の画面起動として、[SQL Session]を生成・保持する。
    		MySqlManager.getSqlSessionFactory();
    		
    		// 画面起動設定
    		this.tableViewSettings();
        	tableListView.setIsColumnSettingCompleted(true);
        	
        	lbl_LoginUser.setText(AppSession.getLoginUserInfo());        	
        	lbl_title.setText(this.getPageTitle());
    	}
    	
    	LogManager.writeDebug("[" + FORM_NAME + "] ： リスト表示処理");
    	super.<InventoryListDataModel>fillTableAsync(); 	

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
    	LogManager.writeTrace("[" + FORM_NAME + "] ： DB 備品一覧データ取得");
    	
    	InventoryListMapper mapper = session.getMapper(InventoryListMapper.class);
	    
	    // 備品一覧データ取得
	    return (List<T>) mapper.getTableRecords();
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
    @SuppressWarnings("unchecked")
	@Override
    protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
    	try {
    		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品一覧データ連携(BIND)処理");
    		
    		// 当該画面では再描画(再検索)はない為[dataSourceClear]は呼出さない
    		
    		List<InventoryListDataModel> rows = (List<InventoryListDataModel>) listData;
        	
        	tableListView.setList( rows );		
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 備品一覧データの連携中にエラーが発生しました";
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
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品一覧データ取得失敗"); 
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
    	
    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(false);
       	tableListView.onSelectedRowLeaveEvent(result -> {
    	    this.callbackTableSelectedRow((InventoryListDataModel) result);
    	});

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
     *            setCellValueFactory( new PropertyValueFactory<>([データModelのプロパティ名]:String文字列))<br>
     *  入力項目の場合：プロパティをそのまま渡すゲッター<br>
     *  (StringProperty staffNameProperty() { return this.staffName; })がMODELに必要<br>
     *            setCellValueFactory( data -> data.getValue().staffNameProperty());
     */
    private void callbackBindTableColumnSource() {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： カラムBIND設定");

    	col_itemName.setCellValueFactory( new PropertyValueFactory<>("itemName"));
    	col_loanCnt.setCellValueFactory( new PropertyValueFactory<>("loanCount"));
    	col_retCnt.setCellValueFactory( new PropertyValueFactory<>("returnCount"));
    	col_unknownCnt.setCellValueFactory( new PropertyValueFactory<>("unknownCount"));
    	col_totalCnt.setCellValueFactory( new PropertyValueFactory<>("totalCount"));
    }
    
    /**
     * TableView 明細行選択イベント
     * @param row 選択明細行
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    private void callbackTableSelectedRow(InventoryListDataModel row) {
    	try {
        	String content = "選択行 分類種別: " + row.getType() +
			                 " 分類コード: " + row.getCode() + 
			                 " 遷移先画面サイズ : [" + row.getWindowSize() + "]";
	
        	LogManager.writeDebug("[" + FORM_NAME + "] ： " + content);
	 
        	super.setPage(
        			new application.
        			java.window.
        			inventoryLoans.
        			inventoryDetails.
        			FormController(row.getType(), row.getCode(), row.getWindowSize())); 		
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
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