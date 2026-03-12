package application.java.window.inventoryLoans.inventoryReturn;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.InventoryLoanDataModel;
import application.java.base.tableViewListModel.InventoryReturnDataModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableViewManager;
import application.resources.mapper.InventoryReturnMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

/**
 * 備品返却画面
 * @brief [inventoryReturn]画面操作メソッド(Controller)<br>
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
	
	private final String FORM_NAME = "備品返却画面";
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	@FXML private Label lbl_stock_name;
	
	@FXML private TableViewManager<InventoryReturnDataModel> tableListView;
	@FXML private TableColumn<InventoryReturnDataModel, String> col_serial;
	@FXML private TableColumn<InventoryReturnDataModel, String> col_staff_name;
	@FXML private TableColumn<InventoryReturnDataModel, String> col_start_date;
	@FXML private TableColumn<InventoryReturnDataModel, String> col_limit_date;
	@FXML private TableColumn<InventoryReturnDataModel, String> col_remarks;
	@FXML private TableColumnManager<InventoryReturnDataModel, Boolean> col_is_checkin;

	
	@FXML private Button inventoryCounting_button;
	
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
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理");
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryReturn"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryReturnStyle"));
		
		this.setPageTitle(FORM_NAME);
	}
	public FormController(Integer type, String code, Integer size) 
	{
		this();

		this.stockType = type;
		this.stockCode = code;
		this.previousPageWindowSize = size;
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
    	super.<InventoryLoanDataModel>fillTableAsync();

    }

    /**
     * [返却]ボタン 押下イベント 
     */
    @FXML
    public void onReturnButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [返却]ボタン押下");
        	
    		// [返却]が選択されている一覧を生成
        	List<InventoryReturnDataModel> availableRows = 
        			tableListView.
        			getItems().
        			stream().
        			filter(r -> r.getIsCheckIn()).
        			collect(Collectors.toList());
        	
        	if (availableRows.isEmpty()) { return; }

        	// 返却用の値(日付)を設定
        	availableRows.forEach(this::setUpdModelData);
       	
        	// 処理(備品データ更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 返却(更新・登録)処理");
        	super.executeBulkQuery(availableRows);  		
    		
        	LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<InventoryLoanDataModel>fillTableAsync();

    	} catch (Exception ex) {
    		// 返却ボタン無効化
    		submit_button.setDisable(true);
    		
    		String title = "[" + FORM_NAME + "] ： [返却]ボタン押下でエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }    
    
    /**
     * [戻る]ボタン 押下イベント 
     */
    @FXML
    public void onBackButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [戻る]ボタン押下"); 
    		
    		// 遷移元画面に切替
    		this.showOwnerPage();
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 詳細画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
    }
 
    
	/**
	 * 備品データ取得クエリ発行(貸出中データ取得)
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
	 */
	@Override
    @SuppressWarnings("unchecked")
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品(返却≒貸出中)データ取得処理");
		
		InventoryReturnMapper mapper = session.getMapper(InventoryReturnMapper.class);
	    
	    // 備品データ(貸出中)取得
	    return (List<T>) mapper.getTableLoanableStockData(this.stockType, this.stockCode);
    }

    /**
     * 備品データ更新クエリ発行(返却処理)
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
	@Override
	protected <T extends BaseTableViewModel> Boolean executeCudMapperFunction(SqlSession session, List<T> listData) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品(貸出中)データ更新・登録(返却)処理");
		
		InventoryReturnMapper mapper = session.getMapper(InventoryReturnMapper.class);

	    for (T row : listData) {
	    	//データ更新(返却)処理
	    	mapper.updStockDataReturn( (InventoryReturnDataModel) row );
	    	//データ登録(同一シリアルNoの新規データ)処理
	    	mapper.insCopySerialNewStockData( (InventoryReturnDataModel) row );
	    }	
		
	 // Bulk処理にて一括更新を行うため、クエリ生成段階のResultとして常に[true]を返す。
		return true;
    }	

    /**
     * DBクエリ(CUD)発行結果に応じた処理
     * @param status ExcuteQueryResultStatus 実行結果のステータス
     * @brief DBクエリを発行した際の結果処理<br>
     * クエリの発行結果に対するメソッド(処理)がある場合に用いる。<br>
     * Exceptionが発生している場合は、当該メソッドの後で、Exceptionがthrowされる。<br>
     * 当該基底では1つだけしか用意していないので、複数必要な場合は子クラスで個別に用意する。
     */
	@Override
	protected void excuteQueryResult(ExcuteQueryResultStatus status){
		
		// 更新件数が0件のクエリが存在していた場合、例外MSGを表示
		if (status ==  ExcuteQueryResultStatus.NO_ROWS_AFFECTED) {
			String title = "DB クエリ発行結果エラー";
			
			StringBuilder sb = new StringBuilder();
			sb.append("更新(貸出)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
			
			showMessageException(title, sb.toString());
			
    		// 返却ボタン無効化
    		submit_button.setDisable(true);
		}
	}	
	
	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品(返却)データ連携(BIND)処理");
		
    	List<InventoryReturnDataModel> rows = (List<InventoryReturnDataModel>) listData;
    	
    	tableListView.setSortedList( rows );

    	Boolean isEmptyRecords = (listData == null || rows.isEmpty());
    	
		if (!isEmptyRecords) {
	    	// 備品分類の表示(再描画の際、0件でも表示させる)
			lbl_stock_name.setText(rows.getFirst().getTypeName());
		}

		submit_button.setDisable(isEmptyRecords);
		
		// 備品データ重複検査(不整合CHECK)
		CheckStockDataIntegrity(rows);
		
    	// TableView Focus指定
    	tableListView.setFocusFirstCell(col_is_checkin);
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
     * 追加機能：整合性検査
     * @param rows 
     * @brief 取得した備品データにてシリアル番号が重複している場合、警告MSGを発報する。<br>
     * 起動時に検査することを想定し、以降の処理を継続とする。<br>
     * 起動時に検査することを想定し、データの並び順(TableViewのカラム ソート)は考慮しない。<br>
     * ボタン押下時などカラム ソートの考慮が必要な場合は、[SortedList]の使用を要す。
     */
    private void CheckStockDataIntegrity(List<InventoryReturnDataModel> rows) {
    	
    	// シリアルNoの重複データを取得
    	List<AppConst.addRowNumData<InventoryReturnDataModel>> duplicateRows = 
    			tableListView.getDuplicateRows("serialNo");
    	
    	if (duplicateRows == null || duplicateRows.isEmpty()) { return; }
    	
    	// Errorメッセージ "重複シリアルNo [ ] 行番号：[ ], [ ] \r\n"
    	String details = duplicateRows.
    			stream().
    			collect(Collectors.
    					groupingBy(d -> d.model().getSerialNo(),
    					                 Collectors.mapping(d -> "[ " + String.valueOf(d.rowNum()) + " ]",
    					                		                  Collectors.joining(", ")))).
    			entrySet().
    			stream().
    			map(e -> "重複シリアルNo [" + e.getKey() + "] 行番号：" + e.getValue()).
    			collect(Collectors.joining(AppUtil.newLine()));
    	
    	// 警告MSG
    	showMessageDuplicateStockData( details );
    }
    
	/**
	 *確認Message
	 * @param message String 表示する内容
	 * @return 確認結果
	 */
    private Boolean showMessageConfimed(String message) {
    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			"確認",
    			null,
    			message + "よろしいですか？");
    }

    /**
     * 警告Message：備品データ不整合(重複データ)
     * @param rowMessage String 表示する内容
     */
    private void showMessageDuplicateStockData(String rowMessage) {
    	LogManager.writeWarnig("データ異常：データ不整合 システム管理者に連絡してください。");
    	LogManager.writeWarnig(rowMessage);
    	
    	MessageBox.ShowWarnig(
    			"警告",
    			"データ異常：データ不整合 システム管理者に連絡してください。",
    			"備品(在庫)データに不整合(重複データ)が存在します。システム管理者に連絡してください。" + AppUtil.newLine() +
    			rowMessage);
    }

    /**
     * 例外Message：例外エラー
     * @param title String タイトル
     * @param message String 表示する内容
     */
    private void showMessageException(String title, String message) {
    	LogManager.writeError(title);
    	LogManager.writeError(message);

    	MessageBox.ShowErrorMessage("例外発生", title, message);
    }    
    
    
    /**
     * TableView設定
     * @param kvpItems コンボボックスの選択リスト(kvpのObservableList)
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
    	tableListView.cellIsEnabled(col_staff_name, false);
    	tableListView.cellIsEnabled(col_start_date, false);
    	tableListView.cellIsEnabled(col_limit_date, false);
    	tableListView.cellIsEnabled(col_remarks, false);
    	
    	// 無効Cell Focus SKIP設定 
    	tableListView.onDisabledCellsFocusSkipEvent();
    	
    	// 項目(カスタムセル)型設定
		col_is_checkin.setCellTypeCustomCheckBox(true, true);

    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(true);
    	
    	// TableView[列]文字設定 
    	col_serial.getStyleClass().add("text-aligned");
    	col_staff_name.getStyleClass().add("center-aligned");
    	col_start_date.getStyleClass().add("center-aligned");
    	col_limit_date.getStyleClass().add("center-aligned"); 
    	col_remarks.getStyleClass().add("text-aligned");
    	col_is_checkin.getStyleClass().add("center-aligned");

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
    	
    	// CheckBoxのカラム設定(TableView)    	
    	col_serial.setCellValueFactory( new PropertyValueFactory<>("serialNo"));
    	col_staff_name.setCellValueFactory( new PropertyValueFactory<>("staffName"));
     	col_start_date.setCellValueFactory( new PropertyValueFactory<>("startDate"));
    	col_limit_date.setCellValueFactory( new PropertyValueFactory<>("limitDate"));
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));
    	col_is_checkin.setCellValueFactory( data -> data.getValue().isCheckInProperty());
    }
    
    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize");
    	
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
    }

    /**
     * 更新データ値設定(返却日・最終所在確認日)
     * @param row InventoryReturnDataModel 対象データの行クラス
     */
    private void setUpdModelData(InventoryReturnDataModel row) {
		// 本日を設定(文字列型)
    	LocalDate dateNow = LocalDate.now();
    	
    	row.setReturnDate(dateNow);
    	row.setConfirmedDate(dateNow);
    }   
    
    /**
     * 遷移元画面呼び出し
     * @brief 遷移元画面(備品明細：inventoryDetails)を呼出す。<br>
     */
    private void showOwnerPage() {
    	// 遷移元画面に切替
    	super.setPage(new application.
    			java.
    			window.
    			inventoryLoans.
    			inventoryDetails.
    			FormController(this.stockType, this.stockCode, this.previousPageWindowSize.toString()));
    }
    
  
    
    
    
    
    
    
    
    
    
    
    
    @FXML
    /**
     * [所在確認]ボタン 押下イベント
     */
    public void onCountingButtonClicked() {
    	try {

    		// 選択行取得
        	InventoryReturnDataModel row = tableListView.
        			getSelectionModel().
        			getSelectedItem();

        	if (row == null) {
        	    // なにもしない
        	    return;
        	} 
 
        	System.out.println("選択行モデルデータ");
        	System.out.println("シリアルNo: [" + row.getSerialNo() + "] ");
        	System.out.println("貸出者名: [" + row.getStaffName() + "] ");
        	System.out.println("貸出開始日: ["+ row.getStartDate() + "]");
        	System.out.println("返却予定日: ["+ row.getLimitDate() + "]");
        	System.out.println("返却: ["+ row.getIsCheckIn() + "]"); 
    		
    		
    	} catch (Exception ex) {
    		System.err.println(ex);
    	}
   }    
    
 }