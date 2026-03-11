package application.java.manager;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import application.java.base.BaseTableViewModel;
import application.java.common.AppUtil;
import application.java.manager.CustomTableCells.CustomCheckBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomComboBoxKvpSourceManager;
import application.java.manager.CustomTableCells.CustomComboBoxTableCellManager;
import application.java.manager.CustomTableCells.CustomComboBoxWithCheckBoxManager;
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
	 * @param <S> 継承元が[BaseTableViewModel]のデータクラス
	 * @param <T> 対象カラムの型
	 * @param isEnabled Boolean 有効化判定
	 * @param isValueChenged Boolean 値によって動的に変更するか
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
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
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
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
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
	 * ComboBox型セル(選択リスト KeyValuePair + 連動プロパティ)の設定
	 * @param items 選択リストに表示するデータ
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param CheckBoxModelName 値更新に連動させるModelのプロパティ名
     * @param placeHolderText 未選択時(空欄)時に表示させる文字(未設定の場合は、表示しない)
	 * @param isEnabled CELL 有効化制御
	 * @param isAlwaysShow 常にComboBoxを表示するか
	 * @brief 値による動的変更はない[false]とする。
	 */
	@SuppressWarnings({ "unused" })
	public void setCellTypeCustomComboBoxKeyValuesWithCheck(
			ObservableList<keyValuePairItem<Integer, String>> kvpItems,
			String keyModelName,
			String CheckBoxModelName,
			String placeHolderText,
			Boolean isEnabled, 
			Boolean isAlwaysShow){
		Boolean isValueChenged = false;
		
		this.setEditable(isEnabled);
		 
		this.setCellFactory(col -> {
		   CustomComboBoxWithCheckBoxManager<S, Integer, T> cb = new CustomComboBoxWithCheckBoxManager<S, Integer, T>(
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
		  };
		
		  if (!AppUtil.StringIsNullOrWhiteSpace(placeHolderText)) {
			  // プレースホルダー（プロンプトテキスト）を設定
			  cb.setPlaceHolder(placeHolderText);			  
		  }
		  
		  return cb;
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
             * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
             * @param item
             * @param empty
             * @brief
             * 行の再利用対策: TableCell は画面に見えている分しか生成されない。<br>
             * スクロールして「有効な行」だったセルが「空の行」になった際、<br>
             * setDisable を更新しないと、空のセルが非活性のまま残るなどの表示バグが起きる。
             */
            @Override
            public void updateItem(T item, boolean empty) {

            	// CustomDatePickerTableCellManager.updateItem
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
	 * 編集モード確定(EnterKey押下)時に次のセルFocus移動
	 * @brief
	 * リフレクション（setAccessible(true)）を使うことが禁止されているため、<br>
	 * データのバインドにColumnのIDを用いる<br>
	 */
	private void setupDefaultEditCommitHandler() {
       
		if(!isEnterNextFocus){ return; }

		onDisabledCellsFocusSkipEvent();
   }

   /**
    * 次の編集可能なカラムを探す
    * @param tv 対象TableView
    * @param currentCol 対象TableColumn
    * @return 編集可能なTableColumn
	* @brief 対象TableColumn より次の編集可能なTableColumnを返す。<br>
	* 有効なカラム
    */
	private void onDisabledCellsFocusSkipEvent() {
		
		// 編集モード時、確定(EnterKey)イベント
		// 入力監視リスナー
		// (event = 変更を監視しているプロパティ:event = TableVolumn.CellEditEvent)		
		this.setOnEditCommit(event -> {
			// 処理確定後の動作
			javafx.application.Platform.runLater(() -> {
				TableView<S> tableview = event.getTableView();
				List<TableColumn<S, ?>> columns = tableview.getColumns();

				// カラム数を取得
		        int columnsCount = columns.size();
		        // 明細行数を取得
		        int rowsCount = tableview.getItems().size();
		        
		        if (columnsCount < 1 || rowsCount < 1) { return; }
		        
		        // 係数
		        int direction = 1;
		        
		        // 次のCELLのINDEXを取得
		        int nextColIdx = columns.indexOf(this) + direction;
		        // 現在の明細行を取得
		        int rowIdx = event.getTablePosition().getRow();

		    	
		        // 有効なセルが見つかるまで、行・列をまたいで探索
		        while (rowIdx >= 0 && rowIdx < rowsCount)
		        {
		        	// 列の範囲外チェック（行をまたぐ処理）
		            if (nextColIdx >= columnsCount) {
		            	// [右]移動(あふれ)の場合、次の行の左端へ
		            	rowIdx++;
		            	nextColIdx = 0;
		                continue;
		            }

		            // 行移動後に範囲外になった場合は終了
		            if ( rowIdx >= rowsCount ){ break; }
		            
		            TableColumn<S, ?> targetCol = columns.get(nextColIdx);
		            
		            if (targetCol.isEditable() && targetCol.isVisible() ) {
		            	/* 有効CELL */
		            	tableview.getSelectionModel().select(rowIdx, targetCol);
		            	tableview.edit(rowIdx, targetCol);
		            	System.out.println("getNextEditableColumn : OK");

		            	break;
		            
		            } else {
		            	/* 無効CELL */
		            	nextColIdx += direction;
		            }
		        } 	
		        
		        System.out.println("getNextEditableColumn : Return NULL");
			});
		});
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
