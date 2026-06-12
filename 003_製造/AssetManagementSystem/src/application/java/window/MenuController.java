package application.java.window;

import java.util.ArrayList;
import java.util.List;

import application.java.base.BaseFormPage;
import application.java.common.AppConst;
import application.java.common.AppUtil;
import application.java.manager.FileManager;
import application.java.manager.LogManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 備品管理画面メニュー (MAC Finder風)
 * @brief ScreenBuilder・CSS学習の為、MAC(finder)風デザインにする。<br>
 * [hover]などのアニメーションはなるべくCSSをもちいるようにする。<br>
 * Window(枠:stage)の切替イベントを追加・適用する。<br>
 * Window枠の透明化(非表示)に伴い、既存のウィンドウバーを操作できるボタン群を追加<br>
 * 背景画像の取り込み/COVER・スライドショー機能を追加する<br>
 * ※ 当該アプリでは、各画面のControllerに対するCSSをControllerクラスで定義・<br>
 * Page(scene)生成時にセットすることで対応させている。<br>
 * fxml(Screenbuilder)にて、CSSを設定している場合、アプリのLoad時にfxmlのcssパスも参照することになり<br>
 * 警告が発生することがある。(多分、Javaとfxmlで相対PATHの開始ディレクトリィが違う？)<br>
 * 警告に問題ある場合は、fxml側のCSS参照を削除すること。(アプリではControllerクラスの設定だけで有効化されます。)<br>
 * ScreenBuilderにて、反映させる(デザイナでの見た目)場合は、 fxml(Screenbuilder)にて、CSSを設定してください。
 */
public class MenuController extends BaseFormPage {

    /**
     * 内部クラス:背景画像(Image)保持クラス
     */
    private class ImageData {
        @SuppressWarnings("unused")
		private final int index;
        private final String path;
        private Image image;

        /**
         * コンストラクタ
         * @param index int 配列のインデックス
         * @param path String 画像が保存されているパス
         * @param image javafx.scene.image.Image Image画像イメージ
         */
        public ImageData(int index, String path, Image image) {
            this.index = index;
            this.path = path;
            this.image = image;
        }
        
        /**
         * [画像イメージ]設定
         * @param image Image 設定する画像イメージ
         */
        public void setImage(Image image) {
        	this.image = null;
        	this.image = image;
        }
        
    	/**
    	* [画像イメージ]取得
    	* @return image Image 設定する画像イメージ
    	*/          
        public Image getImage() { return image; }

    	/**
    	* [画像パス]取得
    	* @return path 画像が保存されているパス
    	*/          
        public String getPath() { return path; }
        
        /**
         * 画像イメージ保持判定
         * @return Boolean 判定結果
         * @brief 画像イメージを保持している場合、[真]
         */
        public Boolean isImageCached() { return (this.image != null) ? true : false; }
    }	
	
	
	private final String FORM_NAME = "備品管理システム メニュー画面";
	private final Integer CLIP_RECT_ANGLE = 12;
	private final String IMAGE_FORM_FOLDER = "menuWindow";
	
	private Boolean isBgImageCover = false;

	private Timeline imageSlideShowTimeline = null;
	private ChangeListener<Number> resizeHandler = null;
	private FadeTransition imageFade = null;
	private FadeTransition masterPaneFade = null;

	private List<ImageData> imagesCache = null;
	
	private double xOffset = 0;
    private double yOffset = 0;
	private int imageIndex = 0;
    
    @FXML private HBox titleBar;
    @FXML private Label lbl_title;
    
    @FXML private AnchorPane imagePane;
    @FXML private ImageView backImage;
    @FXML private ImageView frontImage;

    @FXML private AnchorPane masterPane;    
    
    @FXML private ToggleButton loanlist_button;
    @FXML private ToggleButton masterMainte_Button;

    
    // 閉じるボタンのアクション
    @FXML
    public void handleClose(MouseEvent event) {
        // 押されたボタンからStage（ウィンドウ）を見つけて閉じる
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    // 最小化ボタンのアクション
    @FXML
    public void handleMinimize(MouseEvent event) {
        // ウィンドウを最小化する
        ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true);
    }

	
	/** 
	 * コンストラクタ
	 * @throws Exception 
	 */
	public MenuController() throws Exception 
	{
		LogManager.writeInfo("[" + FORM_NAME + "] ： 初期化処理"); 
		
		// 背景画像の取得(PATH)
		setBackGroundImagePaths();
		
		// MAC風Window設定
		this.setWindowColor(Color.TRANSPARENT);
		this.setIsUseWindowFrame(false);
		
		this.setWindowTitle("備品管理システム");

		this.setfxmlFilePath(AppUtil.MakeFxmlFilePath("Menu"));
		this.setCssFile(AppUtil.MakeCssFilePath("MenuMacFinderStyle"));
		
		this.setPageTitle(FORM_NAME);
	}
	public MenuController(Boolean isImgCover)  throws Exception 
	{
		this();
		this.isBgImageCover = isImgCover;
	}
	
    /**
     * 画面(scene)初期化イベント
     * .NET Load相当 
     * 画面の表示前、ノードが配置された段階で実行
     */
    @FXML
    public void initialize() {
    	lbl_title.setText(FORM_NAME);
    	
    	// 画像用パネル下部設定
    	applyImagePaneBottomRoundedClip();
    	
    	onTitleBarMousePressEvent();

        Platform.runLater(() -> {
        	if (imagesCache != null && imagesCache.size() > 0) {
            	startBackgroundSlideShowFade(frontImage, backImage, 5);
            }
        });
    }

    /**
     * [貸出業務]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onLoanListButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [貸出業務]ボタン押下"); 
    		
        	// 備品一覧画面に切替
        	super.setPage(new application.java.window.inventoryLoans.inventoryList.FormController());
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： 備品一覧画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }        
   
    /**
     * [棚卸業務]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onVerifyLocationButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [棚卸業務]ボタン押下"); 
    		
        	// 棚卸画面に切替
        	super.setPage(new application.java.window.verifyLocation.performInventory.FormController());
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： 棚卸画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }    

    /**
     * [マスタメンテナンス]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @SuppressWarnings("unused")
	@FXML
    public void onMasterMainteButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [マスタメンテナンス]ボタン押下"); 
        	
            FadeTransition fade = new FadeTransition(Duration.millis(300), masterPane);
    		if (masterMainte_Button.isSelected()) {
                // 表示する時
            	masterPane.setManaged(true);
            	masterPane.setVisible(true);

            	fade.setFromValue(0.0);
            	fade.setToValue(1.0);
            } else {
                // 隠す時
            	fade.setFromValue(1.0);
            	fade.setToValue(0.0);
            	
            	fade.setOnFinished( (e) ->
            	{
                	masterPane.setVisible(false);
                	masterPane.setManaged(false);
                });
            }
    		
    		fade.play();
    		this.masterPaneFade = fade;
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： マスタメンテPage表示中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }    

    /**
     * [備品分類マスタ メンテナンス]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onStockTypeMasterButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [備品分類マスタ メンテナンス]ボタン押下"); 
    		
        	// 備品分類マスタ メンテナンス画面に切替
        	super.setPage(new application.java.window.masterMaintenance.stockTypeMaster.FormController());
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： メンテナンス画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }

    /**
     * [備品マスタ メンテナンス]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onStockMasterButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [備品マスタ メンテナンス]ボタン押下"); 
    		
        	// 備品マスタ メンテナンス明細画面に切替
        	super.setPage(new application.java.window.masterMaintenance.stockMaster.details.FormController());
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： メンテナンス画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }         
    
    /**
     * [社員マスタ メンテナンス]ボタン 押下イベント 
     * エラーハンドリングは[JavaFX の UIスレッド（Event Dispatch Thread）]となる。<br>
     */
    @FXML
    public void onStaffMasterButtonClicked() {
    	try {
    		LogManager.writeInfo("[" + FORM_NAME + "] ： [社員マスタ メンテナンス]ボタン押下"); 
    		
        	// 社員マスタ メンテナンス画面に切替
        	super.setPage(new application.java.window.masterMaintenance.staffMaster.FormController());
    		
    	} catch (Exception ex) {
    		String title = "[" + FORM_NAME + "] ： メンテナンス画面遷移中にエラーが発生しました";
    		LogManager.showAndWriteError(title, ex);
    	}
    }        

    /**
     * (page)画面終了 
     * @brief スライドシューに関するオブジェクトを破棄する<br>
     */
    @Override
    public void pageDispose() {
    	if (isBgImageCover) {
            imagePane.widthProperty().removeListener(resizeHandler);
            imagePane.heightProperty().removeListener(resizeHandler);  
    	}
  
        if (imageFade != null) {
        	imageFade.stop();
        	imageFade.setOnFinished(null);
        	imageFade = null;
        }
        
    	if (this.imageSlideShowTimeline != null) {
        	this.imageSlideShowTimeline.stop();
            this.imageSlideShowTimeline = null;
            LogManager.writeInfo("スライドショーのTimelineを停止しました");
        }

        if (masterPaneFade != null) {
        	masterPaneFade.stop();
        	masterPaneFade.setOnFinished(null);
        	masterPaneFade = null;
        }
        
        for (ImageData data : imagesCache) { data.setImage(null); }
        // リスト自体を空にする
        imagesCache.clear();

        frontImage.setImage(null);
        backImage.setImage(null);
    }        
    
    /**
     * 背景画像(Image)取得
     * @param index int 取得する画像インデックス
     * @param targetWidth double 対象画像幅
     * @param targetHeight double 対象画像高
     * @param maxWidth double 最大画像幅
     * @param maxHeight double 最大画像高
     * @return 画像(Image)
     */
    private Image getBackGroundImage(String path, double targetWidth, double targetHeight, double maxWidth, double maxHeight) {
        double w = (targetWidth <= 0) ? imagePane.getPrefWidth() : targetWidth;
        double h = (targetHeight <= 0) ? imagePane.getPrefHeight() : targetHeight;
        
        // 高解像度化（1.5倍）
        double loadW = w * 1.5;
        double loadH = h * 1.5;
         
        loadW = (loadW > maxWidth) ? maxWidth : loadW;
        loadH = (loadH > maxHeight) ? maxHeight : loadH;
        
    	return new Image(path, loadW, loadH, true, true, false); 
    }    
 
    /**
     * キャッシュ画像(背景画像:Image)取得
     * @param index int 取得する画像インデックス
     * @param targetWidth double 対象画像幅
     * @param targetHeight double 対象画像高
     * @param maxWidth double 最大画像幅
     * @param maxHeight double 最大画像高
     * @return 背景画像(Image)
     */
    private Image getCachedImage(int index, double targetWidth, double targetHeight, double maxWidth, double maxHeight) {
    	
    	if (index < 0 || index >= this.imagesCache.size() ) { return null; }

    	ImageData cacheData = this.imagesCache.get(index);

    	if ( !cacheData.isImageCached() )
    	{
    		cacheData.setImage(getBackGroundImage(cacheData.getPath(), targetWidth, targetHeight, maxWidth, maxHeight));
    	}
    
    	return cacheData.getImage();
    }    
    
    /**
     * finder風タイトルバー用画面移動イベント
     * @brief Window枠を無効(不可視)化しているため、コンテンツ枠の移動をWindow枠に偽装する
     */
    private void onTitleBarMousePressEvent() {
    	
    	// タイトルバーをマウスで押したときの処理
        titleBar.setOnMousePressed(
        		( event ) -> 
        		{
        			xOffset = event.getSceneX();
        			yOffset = event.getSceneY();
        		});

        // マウスをドラッグしたときの処理
        titleBar.setOnMouseDragged(
        		( event ) -> 
        		{
        			Stage stage = (Stage) titleBar.getScene().getWindow();
        			stage.setX(event.getScreenX() - xOffset);
        			stage.setY(event.getScreenY() - yOffset);
        		});
    }    

    /**
     * 画像用パネル下部設定(下部の角を丸める)
     */
    private void applyImagePaneBottomRoundedClip() {
    	
        // 描画領域を角丸にするための「切り抜き枠」を作成
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(imagePane.widthProperty());
        clip.heightProperty().bind(imagePane.heightProperty());
        
        // 角丸の直径（半径20pxなら40）
        clip.setArcWidth(CLIP_RECT_ANGLE * 2);
        clip.setArcHeight(CLIP_RECT_ANGLE * 2);

        // 下側だけ丸く見せるためのトリック：
        // 切り抜き枠を少し上にずらすことで、上の角丸を画面外へ追い出し、下だけ残す
        clip.setTranslateY(- CLIP_RECT_ANGLE); 
        // その分、枠の高さだけを伸ばす
        clip.heightProperty().bind(imagePane.heightProperty().add(CLIP_RECT_ANGLE));

        imagePane.setClip(clip);
    }
    
    /**
     * スライドショー機能：背景画像を一定時間で切り替える
     * @param node 対象のAnchorPane
     * @param intervalSeconds int 切り替え間隔（秒）
     */
    @SuppressWarnings("unused")
	private void startBackgroundSlideShow(AnchorPane node, int intervalSeconds) {
    	// 指定時間ごとに実行される処理を定義
        KeyFrame keyFrame = 
        		new KeyFrame(
        				Duration.seconds(intervalSeconds), 
        				( event ) -> 
        				{
        					 // 次の画像へ
        					imageIndex = (imageIndex + 1) % imagesCache.size();
        					
        					// CSSを書き換えて画像を差し替え
        					String imageUrl = getClass().getResource(this.imagesCache.get(imageIndex).getPath()).toExternalForm();
        					node.setStyle("-fx-background-image: url('" + imageUrl + "'); " +
        					              "-fx-background-size: cover; " +
        							      "-fx-background-radius: 0 0 20 20;");
        				});

        // タイマー（Timeline）の設定
    	imageSlideShowTimeline = new Timeline(keyFrame);
    	imageSlideShowTimeline.setCycleCount(Timeline.INDEFINITE);
    	imageSlideShowTimeline.play();
    }
    
    /**
     * スライドショー機能：背景画像を一定時間で切り替える + 切替時Fadeを行う
     * @param frontNode ImageView Fade用前面画像(透過)
     * @param backNode ImageView 表示画像
     * @param intervalSeconds int 切り替え間隔（秒）
     */
    @SuppressWarnings("unused")
	private void startBackgroundSlideShowFade(ImageView frontNode, ImageView backNode, int intervalSeconds) {
        // メインスクリーン(Stage)の情報を取得
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double maxWidth = screenBounds.getWidth();
        double maxHeight = screenBounds.getHeight();    	
 
        double paneW = imagePane.getWidth() > 0 ? imagePane.getWidth() : imagePane.getPrefWidth();
        double paneH = imagePane.getHeight() > 0 ? imagePane.getHeight() : imagePane.getPrefHeight();
    	
    	if (isBgImageCover) {
    		// 親パネルのサイズが変わったら画像を切り抜く
            resizeHandler = (obs, old, val) -> {
                double currW = imagePane.getWidth();
                double currH = imagePane.getHeight();
                
                applyCenterCrop(backNode, currW, currH);
                
                if (frontNode.getOpacity() > 0) {
                    applyCenterCrop(frontNode, currW, currH);
                }
            };
            imagePane.widthProperty().addListener(resizeHandler);
            imagePane.heightProperty().addListener(resizeHandler);  
    	}
        
    	// 最初の画像セット(Back)
        backNode.setImage( getCachedImage(0, paneW, paneH, maxWidth, maxHeight) );
        applyCenterCrop(backNode, imagePane.getWidth(), imagePane.getHeight());

    	KeyFrame keyFrame =
    			new KeyFrame(
    					Duration.seconds(intervalSeconds),
    					( event ) -> 
    					{
    						// 次の画像のインデックス
    						imageIndex = (imageIndex + 1) % imagesCache.size();
    						Image nextImage = getCachedImage(imageIndex, paneW, paneH, maxWidth, maxHeight); 

    						// フェードアニメーションの実行
    						/* 上の画像に次のセットして、透明から不透明へ */
    						// 既に設定している場合、解放する(GC対策:スリープ時等) 
    						frontNode.setImage(null);     						
    						frontNode.setImage(nextImage);
    						
    						if (isBgImageCover) { applyCenterCrop(frontNode, imagePane.getWidth(), imagePane.getHeight()); }
    						
    						frontNode.setOpacity(0);
    						
    						FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), frontNode);
    						fadeIn.setFromValue(0.0);
    						fadeIn.setToValue(1.0);
    						
    						fadeIn.setOnFinished(
    								( e ) -> 
    								{
    		    						// 既に設定している場合、解放する(GC対策:スリープ時等) 
    									backNode.setImage(null);    
    									// アニメーション終了後、下の画像も更新して、上の画像は透明に戻す
    									backNode.setImage(nextImage);
    						            backNode.setViewport(frontNode.getViewport());
    						            backNode.setFitWidth(frontNode.getFitWidth());
    						            backNode.setFitHeight(frontNode.getFitHeight());
    									
    									frontNode.setOpacity(0);

    								    fadeIn.setOnFinished(null);
    								    fadeIn.stop();
    								});
    						
    						fadeIn.play();
    						this.imageFade = fadeIn;
       					});
    	
    	imageSlideShowTimeline = new Timeline(keyFrame);
    	imageSlideShowTimeline.setCycleCount(Timeline.INDEFINITE);
    	imageSlideShowTimeline.play();
    }
    
    /**
     * ImageViewを「Cover（中央で切り抜き）」状態に設定する
     */
    private void applyCenterCrop(ImageView imageView, double containerWidth, double containerHeight) {
        Image image = imageView.getImage();
        
        if (image == null || containerWidth <= 0 || containerHeight <= 0) { return; }

        // バインドを強制解除（念のため）
        imageView.fitWidthProperty().unbind();
        imageView.fitHeightProperty().unbind();        
        
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double containerRatio = containerWidth / containerHeight;
        double imageRatio = imageWidth / imageHeight;

        double viewportWidth, viewportHeight, x, y;

        if (imageRatio > containerRatio) {
            // 画像の方が横長：上下を基準に横をカット
            viewportHeight = imageHeight;
            viewportWidth = imageHeight * containerRatio;
            y = 0;
            x = (imageWidth - viewportWidth) / 2;
        } else {
            // 画像の方が縦長：左右を基準に縦をカット
            viewportWidth = imageWidth;
            viewportHeight = imageWidth / containerRatio;
            x = 0;
            y = (imageHeight - viewportHeight) / 2;
        }

        // 表示領域（窓）を設定
        imageView.setViewport(new Rectangle2D(x, y, viewportWidth, viewportHeight));

        // 画像比率維持無効化
        imageView.setPreserveRatio(false);
        
        // ImageView自体のサイズを親に合わせる
        imageView.setFitWidth(containerWidth);
        imageView.setFitHeight(containerHeight);
        
        image = null;
    }

    /**
     * 背景画像一覧取得
     * @throws Exception
     * @brief 指定フォルダより、対象ファイルのPATHをリストで取得する<br>
     */
    private void setBackGroundImagePaths() throws Exception {
    	
		// 背景画像一覧の取得
    	FileManager fileIo = new FileManager();
		String path = fileIo.
				resourcePathCombine( new String[] { 
								AppConst.SOURCE_FULL_PATH, 
								AppConst.IMAGE_FOLDER_PATH, 
								IMAGE_FORM_FOLDER });

		if ( !fileIo.existsFolder(path) ) { return; }

		String[] imagePaths = fileIo.
				getResourceUrlFullPathList(path).toArray( String[]::new );
		
		if( imagePaths == null || imagePaths.length < 1 ) { return; }

		int cacheIdx = 0;
		this.imagesCache = new ArrayList<>();
		for (String imagePath : imagePaths) 
		{
			imagesCache.add(new ImageData(cacheIdx, imagePath, null));
			cacheIdx++;
		}
    }
}
