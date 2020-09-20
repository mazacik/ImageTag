package frontend.component.center;

import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.component.center.gallery.GalleryPane;
import frontend.component.display.DisplayPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class CenterPane extends StackPane {
	private final GalleryPane galleryPane = new GalleryPane();
	private final DisplayPane displayPane = new DisplayPane();
	
	private DisplayMode currentPane;
	
	public CenterPane() {
		Pane displayPaneWrapper = new Pane(displayPane);
		HBox.setHgrow(displayPaneWrapper, Priority.NEVER);
		
		getChildren().add(galleryPane);
		getChildren().add(displayPaneWrapper);
		
		HBox.setHgrow(this, Priority.ALWAYS);
	}
	
	public void initialize() {
		galleryPane.initialize();
		displayPane.initialize();
		
		showGalleryPane();
	}
	
	public void showGalleryPane() {
		displayPane.setOpacity(0.0);
		galleryPane.setOpacity(1.0);
		
		galleryPane.toFront();
		currentPane = DisplayMode.GALLERY;
		Reload.notify(Notifier.VIEWMODE_CHANGED);
	}
	public void showDisplayPane() {
		displayPane.setOpacity(1.0);
		galleryPane.setOpacity(0.0);
		
		galleryPane.toBack();
		currentPane = DisplayMode.DISPLAY;
		Reload.notify(Notifier.VIEWMODE_CHANGED);
	}
	public void swapCurrentPane() {
		if (currentPane == DisplayMode.GALLERY) {
			showDisplayPane();
		} else {
			showGalleryPane();
		}
	}
	
	public boolean isViewGallery() {
		return currentPane == DisplayMode.GALLERY;
	}
	public boolean isViewDisplay() {
		return currentPane == DisplayMode.DISPLAY;
	}
	
	public DisplayMode getCurrentPane() {
		return currentPane;
	}
	public GalleryPane getGalleryPane() {
		return galleryPane;
	}
	public DisplayPane getDisplayPane() {
		return displayPane;
	}
	
	public enum DisplayMode {
		GALLERY,
		DISPLAY
	}
}
