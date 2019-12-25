package ui.main.stage;

import control.Select;
import control.Target;
import control.reload.Reload;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import main.Main;
import misc.Settings;
import ui.NodeUtil;
import ui.component.simple.EditNode;
import ui.decorator.Decorator;
import ui.decorator.SizeUtil;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.main.top.PaneToolbar;
import ui.stage.StageManager;
import ui.stage.base.StageBase;

import java.awt.*;

public class StageMain extends StageBase {
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
		this.setBorder(NodeUtil.getBorder(1));
		this.setWidth(width);
		this.setHeight(height);
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setOnCloseRequest(event -> Settings.writeToDisk());
		this.centerOnScreen();
		
		sceneIntro.show();
		
		this.setVisible(true);
	}
	public void layoutMain() {
		sceneMain = new SceneMain();
		
		this.setVisible(false);
		this.setBorder(null);
		this.setTitleBar(PaneToolbar.get());
		this.setRoot(sceneMain);
		
		Decorator.applyScrollbarStyle(PaneGallery.get());
		Decorator.applyScrollbarStyle(PaneFilter.get().getScrollPane());
		Decorator.applyScrollbarStyle(PaneSelect.get().getScrollPane());
		
		this.getScene().widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		this.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + Settings.getTileSize());
		this.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + Settings.getTileSize());
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
					Select.shiftSelectFrom(Target.get());
				}
			} else {
				switch (event.getCode()) {
					case DELETE:
						Select.deleteFiles();
						Reload.start();
						break;
					case ESCAPE:
						StageManager.getStageMain().getSceneMain().viewGallery();
						Reload.start();
						break;
					case E:
						Target.get().getGalleryTile().onGroupIconClick();
						Reload.start();
						break;
					case R:
						Select.getEntities().setRandom();
						Reload.start();
						break;
					case G:
						Select.getEntities().setRandomFromCollection();
						Reload.start();
						break;
					case F:
						if (sceneMain.isViewGallery()) sceneMain.viewEntity();
						else sceneMain.viewGallery();
						Reload.start();
						break;
					case SHIFT:
						shiftDown.setValue(true);
						Select.shiftSelectFrom(Target.get());
						break;
					case W:
					case A:
					case S:
					case D:
						Target.move(event.getCode());
						
						if (event.isShiftDown()) Select.shiftSelectTo(Target.get());
						else if (event.isControlDown()) Select.getEntities().add(Target.get());
						else Select.getEntities().set(Target.get());
						
						Reload.start();
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
