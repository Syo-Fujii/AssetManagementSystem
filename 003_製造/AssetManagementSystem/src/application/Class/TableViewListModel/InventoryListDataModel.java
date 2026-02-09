package application.Class.TableViewListModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 備品一覧データクラス
 * @brief 備品一覧画面で表示する明細リストのデータ
 * ※ １行分のデータ
 * 画面Controllerにて呼び出される前提
 */
public class InventoryListDataModel {
	
    private final StringProperty itemName;

     public String getItemName() {
		return itemName.get();
	}

	public void setItemName(String itemName) {
		this.itemName.set(itemName);;
	}

	
	 /**
      * コンストラクタ
      * @param name
      */
    public InventoryListDataModel( String name  )
    {
        this.itemName   = new SimpleStringProperty(name);
    }
}
