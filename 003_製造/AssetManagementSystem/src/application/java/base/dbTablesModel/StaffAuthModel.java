package application.java.base.dbTablesModel;

import java.time.LocalDate;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 社員認証マスター（パスワード管理用）
 * @brief 社員認証マスター（パスワード管理用）<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class StaffAuthModel extends BaseTableViewModel {

    private IntegerProperty staffNo;
	private StringProperty email;
	private StringProperty passwordHash;
	private ObjectProperty<LocalDate> lockoutEnd;
	private IntegerProperty accessFailedCount;

	
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
	 * [メールアドレス(ログインID):ログインID]取得
	 * @return email
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[emailAddr]とする<br>
	 */
	public String getEmailAddr() {
		return email.get();
		}

	 /**
	 * [メールアドレス(ログインID):ログインID]項目設定
	 * @param address 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[emailAddr]とする<br>
	 */
	public void setEmailAddr(String address) {
		this.email.set(address);
		}
	
	/**
	 * [パスワード]取得
	 * @return passwordHash
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[Pass]とする<br>
	 */
	public String getPass() {
		return passwordHash.get();
		}

	 /**
	 * [パスワード]項目設定
	 * @param password 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[Pass]とする<br>
	 */
	public void setPass(String password) {
		this.passwordHash.set(password);
		}	 

	/**
	* [ロックアウト終了日時]項目取得
	* @return lockoutEnd
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDate getLockoutEnd() {
		return lockoutEnd.get();
	}

	/**
	* [ロックアウト終了日時]項目設定
	* @param dateTime 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLockoutEnd(LocalDate dateTime) {
		this.lockoutEnd.set(dateTime);
	}		
	
	/**
	 * [ログイン失敗回数]取得
	 * @return accessFailedCount
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[failedCount]とする<br>
	 */
	public Integer getFailedCount() {
		return accessFailedCount.get();
	}

	 /**
	 * [ログイン失敗回数]項目設定
	 * @param count 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[failedCount]とする<br>
	 */
	public void setFailedCount(Integer count) {
		this.accessFailedCount.set(count);
	}	 

	 /**
     * コンストラクタ
     * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
     * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
     * private StringProperty itemName; と宣言しただけでは null のままで<br>
     * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
     * コンストラクタで、全プロパティの初期化を行う。
     */
	 public StaffAuthModel(){
		 this.staffNo = new SimpleIntegerProperty();
		 this.email = new SimpleStringProperty("");
		 this.passwordHash = new SimpleStringProperty("");
		 this.lockoutEnd  = new SimpleObjectProperty<>();
		 this.accessFailedCount = new SimpleIntegerProperty();
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public StaffAuthModel(StaffAuthModel source) {
	     this();   
		 
	     this.setStaffNo(source.getStaffNo());
		 this.setEmailAddr(source.getEmailAddr());
	     this.setPass(source.getPass());
		 this.setLockoutEnd(source.getLockoutEnd());
		 this.setFailedCount(source.getFailedCount());		 
	 }	 
}
