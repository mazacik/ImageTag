package ui.custom;

import base.CustomList;
import enums.Direction;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import ui.decorator.Decorator;
import ui.main.stage.MainStage;
import ui.override.VBox;

public class HoverMenu extends Popup {
	private static final CustomList<HoverMenu> instances = new CustomList<>();
	
	private final Region root;
	private final CustomList<Region> children;
	
	private HoverMenu(Region root, Region... children) {
		this.root = root;
		this.children = new CustomList<>(children);
		
		VBox vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.getChildren().setAll(children);
		vBox.setBorder(Decorator.getBorder(1));
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.addImpl(this);
	}
	public static HoverMenu install(Region root, Direction direction, Region... children) {
		return install(root, direction, 0, -1, children);
	}
	public static HoverMenu install(Region root, Direction direction, double offsetX, double offsetY, Region... children) {
		HoverMenu hoverMenu = new HoverMenu(root, children);
		root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> hoverMenu.show(event, root, direction, offsetX, offsetY));
		return hoverMenu;
	}
	
	private void show(MouseEvent event, Region root, Direction direction, double offsetX, double offsetY) {
		hideMenus(root);
		
		if (!this.isShowing()) {
			Bounds onScreenBounds;
			
			double x;
			double y;
			
			switch (direction) {
				case NONE:
					Point2D point = root.localToScreen(event.getX(), event.getY());
					x = point.getX() + offsetX;
					y = point.getY() + offsetY;
					this.show(MainStage.getInstance(), x, y);
					break;
				case UP:
					//onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					//x =
					//y =
					//this.show(MainStage.getInstance(), x, y);
					break;
				case DOWN:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMaxY() + offsetY;
					this.show(MainStage.getInstance(), x, y);
					break;
				case LEFT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(MainStage.getInstance(), x, y);
					this.setAnchorX(this.getAnchorX() - this.getWidth());
					break;
				case RIGHT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMaxX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(MainStage.getInstance(), x, y);
					break;
			}
		}
	}
	
	public static void hideMenus() {
		instances.forEach(PopupWindow::hide);
	}
	public static void hideMenus(Region root) {
		instances.forEach(clickMenu -> {
			if (root != clickMenu.root && !clickMenu.children.contains(root)) {
				clickMenu.hide();
			}
		});
	}
}
