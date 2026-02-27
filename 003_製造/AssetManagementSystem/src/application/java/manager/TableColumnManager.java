package application.java.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import application.java.base.BaseTableViewModel;
import application.java.manager.CustomTableCells.CustomComboBoxTableCellManager;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

/**
 * 
 * @param <S>
 * @param <T>
 */
public class TableColumnManager<S extends BaseTableViewModel,T> extends TableColumn<S, T>  {

	private Boolean isEnterNextFocus = true;
	
	public record Pair<K, V>(Integer code, String name) {};
	
	/**
	 * コンストラクタ
	 */
	public TableColumnManager() {
        super();
        
        setupDefaultEditCommitHandler();
        // System.out.println("TableColumnManager New");
	}
	
	/**
	 * カラム列移動(順番)可否設定
	 * @param col
	 * @param isReorderabled
	 */
	public void setReorderable(Boolean isReorderabled) 
	{
		// 特定のカラムの移動の可否
		this.setReorderable(isReorderabled);
	}
	
	/**
	 * CELL 有効化制御
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param <C> 対象カラムの型
	 * @param column 対象カラム
	 * @param isEnabled　有効化判定
	 * setCellValueFactory( new PropertyValueFactory<>())と競合しない
	 */
	@SuppressWarnings({ "unused" })
	public void IsEnabled(Boolean isEnabled) {
		
		this.setCellFactory(col -> new TableCell<S, T>() {
    	    @Override
    	    protected void updateItem(T item, boolean empty) {
    	        super.updateItem(item, empty);
    	        if (empty || item == null) {
    	            setText(null);
    	            setDisable(false);
    	        } else {
    	            setText(item.toString());
    	            // 選択・操作を制御
    	            setDisable(!isEnabled);
    	            setFocusTraversable(!isEnabled);
    	        }
    	    }});}	
	
	/**
	 * ComboBox型セルの設定
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 */
	public void setCellTypeCustomComboBox(ObservableList<T> items, Boolean isEnabled){
		
    	this.setCellFactory(
    			col -> new CustomComboBoxTableCellManager<S,T>(this.getId(), items){

    			      @Override
    			        public void updateItem(T item, boolean empty) {
    			            super.updateItem(item, empty);
    			            
    			            if (empty || item == null) {
    			                setDisable(false);
    			            } else {
    			                // ここで有効・無効を制御
    			                setDisable(!isEnabled);
    			                // 非活性時はクリックできないようにする
    			                setFocusTraversable(isEnabled);
    			                
    			                // 見た目の調整（非活性時にグレーアウトさせる等）
    			                if (!isEnabled) {
    			                    setStyle("-fx-opacity: 0.5; -fx-background-color: #f4f4f4;");
    			                } else {
    			                    setStyle(""); 
    			                }
    			            }
    				} });}

	/**
	 * ComboBox型セルの設定
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 */
	public void setCellTypeCustomComboBoxTest(ObservableList<Pair<Integer, String>> items, Boolean isEnabled){
		
    	this.setCellFactory(
    			col -> new CustomComboBoxTableCellManager<S,T>(this.getId(), null){

    		        // 内部で使う ComboBox を Pair 型で上書き・保持する
    		        private final ComboBox<Pair<Integer, String>> internalCb = new ComboBox<>(items);

    		        {
    		            // 表示設定 (IDではなく名称を出す)
    		            setupPairConverter(internalCb);

    		            // 選択時のイベント：Pair から String (name) を取り出して確定
    		            internalCb.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
    		                if (newVal != null && isEditing()) {
    		                    // String 型として commit する (T は String)
    		                    commitEdit((T) newVal.name()); 
    		                }
    		            });
    		        }   
     			    
    		        @Override
    			        public void updateItem(T item, boolean empty) {
    			            super.updateItem(item, empty);
    			            
    			            if (empty || item == null) {
    			            	setGraphic(null);
    			            	setDisable(false);
    			            } else {
    			                // モデルの String 値と一致する Pair を探してセット
    			                Pair<Integer, String> selected = items.stream()
    			                    .filter(p -> p.name().equals(item))
    			                    .findFirst().orElse(null);
    			                
    			                internalCb.setValue(selected);
    			                setGraphic(internalCb); 			                
      			            	
    			            	// ここで有効・無効を制御
    			                /*setDisable(!isEnabled);
    			                // 非活性時はクリックできないようにする
    			                setFocusTraversable(isEnabled);
    			                
    			                // 見た目の調整（非活性時にグレーアウトさせる等）
    			                if (!isEnabled) {
    			                    setStyle("-fx-opacity: 0.5; -fx-background-color: #f4f4f4;");
    			                } else {
    			                    setStyle(""); 
    			                }*/
    			            }
    				} });}	
	
	/**
	 * ComboBoxにPair型の表示方法（StringConverter）を設定する
	 */
	private void setupPairConverter(ComboBox<TableColumnManager.Pair<Integer, String>> cb) {
	    cb.setConverter(new StringConverter<TableColumnManager.Pair<Integer, String>>() {
	        @Override
	        public String toString(TableColumnManager.Pair<Integer, String> object) {
	            // Pairオブジェクトから名称(name)を取り出して画面に表示する
	            return (object == null) ? "" : object.name();
	        }

	        @Override
	        public TableColumnManager.Pair<Integer, String> fromString(String string) {
	            ObservableList<Pair<Integer, String>> items = cb.getItems();
	            
	            // items が null または空の場合は処理を中断する
	            if (items == null || string == null || string.isEmpty()) {
	                return null;
	            }
	        	
	        	
	        	
	        	// 編集時に文字列から元のPairオブジェクトを探す処理
	            return cb.getItems().stream()
	                .filter(item -> item.name().equals(string))
	                .findFirst()
	                .orElse(null);
	        }
	    });
	}
	
	
	
	
	
	/**
	 * 編集モード確定(EnterKey押下)時に次のセルFocus移動
	 * @brief
	 * リフレクション（setAccessible(true)）を使うことが禁止されているため、<br>
	 * データのバインドにColumnのIDを用いる<br>
	 */
	private void setupDefaultEditCommitHandler() {
       
		if(!isEnterNextFocus){ return; }

		// 編集モード時、確定(EnterKey)イベント
		this.setOnEditCommit(event -> {
    	   // String propertyName = getPropertyName();
    	   String propertyName = this.getId();
    	   
    	   S rowData = event.getRowValue();
    	   T newValue = event.getNewValue();
    	   
    	   /*  TEST */
    	   Pair<Integer, String> A = (Pair<Integer, String>)newValue;
    	   System.out.println("選択値 [" + A.code.toString() + "]"   );
    	   
    	   
    	   // モデルへの値反映
    	    try {
    	        // メソッド(値のSetter プロパティ)名の生成
    	        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    	        
                // モデルからメソッドを探して実行
                Method setter = rowData.getClass().getMethod(methodName, newValue.getClass());
                setter.invoke(rowData, newValue);
    	    } catch (Exception e) {
    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
                System.err.println("モデルへの値反映に失敗しました: " + propertyName);
    	    	
    	    	e.printStackTrace();
    	    }
    	   
           // 2. 次のセルへの遷移
           javafx.application.Platform.runLater(() -> {
               TableView<S> tv = event.getTableView();
               int currentRow = event.getTablePosition().getRow();
               
               // 次の編集可能なカラムを探す
               TableColumn<S, ?> nextCol = getNextEditableColumn(tv, this);

               if (nextCol != null) {
                   tv.getSelectionModel().select(currentRow, nextCol);
                   tv.edit(currentRow, nextCol);
               } else {
                   // 次の行へ
                   int nextRow = currentRow + 1;
                   if (nextRow < tv.getItems().size()) {
                       TableColumn<S, ?> firstCol = tv.getColumns().get(0);
                       tv.getSelectionModel().select(nextRow, firstCol);
                       tv.edit(nextRow, firstCol);
                   }
               }
           });
       });
   }

   private TableColumn<S, ?> getNextEditableColumn(TableView<S> tv, TableColumn<S, ?> currentCol) {
       List<TableColumn<S, ?>> cols = tv.getColumns();
       int index = cols.indexOf(currentCol);
       for (int i = index + 1; i < cols.size(); i++) {
           TableColumn<S, ?> col = cols.get(i);
           if (col.isEditable() && col.isVisible()) return col;
       }
       return null;
   }	

   /**
    * PropertyValueFactory からプロパティ名を抽出
    * @brief 使用禁止：（InaccessibleObjectException）が発生<br>
    * 最新のJava（モジュールシステム）では、JavaFXの内部変数に対して
    * リフレクション（setAccessible(true)）を使うことが禁止されているため<br>
    */
   private String getPropertyName() {
       // 1. カラムに設定されている CellValueFactory を取得
       var factory = this.getCellValueFactory();

       if (factory instanceof PropertyValueFactory) {
           try {
               // 2. PropertyValueFactory クラスの内部フィールド "property" にアクセス
               Field field = PropertyValueFactory.class.getDeclaredField("property");
               field.setAccessible(true); // プライベートフィールドへのアクセスを許可
               
               // 3. フィールドの値を文字列として取得
               return (String) field.get(factory);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return null;
   }   
}
