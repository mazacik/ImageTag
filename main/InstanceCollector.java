package application.main;

import application.backend.base.entity.EntityListMain;
import application.backend.base.tag.TagListMain;
import application.backend.control.Filter;
import application.backend.control.Select;
import application.backend.control.Settings;
import application.backend.control.Target;
import application.backend.control.reload.Reload;
import application.frontend.pane.center.GalleryPane;
import application.frontend.pane.center.MediaPane;
import application.frontend.pane.side.left.FilterPane;
import application.frontend.pane.side.right.SelectPane;
import application.frontend.pane.top.ToolbarPane;

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
