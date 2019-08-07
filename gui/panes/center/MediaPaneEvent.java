package application.gui.panes.center;

import application.gui.scene.SceneUtil;
import application.main.Instances;
import javafx.beans.value.ChangeListener;

public class MediaPaneEvent {
	public MediaPaneEvent() {
		onMouseClick();
		onResize();
	}
	
	private void onMouseClick() {
		Instances.getMediaPane().setOnMouseClicked(event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (event.getClickCount() % 2 != 0) {
						Instances.getMediaPane().requestFocus();
					} else {
						SceneUtil.swapViewMode();
						Instances.getReload().doReload();
					}
					Instances.getClickMenuData().hide();
					break;
				case SECONDARY:
					Instances.getClickMenuData().show(Instances.getMediaPane(), event.getScreenX(), event.getScreenY());
					break;
				default:
					break;
			}
		});
	}
	private void onResize() {
		ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> Instances.getMediaPane().reload();
		Instances.getMediaPane().getCanvas().widthProperty().addListener(previewPaneSizeListener);
		Instances.getMediaPane().getCanvas().heightProperty().addListener(previewPaneSizeListener);
	}
}
