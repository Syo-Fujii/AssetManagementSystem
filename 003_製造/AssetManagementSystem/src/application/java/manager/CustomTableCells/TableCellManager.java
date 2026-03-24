package application.java.manager.CustomTableCells;

import application.java.base.BaseTableViewModel;
import application.java.manager.TableColumnManager.colKeyValuePairItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

public class TableCellManager<S extends BaseTableViewModel, T> extends TableCell<S, T>  {
	
	/**
	 * 対象とするMODELの指定したプロパティの値を取得する
	 * @param propertyName String 対象プロパティ名
	 * @param type プロパティの型
	 * @return 取得した値
	 * @throws Exception
	 */
	protected <R > R getRowClassProperty(String propertyName, Class<R> type) {
		// モデルへの値取得
		try {
	    	S rowData = this.getTableView().getItems().get(getIndex());
	    	
	    	return rowData.getModelProperty(propertyName, type);

	    } catch (Exception e) {
	    	System.err.println("モデルへの値取得に失敗しました: " + propertyName);
	    	throw new RuntimeException(e);
	    }
	}
	
	/**
	 * 対象とするMODEL(Row)の指定したプロパティに値を設定する
	 * @param propertyName String 対象プロパティ名
	 * @param type プロパティの型
	 * @param value 設定する値
	 * @throws Exception
	 */
	protected void setRowClassProperty(String propertyName, Class<?> type, Object value ) {
		// モデルへの値反映
		try {
	    	S rowData = this.getTableView().getItems().get(getIndex());
	    	
	    	rowData.setModelProperty(propertyName, type, value);

	    } catch (Exception e) {
	    	throw new RuntimeException(e);
	    }
	}
	
    /**
     * 
     * @param type
     * @param value
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object convertToType(Class<?> type, Object value) {
        if (value == null) { return null; }
        
        // すでに割り当て可能ならそのまま返す
        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }

        String str = value.toString();
        
        // 基本的なプリミティブ/ラッパー型への変換
        if (type == Integer.class || type == int.class) return Integer.valueOf(str);
        if (type == Long.class || type == long.class) return Long.valueOf(str);
        if (type == Double.class || type == double.class) return Double.valueOf(str);
        if (type == Boolean.class || type == boolean.class) return Boolean.valueOf(str);
        
        // Enum の場合
        if (type.isEnum()) { return Enum.valueOf((Class<Enum>) type, str); }

        return value;
    }

	/**
	 * ComboBoxにPair型の表示方法（StringConverter）を設定する
	 * @param cb 対象コンボボックスControl
	 */
	protected void setupPairConverter(ComboBox<colKeyValuePairItem<Integer, String>> cb) {
	    
		cb.setConverter(new StringConverter<colKeyValuePairItem<Integer, String>>() {

			/**
	         * kvpのクラスを引数に、設定されているValue(String型)を返す
	         */
			@Override
	        public String toString(colKeyValuePairItem<Integer, String> object) {
	            return (object == null) ? "" : object.value();
	        }

			/**
	         * 引数の文字列に一致する、kvpを返す
	         */
			@Override
	        public colKeyValuePairItem<Integer, String> fromString(String string) {
	            ObservableList<colKeyValuePairItem<Integer, String>> items = cb.getItems();
	            
	            // items が null または空の場合は処理を中断する
	            if (items == null || string == null || string.isEmpty()) {
	                return null;
	            }

	        	// 編集時に文字列から元のPairオブジェクトを探す処理
	            return cb.getItems().stream()
	                .filter(item -> item.value().equals(string))
	                .findFirst()
	                .orElseGet(() -> new colKeyValuePairItem<>(-1, string));  // 新規入力対応
	                //.orElse(null);
	        }
	    });
	}      
}
