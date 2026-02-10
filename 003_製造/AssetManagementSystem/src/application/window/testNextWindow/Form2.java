package application.window.testNextWindow;

import application.Class.BaseFormPage;
import application.window.inventoryList.FormController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Form2 extends BaseFormPage{
	
	@FXML private Button BackPage;
	@FXML private Label label_TEST;
	
	private int count = 0;
	
	public Form2() 
	{
		this.setfxmlFilePath("/application/Window/TestNextWindow/TestWindow.fxml");
		this.setWindowTitle("備品管理システム");
	}	
	
    @FXML
    public void onButtonClicked() {
    	count += 1;
    	
    	label_TEST.setText("押下回数：" + count + "回");

    	super.setPage(new FormController());
    }	
	
}
