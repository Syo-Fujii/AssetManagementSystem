using AssetManagementPassKeyLogIn.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace AssetManagementPassKeyLogIn.Data
{
    /// <summary>
    /// DB接続クラス
    /// </summary>
    /// <remarks>
    /// Identity（認証機能）が使用するDB / クラスの定義を行う <br />
    /// Identity(.NET標準機能):　ログイン失敗回数のカウント（AccessFailedCount）やロックアウト処理（LockoutEnd）など
    /// </remarks>
    public class ApplicationDbContext : IdentityDbContext<ApplicationUser, IdentityRole<int>, int>
    {

        public DbSet<StaffPasskey> StaffPasskeys { get; set; }

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="options"> DB情報 (接続情報:JSONファイル内容) </param>
        /// <remarks> 継承元[IdentityDbContext]の初期化を行う </remarks>
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
        {
            // 継承先の定義なし
        }

        /// <summary>
        /// Identity（認証機能）
        /// </summary>
        /// <param name="builder"></param>
        /// <remarks>
        /// 既存DBテーブルと一致させることで、EF Core が勝手に標準のテーブル（AspNetUsers など）を作ろうとするのを防ぐ。<br />
        /// </remarks>
        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);

            // Identityが標準で作るテーブル名を、[staff_auth]:社員認証マスター に合わせる場合の設定
            builder.Entity<ApplicationUser>(entity =>
            {
                entity.ToTable("staff_auth"); // テーブル名指定

                // カラム名の一致
                entity.Property(e => e.Id).HasColumnName("staff_no");
                entity.Property(e => e.Email).HasColumnName("email");
                entity.Property(e => e.PasswordHash).HasColumnName("password_hash");
                entity.Property(e => e.LockoutEnd).HasColumnName("lockout_end");
                entity.Property(e => e.AccessFailedCount).HasColumnName("access_failed_count");
            });

            // パスキー保存用テーブルの設定（前回のSQL案に合わせる場合）
            builder.Entity<StaffPasskey>(entity =>
            {
                entity.ToTable("staff_passkeys");

                // CredentialId は byte[] なので、主キーとして明示
                entity.HasKey(e => e.CredentialId);
                entity.Property(e => e.CredentialId).HasColumnName("credential_id").HasMaxLength(512);
                entity.Property(e => e.StaffNo).HasColumnName("staff_no");
                entity.Property(e => e.FriendlyName).HasColumnName("friendly_name").HasMaxLength(100);
                entity.Property(e => e.PublicKey).HasColumnName("public_key");
                entity.Property(e => e.SignatureCount).HasColumnName("signature_count");
                entity.Property(e => e.CreatedAt).HasColumnName("created_at").HasDefaultValueSql("CURRENT_TIMESTAMP");
            });
        }
    }
}
