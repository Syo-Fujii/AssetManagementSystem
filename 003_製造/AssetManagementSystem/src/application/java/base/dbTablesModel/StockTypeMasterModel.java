package application.java.base.dbTablesModel;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品分類マスタークラス
 * @brief 備品分類マスタのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class StockTypeMasterModel extends BaseTableViewModel {
	
	private IntegerProperty id;
	private IntegerProperty stockType;
	private StringProperty stockCode;
	private StringProperty name;
	private BooleanProperty del;

	/**
	 * [採番]取得
	 * @return id
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeId]とする<br>
	 */
	public Integer getStockTypeId() {
		return id.get();
	}

	/**
	 * [採番]項目設定
	 * @param id 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeId]とする<br>
	 */
	public void setStockTypeId(Integer id) {
		this.id.set(id);
	}	
	
	/**
	 * [分類種別]取得
	 * @return stockType
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockType]とする<br>
	 */
	public Integer getStockType() {
		return stockType.get();
	}
	
	/**
	 * [分類種別]項目設定
	 * @param stockType 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockType]とする<br>
	 */
	public void setStockType(Integer stockType) {
		this.stockType.set(stockType);
	}
	
	/*
	 * [分類コード]取得
	 * @return stockCode
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockCode]とする<br>
	 */
	public String getStockCode() {
		return stockCode.get();
	}

	/**
	 * [分類コード]項目設定
	 * @param stockCode 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockCode]とする<br>
	 */
	public void setStockCode(String stockCode) {
		this.stockCode.set(stockCode);
	}
	
	/**
	 * [分類名称]取得
	 * @return name
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeName]とする<br>
	 */
	public String getStockTypeName() {
		return name.get();
	}

	/**
	 * [分類名称]項目設定
	 * @param name 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[stockTypeName]とする<br>
	 */
	public void setStockTypeName(String name) {
		this.name.set(name);
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
	 public StockTypeMasterModel(){
		 this.id = new SimpleIntegerProperty();
		 this.stockType = new SimpleIntegerProperty();
		 this.stockCode = new SimpleStringProperty("");
		 this.name = new SimpleStringProperty("");
		 this.del = new SimpleBooleanProperty(false);
	 }
	 /**
	 * コンストラクタ(コピー生成用)
     * @brief　Object.Cloneを用いた場合、Exception処理を考慮する必要があるため<br>
     * コピー用のコンストラクタを用意する
     */
	 public StockTypeMasterModel(StockTypeMasterModel source) {
	     this();   
		 
	     this.setStockTypeId(source.getStockTypeId());
		 this.setStockType(source.getStockType());
		 this.setStockCode(source.getStockCode());
		 this.setStockTypeName(source.getStockTypeName());
		 this.setDelFlg(source.getDelFlg());
	 }
}
