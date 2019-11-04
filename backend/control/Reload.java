package application.backend.control;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.base.entity.EntityList;
import application.frontend.pane.PaneInterface;
import application.main.InstanceCollector;

import java.util.ArrayList;

public class Reload implements InstanceCollector {
	private CustomList<PaneInterface> mainPanes;
	private CustomList<Entity> needsTileEffect;
	
	public Reload() {
	
	}
	
	public void init() {
		mainPanes = new CustomList<>();
		mainPanes.add(toolbarPane);
		mainPanes.add(galleryPane);
		mainPanes.add(mediaPane);
		mainPanes.add(filterPane);
		mainPanes.add(selectPane);
		
		needsTileEffect = new CustomList<>();
		
		this.subscribe(toolbarPane
				, Control.TARGET
		);
		this.subscribe(galleryPane
				, Control.DATA
				, Control.FILTER
		);
		this.subscribe(mediaPane
				, Control.TARGET
		);
		this.subscribe(filterPane
				, Control.TAGS
				, Control.FILTER
		);
		this.subscribe(selectPane
				, Control.DATA
				, Control.TAGS
				, Control.FILTER
				, Control.TARGET
				, Control.SELECT
		);
	}
	
	public void doReload() {
		for (PaneInterface node : mainPanes) {
			if (node.getNeedsReload()) {
				if (node.reload()) {
					node.setNeedsReload(false);
				}
			}
		}
		
		EntityList visibleObjects = galleryPane.getDataObjectsOfTiles();
		EntityList helper = new EntityList();
		for (Entity entity : needsTileEffect) {
			if (visibleObjects.contains(entity)) {
				entity.getGalleryTile().updateSelectBorder();
				helper.add(entity);
			}
		}
		needsTileEffect.removeAll(helper);
	}
	
	public void notify(Control... controls) {
		for (Control control : controls) {
			for (PaneInterface node : control.getSubscribers()) {
				node.setNeedsReload(true);
			}
		}
	}
	public void request(PaneInterface pane) {
		pane.setNeedsReload(true);
	}
	
	public void requestTileEffect(EntityList entityList) {
		needsTileEffect.addAll(entityList);
	}
	public void requestTileEffect(Entity entity) {
		needsTileEffect.add(entity);
	}
	
	private void subscribe(PaneInterface node, Control... controls) {
		for (Control control : controls) control.getSubscribers().add(node);
	}
	
	public enum Control {
		DATA,
		TAGS,
		FILTER,
		TARGET,
		SELECT,
		;
		
		private ArrayList<PaneInterface> subscribers;
		Control() {
			this.subscribers = new ArrayList<>();
		}
		public ArrayList<PaneInterface> getSubscribers() {
			return this.subscribers;
		}
	}
}
