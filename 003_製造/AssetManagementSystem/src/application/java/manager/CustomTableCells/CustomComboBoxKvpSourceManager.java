   package application.java.manager.CustomTableCells;

import java.util.Objects;

import application.java.base.BaseTableViewModel;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.TableColumnManager.colKeyValuePairItem;
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
public class CustomComboBoxKvpSourceManager<S  extends BaseTableViewModel, K, V> extends TableCellManager<S, V> 
{
	private final ComboBox<colKeyValuePairItem<Integer, String>> comboBox;

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
	 * @param kvpItems 設定する選択肢（リスト）: ObservableList[keyValuePairItem[Integer, String]]
	 */
	public void setDataSource(ObservableList<colKeyValuePairItem<Integer, String>> kvpItems) {
		this.comboBox.setItems(kvpItems);
	}		
	
	/**
	 * 選択肢（リスト）を追加する。
	 * @param kvpItem 追加するデータ：keyValuePairItem[Integer, String]
	 */
	public void setData(colKeyValuePairItem<Integer, String> kvpItem) {
		this.comboBox.getItems().add(kvpItem);
	}	
	
	/**
	 * コンボボックスの入力テキストを取得する
	 * @return 入力値(Text)
	 */
	public String getComboEditorText(){
		return this.comboBox.getEditor().getText();
	}

	/**
	 * 選択肢（リスト）が存在するか
	 * @return 判定結果
	 * @brief ComboBoxに選択リストが存在する場合は、[真]<br>
	 * 選択リストがない(生成前含む)場合は、[偽]
	 */
	public boolean itemListAny(){
		return !this.comboBox.getItems().isEmpty();
	}	

	/**
	 * 入力テキストに一致するリストの選択肢(KeyValuePair)を取得する
     * @param 入力テキスト
	 * @return 選択肢(KeyValuePair)
	 * @brief 一致する選択肢がない場合は[null]を返す。<br>
	 */
	public colKeyValuePairItem<Integer, String> getSelectedListItem(String text){
		return this.comboBox.
				getItems().
				stream().
				filter(kvp -> kvp.value().equals(text)).
				findFirst().
				orElse(null);
	}	

	
    /**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param kvpItems ComboBoxの選択リスト(KeyValuePairのリスト)
     * @param isAlwaysShow ComboBoxを常時表示するか
     */
    @SuppressWarnings({ "unused"})
	public CustomComboBoxKvpSourceManager(
			String colId,
			String keyModelName,
			ObservableList<colKeyValuePairItem<Integer, String>> kvpItems,
			Boolean isAlwaysShow) 
    {
    	this.isAlwaysShow = isAlwaysShow;

    	// items が null なら空のリストを入れる
        this.comboBox = new ComboBox<>(kvpItems != null ? kvpItems : FXCollections.observableArrayList());
        
        // 編集可能（オートコンプリート用）なComboBoxを準備
        createCustomComboBox(colId, keyModelName);

        if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // ComboBoxがフォーカスを得た＝ユーザーが操作しようとしている
        this.comboBox.focusedProperty().addListener((obs, old, isFocused) -> {
            
            if (isFocused && !isAdjusting) {
                // データが1件もない場合はフォーカスを無視する
                if (getTableView().getItems().isEmpty() || getIndex() < 0) {
                    getTableView().requestFocus();
                    return;
                }
            	
                if (isFocused) {
                	isAdjusting = true; 
                	// 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
                    getTableView().edit(getIndex(), getTableColumn());
                	isAdjusting = false; 
                }
            }        	
        });
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
    	
    	LogManager.writeTrace("[CustomComboBoxKVP][startEdit] Start");

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
    	
    	LogManager.writeTrace("[CustomComboBoxKVP][cancelEdit] Start");
 
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
    protected void updateItem(V value, boolean empty) {
        
    	super.updateItem(value, empty);

    	LogManager.writeTrace("[CustomComboBoxKVP][updateItem] Start");
    	LogManager.writeTrace("value :["+ value + "] empty :[" + empty + "] text: [" + getText() + "]");
    	
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if ( empty || value == null || getIndex() < 0) {
         	setGraphic(null);
         	// TableCell.SetText
         	setText(null);

        } else {
        	// データが存在する場合の表示処理
        	LogManager.writeTrace("comboBox.value :[" + ((this.comboBox.getValue() == null)?null:this.comboBox.getValue().value()) + "]");
        	LogManager.writeTrace("comboBox.ItemCount :[" + this.comboBox.getItems().size() + "]");
        	
    		// 表示する前に、現在のデータモデルの値をセットする(初期値の設定)
            if (!Objects.equals(value.toString(), this.comboBox.getEditor().getText()) &&
            		getIndex() >= 0 && 
            		!this.comboBox.getItems().isEmpty() ) {
            	Platform.runLater(() -> {
            		isAdjusting = true;
            		try {
            			if ( empty || value == null || getIndex() < 0 || !this.itemListAny() ) { return; }
            			this.comboBox.getEditor().setText(value.toString());
                    } finally {
                        isAdjusting = false;
                    }
                });
            }	
        	
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
                    setText(value != null ? value.toString() : null);
                }
            }	
        }
        LogManager.writeTrace("[CustomComboBoxKVP][updateItem] End");
    }
 
    /**
     * カスタムコンボボックス生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     */
    private void createCustomComboBox(String colId, String keyModelName) {

    	// ComboBoxの配置(Cellの中央)
    	this.setAlignment(Pos.CENTER);

    	// ComboBoxのサイズ(Cellに合わせる)
    	comboBox.prefWidthProperty().bind(this.widthProperty().subtract(5.0));
    	comboBox.setMaxWidth(Control.USE_PREF_SIZE);      	
    	
    	// ComboBox 表示設定[fromString(kvpを貰う) / toString(valueを返す)]メソッドをOverRide
        super.setupPairConverter(this.comboBox);
    	
    	// オートコンプリート機能付与
        setupAutoComplete(colId, keyModelName);
        
        // 選択リスト確定機能追加
        onSelectedItemComboBox(colId, keyModelName);
    }
    
	/**
	 * 機能追加：入力Mode + AutoComplete機能 + 確定動作(Leave Event)
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 */
    @SuppressWarnings({ "unused", "unchecked" })
    private void setupAutoComplete(String colId, String keyModelName) {
    	  
        // オートコンプリートを有効にするため「編集可能」にする
        this.comboBox.setEditable(true);
        
     	TextField editor = this.comboBox.getEditor();

     	// 入力監視リスナー(オートコンプリート機能の付与)
     	// (obs = 値変更を監視しているプロパティ:ObservableValue = editor.textProperty())
     	editor.textProperty().addListener(
     			(obs, oldText, newText) -> {
     			    // 追加：リストが空なら何もしない
     			    if (this.comboBox.getItems() == null || this.comboBox.getItems().isEmpty()) { return; }					  
     				
     				if (newText == null || 
						newText.isEmpty() || 
						newText.length() < oldText.length()) { return; }
					  
					  // 前方一致する最初の候補を探す(入力文字と最初が一致するリストの値)
					  String matchText = this.comboBox.getItems().
							  stream().
							  filter(kvp ->  kvp.value().toLowerCase().startsWith(newText.toLowerCase())).
							  findFirst().
							  map(item -> item.value()).  // ここでStringに変換
							  orElse(newText);            // 見つからなければ入力文字そのものを使う					  
					  
					  if (matchText != null) {
						  // 入力完了後(Leave)の動作(runLater)
						  Platform.runLater(() -> 
						  {
							  if ( isAdjusting ) { return; }
							  
							  int caretPos = newText.length();
							  int matchLen = matchText.length();
							  
							  isAdjusting = true;
							  try {
								  if ( getIndex() < 0 || !this.itemListAny() ) { return; }
								  
								  // if (!isEditing()) { getTableView().edit(getIndex(), getTableColumn()); }  
								  // 補完文字をセット
								  editor.setText(matchText);
							  } finally { 
								  isAdjusting = false;
							  }

							  if (caretPos <= matchLen) {
								  // 補完部分をハイライト
								  editor.selectRange(caretPos, matchLen);
							  } else {
								  // もし入力文字の方が長い場合は、とりあえず末尾にカーソルを置く
								  editor.positionCaret(matchLen);
							  }

							  /*//値入力後、選択リストを表示する
							  if (!this.comboBox.isShowing() || 
								  !this.comboBox.getItems().isEmpty()) {
								  LogManager.writeTrace("[CustomComboBoxKVP][textProperty] comboBox.show");
								  // リストを表示
								  this.comboBox.show();
							  } */
						  });
					  }
     			});
     	
        // 入力監視リスナー(Leave相当)値確定時の処理
        // ⇒ 全ての文字列が入力されてからCommitする
        this.comboBox.setOnAction(
        		(e) -> {
    				LogManager.writeTrace("[CustomComboBoxKVP][setOnAction] Start");
    				
    				if( isAdjusting || isEmpty() || getIndex() < 0 ) { return; }

    				// エディタに入力されている文字列を取得
  		            String editText = this.comboBox.getEditor().getText();
  		       
  		            // 値確定処理
  		            commitEdit((V) editText);
  		            
  		            // リストから一致するものを探す
  		            colKeyValuePairItem<Integer, String> selectedKvp = this.getSelectedListItem(editText);

  		            // モデルへの値反映
  		            bindingModelProperty(colId, keyModelName, selectedKvp, editText);
        		});
    }
    
    /**
     * 機能追加：コンボボックス選択確定動作(SelectedValue Event)
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 * @brief 選択したItemをカラムのBINDソース<S>に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提<br>
	 * コンボボックスのリストを選択した瞬間に発報されるイベント<br>
	 * setOnActionと二重実行される。isAdjustingにて監視・制御すること。
     */
    @SuppressWarnings({ "unused", "unchecked" })
	private void onSelectedItemComboBox(String colId, String keyModelName) {
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldKvp, newKvp) -> {
    			    if (newKvp == null || isAdjusting) { return; }
    			    LogManager.writeTrace("[CustomComboBoxKVP][selectedItemProperty] Start");

    			    Platform.runLater(() -> {
                		isAdjusting = true;
                		try {
                			if (!isEditing()) { getTableView().edit(getIndex(), getTableColumn()); }               			
                			
            				// 値の確定(TableView側へ通知)
                			this.commitEdit((V) newKvp.value());
                			
                			// モデルへの値反映
                			bindingModelProperty(colId, keyModelName, newKvp, newKvp.value());
                        } finally {
                            isAdjusting = false;
                        }
                    });    			    

        		});
    }

    /**
     * モデルへの値反映
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param selectedKvp 選択(入力)したコンボボックス一覧の対象データ
     * @param editText 画面に入力されている値
     */
    private void bindingModelProperty(String colId, 
    		                          String keyModelName,
    		                          colKeyValuePairItem<Integer, String> selectedKvp,
    		                          String editText) {
        // 二重実行防止
        if (isAdjusting) return;
        
        isAdjusting = true;
        try {
				
        	// 値の確定 VALUEのBIND
			// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
			super.setRowClassProperty(colId, String.class, editText);

			// KeyのBIND    	    	    	
			if(!AppUtil.StringIsNullOrWhiteSpace(keyModelName)) {
				// 一致するものがあればそのKey、なければ定数(-1)をセット
				Integer keyToSet = (selectedKvp != null) ? 
						selectedKvp.key() : 
							AppConst.UNSET_NUMBER_VALUE;
	                
				super.setRowClassProperty(keyModelName, Integer.class, keyToSet);
			}
         
            // 連動項目のBIND設定
            syncModelPropertyBindingEvent(null, selectedKvp);

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
    protected void syncModelPropertyBindingEvent(
    		colKeyValuePairItem<Integer, String> befValue, 
    		colKeyValuePairItem<Integer, String> newValue) {
    	
    	return;
    }
}
