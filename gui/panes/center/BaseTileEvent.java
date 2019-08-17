package application.gui.panes.center;

import application.controller.Reload;
import application.database.object.DataObject;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.input.MouseEvent;

public class BaseTileEvent {
	public BaseTileEvent(GalleryTile galleryTile) {
		onMouseClick(galleryTile);
	}
	
	private void onMouseClick(GalleryTile galleryTile) {
		galleryTile.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			event.consume();
			switch (event.getButton()) {
				case PRIMARY:
					if (event.getClickCount() % 2 != 0) onLeftClick(galleryTile, event);
					else onLeftDoubleClick(event);
					break;
				case SECONDARY:
					onRightClick(galleryTile, event);
					break;
				default:
					break;
			}
		});
	}
	private void onLeftClick(GalleryTile galleryTile, MouseEvent event) {
		DataObject dataObject = galleryTile.getParentDataObject();
		
		if (isOnGroupButton(event)) {
			onGroupButtonPress(dataObject);
			Instances.getReload().doReload();
			Instances.getGalleryPane().loadCacheOfTilesInViewport();
		} else {
			Instances.getTarget().set(dataObject);
			
			if (event.isControlDown()) {
				Instances.getSelect().swapState(dataObject);
			} else if (event.isShiftDown()) {
				Instances.getSelect().shiftSelectTo(dataObject);
			} else {
				Instances.getSelect().set(dataObject);
			}
			
			Instances.getReload().doReload();
		}
		
		Instances.getClickMenuData().hide();
	}
	private void onLeftDoubleClick(MouseEvent event) {
		if (!isOnGroupButton(event)) {
			Stages.getMainStage().swapViewMode();
			Instances.getReload().doReload();
		}
	}
	private void onRightClick(GalleryTile sender, MouseEvent event) {
		DataObject dataObject = sender.getParentDataObject();
		
		if (!Instances.getSelect().contains(dataObject)) {
			Instances.getSelect().set(dataObject);
		}
		
		Instances.getTarget().set(dataObject);
		Instances.getReload().doReload();
		Instances.getClickMenuData().show(sender, event);
	}
	
	private boolean isOnGroupButton(MouseEvent event) {
		int tileSize = Instances.getSettings().getGalleryTileSize();
		return event.getX() > tileSize - GalleryTile.getEffectGroupSize() && event.getY() < GalleryTile.getEffectGroupSize();
	}
	public static void onGroupButtonPress(DataObject dataObject) {
		if (dataObject.getMergeID() == 0) return;
		if (!Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
			Instances.getGalleryPane().getExpandedGroups().add(dataObject.getMergeID());
		} else {
			//noinspection RedundantCollectionOperation
			Instances.getGalleryPane().getExpandedGroups().remove(Instances.getGalleryPane().getExpandedGroups().indexOf(dataObject.getMergeID()));
		}
		if (!dataObject.getMergeGroup().contains(Instances.getTarget().getCurrentTarget()))
			Instances.getTarget().set(dataObject.getMergeGroup().getFirst());
		Instances.getReload().notify(Reload.Control.OBJ);
	}
}
