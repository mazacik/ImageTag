package frontend;

import frontend.component.display.DisplayPane;
import frontend.component.gallery.GalleryPane;
import frontend.component.side.FilterPane;
import frontend.component.side.select.SelectPane;
import frontend.component.top.ToolbarPane;
import frontend.stage.primary.PrimaryStage;

public abstract class UserInterface {
	public static void initialize() {
		toolbarPane.initialize();
		galleryPane.initialize();
		displayPane.initialize();
		filterPane.initialize();
		selectPane.initialize();
	}
	
	private static final ToolbarPane toolbarPane = new ToolbarPane();
	public static ToolbarPane getToolbarPane() {
		return toolbarPane;
	}
	
	private static final GalleryPane galleryPane = new GalleryPane();
	public static GalleryPane getGalleryPane() {
		return galleryPane;
	}
	
	private static final DisplayPane displayPane = new DisplayPane();
	public static DisplayPane getDisplayPane() {
		return displayPane;
	}
	
	private static final FilterPane filterPane = new FilterPane();
	public static FilterPane getFilterPane() {
		return filterPane;
	}
	
	private static final SelectPane selectPane = new SelectPane();
	public static SelectPane getSelectPane() {
		return selectPane;
	}
	
	private static final PrimaryStage stage = new PrimaryStage();
	public static PrimaryStage getStage() {
		return stage;
	}
}
