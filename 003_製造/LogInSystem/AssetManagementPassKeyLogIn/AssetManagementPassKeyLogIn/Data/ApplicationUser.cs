using Microsoft.AspNetCore.Identity;

namespace AssetManagementPassKeyLogIn.Data
{
    /// <summary>
    /// ユーザープロフィール情報([staff_auth]テーブル)
    /// </summary>
    /// <remarks>
    /// [ASP.NET Core] Identity(ログイン機能).IdentityUser(ユーザ情報)クラスを継承<br />
    /// IdentityUser　にて ID、ユーザ名、メール、パスワードなどのプロパティが定義されている<br />
    /// IdentityUserは[staff_auth]テーブルを取得元とする<br />
    /// IdentityUser[int]を継承することで主キーを int (staff_no) に設定 <br />
    /// IdentityUserにDBテーブル[staff_master]に合わせた項目を追加する<br />
    /// ※ 注意：PasswordHash, Email, LockoutEnd, AccessFailedCount は基底クラス (IdentityUser) <br /> 
    /// に定義されているものをそのまま使用し、DbContext の方でカラム名をマッピングします。
    /// </remarks>
    public class ApplicationUser : IdentityUser<int>
    {
        /// <summary>
        /// 社員氏名
        /// </summary>
        public string? Name { get; set; }

        /// <summary>
        /// 削除フラグ
        /// </summary>
        public bool? Del { get; set; }
    }
}
