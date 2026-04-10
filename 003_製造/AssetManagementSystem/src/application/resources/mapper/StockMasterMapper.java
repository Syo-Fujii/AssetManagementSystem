package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.tableViewListModel.StockMasterDataModel;

/**
 * マスタ：備品マスタ DB操作メソッド(Mapper)
 */
public interface StockMasterMapper {

	/**
	 * 備品マスタ 明細用一覧の取得(テーブルより取得)
	 * @return 備品マスタ(明細に表示するデータ)
	 */
    List<StockMasterDataModel> getTableStockMasterDetailRecords(
    		@Param("type") Integer type,
    		@Param("serialNo") String serialNo,
    		@Param("name") String name,
    		@Param("model") String model,
    		@Param("status") Boolean status,
    		@Param("del") Boolean del);	
}
