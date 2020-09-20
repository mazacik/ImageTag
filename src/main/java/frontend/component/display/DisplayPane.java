package frontend.component.display;

import backend.entity.Entity;
import backend.misc.Direction;
import backend.misc.FileUtil;
import backend.misc.Settings;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.menu.ClickMenu;
import frontend.node.menu.ListMenu;
import frontend.node.textnode.TextNode;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import main.Main;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DisplayPane extends StackPane {
	private final ImageView gifPlayer;
	private final VideoPlayer videoPlayer;
	private final Controls controls;
	
	private final StackPane canvasPane;
	private final BorderPane holderPane;
	
	private final TextNode nodeNoLibsError;
	
	private Image currentImage = null;
	private Entity currentEntity = null;
	
	private TextNode text = null;
	
	public DisplayPane() {
		canvasPane = new StackPane(canvas1, canvas2);
		holderPane = new BorderPane(canvasPane);
		
		fadeCanvas1 = new FadeTransition(transitionAnimationLength, canvas1);
		fadeCanvas2 = new FadeTransition(transitionAnimationLength, canvas2);
		
		gifPlayer = new ImageView();
		videoPlayer = VideoPlayer.create(canvas1);
		
		nodeNoLibsError = new TextNode("Error: libvlc.dll and libvlccore.dll not found.\nVLC Media Player is not installed.", false, false, false, false) {{
			this.setFont(new Font(64));
			this.minWidthProperty().bind(canvas1.widthProperty());
			this.minHeightProperty().bind(canvas1.heightProperty());
		}};
		
		controls = new Controls(videoPlayer);
	}
	
	public void initialize() {
		fadeCanvas1.setFromValue(0);
		fadeCanvas1.setToValue(1);
		
		fadeCanvas2.setFromValue(0);
		fadeCanvas2.setToValue(1);
		
		//		gifPlayer.fitWidthProperty().bind(UserInterface.getCenterPane().getGalleryPane().widthProperty());
		//		gifPlayer.fitHeightProperty().bind(UserInterface.getCenterPane().getGalleryPane().heightProperty());
		
		/* Controls */
		controls.setVisible(false);
		//		controls.prefWidthProperty().bind(this.widthProperty());
		
		PauseTransition autoHideDelay = new PauseTransition();
		autoHideDelay.setOnFinished(event -> controls.setVisible(false));
		
		holderPane.setOnMouseMoved(event -> {
			if (!controls.isVisible()) controls.setVisible(true);
			autoHideDelay.playFromStart();
		});
		controls.setOnMouseEntered(event -> autoHideDelay.stop());
		controls.setOnMouseExited(event -> autoHideDelay.playFromStart());
		/* Controls */
		
		text = new TextNode("TEST");
		text.setFont(DecoratorUtil.getFont());
		StackPane.setAlignment(text, Pos.TOP_LEFT);
		
		this.getChildren().add(holderPane);
		this.getChildren().add(controls);
		//this.getChildren().add(text);
		this.setAlignment(Pos.TOP_CENTER);
		
		this.initEvents();
	}
	
	private final Duration transitionAnimationLength = new Duration(1000);
	private final Canvas canvas1 = new Canvas();
	private final Canvas canvas2 = new Canvas();
	private Canvas activeCanvas;
	
	private final FadeTransition fadeCanvas1;
	private final FadeTransition fadeCanvas2;
	private void prepareCanvas() {
		if (holderPane.getCenter() != canvasPane) {
			holderPane.setCenter(canvasPane);
		}
		
		if (Main.SELECT.getSlideshow().isRunning()) {
			if (activeCanvas == null || activeCanvas == canvas2) {
				activeCanvas = canvas1;
			} else {
				activeCanvas = canvas2;
			}
		} else {
			if (activeCanvas == null || activeCanvas == canvas2) {
				activeCanvas = canvas1;
				fadeCanvas1.stop();
				fadeCanvas2.stop();
				canvas1.setOpacity(1.0);
				canvas2.setOpacity(0.0);
			}
		}
	}
	private void transitionCanvas() {
		if (Main.SELECT.getSlideshow().isRunning()) {
			if (activeCanvas == canvas1) {
				fadeIn(fadeCanvas1);
				fadeOut(fadeCanvas2);
			} else {
				fadeIn(fadeCanvas2);
				fadeOut(fadeCanvas1);
			}
		}
	}
	
	private void fadeIn(FadeTransition fadeTransition) {
		fadeTransition.pause();
		fadeTransition.setRate(1);
		fadeTransition.play();
	}
	private void fadeOut(FadeTransition fadeTransition) {
		fadeTransition.pause();
		fadeTransition.setRate(-1);
		fadeTransition.play();
	}
	
	public boolean reload() {
		Entity currentTarget = Main.SELECT.getTarget();
		if (UserInterface.getCenterPane().isViewDisplay() && currentTarget != null) {
			switch (currentTarget.getType()) {
				case IMG:
					reloadAsImage(currentTarget);
					break;
				case GIF:
					reloadAsGif(currentTarget);
					break;
				case VID:
					reloadAsVideo(currentTarget);
					break;
				default:
					break;
			}
			return true;
		}
		
		return false;
	}
	private void reloadAsImage(Entity entity) {
		controls.setVideoMode(false);
		
		this.prepareCanvas();
		
		double originWidth = 0;
		double originHeight = 0;
		double maxWidth = activeCanvas.getWidth();
		double maxHeight = activeCanvas.getHeight();
		
		double resultWidth;
		double resultHeight;
		
		if (currentEntity == null || !currentEntity.equals(entity)) {
			try {
				File file = new File(FileUtil.getFileEntity(entity));
				if (!file.exists()) return;
				ImageInputStream iis = ImageIO.createImageInputStream(file);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
				
				if (readers.hasNext()) {
					//Get the first available ImageReader
					ImageReader reader = readers.next();
					reader.setInput(iis, true);
					
					originWidth = reader.getWidth(0);
					originHeight = reader.getHeight(0);
				}
				
				iis.close();
				
				currentEntity = entity;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String entityFilePath = FileUtil.getFileEntity(entity);
			if (originWidth > 2 * maxWidth || originHeight > 2 * maxHeight) {
				currentImage = new Image("file:" + entityFilePath, activeCanvas.getWidth() * 1.5, activeCanvas.getHeight() * 1.5, true, true);
			} else {
				currentImage = new Image("file:" + entityFilePath);
			}
		} else {
			originWidth = currentImage.getWidth();
			originHeight = currentImage.getHeight();
		}
		
		if (!Settings.DISPLAY_UPSCALE.getBoolean() && originWidth < maxWidth && originHeight < maxHeight) {
			// image is smaller than canvas or upscaling is off
			resultWidth = originWidth;
			resultHeight = originHeight;
		} else {
			// scale image to fit width
			resultWidth = maxWidth;
			resultHeight = originHeight * maxWidth / originWidth;
			
			// if scaled image is too tall, scale to fit height instead
			if (resultHeight > maxHeight) {
				resultHeight = maxHeight;
				resultWidth = originWidth * maxHeight / originHeight;
			}
		}
		
		double resultX = maxWidth / 2 - resultWidth / 2;
		double resultY = maxHeight / 2 - resultHeight / 2;
		
		text.setTranslateX(resultX);
		text.setTranslateY(resultY);
		
		GraphicsContext gc = activeCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, maxWidth, maxHeight);
		gc.drawImage(currentImage, resultX, resultY, resultWidth, resultHeight);
		
		this.transitionCanvas();
	}
	private void reloadAsGif(Entity entity) {
		controls.setVideoMode(false);
		
		if (holderPane.getCenter() != gifPlayer) holderPane.setCenter(gifPlayer);
		
		if (currentEntity == null || !currentEntity.equals(entity)) {
			currentEntity = entity;
			currentImage = new Image("file:" + FileUtil.getFileEntity(currentEntity));
		}
		
		gifPlayer.setImage(currentImage);
	}
	private void reloadAsVideo(Entity entity) {
		if (holderPane.getCenter() != canvas1) holderPane.setCenter(canvas1);
		
		if (VideoPlayer.hasLibs()) {
			controls.setVideoMode(true);
			
			if (currentEntity == null || !currentEntity.equals(entity)) {
				currentEntity = entity;
				videoPlayer.start(FileUtil.getFileEntity(currentEntity));
			} else {
				videoPlayer.resume();
			}
		} else {
			controls.setVideoMode(false);
			holderPane.setCenter(nodeNoLibsError);
		}
	}
	
	private void initEvents() {
		canvas1.widthProperty().bind(UserInterface.getCenterPane().widthProperty());
		canvas1.heightProperty().bind(UserInterface.getCenterPane().heightProperty());
		
		canvas1.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas1.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		canvas2.widthProperty().bind(UserInterface.getCenterPane().widthProperty());
		canvas2.heightProperty().bind(UserInterface.getCenterPane().heightProperty());
		
		canvas2.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas2.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		holderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() % 2 != 0) {
					holderPane.requestFocus();
				} else {
					UserInterface.getCenterPane().swapCurrentPane();
					Reload.start();
				}
			}
		});
		
		ClickMenu.install(holderPane, Direction.NONE, MouseButton.SECONDARY, ListMenu.Preset.ENTITY);
	}
	
	public void interruptVideoPlayer() {
		if (videoPlayer != null && videoPlayer.isPlaying()) videoPlayer.pause();
	}
	public void disposeVideoPlayer() {
		if (videoPlayer != null) videoPlayer.dispose();
	}
	
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
	public Controls getControls() {
		return controls;
	}
}
