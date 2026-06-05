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
    /// IdentityDbContext（認証機能）が使用するDB / クラスの定義を行う <br />
    /// IdentityDbContext(.NET標準機能):　ログイン失敗回数のカウント（AccessFailedCount）やロックアウト処理（LockoutEnd）など<br />
    /// DbSet[StaffAuth]：社員認証(パスワード管理)は書換先、ApplicationUser(継承：IdentityUser)を用いて操作する
    /// </remarks>
    public class ApplicationDbContext : IdentityDbContext<ApplicationUser, IdentityRole<int>, int>
    {

        /// <summary>
        /// DB-Entity対応(DbSet)：社員パスキーデータ
        /// </summary>
        public DbSet<StaffPasskey> StaffPasskeys { get; set; }

        //public DbSet<StaffMasterEntity> StaffMasters { get; set; }


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
        /// Identity（認証機能）Entitiy自動作成機能(継承)
        /// </summary>
        /// <param name="builder"></param>
        /// <remarks>
        /// 既存DBテーブルと一致させることで、EF Core が勝手に標準のテーブル（AspNetUsers など）を作ろうとするのを防ぐ。<br />
        /// </remarks>
        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);

            // Identityが標準で作るテーブル名を、[staff_auth]:社員認証マスター に合わせる場合の設定
            // ※「標準のユーザー機能は、AspNetUsers ではなく、すでに存在する [staff_auth] 使用する
            builder.Entity<ApplicationUser>(entity =>
            {
                entity.ToTable("staff_auth"); // テーブル名指定

                // カラム名の一致
                entity.Property(e => e.Id).HasColumnName("staff_no");
                entity.Property(e => e.Email).HasColumnName("email");
                entity.Property(e => e.PasswordHash).HasColumnName("password_hash");
                entity.Property(e => e.LockoutEnd).HasColumnName("lockout_end");
                entity.Property(e => e.AccessFailedCount).HasColumnName("access_failed_count");


                // 「staff_master」(社員マスター)テーブルを結合し、氏名(staff_name)を取得
                // ※ EF Core「Split Table / Shared Table」機能
                entity.SplitToTable("staff_master", table =>
                {
                    table.Property(e => e.Id).HasColumnName("staff_no"); // 主キーの結合条件
                    table.Property(e => e.Name).HasColumnName("name");
                });

                // 既存で未使用の[ApplicationUser]のプロパティを除外する
                entity.Ignore(e => e.ConcurrencyStamp);
                entity.Ignore(e => e.SecurityStamp);
                entity.Ignore(e => e.NormalizedEmail);
                entity.Ignore(e => e.NormalizedUserName);
                entity.Ignore(e => e.UserName);
                entity.Ignore(e => e.EmailConfirmed);
                entity.Ignore(e => e.PhoneNumber);
                entity.Ignore(e => e.PhoneNumberConfirmed);
                entity.Ignore(e => e.TwoFactorEnabled);
                entity.Ignore(e => e.LockoutEnabled);
            });

            // パスキー保存用テーブルの設定
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
