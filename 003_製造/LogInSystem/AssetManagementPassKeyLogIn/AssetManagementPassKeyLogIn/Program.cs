using AssetManagementPassKeyLogIn.Components;
using AssetManagementPassKeyLogIn.Components.Account;
using AssetManagementPassKeyLogIn.Data;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;

/* アプリエントリーポイント (個別での認証を有効にする) */
var builder = WebApplication.CreateBuilder(args);

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
// -- MS SQL --
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") ?? throw new InvalidOperationException("Connection string 'DefaultConnection' not found.");
builder.Services.AddDbContext<ApplicationDbContext>( options => options.UseSqlServer(connectionString) );
builder.Services.AddDatabaseDeveloperPageExceptionFilter();

// 認証ルール(内容)定義
builder.Services.AddIdentityCore<ApplicationUser>(options =>
    {
        options.SignIn.RequireConfirmedAccount = true;                   // メール認証（アカウント確認）が完了したユーザーだけがログインできる
        options.Stores.SchemaVersion = IdentitySchemaVersions.Version3;  // NET 8 以前のデータ構造
    })
    .AddEntityFrameworkStores<ApplicationDbContext>()                    // 各認証用の情報はDB経由で参照・格納
    .AddSignInManager()                                                  // SignInManagerの有効化
    .AddDefaultTokenProviders();                                         // 「パスワードリセット」や「二要素認証（2FA）」などの機能で使われる「使い捨ての確認コード（トークン）」を発行・管理する機能を有効にする

builder.Services.AddSingleton<IEmailSender<ApplicationUser>, IdentityNoOpEmailSender>(); // メール送信を行わない ⇒ ダミー設定

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

// 「アンチフォージェリ（偽造防止）」機能有効
app.UseAntiforgery();

// 「静的ファイル」の最適化
app.MapStaticAssets();

// 「Blazor」画面(razor)設定 ⇒ App.razorを起点に展開
app.MapRazorComponents<App>().AddInteractiveServerRenderMode();

// 認証関連の標準機能を使用 : Add additional endpoints required by the Identity /Account Razor components.
app.MapAdditionalIdentityEndpoints();

app.Run();
