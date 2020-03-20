package ui.main.stage;

import base.entity.CollectionUtil;
import base.entity.Entity;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import lire.LireUtil;
import main.Root;
import ui.EntityDetailsUtil;
import ui.custom.ListMenu;
import ui.decorator.Decorator;
import ui.node.EditNode;
import ui.override.HBox;
import ui.override.Scene;
import ui.stage.ProgressNode;

public class MainScene extends Scene {
	private ProgressNode loadingBar;
	
	private BorderPane borderPane;
	
	public MainScene() {
		loadingBar = new ProgressNode();
		
		borderPane = new BorderPane();
		borderPane.setTop(Root.TOOLBAR_PANE);
		borderPane.setLeft(Root.FILTER_PANE);
		borderPane.setCenter(new HBox(Root.GALLERY_PANE));
		HBox.setHgrow(Root.GALLERY_PANE, Priority.NEVER);
		borderPane.setRight(Root.SELECT_PANE);
		
		borderPane.setBackground(Decorator.getBackgroundPrimary());
		
		this.setRoot(borderPane);
		this.initKeybinds();
	}
	
	private void initKeybinds() {
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (this.getFocusOwner() instanceof EditNode) {
				keybindsEditNode(event);
			} else {
				keybindsGlobal(event);
			}
		});
	}
	private void keybindsGlobal(KeyEvent event) {
		ListMenu.hideMenus();
		switch (event.getCode()) {
			case SHIFT:
				Root.SELECT.setupShiftSelect();
				break;
			case F1:
				LireUtil.index();
				break;
			case F2:
				LireUtil.echo(99);
				break;
			case I:
				EntityDetailsUtil.show();
				break;
			case ESCAPE:
				Root.MAIN_STAGE.getMainScene().viewGallery();
				Reload.start();
				break;
			case TAB:
				Root.SELECT_PANE.getNodeSearch().requestFocus();
				break;
			case DELETE:
				Root.SELECT.deleteSelect();
				Reload.start();
				break;
			case E:
				CollectionUtil.toggleCollection(Root.SELECT.getTarget());
				Reload.start();
				break;
			case R:
				Root.SELECT.setRandom();
				Reload.start();
				break;
			case G:
				if (Root.SELECT.getTarget().hasCollection()) {
					Entity randomEntityFromCollection = Root.SELECT.getTarget().getCollection().getRandom();
					Root.SELECT.set(randomEntityFromCollection);
					Root.SELECT.setTarget(randomEntityFromCollection);
					Reload.start();
				}
				break;
			case F:
				if (this.isViewGallery()) this.viewDisplay();
				else this.viewGallery();
				Reload.start();
				break;
			case W:
			case A:
			case S:
			case D:
				Root.SELECT.moveTarget(event);
				Reload.start();
				break;
			case ALT:
				Root.DISPLAY_PANE.getControls().hide();
				break;
		}
	}
	private void keybindsEditNode(KeyEvent event) {
		switch (event.getCode()) {
			case ESCAPE:
			case TAB:
				borderPane.requestFocus();
				event.consume();
				break;
			case UP:
				if (this.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.UP, event.isControlDown());
					event.consume();
				}
				break;
			case DOWN:
				if (this.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.DOWN, event.isControlDown());
					event.consume();
				}
				break;
		}
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			Root.DISPLAY_PANE.interruptVideoPlayer();
			
			borderPane.setCenter(Root.GALLERY_PANE);
			Root.GALLERY_PANE.requestFocus();
			Root.GALLERY_PANE.moveViewportToTarget();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	public void viewDisplay() {
		if (isViewGallery()) {
			borderPane.setCenter(Root.DISPLAY_PANE);
			Root.DISPLAY_PANE.requestFocus();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	
	public boolean isViewGallery() {
		return borderPane.getCenter() != Root.DISPLAY_PANE;
	}
	
	public void showLoadingBar(Object caller, int total) {
		if (Platform.isFxApplicationThread()) {
			loadingBar.setup(caller, total);
			borderPane.setBottom(loadingBar);
		} else {
			Platform.runLater(() -> {
				loadingBar.setup(caller, total);
				borderPane.setBottom(loadingBar);
			});
		}
	}
	public void hideLoadingBar(Object caller) {
		if (loadingBar.getCaller() == caller) {
			if (Platform.isFxApplicationThread()) {
				borderPane.setBottom(null);
			} else {
				Platform.runLater(() -> borderPane.setBottom(null));
			}
		}
	}
	public void advanceLoadingBar(Object caller) {
		if (Platform.isFxApplicationThread()) {
			loadingBar.advance(caller);
		} else {
			Platform.runLater(() -> loadingBar.advance(caller));
		}
	}
}
