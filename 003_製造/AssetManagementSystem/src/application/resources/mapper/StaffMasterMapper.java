package application.resources.mapper;

import java.util.List;

import application.java.base.dbTablesModel.StaffMasterModel;

/**
 * マスタ：社員マスタ DB操作メソッド(Mapper)
 */
public interface StaffMasterMapper {
    
	/**
	 * 全件取得
	 * @return　社員マスタ(ConboBox等に表示するデータ)
	 * @brief ConboBoxの選択肢一覧取得の際などに用いるデータ<br>
	 */
    List<StaffMasterModel> selectAll();
  
	/**
	 * (ComboBox用)社員マスターの取得
	 * @return 社員マスターの一覧
	 * @brief 論理削除しているユーザーは取得しない
	 */
    List<StaffMasterModel> getStaffMasterData();    
    
	/**
	 * 全件取得
	 * @return 社員マスタ(マスタメンテナンス用)
	 * @brief マスタメンテナンス画面での一覧取得に用いるデータ<br>
	 */
    List<StaffMasterModel> selectMaintenance();    
    
	/**
	 * 対象データ取得(1件：一意Key)
	 * @return 社員マスタのデータ
	 * @brief [staff_no](主Key:一意)を条件として、取得する社員マスタのデータ()<br>
	 */
    List<StaffMasterModel> selectByNo(int no);
    
    /**
     * マスタ存在確認 
     * @param data 社員マスタMODEL
     * @return 確認結果
     */
    Boolean existsStaffNo(StaffMasterModel data);
    
    // 新規登録
    void newRecord(int no);

    /**
     * マスタ登録
     * @param row 社員マスタMODEL
     * @return 更新件数
     */
    Integer insStaffMasterOnes(StaffMasterModel row);     
    
    /**
     * マスタ更新
     * @param row 社員マスタMODEL
     * @return 更新件数
     */
    Integer updStaffMasterOnes(StaffMasterModel row); 
}
