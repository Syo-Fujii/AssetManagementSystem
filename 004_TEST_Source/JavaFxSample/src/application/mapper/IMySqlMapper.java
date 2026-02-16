package application.mapper;

import java.util.List;

import application.entity.StockTypeMaster;

public interface IMySqlMapper {
    // 全件取得
    List<StockTypeMaster> selectAll();
    
    // IDで1件取得
    StockTypeMaster selectById(int id);
    
    // 新規登録
    void insertUser(StockTypeMaster user);
}
