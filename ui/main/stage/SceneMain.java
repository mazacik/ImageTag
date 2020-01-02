package ui.main.stage;

import base.entity.Entity;
import base.entity.EntityCollectionUtil;
import control.Select;
import control.filter.Filter;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.component.simple.EditNode;
import ui.component.simple.HBox;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;
import ui.main.center.PaneEntity;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;
import ui.main.top.PaneToolbar;
import ui.stage.Scene;
import ui.stage.StageManager;

public class SceneMain extends Scene {
	private HBox mainBox;
	
	public SceneMain() {
		mainBox = new HBox(PaneFilter.getInstance(), PaneGallery.getInstance(), PaneSelect.getInstance());
		
		VBox vBox = new VBox(PaneToolbar.getInstance(), mainBox);
		vBox.setBackground(ColorUtil.getBackgroundPrimary());
		
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
						StageManager.getStageMain().getSceneMain().viewGallery();
						Reload.start();
						break;
					case TAB:
						PaneSelect.getInstance().getNodeSearch().requestFocus();
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
						Entity randomEntity = PaneGallery.getTileEntities().getRandom();
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
						//todo fix ctrl/shift
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
			PaneEntity.getInstance().interruptVideoPlayer();
			
			mainBox.getChildren().set(1, PaneGallery.getInstance());
			
			PaneGallery.moveViewportToTarget();
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			mainBox.getChildren().set(1, PaneEntity.getInstance());
			
			PaneEntity.getInstance().requestFocus();
			PaneEntity.getInstance().fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return mainBox.getChildren().contains(PaneGallery.getInstance());
	}
}
