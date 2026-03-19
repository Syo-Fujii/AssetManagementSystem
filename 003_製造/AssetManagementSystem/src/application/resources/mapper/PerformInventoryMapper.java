package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.tableViewListModel.InventoryDetailsDataModel;
import application.java.base.tableViewListModel.PerformInventoryDataModel;

/**
 * 棚卸画面 DB操作メソッド(Mapper)
 */
public interface PerformInventoryMapper {

	/**
	 * 棚卸(備品データ)取得(テーブルより取得)
	 * @return 備品データ(棚卸に表示するデータ)
	 */
    List<PerformInventoryDataModel> getTableInventoryRecords(@Param("defaultDate") String defaultDate);
    
	/**
	 * 備品データの最終所在確認日 更新
	 * @return 更新件数
	 */
    Integer updConfirmedDate(PerformInventoryDataModel row);
    
	/**
	 * 備品マスタの備考 更新
	 * @return 更新件数
	 */
    Integer updMasterRemarks(PerformInventoryDataModel row);
    
    /**
	 * 備品詳細データ取得(Viewより取得)
	 * @return 備品データ(備品詳細に表示するデータ)
	 */
    List<InventoryDetailsDataModel> getViewAllRecords(Integer type, String code );
}
