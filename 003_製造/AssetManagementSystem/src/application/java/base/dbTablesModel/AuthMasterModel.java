package application.java.base.dbTablesModel;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * 権限マスタークラス
 * @brief 権限マスタのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class AuthMasterModel extends BaseTableViewModel {

    private IntegerProperty authId;
	private StringProperty name;
    private IntegerProperty permission_mask;
    private StringProperty remarks;
    private BooleanProperty del;
	
	/**
	 * [権限ID]取得
	 * @return authId
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[id]とする<br>
	 */
	public Integer getId() {
		return this.authId.get();
		}

	 /**
	 * [権限ID]項目設定
	 * @param id 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[id]とする<br>
	 */
	public void setId(Integer id) {
		this.authId.set(id);
		}	   

	/**
	 * [権限の名称]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[authName]とする<br>
	 */
	public String getAuthName() {
		return this.name.get();
		}

	 /**
	 * [権限の名称]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[authName]とする<br>
	 */
	public void setAuthName(String name) {
		this.name.set(name);
		}
	
	/**
	 * [権限(BitMask)]取得
	 * @return permission_mask
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[Permission]とする<br>
	 */
	public Integer getPermission() {
		return this.permission_mask.get();
		}

	 /**
	 * [権限(BitMask)]項目設定
	 * @param mask 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[Permission]とする<br>
	 */
	public void setPermission(Integer mask) {
		this.permission_mask.set(mask);
		}	 

	/**
	 * [備考]取得
	 * @return remarks
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public String getRemarks() {
		return this.remarks.get();
		}

	 /**
	 * [備考]項目設定
	 * @param remarks 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 */
	public void setRemarks(String remarks) {
		this.remarks.set(remarks);
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
     * コンストラクタ
     * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
     * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
     * private StringProperty itemName; と宣言しただけでは null のままで<br>
     * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
     * コンストラクタで、全プロパティの初期化を行う。
     */
	 public AuthMasterModel(){
		 this.authId = new SimpleIntegerProperty();
		 this.name = new SimpleStringProperty("");
		 this.permission_mask = new SimpleIntegerProperty();
		 this.remarks = new SimpleStringProperty("");
		 this.del = new SimpleBooleanProperty(false);
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public AuthMasterModel(AuthMasterModel source) {
	     this();   
		 
	     this.setId(source.getId());
		 this.setAuthName(source.getAuthName());
	     this.setPermission(source.getPermission());
	     this.setRemarks(source.getRemarks());
	     this.setDelFlg(source.getDelFlg());
	 }	 
}