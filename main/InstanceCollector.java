package main;

import baseobject.entity.EntityListMain;
import baseobject.tag.TagListMain;
import control.Filter;
import control.Select;
import control.Settings;
import control.Target;
import control.reload.Reload;
import gui.main.center.GalleryPane;
import gui.main.center.MediaPane;
import gui.main.side.left.FilterPane;
import gui.main.side.right.SelectPane;
import gui.main.top.ToolbarPane;

public interface InstanceCollector {
	Settings settings = new Settings();
	
	EntityListMain entityListMain = new EntityListMain();
	TagListMain tagListMain = new TagListMain();
	
	ToolbarPane toolbarPane = new ToolbarPane();
	GalleryPane galleryPane = new GalleryPane();
	MediaPane mediaPane = new MediaPane();
	FilterPane filterPane = new FilterPane();
	SelectPane selectPane = new SelectPane();
	
	Filter filter = new Filter();
	Target target = new Target();
	Select select = new Select();
	Reload reload = new Reload();
}
