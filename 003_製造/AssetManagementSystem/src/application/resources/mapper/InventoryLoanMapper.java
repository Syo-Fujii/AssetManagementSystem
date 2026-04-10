package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.base.tableViewListModel.InventoryLoanDataModel;

/**
 * 備品貸出画面 DB操作メソッド(Mapper / interface)
 */
public interface InventoryLoanMapper {

	/**
	 * 備品貸出データ取得(テーブルより取得)
     * @param type Integer 備品分類
     * @param code String 備品コード
	 * @return 備品データ(備品貸出に表示するデータ 在庫データ)
	 */
    List<InventoryLoanDataModel> getTableReturnableStockData(
    		@Param("type") Integer type, 
    		@Param("code") String code );
    
	/**
	 * 備品貸出データ取得(Viewより取得)
     * @param type Integer 備品分類
     * @param code String 備品コード
	 * @return 備品データ(備品貸出に表示するデータ 在庫データ)
	 */
    List<InventoryLoanDataModel> getViewReturnableData(
    		@Param("type") Integer type, 
    		@Param("code") String code );

	/**
	 * 社員マスターの取得
	 * @return 社員マスターの一覧
	 * @brief 論理削除しているユーザーは取得しない
	 */
    List<StaffMasterModel> getStaffMasterData();
    
    /**
     * 備品データの更新(貸出処理)
     * @param row InventoryLoanDataModel 対象データ行
     * @return 更新件数
     */
     int updStockDataLoanOut(InventoryLoanDataModel row);
}
