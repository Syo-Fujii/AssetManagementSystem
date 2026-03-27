package application.java.manager.customControl;

import application.java.common.AppUtil;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

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
}
