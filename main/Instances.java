package application.main;

import application.controller.Filter;
import application.controller.Reload;
import application.controller.Select;
import application.controller.Target;
import application.database.list.DataObjectListMain;
import application.database.list.TagListMain;
import application.gui.nodes.popup.ClickMenuData;
import application.gui.nodes.popup.ClickMenuTag;
import application.gui.panes.center.GalleryPane;
import application.gui.panes.center.MediaPane;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.panes.top.ToolbarPane;
import application.settings.Settings;

public abstract class Instances {
	private static Settings settings;
	
	private static Filter filter;
	private static Target target;
	private static Select select;
	private static Reload reload;
	
	private static TagListMain tagListMain;
	private static DataObjectListMain objectListMain;
	
	private static MediaPane mediaPane;
	private static FilterPane filterPane;
	private static SelectPane selectPane;
	private static ToolbarPane toolbarPane;
	private static GalleryPane galleryPane;
	
	private static ClickMenuData clickMenuData;
	private static ClickMenuTag clickMenuTag;
	
	public static void createInstances() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %2$s: %5$s%n");
		
		settings = Settings.readFromDisk();
		if (settings == null) settings = new Settings();
		settings.getRecentProjects().setMaxSize(10);
		
		createDatabaseInstances();
		
		createFrontendInstances();
		
		createBackendInstances();
	}
	private static void createDatabaseInstances() {
		objectListMain = new DataObjectListMain();
		tagListMain = new TagListMain();
	}
	private static void createFrontendInstances() {
		toolbarPane = new ToolbarPane();    /* needs Settings */
		galleryPane = new GalleryPane();    /* needs Settings */
		mediaPane = new MediaPane();        /* needs Settings, GalleryPane */
		filterPane = new FilterPane();      /* needs Settings */
		selectPane = new SelectPane();      /* needs Settings */
		
		clickMenuData = new ClickMenuData();
		clickMenuTag = new ClickMenuTag();
	}
	private static void createBackendInstances() {
		filter = new Filter();
		target = new Target();
		select = new Select();
		reload = new Reload();  /* needs Frontend */
	}
	
	public static Settings getSettings() {
		return settings;
	}
	public static Filter getFilter() {
		return filter;
	}
	public static Target getTarget() {
		return target;
	}
	public static Select getSelect() {
		return select;
	}
	public static Reload getReload() {
		return reload;
	}
	public static DataObjectListMain getObjectListMain() {
		return objectListMain;
	}
	public static TagListMain getTagListMain() {
		return tagListMain;
	}
	public static ToolbarPane getToolbarPane() {
		return toolbarPane;
	}
	public static GalleryPane getGalleryPane() {
		return galleryPane;
	}
	public static MediaPane getMediaPane() {
		return mediaPane;
	}
	public static FilterPane getFilterPane() {
		return filterPane;
	}
	public static SelectPane getSelectPane() {
		return selectPane;
	}
	public static ClickMenuData getClickMenuData() {
		return clickMenuData;
	}
	public static ClickMenuTag getClickMenuTag() {
		return clickMenuTag;
	}
}
