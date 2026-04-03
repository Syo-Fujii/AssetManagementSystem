package application.resources.mapper;

import java.util.List;

import application.java.base.dbTablesModel.StockTypeMasterModel;

/**
 * マスタ：備品分類マスタ DB操作メソッド(Mapper)
 */
public interface StockTypeMasterMapper {
    
	/**
	 * 全件取得
	 * @return　備品分類マスタ(ConboBox等に表示するデータ)
	 * @brief ConboBoxの選択肢一覧取得の際などに用いるデータ<br>
	 */
    List<StockTypeMasterModel> selectAll();
  
	/**
	 * 全件取得
	 * @return　備品分類マスタ(マスタメンテナンス用)
	 * @brief マスタメンテナンス画面での一覧取得に用いるデータ<br>
	 */
    List<StockTypeMasterModel> selectMaintenance();    
    
    /**
     * マスタ存在確認 
     * @param data 備品分類マスタMODEL
     * @return 確認結果
     */
    Boolean existsStockType(StockTypeMasterModel data);

	/**
	 * 対象データ取得(1件：一意Key)
	 * @return　備品分類マスタのデータ
	 * @brief [id](主Key:一意)を条件として、取得する備品分類マスタのデータ()<br>
	 */
    StockTypeMasterModel selectById(int id);
    
    // 新規登録
    void insertUser(StockTypeMasterModel user);

    /**
     * マスタ登録
     * @param row 備品分類マスタMODEL
     * @return 更新件数
     */
    Integer insStockTypeMasterOnes(StockTypeMasterModel row);     
    
    /**
     * マスタ更新
     * @param row 備品分類マスタMODEL
     * @return 更新件数
     */
    Integer updStockTypeMasterOnes(StockTypeMasterModel row); 
}
