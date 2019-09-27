package application.gui.panes.center;

import application.controller.Reload;
import application.database.object.DataObject;
import application.gui.nodes.ClickMenu;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.scene.input.MouseEvent;

public class GalleryTileEvent {
	public GalleryTileEvent(GalleryTile galleryTile) {
		onMouseClick(galleryTile);
	}
	
	private void onMouseClick(GalleryTile galleryTile) {
		galleryTile.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (isOnGroupButton(event, galleryTile)) {
						onGroupButtonPress(galleryTile.getParentDataObject());
						Instances.getReload().doReload();
						Instances.getGalleryPane().updateViewportTilesVisibility();
					} else {
						if (event.getClickCount() % 2 != 0) onLeftClick(galleryTile, event);
						else onLeftDoubleClick(event, galleryTile);
					}
					break;
				case SECONDARY:
					onRightClick(galleryTile);
					break;
				default:
					break;
			}
		});
	}
	private void onLeftClick(GalleryTile galleryTile, MouseEvent event) {
		DataObject dataObject = galleryTile.getParentDataObject();
		
		Instances.getTarget().set(dataObject);
		
		if (event.isControlDown()) {
			Instances.getSelect().swapState(dataObject);
		} else if (event.isShiftDown()) {
			Instances.getSelect().shiftSelectTo(dataObject);
		} else {
			Instances.getSelect().set(dataObject);
		}
		
		Instances.getReload().doReload();
		
		ClickMenu.hideAll();
	}
	private void onLeftDoubleClick(MouseEvent event, GalleryTile galleryTile) {
		if (!isOnGroupButton(event, galleryTile)) {
			Stages.getMainStage().swapViewMode();
			Instances.getReload().doReload();
		}
	}
	private void onRightClick(GalleryTile sender) {
		DataObject dataObject = sender.getParentDataObject();
		
		if (!Instances.getSelect().contains(dataObject)) {
			Instances.getSelect().set(dataObject);
		}
		
		Instances.getTarget().set(dataObject);
		Instances.getReload().doReload();
	}
	
	private boolean isOnGroupButton(MouseEvent event, GalleryTile galleryTile) {
		int tileSize = Instances.getSettings().getGalleryTileSize();
		boolean hitWidth = event.getX() >= tileSize - galleryTile.getGroupEffectWidth() - 5;
		boolean hitHeight = event.getY() <= galleryTile.getGroupEffectHeight();
		return hitWidth && hitHeight;
	}
	public static void onGroupButtonPress(DataObject dataObject) {
		if (dataObject.getJointID() == 0) return;
		if (!Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getJointID())) {
			Instances.getGalleryPane().getExpandedGroups().add(dataObject.getJointID());
		} else {
			//noinspection RedundantCollectionOperation
			Instances.getGalleryPane().getExpandedGroups().remove(Instances.getGalleryPane().getExpandedGroups().indexOf(dataObject.getJointID()));
		}
		if (!dataObject.getJointObjects().contains(Instances.getTarget().getCurrentTarget())) {
			Instances.getTarget().set(dataObject.getJointObjects().getFirst());
		}
		
		Instances.getReload().notify(Reload.Control.OBJ);
	}
}
