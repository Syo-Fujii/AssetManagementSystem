package application.resources.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import application.java.base.dbTablesModel.StaffAuthModel;

/**
 * マスタ：社員認証マスター（パスワード管理用） DB操作メソッド(Mapper)
 */
public interface StaffAuthMapper {
    
	/**
	 * 全件取得
	 * @return　社員認証マスタ(ConboBox等に表示するデータ)
	 * @brief ConboBoxの選択肢一覧取得の際などに用いるデータ<br>
	 */
    List<StaffAuthModel> selectAll();
  
	/**
	 * 対象データ取得(1件：一意Key)
	 * @return 社員認証マスタのデータ
	 * @brief [staff_no](主Key:一意)を条件として、取得する社員認証マスタのデータ()<br>
	 */
    List<StaffAuthModel> selectByNo(int no);

	/**
	 * 登録メールアドレス存在確認
     * @param emailAddr 対象メールアドレス
     * @param rejectStaffNo 除外社員番号
     * @return 確認結果
	 * @brief 除外社員番号を指定した場合、当該社員以外のメールアドレスを確認する(更新用)<br>
	 */
    boolean existsEmail(
            @Param("emailAddr") String emailAddr,
            @Param("staffNo") Integer rejectStaffNo
        );    
    
    /**
     * マスタ登録
     * @param row 社員認証マスタMODEL
     * @return 登録件数
	 * @brief ロックアウト終了日時・ログイン失敗回数は登録しない(初期値)<br>
     */
    Integer insStaffAuthOnes(StaffAuthModel row);
    
    /**
     * マスタ更新
     * @param row 社員認証マスタMODEL
     * @return 更新件数
	 * @brief ロックアウト終了日時・ログイン失敗回数は更新しない<br>
     */
    Integer updStaffAuthOnes(StaffAuthModel row); 
}
