/** モジュール定義ファイル
/* @brief モジュールの名前、依存関係(参照設定)、および外部へ公開するパッケージ（API）を定義
 * ・依存関係の定義 (requires): 自モジュールが利用する外部モジュールを指定。
 * ・公開範囲の指定 (exports): 他のモジュールに公開するパッケージを指定。
 * ・リフレクションの許可 (opens): リフレクション機能でのアクセスを許可。
 */
module AssetManagementSystem {
	requires java.desktop;
	
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;	

    requires java.sql;
    /** MySQL Connector/J の JAR ファイル内に module-info.classがない
     *  又は、Automatic-Module-Name の明示的な定義がない*/
    requires transitive mysql.connector.j; 
    requires org.mybatis;
    requires com.zaxxer.hikari;

    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;
	requires javafx.web;
	requires jdk.httpserver;

    
    exports application;
    
    
	opens application to javafx.graphics, javafx.fxml, org.mybatis, ch.qos.logback.core;
	opens application.java.base.dbTablesModel to javafx.base, org.mybatis;
	opens application.java.base.tableViewListModel to javafx.base, org.mybatis;
	opens application.java.manager to javafx.base, javafx.fxml, org.mybatis.logback.core;
	opens application.java.manager.customControl to javafx.fxml;
	opens application.java.window to javafx.fxml;
	opens application.java.window.inventoryLoans.inventoryList to javafx.fxml;
	opens application.java.window.inventoryLoans.inventoryDetails to javafx.fxml;
	opens application.java.window.inventoryLoans.inventoryLoan to javafx.fxml;
	opens application.java.window.inventoryLoans.inventoryReturn to javafx.fxml;
	opens application.java.window.verifyLocation.performInventory to javafx.fxml;
	opens application.java.window.masterMaintenance.stockTypeMaster to javafx.fxml;
	opens application.java.window.masterMaintenance.stockMaster.details to javafx.fxml;
	opens application.java.window.masterMaintenance.stockMaster.maintenance to javafx.fxml;
	opens application.java.window.masterMaintenance.staffMaster to javafx.fxml;
	opens application.resources.xml to org.mybatis;
	opens application.resources.mapper to org.mybatis;
}