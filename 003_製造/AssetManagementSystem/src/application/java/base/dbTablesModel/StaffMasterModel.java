package application.java.base.dbTablesModel;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 社員マスタークラス
 * @brief 社員マスタのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class StaffMasterModel extends BaseTableViewModel {

    private IntegerProperty staffNo;
	private StringProperty name;
    private IntegerProperty authNo;
    private BooleanProperty del;
    
    private AppConst.DataRowState state;

    
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
	 * [氏名]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[staffName]とする<br>
	 */
	public String getStaffName() {
		return name.get();
		}

	 /**
	 * [氏名]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[name]とする<br>
	 */
	public void setName(String name) {
		this.name.set(name);
		}
	
	/**
	 * [権限]取得
	 * @return authNo
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[authNo]とする<br>
	 */
	public Integer getAuthNo() {
		return authNo.get();
		}

	 /**
	 * [権限]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[authNo]とする<br>
	 */
	public void setAuthNo(Integer no) {
		this.authNo.set(no);
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
	 * [データ行状態]取得
	 * @return state
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public AppConst.DataRowState RowStatus() {
		return state;
	}

	 /**
	 * [データ行状態]項目設定
	 * @param state 設定する値
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public void setStatus(AppConst.DataRowState state) {
		this.state = state;
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
		 this.authNo = new SimpleIntegerProperty(AppConst.UNSET_NUMBER_VALUE);
		 this.del = new SimpleBooleanProperty(false);
		 
		 this.state = AppConst.DataRowState.DETACHED;
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public StaffMasterModel(StaffMasterModel source) {
	     this();   
		 
	     this.setStaffNo(source.getStaffNo());
		 this.setName(source.getStaffName());
	     this.setAuthNo(source.getAuthNo());
		 this.setDelFlg(source.getDelFlg());
		 
		 this.setStatus(source.RowStatus());
	 }
}
