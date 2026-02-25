package application.java.base.tableViewListModel;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品貸出データクラス
 * @brief 備品貸出画面で表示する明細リストのデータ
 * ※ １行分のデータ
 * 画面Controllerにて呼び出される前提
 */
public class InventoryLoanDataModel extends BaseTableViewModel {

	private StringProperty typeName;
	private StringProperty serialNo;
	private StringProperty staffName;
	private StringProperty startDate;
	private StringProperty limitDate;
    private StringProperty remarks;
    private BooleanProperty isCheckOut;
    private IntegerProperty stockDataId;
	private IntegerProperty staffNo;
    
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
	* [貸出]項目取得
	* @param isCheckOut 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public Boolean getIsCheckOut() {
		return isCheckOut.get();
	}

	/**
	* [貸出]項目設定
	* @param isCheckOut 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setRemarks(Boolean checked) {
		this.isCheckOut.set(checked);
	}	
	
	/**
	* [備品データ ID]項目取得
	* @param stockDataId 設定する値
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
	* [社員番号]項目取得
	* @param staffNo 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public Integer getStaffNo() {
		return staffNo.get();
	}

	/**
	* [社員番号]項目設定
	* @param staffNo 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setStaffNo(Integer id ) {
		this.staffNo.set(id);
	}
	
	/**
    * コンストラクタ
    * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
    * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
    * private StringProperty itemName; と宣言しただけでは null のままで<br>
    * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
    * コンストラクタで、全プロパティの初期化を行う。
    */
	public InventoryLoanDataModel(){
		this.typeName = new SimpleStringProperty("");
		this.serialNo = new SimpleStringProperty("");
	    this.staffName = new SimpleStringProperty("");
	    this.startDate = new SimpleStringProperty("");
	    this.limitDate = new SimpleStringProperty("");
	    this.remarks = new SimpleStringProperty("");
	    this.isCheckOut = new SimpleBooleanProperty(false);
	    this.stockDataId = new SimpleIntegerProperty();
	    this.staffNo = new SimpleIntegerProperty();
	 }
}
