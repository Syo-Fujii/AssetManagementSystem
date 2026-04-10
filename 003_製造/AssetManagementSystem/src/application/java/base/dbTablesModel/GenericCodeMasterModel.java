package application.java.base.dbTablesModel;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 汎用マスタークラス
 * @brief 汎用マスタのデータ<br>
 * ※ １行分のデータ<br>
 * 画面Controllerにて呼び出される前提<br>
 */
public class GenericCodeMasterModel extends BaseTableViewModel {

    private StringProperty genericKey;
	private StringProperty keyComment;
    private StringProperty typeKey;
	private StringProperty typeComment;
    private StringProperty code;
	private StringProperty codeComment;
	private IntegerProperty sortOrder;
	private StringProperty value1;	
	private StringProperty value2;	
	private StringProperty value3;	
	private StringProperty value4;	
	private StringProperty value5;	
	private StringProperty valuesComment;
    private BooleanProperty isActive;	
	
	
	/**
	 * [分類キー]取得
	 * @return genericKey
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[genericKey]とする<br>
	 */
	public String getGenericKey() {
		return genericKey.get();
	}

	 /**
	 * [分類キー]項目設定
	 * @param genericKey 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[genericKey]とする<br>
	 */
	public void setGenericKey(String genericKey) {
		this.genericKey.set(genericKey);
	}

	/**
	 * [分類キー コメント]取得
	 * @return keyComment
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[keyComment]とする<br>
	 */
	public String getKeyComment() {
		return keyComment.get();
	}

	 /**
	 * [分類キー コメント]項目設定
	 * @param keyComment 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[keyComment]とする<br>
	 */
	public void setKeyComment(String keyComment) {
		this.keyComment.set(keyComment);
	}

	/**
	 * [種別キー]取得
	 * @return typeKey
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[typeKey]とする<br>
	 */
	public String getTypeKey() {
		return typeKey.get();
	}

	/**
	 * [種別キー]取得(数値型)
	 * @return typeKey(数値型)
	 * @brief [javafx.beans.property] 
	 */
	public Integer getTypeKeyValue() {
		return AppUtil.parseInt(getTypeKey(), AppConst.UNSET_NUMBER_VALUE);
		
	}
	
	/**
	 * [種別キー]項目設定
	 * @param typeKey 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[typeKey]とする<br>
	 */
	public void setTypeKey(String typeKey) {
		this.typeKey.set(typeKey);
	}

	/**
	 * [種別キー コメント]取得
	 * @return typeComment
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[typeComment]とする<br>
	 */
	public String getTypeComment() {
		return typeComment.get();
	}

	 /**
	 * [種別キー コメント]項目設定
	 * @param typeComment 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[typeComment]とする<br>
	 */
	public void setTypeComment(String typeComment) {
		this.typeComment.set(typeComment);
	}

	/**
	 * [コード]取得
	 * @return code
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[code]とする<br>
	 */
	public String getCode() {
		return code.get();
	}

	 /**
	 * [コード]項目設定
	 * @param code 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[code]とする<br>
	 */
	public void setCode(String code) {
		this.code.set(code);
	}

	/**
	 * [コード コメント]取得
	 * @return codeComment
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[codeComment]とする<br>
	 */
	public String getCodeComment() {
		return codeComment.get();
	}

	 /**
	 * [コード コメント]項目設定
	 * @param codeComment 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[codeComment]とする<br>
	 */
	public void setCodeComment(String codeComment) {
		this.codeComment.set(codeComment);
	}	
	
	/**
	 * [並び順]取得
	 * @return sortOrder
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[sortOrder]とする<br>
	 */
	public Integer getSortOrder() {
		return sortOrder.get();
	}	
	
	 /**
	 * [並び順]項目設定
	 * @param sortOrder 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[sortOrder]とする<br>
	 */
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder.set(sortOrder);
	}	
	
	/**
	 * [設定値１]取得
	 * @return value1
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value1]とする<br>
	 */
	public String getValue1() {
		return value1.get();
	}
	
	 /**
	 * [設定値１]項目設定
	 * @param value1 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value1]とする<br>
	 */
	public void setValue1(String value) {
		this.value1.set(value);
	}	
	
	/**
	 * [設定値２]取得
	 * @return value2
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value2]とする<br>
	 */
	public String getValue2() {
		return value2.get();
	}
	
	 /**
	 * [設定値２]項目設定
	 * @param value2 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value2]とする<br>
	 */
	public void setValue2(String value) {
		this.value2.set(value);
	}	
	
	/**
	 * [設定値３]取得
	 * @return value3
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value3]とする<br>
	 */
	public String getValue3() {
		return value3.get();
	}
	
	 /**
	 * [設定値３]項目設定
	 * @param value1 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value3]とする<br>
	 */
	public void setValue3(String value) {
		this.value3.set(value);
	}	
	
	/**
	 * [設定値４]取得
	 * @return value4
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value4]とする<br>
	 */
	public String getValue4() {
		return value4.get();
	}
	
	 /**
	 * [設定値４]項目設定
	 * @param value4 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value4]とする<br>
	 */
	public void setValue4(String value) {
		this.value4.set(value);
	}	
	
	/**
	 * [設定値５]取得
	 * @return value5
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value5]とする<br>
	 */
	public String getValue5() {
		return value5.get();
	}
	
	 /**
	 * [設定値５]項目設定
	 * @param value5 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[value5]とする<br>
	 */
	public void setValue5(String value) {
		this.value5.set(value);
	}	
	
	 /**
	 * [設定値の説明]項目設定
	 * @param valuesComment 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[valuesComment]とする<br>
	 */
	public String getValuesComment() {
		return valuesComment.get();
	}

	 /**
	 * [設定値の説明]項目設定
	 * @param valuesComment 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[valuesComment]とする<br>
	 */
	public void setValuesComment(String valuesComment) {
		this.valuesComment.set(valuesComment);
	}

	 /**
	 * [有効フラグ]項目設定
	 * @param isActive 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[isActive]とする<br>
	 */
	public Boolean getIsActive() {
		return isActive.get();
	}

	 /**
	 * [有効フラグ]項目設定
	 * @param isActive 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス<br>
	 * ※ DBとBINDする場合は取得時のカラム名を[isActive]とする<br>
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive.set(isActive);
	}


	 /**
     * コンストラクタ
     * @brief　引数なしコンストラクタがMyBatisの一覧(List)生成で用いられる。<br>
     * JavaFXの Property クラス（SimpleStringPropertyなど）は参照型で<br>
     * private StringProperty itemName; と宣言しただけでは null のままで<br>
     * MyBatisが値を入れようとしても「入れ物」が存在しないためエラーになる為<br>
     * コンストラクタで、全プロパティの初期化を行う。
     */
	 public GenericCodeMasterModel(){
		 this.genericKey = new SimpleStringProperty("");
		 this.keyComment = new SimpleStringProperty("");
		 this.typeKey = new SimpleStringProperty("");
		 this.typeComment = new SimpleStringProperty("");
		 this.code = new SimpleStringProperty("");
		 this.codeComment = new SimpleStringProperty("");
		 this.sortOrder = new SimpleIntegerProperty();
		 this.value1 = new SimpleStringProperty("");	
		 this.value2 = new SimpleStringProperty("");	
		 this.value3 = new SimpleStringProperty("");	
		 this.value4 = new SimpleStringProperty("");	
		 this.value5 = new SimpleStringProperty("");	
		 this.valuesComment = new SimpleStringProperty("");
		 this.isActive = new SimpleBooleanProperty(true);
	 }
}
