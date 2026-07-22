package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.tableViewListModel.InventoryDetailsDataModel;

/**
 * 備品詳細画面 DB操作メソッド(Mapper)
 */
public interface InventoryDetailsMapper {

	/**
	 * 備品詳細データ取得(テーブルより取得)
	 * @return 備品データ(備品詳細に表示するデータ)
	 */
    List<InventoryDetailsDataModel> getTableDetailRecords(
    		Integer type, 
    		String code,
    		@Param("staffNo") Integer staffNo);
    
	/**
	 * 備品データの最終所在確認日更新
	 * @return 更新件数
	 */
    Integer updConfirmedDate(InventoryDetailsDataModel row);
    
	/**
	 * 備品詳細データ取得(Viewより取得)
	 * @return 備品データ(備品詳細に表示するデータ)
	 */
    List<InventoryDetailsDataModel> getViewAllRecords(Integer type, String code );

	/**
	 * 遷移先画面サイズ種別 取得(汎用マスタより取得)
	 * @return 画面サイズ種別
	 */
    String getWindowSize(String type, String code );
}
