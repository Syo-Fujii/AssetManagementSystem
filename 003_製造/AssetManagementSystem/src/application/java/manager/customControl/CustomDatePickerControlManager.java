package application.java.manager.customControl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.java.common.AppUtil;
import application.java.manager.LogManager;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * カスタムControl：DatePicker
 * @brief 入力制限・日付初期/最小/最大値・最大文字数を追加したカスタムDatePicker<br>
 */
public class CustomDatePickerControlManager extends DatePicker {

	private  LocalDate defaultDate;
    private  LocalDate lowerDate;
    private  LocalDate upperDate;
    
    private String format = "yyyy/MM/dd";
    private String regex = "^[0-9]{0,4}/?[0-9]{0,2}/?[0-9]{0,2}$";
    private Integer maxLength = 10;
	
    private Boolean isAdjusting = false;
 
    
    /**
     * 日付の初期値 取得
	 * @return defaultDate
	 */
	public LocalDate getDefaultDate() {
		return defaultDate;
	}

	/**
	* 日付の初期値 設定
	 * @param defaultDate セットする defaultDate
	 */
	public void setDefaultDate(LocalDate defaultDate) {
		this.defaultDate = defaultDate;
	}
	
	/**
	 * 日付の最小値 取得
	 * @return lowerDate
	 */
	public LocalDate getLowerDate() {
		return lowerDate;
	}

	/**
	 * 日付の最小値 設定
	 * @param lowerDate セットする lowerDate
	 */
	public void setLowerDate(LocalDate lowerDate) {
		this.lowerDate = lowerDate;
	}	
	
	/**
	 * 日付の最大値 取得
	 * @return upeerDate
	 */
	public LocalDate getUpperDate() {
		return upperDate;
	}

	/**
	 * 日付の最大値 設定
	 * @param upeerDate セットする upeerDate
	 */
	public void setUpperDate(LocalDate upeerDate) {
		this.upperDate = upeerDate;
	}    

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
	 * 日付の有効範囲 設定
	 * @param min 日付の最小値
	 * @param max 日付の最大値
	 */	
    public void setDateRange(LocalDate min, LocalDate max) {
        this.lowerDate = min;
        this.upperDate = max;
       
        this.onCalenderLimitDateRange();
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

    /**
     * [Enter]Keyを次に遷移とするか
     * @param isEnabled 遷移判定
     * @brief [真]とした場合、[Enter]Key押下で下のControlへ遷移(TAB)。<br>
     * [Enter] + SHIFT Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
    public void isKeyPressToNext(Boolean isEnabled) {
    	if (isEnabled) {
    		onEnterKeyNextFocus();
    		return;
    	}
    	
    	this.setOnKeyPressed(null);
    }    

    
    /**
     * FXML用デフォルトコンストラクタ
     */
    public CustomDatePickerControlManager() {
        super();
        this.lowerDate = null;
        this.upperDate = null;
        this.defaultDate = LocalDate.now();
        
        // コンストラクタで初期化メソッドを呼ぶ
        createCustomDatePickerBox();
    }    
    /**
     * コンストラクタ
	 * @param calShowFirstDate LocalDate 日付の初期値
	 * @param minDate LocalDate 許容される日付の最小値
	 * @param maxDate LocalDate 許容される日付の最大値
	 */
	public CustomDatePickerControlManager(
			LocalDate showFirstDate,
			LocalDate minDate,
			LocalDate maxDate) 
	{
		super();
		
		this.lowerDate = minDate;
		this.upperDate = maxDate;
		
		if (showFirstDate == null) {
			this.defaultDate = LocalDate.now();
		} else {
			this.defaultDate = showFirstDate;
		};
		
		createCustomDatePickerBox();
	}	

    /**
     * 機能追加：DatePicker選択確定動作(Leave Event)
	 * @brief 
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
	@SuppressWarnings({ "unused"})
	private void onLeaveDatePickerValue() {

		// 入力監視リスナー(Leave相当)
		// カレンダーの選択に対応するため[setOnAction]を捉える
		this.setOnAction(
        		(e) -> {
        			if ( isAdjusting ) { return; }
         			
        			LogManager.writeTrace("[CustomDatePicker][setOnAction] Start");
         			
        			LocalDate date = this.getValue();	
        			String value = ( date != null ) ? 
         			       date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) : "" ;
        			
        			// 入力制御      	
        			// 範囲外なら強制的に nullに戻す
        			if (dateIsOutOfRange(date)) {
        				LogManager.writeError("TableColumn : 選択範囲外の日付です: " + date);        
        	            date = null;
        	            value = "";
        			}

        	        try {
        	            isAdjusting = true; // フラグを立てる
        	            
            			this.getEditor().setText(value);
            			this.setValue(date);
        	            
        	        } finally {
        	            isAdjusting = false; // 必ずフラグを下ろす
        	        }        			
         		});
	}	
	
	/**
	 * 機能追加：カレンダー表示 選択可能な日付の範囲を制限
	 * @param lower 下限日（これより前は選択不可）
	 * @param upper 上限日（これより後は選択不可）
	 */
	@SuppressWarnings("unused")
	public void onCalenderLimitDateRange() {
	    this.setDayCellFactory(picker -> new DateCell() {
	        
	    	@Override
	        public void updateItem(LocalDate date, boolean empty) {
	            super.updateItem(date, empty);

	            if (date == null) { return; }

	            // 下限(lower)より前、または上限(upper)より後の日付を無効化
	            if (dateIsOutOfRange(date)) {
	                setDisable(true);
	                // 無効な日付をグレーアウトする
	                setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: #b0b0b0;");
	            } else {
	                setStyle(""); 
	            }
	    	}
	    });
	}
	
	/**
	 * 機能追加：カレンダー表示 初期日付設定
	 */
	@SuppressWarnings("unused")
	private void onCalenderShown() {
	    // カレンダーが開かれようとした時の処理を登録
	    this.setOnShowing(e -> {
	    	Platform.runLater(() -> {
	    		this.requestFocus(); 

	    		// 現在の値が空（null）の場合だけ、カレンダーの初期選択を今日にする
	    		if (this.getValue() == null) 
	    		{
	    			LogManager.writeTrace("[CustomDatePicker][CalenderShown] 初期値設定");
	    			
	    			// セットにより[valueProperty().addListener]が発火するため[Adjusting]しない
	    			this.setValue(defaultDate);
	    		}
	    	});
	    });
	}	

	/**
	 * 正規表現　最大文字数を用いた入力制限
	 */
	private void setupTextValidation() {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		
		// コンバーターの設定
		this.setConverter(new StringConverter<LocalDate>() {
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
		this.getEditor().setTextFormatter(new TextFormatter<>( change -> 
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
	 * カスタムDatePicker生成
	 */
	private void createCustomDatePickerBox() 
	{
	    // DatePicker における IME（入力メソッド）関連の挙動を無効化、あるいは制御する
	    this.setInputMethodRequests(new InputMethodRequests() {
	        @Override public Point2D getTextLocation(int offset) { return new Point2D(0, 0); }
	        @Override public int getLocationOffset(int x, int y) { return 0; }
	        @Override public void cancelLatestCommittedText() {}
	        @Override public String getSelectedText() { return ""; }
	    }); 
		
	    // テキスト入力制限 追加
	    setupTextValidation();
	    
		// チェック確定機能追加
		onLeaveDatePickerValue();

		// カレンダー表示設定
		onCalenderShown();
		onCalenderLimitDateRange();
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
     * Key押下で次のControlに遷移する(TAB押下EVENTと同等)
     * @brief [Enter]Key押下で下のControlへ遷移(TAB)。<br>
     *         [Enter] + SHIFT Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
	private void onEnterKeyNextFocus() {
		this.setOnKeyPressed(
				event -> 
				{
					Boolean isMoveUp = event.getCode() == KeyCode.ENTER && event.isShiftDown();
					
					if (event.getCode() == KeyCode.ENTER || isMoveUp)
					{
						this.fireEvent(
				        		new KeyEvent(
				        				KeyEvent.KEY_PRESSED, 
				        				"", 
				        				"", 
				        				KeyCode.TAB,
				        				isMoveUp, 
				        				false, 
				        				false, 
				        				false)
				        		);
				            
				        event.consume(); 
					}
				});
	}	
}
