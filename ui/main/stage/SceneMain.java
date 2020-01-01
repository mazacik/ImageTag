package ui.main.stage;

import base.entity.Entity;
import cache.CacheManager;
import control.Select;
import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.scene.image.Image;
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
		mainBox = new HBox(PaneFilter.get(), PaneGallery.getInstance(), PaneSelect.get());
		
		VBox vBox = new VBox(PaneToolbar.get(), mainBox);
		vBox.setBackground(ColorUtil.getBackgroundPrimary());
		
		this.initKeybinds();
		this.setRoot(vBox);
	}
	
	private void initKeybinds() {
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			//System.out.println("Focus: " + this.getFocusOwner().getClass().toString());
			//System.out.println("KeyCode: " + event.getCode().getName());
			
			if (this.getFocusOwner() instanceof EditNode) {
				if (event.getCode() == KeyCode.ESCAPE) {
					mainBox.requestFocus();
					event.consume();
				} else if (event.getCode() == KeyCode.SHIFT) {
					Select.shiftSelectFrom(Select.getTarget());
				}
			} else {
				switch (event.getCode()) {
					case BACK_QUOTE:
						Entity target = Select.getTarget();
						Image i = CacheManager.get(target);
						target.getGalleryTile().setImage(i);
						System.out.println(i != null);
						Reload.requestBorderUpdate(target);
						Reload.start();
						PaneGallery.getInstance().reload();
						break;
					case ESCAPE:
						StageManager.getStageMain().getSceneMain().viewGallery();
						Reload.start();
						break;
					case TAB:
						PaneSelect.get().getNodeSearch().requestFocus();
						break;
					case DELETE:
						Select.getEntities().deleteFiles();
						Reload.start();
						break;
					case E:
						Select.getTarget().getGalleryTile().onGroupIconClick();
						Reload.start();
						break;
					case R:
						//todo fixme Select.getEntities().setRandom();
						Reload.start();
						break;
					case G:
						Select.getEntities().set(Select.getTarget().getCollection().getRandom());
						//todo target set
						Reload.start();
						break;
					case F:
						if (this.isViewGallery()) this.viewEntity();
						else this.viewGallery();
						Reload.start();
						break;
					case SHIFT:
						Select.shiftSelectFrom(Select.getTarget());
						break;
					case W:
					case A:
					case S:
					case D:
						Select.moveTarget(event.getCode());
						
						if (event.isShiftDown()) Select.shiftSelectTo(Select.getTarget());
						else if (event.isControlDown()) Select.getEntities().add(Select.getTarget());
						else Select.getEntities().set(Select.getTarget());
						
						Reload.start();
						break;
				}
			}
		});
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			PaneEntity.get().interruptVideoPlayer();
			
			mainBox.getChildren().set(1, PaneGallery.getInstance());
			
			PaneGallery.getInstance().moveViewportToTarget();
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			mainBox.getChildren().set(1, PaneEntity.get());
			
			PaneEntity.get().requestFocus();
			PaneEntity.get().fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return mainBox.getChildren().contains(PaneGallery.getInstance());
	}
}
