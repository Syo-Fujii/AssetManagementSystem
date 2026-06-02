using AssetManagementPassKeyLogIn.Services;
using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.JSInterop;
using QRCoder;
using System.Text.Json;

namespace AssetManagementPassKeyLogIn.Components.Account.Shared
{
    /// <summary>
    /// QRコード認証[PassKey認証]クラス(コードビハインド構成)
    /// </summary>
    public partial class PasskeyQrCode
    {
        /* 各サービス呼出(インスタンス化したクラスの呼出) */
        // JSファイル(wwwroot/WebAuthn.js)呼出
        [Inject] private IJSRuntime JS { get; set; } = default!;
        // (web)Page遷移
        [Inject] private NavigationManager Navigation { get; set; } = default!;
        // パスキー処理クラス(自作)
        [Inject] private PasskeyService PasskeyOp { get; set; } = default!;
        // ユーザー認証状態
        [Inject] private AuthenticationStateProvider AuthStateProvider { get; set; } = default!;



        /// <summary>
        /// 認証成功イベント
        /// </summary>
        /// <remarks>
        /// 親画面（Login.razor）に「認証成功」と通知するためのイベントコールバック
        /// </remarks>
        [Parameter]
        public EventCallback<int> OnAuthSuccess { get; set; }

        /// <summary>
        /// QRコード画像保存場所
        /// </summary>
        protected string QrCodeImageSrc { get; set; } = string.Empty;

        /// <summary>
        /// エラー内容
        /// </summary>
        protected string ErrorMessage { get; set; } = string.Empty;

        /// <summary>
        /// 処理フラグ
        /// </summary>
        protected bool IsProcessing { get; set; } = false;

        /// <summary>
        /// 社員番号
        /// </summary>
        protected int TargetStaffCode { get; set; } = -1;


        /// <summary>
        /// 画面描画処理
        /// </summary>
        /// <param name="firstRender"> 画面が初めて開いた瞬間判定 </param>
        /// <remarks> 
        /// HTML画面の組み立てが完全に終わった後（＝OnAfterRenderAsync）にwebauthn.jsを呼び出す<br />
        /// [ComponentBase.OnAfterRenderAsync]継承
        /// </remarks>
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (firstRender)
            {
                await InitializeQrCodeAndAuthWaiting();
            }
        }

        /// <summary>
        /// QR生成 + 認証待ち処理
        /// </summary>
        private async Task InitializeQrCodeAndAuthWaiting()
        {
            this.ErrorMessage = string.Empty;
            this.IsProcessing = true; //処理中(フラグON)

            StateHasChanged(); // partial クラス内なのでそのまま呼び出せます

            try
            {
                var assertionOptions = PasskeyOp.GetAssertionOptionsForQrCode();
                if (assertionOptions is null)
                {
                    ErrorMessage = "認証の初期化に失敗しました。";
                    return;
                }

                string targetUrl = $"{Navigation.BaseUri}account/login?auth_state=active";

                GenerateQrCode(targetUrl);

                var module = await JS.InvokeAsync<IJSObjectReference>("import", "./js/webauthn.js");
                var credentialJson = await module.InvokeAsync<JsonElement>("getAssertionConditional", assertionOptions);

                this.TargetStaffCode = await PasskeyOp.VerifyAssertionAsync(credentialJson, assertionOptions);

                if (this.TargetStaffCode.Equals(-1))
                {
                    ErrorMessage = "認証エラー：パスキーの検証に失敗しました。";
                    return;
                }

                // 認証成功：
                var uri = Navigation.ToAbsoluteUri(Navigation.Uri);

                // URLの末尾から ?returnUrl= の中身を抽出する
                if (Microsoft.AspNetCore.WebUtilities.QueryHelpers.ParseQuery(uri.Query).TryGetValue("returnUrl", out var returnUrl))
                {
                    // 認証前に見ようとしていた画面へ遷移
                    Navigation.NavigateTo(returnUrl!, forceLoad: true);
                }
                else
                {
                    // 特に指定がない場合は標準のダッシュボード(page)へ遷移
                    Navigation.NavigateTo("/dashboard", forceLoad: true);
                }
            }
            catch (Exception)
            {
                ErrorMessage = $"認証がキャンセルされたか、エラーが発生しました。";
            }
            finally
            {
                this.IsProcessing = false; //処理中(フラグOFF)
                StateHasChanged();
            }
        }

        /// <summary>
        /// QRコード生成処理
        /// </summary>
        /// <param name="text"> QRコード内容 </param>
        /// <remarks> MitMap形式のQRコードを生成し、QR画像を保存する </remarks>
        private void GenerateQrCode(string text)
        {
            try
            {
                using var qrGenerator = new QRCodeGenerator();
                using var qrCodeData = qrGenerator.CreateQrCode(text, QRCodeGenerator.ECCLevel.Q);

                //using var qrCode = new PngByteQRCode(qrCodeData);
                using var qrCode = new BitmapByteQRCode(qrCodeData);

                // QRコード生成
                byte[] qrCodeBytes = qrCode.GetGraphic(20);

                // QRコード保存場所
                this.QrCodeImageSrc = $"data:image/png;base64,{Convert.ToBase64String(qrCodeBytes)}";
            }
            catch (Exception)
            {
                this.ErrorMessage = "QRコードの生成に失敗しました。";
            }
        }
    }
}
