package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.ApplicationUserModel;

/**
 * ログインユーザー DB操作メソッド(Mapper)
 */
public interface ApplicationUserMapper {
    
	/**
	 * ログインユーザ情報取得
     * @param loginId アカウントID(メールアドレス)
	 * @return ログインユーザ情報(社員マスター + 社員認証マスター（パスワード管理用） + 権限)<br>
	 * @brief アカウントID(メールアドレス)を条件に、ログインユーザ情報を取得する<br>
	 */
    List<ApplicationUserModel> getLoginUserData(String loginId);

	/**
	 * ログインユーザ情報取得
	 * @param no 社員番号
	 * @return ログインユーザ情報(社員マスター + 権限)
	 * @brief 社員番号を条件に、ログインユーザ情報を取得する<br>
	 * PassKey認証が成功した際、Response(JSON)より取得した[社員番号]を条件とする
	 */
    List<ApplicationUserModel> getLoginUserByNo(int no);
    
    /**
     * 社員認証マスタ更新
     * @param row ログインユーザー情報クラスMODEL
     * @return 更新件数
	 * @brief [staff_auth] の[lockout_end] (:ロックアウト終了日時)、[access_failed_count] (:ログイン失敗回数)を更新する<br>
	 * ※ ログインユーザー情報 ⇒ 社員マスタ[staff_master] + 社員認証マスター（パスワード管理用）[staff_auth]<br>
     */
    Integer updStaffAuthOnes(ApplicationUserModel row); 
}
