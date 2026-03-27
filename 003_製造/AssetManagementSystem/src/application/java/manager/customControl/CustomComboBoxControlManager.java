package application.java.manager.customControl;

import java.util.List;

import application.java.base.BaseFormPage.keyValuePairItem;
import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/**
 * カスタムControl：ComboBox
 * @brief keyValuePairItem[V]をデータSourceとするカスタムComboBox<br>
 */
public class CustomComboBoxControlManager<V> extends ComboBox<keyValuePairItem<V>> {

	ObservableList<keyValuePairItem<V>> comboBoxSource = FXCollections.observableArrayList(); 
	
	private Boolean isAddBlankRow = false;  

    /**
     * 先頭ブランク追加判定 取得
	 * @return isAddBlankRow 判定結果
	 * @brief 選択肢の先頭にブランクを挿入するかの判定<br>
	 * [真]の場合、データ設定の際に先頭に[空欄]:ブランクを設定する 
	 */
	public Boolean getIsAddBlankRow() {
		return isAddBlankRow;
	}

	/**
	 * 先頭ブランク追加判定 設定 
	 * @param isAddBlankRow 判定結果
	 * @brief 選択肢の先頭にブランクを挿入するかの判定<br>
	 * [真]の場合、データ設定の際に先頭に[空欄]:ブランクを設定する 
	 */
	public void setIsAddBlankRow(Boolean isAddBlankRow) {
		this.isAddBlankRow = isAddBlankRow;
	}

	/**
     * コンボボックス(Kvp)の選択Key取得処理
     */   
	public Integer getSelectedKey() 
	{
		return this.getValue() != null ? this.getValue().key() : AppConst.UNSET_NUMBER_VALUE;
	}		

	/**	
    * コンボボックス(Kvp)の選択Value取得処理
    */   
	public String getSelectedValue() 
	{
		V value = this.getValue() != null ? this.getValue().value() : null;
		return (value != null) ? value.toString() : "";
	}	
	
	/**
	 * データ設定 (データSourceの設定:KeyValuePairのリスト)
	 * @param datas ObservableList[keyValuePairItem[V]] Kvpのリスト(valueの型は指定なし)
	 */
	@SuppressWarnings("unchecked")
	public void setDataSource_KvpList(ObservableList<keyValuePairItem<V>> datas) {
		
		comboBoxSource.clear();
		comboBoxSource.setAll(datas); 

    	// データが存在する場合、先頭リストに空欄を生成する
    	if ( isAddBlankRow && !(comboBoxSource == null || comboBoxSource.isEmpty()) )
    	{
    		comboBoxSource.add(0, new keyValuePairItem<V>(AppConst.UNSET_NUMBER_VALUE, (V)""));
    	}
    	
    	this.setItems(comboBoxSource);
	}

    /**
     * データ設定 (データSourceの設定:DB MODELのリスト)	
     * @param <T> (Table Source / Subject): 行データのクラス(BaseTableViewModelを継承したクラス)
     * @param datas List[T] Modelのリスト
     * @param keyPropertyName String [key]項目にするMODELのプロパティ名
     * @param valuePropertyName String [value]項目にするMODELのプロパティ名
     * @param valueType [value]項目にするプロパティの型
     */
	public <T extends BaseTableViewModel> void setDataSource_ModelList(
			List<T> datas, 
			String keyPropertyName, 
			String valuePropertyName,
			Class<V> valueType)
	{
		comboBoxSource.clear();
    	
        if (datas == null || datas.isEmpty()) { return; }

        datas.
        stream().
        map(( model ) -> new keyValuePairItem<V>(
        		model.getModelProperty(keyPropertyName, Integer.class),
        		model.getModelProperty(valuePropertyName, valueType))).
        forEach(comboBoxSource::add);

    	// データが存在する場合、先頭リストに空欄を生成する
    	if ( isAddBlankRow && !(comboBoxSource == null || comboBoxSource.isEmpty()))
    	{
    		V value = (valueType == String.class) ? valueType.cast("") : null;
    		comboBoxSource.add(0, new keyValuePairItem<V>(AppConst.UNSET_NUMBER_VALUE, value));
    	}
	}

	
    /**
     * FXML用デフォルトコンストラクタ
     */
    public CustomComboBoxControlManager() {
        super();
        
        // コンストラクタで初期化メソッドを呼ぶ
		this.comboBoxKvpDisplayMember();
		this.setItems(comboBoxSource);
    }    	
	
    /**
     * 選択肢リスト先頭項目指定 処理
	 * @brief 選択リストの先頭を選択状態にする<br>
     */
    public void setupListSelectedFirst() {
    	if ( isAddBlankRow && !(comboBoxSource == null || comboBoxSource.isEmpty()))
    	{
        	this.getSelectionModel().selectFirst();
    	}  	
    }

    /**
     * コンボボックス(Kvp)の選択リスト表示処理
     * @brief KVP型のコンボボックスの場合、[Value]を選択肢としてリストを生成する。
     */   
	private void comboBoxKvpDisplayMember() 
	{
		this.setConverter(new StringConverter<keyValuePairItem<V>>() {
			/* Kvpより、Valueを返す */
			@Override
    	    public String toString(keyValuePairItem<V> item) 
			{
				 // valueを表示
				return (item == null || item.value() == null) ? "" : item.value().toString();
    	    }

			/* 選択リストより、Kvpを返す */
    	    @Override
    	    public keyValuePairItem<V> fromString(String string) 
    	    {
    	        // 編集不可（ReadOnly）コンボの場合は null でOK
    	        return null; 
    	    }
    	});
	}		
}
