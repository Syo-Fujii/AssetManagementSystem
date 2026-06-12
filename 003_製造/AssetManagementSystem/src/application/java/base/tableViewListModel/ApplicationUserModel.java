package application.java.base.tableViewListModel;

import java.time.LocalDateTime;

import application.java.base.dbTablesModel.StaffMasterModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ログインユーザー情報クラス
 * @brief ログインで取得するユーザーのデータ<br>
 * ※ 社員マスタ[staff_master] + 社員認証マスター（パスワード管理用）[staff_auth]<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class ApplicationUserModel extends StaffMasterModel {

	private StringProperty email;
    private StringProperty password_hash;
    private ObjectProperty<LocalDateTime> lockout_end;
    private IntegerProperty access_failed_count;
   
	/**
	 * [メールアドレス(ログインID)]取得
	 * @return email
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[LogInId]とする<br>
	 */
	public String getLoginId() {
		return email.get();
		}

	 /**
	 * [メールアドレス(ログインID)]項目設定
	 * @param email 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[LogInId]とする<br>
	 */
	public void setLoginId(String id) {
		this.email.set(id);
		}	   

	/**
	 * [パスワード(ハッシュ値)]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[password]とする<br>
	 */
	public String getPassword() {
		return password_hash.get();
		}

	 /**
	 * [パスワード(ハッシュ値)]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[password]とする<br>
	 */
	public void setPassword(String pass) {
		this.password_hash.set(pass);
		}

	/**
	* [ロックアウト終了日時]項目取得
	* @return lockout_end 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/		
	public LocalDateTime getLockOutDateTime() {
		return lockout_end.get();
	}

	/**
	* [ロックアウト終了日時]項目設定
	* @param lockout_end 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
	public void setLockOutDateTime(LocalDateTime date) {
		this.lockout_end.set(date);
	}		

	/**
	 * [ログイン失敗回数]取得
	 * @return authNo
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[FaildCount]とする<br>
	 */
	public Integer getFailedCount() {
		return access_failed_count.get();
		}

	 /**
	 * [ログイン失敗回数]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[FaildCount]とする<br>
	 */
	public void setFailedCount(Integer count) {
		this.access_failed_count.set(count);
		}	 


	 /**
     * コンストラクタ
     * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
     * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
     * private StringProperty itemName; と宣言しただけでは null のままで<br>
     * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
     * コンストラクタで、全プロパティの初期化を行う。
     */
	 public ApplicationUserModel(){
		 super();
		 
		 this.email = new SimpleStringProperty();
		 this.password_hash = new SimpleStringProperty("");
		 this.lockout_end = new SimpleObjectProperty<>();
		 this.access_failed_count = new SimpleIntegerProperty();
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public ApplicationUserModel(ApplicationUserModel source) {
	     super( (StaffMasterModel) source );   
		 
	     this.setLoginId(source.getLoginId());
		 this.setPassword(source.getPassword());
	     this.setLockOutDateTime(source.getLockOutDateTime());
		 this.setFailedCount(source.getFailedCount());
	 }	 
	
	 /**
	  * DB 社員マスターデータ(MODEL/ENTITY)取得
	  * @brief　継承元の社員マスタのデータをMODELで返す
	  */
	public StaffMasterModel GetStaffMasterData()
	{
		return new StaffMasterModel(this);
	}
}
