package application.java.base.tableViewListModel;

import application.java.base.dbTablesModel.StockMasterModel;

public class StockMasterDataModel  extends StockMasterModel {

	public StockMasterDataModel() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	
	
	
	
	
	public StockMasterModel extractMaster() {
	    // 引数が子クラス(明細Model)であっても、
	    // コンストラクタ側で親クラスの項目のみをコピーするため、
	    // 結果としてマスタ専用のインスタンスが返ります。
	    return new StockMasterModel(this);
	}
}
