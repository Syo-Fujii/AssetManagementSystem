package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.InventoryLoanDataModel;
import application.java.base.tableViewListModel.InventoryReturnDataModel;

/**
 * 備品返却画面 DB操作メソッド(Mapper / interface)
 */
public interface InventoryReturnMapper {

	/**
	 * 備品返却データ取得(テーブルより取得)
	 * @return 備品データ(備品返却に表示するデータ 貸出中データ)
	 */
    List<InventoryReturnDataModel> getTableLoanableStockData(Integer type, String code );
    
	/**
	 * 備品貸出データ取得(Viewより取得)
	 * @return 備品データ(備品返却に表示するデータ 貸出中データ)
	 */
    List<InventoryLoanDataModel> getViewLoanableData(Integer type, String code );
    
    /**
     * 備品データの更新(返却処理)
     * @param row InventoryReturnDataModel 対象データ行
     * @return 更新件数
     */
    int updStockDataReturn(InventoryReturnDataModel row);
     
     /**
      * 備品データの登録(新規在庫・返却したシリアルNoの登録)
      * @param row InventoryReturnDataModel 対象データ行
      * @return 更新件数
      */
    int insCopySerialNewStockData(InventoryReturnDataModel row);
}
