using Microsoft.AspNetCore.Components;

namespace AssetManagementPassKeyLogIn.Components.Account.Pages
{
    /// <summary>
    /// ログイン画面(コードビハインド構成)
    /// </summary>
    public partial class Login
    {
        /* 各サービス呼出(インスタンス化したクラスの呼出) */
        // (web)Page遷移
        [Inject] private NavigationManager Navigation { get; set; } = default!;


        /// <summary>
        /// パスキー(QRコード)認証 成功イベント
        /// </summary>
        /// <param name="authenticatedStaffCode"> 社員コード </param>
        private void HandleLoginSuccess(int authenticatedStaffCode)
        {
            // 子コンポーネントから「社員番号」が送られてくるので、セッション等に記録してダッシュボードへ移動！
            Navigation.NavigateTo("/dashboard", forceLoad: true);
        }

    }
}
