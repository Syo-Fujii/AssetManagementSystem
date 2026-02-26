package application.java.manager;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 
 * @param <S>
 * @param <T>
 */
public class CustomComboBoxTableCellManager<S, T> extends TableCell<S, T> {
    private final ComboBox<T> comboBox;
    
    private Boolean isAlwaysShow = true;
    private boolean isAdjusting = false;    
    
    
    /**
     * コンストラクタ
     * @param items 設定する選択リスト
     */
    public CustomComboBoxTableCellManager(ObservableList<T> items) {

    	this.comboBox = new ComboBox<>(items);
        
        // オートコンプリートを有効にするため「編集可能」にする
        this.comboBox.setEditable(true);
        
        // 編集可能（オートコンプリート用）なComboBoxを準備
        createComboBox();
        
        // オートコンプリート(newValue監視)
        /*setupAutoComplete(comboBox);*/
 
     // ComboBoxのエディタ（TextField）に対してイベントフィルターを追加
        comboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // 1. プログラムによる修正中（isAdjusting）なら無視
                if (isAdjusting) return;

                // 2. 現在の入力値を確定させる
                T newValue = comboBox.getConverter().fromString(comboBox.getEditor().getText());
                comboBox.setValue(newValue);
                
                // 3. TableCellに対して「編集完了」を通知
                commitEdit(newValue);
                
                // 4. (オプション) 次の行へ移動したい場合
                // getTableView().getSelectionModel().selectNext();
                
                event.consume(); // ComboBox標準の挙動を止める
            }
        });
        
        comboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // 1. 現在の入力内容を確定
                commitEdit(comboBox.getValue());

                // 2. 次のセルへフォーカスを移動
                TableColumn<S, ?> nextColumn = getNextColumn();
                if (nextColumn != null) {
                    getTableView().edit(getTableRow().getIndex(), nextColumn);
                } else {
                    // 最後の列なら、次の行の最初の列へ（必要に応じて）
                    int nextRow = getTableRow().getIndex() + 1;
                    if (nextRow < getTableView().getItems().size()) {
                        getTableView().edit(nextRow, getTableView().getColumns().get(0));
                    }
                }
                event.consume(); // 標準のEnter挙動を無効化
            }
        });
        
        
        
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (isEditing()) {
        				commitEdit(newVal);
        			} else {
        			   // 非編集状態でもモデルを更新したい場合
        			   S rowData = getTableView().getItems().get(getIndex());
        			   
        			   // ここでリフレクションやインターフェース経由で値をセットする処理が必要
        			}});     
        
        
        
        if (!this.isAlwaysShow)
        {
        	return;
        }
        
        /*
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (isEditing()) {
        				commitEdit(newVal);
        			} else {
        			   // 非編集状態でもモデルを更新したい場合
        			   S rowData = getTableView().getItems().get(getIndex());
        			   
        			   // ここでリフレクションやインターフェース経由で値をセットする処理が必要
        			}});*/
        }

 // 次の編集可能なカラムを探す補助メソッド
    private TableColumn<S, ?> getNextColumn() {
        List<TableColumn<S, ?>> columns = getTableView().getVisibleLeafColumns();
        int currentIndex = columns.indexOf(getTableColumn());
        for (int i = currentIndex + 1; i < columns.size(); i++) {
            if (columns.get(i).isEditable()) {
                return columns.get(i);
            }
        }
        return null;
    }    
    
    
    @Override
    public void startEdit() {
        if (!isAlwaysShow) {
        	super.startEdit();

        	setGraphic(comboBox);
        	setText(null);
        	
        	comboBox.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        if (!isAlwaysShow) {
            super.cancelEdit();
            
            setGraphic(null);
            setText(getItem() != null ? getItem().toString() : null);
        }
    } 
    
    
    
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            if (isAlwaysShow) {
                // 常時表示モード
                
            	//自動入力による重複処理の制御
            	isAdjusting = true;
                comboBox.setValue(item);
                isAdjusting = false;
                setGraphic(comboBox);

                setText(null);
            } else {
                // 編集時のみ表示モード
                if (isEditing()) {
                    setGraphic(comboBox);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item != null ? item.toString() : null);
                }
            }      	
        }
    }
 
    
    private void createComboBox() {
        
        // オートコンプリートのロジック（前述のnewValue監視）をここに実装
        setupAutoComplete(comboBox);

        // 値確定時の処理
        this.comboBox.setOnAction(e -> {
            if (!isAdjusting) commitEdit(comboBox.getValue());
        });
        
        // Enterキーで次のセルへ移動するロジック（前述）もここに追加
    }    
    
    
    
    @SuppressWarnings("unused")
	private void setupAutoComplete(ComboBox<T> cb) {
		
     	TextField editor = cb.getEditor();

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
					  String match = (String)cb.getItems().stream()
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
     	}
    
    private void enterToNextCell() {
    	
    	
    }
}
