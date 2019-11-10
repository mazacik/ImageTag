package application.main;

import application.base.entity.EntityListMain;
import application.base.tag.TagListMain;
import application.control.Filter;
import application.control.Select;
import application.control.Settings;
import application.control.Target;
import application.control.reload.Reload;
import application.pane.center.GalleryPane;
import application.pane.center.MediaPane;
import application.pane.side.left.FilterPane;
import application.pane.side.right.SelectPane;
import application.pane.top.ToolbarPane;

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
