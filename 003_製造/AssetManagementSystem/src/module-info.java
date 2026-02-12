/** モジュール定義ファイル
/* @brief モジュールの名前、依存関係(参照設定)、および外部へ公開するパッケージ（API）を定義
 * ・依存関係の定義 (requires): 自モジュールが利用する外部モジュールを指定。
 * ・公開範囲の指定 (exports): 他のモジュールに公開するパッケージを指定。
 * ・リフレクションの許可 (opens): リフレクション機能でのアクセスを許可。
 */
module AssetManagementSystem {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	requires javafx.base;
	
	opens application to javafx.graphics, javafx.fxml;
	opens application.window.inventoryList to javafx.fxml;
	opens application.window.testNextWindow to javafx.fxml;
	opens application.java.base.tableViewListModel to javafx.base;
	opens application.java.manager to javafx.base, javafx.fxml;
}