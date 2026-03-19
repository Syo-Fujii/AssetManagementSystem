package application.java.manager.CustomTableCells;

import java.util.Objects;

import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.TableColumnManager.keyValuePairItem;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class CustomComboBoxWithCheckBoxManager<S, K, V> extends CustomComboBoxKvpSourceManager<S, K, V> {
	private final String checkModelName; // 連動先Modelのプロパティ名

    /**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param checkModelName 値更新に連動させるModelのプロパティ名
     * @param kvpItems ComboBoxの選択リスト(KeyValuePairのリスト)
     * @param isAlwaysShow ComboBoxを常時表示するか
     */
	public CustomComboBoxWithCheckBoxManager(
    		String colId, 
    		String keyModelName, 
    		String checkModelName,
    		ObservableList<keyValuePairItem<Integer, String>> kvpItems, 
    		Boolean isAlwaysShow) {
        
        this.checkModelName = checkModelName;

        super(colId, keyModelName, kvpItems, isAlwaysShow);
    }

    /**
     * セルを描画・更新する際に内部で呼び出されるメソッド(ValueChanged Event)
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時<br>
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時<br>
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時<br>
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時<br>
     */
    @Override
    protected void updateItem(V value, boolean empty) {
    	// [CustomComboBoxKVP][updateItem]
    	super.updateItem(value, empty);

    	LogManager.writeTrace("[CustomComboBoxWithCheckBox][updateItem] Start");
    	
    	// データが存在する場合の表示処理
        if ( !empty && value != null && getIndex() >= 0 ) {
    		// 表示する前に、現在のデータモデルの値をセットする(初期値の設定)
            if (!Objects.equals(value.toString(), this.getComboEditorText()) && this.itemListAny() ) {
            	Platform.runLater(() -> {
            		if ( empty || value == null || getIndex() < 0 || !this.itemListAny() ) { return; }
            		this.syncModelPropertyBindingEvent(null, this.getSelectedListItem(value.toString()));
                });
            }	
        }
        LogManager.writeTrace("[CustomComboBoxWithCheckBox][updateItem] End");
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
