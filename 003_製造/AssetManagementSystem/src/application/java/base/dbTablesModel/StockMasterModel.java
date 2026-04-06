package application.java.base.dbTablesModel;

import java.time.LocalDate;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品マスタークラス
 * @brief 備品マスタのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class StockMasterModel extends BaseTableViewModel {
	
	private StringProperty serialNo;
	private StringProperty name;
    private StringProperty model;
    private StringProperty maker;
	private IntegerProperty stockType;
	private StringProperty stockCode;
	private BooleanProperty rentFlg;
	private IntegerProperty assetType;
	private StringProperty vendorCode;
	private ObjectProperty<LocalDate> expiryDate;
	private IntegerProperty payCycle;
	private LongProperty price;
    private StringProperty remarks;
	private BooleanProperty del;

	
	/**
	* [シリアルNo]取得
	* @return serialNo
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getSerialNo() {
		return serialNo.get();
	}

	/**
	* [シリアルNo]項目設定
	* @param type 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setSerialNo(String no) {
		this.serialNo.set(no);
	}	

	/**
	 * [備品名称]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockName]とする<br>
	 */
	public String getStockName() {
		return name.get();
	}

	/**
	 * [備品名称]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockName]とする<br>
	 */
	public void setStockName(String name) {
		this.name.set(name);
	}

	/**
	* [製品名]取得
	* @return model
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public String getModel() {
		return model.get();
	}

	/**
	* [製品名]項目設定
	* @param model 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setModel(String model) {
		this.model.set(model);;
	}

	/**
	* [メーカー]取得
	* @return maker
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public String getMaker() {
		return maker.get();
	}	

	/**
	* [メーカー]項目設定
	* @param model 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setMaker(String maker) {
		this.maker.set(maker);;
	}
	
	/**
	 * [分類種別]取得
	 * @return stockType
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockType]とする<br>
	 */
	public Integer getStockType() {
		return stockType.get();
	}
	
	/**
	 * [分類種別]項目設定
	 * @param stockType 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockType]とする<br>
	 */
	public void setStockType(Integer stockType) {
		this.stockType.set(stockType);
	}
	
	/*
	 * [分類コード]取得
	 * @return stockCode
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockCode]とする<br>
	 */
	public String getStockCode() {
		return stockCode.get();
	}

	/**
	 * [分類コード]項目設定
	 * @param stockCode 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockCode]とする<br>
	 */
	public void setStockCode(String stockCode) {
		this.stockCode.set(stockCode);
	}

	/**
	* [貸出可否]取得
	* @return rentFlgを取得する
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public Boolean getRentFlg() {
		// 取得した数値を、定数の文字列(Label)に変換・表示
		return rentFlg.get();
	}	
	
	/**
	* [貸出可否]項目設定
	* @param flg 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/   	
	public void setRentFlg(Boolean flg) {
		this.rentFlg.set(flg);
	}	
	
	/**
	 * [資産区分]取得
	 * @return assetType
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public Integer getAssetType() {
		return assetType.get();
	}
	
	/**
	 * [資産区分]項目設定
	 * @param assetType 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public void setAssetType(Integer accseType) {
		this.assetType.set(accseType);
	}	

	/**
	 * [貸出会社コード]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public String getVendorCode() {
		return vendorCode.get();
	}

	/**
	 * [貸出会社コード]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public void setVendorCode(String code) {
		this.vendorCode.set(code);
	}	
	
	/**
	* [契約満了日]項目取得
	* @return expiryDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getExpiryDate() {
		return expiryDate.get();
	}

	/**
	* [契約満了日]項目設定
	* @param expiryDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setExpiryDate(LocalDate date) {
		this.expiryDate.set(date);
	}	
	
	/**
	 * [支払区分]取得
	 * @return payCycle
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public Integer getPayCycle() {
		return payCycle.get();
	}
	
	/**
	 * [支払区分]項目設定
	 * @param payCycle 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public void setPayCycle(Integer pay) {
		this.payCycle.set(pay);
	}		
	
	/**
	 * [料金]取得
	 * @return price
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public Long getPrice() {
		return price.get();
	}
	
	/**
	 * [料金]項目設定
	 * @param price 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public void setPrice(Long price) {
		this.price.set(price);
	}		

	/**
	* [備考]項目取得
	* @param remarks 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public String getRemarks() {
		return remarks.get();
	}

	/**
	* [備考]項目設定
	* @param remarks 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setRemarks(String remarks) {
		this.remarks.set(remarks);
	}
	
	/**
	 * [削除フラグ]取得
	 * @return del
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[delFlg]とする<br>
	 */
	public Boolean getDelFlg() {
		return del.get();
	}

	 /**
	 * [削除フラグ]項目設定
	 * @param isdeleted 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[delFlg]とする<br>
	 */
	public void setDelFlg(Boolean isdeleted) {
		this.del.set(isdeleted);
	}	 

	/**
    * コンストラクタ
    * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
    * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
    * private StringProperty itemName; と宣言しただけでは null のままで<br>
    * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
    * コンストラクタで、全プロパティの初期化を行う。
    */
	 public StockMasterModel(){
		 this.serialNo = new SimpleStringProperty("");
		 this.name = new SimpleStringProperty("");
		 this.model = new SimpleStringProperty("");
		 this.maker = new SimpleStringProperty("");
		 this.stockType = new SimpleIntegerProperty(AppConst.UNSET_NUMBER_VALUE);
		 this.stockCode = new SimpleStringProperty("");
		 this.rentFlg = new SimpleBooleanProperty(false);
		 this.assetType = new SimpleIntegerProperty(AppConst.UNSET_NUMBER_VALUE);
		 this.vendorCode = new SimpleStringProperty("");
		 this.expiryDate  = new SimpleObjectProperty<>();
		 this.payCycle = new SimpleIntegerProperty(AppConst.UNSET_NUMBER_VALUE);
		 this.price = new SimpleLongProperty();
		 this.remarks = new SimpleStringProperty("");
		 this.del = new SimpleBooleanProperty(false);		 
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public StockMasterModel(StockMasterModel source) {
	     this();   
		 
		 this.setSerialNo( source.getSerialNo( ));
		 this.setStockName( source.getStockName());
		 this.setModel( source.getModel() );
		 this.setMaker( source.getMaker() );
		 this.setStockType( source.getStockType() );
		 this.setStockCode( source.getStockCode() );
		 this.setRentFlg( source.getRentFlg() );
		 this.setAssetType( source.getAssetType() );
		 this.setVendorCode( source.getVendorCode() );
		 this.setExpiryDate( source.getExpiryDate() );
		 this.setPayCycle( source.getPayCycle() );
		 this.setPrice( source.getPrice() );
		 this.setRemarks( source.getRemarks() );
		 this.setDelFlg( source.getDelFlg() );
	 }
}
