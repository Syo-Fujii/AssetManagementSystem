using Microsoft.AspNetCore.Identity;

namespace AssetManagementPassKeyLogIn.Data
{
    /// <summary>
    /// ユーザープロフィール情報
    /// </summary>
    /// <remarks>
    /// [ASP.NET Core] Identity(ログイン機能).IdentityUser(ユーザ情報)クラスを継承<br />
    /// IdentityUser　にて ID<?>、ユーザ名、メール、パスワードなどのプロパティが定義されている<br /> 
    /// IdentityUserにDBテーブル[staff_master]に合わせた項目を追加する<br />
    /// </remarks>
    public class ApplicationUser : IdentityUser<int>
    {
        /// <summary>
        /// 社員番号
        /// </summary
        /// <remarks> IdentityUserの[id]項目を社員番号とする </remarks>
        public int StaffNo
        {
            get => base.Id;
            set => base.Id = value;
        }

        /// <summary>
        /// 氏名
        /// </summary>
        public string? Name { get; set; }

    }

}
