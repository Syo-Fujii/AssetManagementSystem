package application.java.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import application.java.base.BaseTableViewModel;
import application.java.manager.customTableCells.CustomComboBoxTableCellManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * カスタムControl(TableColumn)
 * @param <S> 継承元が[BaseTableViewModel]のデータクラス(1行データのクラス)
 * @param <T>
 */
public class TableColumnManager<S extends BaseTableViewModel,T> extends TableColumn<S, T>  {

	private Boolean isEnterNextFocus = true;
	
    /** ComboBox設定用 keyValuePair */
    public record Pair<K, V>(K code, V name) {}
	
	
	/**
	 * TableView カラム(Cell)設定可否 判定
	 * @return TableView カラム(Cell)設定可否
	 * @brief カラムヘッダ・セル設定を完了したかの判定。初期表示などで利用
	 */
	public Boolean getIsEnterNextFocus() {
		return isEnterNextFocus;
	}

	/**
	 * TableView カラム(Cell)設定可否 設定
	 * @param isCompleted TableView カラム(Cell)設定可否
	 * @brief カラムヘッダ・セル設定を完了したかの判定。初期表示などで利用
	 */
	public void setIsEnterNextFocus(Boolean isNextFocus) {
		this.isEnterNextFocus = isNextFocus;
	}
	
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
	 * 編集モード確定(EnterKey押下)時に次のセルFocus移動
	 * @brief
	 * リフレクション（setAccessible(true)）を使うことが禁止されているため、<br>
	 * データのバインドにColumnのIDを用いる<br>
	 * CostomしたContorolで制御した場合、当該TableView(Column)とEventが干渉するため、<br>
	 * Foucs制御はTableViwe(Column)項目で行う
	 */
	private void setupDefaultEditCommitHandler() {
       
		if(!isEnterNextFocus){ return; }

		// 編集モード時、確定(EnterKey)イベント
		this.setOnEditCommit(event -> {
   	
			// String propertyName = getPropertyName();
			String propertyName = this.getId();

			S rowData = event.getRowValue();
			T newValue = event.getNewValue();
    	   
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
    	   
    	    if(!isEnterNextFocus){ return; }
    	    // 次のセルへの遷移
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
    	    			tv.edit(nextRow, firstCol);}
               }
           });
       });
   }

	/**
	 * 次の編集可能なCellを取得
	 * @param tv
	 * @param currentCol
	 * @return
	 */
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
