package application.java.manager;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import application.resources.xml.HikariDataSourceFactory;

/*
 * 認証サーバ(結果受信サーバー)操作クラス
 */
public class WebServiceManager {

	/* 認証結果待ち受け受信Server(localhost:指定ポート番号)*/
	private HttpServer receiveLocalServer = null;
	// 認証サーバーアドレス
	private String authUrl;
	// 認証成功時のサーバーからのデータ
	private String authenticatedToken = null;
	
	// 認証成功時のイベント
    private Runnable onSuccessCallback;

    // タイムアウトやサーバー停止時のコールバック
    private Runnable onFailureCallback; 
    
    
    /**
     * 認証サーバーアドレス取得
     * @brief QRコード/パスキー認証サーバーのアドレス
     */
    public String getAuthServerUrl() {
        return this.authUrl;
    }	    

    /**
     * 認証結果Token取得
     * @brief 認証成功時の(認証)サーバーからの送信データ(Token)
     */
    public String getAuthenticatedToken() {
        return this.authenticatedToken;
    }	
 
    
    /**
     * コンストラクタ
     * @brief コンストラクタで認証成功時のイベントを設定する
     */
    public WebServiceManager(Runnable onSuccessCallback, Runnable onFailureCallback) {
        this.onSuccessCallback = onSuccessCallback;
        this.onFailureCallback = onFailureCallback;
    }
	
    /**
     * 認証結果受信開始処理
     * @brief PassKey認証サーバーからの結果(受信)待ち処理開始
     */
    public void startLocalServer() throws Exception {
		try {
	    	LogManager.writeInfo("[Webサーバー操作クラス] ： 受信サーバー設定(LocalHost)");
			
			// 認証サーバーURLを取得
    		String baseAuthUrl = HikariDataSourceFactory.getAuthServerUrl();
    		int callbackPort = HikariDataSourceFactory.getAuthCallbackPort();

    		// 認証完了後のリダイレクト先(callback)を指定してブラウザを開く
    		this.authUrl = String.format("%s?redirect_uri=http://localhost:%d/callback", baseAuthUrl, callbackPort);
    	
            if (baseAuthUrl == null || baseAuthUrl.isEmpty()) {
                throw new IllegalStateException("mybatis-config.xml 内に authServerUrl が定義されていません。");
            }
            
            this.receiveLocalServer = HttpServer.create(new InetSocketAddress(callbackPort), 0);
            this.receiveLocalServer.createContext(
            		"/callback", 
            		exchange -> 
            		{
            			SetReceiveResponse(exchange);
            			
            			this.stopLocalServer();

                        new Thread(() -> {
                            try {
                                Thread.sleep(1500); // 1.5秒待機（環境に合わせて1000〜2000で調整してください）
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            
                            // 1.5秒後、安全にJavaFXのUIをメニュー画面へ切り替える
                            javafx.application.Platform.runLater(() -> {
                            	if (onSuccessCallback != null) { onSuccessCallback.run(); }
                            });
                        }).start();            			
            		});	
			
            this.receiveLocalServer.start();
		
		} catch(Exception e) {
			LogManager.writeError("[Webサーバー操作クラス] ： 受信サーバーの開始に失敗", e);
	        
	        throw new ExceptionInInitializerError(e);
		}
    }

    /**
     * 認証結果受信終了処理
     * @brief PassKey認証サーバーからの結果(受信)待ち処理終了
     */
    public void stopLocalServer() {
        if (this.receiveLocalServer != null) {
            LogManager.writeTrace("[Webサーバー操作クラス] ： 受信サーバー停止");

            this.receiveLocalServer.stop(0);
            this.receiveLocalServer = null;

            // 1.5秒後、安全にJavaFXのUIをメニュー画面へ切り替える
            javafx.application.Platform.runLater(() -> {
            	if (onFailureCallback != null) { onFailureCallback.run(); }
            });
        }
    }

    /**
     * 認証結果受信レスポンス設定処理
     * @brief 現在はデータを何も返さない(204 No Content)
     */
    private void SetReceiveResponse(HttpExchange ex) 
    {
        try {
        	LogManager.writeInfo("[Webサーバー操作クラス] ： 認証サーバーからデータを受信しました");

        	this.authenticatedToken = ex.getRequestURI().getQuery();

            // 「返すデータなし(204 No Content)」のステータスだけを送信
            // 第2引数を「-1」にすることで、レスポンスボディが存在しないことを明示
            ex.sendResponseHeaders(204, -1);

            // ブラウザ側に返す完了画面のHTML
            /* String response = "<html><head><meta charset='UTF-8'></head><body>"
                            + "<h2>認証が完了しました。</h2>"
                            + "<p>このタブを閉じて、備品管理システムアプリに戻ってください。</p>"
                            + "</body></html>";
                            
            httpEx.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            httpEx.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            
            try (OutputStream os = httpEx.getResponseBody()) {
                os.write(response.getBytes("UTF-8"));
            }*/ 
        } catch (IOException e) {
            LogManager.writeError("[Webサーバー操作クラス] : レスポンス送信中にエラーが発生", e);
        } finally {
            if (ex != null) {
                ex.close(); 
            }
        }
    }
}
