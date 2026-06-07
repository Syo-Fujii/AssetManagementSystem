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
        /// 認証待ち時間(初期値：無制限)
        /// </summary>
        public int? TimeOutSecondsInterval { get; set; } = null;

        /// <summary>
        /// 画面描画時に実行するか
        /// </summary>
        public bool IsAfterRenderExecute { get; set; } = false;

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
        /// 画面描画処理(継承:画面が描画された後で呼び出されるメソッド)
        /// </summary>
        /// <param name="firstRender"> 画面が初めて開いた瞬間判定 </param>
        /// <remarks> 
        /// HTML画面の組み立てが完全に終わった後（＝OnAfterRenderAsync）にwebauthn.jsを呼び出す<br />
        /// [ComponentBase.OnAfterRenderAsync]継承
        /// </remarks>
        protected override async Task OnAfterRenderAsync(bool firstRender)
        {
            if (this.IsAfterRenderExecute && firstRender)
            {
                await InitializeQrCodeAndAuthWaiting("getAssertionConditional");
            }
        }

        /// <summary>
        /// QR生成 + 認証待ち処理(ボタン等で呼び出す場合に用いる)
        /// </summary>
        /// <remarks>
        /// webauthn.jsを呼び出す
        /// </remarks>
        public async Task StartPasskeyAuth()
        {
            await InitializeQrCodeAndAuthWaiting("getAssertion");
        }


        /// <summary>
        /// QR生成 + 認証待ち処理
        /// </summary>
        /// <param name="callBackJsMethod"> Js(webauthn.js)呼出メソッド(文字列) </param>
        private async Task InitializeQrCodeAndAuthWaiting(string callBackJsMethod)
        {
            this.ErrorMessage = string.Empty;
            this.IsProcessing = true; //処理中(フラグON)

            StateHasChanged(); // partial クラス内なのでそのまま呼び出せます

            try
            {
                /* タイムアウト待ち時間設定 */
                var timeSpan = Timeout.InfiniteTimeSpan;
                if (this.TimeOutSecondsInterval is not null) { timeSpan = TimeSpan.FromSeconds((int)this.TimeOutSecondsInterval);  } 

                // タイムアウトを有効にするためのCancellationTokenソースを生成
                using var cts = new CancellationTokenSource(timeSpan);

                var assertionOptions = PasskeyOp.GetAssertionOptionsForQrCode();
                if (assertionOptions is null)
                {
                    ErrorMessage = "認証の初期化に失敗しました。";
                    return;
                }

                /* 
                 * QRコードを表示してこの画面で「スマホのBluetooth接続」を待機
                 * QRコードの中身は何でもよい
                 */
                string targetUrl = $"{Navigation.BaseUri}account/login?auth_state=active";
                GenerateQrCode(targetUrl);

                var module = await JS.InvokeAsync<IJSObjectReference>("import", "./js/webauthn.js");
                var credentialJson = await module.InvokeAsync<JsonElement>(
                    callBackJsMethod,
                    cts.Token,
                    assertionOptions);

                // moduleの結果[credentialJson]を待つ
                this.TargetStaffCode = await PasskeyOp.VerifyAssertionAsync(credentialJson, assertionOptions);

                if (this.TargetStaffCode.Equals(-1))
                {
                    ErrorMessage = "認証エラー：パスキーの検証に失敗しました。";
                    return;
                }

                // ✨【修正】JavaFX側がポート8888（localhost）で待ち受けている受信用サーバーへデータを送信
                try
                {
                    // JavaFXが指定した callback ポート（デフォルト 8888）
                    int callbackPort = 8888;

                    // JavaFX側の受信用ローカルサーバーのURLを構築
                    // パラメータ名（"staffCode"など）は、既存のJavaFX側の受信解析ロジック（WebServiceManager等）の仕様に合わせて調整してください
                    string callbackUrl = $"http://localhost:{callbackPort}/callback?staffCode={this.TargetStaffCode}";

                    // HttpClientを使ってJavaFX側へ結果を通知（Fire and Forget / もしくは非同期待機）
                    using (var client = new HttpClient())
                    {
                        // タイムアウトを短めに設定（ローカル通信のため5秒もあれば十分です）
                        client.Timeout = TimeSpan.FromSeconds(5);

                        // JavaFXのローカルサーバーを叩く（GETリクエスト）
                        var response = await client.GetAsync(callbackUrl);

                        if (response.IsSuccessStatusCode)
                        {
                            Console.WriteLine($"[Blazor] JavaFX(localhost:{callbackPort})への認証成功通知に成功しました。");
                        }
                    }
                }
                catch (Exception ex)
                {
                    // 開発中の通常ブラウザ単体テスト時などでJavaFXが起動していない場合のフォールバック
                    ErrorMessage = $"[Blazor] JavaFX受信用サーバーへの通信中にエラーが発生しました:exMessage: {ex.Message}";
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
            catch (OperationCanceledException)
            {
                var timeOutSsconds = this.TimeOutSecondsInterval.HasValue ? $"({this.TimeOutSecondsInterval}秒)" : string.Empty;
                this.ErrorMessage = $"認証の有効期限{timeOutSsconds}が切れました。ページを再読み込みしてください。";
            }
            catch (Exception)
            {
                this.ErrorMessage = $"認証がキャンセルされたか、エラーが発生しました。";
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
