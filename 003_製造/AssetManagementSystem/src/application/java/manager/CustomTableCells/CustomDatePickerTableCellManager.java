package application.java.manager.CustomTableCells;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.java.common.AppUtil;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.MouseEvent;

/**
 * カスタムControl：TableCell + DatePicker
 * @param <S> (Table Source / Subject): 行データのクラス名。
 * @param <T> (Column Type): セルに表示する項目の型。
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomDatePickerTableCellManager<S, T> extends TableCellManager<S, T> {
	private final DatePicker datePicker;
    private final Boolean isAlwaysShow;

    private final LocalDate defaultDate;
    private final LocalDate lowerDate;
    private final LocalDate upeerDate;
    
	private Boolean isAdjusting = false;

    // datePickerを外部から取得するためのメソッドを追加
    public DatePicker getDatePicker() {
        return this.datePicker;
    }	
	
	
	/**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムチェックボックス)のカラムのID)
     * @param isAlwaysShow CheckBoxを常時表示するか
	 */
	@SuppressWarnings("unused")
	public CustomDatePickerTableCellManager(
			String colId,
			LocalDate calShowFirstDate,
			LocalDate minDate,
			LocalDate maxDate,
			Boolean isAlwaysShow) {

		this.isAlwaysShow = isAlwaysShow;

		this.lowerDate = minDate;
		this.upeerDate = maxDate;
		
		if (calShowFirstDate == null) {
			this.defaultDate = LocalDate.now();
		} else {
			this.defaultDate = calShowFirstDate;
			};
		
        this.datePicker = new DatePicker();
        
        // カスタムControlの生成
		createCustomDatePickerBox(colId);
        
		if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // マウスが放された瞬間
        this.datePicker.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (!datePicker.isShowing()) {
            	// 非表示の場合、強制表示
            	Platform.runLater(() -> { datePicker.show(); });
            }
        });
        
        onFocusDatePicker();
	}

	/**
     * セルが入力(編集)モードに移行する際に内部で呼び出されるメソッド(Enter/GotFocus Event)
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・ユーザーがダブルクリックや [F2]・[Enter]入力など（プラットフォームに依存）を行った瞬間<br>
	 *  ※ 下記の条件の場合のみ<br>
	 *      ・TableView が editable(true)<br>
	 *      ・TableColumn が editable(true)<br>
 	 *      ・当該セル自体が editable<br>
     */
	@Override
	public void startEdit() {
    	super.startEdit();
    	
    	System.out.println("CustomDatePicker startEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(this.datePicker);
        	setText(null);
        } else if (!isEditing()) {
        	System.out.println("CustomDatePicker NotEditMode");
        	return;
        }

    	/* 後処理リスナー(startEditの後) */
        Platform.runLater(() -> {
            if (this.datePicker != null && this.datePicker.getScene() != null) {
    	    	System.out.println("CustomDatePicker EditMode");
    	    	this.datePicker.requestFocus();
            }
        });
    }

    /**
     * 入力(編集)モード中にキャンセルする際に内部で呼び出されるメソッド
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・[ESC]入力など（プラットフォームに依存）を行った瞬間<br>
	 *  ※ 下記の条件の場合のみ<br>
	 *      ・TableView が editable(true)<br>
	 *      ・TableColumn が editable(true)<br>
 	 *      ・当該セル自体が editable<br>
 	 *  [commitEdit + フォーカス移動]<br>
     */
    @Override
    public void cancelEdit() {
    	super.cancelEdit();
    	
        System.out.println("CustomDatePicker cancelEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
    		// 非編集モードへ遷移
            setGraphic(null);
    		if (AppUtil.isDate(getItem().toString())){
    			setText(getItem().toString());
    		} else {
    			setText(null);
    		}
    		return;
        }
    	
    	/* 常時表示モード */
    	setGraphic(this.datePicker);
    } 	
		
    /**
     * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時<br>
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時<br>
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時<br>
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時<br>
     */
    @Override
    protected void updateItem(T item, boolean empty) {
    	super.updateItem(item, empty);
    	
    	System.out.println("CustomDatePicker updateItem");
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty || item == null || item.toString().isEmpty()) {
            setGraphic(null);
        } else {
        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */
        		try {
        	        isAdjusting = true; // 開始
	        		if (AppUtil.isDate(item.toString())){
		                // モデルの文字列(yyyy/MM/dd)をLocalDateに変換してセット
	        			LocalDate date = LocalDate.parse(
		                		item.toString(), 
		                		DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	        			
	        			// 現在のDatePickerの値とモデルの値が違う場合のみセットする
	        		    if (!date.equals(this.datePicker.getValue())) {
	        		        this.datePicker.setValue(date);
	        		    }
	        		} else { this.datePicker.setValue(null); }
        	    } finally {
        	    	// 例外が起きても必ず最後に false にする
        	    	isAdjusting = false;
        	    }
                // 既にセットされている場合は再セットしない（フォーカス喪失を防ぐ）
                if (getGraphic() != this.datePicker) {
                    setGraphic(this.datePicker);
                }
        	} else {
                /* 編集時のみ表示モード */
        		if (isEditing()) {
                    setGraphic(this.datePicker);
                } else {
                    setGraphic(null);
                }
        		if (!isEditing()) {
        			setText(item.toString());
        		}
            }
        }}
	    
    /**
     * カスタムチェックボックス生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     */
    private void createCustomDatePickerBox(String colId) {
    	// 表示位置 中央
    	this.setAlignment(Pos.CENTER);

        // DatePicker における IME（入力メソッド）関連の挙動を無効化、あるいは制御する
        datePicker.setInputMethodRequests(new InputMethodRequests() {
            @Override public Point2D getTextLocation(int offset) { return new Point2D(0, 0); }
            @Override public int getLocationOffset(int x, int y) { return 0; }
            @Override public void cancelLatestCommittedText() {}
            @Override public String getSelectedText() { return ""; }
        }); 
    	
    	// チェック確定機能追加
    	onLeaveDatePickerValue(colId);

    	// カレンダー表示設定
    	onCalenderShown();
    	onCalenderLimitDateRange();
    }

    /**
     * 機能追加：DatePicker選択確定動作(Leave Event)
     * @param colId カラムのID(自身(カスタムControl)のカラムのID)
	 * @brief 選択したItemをカラムのBINDソース[S]に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提
	 * 「setOnAction」：ユーザーが操作を確定させた瞬間<br>
	 *  ※ 下記の条件の場合のみ<br>
	 *      ・カレンダー（ポップアップ）から日付をクリックして選択した瞬間<br>
	 *      ・テキストフィールドに直接日付を入力し、Enterキーを押して確定した瞬間<br>
 	 *      ・（プログラムから datePicker.fireEvent(new ActionEvent()) を明示的に呼んだ時）<br>
 	 * 「valueProperty」： Listener　との違い<br> 
 	 *  比較項目	valueProperty (Listener)	        setOnAction (Action)<br>
 	 *  発生条件	値が1ミリでも変われば発生	        ユーザーが「これだ」と決めた時<br>
 	 *  自動更新時	setValue() すると反応してしまう	    setValue() では反応しない<br>
 	 *  利点	    常に最新状態を追える	            無限ループや意図しない上書きを防げる<br>
     */	
	@SuppressWarnings({ "unchecked", "unused" })
	private void onLeaveDatePickerValue(String colId) {
        // 入力監視リスナー(Leave相当)
		// カレンダーの選択に対応するため[setOnAction]を捉える
		this.datePicker.setOnAction(
        		(e) -> {
        			if (isAdjusting) { return; }
        			
        			System.out.println("CustomDatePicker setOnAction");
        			LocalDate newVal = datePicker.getValue();
        			
        		    // newVal が null（空欄）の場合の処理を明示する
        		    if (newVal == null) {
        		    	// モデルを空文字で更新
        		        commitEdit((T)""); 
        				super.setRowClassProperty(colId, String.class, "");
        		        return;
        		    }
        			
    	            // モデルへの反映 (String型として保存する場合)
    	            String value = newVal.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        			
        			if (isEditing()) {
        				// 編集モード(値の確定)
        				this.commitEdit((T)value);
        			} else {
        				//System.out.println("値反映処理実行: " + value);
        				getTableView().edit(getIndex(), getTableColumn());
        				this.commitEdit((T)value); 
        				
        				// 非編集モード(値の確定)
        				// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
        				super.setRowClassProperty(colId, String.class, value);
        			}});}
	
	/**
	 * 機能追加：DatePickerFocus遷移の動作(GetFocus / LostFocus Event)
	 */
	@SuppressWarnings("unused")
	private void onFocusDatePicker() {
        this.datePicker.focusedProperty().addListener(
        		(obs, wasFocused, isFocused) -> {
        			if (isFocused) {
        				/* 後処理リスナー */
        				Platform.runLater(() -> {
        					if (getIndex() >= 0 && getTableView() != null) {
        		                // TableViewに現在のセルを編集状態にするよう依頼
        		                getTableView().edit(getIndex(), getTableColumn());
        		                // DatePickerに明示的に再度フォーカスを戻す（Windows11 IME対策）
        		                this.datePicker.requestFocus();
        		                }
        					
        					System.out.println("CustomDatePicker LostFocus");
        					// 下限(lower)より前、または上限(upper)より後の日付を無効化
            	            LocalDate date = datePicker.getValue();
            				
            	            if (date == null) { return; }
            	            boolean isBeforeLower = (lowerDate != null && date.isBefore(lowerDate));
            	            boolean isAfterUpper = (upeerDate != null && date.isAfter(upeerDate));
        					
        					// 範囲外なら強制的に null (または oldValue) に戻す
        					if (isBeforeLower || isAfterUpper) {
        			            datePicker.setValue(null); 
        			            datePicker.getEditor().clear();
        			            System.out.println(" Focuus : 選択範囲外の日付です: " + date); }
        				});}
        		});}
	
	/**
	 * 機能追加：カレンダー表示 初期日付設定
	 */
	@SuppressWarnings("unused")
	private void onCalenderShown() {
	    // カレンダーが開かれようとした時の処理を登録
	    datePicker.setOnShowing(e -> {
	        // 現在の値が空（null）の場合だけ、カレンダーの初期選択を今日にする
	        if (datePicker.getValue() == null) {
	            Platform.runLater(() -> {
	                datePicker.setValue(defaultDate);
	            });
	        }});
	}
	
	/**
	 * 機能追加：カレンダー表示 選択可能な日付の範囲を制限
	 * @param lower 下限日（これより前は選択不可）
	 * @param upper 上限日（これより後は選択不可）
	 */
	@SuppressWarnings("unused")
	public void onCalenderLimitDateRange() {
	    datePicker.setDayCellFactory(picker -> new DateCell() {
	        
	    	@Override
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);

	            if (date == null) { return; }

	            // 下限(lower)より前、または上限(upper)より後の日付を無効化
	            boolean isBeforeLower = (lowerDate != null && date.isBefore(lowerDate));
	            boolean isAfterUpper = (upeerDate != null && date.isAfter(upeerDate));

	            if (isBeforeLower || isAfterUpper) {
	                setDisable(true);
	                // 無効な日付をグレーアウトする
	                setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: #b0b0b0;");
	            }
	        }
	    });}
}
