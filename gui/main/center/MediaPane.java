package gui.main.center;

import baseobject.CustomList;
import baseobject.entity.Entity;
import control.reload.Reloadable;
import gui.component.clickmenu.ClickMenu;
import gui.stage.StageManager;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.InstanceCollector;
import tools.FileUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Logger;

public class MediaPane extends BorderPane implements InstanceCollector, Reloadable {
	private Canvas canvas;
	private ImageView gifPlayer;
	private VideoPlayer videoPlayer;
	private MediaPaneControls controls;
	
	private Image currentImage = null;
	private Entity currentCache = null;
	
	private Image placeholder = null;
	
	public MediaPane() {
	
	}
	
	public void init() {
		methodsToInvokeOnNextReload = new CustomList<>();
		
		canvas = new Canvas();
		gifPlayer = new ImageView();
		videoPlayer = VideoPlayer.create(canvas);
		controls = new MediaPaneControls(this, videoPlayer);
		
		gifPlayer.fitWidthProperty().bind(galleryPane.widthProperty());
		gifPlayer.fitHeightProperty().bind(galleryPane.heightProperty());
		
		this.setCenter(canvas);
		
		initEvents();
	}
	
	private CustomList<Method> methodsToInvokeOnNextReload;
	@Override
	public CustomList<Method> getMethodsToInvokeOnNextReload() {
		return methodsToInvokeOnNextReload;
	}
	public boolean reload() {
		Entity currentTarget = target.get();
		if (StageManager.getMainStage().isFullView() && currentTarget != null) {
			Logger.getGlobal().info(this.toString());
			
			switch (FileUtil.getFileType(currentTarget)) {
				case IMAGE:
					reloadAsImage(currentTarget);
					break;
				case GIF:
					reloadAsGif(currentTarget);
					break;
				case VIDEO:
					reloadAsVideo(currentTarget);
					break;
			}
			return true;
		}
		
		return false;
	}
	
	private void reloadAsImage(Entity currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != canvas) {
			this.setCenter(canvas);
		}
		
		double originWidth = 0;
		double originHeight = 0;
		double maxWidth = canvas.getWidth();
		double maxHeight = canvas.getHeight();
		
		boolean upScale = true;
		
		double resultWidth;
		double resultHeight;
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			
			try {
				File file = new File(currentCache.getPath());
				ImageInputStream iis = ImageIO.createImageInputStream(file);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
				
				if (readers.hasNext()) {
					//Get the first available ImageReader
					ImageReader reader = readers.next();
					reader.setInput(iis, true);
					
					originWidth = reader.getWidth(0);
					originHeight = reader.getHeight(0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (originWidth > 2 * maxWidth || originHeight > 2 * maxHeight) {
				currentImage = new Image("file:" + currentCache.getPath(), canvas.getWidth() * 1.5, canvas.getHeight() * 1.5, true, true);
			} else {
				currentImage = new Image("file:" + currentCache.getPath());
			}
		} else {
			originWidth = currentImage.getWidth();
			originHeight = currentImage.getHeight();
		}
		
		if (!upScale && originWidth < maxWidth && originHeight < maxHeight) {
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
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, maxWidth, maxHeight);
		gc.drawImage(currentImage, resultX, resultY, resultWidth, resultHeight);
	}
	private void reloadAsGif(Entity currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != gifPlayer) this.setCenter(gifPlayer);
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			currentImage = new Image("file:" + currentCache.getPath());
		}
		
		gifPlayer.setImage(currentImage);
	}
	private void reloadAsVideo(Entity currentTarget) {
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (VideoPlayer.hasLibs()) {
			controls.setVideoMode(true);
			
			if (currentCache == null || !currentCache.equals(currentTarget)) {
				currentCache = currentTarget;
				videoPlayer.start(currentTarget.getPath());
			} else {
				videoPlayer.resume();
			}
		} else {
			controls.setVideoMode(false);
			
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			if (placeholder == null) placeholder = createPlaceholder();
			gc.drawImage(placeholder, 0, 0, canvas.getWidth(), canvas.getHeight());
		}
	}
	
	private Image createPlaceholder() {
		Label label = new Label("Placeholder");
		label.setBackground(new Background(new BackgroundFill(Color.GRAY, null, null)));
		label.setWrapText(true);
		label.setFont(new Font(100));
		label.setAlignment(Pos.CENTER);
		
		int width = (int) canvas.getWidth();
		int height = (int) canvas.getHeight();
		
		label.setMinWidth(width);
		label.setMinHeight(height);
		label.setMaxWidth(width);
		label.setMaxHeight(height);
		
		WritableImage img = new WritableImage(width, height);
		Scene scene = new Scene(new Group(label));
		scene.setFill(Color.GRAY);
		scene.snapshot(img);
		return img;
	}
	
	private void initEvents() {
		canvas.widthProperty().bind(galleryPane.widthProperty());
		canvas.heightProperty().bind(galleryPane.heightProperty());
		
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		ClickMenu.install(this, MouseButton.SECONDARY, ClickMenu.StaticInstance.ENTITY);
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getClickCount() % 2 != 0) {
					requestFocus();
				} else {
					StageManager.getMainStage().swapViewMode();
					reload.doReload();
				}
				ClickMenu.hideAll();
			}
		});
	}
	
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
	public MediaPaneControls getControls() {
		return controls;
	}
}
