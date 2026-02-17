package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.InventoryListDataModel;

public interface IMySqlMapper {
    // 全件取得
    List<InventoryListDataModel> selectAll();
    
    // IDで1件取得
    InventoryListDataModel selectById(int id);
    
    // 新規登録
    void insertUser(InventoryListDataModel user);
}
