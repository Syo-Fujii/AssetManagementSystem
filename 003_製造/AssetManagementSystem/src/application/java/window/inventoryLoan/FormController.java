package application.java.window.inventoryLoan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.base.tableViewListModel.InventoryDetailsDataModel;
import application.java.base.tableViewListModel.InventoryLoanDataModel;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableColumnManager.keyValuePairItem;
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
	@FXML private TableColumnManager<InventoryLoanDataModel, String> col_staff_name;
	@FXML private TableColumnManager<InventoryLoanDataModel, String> col_start_date;
	@FXML private TableColumnManager<InventoryLoanDataModel, String> col_limit_date;
	@FXML private TableColumn<InventoryLoanDataModel, String> col_remarks;
	@FXML private TableColumnManager<InventoryLoanDataModel, Boolean> col_is_checkout;

	
	@FXML private Button inventoryCounting_button;
	
	@FXML private Button submit_button;
	@FXML private Button back_button;
	
	private Integer stockType = 0;
	private String stockCode = "";
	private Integer previousPageWindowSize = 1;
	private LocalDate dateNow = null;

	
	/** 
	 * コンストラクタ
	 */
	public FormController() 
	{
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryLoan"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryLoanStyle"));
		
		this.setPageTitle("備品貸出画面");
	
    	/* カレンダー(DatePicker)の初期値 = 本日 */
     	dateNow = LocalDate.now();	
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
 
    	System.out.println("inventoryLoan controller initialize");
    	
    	// 最初の画面起動として、[SQL Session]を生成・保持する。
		MySqlManager.getSqlSessionFactory();

    	// 選択肢のリスト(空データ)
     	ObservableList<keyValuePairItem<Integer, String>> comboBoxSource = 
     			FXCollections.observableArrayList();
		
		// TableView起動設定
		this.tableViewSettings(comboBoxSource);
		
		// 画面起動設定
		this.formInitialize();
 
		System.out.println("備品貸出 使用者一覧(ComboBox 選択リスト)取得処理");
		this.fetchStaffMembers(comboBoxSource);

    	System.out.println("備品貸出 リスト表示処理");
    	super.<InventoryLoanDataModel>fillTableAsync();
    	
    	// TableView Focus指定
    	tableListView.setFocusFirstCell(col_staff_name);
    }

    /**
     * [貸出]ボタン 押下イベント 
     */
    @FXML
    public void onLoanButtonClicked() {

    	// [貸出]が選択されている一覧を生成
    	List<InventoryLoanDataModel> availableRows = 
    			tableListView.
    			getItems().
    			stream().
    			filter(r -> r.getIsCheckOut()).
    			collect(Collectors.toList());
    	
    	if (availableRows.isEmpty()) { return; }

    	
    	
    	
    	

    }    
    
    /**
     * [戻る]ボタン 押下イベント 
     */
    @FXML
    public void onBackButtonClicked() {

    	// 遷移元画面に切替
    	super.setPage(new application.
    			java.
    			window.
    			inventoryDetails.
    			FormController(this.stockType, this.stockCode, this.previousPageWindowSize.toString()));
    }
 
    
	/**
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
	 */
	@Override
    @SuppressWarnings("unchecked")
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
		InventoryLoanMapper mapper = session.getMapper(InventoryLoanMapper.class);
	    
	    // 備品詳細データ取得
	    return (List<T>) mapper.getTableLoanableData(this.stockType, this.stockCode);
    }

	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
     * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
		
    	List<InventoryLoanDataModel> rows = (List<InventoryLoanDataModel>) listData;
    	
    	tableListView.setList( rows );

    	Boolean isEmptyRecords = (listData == null || rows.isEmpty());
    	
		if (!isEmptyRecords) {
	    	// 備品分類の表示(再描画の際、0件でも表示させる)
			lbl_stock_name.setText(rows.getFirst().getTypeName());
		}
		
		inventoryCounting_button.setDisable(isEmptyRecords);
		submit_button.setDisable(isEmptyRecords);
		
		// 備品データ重複検査(不整合CHECK)
		CheckStockDataIntegrity(rows);
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
	@SuppressWarnings({ "unused" })
	@Override
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
     * 使用者ComboBox 選択リスト取得処理
     * @param comboBoxSource コンボボックスの選択リスト(kvpのObservableList)
     * @brief コンボBOXのSource更新・差し替えのため、予めSourceとして設定したListを引数で受ける。<br>
     */
    private void fetchStaffMembers(
    		ObservableList<keyValuePairItem<Integer, String>> comboBoxSource)
    {
    	System.out.println("使用者ComboBox 選択リスト取得処理");
    	
    	MySqlManager.<StaffMasterModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getStaffMasterData(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> { 
    				modelsConvertToKeyValuePairList( listData, comboBoxSource ); },
    			exception -> {
    				System.err.println("使用者ComboBox 選択リスト取得失敗");
    				super.exceptionResult(exception);
    			}
    	); 
    }	

    /**
     * 社員一覧の取得
     * @param session
     * @throws Exception
     * @return List<StaffMasterModel> 取得結果(行データ:StaffMasterModel のList)
     */
    private List<StaffMasterModel> getStaffMasterData(SqlSession session) throws Exception
    {
    	try {
    		InventoryLoanMapper mapper = session.getMapper(InventoryLoanMapper.class);
    	    
    	    // 社員マスター取得
    	    return mapper.getStaffMasterData();

    	} catch(Exception e){
    		throw new Exception(e);
    	}
    }

    /**
     * 追加機能：整合性検査
     * @param rows 
     * @brief 取得した備品データにてシリアル番号が重複している場合、警告MSGを発報する。<br>
     * 起動時に検査することを想定し、以降の処理を継続とする。<br>
     * 起動時に検査することを想定し、データの並び順(TableViewのカラム ソート)は考慮しない。<br>
     * ボタン押下時などカラム ソートの考慮が必要な場合は、[SortedList]の使用を要す。
     */
    private void CheckStockDataIntegrity(List<InventoryLoanDataModel> rows) {
    	
    	// シリアルNoの重複データを取得
    	List<AppConst.addRowNumData<InventoryLoanDataModel>> duplicateRows = 
    			super.detectInconsistenciesData(rows, "serialNo");
    	
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
    

    private Boolean isLoanableCheck(List<InventoryLoanDataModel> rows) {
    	Boolean result = false;
    	String message = "";
    	
    	this.dateNow = LocalDate.now();
    	
    	for (InventoryLoanDataModel row : rows)
    	{
    		message = "備品[シリアルNo: "+ row.getSerialNo() + " ]";
    		
    		// 社員マスターに登録されている社員以外を入力した場合
    		if (row.getStaffNo().equals(AppConst.UNSET_NUMBER_VALUE)) {
    			message += "の使用者が社員として登録されていません。";
    			break;
    		}
    		
    		LocalDate stDate =  null;
    		if( AppUtil.isDate(row.getStartDate()))
        	{
    			stDate = LocalDate.parse(row.getStartDate(),DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        	}
    				
    		if(stDate == null) {
    			message += "の貸出開始日が未入力です。";
    			break;
    		}
    		
    		if( stDate.isAfter(this.dateNow) ) {
    			message += "の貸出開始日が未入力です。";
    			break;
    		}
    		
    		if( stDate.isBefore(this.dateNow) ) {
    			// MSGBOX(確認)
    		}
    		
    		LocalDate ltDate =  null;
    		if( AppUtil.isDate(row.getLimitDate()))
        	{
    			ltDate = LocalDate.parse(row.getLimitDate(),DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        	}
    				
    		if(ltDate == null) {
    			message += "の返却予定日が未入力です。";
    			break;
    		}
    		
    		if( ltDate.isBefore(this.dateNow) ) {
    			message += "の返却予定日が過去日です。";
    			break;
    		}
    	}
    	
    	return result;
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
     * 警告Message：備品データ不整合(重複データ)
     * @param rowMessage
     */
    private void showMessageDuplicateStockData(String rowMessage) {
    	MessageBox.ShowWarnig(
    			"警告",
    			"データ異常：データ不整合 システム管理者に連絡してください。",
    			"備品(在庫)データに不整合(重複データ)が存在します。システム管理者に連絡してください。" + AppUtil.newLine() +
    			rowMessage);
    }
    
    /**
     * 警告Message：備品データなし
     * @param serialNo
     */
    private void showMessageUnknownStaff(String serialNo) {
    	MessageBox.ShowWarnig(
    			"警告",
    			"データ異常：備品データが存在しません。",
    			"備品[シリアルNo: "+ serialNo + " ]の備品データ(stock_data)が存在しません。" + AppUtil.newLine() +
    			"");
    }
    
    /**
     * TableView設定
     * @param kvpItems コンボボックスの選択リスト(kvpのObservableList)
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * 多分Webページと同じ概念。
     */
	private void tableViewSettings(ObservableList<keyValuePairItem<Integer, String>> kvpItems)
    {
    	System.out.println("備品貸出 カラム・セル設定/定義");
    	
    	// カラムBIND設定
    	tableListView.setBindColumnCallBack(this::callbackBindTableColumnSource);
    	
    	// カラム列移動(順番)可否
    	tableListView.setIsReorderabled(false);
    	
    	// TableView 編集可否設定
    	tableListView.setEditable(true);
   
    	// Cell 有効化制御
    	tableListView.cellIsEnabled(col_serial, false);
    	tableListView.cellIsEnabled(col_remarks, false);
    	
    	// 無効Cell Focus SKIP設定 
    	tableListView.onDisabledCellsFocusSkipEvent();

    	// 項目(カスタムセル)型設定
		col_staff_name.setCellTypeCustomComboBoxKeyValuesWithCheck(
				kvpItems, 
				"staffNo",
				"isCheckOut",
				"選択してください",
				true, 
				true);
		col_start_date.setCellTypeCustomDatePicker(true, true, null, null, dateNow);
		col_limit_date.setCellTypeCustomDatePicker(true, false, dateNow, dateNow, LocalDate.of(2099, 12, 31));
		col_is_checkout.setCellTypeCustomCheckBox(false, true);    	

    	// 選択動作 設定
    	tableListView.setIsMultiSelected(false);
    	tableListView.setIsCellSelected(true);
    	
    	// TableView[列]文字設定 
    	col_serial.getStyleClass().add("text-aligned");
    	col_staff_name.getStyleClass().add("center-aligned");
    	col_start_date.getStyleClass().add("center-aligned");
    	col_limit_date.getStyleClass().add("center-aligned"); 
    	col_remarks.getStyleClass().add("text-aligned");
    	col_is_checkout.getStyleClass().add("center-aligned");

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

    	// CheckBoxのカラム設定(TableView)    	
    	col_serial.setCellValueFactory( new PropertyValueFactory<>("serialNo"));
    	col_staff_name.setCellValueFactory( data -> data.getValue().staffNameProperty());
     	col_start_date.setCellValueFactory( data -> data.getValue().startDateProperty());
    	col_limit_date.setCellValueFactory( data -> data.getValue().limitDateProperty());
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));
    	col_is_checkout.setCellValueFactory( data -> data.getValue().isCheckOutProperty());
    }
    
    /**
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	System.out.println("備品貸出 画面初期化処理");
    	
    	lbl_title.setText(this.getPageTitle());
    	lbl_title.getStyleClass().add("titletext");
    }

    /**
     * データモデル(StaffMasterModel) kvpリスト変換処理
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * メソッド参照: FXCollections::observableArrayList<br>
     * ⇒ ラムダ式: () -> FXCollections.<>observableArrayList()
     * ⇒ Linq(あれば): () => new FXCollections.observableArrayList<>()
     */   
    private void modelsConvertToKeyValuePairList(
    		List<StaffMasterModel> datas,
    		ObservableList<keyValuePairItem<Integer, String>> kvpItems) {
     	
    	kvpItems.clear();
    	
        if (datas == null || datas.isEmpty()) { return; }

        datas.stream().
              map(row -> new keyValuePairItem<Integer, String>(row.getStaffNo(), row.getStaffName())).
              forEach(kvpItems::add);
    }
    
  
    
    
    
    
    
    
    
    
    
    
    
    @FXML
    /**
     * [所在確認]ボタン 押下イベント
     */
    public void onCountingButtonClicked() {
    	try {

    		// 選択行取得
        	InventoryLoanDataModel row = tableListView.
        			getSelectionModel().
        			getSelectedItem();

        	if (row == null) {
        	    // なにもしない
        	    return;
        	} 
 
        	System.out.println("選択行モデルデータ");
        	System.out.println("貸出者名: [" + row.getStaffName() + "] ");
        	System.out.println("貸出者ID: ["+ row.getStaffNo() + "]");
        	System.out.println("貸出開始日: ["+ row.getStartDate() + "]");
        	System.out.println("貸出開始日: ["+ row.getLimitDate() + "]");
        	System.out.println("貸出: ["+ row.getIsCheckOut() + "]"); 
    		
    		
    	} catch (Exception ex) {
    		System.err.println(ex);
    	}
   }    
    
 }