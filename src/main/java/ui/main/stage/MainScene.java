package ui.main.stage;

import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import control.Select;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
				if (event.getCode() == KeyCode.ESCAPE) {
					mainBox.requestFocus();
					event.consume();
				}
			} else {
				switch (event.getCode()) {
					case BACK_QUOTE:
						
						break;
					case ESCAPE:
						MainStage.getMainScene().viewGallery();
						Reload.start();
						break;
					case TAB:
						SelectPane.getInstance().getNodeSearch().requestFocus();
						break;
					case DELETE:
						Select.getEntities().deleteFiles();
						Reload.start();
						break;
					case E:
						EntityCollectionUtil.openCollection(Select.getTarget());
						Reload.start();
						break;
					case R:
						Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
						Entity randomEntity = GalleryPane.getTileEntities().getRandom();
						Select.getEntities().set(randomEntity);
						Select.setTarget(randomEntity);
						Reload.start();
						break;
					case G:
						Entity randomEntityFromCollection = Select.getTarget().getCollection().getRandom();
						Select.getEntities().set(randomEntityFromCollection);
						Select.setTarget(randomEntityFromCollection);
						Reload.start();
						break;
					case F:
						if (this.isViewGallery()) this.viewEntity();
						else this.viewGallery();
						Reload.start();
						break;
					case W:
					case A:
					case S:
					case D:
						Select.moveTarget(event.getCode());
						Select.getEntities().set(Select.getTarget());
						Reload.start();
						break;
				}
			}
		});
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			DisplayPane.getInstance().interruptVideoPlayer();
			
			mainBox.getChildren().set(1, GalleryPane.getInstance());
			
			GalleryPane.getInstance().requestFocus();
			GalleryPane.moveViewportToTarget();
			
			Reload.notify(Notifier.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			mainBox.getChildren().set(1, DisplayPane.getInstance());
			
			DisplayPane.getInstance().requestFocus();
			DisplayPane.getInstance().fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			Reload.notify(Notifier.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return mainBox.getChildren().contains(GalleryPane.getInstance());
	}
}