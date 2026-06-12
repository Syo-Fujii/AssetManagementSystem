package application.java.manager.customControl;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class CustomTextAreaManager extends TextArea {
    
	private Node nextControl;
	
	
    // 外部から「次の移動先」を指定するメソッド
    public void setNextControl(Node control) {
        this.nextControl = control;
    }	
	
	/**
     * [Enter]Keyを次に遷移とするか
     * @param isEnabled 遷移判定
     * @brief [真]とした場合、[DOWN]Key押下で下のControlへ遷移(TAB)。<br>
     *         [Enter] + SHIFT・[UP]Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
    public void isKeyPressToNext(Boolean isEnabled) {
    	if (isEnabled) {
    		onEnterKeyNextFocus();
    		return;
    	}
    	
    	this.setOnKeyPressed(null);
    }    
	
    /**
     * Key押下で次のControlに遷移する(TAB押下EVENTと同等)
     * @brief [DOWN]Key押下で下のControlへ遷移(TAB)。<br>
     *         [Enter] + SHIFT・[UP]Key押下で上のControlへ遷移(Shift + TAB)。<br>
     */
	private void onEnterKeyNextFocus() {
	    this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
	        
	        // 【判定】Shift + Enter が押されたか
	        boolean isShiftEnter = (event.getCode() == KeyCode.ENTER && event.isShiftDown());
	        
	        if (isShiftEnter && nextControl != null) {
	        	nextControl.requestFocus(); // 直接指定した先にジャンプ

	            // TextArea 本来の挙動（改行など）をキャンセル
	            event.consume(); 
	        }
	    });
	}	
}