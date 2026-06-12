using System.Security.Cryptography;

namespace AssetManagementPassKeyLogIn.Services
{
    /// <summary>
    /// HASH値操作専用サービス
    /// </summary>
    public class HashConverter
    {
        /// <summary>
        /// 対象文字(パスワード)：ハッシュ値比較
        /// </summary>
        /// <param name="passWord"></param>
        /// <param name="hashedValue"></param>
        /// <param name="separator"> 区切り文字(初期値：$) </param>
        /// <returns> 判定結果 </returns>
        /// <remarks>
        /// 平文パスワードが、指定されたPBKDF2-SHA256ハッシュ値と一致するか検証する。<br />
        /// 形式: $アルゴリズム$ストレッチング回数$ソルト$ハッシュ値<br />
        /// "$pbkdf2-sha256$[ストレッチング回数]$[ソルト値]=$[ハッシュ値]="
        /// </remarks>
        public bool VerifyPasswordSHA256PBKDF2(string passWord, string hashedValue, char separator = '$')
        {
            if (string.IsNullOrEmpty(passWord) || string.IsNullOrEmpty(hashedValue)) return false;

            // HASH値を分割
            var parts = hashedValue.Split(separator);

            // 形式が不正な場合は検証不可
            if (parts.Length < 5 || parts[1] != "pbkdf2-sha256") return false;

            try
            {
                // 各要素の抽出
                int iterations = int.Parse(parts[2]);
                byte[] salt = Convert.FromBase64String(parts[3]);
                byte[] storedHash = Convert.FromBase64String(parts[4]);

                // 入力された平文パスワードを、同じソルトと回数、SHA256でハッシュ化
                byte[] generatedHash = Rfc2898DeriveBytes.Pbkdf2(
                    passWord,
                    salt,
                    iterations,
                    HashAlgorithmName.SHA256,
                    storedHash.Length);

                // 固定時間比較（タイミング攻撃対策）で完全に一致するかチェック
                return CryptographicOperations.FixedTimeEquals(storedHash, generatedHash);
            }
            catch (Exception)
            {
                return false;
            }
        }

    }
}
