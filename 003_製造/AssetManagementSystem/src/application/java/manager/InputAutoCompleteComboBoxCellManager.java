package application.java.manager;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;

/**
 * カスタムControl(TableView ConboBoxCell)
 * @param S 継承元が[BaseTableViewModel]のデータクラス(1行データのクラス)
 * @param T 
 * @brief ConboBoxを継承したカスタムControl。<br>
 * データSOURCEは、継承元が[BaseTableViewModel]のデータクラスとする。<br>
 * ※ データクラス(1行)の配列(List)をデータとする。<br>
 * ScreenBuilderでは、継承元に関する各機能は実行できない。(コンテナ化されて編集できない)<br>
 * 同一画面内で複数のTableViewが配置される場合を考慮し、各動作(Event)をCallBackにて設定する。<br>
 *  ⇒  各動作に対するEventは配置したController(画面クラス)にて定義する。
 * @note 
 * [javafx.scene.control.TableView]に各イベントを追加したカスタムクラス。<br>
 * 配置された画面の他Controlとの受け渡しは、配置したConntrollerクラス内のメソッドで行う。<br>
 * 各イベントの動作内容(メソッド)は、配置したConntrollerクラス内に定義し、CallBackにて実装する。
 */
public class InputAutoCompleteComboBoxCellManager<S ,T> extends ComboBoxTableCell<S, T> {
	  private ComboBox<T> comboBox;
		
	  /** コンストラクタ */
	  public InputAutoCompleteComboBoxCellManager(ObservableList<T> items) {
	        super(items);
	        
	        /* ComboBoxTableCell が標準で「非編集モード」としてコンボボックスを生成している為
	           コンストラクタで[編集モード]に指定する*/
	        this.setComboBoxEditable(true); 
	    }


	  /**
	   * Focus遷移処理
	   */
	  @SuppressWarnings({ "unused", "unchecked" })
	  @Override
	  public void startEdit() {
		  super.startEdit();
	      
		  // セルが編集状態になったときにComboBoxを取得
		  if (getGraphic() instanceof ComboBox) {
			  
			  comboBox = (ComboBox<T>) getGraphic();
			  // 入力可能にする
			  // comboBox.setEditable(true);

			  // オートコンプリートのロジック
			  TextField editor = comboBox.getEditor();
			  // 入力監視リスナー
			  // (obs = 値変更を監視しているプロパティ:ObservableValue = editor.textProperty())
			  editor.textProperty().addListener(
					  (obs, oldValue, newValue) -> {
						  if (newValue == null || 
							  newValue.isEmpty() || 
							  newValue.length() < oldValue.length()) {
			                    return; // 削除時は補完しない
			                }
						  
	                    // 前方一致する最初の候補を探す(入力文字と最初が一致するリストの値)
	                    String match = (String)this.getItems().stream()
	                            .filter(i ->  i.toString().toLowerCase().startsWith(newValue.toLowerCase()))
	                            .findFirst().orElse(null);
	                    
	                    if (match != null) {
	                        Platform.runLater(
	                        		() -> {
	                        			int caretPos = newValue.length();
	                        			editor.setText(match); // 補完文字をセット
	                        			editor.selectRange(caretPos, match.length()); // 補完部分をハイライト
	                            		
	                        			if (!comboBox.isShowing()) {
	                        				comboBox.show(); // リストを表示
	                        				}});}
	                    });
			  }}
}
