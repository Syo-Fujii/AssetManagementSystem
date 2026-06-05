using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace AssetManagementPassKeyLogIn.Entities
{
    /// <summary>
    /// DBテーブル [staff_auth]:社員認証マスター（パスワード管理用） エンティティ(MODEL)
    /// </summary>
    [Table("staff_auth")]
    public class StaffAuth
    {
        /// <summary>
        /// 社員番号
        /// </summary>
        [Key]
        [Column("staff_no")]
        public int StaffNo { get; set; }

        /// <summary>
        /// メールアドレス(ログインID)
        /// </summary>
        [Column("email")]
        [StringLength(256)]
        public string? Email { get; set; }

        /// <summary>
        /// パスワード(ハッシュ値)
        /// </summary>
        [Column("password_hash")]
        [StringLength(2000)]
        public string? PasswordHash { get; set; }

        /// <summary>
        /// ロックアウト終了日時
        /// </summary>
        [Column("lockout_end")]
        public DateTime? LockoutEnd { get; set; }

        /// <summary>
        /// ログイン失敗回数
        /// </summary>
        /// <remarks> 自動登録・更新 / 初期値:0 </remarks>
        [Column("access_failed_count")]
        public int AccessFailedCount { get; set; } = 0;

        /// <summary>
        /// 登録・更新日時
        /// </summary>
        /// <remarks> 自動登録・更新 / 初期値:現在日時 </remarks>
        [Column("updated_at")]
        public DateTime UpdatedAt { get; set; }
    }
}
