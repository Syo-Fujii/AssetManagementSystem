package application.java.window.inventoryLoans.inventoryLoan;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.base.tableViewListModel.InventoryLoanDataModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.TableColumnManager;
import application.java.manager.TableColumnManager.colKeyValuePairItem;
import application.java.manager.TableViewManager;
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
	
	private final String FORM_NAME = "備品貸出画面";
	
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

	@FXML private Button submit_button;
	@FXML private Button back_button;
	
	private Integer stockType = 0;
	private String stockCode = "";
	private Integer previousPageWindowSize = 1;
	private LocalDate dateNow = null;

	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("InventoryLoan"));
		this.setCssFile(AppUtil.MakeCssFilePath("InventoryLoanStyle"));
		
		this.setPageTitle(FORM_NAME);
	
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
     * 当該が各画面(scene)の呼び出しイベント(FormLoad/FormShown)相当となる。<br>
	 * エラーハンドリングは[setPage](FXMLLoader.load)となる。
     */
    @FXML
	public void initialize() {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： controller initialize"); 
    	
    	// 最初の画面起動として、[SQL Session]を生成・保持する。
		MySqlManager.getSqlSessionFactory();

    	// 選択肢のリスト(空データ) ※ 非同期で取得する為、予め定義
     	ObservableList<colKeyValuePairItem<Integer, String>> comboBoxSource = 
     			FXCollections.observableArrayList();
		
		// TableView起動設定
		this.tableViewSettings(comboBoxSource);
		
		// 画面起動設定
		this.formInitialize();
 
		LogManager.writeDebug("[" + FORM_NAME + "] ： 使用者一覧(ComboBox 選択リスト)取得処理");
		this.fetchStaffMembers(comboBoxSource);

		LogManager.writeDebug("[" + FORM_NAME + "] ： リスト表示処理");
    	super.<InventoryLoanDataModel>fillTableAsync();

    }

    /**
     * [貸出]ボタン 押下イベント
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onLoanButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [貸出]ボタン押下");
    		
    		// [貸出]が選択されている一覧を生成
        	List<InventoryLoanDataModel> availableRows = 
        			tableListView.
        			getItems().
        			stream().
        			filter(r -> r.getIsCheckOut()).
        			collect(Collectors.toList());
        	
        	if (availableRows.isEmpty()) { return; }

        	LogManager.writeDebug("[" + FORM_NAME + "] ： 貸出チェック処理");
        	
        	AppConst.rowCheckResultData checkResult = isLoanableRowsCheck();
        	if (!checkResult.result()) {
        		if( checkResult.isShowMsgBox()) {
        			this.showMessageInputError(checkResult.message());
        		}
        		String content = "[" + FORM_NAME + "] ： 選択行: [" + checkResult.rowNum() + "] " +
        				                                "カラム番号 [" + checkResult.colNo() + "]";
        		LogManager.writeDebug(content);
        		tableListView.setCellFocus(checkResult.rowNum() -1, checkResult.colNo());
        		return;
        	}
        	
        	// 貸出実行確認
        	if( !showMessageIsCheckOutItems(availableRows) ) { return; }
        	
        	// 貸出処理(備品データ更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 貸出(更新)処理");
        	super.executeBulkQuery(availableRows);  		
    		
        	LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<InventoryLoanDataModel>fillTableAsync();

    	} catch (Exception ex) {
    		// 貸出ボタン無効化
    		submit_button.setDisable(true);
    		
    		String title = "[" + FORM_NAME + "] ： [貸出]ボタン押下でエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }
    
    /**
     * [戻る]ボタン 押下イベント
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
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
	 * 備品データ取得クエリ発行(在庫データ取得)
     * クエリ発行処理(Mapper)
     * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
     * @param session SQLセッション
     * @return List<T> 取得結果(行データ:T のList)
     * @brief controller内で用いるクエリ発行処理<br>
	 */
	@Override
    @SuppressWarnings("unchecked")
    protected <T extends BaseTableViewModel> List<T> executeMapperFunction(SqlSession session) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品(貸出)データ取得処理");
		
		InventoryLoanMapper mapper = session.getMapper(InventoryLoanMapper.class);
	    
	    // 備品詳細データ取得
	    return (List<T>) mapper.getTableReturnableStockData(this.stockType, this.stockCode);
    }
	
	/**
     * DB取得成功時の処理(非同期処理)
     * @brief controller内で用いる取得成功時の処理<br>
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected <T extends BaseTableViewModel> void successResult(List<T> listData) {
		LogManager.writeTrace("[" + FORM_NAME + "] ： 備品(貸出)データ連携(BIND)処理");

		// データ設定(BIND・SELL設定値)を初期化
		tableListView.dataSourceClear();
		
		List<InventoryLoanDataModel> rows = (List<InventoryLoanDataModel>) listData;
		
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
    	tableListView.setFocusFirstCell(col_staff_name);
    }
    
    /**
     * DB取得失敗(例外発生)時の処理(非同期処理)
     * @brief controller内で用いる取得例外処理>
     */
	@Override
	protected void exceptionResult(Throwable exception)
    {
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品(貸出)データ取得失敗");
		super.exceptionResult(exception);
    }

    /**
     * 備品データ更新クエリ発行(貸出処理)
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
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品(貸出)データ更新処理");
		
		InventoryLoanMapper mapper = session.getMapper(InventoryLoanMapper.class);

	    for (T row : listData) {
	    	mapper.updStockDataLoanOut( (InventoryLoanDataModel) row );
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
			
    		// 貸出ボタン無効化
    		submit_button.setDisable(true);
		}
	}		
	
    /**
     * 使用者Cell(ComboBox) 選択リスト取得処理
     * @param comboBoxSource コンボボックスの選択リスト(kvpのObservableList)
     * @brief コンボBOXのSource更新・差し替えのため、予めSourceとして設定したListを引数で受ける。<br>
     */
    private void fetchStaffMembers(
    		ObservableList<colKeyValuePairItem<Integer, String>> comboBoxSource)
    {
    	System.out.println("使用者CELL(ComboBox) 選択リスト取得処理");
    	
    	MySqlManager.<StaffMasterModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getStaffMasterData(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> { 
    				staffMasterModelsConvertToKeyValuePairList( listData, comboBoxSource ); },
    			exception -> {
    				System.err.println("使用者CELL(ComboBox) 選択リスト取得失敗");
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
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 社員マスタ取得処理");
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
     * 貸出データ 更新前チェック
     * @return AppConst.rowCheckResultData チェック結果
     */
    private AppConst.rowCheckResultData isLoanableRowsCheck() {
    	
    	this.dateNow = LocalDate.now();
    	
    	List<AppConst.addRowNumData<InventoryLoanDataModel>> rows = 
    			tableListView.getRowsAddNumber();
    	
    	if( rows == null || rows.isEmpty()) { 
   			return new AppConst.
					rowCheckResultData(
							false, 
							false, 
							AppConst.UNSET_NUMBER_VALUE,
							AppConst.UNSET_NUMBER_VALUE,
							"");
    	}
    	
    	for (AppConst.addRowNumData<InventoryLoanDataModel> row : rows)
    	{
    		int rowNum = row.rowNum();
    		InventoryLoanDataModel model = row.model();

    		if (!model.getIsCheckOut()) { continue; } 
    		
    		StringBuilder sb = 
    				new StringBuilder("備品[シリアルNo: ").append(model.getSerialNo()).append(" ]");
	
    		// 社員マスターに登録されている社員以外を入力した場合
    		if (model.getStaffNo().equals(AppConst.UNSET_NUMBER_VALUE)) {
    			sb.append("の使用者が社員として登録されていません。");
    			int colNum = tableListView.getColumns().indexOf(col_staff_name);
    			return new AppConst.rowCheckResultData(false, true, rowNum, colNum, sb.toString());
    		}
    		
    		// 日付チェック(貸出開始日)
    		LocalDate stDate =  null;
    		if( AppUtil.isDate(model.getStartDate()))
        	{
    			stDate = AppUtil.parseDate(model.getStartDate());
        	}
    		if(!isValidateDateInput(stDate, "貸出開始日", sb, true, true, false, false)) {
    			int colNum = tableListView.getColumns().indexOf(col_start_date);
    			return new AppConst.
    					rowCheckResultData(
    							false, 
    							!(AppUtil.StringIsNullOrEmpty(sb.toString())), 
    							rowNum, 
    							colNum, 
    							sb.toString());
    		}
    		
    		// 日付チェック(返却予定日)
    		LocalDate ltDate =  null;
    		if( AppUtil.isDate(model.getLimitDate()))
        	{
    			ltDate = AppUtil.parseDate(model.getLimitDate());
        	}
    		if(!isValidateDateInput(ltDate, "返却予定日", sb, false, false, true, true)) {
    			int colNum = tableListView.getColumns().indexOf(col_limit_date);
    			return new AppConst.
    					rowCheckResultData(
    							false, 
    							!(AppUtil.StringIsNullOrEmpty(sb.toString())), 
    							rowNum, 
    							colNum, 
    							sb.toString());
    		}   		
    		
    		// 日付整合性チェック
    		if(stDate.isAfter(ltDate))
    		{
      			sb.append("の貸出開始日が返却予定日以降になっています。");
    			int colNum = tableListView.getColumns().indexOf(col_start_date);
    			return new AppConst.rowCheckResultData(false, true, rowNum, colNum, sb.toString());
    		}
    	}
    	
    	return new AppConst.rowCheckResultData(
    			true, 
    			false, 
    			AppConst.UNSET_NUMBER_VALUE,
    			AppConst.UNSET_NUMBER_VALUE,
    			"") ;
    }
    
    /**
     * 日付チェック
     * @param date LocalDate 対象日付
     * @param title String 項目名称
     * @param message String メッセージ
     * @param isAllowPastDate Boolean 過去日を許可するか("真"の場合、許可)
     * @param isBeforeConfCheck Boolean 過去日の場合、確認MSGを呼出すか
     * @param isAllowFutureDate Boolean 未来日を許可するか("真"の場合、許可)
     * @param isOneYearAfterCheck Boolean 未来日の場合、確認MSGを呼出すか
     * @return 明細検査結果用データ(クラス:record)
     * @brief 当該メソッドにて確認MessageBoxを表示し[いいえ](cancel)を選択した場合は、messageをnullにする。
     */
    private Boolean isValidateDateInput(
    		LocalDate date, 
    		String title, 
    		StringBuilder sb,
    		Boolean isAllowPastDate,
    		Boolean isBeforeConfCheck,
    		Boolean isAllowFutureDate,
    		Boolean isOneYearAfterCheck) {
		   	
    	LogManager.writeTrace("[" + FORM_NAME + "] ： 日付チェック : [" + title + "]");
    	
    	String message = sb.toString();
    	StringBuilder checkSb = new StringBuilder();
    	
		// 必須チェック
		if(date == null) {
			checkSb.append("の").append(title).append("が未入力です。");
			sb.append(checkSb.toString());
			return false;
		} 	
		
		// 過去日チェック
		Boolean isBefore = date.isBefore(this.dateNow);
		checkSb.append("の").append(title).append("が").append(AppUtil.newLine());
		checkSb.append("過去日になっています");
		if (isBeforeConfCheck && isBefore) {
			checkSb.append("が");
			
			// 確認メッセージ呼び出し
			if (! this.showMessageConfimed( message + checkSb.toString() )) {
				sb.setLength(0);
				return false;
			}

		} else if (!isAllowPastDate && isBefore) {
			// エラーとする
			sb.append(checkSb.append("。").toString());
			return false;
		}
		
		checkSb = new StringBuilder();
		
		// 未来日チェック
		Boolean isAfter = date.isAfter(this.dateNow);
		Boolean isOneYearAfter = date.isAfter(this.dateNow.plusYears(1));
		if (isOneYearAfterCheck && isOneYearAfter) {
			checkSb.append("の").append(title).append("が").append(AppUtil.newLine());
			checkSb.append("1年以上先になっています。").append(AppUtil.newLine());
			checkSb.append("1年おきに棚卸確認が必要になりますが、").append(AppUtil.newLine());
			
			// 確認メッセージ呼び出し
			if (! this.showMessageConfimed( message + checkSb.toString())) {
				sb.setLength(0);
				return false;
			}
		} else if (!isAllowFutureDate && isAfter) {
			checkSb.append("の").append(title).append("が未来日になっています");
			sb.append(checkSb.toString());
			return false;
		}
		
		return true;
    }
 
	/**
	 *貸出確認Message
	 * @param rows 貸出するデータ(MODEL)のリスト
	 * @return 確認結果
	 */
    private Boolean showMessageIsCheckOutItems(List<InventoryLoanDataModel> rows) {
    	StringBuilder sb = new StringBuilder();
    	
    	rows.forEach(r -> 
    	{
    		String sn = r.getSerialNo();
    		String st = r.getStartDate();
    		String lt = r.getLimitDate();

    		sb.append("シリアルNO :[").append(sn != null ? sn : "").append("] ");
    		sb.append("貸出開始日 :[").append(st != null ? st : "").append("] ");
    		sb.append("返却予定日 :[").append(lt != null ? lt : "").append("] ");
    		sb.append(AppUtil.newLine());
    	});

    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			"貸出確認",
    			"下記の備品の貸出を行います。よろしいですか？",
    			sb.toString());
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
     * 例外Message：入力エラー
     */
    private void showMessageInputError(String message) {
    	MessageBox.ShowErrorMessage("入力エラー", "", message);
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
	private void tableViewSettings(ObservableList<colKeyValuePairItem<Integer, String>> kvpItems)
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
		col_limit_date.setCellTypeCustomDatePicker(true, true, dateNow, dateNow, LocalDate.of(2099, 12, 31));
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
     *            setCellValueFactory( new PropertyValueFactory<>([データModelのプロパティ名]:String文字列)<br>
     *  入力項目の場合：プロパティをそのまま渡すゲッター<br>
     *  (StringProperty staffNameProperty() { return this.staffName; })がMODELに必要<br>
     *            setCellValueFactory( data -> data.getValue().staffNameProperty());
     */
    private void callbackBindTableColumnSource() {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： カラムBIND設定");
    	
    	col_serial.setCellValueFactory( new PropertyValueFactory<>("serialNo"));
    	col_remarks.setCellValueFactory( new PropertyValueFactory<>("remarks"));

    	// カラム設定(Cell.valueとmodelのBIND)
    	col_staff_name.setCellValueFactory( data -> data.getValue().staffNameProperty());
     	col_start_date.setCellValueFactory( data -> data.getValue().startDateProperty());
    	col_limit_date.setCellValueFactory( data -> data.getValue().limitDateProperty());
    	col_is_checkout.setCellValueFactory( data -> data.getValue().isCheckOutProperty());
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
     * データモデル(StaffMasterModel) kvpリスト変換処理
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * メソッド参照: FXCollections::observableArrayList<br>
     * ⇒ ラムダ式: () -> FXCollections.<>observableArrayList()
     * ⇒ Linq(あれば): () => new FXCollections.observableArrayList<>()
     */   
    private void staffMasterModelsConvertToKeyValuePairList(
    		List<StaffMasterModel> datas,
    		ObservableList<colKeyValuePairItem<Integer, String>> kvpItems) {
     	
    	col_staff_name.
    	rowsConvertToKeyValuePairList(datas, "staffNo", "staffName", kvpItems);
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
    
 }