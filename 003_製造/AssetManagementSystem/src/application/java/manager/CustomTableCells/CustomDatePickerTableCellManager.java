package application.java.manager.CustomTableCells;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import application.java.base.BaseTableViewModel;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * カスタムControl：TableCell + DatePicker
 * @param <S> (Table Source / Subject): 行データのクラス名。
 * @param <T> (Column Type): セルに表示する項目の型。
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomDatePickerTableCellManager<S extends BaseTableViewModel, T> extends TableCellManager<S, T> 
{
	private final DatePicker datePicker;
    private final Boolean isAlwaysShow;

    private final LocalDate defaultDate;
    private final LocalDate lowerDate;
    private final LocalDate upperDate;

    private String format = "yyyy/MM/dd";
    private String regex = "^[0-9]{0,4}/?[0-9]{0,2}/?[0-9]{0,2}$";
    private Integer maxLength = 10;    
    
	private Boolean isAdjusting = false;
	private String columnId = "";

	/**
	 * 日付の入力形式(書式) 取得
	 * @return format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 日付の入力形式(書式) 設定
	 * @param format セットする format
	 */
	public void setFormat(String format) {
		this.format = format;
	}	

	/**
	 * 入力制限(正規表現パターン) 取得
	 * @return regex
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * 入力制限(正規表現パターン) 設定
	 * @param regex セットする regex
	 */
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	/**
	 * 最大文字数 取得
	 * @return maxLength
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * 最大文字数 取得
	 * @param maxLength セットする maxLength
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}
	
	/**
	 * テキスト入力の入力制限 設定
	 * @param format String 日付の形式
	 * @param regexPattern String 正規表現パターン
	 * @param length Integer 最大文字数
	 */
    public void setValidInput(String format, String regexPattern, Integer length) {
    	if (!AppUtil.StringIsNullOrWhiteSpace(format)) {
    		this.setFormat(format);;
    	}
    
    	if (!AppUtil.StringIsNullOrWhiteSpace(regexPattern)) {
    		this.setRegex(regexPattern);
    	} 
    	
    	if (length != null) { this.setMaxLength(length); }
    	
    	this.setupTextValidation();
    }
	
    // datePickerを外部から取得するためのメソッドを追加
    public DatePicker getDatePicker() {
        return this.datePicker;
    }	    
    
	
	/**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムチェックボックス)のカラムのID)
	 * @param calShowFirstDate LocalDate 日付の初期値
	 * @param minDate LocalDate 許容される日付の最小値
	 * @param maxDate LocalDate 許容される日付の最大値
     * @param isAlwaysShow CheckBoxを常時表示するか
	 */
	public CustomDatePickerTableCellManager(
			String colId,
			LocalDate calShowFirstDate,
			LocalDate minDate,
			LocalDate maxDate,
			Boolean isAlwaysShow) {

		this.isAlwaysShow = isAlwaysShow;

		this.lowerDate = minDate;
		this.upperDate = maxDate;
		
		if (calShowFirstDate == null) {
			this.defaultDate = LocalDate.now();
		} else {
			this.defaultDate = calShowFirstDate;
			};
		
        this.datePicker = new DatePicker();
        this.columnId = colId;
        
        // カスタムControlの生成
		createCustomDatePickerBox();
		
		if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
		this.datePicker.addEventFilter(KeyEvent.ANY, e -> {
		    // カレンダー表示中のキー操作による意図しないフォーカス移動を防止
		    if (this.getDatePicker().isShowing()) {
		        e.consume(); 
		    }
		});		
		
		// マウスが放された瞬間
        /* this.datePicker.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (!datePicker.isShowing()) {
            	// 非表示の場合、強制表示
            	Platform.runLater(() -> { datePicker.show(); });
            }
        }); */
        
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
    	
		LogManager.writeTrace("[CustomDatePicker][startEdit] Start");
    	
		if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(this.datePicker);
        	setText(null);
        
		} else if (!isEditing()) {
        	return;
        }

    	/* 後処理リスナー(startEditの後) */
        Platform.runLater(() -> {
            if (this.datePicker != null && this.datePicker.getScene() != null)
            {
    	    	this.datePicker.requestFocus();
    	    	this.datePicker.getEditor().requestFocus();
            }
        });
        
        LogManager.writeTrace("[CustomDatePicker][startEdit] End");
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
    	
    	LogManager.writeTrace("[CustomDatePicker][cancelEdit] Start");
        
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

    	LogManager.writeTrace("[CustomDatePicker][updateItem] Start");
    	LogManager.writeTrace("Column : [" + columnId + "]");
    	LogManager.writeTrace("item : [" + (item != null ?item.toString() : "NULL" ) + "]");
    	LogManager.writeTrace("dp.text : [" + this.datePicker.getEditor().getText() + "]");
    	LogManager.writeTrace("dp.value : [" + this.datePicker.getValue() + "]");
    	
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
    	if ( empty  || item == null ) {
        	setGraphic(null);
        	// TableCell.SetText
        	setText(null);
 
    	} else {
    		// データが存在する場合の表示処理

    		LocalDate itemDate = null;
        	
    		if( AppUtil.isDate(item.toString()))
        	{
        		itemDate = LocalDate.
        				parse(item.toString(),
        						DateTimeFormatter.
        						ofPattern("yyyy/MM/dd"));
        	}
    		
    		// 表示する前に、現在のデータモデルの値をセットする(初期値の設定)
            if (!Objects.equals(itemDate, datePicker.getValue())) {
                isAdjusting = true;
                try {
                    datePicker.getEditor().setText(item.toString());
                    datePicker.setValue(itemDate);
                } finally {
                    isAdjusting = false;
                }
            }		
    		
    		// データが存在する場合の表示処理
    		if (isAlwaysShow) {
    			/* 常時表示モード */
  
    			LocalDate calenderDate = this.datePicker.getValue();
    			
    			// 現在セットされているインスタンスが DatePicker で、かつ
    			// 表示中の値がVALUEと同じなら、一切のプロパティ変更を行わない（フォーカス喪失を防ぐ）
    			if (getGraphic() != this.datePicker ||
            				!Objects.equals(itemDate, calenderDate)) {
    				setGraphic(this.datePicker);
    			}

    			setText(null);

    		} else {
    			/* 編集時のみ表示モード */
    			if (isEditing()) {
    				setGraphic(this.datePicker);
    				setText(null);
    			} else {
    				setGraphic(null);
    				// TableCell.SetText
    				setText(item.toString());
    			}
    		} 
        }
    	LogManager.writeTrace("[CustomDatePicker][updateItem] End");  
    }
	    
    /**
     * カスタムDatePicker生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     */
    private void createCustomDatePickerBox() {
    	
    	// DatePickerの配置(Cellの中央)
    	this.setAlignment(Pos.CENTER);

    	// DataPickerのサイズ(Cellに合わせる)
    	datePicker.prefWidthProperty().bind(this.widthProperty().subtract(5.0));
    	datePicker.setMaxWidth(Control.USE_PREF_SIZE);

    	this.getDatePicker().setFocusTraversable(false);
    	this.getDatePicker().getEditor().setFocusTraversable(false);
    	
    	// マウスでテキスト部分をクリックしてもフォーカスを奪わせない
    	this.getDatePicker().getEditor().setMouseTransparent(true);
    	
    	datePicker.getEditor().setAlignment(Pos.CENTER);
    	
        // DatePicker における IME（入力メソッド）関連の挙動を無効化、あるいは制御する
        datePicker.setInputMethodRequests(new InputMethodRequests() {
            @Override public Point2D getTextLocation(int offset) { return new Point2D(0, 0); }
            @Override public int getLocationOffset(int x, int y) { return 0; }
            @Override public void cancelLatestCommittedText() {}
            @Override public String getSelectedText() { return ""; }
        }); 
		
	    // テキスト入力制限 追加
	    setupTextValidation();        
        
    	// チェック確定機能追加
    	onLeaveDatePickerValue(this.columnId);

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
	@SuppressWarnings({ "unused", "unchecked" })
	private void onLeaveDatePickerValue(String colId) {

		// 入力監視リスナー(Leave相当)
		// カレンダーの選択に対応するため[setOnAction]を捉える
		this.datePicker.setOnAction(
        		(e) -> {
        			if (isAdjusting || isEmpty() || getIndex() < 0 ) { return; }
         			
        			LogManager.writeTrace("[CustomDatePicker][setOnAction] Start");
         			
        			LocalDate date = datePicker.getValue();	
        			String value = ( date != null ) ? 
         			       date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "" ;
        			
        			// 入力制御      	
        			// 範囲外なら強制的に nullに戻す
        			if (dateIsOutOfRange(date)) {
        				LogManager.writeError("TableColumn : 選択範囲外の日付です: " + date);; 
        	            date = null;
        	            value = "";
        			}
        			
        			// 強制的に編集モードにする
        			getTableView().edit(getIndex(), getTableColumn());

        			this.commitEdit((T) value);
        			
    				// モデルへの値反映
        			bindingModelProperty(colId, value, date);
         		});
	}

	/**
	 * 機能追加：DatePickerFocus遷移の動作(GetFocus / LostFocus Event)
	 * @brief 選択した日付をカラムのBINDソースに反映<br>
	 * カラムの[id]と[fx:id]は同一の前提<br>
	 * カレンダーの日付を選択した/ 入力を確定した瞬間に発報されるイベント<br>
	 * setOnActionと二重実行される。isAdjustingにて監視・制御すること。
	 * setValueに対するListener<br>
	 */
	@SuppressWarnings("unused")
	private void onFocusDatePicker() {
        this.datePicker.focusedProperty().addListener(
        		(obs, wasFocused, isFocused) -> {
        			if (isFocused && !isAdjusting) {
        				
        				LogManager.writeTrace("[CustomDatePicker][focusedProperty] Start");
        				
        				/* 後処理リスナー */
        				Platform.runLater(() -> {
        		            try {
            		            isAdjusting = true;
            		            if (getIndex() >= 0 && getTableView() != null) {
            		            	/*※当該メソッドを有効化すると隣の項目とFocusを取り合う無限LOOPが発生
            		            	// TableViewに現在のセルを編集状態にするよう依頼
            		            	getTableView().edit(getIndex(), getTableColumn());*/
            		            	
            		            	// DatePickerに明示的に再度フォーカスを戻す（Windows11 IME対策）
            		                this.datePicker.requestFocus();
            		            }
        		            } finally {
        		                isAdjusting = false;
        		            }
        				});
        }});
	}
	
	/**
	 * 機能追加：カレンダー表示 初期日付設定
	 */
	@SuppressWarnings("unused")
	private void onCalenderShown() {
	    // カレンダーが開かれようとした時の処理を登録
	    datePicker.setOnShowing(e -> {
	    	
	    	Platform.runLater(() -> {
	    		this.requestFocus(); 

	    		// 現在の値が空（null）の場合だけ、カレンダーの初期選択を今日にする
	    		if (datePicker.getValue() == null) {
	    			LogManager.writeTrace("[CustomDatePicker][CalenderShown] 初期値設定");
	    			
	    			// セットにより[valueProperty().addListener]が発火するため[Adjusting]しない
	    			datePicker.setValue(defaultDate);
	    		}
	    	});
	    });
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
	            if (dateIsOutOfRange(date)) {
	                setDisable(true);
	                // 無効な日付をグレーアウトする
	                setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: #b0b0b0;");
	            }
	    	}
	    });
	}

	/**
	 * 正規表現　最大文字数を用いた入力制限
	 */
	private void setupTextValidation() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		
		// コンバーターの設定
		datePicker.setConverter(new StringConverter<LocalDate>() {
		    @Override
		    public String toString(LocalDate date) {
		        return (date != null) ? formatter.format(date) : "";
		    }
		    
		    @Override
		    public LocalDate fromString(String string) {
		        if (string == null || string.isEmpty()) return null;
		        try {
		            return LocalDate.parse(string, formatter);
		        } catch (Exception e) {
		            return null;		        }
		    }
		});	
		
		// 入力制限
		datePicker.getEditor().setTextFormatter(new TextFormatter<>( change -> 
		{
		    String newText = change.getControlNewText();
		
		    // 正規表現 & 文字数制限
		    if (newText.matches(regex) && newText.length() <= maxLength) {
		        return change;
		    }
		    
		    return null;
		}));
	}
	
	/**
	 *設定した日付が範囲内かどうか
	 * @param date 日付
	 * @return　判定結果
	 */
	private boolean dateIsOutOfRange(LocalDate date) {
 		
		// 未入力（クリア）を許可する場合
		if(date == null) { return false; }
		
		boolean isBeforeLower = true;
        boolean isAfterUpper = true;
		
		// 下限(lower)より前、または上限(upper)より後の日付を無効化
        isBeforeLower = (lowerDate != null && date.isBefore(lowerDate));
        isAfterUpper = (upperDate != null && date.isAfter(upperDate));		
		
		return (isBeforeLower || isAfterUpper);
	}
	
    /**
     * モデルへの値反映
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param date
     */
    private void bindingModelProperty(String colId, String value, LocalDate date) {
        // 二重実行防止
        if (isAdjusting) return;
        
        isAdjusting = true;
        try {
         	// 値の確定 VALUEのBIND
			// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
			super.setRowClassProperty(colId, String.class, value);
         
            // 連動項目のBIND設定
            syncModelPropertyBindingEvent(null, date);

        } catch (Exception ex) {
        	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
        	LogManager.writeError("モデルへの値反映に失敗しました: " + colId);
            throw ex;
 
        } finally {
            isAdjusting = false;
        }
    } 	
	
    /**
     * 値の更新(確定)に連動する外部イベント設定
     * @param befValue 変更前の値
     * @param newValue 確定した値
	 * @brief 行データ(Model)の他の項目(Property)を連動して変更する場合などの用いる。<br>
	 * 当該クラスを継承した、子クラスにて内容を定義する.
     */
    protected void syncModelPropertyBindingEvent(LocalDate befValue, LocalDate newValue) {
    	return;
    }
}
