package application.java.base.tableViewListModel;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst.LoanStatus;
import application.java.common.AppUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品詳細データクラス
 * @brief 備品詳細画面で表示する明細リストのデータ
 * ※ １行分のデータ
 * 画面Controllerにて呼び出される前提
 */
public class InventoryDetailsDataModel extends BaseTableViewModel {

	private StringProperty typeName;
	private StringProperty serialNo;
	private StringProperty staffName;
    /* DataViewからの取得の場合を考慮しString型とする */
	private StringProperty rentFlg;
	private StringProperty startDate;
	private StringProperty limitDate;
	private StringProperty confirmedDate;
    private StringProperty model;
    private StringProperty maker;
    private StringProperty destinationSerialNo;
    private StringProperty type;
	private StringProperty leaseDate;
    private StringProperty remarks;
    private IntegerProperty stockDataId;

	/**
	* [分類名称]取得
	* @return typeName
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getTypeName() {
		return typeName.get();
	}

	/**
	* [分類名称]項目設定
	* @param typeName 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setTypeName(String name) {
		this.typeName.set(name);
	}    
    
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
	* [使用者]取得
	* @return staff Name
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public String getStaffName() {
		return staffName.get();
	}

	/**
	* [使用者]項目設定
	* @param staff Name 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/   	
	public void setStaffName(String name) {
		this.staffName.set(name);
	}

	/**
	* [貸出可否]取得
	* @return rentFlgと一致するEnum定数(LoanStatus)
	*/    
	public String getRentFlg() {
		// 取得した数値を、定数の文字列(Label)に変換・表示
		return LoanStatus.
				fromState(this.getRentValue()).getLabel();
	}

	/**
	* [貸出可否]取得(数値)
	* @return rentFlgを数値として取得する
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public Integer getRentValue() {
		// 取得した数値を、定数の文字列(Label)に変換・表示
		return AppUtil.parseInt(rentFlg.get(), 4);
	}	
	
	/**
	* [貸出可否]項目設定
	* @param flg 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/   	
	public void setRentFlg(String flg) {
		this.rentFlg.set(flg);
	}

	/**
	* [貸出開始日]取得
	* @return startDate
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    	
	public String getStartDate() {
		return startDate.get();
	}

	/**
	* [貸出開始日]項目設定
	* @param startDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/  	
	public void setStartDate(String startDate) {
		this.startDate.set(startDate);
	}

	/**
	* [返却予定日]取得
	* @return limitDate
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/ 
	public String getLimitDate() {
		return limitDate.get();
	}

	/**
	* [返却予定日]項目設定
	* @param limitDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLimitDate(String limitDate) {
		this.limitDate.set(limitDate);;
	}

	/**
	* [最終所在確認日]取得
	* @return confirmedDate
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/ 	
	public String getConfirmedDate() {
		return confirmedDate.get();
	}

	/**
	* [最終所在確認日]項目設定
	* @param confirmedDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/	
	public void setConfirmedDate(String confirmedDate) {
		this.confirmedDate.set(confirmedDate);
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
	* [分類]取得
	* @return type
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public String getType() {
		return type.get();
	}
	
	/**
	* [分類]項目設定
	* @param type 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setType(String type) {
		this.type.set(type);
	}	
	
	/**
	* [接続先PCシリアルNo]取得
	* @return destinationSerialNo
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public String getDestinationSerialNo() {
		return destinationSerialNo.get();
	}

	/**
	* [接続先PCシリアルNo]項目設定
	* @param model 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/	
	public void setDestinationSerialNo(String destinationSerialNo) {
		this.destinationSerialNo.set(destinationSerialNo);
	}

	/**
	* [リース返却予定日]項目取得
	* @return leaseDate
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/	
	public String getLeaseDate() {
		return leaseDate.get();
	}

	/**
	* [リース返却予定日]項目設定
	* @param leaseDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLeaseDate(String leaseDate) {
		this.leaseDate.set(leaseDate);
	}

	/**
	* [備考]項目取得
	* @return remarks
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
	* [備品データ ID]項目取得
	* @return stockDataId
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public Integer getStockDataId() {
		return stockDataId.get();
	}

	/**
	* [備品データ ID]項目設定
	* @param stockDataId 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setStockDataId(Integer id ) {
		this.stockDataId.set(id);
	}

	
	/**
    * コンストラクタ
    * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
    * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
    * private StringProperty itemName; と宣言しただけでは null のままで<br>
    * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
    * コンストラクタで、全プロパティの初期化を行う。
    */
	public InventoryDetailsDataModel(){
		this.typeName = new SimpleStringProperty("");
		this.serialNo = new SimpleStringProperty("");
	    this.staffName = new SimpleStringProperty("");
	    this.rentFlg = new SimpleStringProperty("");
	    this.startDate = new SimpleStringProperty("");
	    this.limitDate = new SimpleStringProperty("");
	    this.confirmedDate = new SimpleStringProperty("");
	    this.model = new SimpleStringProperty("");
	    this.maker = new SimpleStringProperty("");
	    this.destinationSerialNo = new SimpleStringProperty("");
	    this.type = new SimpleStringProperty("");
	    this.leaseDate = new SimpleStringProperty("");
	    this.remarks = new SimpleStringProperty("");
	    this.stockDataId = new SimpleIntegerProperty();
	    		
	 }
}
