using AssetManagementPassKeyLogIn.Data;
using AssetManagementPassKeyLogIn.Services;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Identity;
using Microsoft.JSInterop;
using System.Text.Json;

namespace AssetManagementPassKeyLogIn.Components.Account.Pages
{
    /// <summary>
    /// パスキー登録画面
    /// </summary>
    public partial class PassKeyRegister
    {
        /* 各サービス呼出 */
        [Inject] private IJSRuntime JS { get; set; } = default!;
        [Inject] private PasskeyService PasskeyOp { get; set; } = default!;
        [Inject] private UserManager<ApplicationUser> UserManager { get; set; } = default!;
        [Inject] private MySqlService MySql { get; set; } = default!;
        [Inject] private HashConverter Hash { get; set; } = default!;


        // 入力フォーム用プロパティ
        protected string EmployeeId { get; set; } = string.Empty;
        protected string Password { get; set; } = string.Empty;

        protected bool IsProcessing { get; set; } = false;
        protected bool IsSuccess { get; set; } = false;
        protected string Message { get; set; } = string.Empty;

        /// <summary>
        /// 本人確認とパスキー新規登録処理
        /// </summary>
        public async Task CreateNewPasskey(int failedMaxCount = 10, int lockOutWaitTime = 10)
        {
            IsProcessing = true;
            IsSuccess = false;

            // 入力チェック
            if (string.IsNullOrWhiteSpace(EmployeeId) || string.IsNullOrWhiteSpace(Password))
            {
                Message = "社員IDとパスワードを入力してください。";
                return;
            }

            try
            {
                Message = "社員IDとパスワードを検証中...";
                StateHasChanged();

                // MySQLのユーザーマスターと「社員ID」「パスワード」を照合する
                var appUserEntitty = await MySql.GetActiveUserByEmailAsync(EmployeeId);

                if (appUserEntitty is null)
                {
                    Message = "アカウントのログインIDが正しくありません。又は登録されていません。";
                    return;
                }

                if (appUserEntitty.Del is true)
                {
                    Message = "アカウントは削除されました。このアカウントは使用できません。";
                    return;
                }

                // アカウントのロックアウト状態をチェック
                if (MySql.IsEntityLockOut(appUserEntitty))
                {
                    Message = "このアカウントは現在ロックアウトされています。しばらく時間をおいてからお試しください。";
                    return;
                }

                // パスワードの検証(HASH：SHA256 - PBKDF2)
                if (! Hash.VerifyPasswordSHA256PBKDF2(Password, appUserEntitty?.PasswordHash ?? string.Empty))
                {
                    if (appUserEntitty is null) { return; }
                    
                    // 失敗カウントを1増やす
                    appUserEntitty.AccessFailedCount++;

                    if (appUserEntitty.AccessFailedCount >= failedMaxCount)
                    {
                        // 10回以上失敗した場合は、現在時刻から10分間のロックアウトを設定
                        appUserEntitty.LockoutEnd = DateTimeOffset.UtcNow.AddMinutes(lockOutWaitTime);

                        Message = $"パスワードが{failedMaxCount}回連続で間違っているため、アカウントがロックアウトされました。" +
                                  $"{lockOutWaitTime}分後にお試しください。";
                    }
                    else
                    {
                        // あと何回間違えられるかをユーザーに親切に通知
                        int remainingAttempts = 10 - appUserEntitty.AccessFailedCount;
                        Message = $"パスワードが正しくありません。（ロックアウトまであと {remainingAttempts} 回）";
                    }

                    // 失敗カウントとロックアウト状態をMySQLデータベースへ保存（更新）する
                    await MySql.UpdateApplicationUserAsync(appUserEntitty);
                    return;
                }

                if ((appUserEntitty?.AccessFailedCount ?? 0) > 0)
                {
                    if (appUserEntitty is null) { return; }

                    appUserEntitty?.AccessFailedCount = 0;
                    appUserEntitty?.LockoutEnd = null; // ロックアウト期間も消去

                    await MySql.UpdateApplicationUserAsync(appUserEntitty);
                }

                // 本人確認が取れたので、その「社員番号（int）」を使ってパスキーの処理を進める
                var verifiedStaffNo = appUserEntitty?.Id ?? -1;
                Message = "パスキー登録を初期化中（スマートフォンを起動します）...";
                StateHasChanged();

                // サーバー（FIDO2）側で登録用のオプション（チャレンジ等）を生成
                var registerOptions = await PasskeyOp.GetRegisterOptionsAsync(verifiedStaffNo);

                // JavaScriptの「createCredential」を呼び出してスマホの生体認証を起動
                var module = await JS.InvokeAsync<IJSObjectReference>("import", "./js/webauthn.js?v=1");
                var credentialJson = await module.InvokeAsync<JsonElement>("createCredential", CancellationToken.None, registerOptions);

                // スマホから戻ってきた鍵データをMySQLに保存
                string resultMsg = await PasskeyOp.VerifyAndSaveRegisterAsync(credentialJson, registerOptions, verifiedStaffNo);

                if (resultMsg == "SUCCESS")
                {
                    IsSuccess = true;
                    Message = $"社員ID: {EmployeeId} のパスキー登録に成功しました！ログイン画面からサインインを行ってください。";
                    // 入力欄をクリア
                    EmployeeId = string.Empty;
                    Password = string.Empty;
                }
                else
                {
                    Message = resultMsg;
                }
            }
            catch (Exception ex)
            {
                Message = $"登録がキャンセルされたかエラーです: {ex.Message} / {ex.InnerException?.Message}";
            }
            finally
            {
                IsProcessing = false;
                StateHasChanged();
            }
        }
    }
}
