package ui.custom;

import base.CustomList;
import enums.Direction;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import ui.decorator.Decorator;
import ui.main.stage.MainStage;
import ui.override.VBox;

public class HoverMenu extends Popup {
	private static final CustomList<HoverMenu> instances = new CustomList<>();
	
	private final Region root;
	private final CustomList<Region> children;
	
	public HoverMenu(Region root, Direction direction, double offsetX, double offsetY, Region... children) {
		this.root = root;
		this.children = new CustomList<>(children);
		
		root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			if (!this.isShowing()) this.show(event, root, direction, offsetX, offsetY);
			hideMenus(root);
		});
		
		VBox vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.getChildren().setAll(children);
		vBox.setBorder(Decorator.getBorder(1));
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.add(this);
	}
	
	private void show(MouseEvent event, Region root, Direction direction, double offsetX, double offsetY) {
		double x;
		double y;
		Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
		
		switch (direction) {
			case POINT:
				this.show(root, event.getSceneX(), event.getSceneY());
				break;
			case UP:
				break;
			case DOWN:
				x = rootBounds.getMinX() + offsetX;
				y = rootBounds.getMaxY() + offsetY;
				this.show(MainStage.getInstance(), x, y);
				break;
			case LEFT:
				x = rootBounds.getMinX() + offsetX;
				y = rootBounds.getMinY() + offsetY;
				this.show(MainStage.getInstance(), x, y);
				this.setAnchorX(this.getAnchorX() - this.getWidth());
				break;
			case RIGHT:
				x = rootBounds.getMaxX() + offsetX;
				y = rootBounds.getMinY() + offsetY;
				this.show(MainStage.getInstance(), x, y);
				break;
		}
	}
	
	public static void hideMenus() {
		instances.forEach(clickMenu -> {
			if (clickMenu.isShowing()) {
				clickMenu.hide();
			}
		});
	}
	public static void hideMenus(Region root) {
		instances.forEach(clickMenu -> {
			if (root != clickMenu.root && !clickMenu.children.contains(root) && clickMenu.isShowing()) {
				clickMenu.hide();
			}
		});
	}
}
