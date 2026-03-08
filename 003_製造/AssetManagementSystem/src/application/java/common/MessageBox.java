package application.java.common;

import java.util.Optional;

import javafx.application.Platform;
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

	
	/**
	 * 選択(CONFIRMATION)MessageBox
	 * @param buttonType ボタンの種類(SET enum ShowButtonType型)
	 * @param title メッセージBOXのタイトル
	 * @param headerText　概要(タイトル)　※ 不要な場合はNULLを指定
	 * @param message メッセージ内容
	 * @return　真偽値([OK]の場合、TRUE, [Cancel]の場合、FALSE)
     * @brief ユーザーに確認（はい/いいえ等）を求める<br>
	 */
	public static Boolean ShowConfirmation(
			ShowButtonType buttonType,
			String title,
			String headerText,
			String message){
		
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	
    	setButton(alert, buttonType);
    	
    	alert.setTitle(title);
    	alert.setHeaderText(headerText);
    	alert.setContentText(message);
		
    	Optional<ButtonType> result =  alert.showAndWait();
		
    	// [OK]に値するボタンの取得 
    	ButtonType resultOkButton = alert.getDialogPane().getButtonTypes().getFirst();
    	
    	if (result.isPresent() && result.get() == resultOkButton) {
    	    // 「OK」が押された時の処理
    	    return true;
    	}
    	
    	return false;
	}

	/**
	 * 警告(WARNING)MessageBox
	 * @param title メッセージBOXのタイトル
	 * @param headerText　概要(タイトル)　※ 不要な場合はNULLを指定
	 * @param message メッセージ内容
     * @brief 注意や警告を促す
	 */
	@SuppressWarnings("unused")
	public static void ShowWarnig(
			String title,
			String headerText,
			String message){
		
    	Alert alert = new Alert(Alert.AlertType.WARNING);
     	
    	alert.setTitle(title);
    	alert.setHeaderText(headerText);
    	alert.setContentText(message);
		
    	Optional<ButtonType> result =  alert.showAndWait();
	}	

	/**
	 *DB例外(ERROR)MessageBox
	 * @param headerText　概要(タイトル)　※ 不要な場合はNULLを指定
	 * @param message メッセージ内容
     * @brief 注意や警告を促す
	 */
	@SuppressWarnings("unused")
	public static void ShowErrorDbException(
			Throwable exception,
			String headerText){
		
    	Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("データベースエラー");
    	alert.setHeaderText(headerText);
        alert.setContentText(exception.getCause() != null ? 
                             exception.getCause().getMessage() : exception.getMessage());
		
    	Optional<ButtonType> result =  alert.showAndWait();
	}	
	
	/**
	 * 表示ボタン設定(選択(CONFIRMATION)MessageBox)
	 * @param msgBox 表示MessageBox
	 * @param buttonType 表示するボタンの種類[ENUM ShowButtonType型]
     * @brief ボタンの種類による表示内容：<br>
     *   ・[YES_NO]<br>
     *       [はい]・[いいえ]ボタン表示<br>
     *       初期Focus⇒[いいえ]ボタン<br>
     *   ・[OK_CANCEL]<br>
     *       デフォルト：[OK]・[Cancel]ボタン表示<br>
    */
	private static void setButton(Alert msgBox, ShowButtonType buttonType)
	{
		switch (buttonType) {
	    case YES_NO:
	    	msgBox.getButtonTypes().setAll(btnYes, btnNo);
	    	Button noButton = (Button)msgBox.getDialogPane().lookupButton(btnNo);
	    	noButton.setDefaultButton(true);

	    	// 【重要】初期フォーカスを「いいえ」に設定する
	    	Platform.runLater(() -> noButton.requestFocus());

	        break;
	    case OK_CANCEL:
	        break;
	        }
	}
}
