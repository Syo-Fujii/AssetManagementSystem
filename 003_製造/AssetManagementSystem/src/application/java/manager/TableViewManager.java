package application.java.manager;

import java.util.List;
import java.util.function.Consumer;

import application.java.base.BaseTableViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

/**
 * カスタムControl(TableView)
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
	
	private Boolean isColumnSettingCompleted = false;

	private Boolean isRowsMultiSelected = false;
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
	public Boolean getIsRowsMultiSelected() {
		return this.isRowsMultiSelected;
	}

	/**
	 * 複数行選択設定
	 * @param isMultiSelected 複数行選択判定
	 * @brief [真]の場合、複数行(Cell)選択可。[偽]の場合、単一行(Cell)選択。
	 */
	public void setIsRowsMultiSelected(Boolean isMultiSelected) {
		this.isRowsMultiSelected = isMultiSelected;
		
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
	 * カラム移動判定
	 * @param col
	 * @param isReorderabled
	 */
	public void setColumnReorderable(TableColumn<T, ?> col, Boolean isReorderabled) 
	{
		// 特定のカラムの移動の可否
		col.setReorderable(isReorderabled);
	}
	
	/**
	 * 明細選択行イベント
	 * @param lostCallback 選択が抜けた項目に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
	public void onSelectedRowEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {

		// 選択
		if (this.isRowsMultiSelected )
		{
			this.addSelectedMultiRowsEvent(lostCallback, selectedCallback);
		} else {
			this.addSelectedRowEvent(lostCallback, selectedCallback);
		}
	}
	
	/**
	 * CELL選択イベント
	 * @param selectedCallback 選択に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
	public void onSelectedCellsEvent(Consumer<T> lostCallback, Consumer<T> selectedCallback) {

		// 選択
		if (this.isRowsMultiSelected )
		{
			this.addSelectedCellsEvent(selectedCallback);
		} else {
			this.addSelectedCellEvent(lostCallback, selectedCallback);
		}
	}	

	
	/**
	 * 選択行番号取得
	 */
	public Integer getSelectedRowNumber() {
		if(this.isRowsMultiSelected)
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
	 * 行選択(１行)Event
	 * @param lostCallback 選択が抜けた行に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @param selectedCallback 選択行に対するEvent 戻り値なし・引数:継承元が[BaseTableViewModel]のデータクラス
	 * @brief 同一画面内で複数のTableViewが配置される場合を考慮し、動作(Event)をCallBackにて設定する。<br>
	 *  ⇒  動作に対するEventは配置したController(画面クラス)にて定義する。
	 */
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
