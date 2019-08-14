package application.gui.stage;

import application.controller.Reload;
import application.controller.Select;
import application.controller.Target;
import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.panes.center.*;
import application.main.Instances;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.util.logging.Logger;

public class MainStage extends StageBase {
	private HBox mainHBox;
	
	public MainStage() {
		super(Instances.getToolbarPane());
		
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + SizeUtil.getGalleryIconSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + SizeUtil.getGalleryIconSize());
		this.setWidth(SizeUtil.getUsableScreenWidth());
		this.setHeight(SizeUtil.getUsableScreenHeight());
		this.setOnCloseRequest(event -> {
			Logger.getGlobal().info("application exit");
			VideoPlayer videoPlayer = Instances.getMediaPane().getVideoPlayer();
			if (videoPlayer != null) videoPlayer.dispose();
			Instances.getSettings().writeToDisk();
			Instances.getObjectListMain().writeToDisk();
			Instances.getTagListMain().writeDummyToDisk();
		});
		
		mainHBox = new HBox(Instances.getFilterPane(), Instances.getGalleryPane(), Instances.getSelectPane());
		this.setRoot(mainHBox);
		
		this.setAlwaysOnTop(false);
		
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		this.getScene().heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());
		
		onKeyPress();
		onKeyRelease();
	}
	
	public void swapViewMode() {
		GalleryPane galleryPane = Instances.getGalleryPane();
		MediaPane mediaPane = Instances.getMediaPane();
		ObservableList<Node> panes = mainHBox.getChildren();
		
		if (panes.contains(mediaPane)) {
			MediaPaneControls controls = mediaPane.getControls();
			if (controls.isShowing()) controls.hide();
			
			VideoPlayer videoPlayer = mediaPane.getVideoPlayer();
			if (videoPlayer != null && videoPlayer.isPlaying()) videoPlayer.pause();
			
			panes.set(panes.indexOf(mediaPane), galleryPane);
			galleryPane.adjustViewportToCurrentTarget();
			galleryPane.requestFocus();
		} else if (panes.contains(galleryPane)) {
			panes.set(panes.indexOf(galleryPane), mediaPane);
			mediaPane.requestFocus();
		}
	}
	public boolean isFullView() {
		return mainHBox.getChildren().contains(Instances.getMediaPane());
	}
	
	private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty shiftDownProperty() {
		return shiftDown;
	}
	
	private void onKeyPress() {
		getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (Instances.getSelectPane().getTfSearch().isFocused()) return;
			
			Select select = Instances.getSelect();
			Reload reload = Instances.getReload();
			Target target = Instances.getTarget();
			
			switch (event.getCode()) {
				case ESCAPE:
					if (isFullView()) swapViewMode();
					break;
				case E:
					BaseTileEvent.onGroupButtonPress(target.getCurrentTarget());
					reload.doReload();
					Instances.getGalleryPane().loadCacheOfTilesInViewport();
					break;
				case R:
					select.setRandom();
					reload.doReload();
					break;
				case F:
					swapViewMode();
					reload.doReload();
					break;
				case SHIFT:
					shiftDown.setValue(true);
					Instances.getSelect().setShiftStart(target.getCurrentTarget());
					break;
				case W:
				case A:
				case S:
				case D:
					target.move(event.getCode());
					
					if (event.isShiftDown()) select.shiftSelectTo(target.getCurrentTarget());
					else if (event.isControlDown()) select.add(target.getCurrentTarget());
					else select.set(target.getCurrentTarget());
					
					Instances.getReload().doReload();
					break;
				default:
					break;
			}
		});
	}
	private void onKeyRelease() {
		getScene().addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
				case SHIFT:
					shiftDown.setValue(false);
					break;
				default:
					break;
			}
		});
	}
	
	@Override
	public Object _show(String... args) {
		this.show();
		mainHBox.requestFocus();
		
		Decorator.applyScrollbarStyle(Instances.getGalleryPane());
		Decorator.applyScrollbarStyle(Instances.getFilterPane().getScrollPane());
		Decorator.applyScrollbarStyle(Instances.getSelectPane().getScrollPane());
		
		return null;
	}
}
