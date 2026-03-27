package application.java.manager.CustomTableCells;

import java.util.Objects;

import application.java.base.BaseTableViewModel;
import application.java.manager.LogManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;

/**
 * カスタムControl：TableCell + TextField
 * @param <S> (Table Source / Subject): 行データのクラス名。
 * @param <T> (Column Type): セルに表示する項目の型。
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomTextFieldTableCellManager<S extends BaseTableViewModel, T> extends TableCellManager<S, T>
{
	private final TextField InputText;
	private final Boolean isAlwaysShow;

	private Boolean isAdjusting = false;
	
	
	/**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムテキスト)のカラムのID)
     * @param isAlwaysShow TextFieldを常時表示するか
	 */
	@SuppressWarnings("unused")
	public CustomTextFieldTableCellManager(String colId, Boolean isAlwaysShow) {
		this.isAlwaysShow = isAlwaysShow;

		this.InputText = new TextField();

		// カスタムControlの生成
		createCustomTextField(colId); 
	        
        if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // TextFieldがフォーカスを得た＝ユーザーが操作しようとしている
        this.InputText.focusedProperty().addListener(
        		(obs, oldVal, isFocused) -> {
        			if (!isFocused) {
        		        Platform.runLater(() -> {
        		            if (isEditing()) {
        		                // 入力値と元の値を比較
        		                String currentInput = InputText.getText();
        		                String modelValue = getItem() != null ? getItem().toString() : "";

        		                // 値が変わっていない、あるいは特定のキャンセル条件なら戻す
        		                if (Objects.equals(currentInput, modelValue)) {
        		                    // 値が同じなら、編集モードを終了するだけでOK
        		                	cancelEdit(); 
        		                } else {
        		                    // 値が変わっている場合のみ確定させる
        		                    valueCommited(colId);
        		                }
        		            }
        		        }); 
        				return;
        			} else {
        				LogManager.writeTrace("[CustomTextFeild][focusedProperty] Start");
        				
        		        if (getTableView() != null) {
        		        	// 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
        		        	getTableView().edit(getIndex(), getTableColumn());
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
    	
    	LogManager.writeTrace("[CustomTextFeild][cancelEdit] Start");
    
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(InputText);
        	setText(null);
 
    	} else if (!isEditing()) {
         	return;
        }

    	InputText.requestFocus();
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
    	
    	LogManager.writeTrace("[CustomTextFeild][cancelEdit] Start");
        // モデルが持っている本来の値を取得して TextField に上書きする
        String modelValue = getItem() != null ? getItem().toString() : null;
        
        isAdjusting = true;
        try {
            this.InputText.setText(modelValue);
        } finally {
            isAdjusting = false;
        }

        if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
    		// 非編集モードへ遷移
            setGraphic(null);
            setText(modelValue);
            return;
        }
    	
    	/* 常時表示モード */
    	setGraphic(this.InputText); 
    } 	
	
    /**
     * セルを描画・更新する際に内部で呼び出されるメソッド(TextChanged Event)
	 * @brief 主に以下のタイミングで実行<br>
	 *  ・セルの初期表示 : テーブルが画面に表示され、各セルにデータが流し込まれる時<br>
	 *  ・スクロール時   : セルが画面外に消え、新しいデータを表示するために再利用（リサイクル）される時<br>
	 *  ・データの変更   : ObservableList の中身が入れ替わったり、特定のプロパティが更新されて通知が飛んだ時<br>
	 *  ・表示の強制更新 : tableView.refresh() を明示的に実行した時<br>
     */
    @Override
    protected void updateItem(T item, boolean empty) {
        
    	super.updateItem(item, empty);

    	LogManager.writeTrace("[CustomTextFeild][updateItem] Start");

    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty || item == null) {
            setGraphic(null);
            // TableCell.SetText
            setText(null);
            
        } else {
        	// 表示する前に、現在のデータモデルの値をセットする(初期値の設定)
            if (!Objects.equals(item.toString(), this.InputText.getText())) {
                isAdjusting = true;
                try {
                	//自動入力による重複処理の制御
                	this.InputText.setText(item.toString());
                } finally {
                    isAdjusting = false;
                }
            }	
        	
        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */
                // すでにセット済みなら何もしない（マウス入力対応）
                if (getGraphic() != this.InputText ||
                		!Objects.equals(item.toString(), this.InputText.getText())) {
                    setGraphic(this.InputText);
                }
                setText(null);

        	} else {
                /* 編集時のみ表示モード */
        		if (isEditing()) {
                    setGraphic(this.InputText);
                    // エディタ表示中は文字を消す
                    setText(null);
                } else {
                    setGraphic(null);
                    // TableCell.SetText
                    setText(item != null ? item.toString() : null);
                }
            }
        }
       	LogManager.writeTrace("[CustomTextFeild][updateItem] End");     
    }
	
    /**
     * カスタムテキスト生成
     * @param colId カラムのID(自身(カスタムテキスト)のカラムのID)
     */
    private void createCustomTextField(String colId) {
        
    	// TextFieldの配置(Cellの左)
    	this.setAlignment(Pos.CENTER_LEFT);

    	// TextFieldのサイズ(Cellに合わせる)    	
    	// TextField.prefWidthProperty().bind(this.widthProperty().subtract(5.0));
        // TextFieldの幅を「自動（内容に合わせる）」にする
        // ※ bind してしまうと、TextFieldの領域がセルいっぱいに広がり、
        //    中のアイコンは「TextField領域内の左」に固定されてしまいます。
    	InputText.prefWidthProperty().bind(this.widthProperty().subtract(5)); 

    	// 値確定機能追加
    	onLeaveTextValue(colId);        
    }	
	
    /**
	 * 機能追加：TextField選択確定動作(Leave Event)
	 * @param colId カラムのID(自身(カスタムControl)のカラムのID)
     * @brief 選択したItemをカラムのBINDソース[S]に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提
     */	
	@SuppressWarnings({ "unused" })
	private void onLeaveTextValue(String colId) {

        // 入力監視リスナー(Leave相当)
		this.InputText.setOnAction(
        		(e) -> 
        		{
        			LogManager.writeTrace("[CustomTextFeild][setOnAction] Start");
         			valueCommited(colId);
        		});
	}

	@SuppressWarnings("unchecked")
	private void valueCommited(String colId) {
		
		if (isAdjusting || !isEditing()) { return; }

		isAdjusting = true;
		try {
			String editValue = this.InputText.getText();
			
			// 値の確定(TableView側へ通知)
			this.commitEdit((T) editValue);
			
			// モデルへの値反映
			bindingModelProperty(colId, null, editValue); 			
		} finally {
			isAdjusting = false;
		}
	}
	
	
    /**
     * モデルへの値反映
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     * @param oldVal 変更前の入力値
     * @param newVal 入力値
     */
	private void bindingModelProperty(String colId,String oldVal,String newVal) {
        // 二重実行防止
        if (isAdjusting) return;
        
        isAdjusting = true;
        try {
        	// 値の確定 VALUEのBIND
			// ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
			super.setRowClassProperty(colId, String.class, newVal);
        
            // 連動項目のBIND設定
            syncModelPropertyBindingEvent(null, newVal);

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
    protected void syncModelPropertyBindingEvent(String befValue, String newValue) {
    	return;
    }	
}
