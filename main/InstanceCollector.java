package main;

import baseobject.entity.EntityList;
import baseobject.tag.TagList;
import control.Filter;
import control.Select;
import control.Settings;
import control.Target;
import control.reload.Reload;
import gui.main.center.GalleryPane;
import gui.main.center.EntityPane;
import gui.main.side.left.FilterPane;
import gui.main.side.right.SelectPane;
import gui.main.top.ToolbarPane;

public interface InstanceCollector {
	Settings settings = new Settings();
	
	EntityList entityListMain = new EntityList();
	TagList tagListMain = new TagList();
	
	ToolbarPane toolbarPane = new ToolbarPane();
	GalleryPane galleryPane = new GalleryPane();
	EntityPane entityPane = new EntityPane();
	FilterPane filterPane = new FilterPane();
	SelectPane selectPane = new SelectPane();
	
	Filter filter = new Filter();
	Target target = new Target();
	Select select = new Select();
	Reload reload = new Reload();
}
