using AssetManagementPassKeyLogIn.Components;
using AssetManagementPassKeyLogIn.Components.Account;
using AssetManagementPassKeyLogIn.Data;
using AssetManagementPassKeyLogIn.Services;
using Fido2NetLib;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;


// exeが置いてある実際のフォルダパスをカレントディレクトリに強制設定する
//Directory.SetCurrentDirectory(AppContext.BaseDirectory);

/* アプリエントリーポイント (個別での認証を有効にする) */
var builder = WebApplication.CreateBuilder(args);

// builder.WebHost.UseUrls("https://localhost:7215", "http://localhost:5058");

// Razorコンポーネントの追加(Blazorサービス)
builder.Services.AddRazorComponents().AddInteractiveServerComponents();

// 認証機能の追加 - スコープ(範囲)の設定:全ての画面で参照する
builder.Services.AddCascadingAuthenticationState();
builder.Services.AddScoped<IdentityRedirectManager>();
builder.Services.AddScoped<AuthenticationStateProvider, IdentityRevalidatingAuthenticationStateProvider>();


builder.Services.AddAuthentication(options =>
    {
        options.DefaultScheme = IdentityConstants.ApplicationScheme;
        options.DefaultSignInScheme = IdentityConstants.ExternalScheme;
    })
    .AddIdentityCookies();

// DB設定
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' not found.");
// -- MS SQL --
// builder.Services.AddDbContext<ApplicationDbContext>( options => options.UseSqlServer(connectionString) );

// -- MySQL --
var serverVersion = ServerVersion.AutoDetect(connectionString);
builder.Services.AddDbContext<ApplicationDbContext>(
    options => options.UseMySql(connectionString, serverVersion));

builder.Services.AddDatabaseDeveloperPageExceptionFilter();
// DB操作クラス読込
builder.Services.AddScoped<MySqlService>();

// 認証ルール(内容)定義
builder.Services.AddIdentityCore<ApplicationUser>(options =>
    {
        options.SignIn.RequireConfirmedAccount = false;
        /*
        options.SignIn.RequireConfirmedAccount = true;                   // MS-SQL:メール認証（アカウント確認）が完了したユーザーだけがログインできる
        options.Stores.SchemaVersion = IdentitySchemaVersions.Version3;  // MS-SQL:NET 8 以前のデータ構造
        */
    })
    .AddEntityFrameworkStores<ApplicationDbContext>()                    // 各認証用の情報はDB経由で参照・格納
    .AddSignInManager()                                                  // SignInManagerの有効化
    .AddDefaultTokenProviders();                                         // 「パスワードリセット」や「二要素認証（2FA）」などの機能で使われる「使い捨ての確認コード（トークン）」を発行・管理する機能を有効にする

// メール送信を行わない ⇒ ダミー設定
builder.Services.AddSingleton<IEmailSender<ApplicationUser>, IdentityNoOpEmailSender>();

// パスキー (FIDO2) 設定
// builder.Services.AddFido2(builder.Configuration.GetSection("fido2"));
builder.Services.AddFido2((Fido2Configuration options) =>
{
    // サーバーのドメイン（開発時は localhost）
    options.ServerDomain = "localhost";
    options.ServerName = "備品管理システム";
    // ブラウザがアクセスを許可するオリジン（BlazorのURL）
    options.Origins = new HashSet<string> 
    {
        "https://localhost:7215",
        "https://localhost:7193", 
        "http://localhost:5249" 
    };
    
    options.TimestampDriftTolerance = 300000; // 5分間の許容誤差
});

builder.Services.AddScoped<PasskeyService>();

// セッション（チャレンジの一時保存用）を有効にする
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(2); // チャレンジの有効期限(2分)
    options.Cookie.HttpOnly = true;
    options.Cookie.SameSite = SameSiteMode.Strict;
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    // エラー処理(開発時)
    app.UseMigrationsEndPoint();
}
else
{
    // エラー処理(運用時) ⇒ スタックトレースを隠蔽する 
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

// 404 Error(Page NotFound)カスタム定義
app.UseStatusCodePagesWithReExecute("/not-found", createScopeForStatusCodePages: true);
app.UseHttpsRedirection();

// セッションミドルウェアを有効化
app.UseSession();

// 「アンチフォージェリ（偽造防止）」機能有効
app.UseAntiforgery();

// 「静的ファイル」の最適化
app.MapStaticAssets();

// 「Blazor」画面(razor)設定 ⇒ App.razorを起点に展開
app.MapRazorComponents<App>().AddInteractiveServerRenderMode();

// 認証関連の標準機能を使用 : Add additional endpoints required by the Identity /Account Razor components.
// ログアウト用の軽量API（Minimal API）
app.MapPost("/Account/Logout", 
    async (
        SignInManager<ApplicationUser> signInManager,
        [FromForm] string returnUrl) =>
        {
            await signInManager.SignOutAsync();
            
            return Results.LocalRedirect($"~/{returnUrl ?? ""}");
        });

app.Run();


/// <summary>
/// メールクラス(ダミー)
/// </summary>
/// <remarks> 内容が未定義のメソッド = なにもしない </remarks>
public class IdentityNoOpEmailSender : IEmailSender<ApplicationUser>
{
    public Task SendConfirmationLinkAsync(ApplicationUser user, string email, string confirmationLink) => Task.CompletedTask;
    public Task SendPasswordResetLinkAsync(ApplicationUser user, string email, string resetLink) => Task.CompletedTask;
    public Task SendPasswordResetCodeAsync(ApplicationUser user, string email, string resetCode) => Task.CompletedTask;
}