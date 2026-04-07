package application.java.base.tableViewListModel;

import application.java.base.dbTablesModel.StockMasterModel;
import application.java.common.AppConst.LoanStatus;
import application.java.common.AppUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品マスタメンテナンス データクラス
 * @brief 備品マスタメンテナンス画面で表示する明細リストのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 * 備品マスタMODELを継承する<br>
 */
public class StockMasterDataModel  extends StockMasterModel {

	private StringProperty typeName;
	private StringProperty assetStatus;
	private StringProperty paymentStatus;
	private StringProperty vendorName;
	private StringProperty expiryDateString;
	private StringProperty rentStatus;
	
	
	/**
	* [備品分類名称]取得
	* @return typeName
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getTypeName() {
		return typeName.get();
	}

	/**
	* [備品分類名称]項目設定
	* @param typeName 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setTypeName(String name) {
		this.typeName.set(name);
	}		

	/**
	* [資産区分名]取得
	* @return assetStatus
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getAssetStatus() {
		return assetStatus.get();
	}

	/**
	* [資産区分名]項目設定
	* @param assetStatus 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setAssetStatus(String state) {
		this.assetStatus.set(state);
	}	
	
	/**
	* [支払区分名]取得
	* @return paymentStatus
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getPaymentStatus() {
		return paymentStatus.get();
	}

	/**
	* [支払区分名]項目設定
	* @param paymentStatus 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setPaymentStatus(String state) {
		this.paymentStatus.set(state);
	}	
	
	/**
	* [貸出会社名称]取得
	* @return vendorName
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getVendorName() {
		return vendorName.get();
	}

	/**
	* [貸出会社名称]項目設定
	* @param vendorName 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setVendorName(String name) {
		this.vendorName.set(name);
	}
	
	/**
	* [契約満了日(文字列)]取得
	* @return expiryDateString
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getExpiryDateString() {
		return expiryDateString.get();
	}
    
	/**
	* [契約満了日(文字列)]項目設定
	* @param expiryDateString 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setExpiryDateString(String date) {
		this.expiryDateString.set(date);
	}	

	/**
	* [貸出可否]取得
	* @return rentStatusと一致するEnum定数(LoanStatus)
	*/    
	public String getRentStatus() {
		// 取得した数値を、定数の文字列(Label)に変換・表示
		return LoanStatus.
				fromState(this.getRentValue()).getLabel();
	}

	/**
	* [貸出可否]取得(数値)
	* @return rentStatusを数値として取得する
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public Integer getRentValue() {
		// 取得した数値を、定数の文字列(Label)に変換・表示
		return AppUtil.parseInt(rentStatus.get(), 4);
	}	
	
	/**
	* [貸出可否]項目設定
	* @param state 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/   	
	public void setRentStatus(String state) {
		this.rentStatus.set(state);
	}	
	

	/**
    * コンストラクタ
    * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
    * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
    * private StringProperty itemName; と宣言しただけでは null のままで<br>
    * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
    * コンストラクタで、全プロパティの初期化を行う<br>
    * 備品マスタMODELを継承する<br>
    */
	public StockMasterDataModel() {
		
		super();

		this.typeName = new SimpleStringProperty("");
		this.assetStatus = new SimpleStringProperty("");
		this.paymentStatus = new SimpleStringProperty("");
		this.vendorName = new SimpleStringProperty("");
		this.expiryDateString = new SimpleStringProperty("");
		this.rentStatus = new SimpleStringProperty("");
	}
	
	/**
	 * 備品マスタMODEL 抽出処理
	 * @return 備品マスタMODEL
	 * @brief コピー用コンストラクタによる生成の為、複製したMODELを返す<br>
	 */
	public StockMasterModel extractStockMasterModel() {
	    return new StockMasterModel(this);
	}
}
