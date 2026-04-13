package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.dbTablesModel.StockMasterModel;
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

    /**
     * マスタ存在確認 
     * @param data 備品マスタMODEL
     * @return 確認結果
     */
    Boolean existsStockMaster(StockMasterModel data);    
    
    /**
     * マスタ登録
     * @param row 備品マスタMODEL
     * @return 更新件数
     */
    Integer insStockMasterOnes(StockMasterModel row);     
    
    /**
     * マスタ更新
     * @param row 備品マスタMODEL
     * @return 更新件数
     */
    Integer updStockMasterOnes(StockMasterModel row);    
    
}
