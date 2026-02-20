module JavaFxSample {
	requires javafx.controls;
	requires javafx.fxml;
    requires java.sql;
    /** MySQL Connector/J の JAR ファイル内に module-info.classがない
     *  又は、Automatic-Module-Name の明示的な定義がない*/
    requires transitive mysql.connector.j; 
    requires org.mybatis;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    
    exports application; 
    
	opens application to org.mybatis, javafx.graphics, javafx.fxml;
	opens application.entity to org.mybatis;
	
}
