package application.resources.mapper;

import java.util.List;

import application.java.base.dbTablesModel.AuthMasterModel;

/**
 * マスタ：権限マスタ DB操作メソッド(Mapper)
 */
public interface StaffAuthMapper {
    
	/**
	 * 全件取得
	 * @return　権限マスタ(ConboBox等に表示するデータ)
	 * @brief ConboBoxの選択肢一覧取得の際などに用いるデータ<br>
	 */
    List<AuthMasterModel> selectAll();
  
	/**
	 * (ComboBox用)権限マスターの取得
	 * @return 権限マスターの一覧
	 * @brief 論理削除している権限を含める
	 */
    List<AuthMasterModel> getAuthMasterData();    
    
	/**
	 * 全件取得
	 * @return 権限マスタ(マスタメンテナンス用)
	 * @brief マスタメンテナンス画面での一覧取得に用いるデータ<br>
	 */
    List<AuthMasterModel> selectMaintenance();    
    
	/**
	 * 対象データ取得(1件：一意Key)
	 * @return 権限マスタのデータ
	 * @brief [auth_id](主Key:一意)を条件として、取得する権限マスタのデータ()<br>
	 */
    List<AuthMasterModel> selectById(int id);
    
    /**
     * マスタ存在確認 
     * @param data 権限マスタMODEL
     * @return 確認結果
     */
    Boolean existsAuthId(AuthMasterModel data);
    
    /**
     * マスタ登録
     * @param row 権限マスタMODEL
     * @return 登録件数
     */
    Integer insAuthMasterOnes(AuthMasterModel row);
    
    /**
     * マスタ更新
     * @param row 権限マスタMODEL
     * @return 更新件数
     */
    Integer updAuthMasterOnes(AuthMasterModel row); 
}
