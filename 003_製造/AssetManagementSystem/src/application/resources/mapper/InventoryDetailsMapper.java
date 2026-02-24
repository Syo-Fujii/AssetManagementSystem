package application.resources.mapper;

import java.util.List;

import application.java.base.tableViewListModel.InventoryDetailsDataModel;

public interface InventoryDetailsMapper {

	/**
	 * 備品詳細データ取得(テーブルより取得)
	 * @return 備品一覧データ
	 */
    List<InventoryDetailsDataModel> getTableDetailRecords(Integer type, String code );
    

	/**
	 * 備品データの最終所在確認日更新
	 * @return 更新件数
	 */
    Integer updConfirmedDate(InventoryDetailsDataModel row);
    
	/**
	 * 備品詳細データ取得(Viewより取得)
	 * @return 備品一覧データ
	 */
    List<InventoryDetailsDataModel> getViewAllRecords(Integer type, String code );

	/**
	 * 遷移先画面サイズ種別 取得(汎用マスタより取得)
	 * @return 画面サイズ種別
	 */
    String getWindowSize(String type, String code );    
}
