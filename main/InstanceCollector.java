package application.main;

import application.baseobject.entity.EntityListMain;
import application.baseobject.tag.TagListMain;
import application.control.Filter;
import application.control.Select;
import application.control.Settings;
import application.control.Target;
import application.control.reload.Reload;
import application.gui.main.center.GalleryPane;
import application.gui.main.center.MediaPane;
import application.gui.main.side.left.FilterPane;
import application.gui.main.side.right.SelectPane;
import application.gui.main.top.ToolbarPane;

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
