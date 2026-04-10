package application.java.manager.customControl;

import java.text.DecimalFormat;
import java.util.function.UnaryOperator;

import application.java.common.AppConst;
import application.java.common.AppUtil;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * カスタムControl：TextField
 * @brief 入力制限・最大文字数設定を追加したカスタムTextField<br>
 */
public class CustomTextFieldControlManager extends TextField  {

    private String regex = "";
    private Integer maxLength = null;
	
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
	 * 値の取得(数値型：LONG)
	 * @return 設定されている値(LONG)
	 */
	public Long getValue() {
        return (Long) this.getTextFormatter().getValue();
    }
	
	/**
	 * テキスト入力の入力制限 設定
	 * @param regexPattern String 正規表現パターン
	 * @param length Integer 最大文字数
	 */
    public void setValidInput(String regexPattern, Integer length) {
   
    	if (!AppUtil.StringIsNullOrWhiteSpace(regexPattern)) {
    		this.setRegex(regexPattern);
    	} 
    	
    	if (length != null) { this.setMaxLength(length); }
    	
    	this.setupTextValidation();
    }       
  
    /**
     * [Enter]Keyを次に遷移とするか
     * @param isEnabled 遷移判定
     * @brief [真]とした場合、[Enter]・[DOWN]Key押下で下のControlへ遷移(TAB)。<br>
     * [UP]Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
    public void isKeyPressToNext(Boolean isEnabled) {
    	if (isEnabled) 
    	{
    		onEnterKeyNextFocus();
    		return;
    	}
    	
    	this.setOnKeyPressed(null);
    }
    
    /**
     * 通貨型のControlとするか
     * @param isEnabled
     * @brief [真]とした場合、値をカンマ区切りの通貨型で表示する。<br>
     */
    public void isCurrency(Boolean isEnabled) {
    	if (isEnabled) 
    	{ 
        	this.regex = AppConst.REGEX_NUMERIC;
        	setupCurrencyControl();

    		return; 
    	}
    	
    	this.setTextFormatter(null);
    }
    
    /**
     * FXML用デフォルトコンストラクタ
     */
    public CustomTextFieldControlManager() {
        super();
        
        // コンストラクタで初期化メソッドを呼ぶ
        this.setupTextValidation();
    }    
    /**
     * コンストラクタ
	 * @param calShowFirstDate LocalDate 日付の初期値
	 * @param regexPattern String 正規表現パターン
	 * @param length Integer 最大文字数
	 */
	public CustomTextFieldControlManager(String regexPattern, Integer length) 
	{
		super();
		
		this.setValidInput(regexPattern, length);
	}		

    /**
     * Key押下で次のControlに遷移する(TAB押下EVENTと同等)
     * @brief [Enter]・[DOWN]Key押下で下のControlへ遷移(TAB)。<br>
     *         [Enter] + SHIFT・[UP]Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
	private void onEnterKeyNextFocus() {
		this.setOnKeyPressed(
				event -> 
				{
					Boolean isMoveUp = event.getCode() == KeyCode.UP || 
							          (event.getCode() == KeyCode.ENTER && event.isShiftDown());
					
					if (event.getCode() == KeyCode.ENTER || 
						event.getCode() == KeyCode.DOWN || 
						isMoveUp)
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
	
	/**
	 * 正規表現　最大文字数を用いた入力制限
	 */
	private void setupTextValidation() {

		// 入力制限
		this.setTextFormatter(new TextFormatter<>( change -> 
		{
			String newText = change.getControlNewText();

		    // 正規表現 & 文字数制限
		    if (!AppUtil.StringIsNullOrWhiteSpace(regex) && !newText.matches(regex)) {
		        return null;
		    }

		    if (maxLength != null && newText.length() > maxLength) {
		        return null;
		    }

		    return change;
		}));
	}
	
	/**
	 * 通貨型Control設定処理
	 */
	private void setupCurrencyControl() {
	    // カンマ区切りへの変換器 (StringConverter)
	    DecimalFormat df = new DecimalFormat("#,###");
	    
	    StringConverter<Long> converter = new StringConverter<>() 
	    {
	    	
	        @Override
	        public String toString(Long value) {
	            return (value == null) ? "" : df.format(value);
	        }
	        
	        @Override
	        public Long fromString(String string) {
	            try {
	                if (string == null || string.isEmpty()) return null;
	                // カンマを除去してからパース
	                return df.parse(string.replace(",", "")).longValue();
	            } catch (Exception e) {
	                return null;
	            }
	        }
	    };	
		
	    // 入力制限 (Filter)
	    UnaryOperator<TextFormatter.Change> filter = change -> {
	        String newText = change.getControlNewText();
	        
	        // カンマを除去した状態で正規表現・文字数チェックを通す（数値としての妥当性チェック）
	        String plainText = newText.replace(",", "");

	        if (!AppUtil.StringIsNullOrWhiteSpace(regex) && !plainText.matches(regex)) {
	            return null;
	        }
	        if (maxLength != null && plainText.length() > maxLength) {
	            return null;
	        }

	        return change;
	    };		
		
	    this.setTextFormatter(new TextFormatter<>(converter, null, filter));		
	}
}
