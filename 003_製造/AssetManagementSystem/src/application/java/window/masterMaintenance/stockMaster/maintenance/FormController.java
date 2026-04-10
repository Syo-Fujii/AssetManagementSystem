package application.java.window.masterMaintenance.stockMaster.maintenance;

import java.util.List;
import java.util.Objects;

import org.apache.ibatis.session.SqlSession;

import application.java.base.BaseFormPage;
import application.java.base.dbTablesModel.GenericCodeMasterModel;
import application.java.base.dbTablesModel.StockMasterModel;
import application.java.base.dbTablesModel.StockTypeMasterModel;
import application.java.common.AppConst;
import application.java.common.AppConst.GenericKey;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.MySqlManager;
import application.java.manager.customControl.CustomComboBoxControlManager;
import application.java.manager.customControl.CustomDatePickerControlManager;
import application.java.manager.customControl.CustomTextAreaManager;
import application.java.manager.customControl.CustomTextFieldControlManager;
import application.java.manager.customControl.CustomToggleButtonManager;
import application.resources.mapper.GenericCodeMasterMapper;
import application.resources.mapper.StockTypeMasterMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
    	// TextAreaの遷移は[Shift] + [Enter]とする 
    	this.txt_maker.isKeyPressToNext(true);
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
    		this.lbl_sub_title.setText("マスタデータ更新画面");
    		this.txt_serial_no.setDisable(true);
    		this.submit_button.setText("更新");

    		Platform.runLater(() -> cbo_type.requestFocus());
    	}
    	else
    	{
    		this.lbl_sub_title.setText("新規登録画面");
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
     * @brief 項目の[備品分類]を設定する 
     */   
    private void setupComboBox_StockType( List<StockTypeMasterModel> datas ) 
    {
    	cbo_type.setIsAddBlankRow(true);
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
     * 登録項目設定
     * @brief 備品マスタMODELを各登録項目の値に設定する。<br>
     * [備品分類]の設定はDB非同期処理の為、DB取得時に行う
     */
    private void setupControlsValue ()
    {   
    	this.txt_serial_no.setText( editMasterData.getSerialNo() );
    	this.txt_name.setText( editMasterData.getStockName() );
    	this.txt_model.setText( editMasterData.getModel() );
    	this.txt_maker.setText( editMasterData.getMaker() );
    	this.txt_remarks.setText( editMasterData.getRemarks() );

    	this.tgb_rent.setSelected( !editMasterData.getRentFlg() );
    	this.tgb_del.setSelected( editMasterData.getDelFlg() );
    	
    	this.txt_vendor_code.setText( editMasterData.getVendorCode() );
    	
    	this.dp_expiry_date.setValue( editMasterData.getExpiryDate() );
    	this.txt_price.setText( editMasterData.getPrice().toString() );
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
