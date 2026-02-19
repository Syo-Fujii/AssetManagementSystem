package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.InventoryListDataModel;

public interface InventoryListMapper {

	/**
	 * 備品一覧データ取得(テーブルより取得)
	 * @return 備品一覧データ
	 */
    List<InventoryListDataModel> getTableRecords();
    
	/**
	 * 備品一覧データ取得(Viewより取得)
	 * @return 備品一覧データ
	 */
    List<InventoryListDataModel> getViewAllRecords();

	/**
	 * 遷移先画面サイズ種別 取得(汎用マスタより取得)
	 * @return 画面サイズ種別
	 */
    String getWindowSize(String type, String code );    
}
