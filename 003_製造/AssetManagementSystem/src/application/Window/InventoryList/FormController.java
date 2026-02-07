package application.Window.InventoryList;

import java.nio.file.Paths;

import application.Class.BaseFormPage;
import application.Window.TestNextWindow.Form2;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * 備品一覧画面
 * 画面操作メソッド(Controller)
 */
public class FormController extends BaseFormPage{
	
	@FXML private Button NextPage;
	@FXML private Label label_TEST;
	
	private int count = 0;
	
	/** 
	 * 
	 *
	 */
	public FormController() 
	{
		this.setfxmlFilePath("/application/Window/InventoryList/InventoryList.fxml");
		this.setWindowTitle("備品管理システム");
		
		String a = Paths.get("").toAbsolutePath().toString();
	}
	
	
    @FXML
    public void onButtonClicked() {
    	count += 1;
    	
    	label_TEST.setText("押下回数：" + count + "回");

    	super.setPage(new Form2());
    }
}
