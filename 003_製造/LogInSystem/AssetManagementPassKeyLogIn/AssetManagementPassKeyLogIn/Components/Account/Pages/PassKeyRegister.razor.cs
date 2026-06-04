using AssetManagementPassKeyLogIn.Services;
using Microsoft.AspNetCore.Components;
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
        [Inject] private NavigationManager NavigationManager { get; set; } = default!;
        [Inject] private PasskeyService PasskeyOp { get; set; } = default!;

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
                // ※ このメソッド（VerifyEmployeeCredentialsAsync）を次ステップで PasskeyService 等に実装します
                //int? verifiedStaffNo = await PasskeyOp.VerifyEmployeeCredentialsAsync(EmployeeId, Password);
                int? verifiedStaffNo = 2;

                if (verifiedStaffNo is null)
                {
                    IsSuccess = false;
                    Message = "社員IDまたはパスワードが正しくありません。";

                    return;
                }

                // 本人確認が取れたので、その「社員番号（int）」を使ってパスキーの処理を進める
                Message = "パスキー登録を初期化中（スマートフォンを起動します）...";
                StateHasChanged();

                // サーバー（FIDO2）側で登録用のオプション（チャレンジ等）を生成
                var registerOptions = await PasskeyOp.GetRegisterOptionsAsync(verifiedStaffNo.Value);

                // JavaScriptの「createCredential」を呼び出してスマホの生体認証を起動
                var module = await JS.InvokeAsync<IJSObjectReference>("import", "./js/webauthn.js?v=1");
                var credentialJson = await module.InvokeAsync<JsonElement>("createCredential", CancellationToken.None, registerOptions);

                // スマホから戻ってきた鍵データをMySQLに保存
                string resultMsg = await PasskeyOp.VerifyAndSaveRegisterAsync(credentialJson, registerOptions, verifiedStaffNo.Value);

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
