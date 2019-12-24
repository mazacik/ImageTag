package gui.stage.main;

import control.reload.ChangeIn;
import gui.component.simple.HBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;

public class SceneMain extends HBox implements InstanceCollector {
	public SceneMain() {
		super(filterPane, galleryPane, selectPane);
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			entityPane.interruptVideoPlayer();
			
			this.getChildren().set(1, galleryPane);
			
			galleryPane.moveViewportToTarget();
			galleryPane.requestFocus();
			
			reload.notify(ChangeIn.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			this.getChildren().set(1, entityPane);
			
			entityPane.requestFocus();
			entityPane.fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			reload.notify(ChangeIn.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return this.getChildren().contains(galleryPane);
	}
}
