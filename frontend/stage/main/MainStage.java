package application.frontend.stage.main;

import application.frontend.component.simple.EditNode;
import application.frontend.decorator.Decorator;
import application.frontend.decorator.SizeUtil;
import application.frontend.pane.center.MediaPaneControls;
import application.frontend.pane.center.VideoPlayer;
import application.frontend.stage.base.StageBase;
import application.main.InstanceCollector;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.logging.Logger;

public class MainStage extends StageBase implements InstanceCollector {
	private HBox mainHBox;
	
	public MainStage() {
		super(toolbarPane);
		
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + SizeUtil.getGalleryTileSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + SizeUtil.getGalleryTileSize());
		this.setWidth(SizeUtil.getUsableScreenWidth());
		this.setHeight(SizeUtil.getUsableScreenHeight());
		this.setOnCloseRequest(event -> {
			Logger.getGlobal().info("application exit");
			VideoPlayer videoPlayer = mediaPane.getVideoPlayer();
			if (videoPlayer != null) videoPlayer.dispose();
			settings.writeToDisk();
			entityListMain.writeToDisk();
			tagListMain.writeDummyToDisk();
		});
		
		mainHBox = new HBox(filterPane, galleryPane, selectPane);
		this.setRoot(mainHBox);
		
		this.setBorder(null);
		this.setAlwaysOnTop(false);
		
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		
		this.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Node node = event.getPickResult().getIntersectedNode();
			if (node instanceof EditNode) {
				event.consume();
			} else {
				node.requestFocus();
			}
		});
		
		onKeyPress();
		onKeyRelease();
	}
	
	public void swapViewMode() {
		ObservableList<Node> panes = mainHBox.getChildren();
		
		if (panes.contains(mediaPane)) {
			MediaPaneControls controls = mediaPane.getControls();
			if (controls.isShowing()) controls.hide();
			
			VideoPlayer videoPlayer = mediaPane.getVideoPlayer();
			if (videoPlayer != null && videoPlayer.isPlaying()) videoPlayer.pause();
			
			panes.set(panes.indexOf(mediaPane), galleryPane);
			galleryPane.adjustViewportToTarget();
			galleryPane.requestFocus();
		} else if (panes.contains(galleryPane)) {
			panes.set(panes.indexOf(galleryPane), mediaPane);
			reload.request(mediaPane);
			mediaPane.requestFocus();
			mediaPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
		}
	}
	public boolean isFullView() {
		return mainHBox.getChildren().contains(mediaPane);
	}
	
	private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty shiftDownProperty() {
		return shiftDown;
	}
	public boolean isShiftDown() {
		return shiftDownProperty().get();
	}
	
	private void onKeyPress() {
		getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (getScene().getFocusOwner() instanceof EditNode) {
				if (event.getCode() == KeyCode.ESCAPE) {
					getScene().getRoot().requestFocus();
				} else if (event.getCode() == KeyCode.SHIFT) {
					shiftDown.setValue(true);
					select.setShiftStart(target.get());
				}
			} else {
				switch (event.getCode()) {
					case ESCAPE:
						if (isFullView()) swapViewMode();
						reload.doReload();
						break;
					case E:
						target.get().getGalleryTile().onGroupEffectClick();
						reload.doReload();
						break;
					case R:
						select.setRandom();
						reload.doReload();
						break;
					case G:
						select.setRandomFromEntityGroup();
						reload.doReload();
						break;
					case F:
						swapViewMode();
						reload.doReload();
						break;
					case SHIFT:
						shiftDown.setValue(true);
						select.setShiftStart(target.get());
						break;
					case W:
					case A:
					case S:
					case D:
						target.move(event.getCode());
						
						if (event.isShiftDown()) select.shiftSelectTo(target.get());
						else if (event.isControlDown()) select.add(target.get());
						else select.set(target.get());
						
						reload.doReload();
						break;
				}
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
		
		Decorator.applyScrollbarStyle(galleryPane);
		Decorator.applyScrollbarStyle(filterPane.getScrollPane());
		Decorator.applyScrollbarStyle(selectPane.getScrollPane());
		
		return null;
	}
}
