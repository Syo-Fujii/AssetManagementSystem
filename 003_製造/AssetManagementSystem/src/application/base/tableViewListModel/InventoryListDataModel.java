package application.base.tableViewListModel;

import application.base.BaseTableViewModel;
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
    
    
     public String getItemName() {
		return itemName.get();
		}

	public void setItemName(String itemName) {
		this.itemName.set(itemName);
		}

	public Integer getLoanCount() {
		return loanCount.get();
	}

	public void setLoanCount(Integer cnt) {
		this.loanCount.set(cnt);
		}	 
	 
	 
	 /**
      * コンストラクタ
      * @param name
      */
    public InventoryListDataModel( String name , Integer loan )
    {
        this.itemName = new SimpleStringProperty(name);
        this.loanCount = new SimpleIntegerProperty(loan);
        }
}
