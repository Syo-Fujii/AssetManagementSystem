package application.java.window.masterMaintenance.stockMaster.maintenance;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.BaseTableViewModel;
import application.java.base.dbTablesModel.GenericCodeMasterModel;
import application.java.base.dbTablesModel.StockDataModel;
import application.java.base.dbTablesModel.StockMasterModel;
import application.java.base.dbTablesModel.StockTypeMasterModel;
import application.java.common.AppConst;
import application.java.common.AppConst.ExcuteQueryResultStatus;
import application.java.common.AppConst.GenericKey;
import application.java.common.AppUtil;
import application.java.common.MessageBox;
import application.java.common.MessageBox.ShowButtonType;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.customControl.CustomComboBoxControlManager;
import application.java.manager.customControl.CustomDatePickerControlManager;
import application.java.manager.customControl.CustomTextAreaManager;
import application.java.manager.customControl.CustomTextFieldControlManager;
import application.java.manager.customControl.CustomToggleButtonManager;
import application.resources.mapper.GenericCodeMasterMapper;
import application.resources.mapper.StockDataMapper;
import application.resources.mapper.StockMasterMapper;
import application.resources.mapper.StockTypeMasterMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

public class FormController extends BaseFormPage {

	private final String FORM_NAME = "備品マスタメンテナンス";

	private StockMasterModel editMasterData = null;
	private Boolean isEditMode = false;	
	
	
	@FXML private AnchorPane pane_form;
	
	@FXML private Label lbl_title;
	@FXML private Label lbl_sub_title;
	
	@FXML private CustomTextFieldControlManager txt_serial_no;
	@FXML private CustomComboBoxControlManager<String> cbo_type;
	@FXML private CustomTextFieldControlManager txt_name;
	@FXML private CustomTextFieldControlManager txt_model;
	@FXML private CustomTextFieldControlManager txt_maker;
	@FXML private CustomTextAreaManager txt_remarks;
	@FXML private CustomToggleButtonManager tgb_rent;
	@FXML private CustomToggleButtonManager tgb_del;
	@FXML private CustomComboBoxControlManager<String> cbo_asset;
	@FXML private CustomTextFieldControlManager txt_vendor_code;
	@FXML private CustomDatePickerControlManager dp_expiry_date;
	@FXML private CustomComboBoxControlManager<String> cbo_pay_cycle;
	@FXML private CustomTextFieldControlManager txt_price;
	
	@FXML private Button submit_button;
	@FXML private Button back_button;
	
	
	/** 
	 * コンストラクタ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController() 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理");
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("StockMasterMaintenance"));
		this.setCssFile(AppUtil.MakeCssFilePath("StockMasterMaintenanceStyle"));
		
		this.setPageTitle(FORM_NAME);
	}
	/**
	 * コンストラクタ(引数付)
	 * @param isEdit 編集モード(更新処理)かの判定
	 * @param row 明細(一覧)で選択したデータ(行) / 新規登録時の初期化データ
	 * @brief エラーハンドリングは[setPage]となる。<br>
	 */
	public FormController(Boolean isEdit,  StockMasterModel row) {
		
		this();
		
		this.isEditMode = isEdit;
		this.editMasterData = row;
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
		
		// 画面起動設定
		this.formInitialize();

		// コンボボックスの生成(DB)
		makeComboBox_StockTypes();		
		makeComboBox_AssetTypes();
		makeComboBox_PayCycle();
		
		setupControlsValue();
    }

    /**
     * [メニューに戻る]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onBackButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [備品マスタ明細に戻る]ボタン押下"); 
    		
    		// 遷移元画面に切替
    		this.showOwnerPage();
    	
    	} catch ( Exception e) {
    		String title = "[" + FORM_NAME + "] ： 備品マスタ明細画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, e);
    	}
    }    

    /**
     * [登録] / [更新]ボタン 押下イベント 
     * @brief エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br> 
     */
    @FXML
    public void onSubmitButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [" + this.submit_button.getText() + "]ボタン押下"); 
    		
    		String execute = this.isEditMode ? "更新" : "登録" ;
    		
    		if ( isEditMode ) 
    		{
    			// 更新処理
        		if( !isEditControlsChanged() ) { return; }    			
    			
        		getEditControlsValue();
        		
    		} else {
        		// 登録処理
        		getEditControlsValue();   		
        		
            	AppConst.rowCheckResultData checkResult = isMasterRowsCheck();
            	if (!checkResult.result()) {
            		if( checkResult.isShowMsgBox()) {
            			this.showMessageInputError(checkResult.message());
            		}
            		setupErrorInputControlsFocus( checkResult.colNo() );
            		return;
            	}
    		}
    		
        	// 実行確認
        	if( !showMessageIsExecuting( execute, this.editMasterData.getDelFlg()) ) { return; }

        	// DB処理(備品マスタ登録 / 更新処理):データ操作のため垂直処理にて行う
        	LogManager.writeDebug("[" + FORM_NAME + "] ： 備品マスタ" + 
        	                       (!this.isEditMode ? "/ 備品データ ": " ") + execute + "処理");
           	if ( super.executeCudQuery( List.of(editMasterData)) ) {

           		showMessageDbExecuted( execute );

           		if ( !this.isEditMode ) { setupNewRecordMode(); } 
        	
           	} else {
        		this.submit_button.setDisable( true );
        	}

    	} catch ( Exception e) {
    		this.submit_button.setDisable( true );
    		String title = "[" + FORM_NAME + "] ： [" + this.submit_button.getText() + "]処理中にエラーが発生しました";
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
		this.submit_button.setDisable( true );
		LogManager.writeError("[" + FORM_NAME + "] ： DB 備品分類マスタ取得失敗");
		super.exceptionResult(exception);
    }

    /**
     * 備品マスタ更新クエリ発行
     * クエリ発行処理(Mapper:トランザクション処理：executeCudQueryのMapper処理)
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
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品マスタ更新処理");
		
		StockMasterMapper mapper = session.getMapper(StockMasterMapper.class);
		
		Integer resultCount = -1;
		StockMasterModel data = (StockMasterModel) listData.getFirst();
		
		if (this.isEditMode)
		{
			// 更新処理
			resultCount = mapper.updStockMasterOnes( data );
		} else {
			// 登録処理
			resultCount = mapper.insStockMasterOnes( data );
			
			if (resultCount == 1) {
				// 備品データ 新規登録
				StockDataMapper mapper_data = session.getMapper(StockDataMapper.class);
				resultCount = mapper_data.insStockDataOnes( new StockDataModel( data ) );
			} 
		}

		return resultCount == 1 ? true : false; 
    }    	
	
    /**
     * DBクエリ(CUD)発行結果に応じた処理(executeCudQueryの処理結果)
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
			sb.append("更新(備品マスタ)処理にて、結果件数が0件のクエリが発行されました。").append(AppUtil.newLine());
			sb.append("全ての更新処理を中断しています。").append(AppUtil.newLine());
			sb.append("システム管理者に連絡してください。" );
			
			showMessageException(title, sb.toString());
			
			this.submit_button.setDisable( true );
		}
	}	
    
    /**
     * 備品分類ComboBox 選択リスト取得処理
     */
	private void fetchStockTypes()
    {
		LogManager.writeTrace("備品分類(ComboBox) 選択リスト取得処理");
    	
    	MySqlManager.<StockTypeMasterModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getStockTypeMasterData(session);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> 
				{ 
    				setupComboBox_StockType( listData );
    				
    				// 非同期処理の為、取得成功時に選択の設定を行う
    		    	Integer stockTypeId = 
    		    			getStockTypeId(editMasterData.getStockType(),
    		    					       editMasterData.getStockCode());
    		    	
    		    	if ( stockTypeId == AppConst.UNSET_NUMBER_VALUE ) { return; }
    		    	
    		    	this.cbo_type.setSelectedItem( stockTypeId );   				
    			},
    			exception -> {
    				LogManager.writeError("備品分類(ComboBox)  選択リスト取得失敗");
    				super.exceptionResult(exception);
    			}
    	); 
    }

    /**
     * 資産区分・支払区分(汎用マスタ)ComboBox 選択リスト取得処理
     * @param genericKey 分類キー
     */
	private void fetchGenericCodes(String key)
    {
		GenericKey enumKey = AppConst.GenericKey.fromKey(key);

		LogManager.writeTrace("汎用マスタ(ComboBox) : " + enumKey.getComment() + " 選択リスト取得処理");
		
		CustomComboBoxControlManager<String> cbo = switch (enumKey) 
				{
		          case ASSET_TYPE -> this.cbo_asset;
		          case PAY_CYCLE  -> this.cbo_pay_cycle;
		          default          -> null;
		        };
		        
		if (cbo == null) { return; }
		
		MySqlManager.<GenericCodeMasterModel>FillOnParallel(
    			(SqlSession session) -> {
					try {
						return this.getGenericCodeMasterData(session, key);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				},
    			listData -> 
				{ 
					setupComboBox_GenericCode( cbo, listData );
    				
    				// 非同期処理の為、取得成功時に選択の設定を行う
    		    	Integer selectedKey = switch (enumKey) 
    						{
    				          case ASSET_TYPE -> editMasterData.getAssetType();
    				          case PAY_CYCLE  -> editMasterData.getPayCycle();
    				          default          -> AppConst.UNSET_NUMBER_VALUE;
    				        };
    				        
    				if (selectedKey == null || selectedKey == AppConst.UNSET_NUMBER_VALUE) { return; }
    				
    		    	cbo.setSelectedItem( selectedKey );   				
    			},
    			exception -> {
    				LogManager.writeError("汎用マスタ(ComboBox) : " + enumKey.getComment() + " 選択リスト取得失敗");
    				super.exceptionResult(exception);
    			}
    	); 
    }	
	
	/**
     * 備品分類一覧の取得
     * @param session
     * @throws Exception
     * @return List<StockTypeMasterModel> 取得結果(行データ:StockTypeMasterModel のList)
     */
    private List<StockTypeMasterModel> getStockTypeMasterData(SqlSession session) throws Exception
    {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品分類マスタ取得処理");
    	try {
    		StockTypeMasterMapper mapper = session.getMapper(StockTypeMasterMapper.class);
    	    
    	    // 備品分類マスター取得(削除データを含む)
    	    return mapper.selectAll();

    	} catch(Exception e){
    		throw new Exception(e);
    	}
    }	
	
	/**
     * 汎用マスタ一覧の取得
     * @param session
     * @param genericKey 分類キー
     * @throws Exception
     * @return List<GenericCodeMasterModel> 取得結果(行データ:GenericCodeMasterModel のList)
     */
    private List<GenericCodeMasterModel> getGenericCodeMasterData(SqlSession session, String genericKey) throws Exception
    {
    	LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品分類マスタ取得処理");
    	try {
    		GenericCodeMasterMapper mapper = session.getMapper(GenericCodeMasterMapper.class);
    	    
    	    // 汎用マスター取得(無効データを含む)
    	    return mapper.genericCodeMasterData(genericKey, null, null, null);

    	} catch(Exception e){
    		throw new Exception(e);
    	}
    }		

	/**
	 * 備品マスタ存在確認用クエリ発行処理(Mapper)
	 * @param <T> TableViewの行データのクラス(基底クラス[BaseTableViewModel]の継承クラス)
	 * @param session 継承元より渡される[SQLSession]
	 * @param data 発行するクエリの条件の値( 行データのクラス )
	 * @return 存在確認の結果
	 */
	private <T extends BaseTableViewModel> Boolean isExistsMasterData(SqlSession session, T data) {
		LogManager.writeDebug("[" + FORM_NAME + "] ： DB 備品マスタ存在確認");
		
		StockMasterMapper mapper = session.getMapper(StockMasterMapper.class);
		
		// 存在確認
		return  mapper.existsStockMaster( (StockMasterModel) data );
    }    
    
	/**
	 * 登録項目 値変更チェック処理
	 * @return boolean 判定結果
     * @brief 登録用の各Controlの値が、初期と違う(値を編集した)場合は[真]<br>
	 */
	private boolean isEditControlsChanged() {
    	Integer id  = getStockTypeId(this.editMasterData.getStockType(), this.editMasterData.getStockCode());
    	String name = this.editMasterData.getStockName();
    	String model = this.editMasterData.getModel();
    	String maker = this.editMasterData.getMaker();
    	String remarks = this.editMasterData.getRemarks();
    	
    	Boolean rent = !this.editMasterData.getRentFlg();
    	Boolean del = this.editMasterData.getDelFlg();
    	
    	Integer asset = this.editMasterData.getAssetType();
    	String vendor = this.editMasterData.getVendorCode();
    	LocalDate date = this.editMasterData.getExpiryDate();
    	Integer pay = this.editMasterData.getPayCycle();
    	Long price = this.editMasterData.getPrice();
    	
    	if ( !Objects.equals(cbo_type.getSelectedKey(), id) ) { return true; }	
    	if ( !Objects.equals(txt_name.getText(), name) ) { return true; }	
    	if ( !Objects.equals(txt_model.getText(), model) ) { return true; }	
    	if ( !Objects.equals(txt_maker.getText(), maker) ) { return true; }	
    	if ( !Objects.equals(txt_remarks.getText(), remarks) ) { return true; }
    
    	Boolean rowDataRentFlg = rent != null ? rent : false;
		if ( !Objects.equals(tgb_rent.isSelected(), rowDataRentFlg) ) { return true; }
    	Boolean rowDataDelFlg = del != null ? del : false;
		if ( !Objects.equals(tgb_del.isSelected(), rowDataDelFlg) ) { return true; }	
    	
    	Integer rowDataAsset = asset != null ? asset : AppConst.UNSET_NUMBER_VALUE;
    	if ( !Objects.equals(cbo_asset.getSelectedKey(), rowDataAsset) ) { return true; }	
    	if ( !Objects.equals(txt_vendor_code.getText(), vendor) ) { return true; }
    	if ( !Objects.equals(dp_expiry_date.getValue(), date) ) { return true; }
    	Integer rowDataPayCycle = pay != null ? pay : AppConst.UNSET_NUMBER_VALUE;
    	if ( !Objects.equals(cbo_pay_cycle.getSelectedKey(), rowDataPayCycle) ) { return true; }	
    	if ( !Objects.equals(txt_price.getValue(), price) ) { return true; }	

		return false;
	}    
	
	/**
	 *DB処理確認Message
	 * @return 確認結果
	 */
    private Boolean showMessageIsExecuting(String execute, boolean delFlg) {
    	String serialNo = this.editMasterData.getSerialNo();

    	StringBuilder sb = new StringBuilder();
		
    	if ( delFlg ) 
    	{
    		sb.append("※ 削除フラグを有効にした場合、貸出業務の対象外となります。");
    		sb.append(AppUtil.newLine());
    		sb.append("   現在貸出中の備品も対象外となります。よろしいですか？");
    		sb.append(AppUtil.newLine());
    		sb.append(AppUtil.newLine());
    	}
    	
		sb.append("シリアルナンバー :[").append(serialNo).append("] ");
		sb.append(AppUtil.newLine());

    	return MessageBox.ShowConfirmation(
    			ShowButtonType.YES_NO,
    			false,
    			execute + "確認",
    			"下記の備品マスタ" + 
    			 (!this.isEditMode ? "/備品データ": "") + 
    			 "の" + 
    			 execute + 
    			 "を行います。よろしいですか？",
    			sb.toString());
    }	

	/**
	 *DB処理完了Message
	 */
    private void showMessageDbExecuted(String execute) {
    	StringBuilder sb = new StringBuilder();
 		sb.append("シリアルナンバー :[").append(this.editMasterData.getSerialNo()).append("] ");
		sb.append(AppUtil.newLine());

    	MessageBox.ShowInformation(execute, execute + "しました。", sb.toString());
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
     * 画面初期設定
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     */
    private void formInitialize()
    {
    	LogManager.writeTrace("[" + FORM_NAME + "] ： 画面初期化処理");
    	
    	lbl_title.setText(this.getPageTitle());

    	// 登録項目 入力制限　設定
    	this.txt_serial_no.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, 20);
    	this.txt_name.setValidInput(null, 100);
    	this.txt_model.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, 100);
    	this.txt_maker.setValidInput(null, 100);
    	this.txt_vendor_code.setValidInput(AppConst.REGEX_ALPHA_NUMERIC, 20);
    	//this.txt_price.setValidInput(AppConst.REGEX_NUMERIC, null);

    	this.txt_price.isCurrency(true);
    	
    	
    	// 登録項目 Key押下Focus遷移　設定
    	this.txt_serial_no.isKeyPressToNext(true);
    	this.cbo_type.isKeyPressToNext(true);
    	this.txt_name.isKeyPressToNext(true);
    	this.txt_model.isKeyPressToNext(true);
    	this.txt_maker.isKeyPressToNext(true);

    	// TextAreaの遷移は[Shift] + [Enter]とする     	
    	this.txt_remarks.setNextControl( this.tgb_rent );
    	this.txt_remarks.isKeyPressToNext(true);
    	
    	this.tgb_rent.isKeyPressToNext(true);
    	this.tgb_del.isKeyPressToNext(true);
    	
    	this.cbo_asset.isKeyPressToNext(true);
    	this.txt_vendor_code.isKeyPressToNext(true);
    	this.dp_expiry_date.isKeyPressToNext(true);
    	this.cbo_pay_cycle.isKeyPressToNext(true);
    	this.txt_price.isKeyPressToNext(true);
    	
    	if (this.isEditMode) 
    	{
    		this.lbl_sub_title.setText("マスタ更新画面");
    		this.txt_serial_no.setDisable(true);
    		this.submit_button.setText("更新");

    		Platform.runLater(() -> cbo_type.requestFocus());
    	}
    	else
    	{
    		this.lbl_sub_title.setText("マスタ新規登録画面");
    		this.submit_button.setText("登録");
 
    		Platform.runLater(() -> txt_serial_no.requestFocus());
    	}
    }    

    /**
     * コンボボックス:備品分類　生成処理
     * @brief 項目の[備品分類]を設定する 
     */   
    private void makeComboBox_StockTypes() {
     	// データ取得・生成
     	fetchStockTypes();
    }    

    /**
     * コンボボックス:資産区分　生成処理
     * @brief 項目の[資産区分]を設定する 
     */   
    private void makeComboBox_AssetTypes() {
     	// データ取得・生成
    	fetchGenericCodes("ASSET_TYPE");
    }        
    
    /**
     * コンボボックス:資産区分　生成処理
     * @brief 項目の[資産区分]を設定する 
     */   
    private void makeComboBox_PayCycle() {
     	// データ取得・生成
    	fetchGenericCodes("PAY_CYCLE");
    }  
    
    /**
     * コンボボックス:備品分類 データ設定処理
     * @brief 項目の[備品分類]を設定する <br>
     * 設定させたい為、先頭を空白としない
     */   
    private void setupComboBox_StockType( List<StockTypeMasterModel> datas ) 
    {
    	//cbo_type.setIsAddBlankRow(true);
    	cbo_type.setDataSource_ModelList( datas, "stockTypeId", "stockTypeName", String.class );
    	
    	// 検索コンボボックスの初期位置(先頭 ≒ 空欄)
    	cbo_type.getSelectionModel().selectFirst();
    }    
    
    /**
     * コンボボックス:資産区分・支払区分(汎用マスタ) データ設定処理
     * @brief 項目の[資産区分] / [支払区分]を設定する 
     */   
    private void setupComboBox_GenericCode(
    		CustomComboBoxControlManager<String> cbo, 
    		List<GenericCodeMasterModel> datas ) 
    {
    	cbo.setIsAddBlankRow(true);
    	cbo.setDataSource_ModelList( datas, "typeKeyValue", "value1", String.class );
    	
    	// コンボボックスの初期位置(先頭 ≒ 空欄)
    	cbo.getSelectionModel().selectFirst();
    }        

    /**
     * 登録項目 新規モード設定
     */
    private void setupNewRecordMode()
    {
		this.isEditMode = false; 

    	// マスター登録用データ初期化
		this.editMasterData = new StockMasterModel();
		setupControlsValue();
    	
    	Platform.runLater(() -> txt_serial_no.requestFocus());
    }    
    
    /**
     * 登録項目設定
     * @brief 備品マスタMODELを各登録項目の値に設定する。<br>
     * [備品分類]の設定はDB非同期処理の為、DB取得時に行う
     */
    private void setupControlsValue ()
    {   
    	this.txt_serial_no.setText( editMasterData.getSerialNo() );
    	
    	this.cbo_type.setSelectedItem( AppConst.UNSET_NUMBER_VALUE );
    	
    	this.txt_name.setText( editMasterData.getStockName() );
    	this.txt_model.setText( editMasterData.getModel() );
    	this.txt_maker.setText( editMasterData.getMaker() );
    	this.txt_remarks.setText( editMasterData.getRemarks() );

    	this.tgb_rent.setSelected( !editMasterData.getRentFlg() );
    	this.tgb_del.setSelected( editMasterData.getDelFlg() );

    	this.cbo_asset.setSelectedItem( AppConst.UNSET_NUMBER_VALUE );
    	
    	this.txt_vendor_code.setText( editMasterData.getVendorCode() );
    	this.dp_expiry_date.setValue( editMasterData.getExpiryDate() );

    	this.cbo_pay_cycle.setSelectedItem( AppConst.UNSET_NUMBER_VALUE );

    	Long price = editMasterData.getPrice();
    	this.txt_price.setText( price != null ? price.toString() : "" );
    }

    /**
     * 登録項目 値取得処理
     * @brief [新規](登録)・[更新](明細行データの更新)に合わせて、各登録項目の値設定を行う<br>
     */
    private void getEditControlsValue() {
    	
    	if (!isEditMode) 
    	{
    		this.editMasterData.setSerialNo( txt_serial_no.getText() );
    	}

    	Pair<Integer, String> typeCodePair = getStockTypeCodePair( cbo_type.getSelectedKey() );
    	this.editMasterData.setStockType( typeCodePair.getKey() );
		this.editMasterData.setStockCode( typeCodePair.getValue() );
    	
    	this.editMasterData.setStockName( txt_name.getText() );
    	this.editMasterData.setModel( txt_model.getText());
    	this.editMasterData.setMaker( txt_maker.getText());
    	this.editMasterData.setRemarks( txt_remarks.getText());
    	
    	this.editMasterData.setRentFlg( !tgb_rent.isSelected() );
    	this.editMasterData.setDelFlg( tgb_del.isSelected() );
    	
    	this.editMasterData.setAssetType( cbo_asset.getSelectedKey() );
    	this.editMasterData.setVendorCode( txt_vendor_code.getText() );
    	this.editMasterData.setExpiryDate( dp_expiry_date.getValue() );
    	this.editMasterData.setPayCycle( cbo_pay_cycle.getSelectedKey() );
    	this.editMasterData.setPrice( txt_price.getValue() );
    }      
    
    
    /**
     * 備品分類ID取得処理
     * @param type 備品種別
     * @param code 備品コード
     * @return 備品分類ID
     * @brief ComboBoxで取得した一覧を用いて、[種別]、[コード]より[ID]を取得する。<br>
     */
    @SuppressWarnings("unchecked")
	private Integer getStockTypeId(Integer type, String code) {
    	if (this.cbo_type.getDataSource_ModelList() == null ) { return AppConst.UNSET_NUMBER_VALUE; }
    	
    	return ((List<StockTypeMasterModel>) this.cbo_type.getDataSource_ModelList()).
    			stream().
    			filter(model -> Objects.equals(model.getStockType(), type) && 
    					        Objects.equals(model.getStockCode(), code)).
    			findFirst().
    			map( StockTypeMasterModel::getStockTypeId ).
                orElse( AppConst.UNSET_NUMBER_VALUE );
    }

    /**
     * 備品分類・コード取得処理
     * @param id 備品ID
     * @return 備品分類ID
     * @brief ComboBoxで取得した一覧を用いて、[ID]より[種別]、[コード]を取得する。<br>
     */
    @SuppressWarnings("unchecked")
	private Pair<Integer, String> getStockTypeCodePair(Integer id) {
    	if (this.cbo_type.getDataSource_ModelList() == null ) { return new Pair<>(AppConst.UNSET_NUMBER_VALUE, ""); }
    	
    	return ((List<StockTypeMasterModel>) this.cbo_type.getDataSource_ModelList()).
    			stream().
    			filter(model -> Objects.equals(model.getStockTypeId(), id)).
    			findFirst().
    			map(model -> new Pair<>(model.getStockType(), model.getStockCode()) ).
                orElse( new Pair<>(AppConst.UNSET_NUMBER_VALUE, "") );
    }    

    /**
     * 入力エラー項目 Focus指定処理
     * @param colNo 対象Focus番号
     * @brief 登録項目でエラーがあった場合、登録前チェック処理:isMasterRowsCheckで<br>
     * 設定した項目番号(列番号)に一致するControlにFocusを遷移する。
     */
    private void setupErrorInputControlsFocus(Integer colNo)
    {
    	Platform.runLater(() -> 
    	{ 
    		switch (colNo)
    		{
    		 case 1:
    	    	txt_serial_no.requestFocus();
    	        break;
    		 case 2:
    			 cbo_type.requestFocus();
     	        break;
    		 default:
    	        break;
    	    }
    	});
    }        
    
    /**
     * 登録前チェック処理
     * @return AppConst.rowCheckResultData チェック結果
     */
    private AppConst.rowCheckResultData isMasterRowsCheck() throws Exception {
		StringBuilder sb = new StringBuilder();    	
    	
		// 必須確認：シリアルナンバー
		if ( AppUtil.StringIsNullOrWhiteSpace(this.editMasterData.getSerialNo()) ) {
			sb.append("[シリアルナンバー]が入力されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
		}	
    	
		/* -- 選択肢の空欄を除外(非選択はない)したためコメントアウト -- 
		// 必須確認：備品分類
		if ( this.cbo_type.getSelectedKey() < 1) {
			sb.append("[部品分類]が選択されていません。");
			return new AppConst.rowCheckResultData(false, true, -1, 2, sb.toString());
		}*/	

		
		// 存在確認
		if( super.executeNonQuery( this::isExistsMasterData, this.editMasterData)) {
			sb.append("既にシリアルナンバーが登録されています。");
    		sb.append(AppUtil.newLine());
    		sb.append("同一のシリアルナンバーは登録できません。");
			return new AppConst.rowCheckResultData(false, true, -1, 1, sb.toString());
		}

    	return new AppConst.rowCheckResultData(
    			true, 
    			false, 
    			AppConst.UNSET_NUMBER_VALUE,
    			AppConst.UNSET_NUMBER_VALUE,
    			"") ;
    }      
    
    /**
     * 遷移元画面呼び出し
     * @brief 遷移元画面(備品マスタ詳細：StockMasterMaintenanceDetalis)を呼出す。<br>
     */
    private void showOwnerPage() throws Exception {
    	// 遷移元画面に切替
    	super.setPage(
    			new application.
    			java.
    			window.
    			masterMaintenance.
    			stockMaster.
    			details.
    			FormController());
    }    
    
}
