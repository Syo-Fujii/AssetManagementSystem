package application.java.common;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class MessageBox {

    public enum ShowButtonType {
        YES_NO,
        OK_CANCEL,
    }
	
	private static ButtonType btnYes = new ButtonType("はい", ButtonData.OK_DONE);
	private static ButtonType btnNo = new ButtonType("いいえ", ButtonData.CANCEL_CLOSE);
	private static ButtonType buttonTypeOk = ButtonType.OK;
	private static ButtonType buttonTypeCancel = ButtonType.CANCEL;

	
	public static Optional<ButtonType> Show(
			Alert.AlertType AlertType,
			ShowButtonType buttonType,
			String title,
			String headerText,
			String message){
		
    	Alert alert = new Alert(AlertType);
    	
    	setButton(alert, buttonType);
    	
    	alert.setTitle(title);
    	alert.setHeaderText(headerText);
    	alert.setContentText(message);
		
		return  alert.showAndWait();
	}
	
	
	
	private static void setButton(Alert msgBox, ShowButtonType buttonType)
	{
		switch (buttonType) {
	    case YES_NO:
	    	msgBox.getButtonTypes().setAll(btnYes, btnNo);
	    	Button noButton = (Button)msgBox.getDialogPane().lookupButton(btnNo);
	    	noButton.setDefaultButton(true);

	        break;
	    case OK_CANCEL:
	        break;
	        }
	}
}
