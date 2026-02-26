package application.java.base.dbTablesModel;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StaffMasterModel extends BaseTableViewModel  {

    private IntegerProperty staffNo;
	private StringProperty name;
    private IntegerProperty authNo;	
    private BooleanProperty del;		
	
	/**
	 * [社員番号]取得
	 * @return staffNo
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public Integer getStaffNo() {
		return staffNo.get();
		}

	 /**
	 * [社員番号]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public void setStaffNo(Integer no) {
		this.staffNo.set(no);
		}	   

	/**
	 * [氏名]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public String getName() {
		return name.get();
		}

	 /**
	 * [氏名]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public void setName(String name) {
		this.name.set(name);
		}
	
	/**
	 * [権限]取得
	 * @return authNo
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public Integer getAuthNo() {
		return authNo.get();
		}

	 /**
	 * [権限]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public void setAuthNo(Integer no) {
		this.authNo.set(no);
		}	 

	/**
	 * [削除フラグ]取得
	 * @return del
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public Boolean getLoanCount() {
		return del.get();
		}

	 /**
	 * [削除フラグ]項目設定
	 * @param isdeleted 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public void setLoanCount(Boolean isdeleted) {
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
	 public StaffMasterModel(){
		 this.staffNo = new SimpleIntegerProperty();
		 this.name = new SimpleStringProperty("");
		 this.authNo = new SimpleIntegerProperty();
		 this.del = new SimpleBooleanProperty(false);
	 }   
}
