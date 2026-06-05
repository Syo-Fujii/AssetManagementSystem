using AssetManagementPassKeyLogIn.Data;
using Microsoft.EntityFrameworkCore;

namespace AssetManagementPassKeyLogIn.Services
{
    /// <summary>
    /// MySQLデータベース操作専用サービス
    /// </summary>
    public class MySqlService
    {
        private readonly ApplicationDbContext dbContext;
   

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="context"> DBコンテキスト </param>
        public MySqlService(ApplicationDbContext context)
        {
            this.dbContext = context;
        }

        /// <summary>
        /// ログインID（メールアドレス）をキーに、有効な社員認証情報を取得する
        /// </summary>
        /// <param name="loginId"> ログインID </param>
        /// <returns> ログインユーザー(ApplicationUser) </returns>
        /// <remarks>
        /// [UserManager.FindByEmailAsync]を用いた取得では、どれだけC#側でプロパティを偽装しても<br />
        /// Ignore（無視）に指定されているプロパティが条件式に入っている時点で100%翻訳エラー（クラッシュ）を起こす為<br />
        /// [ApplicationDbContext]を用いて直接DBを操作する。
        /// </remarks>
        public Task<ApplicationUser?> GetActiveUserByEmailAsync(string loginId)
        {
            if (string.IsNullOrEmpty(loginId)) { return Task.FromResult<ApplicationUser?>(null); }

            // DbContextから直接、Emailが一致するユーザーをクエリします。
            return dbContext.Users.FirstOrDefaultAsync(u => !string.IsNullOrEmpty(u.Email) && u.Email.Equals(loginId));
        }

        /// <summary>
        /// ログインユーザーがLockOutされているか判定
        /// </summary>
        /// <returns> 判定結果 </returns>
        public bool IsEntityLockOut(ApplicationUser? entity)
        {
            if (entity is null) { return true; }
            
            return entity.LockoutEnd.HasValue && entity.LockoutEnd.Value > DateTimeOffset.UtcNow;
        }

    }
}
