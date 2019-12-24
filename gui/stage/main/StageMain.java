package gui.stage.main;

import gui.component.simple.EditNode;
import gui.decorator.Decorator;
import gui.decorator.SizeUtil;
import gui.stage.StageManager;
import gui.stage.base.StageBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;
import main.Main;

import java.awt.*;

public class StageMain extends StageBase implements InstanceCollector {
	private SceneIntro sceneIntro;
	private SceneProject sceneProject;
	private SceneMain sceneMain;
	
	public StageMain() {
		super("", false, false, false);
		
		this.setBorder(null);
		this.setAlwaysOnTop(false);
		this.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Node node = event.getPickResult().getIntersectedNode();
			if (node instanceof EditNode) {
				event.consume();
			} else {
				node.requestFocus();
			}
		});
		
		this.show("");
	}
	
	public void layoutIntro() {
		sceneIntro = new SceneIntro();
		sceneProject = new SceneProject();
		
		Rectangle usableScreenBounds = SizeUtil.getUsableScreenBounds();
		double width = usableScreenBounds.getWidth() / 2;
		double height = usableScreenBounds.getHeight() / 2;
		
		this.setVisible(false);
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setOnCloseRequest(event -> settings.writeToDisk());
		this.centerOnScreen();
		
		sceneIntro.show();
		
		this.setVisible(true);
	}
	public void layoutMain() {
		sceneMain = new SceneMain();
		
		this.setVisible(false);
		this.setTitleBar(toolbarPane);
		this.setRoot(sceneMain);
		
		Decorator.applyScrollbarStyle(galleryPane);
		Decorator.applyScrollbarStyle(filterPane.getScrollPane());
		Decorator.applyScrollbarStyle(selectPane.getScrollPane());
		
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + settings.getTileSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + settings.getTileSize());
		this.setWidth(SizeUtil.getUsableScreenWidth());
		this.setHeight(SizeUtil.getUsableScreenHeight());
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Main.exitApplication());
		
		sceneMain.requestFocus();
		sceneMain.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (getScene().getFocusOwner() instanceof EditNode) {
				if (event.getCode() == KeyCode.ESCAPE) {
					getScene().getRoot().requestFocus();
				} else if (event.getCode() == KeyCode.SHIFT) {
					shiftDown.setValue(true);
					select.shiftSelectFrom(target.get());
				}
			} else {
				switch (event.getCode()) {
					case ESCAPE:
						StageManager.getStageMain().getSceneMain().viewGallery();
						reload.doReload();
						break;
					case E:
						target.get().getGalleryTile().onGroupIconClick();
						reload.doReload();
						break;
					case R:
						select.setRandom();
						reload.doReload();
						break;
					case G:
						select.setRandomFromCollection();
						reload.doReload();
						break;
					case F:
						if (sceneMain.isViewGallery()) sceneMain.viewEntity();
						else sceneMain.viewGallery();
						reload.doReload();
						break;
					case SHIFT:
						shiftDown.setValue(true);
						select.shiftSelectFrom(target.get());
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
		sceneMain.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
				case SHIFT:
					shiftDown.setValue(false);
					break;
				default:
					break;
			}
		});
		
		this.setVisible(true);
	}
	
	private SimpleBooleanProperty shiftDown = new SimpleBooleanProperty(false);
	public SimpleBooleanProperty shiftDownProperty() {
		return shiftDown;
	}
	public boolean isShiftDown() {
		return shiftDownProperty().get();
	}
	
	public void setVisible(boolean value) {
		if (value) {
			this.setOpacity(1);
		} else {
			this.setOpacity(0);
		}
	}
	
	public SceneIntro getSceneIntro() {
		return sceneIntro;
	}
	public SceneProject getSceneProject() {
		return sceneProject;
	}
	public SceneMain getSceneMain() {
		return sceneMain;
	}
	
	@Override
	public Object show(String... args) {
		super.show();
		return null;
	}
}
