package application.manager;

import application.base.BaseFormPage;
import application.base.BaseTableViewModel;
//import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class TableViewManager<T extends BaseTableViewModel> extends TableView<T> {

	private Boolean isRowsMultiSelected = false;
	private Boolean isCellSelected = false;	
	
	
	public Boolean getIsRowsMultiSelected() {
		return isRowsMultiSelected;
	}


	public void setIsRowsMultiSelected(Boolean isMultiSelected) {
		this.isRowsMultiSelected = isMultiSelected;
		
		if (isMultiSelected) {
			this.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
		} else {
			this.getSelectionModel().setSelectionMode( SelectionMode.SINGLE );
		}
	}

	public Boolean getIsCellSelected() {
		return isCellSelected;
	}


	public void setIsCellSelected(Boolean isCellSelected) {
		this.isCellSelected = isCellSelected;
		
		if (isCellSelected) {
			this.getSelectionModel().setCellSelectionEnabled( true );
		}
	}


	/**
	 * コンストラクタ
	 */
	public TableViewManager() {
        super();

        System.out.println("TableViewManager");
	}

	/**
	 * 
	 * @param form
	 */
	public void tableViewSettings(BaseFormPage form) {
		
		// Bind設定
		form.bindTableColumnSource();
		
		// 選択
		if( this.isCellSelected) {
			
		} else {
			this.addSelectedRowsEvent(form);
		}
	}
	
	/**
	 * 
	 */
	private void addSelectedRowsEvent(BaseFormPage form) {
        
		// 選択を検知するバインディングを設定
		this.getSelectionModel().selectedItemProperty().addListener((ov , old , current) -> {
		    if (current != null) {
		    	form.onTableSelectedRowsEvent(current);
		    }
		});

		
		// 選択を検知するバインディングを設定
        /*this.getSelectionModel().selectedItemProperty().addListener( 
                ( ov , old , current) ->
                {
                    // 標準出力にヘッダ文字出力
                    System.out.println( "選択セル（TableView）" );
                     
                    // 選択したセル位置を取得
                    for( TablePosition<T, ?> pos : this.getSelectionModel().getSelectedCells() )
                    {
                        // 選択行・列の情報を取得
                        int row = pos.getRow();
                        TableColumn<T, ?> col = pos.getTableColumn();
                         
                        // 選択行を取得
                        TableRow item = this.getItems().get( row );
 
                        // 選択セルを取得
                        String selected = (String)col.getCellObservableValue(item).getValue();
                         
                        // 標準出力に出力
                        System.out.println( "　" + selected );
                         
                    }
                });*/
		
		/*List<Field> allFields = getClass().getDeclaredFields(cls.getClass());

        for (Field field : allFields) {
            // フィールド名、型、アクセス修飾子を取得
            System.out.println("Field Name: " + field.getName());
            System.out.println("Field Type: " + field.getType().getName());
            System.out.println("Field Declaring Class: " + field.getDeclaringClass().getSimpleName());
            System.out.println("---");*/
	}	
}
