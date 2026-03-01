package application.java.manager.CustomTableCells;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.java.common.AppUtil;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;

/**
 * 
 * @param <S>
 * @param <T>
 * @brief
 * 編集時のみ表示モード
 * [Enter]で入力(編集)モードへ遷移 / 編集モード値確定
 * [ESC]で入力(編集)キャンセル処理
 */
public class CustomDatePickerManager<S, T> extends TableCell<S, T>  {
	private final DatePicker datePicker;
    private final Boolean isAlwaysShow;
	
	private final String colId;

	private Boolean isAdjusting = false;

	
	/**
     * コンストラクタ
     * @param colId カラムのID(自身(カスタムチェックボックス)のカラムのID)
     * @param isAlwaysShow CheckBoxを常時表示するか
	 */
	public CustomDatePickerManager(String colId, Boolean isAlwaysShow) {
		this.isAlwaysShow = isAlwaysShow;
		
        this.datePicker = new DatePicker();
		
		this.colId = colId;

	        
	        // カレンダーから日付が選ばれた時の処理
	        this.datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
	            if (isAdjusting || newVal == null) return;
	            
	            // モデルへの反映 (String型として保存する場合)
	            updateModel(newVal.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
	        });
	        
	        // セル内中央揃え
	        this.setAlignment(javafx.geometry.Pos.CENTER);
	    }

	    private void updateModel(String dateStr) {
	        try {
	            S rowData = getTableView().getItems().get(getIndex());
	            String methodName = "set" + colId.substring(0, 1).toUpperCase() + colId.substring(1);
	            Method setter = rowData.getClass().getMethod(methodName, String.class);
	            setter.invoke(rowData, dateStr);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
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
	    	
	    	System.out.println("CustomDatePicker startEdit");
	    	if (!isAlwaysShow) {
	    		/* 編集時のみ表示モード */
	        	setGraphic(this.datePicker);
	        	setText(null);
	        } else if (!isEditing()) {
	        	System.out.println("CustomDatePicker NotEditMode");
	        	return;
	        }

	    	System.out.println("CustomDatePicker EditMode");
	    	this.datePicker.requestFocus();
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
	    	
	        System.out.println("CustomDatePicker cancelEdit");
	    	if (!isAlwaysShow) {
	    		/* 編集時のみ表示モード */
	    		// 非編集モードへ遷移
	            setGraphic(null);
        		if (AppUtil.isDate(getItem().toString())){
        			setText(getItem().toString());
        		} else {
        			setText(null);
        		}

        		return;
	        }
	    	
	    	/* 常時表示モード */
	    	setGraphic(this.datePicker);
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
	    protected void updateItem(T item, boolean empty) {
	        
	    	super.updateItem(item, empty);

	    	System.out.println("CustomDatePicker updateItem");
	    	// セルが空、またはデータがnullの場合の処理（重要：再利用対策）
	        if (empty || item == null || item.toString().isEmpty()) {
	            setGraphic(null);
	        } else {
	        	// データが存在する場合の表示処理
	        	if (isAlwaysShow) {
	                /* 常時表示モード */
		            
	            	//自動入力による重複処理の制御
	            	isAdjusting = true;
	        		if (AppUtil.isDate(item.toString())){
		                // モデルの文字列(yyyy/MM/dd)をLocalDateに変換してセット
	        			LocalDate date = LocalDate.parse(
		                		item.toString(), 
		                		DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	        			
	        			this.datePicker.setValue(date);
	        		} else {
	        			this.datePicker.setValue(null);
	        		}
	        		isAdjusting = false;
	        		
	                setGraphic(this.datePicker);

	        	} else {
	                /* 編集時のみ表示モード */
	        		if (isEditing()) {
	                    setGraphic(this.datePicker);
	                } else {
	                    setGraphic(null);
	                }
	            }
	        }}	    
	    
	    /**
	     * カスタムチェックボックス生成
	     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
	     */
	    private void createCustomDatePickerBox(String colId) {

	        // チェック確定機能追加
	        setupDatePickerSelectedValue(colId);        
	    }	
		
	    /**
	     * 機能追加：チェックボックス選択確定動作
	     * @param colId カラムのID(自身(カスタムコンボボックス)のカラムのID)
		 * @brief 選択したItemをカラムのBINDソース<S>に反映<br>
		 * カラムの[id]と[fx:id]は同一の前提
	     */	
		@SuppressWarnings({ "unchecked", "unused" })
		private void setupDatePickerSelectedValue(String colId) {
	        // 入力監視リスナー
			// (obs = 値変更を監視しているプロパティ:ObservableValue = comboBox.getSelectionModel().selectedItemProperty())
	        this.datePicker.valueProperty().addListener(
	        		(obs, oldVal, newVal) -> {
	        			if (newVal == null || isAdjusting) { return; }
	        			
	        			// System.out.println("CustomCell selectedItemProperty().addListener");

	    	            // モデルへの反映 (String型として保存する場合)
	    	            String value = newVal.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));	        			
	        			
	        			if (isEditing()) {
	        				
	        				// 編集モード(値の確定)
	        				this.commitEdit((T)value);
	        			} else {
	        				// 非編集モード(値の確定)
	        				S rowData = getTableView().getItems().get(getIndex());
	        			   
	        			   // ※ リフレクションが最新のJAVAでは禁止(Exception)されているため
	         	    	   // モデルへの値反映
	        	    	    try {
	        	    	        // メソッド(値のSetter プロパティ)名の生成
	        	    	        String methodName = "set" + colId.substring(0, 1).toUpperCase() + colId.substring(1);
	        	    	        
	        	                // モデルからメソッドを探して実行
	        	                Method setter = rowData.getClass().getMethod(methodName, String.class);
	        	                setter.invoke(rowData, value);
	        	    	    } catch (Exception e) {
	        	    	    	// 型が不一致（例: String vs Object）で失敗する場合のデバッグ
	        	                System.err.println("モデルへの値反映に失敗しました: " + colId);
	        	    	    	
	        	    	    	e.printStackTrace();
	        	    	    }
	        			}});}
}
