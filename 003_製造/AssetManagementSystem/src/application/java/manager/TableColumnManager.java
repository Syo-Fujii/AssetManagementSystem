package application.java.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import application.java.base.BaseTableViewModel;
import application.java.common.AppUtil;
import application.java.manager.CustomTableCells.CustomCheckBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomComboBoxKvpSourceManager;
import application.java.manager.CustomTableCells.CustomComboBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomDatePickerTableCellManager;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * カスタムControl(継承：TableColumn)
 * @param <S> 継承元が[BaseTableViewModel]のデータクラス
 * @param <T> カラムのデータ型
 * @brief TableColumnを継承したカスタムControlのManagerクラス。<br>
 * 各カスタムTableColumnの呼び出しを当該メソッドで行う。<br>
 */
public class TableColumnManager<S extends BaseTableViewModel,T> extends TableColumn<S, T>  {

	private Boolean isEnterNextFocus = true;
	
	/* 内部クラス */
	public record keyValuePairItem<K, V>(Integer key, String value) {};
	
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

	                // 見た目の調整（非活性時にグレーアウトさせる等）
	                if (!isEnabled) {
	                    setStyle("-fx-opacity: 0.5; -fx-background-color: #f4f4f4;");
	                } else {
	                    setStyle(""); 
	                }
    	        }
    	    }});}
	
	/**
	 * ComboBox型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 */
	@SuppressWarnings("unused")
	public void setCellTypeCustomComboBox(ObservableList<T> items, Boolean isEnabled, Boolean isAlwaysShow){
		
    	this.setCellFactory(
    			col -> new CustomComboBoxTableCellManager<S,T>(this.getId(), items, isAlwaysShow){

    			      @Override
    			        public void updateItem(T item, boolean empty) {
    			          // CustomComboBoxTableCellManager.updateItem
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
	 * ComboBox型セル(選択リスト KeyValuePair)の設定
	 * @param items 選択リストに表示するデータ
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void setCellTypeCustomComboBoxKeyValues(
			ObservableList<keyValuePairItem<Integer, String>> kvpItems,
			String keyModelName,			
			Boolean isEnabled, 
			Boolean isAlwaysShow){
		
		try {
			this.setCellFactory(
					col -> new CustomComboBoxKvpSourceManager(this.getId(), keyModelName, kvpItems, isAlwaysShow){

						public void updateItem(String item, boolean empty) {
							// CustomComboBoxTableCellManager.updateItem
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
								}}});
	    	} catch (Exception ex) {
	    		System.err.println(ex);
	    		}
		}

	/**
	 * CheckBox型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 */
	@SuppressWarnings("unused")
	public void setCellTypeCustomCheckBox(Boolean isEnabled, Boolean isAlwaysShow){
		
    	this.setCellFactory(
    			col -> new CustomCheckBoxTableCellManager<S,T>(this.getId(), isAlwaysShow){

    				@Override
    				public void updateItem(T item, boolean empty) {
    					// CustomComboBoxTableCellManager.updateItem
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
    							setStyle("");}
    						
    						if (!isAlwaysShow) {
        				        if (empty || item == null) {
        				            setText(null);
        				        } else {
        				            setText((boolean) item ? "CHECKED" : "");
        				        }
    						}}
    				} });}

	/**
	 * DatePicker型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 */
	@SuppressWarnings("unused")
	public void setCellTypeCustomDatePicker(
			Boolean isEnabled, 
			Boolean isAlwaysShow,
			LocalDate defaultDate,
			LocalDate minDate,
			LocalDate maxDate){
		
    	this.setCellFactory(
    			col -> new CustomDatePickerTableCellManager<S,T>(
    					this.getId(),
    					defaultDate,
    					minDate,
    					maxDate,
    					isAlwaysShow){

    				@Override
    				public void updateItem(T item, boolean empty) {
    					// CustomDatePickerTableCellManager.updateItem
    					super.updateItem(item, empty);
    					
    					if (empty || item == null) {
    						setDisable(false);
    						setGraphic(null);
    					} else {
    						// ここで有効・無効を制御
    						setDisable(!isEnabled);
    			            
    						// 非活性時はクリックできないようにする
    						setFocusTraversable(isEnabled);
    			            
    						// 見た目の調整（非活性時にグレーアウトさせる等）
    						if (!isEnabled) {
    							setStyle("-fx-opacity: 0.5; -fx-background-color: #f4f4f4;");
    						} else {
    							setStyle(""); }

    						if (!AppUtil.StringIsNullOrEmpty(item.toString()))
    						{
            					// 下限(lower)より前、または上限(upper)より後の日付を無効化
    							LocalDate date = LocalDate.parse(
        		                		item.toString(), 
        		                		DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                	            
                	            boolean isBeforeLower = (minDate != null && date.isBefore(minDate));
                	            boolean isAfterUpper = (maxDate != null && date.isAfter(maxDate));
            					
            					// 範囲外なら強制的に null (または oldValue) に戻す
            					if (isBeforeLower || isAfterUpper) {
            					    getDatePicker().setValue(null);
            					    commitEdit(null); // モデルも空にする
            			            getDatePicker().getEditor().clear();
            			            setText("");
            			            System.out.println("TableColumn : 選択範囲外の日付です: " + date); } 
    						}
        					
    						if (isAlwaysShow) {
    							// 現在表示されている graphic が datePicker でない場合のみセット
    							// ※ これにより、クリック時の再描画による「消え」を防ぐ
    							Node currentGraphic = getGraphic();
    							if (currentGraphic == null || !(currentGraphic instanceof DatePicker)) {
    								// 必要な時だけセット
    								setGraphic(this.getDatePicker()); }

    							// 内部のDatePickerにも状態を伝播させる
    							Node graphic = getGraphic();
    							if (graphic instanceof DatePicker dp) {
    								dp.setDisable(!isEnabled); }
    						}}
    				} });}
	
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
    	   
    	   // モデルへの値反映
    	    try {
    	        // メソッド(値のSetter プロパティ)名の生成
    	        String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    	        
                // モデルからメソッド(Setter)を探して実行
                Method setter = rowData.getClass().getMethod(methodName, newValue.getClass());
                setter.invoke(rowData, newValue);
                
                System.out.print("TableColumnManager.setupDefaultEditCommitHandler");
                
    	    } catch (Exception e) {
    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
                System.err.println("モデルへの値反映に失敗しました: " + propertyName);
    	    	
    	    	e.printStackTrace();
    	    }
    	   
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
                       tv.edit(nextRow, firstCol);
                   }
               }
           });
       });
   }

   /**
    * 次の編集可能なカラムを探す
    * @param tv 対象TableView
    * @param currentCol 対象TableColumn
    * @return 編集可能なTableColumn
	* @brief 対象TableColumn より次の編集可能なTableColumnを返す。<br>
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
   @SuppressWarnings("unused")
   private String getPropertyName() {
       // カラムに設定されている CellValueFactory を取得
       var factory = this.getCellValueFactory();

       if (factory instanceof PropertyValueFactory) {
           try {
               // PropertyValueFactory クラスの内部フィールド "property" にアクセス
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
