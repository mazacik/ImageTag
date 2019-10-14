package application.main;

import application.control.Filter;
import application.control.Reload;
import application.control.Select;
import application.control.Target;
import application.data.list.DataListMain;
import application.data.list.TagListMain;
import application.gui.panes.center.GalleryPane;
import application.gui.panes.center.MediaPane;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.panes.top.ToolbarPane;
import application.settings.Settings;

public abstract class Instances {
	private static Settings settings;
	
	private static DataListMain dataListMain;
	private static TagListMain tagListMain;
	
	private static MediaPane mediaPane;
	private static FilterPane filterPane;
	private static SelectPane selectPane;
	private static ToolbarPane toolbarPane;
	private static GalleryPane galleryPane;
	
	private static Filter filter;
	private static Target target;
	private static Select select;
	private static Reload reload;
	
	public static void init() {
		settings = Settings.readFromDisk();
		
		dataListMain = new DataListMain();
		tagListMain = new TagListMain();
		
		toolbarPane = new ToolbarPane();    /* needs Settings */
		galleryPane = new GalleryPane();    /* needs Settings */
		mediaPane = new MediaPane();        /* needs Settings, GalleryPane */
		filterPane = new FilterPane();      /* needs Settings */
		selectPane = new SelectPane();      /* needs Settings */
		
		filter = new Filter();
		target = new Target();
		select = new Select();
		reload = new Reload();              /* needs everything */
	}
	
	public static Settings getSettings() {
		return settings;
	}
	
	public static DataListMain getDataListMain() {
		return dataListMain;
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
}
