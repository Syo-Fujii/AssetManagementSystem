package application.java.common;

public class AppConst {
	
	public static enum WindowSize {
	    NOMAL(1, "標準"),
	    WIDE(2, "拡大"),
	    PERIPHERAL(3, "周辺機器");

	    private final int id;
	    private final String label;

	    /** コンストラクタ */
	    WindowSize(int id, String label) {
	        this.id = id;
	        this.label = label;
	    }

	    public int getId() { return id; }
	    public String getlabel() { return label; }


	    public static WindowSize fromId(int id) {
	        for (WindowSize size : WindowSize.values()) {
	            if (size.getId() == id) {
	                return size;
	            }
	        }
	        // 見つからない場合は例外を投げるか、nullを返す
	        //throw new IllegalArgumentException("不正なIDです: " + id);
	        return null;
	    }	
	}
	
	public static final String FXML_PATH = "/application/resources/fxml/";

	public static final String CSS_PATH = "/application/resources/css/";



}
