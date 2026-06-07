package application.resources.xml;

import java.util.Properties;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceFactory extends UnpooledDataSourceFactory{

    private static String authServerUrl;
    private static int authCallbackPort = 8888; // デフォルト値	
	
	/* コンストラクタ */
	public HikariDataSourceFactory() {
        // MyBatisの標準DataSourceをHikariCPに差し替える
        this.dataSource = new HikariDataSource();
    }

    /**
     * MyBatisがXMLの <property> を読み込む際に最初に呼び出されるメソッド
     * 親クラスにデータを渡す前に、自作プロパティを安全に回収して削除する
     */
    @Override
    public void setProperties(Properties properties) {
        
    	// XMLから値を取得して静的変数に代入
    	if (properties.containsKey("authServerUrl")) {
            authServerUrl = properties.getProperty("authServerUrl");
            
            // 親クラス（MyBatis）が「知らないプロパティだ」と怒らないようにリストから消去する
            properties.remove("authServerUrl"); 
        }
        
        if (properties.containsKey("authCallbackPort")) {
            try {
                authCallbackPort = Integer.parseInt(properties.getProperty("authCallbackPort"));
            } catch (NumberFormatException e) {
                // パース失敗時はデフォルト値維持
            }
            properties.remove("authCallbackPort");
        }

        // 残った純粋なDB設定（jdbcUrlやusernameなど）だけを安全に親クラス（MyBatis/HikariCP）に渡す
        super.setProperties(properties);
    }	

    
    // 外部から取得するためのGetter
    public static String getAuthServerUrl() {
        return authServerUrl;
    }

    // 外部から取得するためのGetter
    public static int getAuthCallbackPort() {
        return authCallbackPort;
    }
}
