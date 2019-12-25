package gui.stage.main;

import control.reload.ChangeIn;
import gui.component.simple.HBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;

public class SceneMain extends HBox implements InstanceCollector {
	public SceneMain() {
		super(paneFilter, paneGallery, paneSelect);
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			paneEntity.interruptVideoPlayer();
			
			this.getChildren().set(1, paneGallery);
			
			paneGallery.moveViewportToTarget();
			paneGallery.requestFocus();
			
			reload.notify(ChangeIn.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			this.getChildren().set(1, paneEntity);
			
			paneEntity.requestFocus();
			paneEntity.fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			reload.notify(ChangeIn.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return this.getChildren().contains(paneGallery);
	}
}
