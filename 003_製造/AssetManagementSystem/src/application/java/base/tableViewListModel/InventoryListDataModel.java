package application.java.base.tableViewListModel;

import application.java.base.BaseTableViewModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * 備品一覧データクラス
 * @brief 備品一覧画面で表示する明細リストのデータ
 * ※ １行分のデータ
 * 画面Controllerにて呼び出される前提
 */
public class InventoryListDataModel extends BaseTableViewModel {
	
    private final StringProperty itemName;
    private IntegerProperty loanCount = new SimpleIntegerProperty(0);
    private IntegerProperty returnCount = new SimpleIntegerProperty(0);
    private IntegerProperty unknownCount = new SimpleIntegerProperty(0);
    private IntegerProperty totalCount = new SimpleIntegerProperty(0);
    
	/**
	 * [名称]取得 
	 * @return itemName
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
    public String getItemName() {
		return itemName.get();
		}

    /**
	* [名称]項目設定
	* @param itemName 設定する値
	* @brief [javafx.beans.property] 
	*         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	*/
 	public void setItemName(String itemName) {
		this.itemName.set(itemName);
		}

	/**
	 * [貸出]取得
	 * @return loanCount
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public Integer getLoanCount() {
		return loanCount.get();
		}

	 /**
	 * [貸出]項目設定
	 * @param cnt 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	public void setLoanCount(Integer cnt) {
		this.loanCount.set(cnt);
		}	 

	/**
	 * [返却]取得
	 * @return returnCount
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
     public Integer getReturnCount() {
		return returnCount.get();
		}

	 /**
	 * [返却]項目設定
	 * @param cnt 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	 public void setReturnCount(Integer cnt) {
		 this.returnCount.set(cnt);
	 }
		
	 /**
	 * [不明]取得
	 * @return unknownCount
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	 public Integer getUnknownCount() {
		 return unknownCount.get();
		 }

	 /**
	 * [不明]項目設定
	 * @param cnt 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	 public void setUnknownCount(Integer cnt) {
		 this.unknownCount.set(cnt);
		 }

	 /**
	 * [合計]取得
	 * @return totalCount
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	 public Integer getTotalCount() {
		 return totalCount.get();
		 }

	 /**
	 * [合計]項目設定
	 * @param cnt 設定する値
	 * @brief [javafx.beans.property] 
	 *         UIとデータを連動させる（データが変更されたらUIも更新する）ためのラッパークラス
	 */
	 public void setTotalCount(Integer cnt) {
		 this.totalCount.set(cnt);
		 }

	 
	 /**
      * コンストラクタ
      * @param name
      */
    public InventoryListDataModel( String name)
    {
        this.itemName = new SimpleStringProperty(name);
        }

    public InventoryListDataModel( String name, Integer loan )
    {
        this.itemName = new SimpleStringProperty(name);
        this.loanCount = new SimpleIntegerProperty(loan);
        }

    public InventoryListDataModel( String name, Integer loan, Integer rtn )
    {
        this.itemName = new SimpleStringProperty(name);
        this.loanCount = new SimpleIntegerProperty(loan);
        this.returnCount = new SimpleIntegerProperty(rtn);
        }

    public InventoryListDataModel( String name, Integer loan, Integer rtn, Integer unknown )
    {
        this.itemName = new SimpleStringProperty(name);
        this.loanCount = new SimpleIntegerProperty(loan);
        this.returnCount = new SimpleIntegerProperty(rtn);
        this.unknownCount = new SimpleIntegerProperty(unknown);
        }

    public InventoryListDataModel( String name , Integer loan, Integer rtn, Integer unknown, Integer total )
    {
        this.itemName = new SimpleStringProperty(name);
        this.loanCount = new SimpleIntegerProperty(loan);
        this.returnCount = new SimpleIntegerProperty(rtn);
        this.unknownCount = new SimpleIntegerProperty(unknown);
        this.totalCount = new SimpleIntegerProperty(total);
        }
}
