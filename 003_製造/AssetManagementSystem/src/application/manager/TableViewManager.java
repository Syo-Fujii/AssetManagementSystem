package application.manager;

import application.base.BaseTableViewModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

public class TableViewManager<T extends BaseTableViewModel> extends TableView<T> {

	
	public TableViewManager() {
		
		
		
		
		
		
		
	}
	
	
	
	
	public void test( T cls){
        this.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );
        this.getSelectionModel().setCellSelectionEnabled( true );
	}

	/*
	@Override
	public void setItems(ObservableList<T> data) {

		
		
		
		
		
		this.setItems(data);
	}
	
	/*public void test(Class<?> cls) {
        // 選択を検知するバインディングを設定
        this.getSelectionModel().selectedItemProperty().addListener( 
                ( ov , old , current) ->
                {
                    // 標準出力にヘッダ文字出力
                    System.out.println( "選択セル（TableView）" );
                     
                    // 選択したセル位置を取得
                    for( TablePosition<TableData, ?> pos : this.getSelectionModel().getSelectedCells() )
                    {
                        // 選択行・列の情報を取得
                        int row = pos.getRow();
                        TableColumn<TableData, ?> col = pos.getTableColumn();
                         
                        // 選択行を取得
                        TableRow item = tableListView.getItems().get( row );
 
                        // 選択セルを取得
                        String selected = (String)col.getCellObservableValue(item).getValue();
                         
                        // 標準出力に出力
                        System.out.println( "　" + selected );
                         
                    }
                }
                );
		
		/*List<Field> allFields = getClass().getDeclaredFields(cls.getClass());

        for (Field field : allFields) {
            // フィールド名、型、アクセス修飾子を取得
            System.out.println("Field Name: " + field.getName());
            System.out.println("Field Type: " + field.getType().getName());
            System.out.println("Field Declaring Class: " + field.getDeclaringClass().getSimpleName());
            System.out.println("---");*/
   //}*/
}
