package application.java.manager.CustomTableCells;

import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.TableColumnManager.keyValuePairItem;
import javafx.collections.ObservableList;

public class CustomComboBoxWithChecBoxkManager<S, K, V> extends CustomComboBoxKvpSourceManager<S, K, V> {
	private final String checkModelName; // 連動先Modelのプロパティ名

    /**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param checkModelName 値更新に連動させるModelのプロパティ名
     * @param kvpItems ComboBoxの選択リスト(KeyValuePairのリスト)
     * @param isAlwaysShow ComboBoxを常時表示するか
     */
	public CustomComboBoxWithChecBoxkManager(
    		String colId, 
    		String keyModelName, 
    		String checkModelName,
    		ObservableList<keyValuePairItem<Integer, String>> kvpItems, 
    		Boolean isAlwaysShow) {
        
        this.checkModelName = checkModelName;

        super(colId, keyModelName, kvpItems, isAlwaysShow);
    }

    /**
     * 値の更新(確定)に連動する外部イベント設定(継承)
     * @param befValue selectedItemProperty().addListener oldVal
     * @param newValue selectedItemProperty().addListener newVal
	 * @brief 親クラスにて実行される Modelの連動処理を定義する<br> 
     */
    @Override
    protected void syncModelPropertyBindingEvent(
            keyValuePairItem<Integer, String> befValue, 
            keyValuePairItem<Integer, String> newValue) {
        
    	if (!AppUtil.StringIsNullOrWhiteSpace(checkModelName)) {
    		
			// 連動先値設定
    		Integer key = (newValue != null) ? newValue.key() : AppConst.UNSET_NUMBER_VALUE;
    		super.setRowClassProperty(
    				checkModelName, 
    				Boolean.class, 
    				(Boolean)!(key == AppConst.UNSET_NUMBER_VALUE));

        }
    }	
}
