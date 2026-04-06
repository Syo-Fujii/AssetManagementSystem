package application.java.base.tableViewListModel;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst.LoanStatus;
import application.java.common.AppUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 棚卸データクラス
 * @brief 棚卸画面で表示する明細リストのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class PerformInventoryDataModel extends BaseTableViewModel {

	private StringProperty serialNo;
	private StringProperty typeName;
	private StringProperty model;
    /* DataViewからの取得の場合を考慮しString型とする */
	private StringProperty rentFlg;
    private StringProperty staffName;
	private StringProperty startDate;
	private StringProperty limitDate;
	private StringProperty confirmedDate;
	private StringProperty remarks;
	private StringProperty inventoryDate;
    private BooleanProperty isInventory;

	private StringProperty remarksMasterValue;
    private IntegerProperty stockDataId;

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
	* [備考] Property取得 (TableViewとの連動に必須)
	* @return remarks (Property)
	* @brief [javafx.beans.property]
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* 値に応じて、TableViewの表示を変更(連動)させる為に必須<br>
	* ≒ 明細の入力項目の場合はプロパティを渡すゲッターを用意する
	*/
	public StringProperty remarksProperty() {
	    return remarks;
	}
	
	/**
	* [棚卸日]取得
	* @return inventoryDate
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/ 	
	public String getInventoryDate() {
		return inventoryDate.get();
	}

	/**
	* [棚卸日]項目設定
	* @param inventoryDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/	
	public void setInventoryDate(String inventoryDate) {
		this.inventoryDate.set(inventoryDate);
	}

	/**
	* [棚卸日] Property取得 (TableViewとの連動に必須)
	* @return inventoryDate (Property)
	* @brief [javafx.beans.property]
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* 値に応じて、TableViewの表示を変更(連動)させる為に必須<br>
	* ≒ 明細の入力項目の場合はプロパティを渡すゲッターを用意する
	*/
	public StringProperty inventoryDateProperty() {
	    return inventoryDate;
	}		
	
	/**
	* [棚卸]項目取得
	* @return isInventory
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public Boolean getIsInventory() {
		return isInventory.get();
	}

	/**
	* [棚卸]項目設定
	* @param isInventory 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setIsInventory(Boolean checked) {
		this.isInventory.set(checked);
	}

	/**
	* [棚卸] Property取得 (TableViewとの連動に必須)
	* @return isInventory (Property)
	* @brief [javafx.beans.property]
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* 値に応じて、TableViewの表示を変更(連動)させる為に必須<br>
	* ≒ 明細の入力項目の場合はプロパティを渡すゲッターを用意する
	*/
	public BooleanProperty isInventoryProperty() {
	    return isInventory;
	}	
	
	/**
	* [備考](更新対象比較用)項目取得
	* @return remarksMasterValue
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* ※ DBとBINDする場合は取得時のカラム名を[remarksMasterValue]とする<br>
	*/		
	public String getRemarksMasterValue() {
		return remarksMasterValue.get();
	}

	/**
	* [備考](更新対象比較用)項目設定
	* @param remarksMasterValue 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* ※ DBとBINDする場合は取得時のカラム名を[remarksMasterValue]とする<br>
	*/
	public void setRemarksMasterValue(String remarks) {
		this.remarksMasterValue.set(remarks);
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
	public PerformInventoryDataModel(){
		this.serialNo = new SimpleStringProperty("");
		this.typeName = new SimpleStringProperty("");
	    this.model = new SimpleStringProperty("");
	    this.rentFlg = new SimpleStringProperty("");
		this.staffName = new SimpleStringProperty("");
	    this.startDate = new SimpleStringProperty("");
	    this.limitDate = new SimpleStringProperty("");
	    this.confirmedDate = new SimpleStringProperty("");
	    this.remarks = new SimpleStringProperty("");
	    this.inventoryDate = new SimpleStringProperty("");
	    this.isInventory = new SimpleBooleanProperty(false);
	    
	    this.remarksMasterValue = new SimpleStringProperty("");
	    this.stockDataId = new SimpleIntegerProperty();
	 }
}
