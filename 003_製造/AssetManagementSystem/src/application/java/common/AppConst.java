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

	/**
	 * Enum SQL実行結果
	 */
	public static enum ExcuteQueryResultStatus {
	    SUCCESS(1, "成功"),
	    UNSESSION(2, "SQLセッション未生成"),
	    NO_ROWS_AFFECTED(3, "影響件数0件(対象が存在しない)"),
		EXCEPTION(4, "例外発生"),
		NONE(5, "なにもしない");

	    private final int id;
	    private final String label;

	    /** コンストラクタ */
	    ExcuteQueryResultStatus(int id, String label) {
	        this.id = id;
	        this.label = label;
	    }

	    public int getId() { return id; }
	    public String getlabel() { return label; }

	    /**
	     * 数値(引数)から一致するEnum要素を返す
	     * @param state
	     * @return 一致するEnum要素
	     */
	    public static ExcuteQueryResultStatus fromId(int id) {
	        for (ExcuteQueryResultStatus size : ExcuteQueryResultStatus.values()) {
	            if (size.getId() == id) {
	                return size;
	            }
	        }
	        // 見つからない場合は例外を投げるか、nullを返す
	        //throw new IllegalArgumentException("不正なIDです: " + id);
	        return null;
	    }	
	}	

	/**	貸出状態:可 */
	public static final Integer LOANSTATE_AVAILABLE = 1;

	/**	貸出状態:貸出中 */
	public static final Integer LOANSTATE_CHECKED_OUT = 2;
	
	
	/** 数値未設定 初期値 */
	public static final int UNSET_NUMBER_VALUE = -1;
	
	public static final String FXML_PATH = "/application/resources/fxml/";

	public static final String CSS_PATH = "/application/resources/css/";

	/**
	 * 明細行付き行データ(クラス:record)
	 * @param <T>
     * @brief T：[model](1行データのクラス)にrowNum：明細行をセットでRecord型に保持する。<br>
	 */
	public record addRowNumData<T extends BaseTableViewModel>(int rowNum, T model) {};
	
	/**
	 * 明細検査結果用データ(クラス:record)
     * @brief 明細行などを検査した場合の結果。<br>
     * 結果:[Boolean],<br>
     * メッセージ(ERROR)を表示するか:[Boolean],<br>
     * 行番号:[int],<br>
     * 列番号:[int],<br>
     * メッセージ:[String]<br>
     * を保持する。<br>
	 */
	public record rowCheckResultData(Boolean result, Boolean isShowMsgBox, int rowNum, int colNo, String message) {};
}
