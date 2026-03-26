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
 */
public class CustomComboBoxControlManager<V> extends ComboBox<keyValuePairItem<V>> {

	ObservableList<keyValuePairItem<V>> comboBoxSource = FXCollections.observableArrayList(); 
	
	private Boolean isAddBlankRow = false;  

    /**
	 * @return isAddBlankRow
	 */
	public Boolean getIsAddBlankRow() {
		return isAddBlankRow;
	}

	/**
	 * @param isAddBlankRow セットする isAddBlankRow
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
	
	@SuppressWarnings("unchecked")
	public void setDataSource_KvpList(ObservableList<keyValuePairItem<V>> datas) {
		
		comboBoxSource.clear();
		comboBoxSource = datas;
		this.setItems(comboBoxSource); 

    	// データが存在する場合、先頭リストに空欄を生成する
    	if ( isAddBlankRow && !(comboBoxSource == null || comboBoxSource.isEmpty()) )
    	{
    		comboBoxSource.add(0, new keyValuePairItem<V>(AppConst.UNSET_NUMBER_VALUE, (V)""));
    	}
	}

    /**
     * データモデル kvpリスト変換処理
     * @brief Page表示の際、常にPageを初期化(new 生成)しているため、常に呼出される。<br>
     * メソッド参照: FXCollections::observableArrayList<br>
     * ⇒ ラムダ式: () -> FXCollections.<>observableArrayList()
     * ⇒ Linq(あれば): () => new FXCollections.observableArrayList<>()
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
     * 
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
