package application.java.manager.customTableCells;

import java.lang.reflect.Method;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

/**
 * カスタムComboBoxクラス
 * @param <S> 継承元が[BaseTableViewModel]のデータクラス(1行データのクラス)
 * @param <T> 入力する値の型
 * @brief
 * コンボBox型のTableCellの設定
 *　・[isAlwaysShow] が真の場合、常にComboBoxを表示する
 */
public class CustomComboBoxTableCellManager<S, T> extends TableCell<S, T> {
    private final ComboBox<T> comboBox;

    private Boolean isAlwaysShow = false;
    private Boolean isAdjusting = false;    
    
    /**
     * コンストラクタ
     * @param items 設定する選択リスト
     */
    @SuppressWarnings({ "unused"})
	public CustomComboBoxTableCellManager(String colId, ObservableList<T> items) {

    	this.comboBox = new ComboBox<T>(items);
        
        // 編集可能（オートコンプリート用）なComboBoxを準備
        createCustomComboBox();

        if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // ComboBoxがフォーカスを得た＝ユーザーが操作しようとしている
        this.comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
                getTableView().edit(getIndex(), getTableColumn());
            }
        });      
        
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (isEditing()) {
        				// 編集モード(値の確定)
        				commitEdit(newVal);
        			} else {
        				// 非編集モード(値の確定)
        			   
        				S rowData = getTableView().getItems().get(getIndex());
        			   
        			   // ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
        			   
         	    	   // モデルへの値反映
        	    	    try {
        	    	        // メソッド(値のSetter プロパティ)名の生成
        	    	        String methodName = "set" + colId.substring(0, 1).toUpperCase() + colId.substring(1);
        	    	        
        	                // モデルからメソッドを探して実行
        	                Method setter = rowData.getClass().getMethod(methodName, newVal.getClass());
        	                setter.invoke(rowData, newVal);
        	    	    } catch (Exception e) {
        	    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
        	                System.err.println("モデルへの値反映に失敗しました: " + colId);
        	    	    	
        	    	    	e.printStackTrace();
        	    	    }
        			}});}
     public CustomComboBoxTableCellManager(String colId, ObservableList<T> items, Boolean isAlwaysShow) {
    	this.isAlwaysShow = isAlwaysShow;
    	this(colId, items);
        }
 
    /**
     * セルが入力(編集)モードに移行する際に内部で呼び出されるメソッド
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・ユーザーがダブルクリックや [F2]・[Enter]入力など（プラットフォームに依存）を行った瞬間<br>
	 *  ※ 下記の条件の場合のみ<br>
	 *      ・TableView が editable(true)<br>
	 *      ・TableColumn が editable(true)<br>
 	 *      ・当該セル自体が editable<br>
     */
    @Override
    public void startEdit() {
    	super.startEdit();
    	
    	// System.out.println("CustomCell startEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(comboBox);
        	setText(null);
        } else if (!isEditing()) {
            return;
        }

    	comboBox.requestFocus();
    	comboBox.getEditor().requestFocus();
    }

    /**
     * 入力(編集)モード中にキャンセルする際に内部で呼び出されるメソッド
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・[ESC]入力など（プラットフォームに依存）を行った瞬間<br>
	 *  ※ 下記の条件の場合のみ<br>
	 *      ・TableView が editable(true)<br>
	 *      ・TableColumn が editable(true)<br>
 	 *      ・当該セル自体が editable<br>
 	 *  [commitEdit + フォーカス移動]<br>
     */
    @Override
    public void cancelEdit() {
    	super.cancelEdit();
    	
        // System.out.println("CustomCell cancelEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
    		// 非編集モードへ遷移
            setGraphic(null);
            setText(getItem() != null ? getItem().toString() : null);
            
            return;
        }
    	
    	/* 常時表示モード */
    	setGraphic(this.comboBox); 
    } 
    
    /**
     * セルを描画・更新する際に内部で呼び出されるメソッド
	 * @brief 主に以下のタイミングで実行
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        
    	super.updateItem(item, empty);

    	// System.out.println("CustomCell updateItem");
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */
                
            	//自動入力による重複処理の制御
            	isAdjusting = true;
                this.comboBox.setValue(item);
                isAdjusting = false;

                setGraphic(this.comboBox);
                setText(null);

        	} else {
                /* 編集時のみ表示モード */
        		if (isEditing()) {
                    setGraphic(this.comboBox);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item != null ? item.toString() : null);
                }
            }      	
        }
    }
 
    /**
     * カスタムコンボボックス生成
     */
    private void createCustomComboBox() {
        // オートコンプリート機能付与
        setupAutoComplete();
        }    
    
	/**
	 * 機能追加：入力Mode + AutoComplete機能
	 */
    @SuppressWarnings("unused")
    private void setupAutoComplete() {
    	  
        // オートコンプリートを有効にするため「編集可能」にする
        this.comboBox.setEditable(true);
        
     	TextField editor = this.comboBox.getEditor();

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
					  String match = (String)this.comboBox.getItems().stream()
							  .filter(i ->  i.toString().toLowerCase().startsWith(newValue.toLowerCase()))
							  .findFirst().orElse(null);
                  
					  if (match != null) {
						  Platform.runLater(
								  () -> {
									  int caretPos = newValue.length();
									  
									  editor.setText(match); // 補完文字をセット
									  editor.selectRange(caretPos, match.length()); // 補完部分をハイライト
                      			
									  if (!this.comboBox.isShowing()) {
										  this.comboBox.show(); // リストを表示
										  }});}
					  });
     	
        // オートコンプリートに伴う、値確定時の処理
        // ⇒ 全ての文字列が入力されてからCommitする
        this.comboBox.setOnAction(
        		e -> {
        			if (!isAdjusting && isEditing()) { 
        				System.out.println("CustomCell setOnAction");
   
        				// 値確定処理
        				commitEdit(comboBox.getValue());
        				}
        			});}

}
