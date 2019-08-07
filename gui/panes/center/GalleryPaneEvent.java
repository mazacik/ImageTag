package application.gui.panes.center;

import application.main.Instances;

public class GalleryPaneEvent {
	public GalleryPaneEvent() {
		onMouseClick();
	}
	
	private void onMouseClick() {
		Instances.getGalleryPane().setOnMouseClicked(event -> Instances.getGalleryPane().requestFocus());
	}
}
