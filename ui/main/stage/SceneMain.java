package ui.main.stage;

import control.reload.ChangeIn;
import control.reload.Reload;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.component.simple.HBox;
import ui.main.center.PaneEntity;
import ui.main.center.PaneGallery;
import ui.main.side.left.PaneFilter;
import ui.main.side.right.PaneSelect;

public class SceneMain extends HBox {
	public SceneMain() {
		super(PaneFilter.get(), PaneGallery.get(), PaneSelect.get());
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			PaneEntity.get().interruptVideoPlayer();
			
			this.getChildren().set(1, PaneGallery.get());
			
			PaneGallery.get().moveViewportToTarget();
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	public void viewEntity() {
		if (isViewGallery()) {
			this.getChildren().set(1, PaneEntity.get());
			
			PaneEntity.get().requestFocus();
			PaneEntity.get().fireEvent(new MouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false, false, false, false, false, false, false, null));
			
			Reload.notify(ChangeIn.VIEWMODE);
		}
	}
	
	public boolean isViewGallery() {
		return this.getChildren().contains(PaneGallery.get());
	}
}
