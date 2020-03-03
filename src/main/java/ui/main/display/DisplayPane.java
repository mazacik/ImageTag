package ui.main.display;

import base.entity.Entity;
import control.Select;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import misc.FileUtil;
import ui.custom.ClickMenu;
import ui.custom.HoverMenu;
import ui.main.gallery.GalleryPane;
import ui.main.stage.MainStage;
import ui.node.textnode.TextNode;
import ui.node.textnodewitharrow.ArrowTextNodeTemplates;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class DisplayPane extends BorderPane {
	private Canvas canvas;
	private ImageView gifPlayer;
	private VideoPlayer videoPlayer;
	private Controls controls;
	
	private Image currentImage = null;
	private Entity currentCache = null;
	
	private TextNode nodeNoLibsError;
	
	public boolean reload() {
		Entity currentTarget = Select.getTarget();
		if (!MainStage.getMainScene().isViewGallery() && currentTarget != null) {
			switch (currentTarget.getMediaType()) {
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
		
		//todo maybe move shit like this to separate private methods for clarity and ease-of-access?
		if (currentCache == null || !currentCache.equals(currentTarget)) {
			try {
				File file = new File(FileUtil.getFileEntity(currentTarget));
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
				
				currentCache = currentTarget;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String entityFilePath = FileUtil.getFileEntity(currentTarget);
			if (originWidth > 2 * maxWidth || originHeight > 2 * maxHeight) {
				currentImage = new Image("file:" + entityFilePath, canvas.getWidth() * 1.5, canvas.getHeight() * 1.5, true, true);
			} else {
				currentImage = new Image("file:" + entityFilePath);
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
			currentImage = new Image("file:" + FileUtil.getFileEntity(currentCache));
		}
		
		gifPlayer.setImage(currentImage);
	}
	private void reloadAsVideo(Entity currentTarget) {
		if (this.getCenter() != canvas) this.setCenter(canvas);
		
		if (VideoPlayer.hasLibs()) {
			controls.setVideoMode(true);
			
			if (currentCache == null || !currentCache.equals(currentTarget)) {
				currentCache = currentTarget;
				videoPlayer.start(FileUtil.getFileEntity(currentCache));
			} else {
				videoPlayer.resume();
			}
		} else {
			controls.setVideoMode(false);
			this.setCenter(nodeNoLibsError);
		}
	}
	
	private void initEvents() {
		canvas.widthProperty().bind(GalleryPane.getInstance().widthProperty());
		canvas.heightProperty().bind(GalleryPane.getInstance().heightProperty());
		
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> reload());
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> reload());
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				ClickMenu.hideMenus();
				HoverMenu.hideMenus();
				
				if (event.getClickCount() % 2 != 0) {
					requestFocus();
				} else {
					MainStage.getMainScene().viewGallery();
					Reload.start();
				}
			}
		});
		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY
				, ArrowTextNodeTemplates.FILE.get()
				, ArrowTextNodeTemplates.SELECTION.get()
		);
	}
	
	public void interruptVideoPlayer() {
		if (controls.isShowing()) controls.hide();
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
	
	private DisplayPane() {
		canvas = new Canvas();
		gifPlayer = new ImageView();
		videoPlayer = VideoPlayer.create(canvas);
		controls = new Controls(this, videoPlayer);
		
		gifPlayer.fitWidthProperty().bind(GalleryPane.getInstance().widthProperty());
		gifPlayer.fitHeightProperty().bind(GalleryPane.getInstance().heightProperty());
		
		nodeNoLibsError = new TextNode("Error: No VLC Library", false, false, false, false) {{
			this.setFont(new Font(64));
			this.minWidthProperty().bind(canvas.widthProperty());
			this.minHeightProperty().bind(canvas.heightProperty());
		}};
		
		this.setCenter(canvas);
		
		this.initEvents();
	}
	private static class Loader {
		private static final DisplayPane INSTANCE = new DisplayPane();
	}
	public static DisplayPane getInstance() {
		return Loader.INSTANCE;
	}
}
