package application.resources.mapper;

import application.java.base.dbTablesModel.StockDataModel;

/**
 * トラン：備品データ DB操作メソッド(Mapper)
 */
public interface StockDataMapper {

    /**
     * 備品データ存在確認 
     * @param data 備品データMODEL
     * @return 確認結果
     * @brief 既にシリアルナンバーが登録されている(備品データが存在している)かを確認。
     */
    Boolean existsStockDataSerial(StockDataModel data);    
    
    /**
     * 備品データ登録
     * @param row 備品データMODEL
     * @return 更新件数
     */
    Integer insStockDataOnes(StockDataModel row);     
    
    /**
     * 備品データ更新
     * @param row 備品データMODEL
     * @return 更新件数
     */
    Integer updStockDataOnes(StockDataModel row);    
 
	/**
	 * 備品データの最終所在確認日更新
	 * @return 更新件数
	 */
    Integer updConfirmedDate(StockDataModel row);
}
