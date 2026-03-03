package application.java.manager.CustomTableCells;

import javafx.scene.control.CheckBox;

/**
 * カスタムControl：TableCell + CheckBox
 * @param <S> (Table Source / Subject): 行データのクラス名。
 * @param <T> (Column Type): セルに表示する項目の型。
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomCheckBoxTableCellManager<S, T> extends TableCellManager<S, T> {
	private final CheckBox checkBox;
	private final Boolean isAlwaysShow;

	private Boolean isAdjusting = false;
	
	/**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムチェックボックス)のカラムのID)
     * @param isAlwaysShow CheckBoxを常時表示するか
	 */
	@SuppressWarnings("unused")
	public CustomCheckBoxTableCellManager(String colId, Boolean isAlwaysShow) {
		this.isAlwaysShow = isAlwaysShow;

		this.checkBox = new CheckBox();

		// カスタムControlの生成
		createCustomCheckBox(colId); 
	        
        if (!this.isAlwaysShow)
        {
        	return;
        }

        /* 常時表示モード */
        // ComboBoxがフォーカスを得た＝ユーザーが操作しようとしている
        this.checkBox.focusedProperty().addListener(
        		(obs, oldVal, newVal) -> {
        			if (newVal) {
        				System.out.println("CustomCheckBox focusedProperty().addListener");
        				
        				// 親の TableView(TableCell)に対して、編集状態への移行(Enterが押下された)を通知
        				getTableView().edit(getIndex(), getTableColumn());
        			}});}

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
    	
    	System.out.println("CustomCheckBox startEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
        	setGraphic(checkBox);
        	setText(null);
        } else if (!isEditing()) {
        	System.out.println("CustomCheckBox NotEditMode");
        	return;
        }

    	System.out.println("CustomCheckBox EditMode");

    	checkBox.requestFocus();
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
    	
        System.out.println("CustomCheckBox cancelEdit");
    	if (!isAlwaysShow) {
    		/* 編集時のみ表示モード */
    		// 非編集モードへ遷移
            setGraphic(null);
            setText(getItem() != null ? ((boolean) getItem() ? "CHECKED" : "") : null);
            return;
        }
    	
    	/* 常時表示モード */
    	setGraphic(this.checkBox); 
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

    	System.out.println("CustomCheckBox updateItem");
    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
        if (empty) {
            setGraphic(null);
        } else {
        	// データが存在する場合の表示処理
        	if (isAlwaysShow) {
                /* 常時表示モード */

            	//自動入力による重複処理の制御
            	isAdjusting = true;
        		this.checkBox.setSelected((Boolean) item);
        		isAdjusting = false;
        		
                setGraphic(this.checkBox);

        	} else {
                /* 編集時のみ表示モード */
        		if (isEditing()) {
                    setGraphic(this.checkBox);
                } else {
                    setGraphic(null);
                }
            }
        }}
	
    /**
     * カスタムチェックボックス生成
     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
     */
    private void createCustomCheckBox(String colId) {
        // チェック確定機能追加
    	onCheckedCheckBox(colId);        
    }	
	
    /**
	 * 機能追加：CheckBox選択確定動作(Leave Event)
	 * @param colId カラムのID(自身(カスタムControl)のカラムのID)
     * @brief 選択したItemをカラムのBINDソース[S]に反映<br>
	 * カラムの[id]と[fx:id]は同一の前提
     */	
	@SuppressWarnings({ "unchecked", "unused" })
	private void onCheckedCheckBox(String colId) {

        // 入力監視リスナー(Leave相当)
		this.checkBox.setOnAction(
        		(e) -> {
        			if (isAdjusting) { return; }
        			
        			System.out.println("CustomCheckBox setOnAction");
        			Boolean newVal = checkBox.isSelected();
        			
        			if (isEditing()) {
        				// 編集モード(値の確定)
        				this.commitEdit((T)newVal);
        			} else {
        				//System.out.println("値反映処理実行: " + value);
        				getTableView().edit(getIndex(), getTableColumn());
        				commitEdit((T)newVal); 
        				
        				// 非編集モード(値の確定)
        				super.setRowClassProperty(colId, Boolean.class, newVal);
        			}});
        }
}
