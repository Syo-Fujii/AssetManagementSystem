using AssetManagementPassKeyLogIn.Services;
using Microsoft.AspNetCore.Components;
using Microsoft.JSInterop;
using System.Text.Json;

namespace AssetManagementPassKeyLogIn.Components.Account.Pages
{
    public partial class TestPassKeyRegister
    {
        /* 各サービス呼出 */
        [Inject] private IJSRuntime JS { get; set; } = default!;
        [Inject] private NavigationManager NavigationManager { get; set; } = default!;
        [Inject] private PasskeyService PasskeyOp { get; set; } = default!;

        // プロパティ
        protected int StaffNo { get; set; } = 1;
        protected bool IsProcessing { get; set; } = false;
        protected bool IsSuccess { get; set; } = false;
        protected string Message { get; set; } = string.Empty;

        /// <summary>
        /// パキー新規登録処理
        /// </summary>
        public async Task CreateNewPasskey()
        {
            IsProcessing = true;
            Message = "登録の初期化中...";
            StateHasChanged();

            try
            {
                // 1. サーバー（FIDO2）側で登録用のオプション（チャレンジ等）を生成
                // ※前回のステップで PasskeyService.cs に追加したメソッドを呼び出します
                var registerOptions = await PasskeyOp.GetRegisterOptionsAsync(StaffNo);

                // 2. wwwroot内のwebauthn.jsから「createCredential（1番上の関数）」を呼び出す
                var module = await JS.InvokeAsync<IJSObjectReference>("import", "./js/webauthn.js?v=1");
                var credentialJson = await module.InvokeAsync<JsonElement>("createCredential", CancellationToken.None, registerOptions);

                // 3. スマホ/PCから戻ってきた鍵データをMySQLに保存
                // ※前回のステップで PasskeyService.cs に追加したメソッドを呼び出します
                string resultMsg = await PasskeyOp.VerifyAndSaveRegisterAsync(credentialJson, registerOptions, StaffNo);

                if (resultMsg == "SUCCESS")
                {
                    IsSuccess = true;
                    Message = $"社員番号 {StaffNo} のパスキー登録に成功しました！MySQLを確認してください。";
                }
                else
                {
                    IsSuccess = false;
                    Message = resultMsg; // 💡 サーバーから戻ってきた具体的なエラー内容をそのまま画面に出す
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
