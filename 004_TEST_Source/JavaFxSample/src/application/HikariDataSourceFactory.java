package application;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;

import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceFactory  extends UnpooledDataSourceFactory{
    public HikariDataSourceFactory() {
        // MyBatisの標準DataSourceをHikariCPに差し替える
        this.dataSource = new HikariDataSource();
    }
}
