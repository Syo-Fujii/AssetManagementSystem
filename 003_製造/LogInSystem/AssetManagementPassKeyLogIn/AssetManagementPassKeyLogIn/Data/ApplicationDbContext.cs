using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace AssetManagementPassKeyLogIn.Data
{
    /// <summary>
    /// DB接続クラス
    /// </summary>
    /// <remarks>
    /// Identity（認証機能）が使用するDB / クラスの定義を行う
    /// </remarks>
    public class ApplicationDbContext : IdentityDbContext<ApplicationUser, IdentityRole<int>, int>
    {
        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="options"> DB情報 (接続情報:JSONファイル内容) </param>
        /// <remarks> 継承元[IdentityDbContext]の初期化を行う </remarks>
        public ApplicationDbContext( DbContextOptions<ApplicationDbContext> options ) : base(options)
        {
            // 継承先の定義なし
        }

        /// <summary>
        /// Identity（認証機能）
        /// </summary>
        /// <param name="builder"></param>
        protected override void OnModelCreating(ModelBuilder builder)
        {
            base.OnModelCreating(builder);

            // Identityが標準で作るテーブル名を、あなたの設計（staff_auth等）に合わせる場合の設定
            builder.Entity<ApplicationUser>(entity =>
            {
                entity.ToTable("staff_auth"); // 認証情報を保存するテーブル名
                entity.Property(e => e.Id).HasColumnName("staff_no"); // 主キーのカラム名を合わせる
            });

            // パスキー保存用テーブルの設定（前回のSQL案に合わせる場合）
            builder.Entity(entity =>
            {
                entity.ToTable("staff_passkeys");
                entity.Property(e => e.UserId).HasColumnName("staff_no");
            });
        }
    }
}
