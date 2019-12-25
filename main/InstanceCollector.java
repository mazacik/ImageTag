package main;

import baseobject.entity.EntityList;
import baseobject.tag.TagList;
import control.Filter;
import control.Select;
import control.Settings;
import control.Target;
import control.reload.Reload;
import gui.main.center.PaneGallery;
import gui.main.center.PaneEntity;
import gui.main.side.left.PaneFilter;
import gui.main.side.right.PaneSelect;
import gui.main.top.PaneToolbar;

public interface InstanceCollector {
	Settings settings = new Settings();
	
	EntityList mainEntityList = new EntityList();
	TagList mainTagList = new TagList();
	
	PaneToolbar paneToolbar = new PaneToolbar();
	PaneGallery paneGallery = new PaneGallery();
	PaneEntity paneEntity = new PaneEntity();
	PaneFilter paneFilter = new PaneFilter();
	PaneSelect paneSelect = new PaneSelect();
	
	Filter filter = new Filter();
	Target target = new Target();
	Select select = new Select();
	Reload reload = new Reload();
}
