package application.java.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

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
		
    	// 1. ダイアログのサイズ変更を一時的に許可する
    	alert.setResizable(true);

    	// 2. メッセージを表示するLabelを取得し、折り返しを無効にする
    	// (文字数に応じて横に伸ばすため)
    	alert.getDialogPane().lookup(".content.label").setStyle("-fx-wrap-text: false;");    	
    	
    	alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);    	
    	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    	
    	
    	alert.getDialogPane().getScene().getWindow().setOnShowing(ev -> {
    	    // 内部のラベルを探して折り返しを禁止する
    	    Node label = alert.getDialogPane().lookup(".content.label");
    	    if (label != null) {
    	        label.setStyle("-fx-wrap-text: false;");
    	    }
    	});
    	
    	alert.getDialogPane().applyCss();
    	alert.getDialogPane().layout();

    	// 4. 計算された「推奨サイズ」をウィンドウに反映
    	double prefWidth = alert.getDialogPane().getPrefWidth();
    	alert.getDialogPane().setMinWidth(prefWidth); // 最小幅を推奨幅に固定

    	// 5. それでも足りない場合や、特定の文字数を超えるなら三項演算子で補正
    	alert.getDialogPane().setPrefWidth(message.length() > 30 ? 600 : Region.USE_COMPUTED_SIZE);

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
		
    	alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    	
    	Optional<ButtonType> result =  alert.showAndWait();
	}	

	/**
	 * 例外(ERROR)MessageBox
	 * @param headerText 概要(タイトル)　※ 不要な場合はNULLを指定
	 * @param message メッセージ内容
     * @brief 例外(ERROR)メッセージを表示する。
	 */
	@SuppressWarnings("unused")
	public static void ShowErrorMessage(
			String title,
			String headerText,
			String message){
		
    	Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
    	alert.setHeaderText(headerText);
        alert.setContentText(message);
		
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        
    	Optional<ButtonType> result =  alert.showAndWait();
	}

	/**
	 * 例外(ERROR)MessageBox
	 * @param title String ウィンドウタイトル
	 * @param headerText String 概要(タイトル)　※ 不要な場合はNULLを指定
	 * @param t Throwable 例外情報(Exceptionを含む)
	 * @brief Exceptionを受け取り、スタックトレースを[詳細]にて表示する。<br>
	 * 内容は、Exception Messageを表示する。
	 */
	public static void ShowErrorMessage(String title, String headerText, Throwable t) {
	    
		Alert alert = new Alert(Alert.AlertType.ERROR);
	    alert.setTitle(title);
	    alert.setHeaderText(headerText);

	    // メッセージが空なら例外クラス名を表示する
	    String message = 
	    		(t != null && t.getMessage() != null) ? t.getMessage() : "予期せぬエラーが発生しました。";
	    alert.setContentText(message);	    
	    
	    // スタックトレースを表示
	    if (t != null) {
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);

	        // 例外のスタックトレースを文字列に変換	        
	        t.printStackTrace(pw);
	        String exceptionText = sw.toString();

	        // テキストエリアの生成
	        TextArea textArea = new TextArea(exceptionText);
	        textArea.setEditable(false);
	        textArea.setWrapText(true);

	        textArea.setMaxWidth(Double.MAX_VALUE);
	        textArea.setMaxHeight(Double.MAX_VALUE);
	        GridPane.setVgrow(textArea, Priority.ALWAYS);
	        GridPane.setHgrow(textArea, Priority.ALWAYS);

	        GridPane expContent = new GridPane();
	        expContent.setMaxWidth(Double.MAX_VALUE);
	        expContent.add(new Label("詳細なエラー情報:"), 0, 0);
	        expContent.add(textArea, 0, 1);

	        // 詳細エリア」としてセット
	        alert.getDialogPane().setExpandableContent(expContent);
	    }

	    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
	    alert.showAndWait();
	}
	
	/**
	 *DB例外(ERROR)MessageBox
	 * @param headerText String 概要(タイトル) ※ 不要な場合はNULLを指定
	 * @param message メッセージ内容
     * @brief 例外(ERROR)メッセージを表示する。
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
		
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        
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
