package ui.main.stage;

import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.input.KeyEvent;
import ui.EntityDetailsUtil;
import ui.decorator.Decorator;
import ui.main.display.DisplayPane;
import ui.main.gallery.GalleryPane;
import ui.main.side.FilterPane;
import ui.main.side.SelectPane;
import ui.main.top.ToolbarPane;
import ui.node.EditNode;
import ui.override.HBox;
import ui.override.Scene;
import ui.override.VBox;

public class MainScene extends Scene {
	private HBox mainBox;
	
	public MainScene() {
		mainBox = new HBox(FilterPane.getInstance(), GalleryPane.getInstance(), SelectPane.getInstance());
		
		VBox vBox = new VBox(ToolbarPane.getInstance(), mainBox);
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		this.initKeybinds();
		this.setRoot(vBox);
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
		switch (event.getCode()) {
			case I:
				EntityDetailsUtil.show();
				break;
			case ESCAPE:
				MainStage.getMainScene().viewGallery();
				Reload.start();
				break;
			case TAB:
				SelectPane.getInstance().getNodeSearch().requestFocus();
				break;
			case DELETE:
				Select.getEntities().deleteSelect();
				Reload.start();
				break;
			case E:
				EntityCollectionUtil.toggleCollection(Select.getTarget());
				Reload.start();
				break;
			case R:
				Entity randomEntity = null;
				while (!Filter.isValid(randomEntity)) {
					randomEntity = Filter.getEntities().getRandom();
				}
				Select.getEntities().setImpl(randomEntity);
				Select.setTarget(randomEntity);
				Reload.start();
				break;
			case G:
				if (Select.getTarget().getCollectionID() != 0) {
					Entity randomEntityFromCollection = Select.getTarget().getCollection().getRandom();
					Select.getEntities().setImpl(randomEntityFromCollection);
					Select.setTarget(randomEntityFromCollection);
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
				Select.moveTarget(event.getCode());
				Select.getEntities().setImpl(Select.getTarget());
				Reload.start();
				break;
			case ALT:
				DisplayPane.getInstance().getControls().hide();
				break;
		}
	}
	private void keybindsEditNode(KeyEvent event) {
		switch (event.getCode()) {
			case ESCAPE:
			case TAB:
				mainBox.requestFocus();
				event.consume();
				break;
			case UP:
				if (this.getFocusOwner() == SelectPane.getInstance().getNodeSearch()) {
					SelectPane.getInstance().nextMatch(Direction.UP, event.isControlDown());
					event.consume();
				}
				break;
			case DOWN:
				if (this.getFocusOwner() == SelectPane.getInstance().getNodeSearch()) {
					SelectPane.getInstance().nextMatch(Direction.DOWN, event.isControlDown());
					event.consume();
				}
				break;
		}
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			DisplayPane.getInstance().interruptVideoPlayer();
			
			mainBox.getChildren().set(1, GalleryPane.getInstance());
			
			GalleryPane.getInstance().requestFocus();
			GalleryPane.moveViewportToTarget();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	public void viewDisplay() {
		if (isViewGallery()) {
			mainBox.getChildren().set(1, DisplayPane.getInstance());
			DisplayPane.getInstance().requestFocus();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	
	public boolean isViewGallery() {
		return mainBox.getChildren().contains(GalleryPane.getInstance());
	}
}
