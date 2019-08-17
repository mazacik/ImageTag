package application.controller;

import application.database.list.CustomList;
import application.database.list.DataObjectList;
import application.database.object.DataObject;
import application.gui.panes.NodeBase;
import application.gui.panes.center.GalleryTile;
import application.main.Instances;

import java.util.ArrayList;

public class Reload {
	private CustomList<NodeBase> mainPanes;
	private CustomList<DataObject> needsTileEffect;
	
	public Reload() {
		mainPanes = new CustomList<>();
		mainPanes.add(Instances.getFilterPane());
		mainPanes.add(Instances.getSelectPane());
		mainPanes.add(Instances.getGalleryPane());
		mainPanes.add(Instances.getMediaPane());
		mainPanes.add(Instances.getToolbarPane());
		
		needsTileEffect = new CustomList<>();
		
		this.subscribe(Instances.getMediaPane()
				, Control.TARGET
		);
		this.subscribe(Instances.getFilterPane()
				, Control.TAG
				, Control.FILTER
		);
		this.subscribe(Instances.getSelectPane()
				, Control.TAG
				, Control.FILTER
				, Control.TARGET
				, Control.SELECT
		);
		this.subscribe(Instances.getGalleryPane()
				, Control.OBJ
				, Control.FILTER
		);
		this.subscribe(Instances.getToolbarPane()
				, Control.TARGET
		);
	}
	
	public void doReload() {
		for (NodeBase node : mainPanes) {
			if (node.getNeedsReload()) {
				if (node.reload()) {
					node.setNeedsReload(false);
				}
			}
		}
		
		DataObjectList visibleObjects = Instances.getGalleryPane().getDataObjectsOfTiles();
		DataObjectList helper = new DataObjectList();
		for (DataObject dataObject : needsTileEffect) {
			if (visibleObjects.contains(dataObject)) {
				dataObject.getGalleryTile().generateEffect();
				helper.add(dataObject);
			}
		}
		needsTileEffect.removeAll(helper);
	}
	
	public void notify(Control... controls) {
		for (Control control : controls) {
			for (NodeBase node : control.getSubscribers()) {
				node.setNeedsReload(true);
			}
		}
	}
	
	public void requestTileEffect(GalleryTile galleryTile) {
		needsTileEffect.add(galleryTile.getParentDataObject());
	}
	public void requestTileEffect(DataObjectList dataObjectList) {
		needsTileEffect.addAll(dataObjectList);
	}
	public void requestTileEffect(DataObject dataObject) {
		needsTileEffect.add(dataObject);
	}
	
	private void subscribe(NodeBase node, Control... controls) {
		for (Control control : controls) control.getSubscribers().add(node);
	}
	
	public enum Control {
		OBJ,
		TAG,
		FILTER,
		TARGET,
		SELECT,
		;
		
		private ArrayList<NodeBase> subscribers;
		Control() {
			this.subscribers = new ArrayList<>();
		}
		public ArrayList<NodeBase> getSubscribers() {
			return this.subscribers;
		}
	}
}
