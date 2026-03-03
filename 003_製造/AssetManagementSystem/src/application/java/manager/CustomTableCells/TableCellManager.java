package application.java.manager.CustomTableCells;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import application.java.manager.TableColumnManager.keyValuePairItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.util.StringConverter;

public class TableCellManager<S, T> extends TableCell<S, T>  {
	
	/**
	 * 
	 * @param propertyName
	 * @param type
	 * @param value
	 * @throws Exception
	 */
	protected void setRowClassProperty(String propertyName, Class<?> type, Object value ) {
		// モデルへの値反映
		try {
	    	S rowData = this.getTableView().getItems().get(getIndex());
	    	
	    	// メソッド(値のSetter プロパティ)名の生成
	        String methodName = "set" +
	    	                    propertyName.substring(0, 1).toUpperCase() +
	    	                    propertyName.substring(1);
	        
            // モデルからメソッドを探して実行
            Method setter = rowData.getClass().getMethod(methodName, type);
            setter.invoke(rowData, type.cast(value));

	    } catch (InvocationTargetException itex) {
	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
            System.err.println("モデルへの値反映に失敗しました: " + propertyName);
            // リフレクション先の例外を処理
            Throwable cause = itex.getCause();
            System.err.println("メソッド内部でエラーが発生しました: " + cause.getMessage());
            cause.printStackTrace(); 
            throw new RuntimeException(cause);

	    } catch (NoSuchMethodException e) {
	    	System.err.println("モデルへの値反映に失敗しました: " + propertyName);
	    	System.err.println("メソッドが見つかりません: " + propertyName);
	        throw new RuntimeException(e);

	    } catch (Exception e) {
	    	System.err.println("モデルへの値反映に失敗しました: " + propertyName);
	    	throw new RuntimeException(e);
	    }}
	
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
	protected void setupPairConverter(ComboBox<keyValuePairItem<Integer, String>> cb) {
	    
		cb.setConverter(new StringConverter<keyValuePairItem<Integer, String>>() {

			/**
	         * kvpのクラスを引数に、設定されているValue(String型)を返す
	         */
			@Override
	        public String toString(keyValuePairItem<Integer, String> object) {
	            return (object == null) ? "" : object.value();
	        }

			/**
	         * 引数の文字列に一致する、kvpを返す
	         */
			@Override
	        public keyValuePairItem<Integer, String> fromString(String string) {
	            ObservableList<keyValuePairItem<Integer, String>> items = cb.getItems();
	            
	            // items が null または空の場合は処理を中断する
	            if (items == null || string == null || string.isEmpty()) {
	                return null;
	            }

	        	// 編集時に文字列から元のPairオブジェクトを探す処理
	            return cb.getItems().stream()
	                .filter(item -> item.value().equals(string))
	                .findFirst()
	                .orElseGet(() -> new keyValuePairItem<>(-1, string));  // 新規入力対応
	                //.orElse(null);
	        }
	    });
	}      
}
