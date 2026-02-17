package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.InventoryListDataModel;

public class InventoryListMapper {

	/**
	 * TableView一覧データ取得(テーブルより取得)
	 * @return　TableView一覧データ
	 */
    List<InventoryListDataModel> getTableRecords();
    
	/**
	 * TableView一覧データ取得(Viewより取得)
	 * @return　TableView一覧データ
	 */
    List<InventoryListDataModel> getViewRecords();
}
