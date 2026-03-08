package application.java.common;

import java.util.Arrays;

import application.java.base.BaseTableViewModel;

public class AppConst {
	
	/**
	 * Enum 画面サイズ
	 */
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

	/**
	 * Enum 貸出状態
	 */
	public static enum LoanStatus {
		AVAILABLE(1, "可"),
	    CHECKED_OUT(2, "貸出中"),
	    UNKNOWN(3, "不明"),
	    UNAVAILABLE(4,"不可");

	    private final int state;
	    private final String label;

	    /** コンストラクタ */
	    LoanStatus(int state, String label) {
	        this.state = state;
	        this.label = label;
	    }

	    public int getState() { return state; }
	    public String getlabel() { return label; }

	    /**
	     * 数値(引数)から一致するEnum要素を返す
	     * @param state
	     * @return 一致するEnum要素
	     */
	    public static LoanStatus fromState(int state) {
	        return Arrays.stream(LoanStatus.values())
	                 .filter(s -> s.getState() == state)
	                 .findFirst()  // 最初に見つかったものを取得
	                 .orElse(null); // 見つからなければ null を返す
	    }	
	}	
	
	/**	貸出状態:可 */
	public static final Integer LOANSTATE_AVAILABLE = 1;
	
	/** 数値未設定 初期値 */
	public static final int UNSET_NUMBER_VALUE = -1;
	
	public static final String FXML_PATH = "/application/resources/fxml/";

	public static final String CSS_PATH = "/application/resources/css/";

	/**
	 * 
	 * @param <T>
	 */
	public record addRowNumData<T extends BaseTableViewModel>(int rowNum, T model) {};

}
