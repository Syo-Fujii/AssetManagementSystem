package application.resources.mapper;

import java.util.List;

import application.java.base.dbTablesModel.StockTypeMasterModel;

public interface StockTypeMasterMapper {
    // 全件取得
    List<StockTypeMasterModel> selectAll();
    
    // IDで1件取得
    StockTypeMasterModel selectById(int id);
    
    // 新規登録
    void insertUser(StockTypeMasterModel user);
}
