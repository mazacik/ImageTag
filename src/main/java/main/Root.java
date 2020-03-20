package main;

import base.entity.EntityList;
import base.tag.TagList;
import control.Select;
import control.filter.Filter;
import ui.main.display.DisplayPane;
import ui.main.gallery.GalleryPane;
import ui.main.side.FilterPane;
import ui.main.side.SelectPane;
import ui.main.stage.MainStage;
import ui.main.top.ToolbarPane;

public abstract class Root {
	public static final ThreadGroup THREADPOOL;
	
	public static final EntityList ENTITYLIST;
	public static final TagList TAGLIST;
	
	public static final Filter FILTER;
	public static final Select SELECT;
	
	public static final MainStage MAIN_STAGE;
	
	public static final ToolbarPane TOOLBAR_PANE;
	public static final GalleryPane GALLERY_PANE;
	public static final DisplayPane DISPLAY_PANE;
	public static final FilterPane FILTER_PANE;
	public static final SelectPane SELECT_PANE;
	
	static {
		THREADPOOL = new ThreadGroup("ROOT");
		
		ENTITYLIST = new EntityList();
		TAGLIST = new TagList();
		
		FILTER = new Filter();
		SELECT = new Select();
		
		MAIN_STAGE = new MainStage();
		
		TOOLBAR_PANE = new ToolbarPane();
		GALLERY_PANE = new GalleryPane();
		DISPLAY_PANE = new DisplayPane();
		FILTER_PANE = new FilterPane();
		SELECT_PANE = new SelectPane();
	}
}
