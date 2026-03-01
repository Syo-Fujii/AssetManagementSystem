package application.java.manager.CustomTableCells;

import java.lang.reflect.Method;

import application.java.manager.TableColumnManager.keyValuePairItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class CustomComboBoxKvpSourceManager<S, K, V> extends TableCell<S, V> {
	private static final int NOT_SELECTED_ITEM_VALUE = -1;
	private final ComboBox<keyValuePairItem<Integer, String>> comboBox;

    private final Boolean isAlwaysShow;
    private Boolean isAdjusting = false;
    
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
    	
    	System.out.println("CustomCell startEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(comboBox);
        	setText(null);
        } else if (!isEditing()) {
        	return;
        }
    	System.out.println("CustomCell EditMode");
    	
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
    	
        System.out.println("CustomCell cancelEdit");
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
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時<br>
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時<br>
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時<br>
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時<br>
     */
    @Override
    protected void updateItem(V value, boolean empty) {
        
    	super.updateItem(value, empty);

    	System.out.println("CustomComboBoxKeyValuePair updateItem");
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */

        		/*
        		//自動入力による重複処理の制御
            	isAdjusting = true;
            	// モデルの String 値と一致する Pair を探してセット
            	keyValuePairItem<Integer, String> selected = 
            	 this.comboBox.getItems().
            	               stream().
            	               filter(p -> p.value().equals(value)).
            	               findFirst().
            	               orElseGet(() -> new keyValuePairItem<>(NOT_SELECTED_ITEM_VALUE, value.toString()));

                this.comboBox.setValue(selected);
                isAdjusting = false;*/

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
        }}
 
    /**
     * カスタムコンボボックス生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
     */
    private void createCustomComboBox(String colId, String keyModelName) {

    	// ComboBox 表示設定[fromString(kvpを貰う) / toString(valueを返す)]メソッドをOverRide
        setupPairConverter(this.comboBox);
    	
    	// オートコンプリート機能付与
        setupAutoComplete(keyModelName);
        
        // 選択リスト確定機能追加
        setupComboBoxSelectedItem(colId, keyModelName);
    }
    
	/**
	 * 機能追加：入力Mode + AutoComplete機能
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 */
    @SuppressWarnings({ "unused", "unchecked" })
    private void setupAutoComplete(String keyModelName) {
    	  
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
     	
        // オートコンプリートに伴う、値確定時の処理
        // ⇒ 全ての文字列が入力されてからCommitする
        this.comboBox.setOnAction(
        		e -> {
    				System.out.println("CustomCell setOnAction");

        			//if(isAdjusting || !isEditing()) { return; }
    				if(isAdjusting) { return; }

    				//System.out.println("CustomCell setOnAction2");
    				// エディタに入力されている文字列を取得
  		            String cellValue = this.comboBox.getEditor().getText();
  		            
  		            //if (cellValue == null || cellValue.trim().isEmpty()) { e.consume(); return; }
  		            // リストから一致するものを探す、なければ入力文字列そのものを確定
  		            keyValuePairItem<Integer, String> selectedKvp = 
  		            		this.comboBox.
  		            		     getItems().
  		            		     stream().
  		            		     filter(kvp -> kvp.value().equals(cellValue)).
  		            		     findFirst().
  		            		     orElse(null);
  		            // startEdit();

  		            if (selectedKvp != null) {
  	    				// 値確定処理
        				commitEdit((V)selectedKvp.value());
  		            } else {
  		                // リストにない文字が直接打たれた場合
  		                String rawText = this.comboBox.getEditor().getText();
  		                commitEdit((V) rawText);
  		            }

       	    	   // モデルへの値反映
      	    	    try {
      	    	        // メソッド(値のSetter プロパティ)名の生成
      	                if(keyModelName != null && !keyModelName.isEmpty()) {
      	                	S rowData = this.getTableView().getItems().get(getIndex());
      	                	
      	                	// メソッド(値のSetter プロパティ)名の生成
          	    	        String methodKey = "set" + keyModelName.substring(0, 1).toUpperCase() + keyModelName.substring(1);
          	    	        
          	                // モデルからメソッドを探して実行
          	                Method keySetter = rowData.getClass().getMethod(methodKey, Integer.class);
          	                
          	                // 一致するものがあればそのKey、なければ定数(-1)をセット
          	                Integer keyToSet = (selectedKvp != null) ? selectedKvp.key() : NOT_SELECTED_ITEM_VALUE;
          	                keySetter.invoke(rowData, keyToSet);

          	                System.out.println("call methodKey");

          	                // 画面表示を強制更新（TableViewのリフレッシュ）
          	                //getTableView().refresh();
      	                }
      	    	    } catch (Exception ex) {
      	    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
      	                System.err.println("モデルへの値反映に失敗しました: " + keyModelName);
      	    	    	ex.printStackTrace();
      	    	    }
        		});}
    
    /**
     * 機能追加：コンボボックス選択確定動作
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param keyModelName 選択リストのkeyを格納するModelのプロパティ名
	 * @brief 選択したItemをカラムのBINDソース<S>に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提
     */
    @SuppressWarnings({ "unused", "unchecked" })
	private void setupComboBoxSelectedItem(String colId, String keyModelName) {
        // 入力監視リスナー
		// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
        this.comboBox.getSelectionModel().selectedItemProperty().addListener(
        		(obs, oldVal, newVal) -> {
    			    if (newVal == null || isAdjusting) { return; }
        			
        			if (isEditing()) {
	                    //startEdit(); 	                    

        				// 編集モード(値の確定)
        				this.commitEdit((V) newVal.value());

        			} else {
        				// 非編集モード(値の確定)
        				S rowData = this.getTableView().getItems().get(getIndex());
        			   
        			   // ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
        			   
         	    	   // モデルへの値反映
        	    	    try {
        	    	        // メソッド(値のSetter プロパティ)名の生成
        	    	        String methodName = "set" + colId.substring(0, 1).toUpperCase() + colId.substring(1);
        	    	        
        	                // モデルからメソッドを探して実行
        	                Method setter = rowData.getClass().getMethod(methodName, String.class);
        	                setter.invoke(rowData, newVal.value());
        	                
        	                System.out.println("call methodName");
        	                
        	                if(keyModelName != null && !keyModelName.isEmpty()) {
            	    	        // メソッド(値のSetter プロパティ)名の生成
            	    	        String methodKey = "set" + keyModelName.substring(0, 1).toUpperCase() + keyModelName.substring(1);
            	    	        
            	                // モデルからメソッドを探して実行
            	                Method keySetter = rowData.getClass().getMethod(methodKey, Integer.class);
            	                keySetter.invoke(rowData, newVal.key());

            	                System.out.println("call methodKey");
            	                // 画面表示を強制更新（TableViewのリフレッシュ）
            	                //getTableView().refresh();
        	                }
        	    	    } catch (Exception e) {
        	    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
        	                System.err.println("モデルへの値反映に失敗しました: " + colId);
        	    	    	
        	    	    	e.printStackTrace();
        	    	    }
        			}});
    }
    
	/**
	 * ComboBoxにPair型の表示方法（StringConverter）を設定する
	 */
	private void setupPairConverter(ComboBox<keyValuePairItem<Integer, String>> cb) {
	    
		cb.setConverter(new StringConverter<keyValuePairItem<Integer, String>>() {

			/**
	         * kvpのクラスを引数に、設定されているValue(String型)を返す
	         */
			@Override
	        public String toString(keyValuePairItem<Integer, String> object) {
	            return (object == null) ? "" : object.value();
	        }

			/**
	         * 引数の文字列に一致する、kvpを返す
	         */
			@Override
	        public keyValuePairItem<Integer, String> fromString(String string) {
	            ObservableList<keyValuePairItem<Integer, String>> items = cb.getItems();
	            
	            // items が null または空の場合は処理を中断する
	            if (items == null || string == null || string.isEmpty()) {
	                return null;
	            }

	        	// 編集時に文字列から元のPairオブジェクトを探す処理
	            return cb.getItems().stream()
	                .filter(item -> item.value().equals(string))
	                .findFirst()
	                .orElseGet(() -> new keyValuePairItem<>(-1, string));  // 新規入力対応
	                //.orElse(null);
	        }
	    });
	}    
	
}
