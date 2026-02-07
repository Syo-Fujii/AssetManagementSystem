package application.Window.TestNextWindow;

import java.nio.file.Paths;

import application.Class.BaseFormPage;

public class Form2 extends BaseFormPage{

	public Form2() 
	{
		this.setfxmlFilePath("/application/Window/TestNextWindow/TestWindow.fxml");
		this.setWindowTitle("備品管理システム");
		
		String a = Paths.get("").toAbsolutePath().toString();
		int b = 0;
	}	
	
	
	
}
