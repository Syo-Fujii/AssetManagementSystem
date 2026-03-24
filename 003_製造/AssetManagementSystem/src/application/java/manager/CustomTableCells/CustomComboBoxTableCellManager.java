package application.java.manager.CustomTableCells;

import java.util.Objects;

import application.java.base.BaseTableViewModel;
import application.java.manager.LogManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * カスタムControl：TableCell + ComboBox
 * @param <S> (Table Source / Subject): 行データのクラス名。
 * @param <T> (Column Type): セルに表示する項目の型。
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomComboBoxTableCellManager<S extends BaseTableViewModel, T> extends TableCellManager<S, T> 
{
    private final ComboBox<T> comboBox;
    private final Boolean isAlwaysShow;

    private Boolean isAdjusting = false;

 	/**
	 * プレースホルダー（入力例・ヒント）を設定する。
	 * @param promptText 設定する文字
	 */
	public void setPlaceHolder(String promptText) {
		this.comboBox.setPromptText(promptText);
	}	
  
	/**
	 * 選択肢（リスト）を設定する。
	 * @param items 設定する選択肢（リスト）: ObservableList[T]
	 */
	public void setDataSource(ObservableList<T> items) {
		this.comboBox.setItems(items);
	}		
	
	/**
	 * 選択肢（リスト）を追加する。
	 * @param item 追加するデータ：T
	 */
	public void setData(T item) {
		this.comboBox.getItems().add(item);
	}	    
    
    
    /**
     * コンストラクタ
     * @param items ComboBoxの選択リスト
     * @param isAlwaysShow ComboBoxを常時表示するか
     */
    @SuppressWarnings({ "unused"})
	public CustomComboBoxTableCellManager(String colId, ObservableList<T> items, Boolean isAlwaysShow) {
    	this.isAlwaysShow = isAlwaysShow;

    	// items が null なら空のリストを入れる
        this.comboBox = new ComboBox<>(items != null ? items : FXCollections.observableArrayList());
        
        // 編集可能（オートコンプリート用）なComboBoxを準備
        createCustomComboBox(colId);

        if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // ComboBoxがフォーカスを得た＝ユーザーが操作しようとしている
        this.comboBox.focusedProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (newVal) {
        				LogManager.writeTrace("[CustomComboBox][focusedProperty] Start");
        				
        				// 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
        				getTableView().edit(getIndex(), getTableColumn());
        			}});
    }
 
    /**
     * セルが入力(編集)モードに移行する際に内部で呼び出されるメソッド(Enter/GotFocus Event)
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
    	
    	LogManager.writeTrace("[CustomComboBox][startEdit] Start");
    	
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
    	
    	LogManager.writeTrace("[CustomComboBox][cancelEdit] Start");
    	
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
     * セルを描画・更新する際に内部で呼び出されるメソッド(ValueChanged Event)
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時<br>
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時<br>
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時<br>
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時<br>
     */
    @Override
    protected void updateItem(T item, boolean empty) {
    	
    	super.updateItem(item, empty);
  
      	LogManager.writeTrace("[CustomComboBox][updateItem] Start");
    	LogManager.writeTrace("value :["+ item + "] empty :[" + empty + "] text: [" + getText() + "]");
    	
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty || item == null) {
            setGraphic(null);
            // TableCell.SetText
            setText(null);
        
        } else {
    		// 表示する前に、現在のデータモデルの値をセットする(初期値の設定)
            if (!Objects.equals(item.toString(), this.comboBox.getEditor().getText()) &&
            		getIndex() >= 0 && 
            		!this.comboBox.getItems().isEmpty() ) {
            	Platform.runLater(() -> {
            		isAdjusting = true;
            		try {
                        this.comboBox.getEditor().setText(item.toString());
                    } finally {
                        isAdjusting = false;
                    }
                });
            }	

        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */
        		setGraphic(this.comboBox);
                setText(null);
        	} else {
                /* 編集時のみ表示モード */
        		if (isEditing()) {
                    setGraphic(this.comboBox);
                    setText(null);
                } else {
                    setGraphic(null);
                    // TableCell.SetText
                    setText(item != null ? item.toString() : null);
                }
            }
        }
    	LogManager.writeTrace("[CustomComboBox][updateItem] End");   
    }
 
    /**
     * カスタムコンボボックス生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     */
    private void createCustomComboBox(String colId) {
  
    	// ComboBoxの配置(Cellの中央)
    	this.setAlignment(Pos.CENTER);

    	// ComboBoxのサイズ(Cellに合わせる)
    	comboBox.prefWidthProperty().bind(this.widthProperty().subtract(5.0));
    	comboBox.setMaxWidth(Control.USE_PREF_SIZE);    	
    	
    	// オートコンプリート機能付与
        setupAutoComplete(colId);

        // 選択リスト確定機能追加
        onSelectedItemComboBox(colId);        
    }
    
	/**
	 * 機能追加：入力Mode + AutoComplete機能
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
	 */
    @SuppressWarnings({ "unused", "unchecked" })
    private void setupAutoComplete(String colId) {
    	  
        // オートコンプリートを有効にするため「編集可能」にする
        this.comboBox.setEditable(true);
        
     	TextField editor = this.comboBox.getEditor();

     	// 入力監視リスナー
     	// (obs = 値変更を監視しているプロパティ:ObservableValue = editor.textProperty())
     	editor.textProperty().addListener(
     			(obs, oldText, newText) -> {
     			    // 追加：リストが空なら何もしない
     			    if (this.comboBox.getItems() == null || this.comboBox.getItems().isEmpty()) { return; }	
     				if (newText == null || newText.isEmpty() || newText.length() < oldText.length()) {
     					return; // 削除時は補完しない
     				}
					  
     				// 前方一致する最初の候補を探す(入力文字と最初が一致するリストの値)
     				String match = (String)this.comboBox.
     						getItems().
     						stream().
     						filter(i ->  i.toString().toLowerCase().startsWith(newText.toLowerCase())).
     						findFirst().
     						orElse(null);
					  
     				if (match != null) {
     					Platform.runLater( () -> 
     					{
     						isAdjusting = true;
     						try {
     							int caretPos = newText.length();
     							int matchLen = match.length();
										  
     							editor.setText(match); // 補完文字をセット

     							if (caretPos <= matchLen) {
     								editor.selectRange(caretPos, matchLen); // 補完部分をハイライト
     							} else {
     								// もし入力文字の方が長い場合は、とりあえず末尾にカーソルを置く
     								editor.positionCaret(matchLen);
     							}

     							/*// 値入力後、選択リストを表示する
     							if (!this.comboBox.isShowing() || 
   									!this.comboBox.getItems().isEmpty()) {
     								LogManager.writeTrace("[CustomComboBox][textProperty] comboBox.show");
     								// リストを表示
     								this.comboBox.show();
     							}*/
     						} finally { 
     							isAdjusting = false;
     						}
     					});
     				}
     			});
     	
        // オートコンプリートに伴う、値確定時の処理
        // ⇒ 全ての文字列が入力されてからCommitする
        this.comboBox.setOnAction(
        	  ( e ) -> 
        	  {
        		  LogManager.writeTrace("[CustomComboBox][setOnAction] Start");
        
        		  if( isAdjusting || isEmpty() || getIndex() < 0 ) { return; }
        			
        		  // エディタに入力されている文字列を取得
        		  String editValue = this.comboBox.getEditor().getText();

        		  // 値の確定
        		  this.commitEdit((T) editValue);
    				
        		  // モデルへの値反映
        		  bindingModelProperty(colId, null, editValue); 
        	  });
    }

    /**
     * 機能追加：コンボボックス選択確定動作(SelectedValue Event)
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
	 * @brief 選択したItemをカラムのBINDソース<S>に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提
     */
    @SuppressWarnings({ "unused" })
	private void onSelectedItemComboBox(String colId) {
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (newVal == null || isAdjusting) { return; }

        			LogManager.writeTrace("[CustomComboBox][selectedItemProperty] Start");
    			    Platform.runLater(() -> {
                		isAdjusting = true;
                		try {
                			if (!isEditing()) { getTableView().edit(getIndex(), getTableColumn()); }               			
                			
            				// 値の確定(TableView側へ通知)
                			this.commitEdit(newVal);
                			
            				// モデルへの値反映
            				bindingModelProperty(colId, oldVal.toString(), newVal.toString());
                        } finally {
                            isAdjusting = false;
                        }
    			    });          			
        		});
    }

    /**
     * モデルへの値反映
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param oldVal 変更前の入力値
     * @param newVal 入力値
     */
    @SuppressWarnings("unchecked")
	private void bindingModelProperty(String colId,String oldVal,String newVal) {
        // 二重実行防止
        if (isAdjusting) return;
        
        isAdjusting = true;
        try {
        	// 値の確定 VALUEのBIND
			// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
			super.setRowClassProperty(colId, String.class, newVal);
        
            // 連動項目のBIND設定
            syncModelPropertyBindingEvent((T)oldVal, (T)newVal);

        } catch (Exception ex) {
        	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
        	LogManager.writeError("モデルへの値反映に失敗しました: " + colId);
            throw ex;
 
        } finally {
            isAdjusting = false;
        }
    }
    
    /**
     * 値の更新(確定)に連動する外部イベント設定
     * @param befValue 変更前の値
     * @param newValue 確定した値
	 * @brief 行データ(Model)の他の項目(Property)を連動して変更する場合などの用いる。<br>
	 * 当該クラスを継承した、子クラスにて内容を定義する.
     */
    protected void syncModelPropertyBindingEvent(T befValue, T newValue) {
    	return;
    }
}
