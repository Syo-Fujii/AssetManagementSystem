package application.java.base.tableViewListModel;

import java.time.LocalDateTime;

import application.java.base.dbTablesModel.StaffAuthModel;
import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.common.AppConst;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ログインユーザー情報クラス
 * @brief ログインで取得するユーザーのデータ<br>
 * ※ 社員マスタ[staff_master] + 社員認証マスター（パスワード管理用）[staff_auth] + 権限マスター[auth_master](権限：permission_mask)<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class ApplicationUserModel extends StaffMasterModel {

	private StringProperty email;
    private StringProperty password_hash;
    private ObjectProperty<LocalDateTime> lockout_end;
    private IntegerProperty access_failed_count;
    private IntegerProperty permission_mask;
    
    private AppConst.DataRowState masterDataState;
    private AppConst.DataRowState authDataState;
    
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
	 * @return access_failed_count
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[FaildCount]とする<br>
	 */
	public Integer getFailedCount() {
		return access_failed_count.get();
		}

	 /**
	 * [ログイン失敗回数]項目設定
	 * @param count 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[FaildCount]とする<br>
	 */
	public void setFailedCount(Integer count) {
		this.access_failed_count.set(count);
		}


	/**
	 * [権限]取得
	 * @return permission_mask
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[PermissionMask]とする<br>
	 */
	public Integer getPermissionMask() {
		return permission_mask.get();
		}

	 /**
	 * [権限]項目設定
	 * @param no 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[PermissionMask]とする<br>
	 */
	public void setPermissionMask(Integer mask) {
		this.permission_mask.set(mask);
		}
	
	/**
	 * [社員マスタ：データ行状態]取得
	 * @return authDataState
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public AppConst.DataRowState MasterRowStatus() {
		return masterDataState;
	}

	 /**
	 * [社員マスタ：データ行状態]項目設定
	 * @param state 設定する値
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public void setMastetStatus(AppConst.DataRowState state) {
		this.masterDataState = state;
	}
	
	/**
	 * [社員認証マスタ：データ行状態]取得
	 * @return authDataState
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public AppConst.DataRowState AuthRowStatus() {
		return authDataState;
	}

	 /**
	 * [社員認証マスタ：データ行状態]項目設定
	 * @param state 設定する値
	 * @brief 当該データ(DB データ)の状態を取得する<br>
	 * ※ MOEDL新規作成(データ未設定：初期値)：DETACHED<br>
	 * ※ データ取得(DB連携)未変更(DBと同じ値)：UNCHANGED<br>
	 * ※ データ設定(値変更・設定)：MODIFIED<br>
	 */
	public void setAuthStatus(AppConst.DataRowState state) {
		this.authDataState = state;
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
		 this.permission_mask = new SimpleIntegerProperty();
		 
		 this.masterDataState = AppConst.DataRowState.DETACHED;
		 this.authDataState = AppConst.DataRowState.DETACHED;
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public ApplicationUserModel(ApplicationUserModel source) {
	     this();
		 
	     this.setStaffNo(source.getStaffNo());
	     this.setName(source.getStaffName());
	     this.setAuthNo(source.getAuthNo());
	     this.setDelFlg(source.getDelFlg());
		 
	     this.setLoginId(source.getLoginId());
		 this.setPassword(source.getPassword());
	     this.setLockOutDateTime(source.getLockOutDateTime());
		 this.setFailedCount(source.getFailedCount());
		 this.setPermissionMask(source.getPermissionMask());

		 this.masterDataState = source.MasterRowStatus();
		 this.authDataState = source.AuthRowStatus();
	 }	 
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public ApplicationUserModel(StaffMasterModel masterEntity, StaffAuthModel authEntity) {
	     this();
		 
	     this.setStaffNo(masterEntity.getStaffNo());
	     this.setName(masterEntity.getStaffName());
	     this.setAuthNo(masterEntity.getAuthNo());
	     this.setDelFlg(masterEntity.getDelFlg());
		 
	     this.setLoginId(authEntity.getEmailAddr());
		 this.setPassword(authEntity.getPass());
	     this.setLockOutDateTime(authEntity.getLockoutEnd());
		 this.setFailedCount(authEntity.getFailedCount());
		 
		 this.masterDataState = masterEntity.RowStatus();
		 this.authDataState = authEntity.RowStatus();
	 }
	 
	 /**
	  * DB 社員マスターデータ(MODEL/ENTITY)取得
	  * @brief　継承元の社員マスタのデータをMODELで返す
	  */
	public StaffMasterModel GetStaffMasterData()
	{
		return new StaffMasterModel(this);
	}

	 /**
	  * DB 社員認証マスターデータ(MODEL/ENTITY)取得
	  * @brief　社員認証マスタのデータをMODELで返す
	  */
	public StaffAuthModel GetStaffAuthData()
	{
		var entity = new StaffAuthModel();
		
		entity.setStaffNo(this.getStaffNo());
		entity.setEmailAddr(this.getLoginId());
		entity.setPass(this.getPassword());
		entity.setLockoutEnd(this.getLockOutDateTime());
		
		return entity;
	}

}
