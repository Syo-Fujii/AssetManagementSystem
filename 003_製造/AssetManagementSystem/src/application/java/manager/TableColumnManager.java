package application.java.manager;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import application.java.base.BaseTableViewModel;
import application.java.manager.CustomTableCells.CustomCheckBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomComboBoxKvpSourceManager;
import application.java.manager.CustomTableCells.CustomComboBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomComboBoxWithChecBoxkManager;
import application.java.manager.CustomTableCells.CustomDatePickerTableCellManager;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
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

	private static final PseudoClass DISABLED_PC = PseudoClass.getPseudoClass("disabled-cell");
	
	private Boolean isEnterNextFocus = true;
	
	/* 内部クラス */
	public record keyValuePairItem<K, V>(Integer key, String value) {};
	
	/**
	 * コンストラクタ
	 */
	public TableColumnManager() {
        super();
        
        setupDefaultEditCommitHandler();
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
	 * @param isValueChenged 値によって動的に変更するか
	 * @brief setCellValueFactory( new PropertyValueFactory<>())と競合しない
	 */
	@SuppressWarnings("unused")
	public void IsEnabled(Boolean isEnabled, Boolean isValueChenged) {
		this.setEditable(isEnabled);
        
        this.setCellFactory(col -> new TableCell<S, T>() {
            {
            	// コンストラクタ
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * 
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                	
                	if( isValueChenged ) {
                    	setDisable(false);
                    	pseudoClassStateChanged(DISABLED_PC, false);
                    	setFocusTraversable(false);      
                	}  
                } else {
                    setText(item.toString());
                	
                	if( isValueChenged ) {
                    	setDisable( !isEnabled );
                    	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                    	setFocusTraversable(isEnabled);      
                	} 
                }  
            }
        });
	}
	
	/**
	 * ComboBox型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	public void setCellTypeCustomComboBox(ObservableList<T> items, Boolean isEnabled, Boolean isAlwaysShow){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
 
		this.setCellFactory(
    			col -> new CustomComboBoxTableCellManager<S,T>(this.getId(), items, isAlwaysShow){
    		// 初期化ブロックの為、親のコンストラクタ(super)は実行済
            {
            	// 初期化ブロック
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * 
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
                    setGraphic(null);
                    setText(null);

                    // 【重要】isValueChengedがfalseであっても、
                    // 再利用対策として「空セル」は標準状態（disable=false）に戻す 
                	setDisable(false);
                	pseudoClassStateChanged(DISABLED_PC, false);
                	setFocusTraversable(false); 
                    
                } else {
                    setText(item.toString());
 
                	setDisable( !isEnabled );
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                	setFocusTraversable(isEnabled);                      
                }  
            }
        });		
	}

	/**
	 * ComboBox型セル(選択リスト KeyValuePair)の設定
	 * @param items 選択リストに表示するデータ
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	@SuppressWarnings({ "unused" })
	public void setCellTypeCustomComboBoxKeyValues(
			ObservableList<keyValuePairItem<Integer, String>> kvpItems,
			String keyModelName,			
			Boolean isEnabled, 
			Boolean isAlwaysShow){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
 
		this.setCellFactory(
    			col -> new CustomComboBoxKvpSourceManager<S, Integer, T>(this.getId(), keyModelName, kvpItems, isAlwaysShow){
    		// 初期化ブロックの為、親のコンストラクタ(super)は実行済
            {
            	// 初期化ブロック
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。<br>
             * カスタムControlの[updateItem]を呼出す(super)こと
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
                    // 再利用の際、空行などの場合
         			setGraphic(null);
                    setText(null);

                    // isValueChengedがfalseであっても、
                    // 再利用対策として「空セル」は標準状態（disable=false）に戻す 
                	setDisable(false);
                	pseudoClassStateChanged(DISABLED_PC, false);
                	setFocusTraversable(false); 
                    
                } else {
                    setText(item.toString());
 
                	setDisable( !isEnabled );
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                	setFocusTraversable(isEnabled);                      
                }  
            }
        });			
	}

	/**
	 * ComboBox型セル(選択リスト KeyValuePair)の設定
	 * @param items 選択リストに表示するデータ
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param CheckBoxModelName 値更新に連動させるModelのプロパティ名
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	@SuppressWarnings({ "unused" })
	public void setCellTypeCustomComboBoxKeyValuesWithCheck(
			ObservableList<keyValuePairItem<Integer, String>> kvpItems,
			String keyModelName,
			String CheckBoxModelName,
			Boolean isEnabled, 
			Boolean isAlwaysShow){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
		 
		this.setCellFactory(
				col -> new CustomComboBoxWithChecBoxkManager<S, Integer, T>(
						this.getId(), 
						keyModelName, 
						CheckBoxModelName,
						kvpItems, 
						isAlwaysShow){
    		// 初期化ブロックの為、親のコンストラクタ(super)は実行済
            {
            	// 初期化ブロック
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。<br>
             * カスタムControlの[updateItem]を呼出す(super)こと
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
         			// 再利用の際、空行などの場合
         			setGraphic(null);
                    setText(null);

                    // isValueChengedがfalseであっても、
                    // 再利用対策として「空セル」は標準状態（disable=false）に戻す 
                	setDisable(false);
                	pseudoClassStateChanged(DISABLED_PC, false);
                	setFocusTraversable(false); 
                    
                } else {
                    setText(item.toString());
 
                	setDisable( !isEnabled );
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                	setFocusTraversable(isEnabled);                      
                }  
            }
        });		
	}
	
	/**
	 * CheckBox型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param items 選択リストに表示するデータ
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	@SuppressWarnings("unused")
	public void setCellTypeCustomCheckBox(Boolean isEnabled, Boolean isAlwaysShow){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
		 
    	this.setCellFactory(
    			col -> new CustomCheckBoxTableCellManager<S,T>(this.getId(), isAlwaysShow){
    		// 初期化ブロックの為、親のコンストラクタ(super)は実行済
            {
            	// 初期化ブロック
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。<br>
             * カスタムControlの[updateItem]を呼出す(super)こと
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
         		// 再利用の際、空行などの場合
         			setGraphic(null);
                    setText(null);

                    // 【重要】isValueChengedがfalseであっても、
                    // 再利用対策として「空セル」は標準状態（disable=false）に戻す 
                	setDisable(false);
                	pseudoClassStateChanged(DISABLED_PC, false);
                	setFocusTraversable(false); 
                    
                } else {
                    setText(item.toString());
 
                	setDisable( !isEnabled );
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                	setFocusTraversable(isEnabled);                      

                	// 編集時のみ表示MODEでのTEXT設定
					if (isAlwaysShow) {
						setText(null);
					} else {
			            if (item instanceof Boolean) {
			                setText((Boolean) item ? "CHECKED" : "");
			            } else {
			                setText(item.toString());
			            }
					}                
                }  
            }
        });	
	}

	/**
	 * DatePicker型セルの設定
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	@SuppressWarnings("unused")
	public void setCellTypeCustomDatePicker(
			Boolean isEnabled, 
			Boolean isAlwaysShow,
			LocalDate defaultDate,
			LocalDate minDate,
			LocalDate maxDate){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
		 
	   	this.setCellFactory(
    			col -> new CustomDatePickerTableCellManager<S,T>(
    					this.getId(),
    					defaultDate,
    					minDate,
    					maxDate,
    					isAlwaysShow){
    		// 初期化ブロックの為、親のコンストラクタ(super)は実行済
            {
            	// 初期化ブロック
            	if( !isValueChenged ) {
                	setDisable(!isEnabled);
      
                    // 見た目の調整（非活性時にグレーアウトさせる等）
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled);
                	setFocusTraversable(isEnabled);
            	}
            }

            /**
             * 
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。
             */
            @Override
            public void updateItem(T item, boolean empty) {
            	super.updateItem(item, empty);

         		if (empty || item == null) {
                    setGraphic(null);
                    setText(null);

                    // 【重要】isValueChengedがfalseであっても、
                    // 再利用対策として「空セル」は標準状態（disable=false）に戻す 
                	setDisable(false);
                	pseudoClassStateChanged(DISABLED_PC, false);
                	setFocusTraversable(false); 
                    
                } else {
                 	setText(item.toString());
 
                	setDisable( !isEnabled );
                	pseudoClassStateChanged(DISABLED_PC, !isEnabled );
                	setFocusTraversable(isEnabled);                      

                	
                	// LocalDate date = null;
					/*
					if (!AppUtil.StringIsNullOrEmpty(item.toString()))
					{
				        try {
				        	date = LocalDate.parse(item.toString(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
				        } catch (Exception e) {
				            throw e;
				        }
						
						// 下限(lower)より前、または上限(upper)より後の日付を無効化
        	            boolean isBeforeLower = (minDate != null && date.isBefore(minDate));
        	            boolean isAfterUpper = (maxDate != null && date.isAfter(maxDate));
    					
    					// 範囲外なら強制的に nullに戻す
    					if (isBeforeLower || isAfterUpper) {
    						getDatePicker().setValue(null);
    						getDatePicker().getEditor().clear();
    						commitEdit(null); // モデルも空にする
    			            
    			            setText("");
    			            System.out.println("TableColumn : 選択範囲外の日付です: " + date); 
    			        } else {
    			        	setText(date != null ? 
    			        			date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : 
    			        			"");
    			        }
					}*/
					
					/*if (isAlwaysShow) {
						// 現在表示されている graphic が datePicker でない場合のみセット
						// ※ これにより、クリック時の再描画による「消え」を防ぐ
						Node currentGraphic = getGraphic();
						if (currentGraphic == null || !(currentGraphic instanceof DatePicker)) {
		                    // 現在セットされているインスタンスが DatePicker で、かつ
		                    // 表示中の値が同じなら、一切のプロパティ変更を行わない
		        			if (currentGraphic == getDatePicker() && 
		        				Objects.equals(date, getDatePicker().getValue())) {
		                        return;
		                    }
							// 必要な時だけセット
							if (getGraphic() != getDatePicker()) { setGraphic(this.getDatePicker()); }
						}

						// 内部のDatePickerにも状態を伝播させる
						Node graphic = getGraphic();
						if (graphic instanceof DatePicker dp) { dp.setDisable(!isEnabled); }
					} */
                }  
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
		// 入力監視リスナー
		// (event = 変更を監視しているプロパティ:event = TableVolumn.CellEditEvent)
		this.setOnEditCommit(event -> {
           
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
			
			System.out.println("getNextEditableColumn : OK");
       
		}
        System.out.println("getNextEditableColumn : Return NULL");
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
