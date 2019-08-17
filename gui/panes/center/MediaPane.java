package application.gui.panes.center;

import application.database.object.DataObject;
import application.gui.panes.NodeBase;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MediaPane extends BorderPane implements NodeBase {
	private final Canvas canvas;
	private final ImageView gifPlayer;
	private final VideoPlayer videoPlayer;
	private final MediaPaneControls controls;
	
	private Image currentImage = null;
	private DataObject currentCache = null;
	
	private Image placeholder = null;
	
	private boolean needsReload;
	
	public MediaPane() {
		needsReload = false;
		
		canvas = new Canvas();
		gifPlayer = new ImageView();
		videoPlayer = VideoPlayer.create(canvas);
		controls = new MediaPaneControls(canvas, videoPlayer);
		
		GalleryPane galleryPane = Instances.getGalleryPane();
		
		gifPlayer.fitWidthProperty().bind(galleryPane.widthProperty());
		gifPlayer.fitHeightProperty().bind(galleryPane.heightProperty());
		
		this.setCenter(canvas);
		
		initEvents();
	}
	
	public boolean reload() {
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (Stages.getMainStage().isFullView() && currentTarget != null) {
			switch (currentTarget.getFileType()) {
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
	
	private void reloadAsImage(DataObject currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			currentImage = new Image("file:" + currentCache.getPath());
		}
		
		double imageWidth = currentImage.getWidth();
		double imageHeight = currentImage.getHeight();
		double maxWidth = canvas.getWidth();
		double maxHeight = canvas.getHeight();
		
		boolean upScale = true;
		
		double resultWidth;
		double resultHeight;
		
		//noinspection ConstantConditions
		if (!upScale && imageWidth < maxWidth && imageHeight < maxHeight) {
			// image is smaller than canvas, upscaling is off
			resultWidth = imageWidth;
			resultHeight = imageHeight;
		} else {
			// scale image to fit width
			resultWidth = maxWidth;
			resultHeight = imageHeight * maxWidth / imageWidth;
			
			// if scaled image is too tall, scale to fit height instead
			if (resultHeight > maxHeight) {
				resultHeight = maxHeight;
				resultWidth = imageWidth * maxHeight / imageHeight;
			}
		}
		
		double resultX = maxWidth / 2 - resultWidth / 2;
		double resultY = maxHeight / 2 - resultHeight / 2;
		
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.drawImage(currentImage, resultX, resultY, resultWidth, resultHeight);
	}
	private void reloadAsGif(DataObject currentTarget) {
		controls.setVideoMode(false);
		
		if (this.getCenter() != gifPlayer) this.setCenter(gifPlayer);
		
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			currentCache = currentTarget;
			currentImage = new Image("file:" + currentCache.getPath());
		}
		
		gifPlayer.setImage(currentImage);
	}
	private void reloadAsVideo(DataObject currentTarget) {
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (VideoPlayer.doLibsExist()) {
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
		canvas.widthProperty().bind(Instances.getGalleryPane().widthProperty());
		canvas.heightProperty().bind(Instances.getGalleryPane().heightProperty());
		
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		this.setOnMouseClicked(event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (event.getClickCount() % 2 != 0) {
						requestFocus();
					} else {
						Stages.getMainStage().swapViewMode();
						Instances.getReload().doReload();
					}
					Instances.getClickMenuData().hide();
					break;
				case SECONDARY:
					Instances.getClickMenuData().show(this, event.getScreenX(), event.getScreenY());
					break;
			}
		});
	}
	
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
	public MediaPaneControls getControls() {
		return controls;
	}
}
