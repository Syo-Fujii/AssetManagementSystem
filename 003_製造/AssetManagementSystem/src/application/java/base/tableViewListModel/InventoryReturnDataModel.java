package application.java.base.tableViewListModel;

import java.time.LocalDate;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品返却データクラス
 * @brief 備品返却画面で表示する明細リストのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class InventoryReturnDataModel extends BaseTableViewModel {

	private StringProperty typeName;
	private StringProperty serialNo;
	private StringProperty staffName;
	private StringProperty startDate;
	private StringProperty limitDate;
    private StringProperty remarks;
    private BooleanProperty isCheckIn;
    private IntegerProperty stockDataId;
    
    private ObjectProperty<LocalDate> returnDate;
    private ObjectProperty<LocalDate> confirmedDate;
	
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
	* [使用者]Property取得 (TableViewとの連動に必須)
	* @return staff Name(Property)
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	* 値に応じて、TableViewの表示を変更(連動)させる為に必須
	*/
	public StringProperty staffNameProperty() {
	    return this.staffName;
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
	* [返却]項目取得
	* @return isCheckIn
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public Boolean getIsCheckIn() {
		return isCheckIn.get();
	}

	/**
	* [返却]項目設定
	* @param isCheckIn 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setIsCheckIn(Boolean checked) {
		this.isCheckIn.set(checked);
	}

	/**
	* [返却] Property取得 (TableViewとの連動に必須)
	* @return isCheckIn (Property)
	* @brief [javafx.beans.property]
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	* 値に応じて、TableViewの表示を変更(連動)させる為に必須<br>
	* ≒ 明細の入力項目の場合はプロパティを渡すゲッターを用意する
	*/
	public BooleanProperty isCheckInProperty() {
	    return isCheckIn;
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
	* [返却日]項目取得
	* @return returnDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getReturnDate() {
		return returnDate.get();
	}

	/**
	* [返却日]項目設定
	* @param returnDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setReturnDate(LocalDate date) {
		this.returnDate.set(date);
	}	

	/**
	* [最終所在確認日]項目取得
	* @param confirmedDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getConfirmedDate() {
		return confirmedDate.get();
	}

	/**
	* [最終所在確認日]項目設定
	* @param confirmedDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setConfirmedDate(LocalDate date) {
		this.confirmedDate.set(date);
	}	
	
	
	/**
    * コンストラクタ
    * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
    * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
    * private StringProperty itemName; と宣言しただけでは null のままで<br>
    * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
    * コンストラクタで、全プロパティの初期化を行う。
    */
	public InventoryReturnDataModel(){
		this.typeName = new SimpleStringProperty("");
		this.serialNo = new SimpleStringProperty("");
	    this.staffName = new SimpleStringProperty("");
	    this.startDate = new SimpleStringProperty("");
	    this.limitDate = new SimpleStringProperty("");
	    this.remarks = new SimpleStringProperty("");
	    this.isCheckIn = new SimpleBooleanProperty(false);
	    this.stockDataId = new SimpleIntegerProperty();
	    
	    this.returnDate = new SimpleObjectProperty<>();
	    this.confirmedDate = new SimpleObjectProperty<>();
	}
}
