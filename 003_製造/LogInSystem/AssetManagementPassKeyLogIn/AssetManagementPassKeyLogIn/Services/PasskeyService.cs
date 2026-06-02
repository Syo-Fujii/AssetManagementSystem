using AssetManagementPassKeyLogIn.Data;
using Fido2NetLib;
using Fido2NetLib.Objects;
using Microsoft.EntityFrameworkCore;
using System.Text.Json;

namespace AssetManagementPassKeyLogIn.Services
{
    /// <summary>
    /// FIDO2/パスキー暗号処理クラス(バックエンドロジック)
    /// </summary>
    public class PasskeyService
    {
        // FIDO2（ファイドツー）
        private readonly IFido2 fido2Interface;
        // DB操作(Context)
        private readonly ApplicationDbContext mySqlDbContext;

        /// <summary>
        /// コンストラクタ
        /// </summary>
        /// <param name="fido2"> FID2(インターフェース) </param>
        /// <param name="db"> DBオブジェクト(Context) </param>
        public PasskeyService(IFido2 fido2, ApplicationDbContext db)
        {
            this.fido2Interface = fido2;
            mySqlDbContext = db;
        }

        /// <summary>
        /// ログイン開始：チャレンジ（合言葉）生成処理(非同期)
        /// </summary>
        /// <param name="staffNo"> 社員番号 </param>
        /// <returns></returns>
        /// <remarks>
        /// 社員番号を条件に、DBよりパスキーを取得する<br />
        /// DB操作を伴う為、非同期とする
        /// </remarks>
        public async Task<AssertionOptions> GetAssertionOptionsAsync(int staffNo)
        {
            // DBからその社員の登録済みパスキーを取得
            var existingCredentials = await mySqlDbContext.StaffPasskeys
                .Where(p => p.StaffNo == staffNo)
                .Select(p => new PublicKeyCredentialDescriptor(p.CredentialId))
                .ToListAsync();

            if (!existingCredentials.Any())
            {
                throw new Exception("パスキーが登録されていません。");
            }


            // 認証オプションの生成(Paramsオブジェクトを作成)
            var authParams = new GetAssertionOptionsParams
            {
                AllowedCredentials = existingCredentials,
                UserVerification = UserVerificationRequirement.Preferred
            };

            return fido2Interface.GetAssertionOptions(authParams);

        }

        /// <summary>
        /// ログイン開始：QRコード待ち受け用のチャレンジ（合言葉）生成処理
        /// </summary>
        /// <remarks>
        /// QRコード待ち受け用に、ユーザーを指定しない認証待ちを生成する
        /// </remarks>
        public AssertionOptions GetAssertionOptionsForQrCode()
        {
            // QRコード待ち受け時は、誰が来るか分からないため AllowedCredentials は指定しません
            var authParams = new GetAssertionOptionsParams
            {
                UserVerification = UserVerificationRequirement.Preferred
            };

            // fido2-net-lib が自動的に「誰でもウェルカム」な認証オプションを作ってくれます
            return fido2Interface.GetAssertionOptions(authParams);
        }

        /// <summary>
        /// ログイン完了：ブラウザから届いた署名を検証する(非同期)
        /// </summary>
        /// <param name="rawResponse"> ブラウザからのデータ(AuthenticatorAssertionRawResponse) </param>
        /// <param name="options"></param>
        /// <param name="staffNo"> 社員番号 </param>
        /// <returns> 認証可否 </returns>
        /// <remarks>
        /// 条件とした社員番号のブラウザ画面結果を検証する<br />
        /// DB操作を伴う為、非同期とする
        /// </remarks>
        public async Task<bool> VerifyAssertionAsync(AuthenticatorAssertionRawResponse rawResponse, AssertionOptions options, int staffNo)
        {
            // rawResponse検証： IDを比較して、DBテーブル(DbSet)から該当行(データ)を取得
            var credential = await mySqlDbContext.
                StaffPasskeys.
                FirstOrDefaultAsync(p => p.StaffNo == staffNo && p.CredentialId == rawResponse.RawId);

            if (credential is null) { return false; }

            // FID2認証パラメータ生成
            var makeParams = new MakeAssertionParams
            {
                AssertionResponse = rawResponse,                          // ブラウザから届いた生データ
                OriginalOptions = options,                                // サーバーのドメイン
                StoredPublicKey = credential.PublicKey,                   // DBに保存されている公開鍵
                StoredSignatureCounter = (uint)credential.SignatureCount, // 現在の署名回数
                IsUserHandleOwnerOfCredentialIdCallback = async (args, cancellationToken) => true
            };

            // MakeAssertionAsync(認証判定:暗号の検証)処理 呼出
            var res = await fido2Interface.MakeAssertionAsync( makeParams );

            // 結果判定
            if (res is not null)
            {
                // 署名カウンタを更新(行データ)
                credential.SignatureCount = (int)res.SignCount;
                // DB更新
                await mySqlDbContext.SaveChangesAsync();
                return true;
            }

            return false;
        }

        /// <summary>
        /// ログイン完了：ブラウザから届いた署名を検証する(ユーザ含む；非同期)
        /// </summary>
        /// <param name="rawResponseJson"> ブラウザからのデータ(JSONファイル：JsonElement) </param>
        /// <param name="options"></param>
        /// <returns> 認証可否(可:社員番号,否：-1) </returns>
        /// <remarks>
        /// ブラウザ画面結果(JSONファイル)より、社員番号を割出し、結果を検証する<br />
        /// DB操作を伴う為、非同期とする
        /// </remarks>
        public async Task<int> VerifyAssertionAsync(JsonElement rawResponseJson, AssertionOptions options)
        {
            try
            {
                // JSON(JavaScriptから)を、デシリアライズ(AuthenticatorAssertionRawResponse型)
                var rawResponse = rawResponseJson.Deserialize<AuthenticatorAssertionRawResponse>();

                if (rawResponse is null) { return -1; }

                // RowIDが一致するDBテーブル(DbSet)から該当行(データ:条件 識別Key)を取得
                var credential = await mySqlDbContext.StaffPasskeys
                    .FirstOrDefaultAsync(p => p.CredentialId == rawResponse.RawId);

                if (credential is null) { return -1; }

                // FID2認証パラメータ生成
                var makeParams = new MakeAssertionParams
                {
                    AssertionResponse = rawResponse,                          // スマホから届いた生データ
                    OriginalOptions = options,                                // サーバー設定
                    StoredPublicKey = credential.PublicKey,                   // DBに保存されている公開鍵
                    StoredSignatureCounter = (uint)credential.SignatureCount, // 現在の署名回数
                    IsUserHandleOwnerOfCredentialIdCallback = async (args, cancellationToken) => true
                };

                // MakeAssertionAsync(認証判定:暗号の検証)処理 呼出
                var res = await fido2Interface.MakeAssertionAsync(makeParams);

                if (res is not null)
                {
                    // 署名カウンタを更新(行データ)
                    credential.SignatureCount = (int)res.SignCount;
                    // DB更新
                    await mySqlDbContext.SaveChangesAsync();

                    return credential.SignatureCount;
                }

                return -1;
            }
            catch (Exception)
            {
                // ログ出力など

                return -1;
            }
        }

    }
}
