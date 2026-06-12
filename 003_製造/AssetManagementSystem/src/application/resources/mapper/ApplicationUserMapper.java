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
	 * @return　ログインユーザ情報(社員マスター + 社員認証マスター（パスワード管理用）)
	 * @brief アカウントID(メールアドレス)を条件に、ログインユーザ情報を取得する<br>
	 */
    List<ApplicationUserModel> getLoginUserData(String loginId);
    
    /**
     * 社員認証マスタ更新
     * @param row ログインユーザー情報クラスMODEL
     * @return 更新件数
	 * @brief [staff_auth] の[lockout_end] (:ロックアウト終了日時)、[access_failed_count] (:ログイン失敗回数)を更新する<br>
	 * ※ ログインユーザー情報 ⇒ 社員マスタ[staff_master] + 社員認証マスター（パスワード管理用）[staff_auth]<br>
     */
    Integer updStaffAuthOnes(ApplicationUserModel row); 
}
