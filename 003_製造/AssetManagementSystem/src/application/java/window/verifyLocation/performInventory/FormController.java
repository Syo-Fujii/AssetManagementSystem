package application.java.window.verifyLocation.performInventory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.tableViewListModel.PerformInventoryDataModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
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

/**
 * 棚卸画面
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
	 * @brief エラーハンドリングは[setPage]となる。<br>
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
     * 当該が各画面(scene)の呼び出しイベント(FormLoad/FormShown)相当となる。<br>
	 * エラーハンドリングは[setPage](FXMLLoader.load)となる。
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
 
    /**
     * [棚卸]ボタン 押下イベント
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onInventoryButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [棚卸]ボタン押下");
    		
    		// [棚卸]が選択されている一覧を生成
        	List<PerformInventoryDataModel> availableRows = 
        			tableListView.
        			getItems().
        			stream().
        			filter(r -> r.getIsInventory()).
        			collect(Collectors.toList());
        	
        	if (availableRows.isEmpty()) { return; }

        	LogManager.writeDebug("[" + FORM_NAME + "] ： 棚卸チェック処理");
        	
        	AppConst.rowCheckResultData checkResult = isInventoryRowsCheck();
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
        	
        	// 棚卸実行確認
        	if( !showMessageIsInventoryItems(availableRows) ) { return; }
        	
        	// 棚卸処理(備品データ / 備品マスタ 更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 棚卸(更新)処理");
        	super.executeBulkQuery(availableRows);  		
    		
        	LogManager.writeDebug("[" + FORM_NAME + "] ： リスト再表示処理");
        	super.<PerformInventoryDataModel>fillTableAsync();

    	} catch (Exception ex) {
    		// 棚卸ボタン無効化
    		inventory_button.setDisable(true);
    		
    		String title = "[" + FORM_NAME + "] ： [棚卸]ボタン押下でエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }
    
    /**
     * [メニューに戻る]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
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
	 * 備品データ取得クエリ発行(棚卸データ取得)
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

        	Boolean isEmptyRecords = (listData == null || rows.isEmpty());

        	inventory_button.setDisable(isEmptyRecords);
    		
    		// 備品データ重複検査(不整合CHECK)
    		CheckStockDataIntegrity(rows);
    		
        	// TableView Focus指定
        	tableListView.setFocusFirstCell(col_is_inventory);        	

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
     * 備品データ更新クエリ発行(棚卸処理)
     * クエリ発行処理(Mapper:トランザクション処理：executeBulkQueryのMapper処理)
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
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品(棚卸)データ更新処理");
		
		PerformInventoryMapper mapper = session.getMapper(PerformInventoryMapper.class);

	    for (T data : listData) {
	    	PerformInventoryDataModel row = (PerformInventoryDataModel) data;
	    	
	    	// 最終所在確認日・更新
	    	mapper.updConfirmedDate( row );
	    	
    		// マスタの[備考]欄と明細の[備考]が違う場合
            if (!Objects.equals(row.getRemarks(), row.getRemarksMasterValue())) {
    	    	// マスタ備考・更新
            	mapper.updMasterRemarks( row );
            }		    
	    }
	
	    // Bulk処理にて一括更新を行うため、クエリ生成段階のResultとして常に[true]を返す。
		return true;
    }    	
	
    /**
     * DBクエリ(CUD)発行結果に応じた処理(executeBulkQueryの処理結果)
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
			sb.append("更新(棚卸)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
			
			showMessageException(title, sb.toString());
			
    		// 棚卸ボタン無効化
			inventory_button.setDisable(true);
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
    private void CheckStockDataIntegrity(List<PerformInventoryDataModel> rows) {
    	
    	// シリアルNoの重複データを取得
    	List<AppConst.addRowNumData<PerformInventoryDataModel>> duplicateRows = 
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
   	 *棚卸確認Message
   	 * @param rows 貸出するデータ(MODEL)のリスト
   	 * @return 確認結果
   	 */
    private Boolean showMessageIsInventoryItems(List<PerformInventoryDataModel> rows) 
    {
    	StringBuilder sb = new StringBuilder();
       	rows.forEach(r -> 
       	{
       		String sn = r.getSerialNo();
       		String id = r.getInventoryDate();

       		sb.append("シリアルNO :[").append(sn != null ? sn : "").append("] ");
       		sb.append("棚卸日 :[").append(id != null ? id : "").append("] ");
       		sb.append(AppUtil.newLine());
       	});

       	return MessageBox.ShowConfirmation(
       			ShowButtonType.YES_NO,
       			"棚卸確認",
       			"下記の備品の棚卸を行います。よろしいですか？",
       			sb.toString());
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
     * 棚卸データ 更新前チェック
     * @return AppConst.rowCheckResultData チェック結果
     */
    private AppConst.rowCheckResultData isInventoryRowsCheck() {
    	this.dateNow = LocalDate.now();
 
    	List<AppConst.addRowNumData<PerformInventoryDataModel>> rows = tableListView.getRowsAddNumber();
    	
    	if( rows == null || rows.isEmpty()) { 
   			return new AppConst.
					rowCheckResultData(
							false, 
							false, 
							AppConst.UNSET_NUMBER_VALUE,
							AppConst.UNSET_NUMBER_VALUE,
							"");
    	}
    	
    	for (AppConst.addRowNumData<PerformInventoryDataModel> row : rows)
    	{
    		int rowNum = row.rowNum();
    		PerformInventoryDataModel model = row.model();

    		if (!model.getIsInventory()) { continue; } 
    		
    		StringBuilder sb = 
    				new StringBuilder("備品[シリアルNo: ").append(model.getSerialNo()).append(" ]");
    		
    		// 日付チェック(棚卸日)
    		LocalDate stDate =  null;
    		if( AppUtil.isDate(model.getInventoryDate()))
        	{
    			stDate = AppUtil.parseDate(model.getInventoryDate());
        	}

    		if(!isValidateDateInput(stDate, "棚卸日", sb)) {
    			int colNum = tableListView.getColumns().indexOf(col_inventory_date);
    			return new AppConst.
    					rowCheckResultData(
    							false, 
    							!(AppUtil.StringIsNullOrEmpty(sb.toString())), 
    							rowNum, 
    							colNum, 
    							sb.toString());
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
     * @param sb StringBuilder メッセージ
     * @return 明細検査結果用データ(クラス:record)
     */
    private Boolean isValidateDateInput(LocalDate date,	String title, StringBuilder sb) {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： 日付チェック : [" + title + "]");
    	
    	StringBuilder checkSb = new StringBuilder();
    	
		// 必須チェック
		if(date == null) {
			checkSb.append("の").append(title).append("が未入力です。");
			sb.append(checkSb.toString());
			return false;
		} 	
		
		checkSb = new StringBuilder();
		// 未来日チェック
		if (date.isAfter(this.dateNow)) {
			checkSb.append("の").append(title).append("が未来日になっています");
			sb.append(checkSb.toString());
			return false;
		}
		
		return true;
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
    	col_remarks.setCellValueFactory(  data -> data.getValue().remarksProperty() );
    	col_inventory_date.setCellValueFactory(  data -> data.getValue().inventoryDateProperty() );
    	col_is_inventory.setCellValueFactory( data -> data.getValue().isInventoryProperty() );
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
    
    private void testRowSelectedValues() {
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
}
