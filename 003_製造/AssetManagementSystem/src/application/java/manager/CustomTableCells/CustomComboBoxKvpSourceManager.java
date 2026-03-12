   package application.java.manager.CustomTableCells;

import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.LogManager;
import application.java.manager.TableColumnManager.keyValuePairItem;
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
 *
 */
public class CustomComboBoxKvpSourceManager<S, K, V> extends TableCellManager<S, V> {
	private final ComboBox<keyValuePairItem<Integer, String>> comboBox;

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
	public void setDataSource(ObservableList<keyValuePairItem<Integer, String>> kvpItems) {
		this.comboBox.setItems(kvpItems);
	}		
	
	/**
	 * 選択肢（リスト）を追加する。
	 * @param kvpItem 追加するデータ：keyValuePairItem[Integer, String]
	 */
	public void setData(keyValuePairItem<Integer, String> kvpItem) {
		this.comboBox.getItems().add(kvpItem);
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
			ObservableList<keyValuePairItem<Integer, String>> kvpItems,
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
        this.comboBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
            	isAdjusting = true; 
            	// 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
                getTableView().edit(getIndex(), getTableColumn());
            	isAdjusting = false; 
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
    	
    	System.out.println("CustomComboBoxKVP startEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(comboBox);
        	setText(null);
        } else if (!isEditing()) {
        	return;
        }
    	System.out.println("CustomComboBoxKVP EditMode");
    	
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
    	
        System.out.println("CustomComboBoxKVP cancelEdit");
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

    	System.out.println("CustomComboBoxKVP updateItem");
    	System.out.println("empty : "+ empty);
    	
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if ( empty || value == null ) {
            System.out.println("value : "+ value);
            System.out.println("text : "+ getText());
        	
        	setGraphic(null);
            setText(null);

        } else {
        	// データが存在する場合の表示処理
        	isAdjusting = true;
        	if(this.comboBox.getValue() != null)
        	{
        		System.out.println("value : "+ this.comboBox.getValue().value());
        	}
        	
        	
            try {
                // ComboBoxの選択状態を現在のモデルの値に合わせる
                keyValuePairItem<Integer, String> selectedKvp = this.comboBox.getItems().stream()
                        .filter(kvp -> kvp.value() != null && kvp.value().equals(value))
                        .findFirst()
                        .orElse(null);

                //this.comboBox.setValue(selectedKvp); // ここでリスナーが動くが、フラグによりガードされる
            } finally {
                // ★処理が終わったらフラグを下ろす
            	isAdjusting = false;
            }
        	
        	
        	
            setText("");
        	
        	
        	
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
                    setText(value != null ? value.toString() : null);
                }
            }	
        }
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
     			(obs, oldValue, newValue) -> {
					  if (newValue == null || 
						  newValue.isEmpty() || 
						  newValue.length() < oldValue.length()) {
		                    return; // 削除時は補完しない
		                }

					  // 前方一致する最初の候補を探す(入力文字と最初が一致するリストの値)
					  String match = this.comboBox.getItems().
							  stream().
							  filter(kvp ->  kvp.value().toLowerCase().startsWith(newValue.toLowerCase())).
							  findFirst().
							  map(item -> item.value()).  // ここでStringに変換
							  orElse(newValue);            // 見つからなければ入力文字そのものを使う					  
					  
					  if (match != null) {
						  Platform.runLater(
								  () -> {
									  isAdjusting = true;
									  
									  try {
										  int caretPos = newValue.length();
										  int matchLen = match.length();
										  
										  editor.setText(match); // 補完文字をセット

									        if (caretPos <= matchLen) {
									            editor.selectRange(caretPos, matchLen); // 補完部分をハイライト
									        } else {
									            // もし入力文字の方が長い場合は、とりあえず末尾にカーソルを置く
									            editor.positionCaret(matchLen);
									        }

									        if (!this.comboBox.isShowing()) {
									        	this.comboBox.show(); // リストを表示
									        	}
									  } finally { 
										  isAdjusting = false;
									  }
								  });}
					  });
     	
        // 入力監視リスナー(Leave相当)値確定時の処理
        // ⇒ 全ての文字列が入力されてからCommitする
        this.comboBox.setOnAction(
        		e -> {
    				System.out.println("CustomComboBoxKVP setOnAction");

    				if(isAdjusting) { return; }

    				// エディタに入力されている文字列を取得
  		            String editValue = this.comboBox.getEditor().getText();
  		       
  		            // 値確定処理
  		            commitEdit((V) editValue);
  		            
  		            // リストから一致するものを探す
  		            keyValuePairItem<Integer, String> selectedKvp = 
  		            		this.comboBox.
  		            		     getItems().
  		            		     stream().
  		            		     filter(kvp -> kvp.value().equals(editValue)).
  		            		     findFirst().
  		            		     orElse(null);

  		            // モデルへの値反映
  		            bindingModelProperty(colId, keyModelName, selectedKvp, editValue);
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
        		(obs, oldVal, newVal) -> {
    			    if (newVal == null || isAdjusting) { return; }

        			System.out.println("CustomComboBoxKVP SelectedItem");
           			
        			if (!isEditing()) {
        				getTableView().edit(getIndex(), getTableColumn());
        			}               			
        			
    				// 値の確定(TableView側へ通知)
        			this.commitEdit((V) newVal.value());
        			
        			// モデルへの値反映
        			bindingModelProperty(colId, keyModelName, newVal, newVal.value());
        		});
    }

    /**
     * モデルへの値反映
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     * @param selectedKvp 選択(入力)したコンボボックス一覧の対象データ
     * @param editValue 画面に入力されている値
     */
    private void bindingModelProperty(String colId, 
    		                          String keyModelName,
    		                          keyValuePairItem<Integer, String> selectedKvp,
    		                          String editValue) {
        // 二重実行防止
        if (isAdjusting) return;
        
        isAdjusting = true;
        try {
				
        	// 値の確定 VALUEのBIND
			// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
			super.setRowClassProperty(colId, String.class, editValue);

			// KeyのBIND    	    	    	
			if(!AppUtil.StringIsNullOrWhiteSpace(keyModelName)) {
				// 一致するものがあればそのKey、なければ定数(-1)をセット
				Integer keyToSet = (selectedKvp != null) ? 
						selectedKvp.key() : 
							AppConst.UNSET_NUMBER_VALUE;
	                
				super.setRowClassProperty(keyModelName, Integer.class, keyToSet);

				System.out.println("call methodKey");
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
     * @param befValue selectedItemProperty().addListener oldVal
     * @param newValue selectedItemProperty().addListener newVal
	 * @brief 行データ(Model)の他の項目(Property)を連動して変更する場合などの用いる。<br>
	 * 当該クラスを継承した、子クラスにて内容を定義する.
     */
    protected void syncModelPropertyBindingEvent(
    		keyValuePairItem<Integer, String> befValue, 
    		keyValuePairItem<Integer, String> newValue) {
    	
    	return;
    }
}
