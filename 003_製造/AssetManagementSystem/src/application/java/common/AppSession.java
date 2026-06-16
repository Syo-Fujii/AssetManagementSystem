package application.java.common;

import application.java.base.dbTablesModel.StaffMasterModel;
import application.java.base.tableViewListModel.ApplicationUserModel;

/*
 * ログインユーザ情報 クラス
 * @brief ログインしたユーザ情報を保持する<br>
 * ユーザ情報は社員マスタ(StaffMaster)のデータ(Model:Entity)形式で保持する。
 */
public class AppSession {
    
	// ログインユーザー情報
    private static StaffMasterModel loginUser = null;

	/**
	 * [ログインユーザ情報]設定
	 * @param ログインユーザ情報(StaffMasterModel)
	 * @brief ログインしたユーザの情報[DB StaffMaster]を設定する
	 */  
    public static void setLoginUser(StaffMasterModel user) {
        AppSession.loginUser = user;
    }

	/**
	 * [ログインユーザ情報]取得
	 * @return ログインユーザ情報(StaffMasterModel)
	 * @brief ログインしたユーザの情報[DB StaffMaster]を取得する
	 */  
    public static StaffMasterModel getLoginUser() {
        return AppSession.loginUser;
    }

    /**
     * ログインしている社員番号（スタッフコード）を取得
     * @brief 未取得の場合は'-1'を返す
     */
    public static int getLoginStaffCode() {
        return loginUser != null ? loginUser.getStaffNo() : -1;
    }

    /**
     * ログインしているユーザの社員番号・氏名を取得
     */
    public static String getLoginUserInfo() {
        var stuffNo = loginUser != null ? loginUser.getStaffNo().toString() : "";
        var name = loginUser != null ? loginUser.getStaffName() : "";
    	
    	return "ID：" + stuffNo + " / NAME:"+ name ;
    }
    
    /**
     * ログインしているユーザの権限を取得
	 * @brief ログインしたユーザの権限を取得<br />
	 * ユーザー情報を(社員マスター)Entityにアップキャストして保持しているため<br />
	 * ダウンキャストして、権限(権限マスター)を取得する
     */
    public static int getUserPermission()
    {
    	if (loginUser instanceof ApplicationUserModel appUser) {
    	    return appUser.getPermissionMask();
    	}
    	
    	return 0;
    }
    
    /**
     * クリア(開放)処理
     */
    public static void clear() {
        loginUser = null;
    }
}
