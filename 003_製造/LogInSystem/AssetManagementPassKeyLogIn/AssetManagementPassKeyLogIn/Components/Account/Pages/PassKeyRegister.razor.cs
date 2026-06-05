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


        // 入力フォーム用プロパティ
        protected string EmployeeId { get; set; } = string.Empty;
        protected string Password { get; set; } = string.Empty;

        protected bool IsProcessing { get; set; } = false;
        protected bool IsSuccess { get; set; } = false;
        protected string Message { get; set; } = string.Empty;

        /// <summary>
        /// 本人確認とパスキー新規登録処理
        /// </summary>
        public async Task CreateNewPasskey()
        {
            // 入力チェック
            if (string.IsNullOrWhiteSpace(EmployeeId) || string.IsNullOrWhiteSpace(Password))
            {
                IsSuccess = false;
                Message = "社員IDとパスワードを入力してください。";
                
                return;
            }

            IsProcessing = true;
            IsSuccess = false;

            try
            {
                Message = "社員IDとパスワードを検証中...";
                StateHasChanged();

                // MySQLのユーザーマスターと「社員ID」「パスワード」を照合する
                var appUserEntitty = await MySql.GetActiveUserByEmailAsync(EmployeeId);

                if (appUserEntitty is null)
                {
                    IsSuccess = false;
                    Message = "アカウントのログインIDが正しくありません。又は登録されていません。";

                    return;
                }

                // アカウントのロックアウト状態をチェック
                if (MySql.IsEntityLockOut(appUserEntitty))
                {
                    IsSuccess = false;
                    Message = "このアカウントは現在ロックアウトされています。しばらく時間をおいてからお試しください。";
                    
                    return;
                }

                // パスワードの検証
                // ※ 現在は平文運用のため直接比較します。今後ハッシュ化（PBKDF2）へ切り替える際は、
                // 下記のif文を「if (await UserManager.CheckPasswordAsync(user, Password))」に変更する
                if (appUserEntitty.PasswordHash != Password)
                {
                    // 失敗カウントを増やす等の処理をここに入れることも可能です
                    IsSuccess = false;
                    Message = "パスワードが正しくありません。";
                    
                    return;
                }

                // 本人確認が取れたので、その「社員番号（int）」を使ってパスキーの処理を進める
                var verifiedStaffNo = appUserEntitty.Id;
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
                    IsSuccess = false;
                    Message = resultMsg;
                }
            }
            catch (Exception ex)
            {
                IsSuccess = false;
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
