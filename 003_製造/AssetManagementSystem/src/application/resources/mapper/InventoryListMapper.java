package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.tableViewListModel.InventoryListDataModel;

/**
 * 備品一覧画面 DB操作メソッド(Mapper)
 */
public interface InventoryListMapper {

	/**
	 * 備品一覧データ取得(テーブルより取得)
	 * @return 備品データ(備品一覧に表示するデータ)
	 */
    List<InventoryListDataModel> getTableRecords(
    	    @Param("authId") int authId, 
    	    @Param("staffNo") Integer staffNo);
    
	/**
	 * 備品一覧データ取得(Viewより取得)
	 * @return 備品データ(備品一覧に表示するデータ)
	 */
    List<InventoryListDataModel> getViewAllRecords();

	/**
	 * 遷移先画面サイズ種別 取得(汎用マスタより取得)
	 * @return 画面サイズ種別
	 */
    String getWindowSize(String type, String code );    
}
