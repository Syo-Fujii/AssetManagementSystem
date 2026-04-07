package application.java.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * カスタムControl(継承：TableView)
 * @param [T] 継承元が[BaseTableViewModel]のデータクラス(1行データのクラス)
 * @brief TableViewを継承したカスタムControl。<br>
 * データSOURCEは、継承元が[BaseTableViewModel]のデータクラスとする。<br>
 * ※ データクラス(1行)の配列(List)をデータとする。<br>
 * ScreenBuilderでは、継承元に関する各機能は実行できない。(コンテナ化されて編集できない)<br>
 * 同一画面内で複数のTableViewが配置される場合を考慮し、各動作(Event)をCallBackにて設定する。<br>
 *  ⇒  各動作に対するEventは配置したController(画面クラス)にて定義する。
 * @note 
 * [javafx.scene.control.TableView]に各イベントを追加したカスタムクラス。<br>
 * 配置された画面の他Controlとの受け渡しは、配置したConntrollerクラス内のメソッドで行う。<br>
 * 各イベントの動作内容(メソッド)は、配置したConntrollerクラス内に定義し、CallBackにて実装する。
 */
public class TableViewManager<T extends BaseTableViewModel> extends TableView<T> {

	private String cssSelector = "table-view";
	private Boolean isReverting = false; // 再選択中かどうかのフラグ
	private Boolean isFocusMoveing = false; //Focus遷移中かどうかのフラグ	
	
	private Boolean isColumnSettingCompleted = false;

	private Boolean isMultiSelected = false;
	private Boolean isCellSelected = false;
	private Boolean isReorderabled = false;	


	/**
	 * CSSセレクタ名(識別名)取得
	 * @return cssSelector CSSセレクタ名
	 */
	public String getCssSelector() {
		return this.cssSelector;
	}

	/**
	 * CSSセレクタ名(識別名)設定
	 * @param selector CSSセレクタ名
	 * @brief 複数設置した際に、識別セレクタ名を設定する。
	 */
	public void setCssSelector(String selector) {
		
		this.cssSelector = selector;
		
		if (selector.isEmpty()) {
			this.getStyleClass().add("table-view");
		} else {
			this.getStyleClass().add(selector);
		}
	}

	/**
	 * TableView カラム(Cell)設定可否 判定
	 * @return TableView カラム(Cell)設定可否
	 * @brief カラムヘッダ・セル設定を完了したかの判定。初期表示などで利用
	 */
	public Boolean getIsColumnSettingCompleted() {
		return isColumnSettingCompleted;
	}

	/**
	 * TableView カラム(Cell)設定可否 設定
	 * @param isCompleted TableView カラム(Cell)設定可否
	 * @brief カラムヘッダ・セル設定を完了したかの判定。初期表示などで利用
	 */
	public void setIsColumnSettingCompleted(Boolean isCompleted) {
		this.isColumnSettingCompleted = isCompleted;
	}
	
	
	/**
	 * 複数行選択判定
	 * @return
	 */
	public Boolean getIsMultiSelected() {
		return this.isMultiSelected;
	}

	/**
	 * 複数行選択設定
	 * @param isMultiSelected 複数行選択判定
	 * @brief [真]の場合、複数行(Cell)選択可。[偽]の場合、単一行(Cell)選択。
	 */
	public void setIsMultiSelected(Boolean isMultiSelected) {
		this.isMultiSelected = isMultiSelected;
		
		if (isMultiSelected) {
			this.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		} else {
			this.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
		}
	}

	/**
	 * Cell(単一項目)選択判定
	 * @return
	 */
	public Boolean getIsCellSelected() {
		return isCellSelected;
	}

	/**
	 * Cell(単一項目)選択設定
	 * @param isCellSelected Cell(単一項目)選択判定
	 * @brief [真]の場合、Cell(単一項目)選択可。[偽]の場合、行(Row)選択。
	 */
	public void setIsCellSelected(Boolean isCellSelected) {
		this.isCellSelected = isCellSelected;
		// セル選択モード
		this.getSelectionModel().setCellSelectionEnabled( isCellSelected );
	}
	
	/**
	 * 全カラム移動判定
	 * @return isReorderabled
	 */
	public Boolean getIsReorderabled() {
		return isReorderabled;
	}

	/**
	 * 全カラム移動設定
	 * @param isReorderabled カラム移動可否
	 */
	public void setIsReorderabled(Boolean isReorderabled) {
		this.isReorderabled = isReorderabled;
		// 全てのカラムの移動を設定
		this.getColumns().forEach(col -> col.setReorderable(isReorderabled));
	}
	
	
	/**
	 * コンストラクタ
	 */
	public TableViewManager() {
        super();
        // System.out.println("TableViewManager New");
	}

	/**
	 * 行番号付きの明細行を返す
	 * @param <T>
	 * @return 行番号付きの明細行(rowNum:明細行番号,　model：行データ)のリスト(行番号付きデータ)
	 * @brief 行番号付きの明細行を[AppConst.addRowNumData]型で返す。<br>
	 * 明細行がない場合、NULLを返す。<br>
	 */
	public List<AppConst.addRowNumData<T>> getRowsAddNumber() {

		List<T> rows = (List<T>) this.getItems();

		if(rows == null || rows.isEmpty()){ return null; }
		
    	// 行番号を付与
		return IntStream.
				range(0, rows.size()).
    			mapToObj(i -> new AppConst.addRowNumData<>(i + 1, rows.get(i))).
    			collect(Collectors.toList());
    }	
	
	/**
	 * 重複している明細行を返す
	 * @param <T>
	 * @param propertyName String 重複を確認するプロパティ名
	 * @return 重複している行(:model：行データ)のリスト(行番号付きデータ)
	 * @brief 重複している行のリストをaddRowNumData型(int:行番号, model:行データ)で返す
	 * 明細行がない場合、NULLを返す。<br>
	 */
	public List<AppConst.addRowNumData<T>> getDuplicateRows( String propertyName) {
		
    	// 行番号を付与
		List<AppConst.addRowNumData<T>> numRows = this.getRowsAddNumber();

		if(numRows == null || numRows.isEmpty()){ return null; }		
		
    	// メソッド(値のGetter プロパティ)名の生成
        String methodName = "get" +
    	                    propertyName.substring(0, 1).toUpperCase() +
    	                    propertyName.substring(1);
        	
        return numRows.stream().
        		// [propertyName]でグループ化する (Map<String, List<addRowNumData<T>>>)
    			collect(Collectors.groupingBy((AppConst.addRowNumData<T> item) -> {
    				try {
    					T model = item.model();
    					Object value = model.getClass().getMethod(methodName).invoke(model);
				                
    					// nullや空文字の場合、重複グループに入れないようユニークな値を返す
    					return (value == null || value.toString().isEmpty()) ? UUID.randomUUID() : value;
    				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
    					e.printStackTrace();
    					throw new RuntimeException("リフレクションエラー: " + methodName, e);
    				}
				})).
    			values().
    			stream().
    			// リストのサイズが 1 より大きい（重複している）グループだけ残す
    			filter(list -> list.size() > 1).
    			// 全ての重複データを一つのリストにまとめる(平坦化:Groupの展開)
    			flatMap(List::stream).
    			collect(Collectors.toList());
    }	
	
	/**
	 * 項目(column-Data)Bind設定
	 * @param callback 戻り値・引数のないメソッド
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
	public void setBindColumnCallBack(Runnable callback) 
	{
		callback.run();
	}
	
	/**
	 * カラム列移動(順番)可否設定
	 * @param col
	 * @param isReorderabled
	 */
	public void setColumnReorderable(TableColumn<T, ?> col, Boolean isReorderabled) 
	{
		// 特定のカラムの移動の可否
		col.setReorderable(isReorderabled);
	}
	
	/**
	 * CELL 有効化制御
	 * @param <T> 継承元が[BaseTableViewModel]のデータクラス
	 * @param <C> 対象カラムの型
	 * @param column TableColumn 対象カラム
	 * @param isEnabled Boolean 有効化判定
	 */
	@SuppressWarnings({ "hiding", "unused" })
	public <T, C> void cellIsEnabled(TableColumn<T, C> column, Boolean isEnabled) {
		Boolean isValueChenged = false;
		
		column.setEditable(isEnabled);
		
		column.setCellFactory(col -> new TableCell<T, C>() {
			{
				// コンストラクタ
				if( !isValueChenged ) {
					setDisable(!isEnabled);
  
					// 見た目の調整（非活性時にグレーアウトさせる等）
					//pseudoClassStateChanged(DISABLED_PC, !isEnabled);
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
    	    protected void updateItem(C item, boolean empty) {
    	    	
    	    	
    	        super.updateItem(item, empty);
    	        
    	        if (empty || item == null) {
    	            setText(null);
    	            setGraphic(null);
    	            // 空のセルは無効化しない（リセット）
    	            setDisable(false);
    	        } else {
    	        	setText(item.toString());
    	        	
    	        	if(isValueChenged) {
        	        	// 選択・操作を制御
        	            setDisable(!isEnabled);
        	            setFocusTraversable(isEnabled);
    	        	}
    	        }
    	    }
    	});
	}

	/**
	 * TableView Focusイベント
	 * @param lostCallback Focusが抜けた項目に対するEvent 引数、戻り値なし
	 * @param selectedCallback Focusされた場合に対するEvent 引数、戻り値なし
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 *  呼出元イベント[focusedProperty().addListener]
	 */	
	@SuppressWarnings("unused")
	public void onTableViewFocusEvent(Runnable lostCallback, Runnable selectedCallback) {
    	this.focusedProperty().addListener((observable, oldValue, newValue) -> {
    	    if (lostCallback != null && !newValue) {
    	    	lostCallback.run();
    	    	return;
    	    }
    	    
    	    if (selectedCallback != null && newValue)
    	    {
    	    	selectedCallback.run();
    	    }
    	});		
	}
	
	/**
	 * 明細選択行イベント
	 * @param lostCallback 選択が抜けた項目に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 *  呼出元イベント[getSelectionModel().selectedItemProperty().addListener]
	 */
	public void onSelectedRowEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {

		// 選択
		if (this.isMultiSelected )
		{
			this.addSelectedMultiRowsEvent(lostCallback, selectedCallback);
		} else {
			this.addSelectedRowEvent(lostCallback, selectedCallback);
		}
	}
	
	/**
	 * 明細選択行イベント(Focus遷移)
	 * @param cancelCheckCallback Focus遷移をキャンセルするかを判定するEvent <br>
	 * 戻り値[Boolean：判定結果]・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 明細行の選択を変更した場合(Focusを遷移した場合)のイベント<br> 
	 * 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 *  呼出元イベント[getSelectionModel().selectedItemProperty().addListener]
	 */
	public void onSelectedRowLeaveEvent(Predicate<T> cancelCheckCallback, Consumer<T> resultCallback) {
		this.onSelectedRowEvent(
			    bef -> {
			    	isReverting = false;
			    	
			    	if ( isFocusMoveing ) { return; }
			    	
			    	if (bef != null ) {
				    	try 
			    		{
			    			if ( cancelCheckCallback.test(bef)) {
		                    	isReverting = true;			                
			    				Platform.runLater(() -> {
				                    try {
				                    	isFocusMoveing = true;
				    			    	this.getSelectionModel().select(bef);
				                        this.getFocusModel().focus(this.getItems().indexOf(bef));
				                    } finally {
				                    	isFocusMoveing = false;
				                    }});
			    			}
			    		} finally {
	                    	isFocusMoveing = false;
			    		}
			    	}
			    },
			    result -> {
			    	try {
				    	if ( isFocusMoveing || isReverting ) { return; }		    		

				    	Platform.runLater(() -> {
		                    try {
		    			    	resultCallback.accept(result);
		                    } finally {
		                    	isFocusMoveing = false;
		                    }});			    		
			    	} finally {
                    	isFocusMoveing = false;
                    	isReverting = false;
			    	}
			    });
	}	
	/**
	 * 明細選択行イベント(Focus遷移)
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 明細行の選択を変更した場合(Focusを遷移した場合)のイベント<br> 
	 * 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 *  呼出元イベント[getSelectionModel().selectedItemProperty().addListener]
	 */
	@SuppressWarnings("unused")
	public void onSelectedRowLeaveEvent(Consumer<T> resultCallback) {
	    this.onSelectedRowLeaveEvent(bef -> false, resultCallback);
	}
	
	/**
	 * CELL選択イベント
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
	public void onSelectedCellsEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {

		// 選択
		if (this.isMultiSelected )
		{
			this.addSelectedCellsEvent(selectedCallback);
		} else {
			this.addSelectedCellEvent(lostCallback, selectedCallback);
		}
	}	

	/**
	 * 無効化CELL 選択(Focus)スキップ イベント
	 * @brief Key[左]・[右]・[TAB]を押下した場合のEventを制御(次(前)のCELLへ遷移)
	 */
	public void onDisabledCellsFocusSkipEvent() {
    	this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
    	    if (event.getCode() == KeyCode.RIGHT || 
    	    	event.getCode() == KeyCode.TAB || 
    	    	event.getCode() == KeyCode.LEFT)
    	    {
    	    	// 現在のフォーカス位置を取得
    	        @SuppressWarnings("unchecked")
				TablePosition<T, ?> pos = this.getFocusModel().getFocusedCell();
    	        if (pos == null) { return; }
    	        
    	        // カラム数を取得
    	        int columnsCount = this.getColumns().size();
    	        // 明細行数を取得
    	        int rowsCount = this.getItems().size();
    	        
    	        if (columnsCount < 1 || rowsCount < 1) { return; }
    	        
    	        // [左]移動の場合、マイナス係数にする
    	        int direction = (event.getCode() == KeyCode.LEFT) ? -1 : 1;
    	        
    	        // 次のCELLのINDEXを取得
    	        int nextColIdx = pos.getColumn() + direction;
    	        // 現在の明細行を取得
    	        int rowIdx = pos.getRow();

    	        LogManager.writeTrace("[CELL FOCUS制御]");
    	        LogManager.writeTrace("列数: [" + columnsCount + "] ");
    	        LogManager.writeTrace("行数: [" + rowsCount + "] ");    	        
    	        LogManager.writeTrace("係数: [" + direction + "] ");   
    	        LogManager.writeTrace("[次]選択列番号: [" + nextColIdx + "] ");
    	        LogManager.writeTrace("選択行番号: [" + rowIdx + "] ");
            	
    	        // 有効なセルが見つかるまで、行・列をまたいで探索
                while (rowIdx >= 0 && rowIdx < rowsCount)
                {
                	LogManager.writeTrace("[CELL FOCUS制御]");
                	LogManager.writeTrace("[次]選択列番号: [" + nextColIdx + "] ");
                	LogManager.writeTrace("選択行番号: [" + rowIdx + "] ");
                	
                	// 列の範囲外チェック（行をまたぐ処理）
                    if ( nextColIdx < 0) { 
                    	// [左]移動(あふれ)の場合、前の行の右端へ
                    	rowIdx--;
                    	nextColIdx = columnsCount - 1;
                    	continue;
                    
                    } else if (nextColIdx >= columnsCount) {
                    	// [右]移動(あふれ)の場合、次の行の左端へ
                    	rowIdx++;
                    	nextColIdx = 0;
                        continue;
                    }

                    // 行移動後に範囲外になった場合は終了
                    if (rowIdx < 0 || rowIdx >= rowsCount){ break; }
                    
                    TableColumn<T, ?> targetCol = this.getColumns().get(nextColIdx);
                    
                    if (targetCol.isEditable()) {
    	            	/* 有効CELL */

                    	// スキップが発生した(前回の移動先が無効CELL)場合
    	                if (!event.isConsumed()){ event.consume(); }

                        this.getSelectionModel().clearAndSelect(rowIdx, targetCol);
                        this.getFocusModel().focus(rowIdx, targetCol);    	                

                        break;
                        //return; // 探索終了
                    
                    } else {
    	            	/* 無効CELL */

                    	// Focus移動をキャンセル
                    	if (!event.isConsumed()){ event.consume(); }
                    	// 無効なセルの場合はさらに次へ
                    	nextColIdx += direction;
                    }
                } 
    	    }});
	}
	
	/**
	 * 選択行番号取得
	 * @brief 複数選択Modeの場合、[-1]を返す。
	 */
	public Integer getSelectedRowNumber() {
		if(this.isMultiSelected)
		{
			return -1;
		}

		return this.getSelectionModel().getSelectedIndex();
	}		

	/**
	 * データセット(List型)
	 * @param data　継承元が[BaseTableViewModel]のデータクラスのリスト
	 * @brief FXCollections.observableArrayList は、<br>
	 * 「中身が変更されたらUI（TableViewなど）に即座に通知する」 機能を備えた、JavaFX専用のリスト
	 */
	public void setList(List<T> data) {
        ObservableList<T> observableListData = 
                FXCollections.observableArrayList(data);
		
		this.setItems( observableListData );
	}

	/**
	 * データセット(SortedList型)
	 * @param data　継承元が[BaseTableViewModel]のデータクラスのリスト
	 * @brief SortedListはTableViewの[カラム]でのSORT機能を考慮したLIST<br>
	 * TableViewのSORTに応じて、内部LISTの行順番も変更される。<br>
	 * FXCollections.observableArrayList は、<br>
	 * 「中身が変更されたらUI（TableViewなど）に即座に通知する」 機能を備えた、JavaFX専用のリスト<br>
	 */
	public void setSortedList(List<T> data) {
		// SortedListでラップ
		SortedList<T> sortedrows = 
    			new SortedList<>(FXCollections.observableArrayList(data));
    
    	// TableViewのソート状態と同期
    	sortedrows.comparatorProperty().bind(this.comparatorProperty());  

		this.setItems( sortedrows );
	}	
	
	/**
	 * 明細データ初期化処理
	 * @brief JavaFxの機能として、CELLの再利用(既に生成されたCELL)を有している。<br>
	 * 画面遷移(表示外明細行にスクロール)やデータ(DataSource)の入れ替えを行う際に<br>
	 * 新行を生成するのではなく、既に生成されている(現在の表示に用いている)セルを利用(値の差し替え等)して<br>
	 * データの切替をおこなっている。<br>
	 * データの差し替えを行う際に、既に生成されているセルに値が設定されている場合は、データのBINDを差し替えても<br>
	 * 差し替え前のセルの設定値が残ったままになる場合があるので、<br>
	 * 当該メソッドにて、データのBIND設定・CELLの値を完全初期化する。<br>
	 * 再検索などのデータに切替を行う際、切替前に呼び出すこと。
	 */
	public void dataSourceClear() {
	    // 内部の選択インデックスをリセット（重要）
	    this.getSelectionModel().clearSelection();
	    
	    // データの入れ替え前に、TableView内部の「現在地」を完全に忘却させる
	    this.getSelectionModel().select(null); 
	    this.getFocusModel().focus(-1);
	    
	    // リストを空のObservableListで上書き（直接クリアせずインスタンスごと替えるのが安全）
	    this.setItems(null);
	    this.setItems(FXCollections.observableArrayList());

	    this.refresh();	
	}
	
	/**
 	 * 初期Focus設定(先頭行, 指定カラム)
	 * @param <S>
	 * @param <V>
	 * @param targetColumn 指定するカラム
	 * @brief 「ウィンドウが表示された直後」にフォーカスを要求する。<br>
	 * Stage の setOnShown イベントを使う
	 */
	public <S extends TableColumn<T, V>, V> void setFocusFirstCell(S targetColumn) {
	    Platform.runLater(() -> {		
	    	Platform.runLater(() -> {
	    		// 明細行が1行以上あるかチェック
	    		if (!this.getItems().isEmpty()) {

	    			this.requestFocus();
	    			this.getSelectionModel().clearSelection();

	    			this.getSelectionModel().select(0, targetColumn);
	    			this.edit(0, targetColumn);

	    			this.scrollTo(0);
	    		}
	    	});
	    });
	}

	/**
	 * Focus設定(指定行, 指定カラム)
	 * @param rowIndex 対象行番号
	 * @param colIndex 対象カラム番号
	 */
	public void setCellFocus(int rowIndex, int colIndex) {
	    Platform.runLater(() -> {
	        // 2回目：UIの再描画が終わった後に実行させる
			Platform.runLater(() -> {
		        if (rowIndex < 0 || colIndex < 0) return;
		        
		        TableColumn<T, ?> targetColumn = this.getColumns().get(colIndex);
		        
			    // テーブル自体をフォーカス
		        this.requestFocus();
		        this.getSelectionModel().clearSelection();
		     
		        // 選択の指定(Cell)
		        this.getSelectionModel().select(rowIndex, targetColumn);
		   
		        // Focusの指定(Cell)
		        this.getFocusModel().focus(rowIndex, targetColumn);
		 
		        // 指定したCellの明細行までスクロール
		        this.scrollTo(rowIndex);
		    });
	    });
	}	
	
	/**
	 * 行選択(１行)Event
	 * @param lostCallback 選択が抜けた行に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択行に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
	@SuppressWarnings("unused")
	private void addSelectedRowEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {
        
		// 行選択EVENTの登録
		this.getSelectionModel().selectedItemProperty().addListener((ov, old, current) -> {

	    	// 選択アウトEvent
			if ( lostCallback != null && old != null) {
				lostCallback.accept(old);
		    }
			
			// 選択行Event
			if (current != null) {
				selectedCallback.accept(current);
		    }
		});
	}	
	
	/**
	 * 行選択(複数行)Event
	 * @param lostCallback 選択が抜けた全行に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択行(複数)に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 * 現時点では、各選択行に対して全て同じイベントを実行する前提。必要に応じて修正を要す。 
	 */
	private void addSelectedMultiRowsEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {
        
		// 複数行選択EVENTの登録
		this.getSelectionModel().getSelectedItems().addListener(
				(ListChangeListener<T>) change ->  {
				    while (change.next()) {
				        if (change.wasRemoved()) {
				        	// 選択アウトEvent(選択されていた全行)
				        	change.getRemoved().forEach(row -> lostCallback.accept(row));
				        }
				    	if (change.wasAdded()) {
				    		// 選択行Event(選択されていた全行)
				        	change.getAddedSubList().forEach(row -> selectedCallback.accept(row));
				        }
				    }});
	}

	/**
	 * CELL選択(単一Cell)Event
	 * @param lostCallback 選択が抜けたCellに対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択Cellに対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 * 現時点では、各選択Cellに対して全て同じイベントを実行する前提。必要に応じて修正を要す。 
	 */
	@SuppressWarnings("unused")
	private void addSelectedCellEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {
        
		/* JavaFXの TableView.getSelectionModel().getSelectedCells() メソッドは、設計上の制約（JDK-8089446）により、
		 * ジェネリクスが欠落した ObservableList<TablePosition> (生の型) を返します。
		 * そのため、型安全な ObservableList<TablePosition<T, ?>> に直接代入しようとすると、
		 * コンパイラが「型の不一致」としてエラーを出します。
		 * よって下記ないようにて実装。未検証。
		 * */
		TableView<T> tb = (TableView<T>)this;
		tb.getSelectionModel().selectedItemProperty().addListener(
				(ov, old, current) ->
				{
			    	// 選択アウトEvent
					if ( lostCallback != null && old != null) {
						lostCallback.accept(old);
				    }
					
					// 選択行Event
					if (current != null) {
						selectedCallback.accept(current);
				    }
				});
	}		
	
	/**
	 * CELL選択(複数)Event
	 * @param selectedCallback 選択Cell(複数)に対するEvent 戻り値なし・引数:Cellの値(Object)
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。<br>
	 * 現時点では、各選択Cellに対して全て同じイベントを実行する前提。必要に応じて修正を要す。 
	 */
	@SuppressWarnings("unused")
	private void addSelectedCellsEvent(Consumer<T> selectedCallback) {
        
		/* JavaFXの TableView.getSelectionModel().getSelectedCells() メソッドは、設計上の制約（JDK-8089446）により、
		 * ジェネリクスが欠落した ObservableList<TablePosition> (生の型) を返します。
		 * そのため、型安全な ObservableList<TablePosition<T, ?>> に直接代入しようとすると、
		 * コンパイラが「型の不一致」としてエラーを出します。
		 * よって下記ないようにて実装。未検証。
		 * */
		TableView<T> tb = (TableView<T>)this;
		tb.getSelectionModel().getSelectedCells().addListener(
				(ListChangeListener<Object>) change ->
				{
					for (Object obj : change.getList()) 
					{
						@SuppressWarnings("unchecked")
						TablePosition<T, ?> pos = (TablePosition<T, ?>) obj;
						
						// 選択行・列の情報を取得
						int row = pos.getRow();
						TableColumn<T, ?> col = pos.getTableColumn();
						
						// 選択行を取得
						T item = tb.getItems().get( row );
						
						// 選択セルを取得
						selectedCallback.accept(item);
					}
				});
	}	
}
