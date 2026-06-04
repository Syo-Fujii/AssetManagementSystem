using Microsoft.AspNetCore.Components;

namespace AssetManagementPassKeyLogIn.Components.Account.Pages
{
    /// <summary>
    /// ログイン画面(コードビハインド構成)
    /// </summary>
    public partial class Login
    {
        /* 各サービス呼出(インスタンス化したクラスの呼出) */
        // (web)Page遷移機能[Blazor標準機能:Microsoft.AspNetCore.Components]
        [Inject] private NavigationManager Navigation { get; set; } = default!;


        /// <summary>
        /// パスキー(QRコード)認証 成功イベント
        /// </summary>
        /// <param name="authenticatedStaffCode"> 社員コード </param>
        private void HandleLoginSuccess(int authenticatedStaffCode)
        {
            // ページ全体を強制リロード（再読み込み）して[dashboard]へ遷移
            Navigation.NavigateTo("/dashboard", forceLoad: true);
        }

    }
}
