package application.java.base.dbTablesModel;

import java.time.LocalDate;

import application.java.common.AppConst;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品データクラス
 * @brief 備品データのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class StockDataModel {

	private IntegerProperty id;
	private StringProperty serialNo;
	private StringProperty parentStockCode;
	private IntegerProperty staffNo;
	private ObjectProperty<LocalDate> startDate;
	private ObjectProperty<LocalDate> limitDate;
	private ObjectProperty<LocalDate> returnDate;
	private ObjectProperty<LocalDate> confirmedDate;
	private BooleanProperty del;
	
	
	/**
	 * [採番]取得
	 * @return id
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeId]とする<br>
	 */
	public Integer getStockId() {
		return id.get();
	}

	/**
	 * [採番]項目設定
	 * @param id 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeId]とする<br>
	 */
	public void setStockId(Integer id) {
		this.id.set(id);
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
	* @param serialNo 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setSerialNo(String no) {
		this.serialNo.set(no);
	}		

	/**
	* [親シリアルナンバー]取得
	* @return parentStockCode
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
    public String getParentStockCode() {
		return parentStockCode.get();
	}

	/**
	* [親シリアルナンバー]項目設定
	* @param parentStockCode 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/    
	public void setParentStockCode(String no) {
		this.parentStockCode.set(no);
	}		
	
	/**
	 * [社員番号]取得
	 * @return staffNo
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[staffNo]とする<br>
	 */
	public Integer getStaffNo() {
		return staffNo.get();
		}

	 /**
	 * [社員番号]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[staffNo]とする<br>
	 */
	public void setStaffNo(Integer no) {
		this.staffNo.set(no);
		}	
	
	/**
	* [最終所在確認日]項目取得
	* @return ConfirmedDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getConfirmedDate() {
		return confirmedDate.get();
	}

	/**
	* [最終所在確認日]項目設定
	* @param ConfirmedDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setConfirmedDate(LocalDate date) {
		this.confirmedDate.set(date);
	}		
	
	/**
	* [貸出開始日]項目取得
	* @return startDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getStartDate() {
		return startDate.get();
	}

	/**
	* [貸出開始日]項目設定
	* @param startDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setStartDate(LocalDate date) {
		this.startDate.set(date);
	}	
	
	/**
	* [返却予定日]項目取得
	* @return limitDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getLimitDate() {
		return limitDate.get();
	}

	/**
	* [返却予定日]項目設定
	* @param limitDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLimitDate(LocalDate date) {
		this.limitDate.set(date);
	}		
	
	/**
	* [返却日]項目取得
	* @return limitDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getReturnDate() {
		return returnDate.get();
	}

	/**
	* [返却日]項目設定
	* @param limitDate 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLReturnDate(LocalDate date) {
		this.returnDate.set(date);
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
	 * @param del 設定する値
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
			 this.price = new SimpleObjectProperty<>();
			 this.remarks = new SimpleStringProperty("");
			 this.del = new SimpleBooleanProperty(false);		 
		 }
	
}
