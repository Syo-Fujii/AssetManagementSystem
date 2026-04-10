package application.java.manager.customControl;

import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * カスタムControl：ToggleButton
 * @brief Key入力遷移を追加したカスタムToggleButton<br>
 */
public class CustomToggleButtonManager extends ToggleButton {

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
		this.setOnKeyPressed(
				event -> 
				{
					Boolean isMoveUp = event.getCode() == KeyCode.UP || 
							           event.getCode() == KeyCode.ENTER && event.isShiftDown();
					
					if (event.getCode() == KeyCode.DOWN || isMoveUp)
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
