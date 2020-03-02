package ui.custom;

import base.CustomList;
import enums.Direction;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import ui.decorator.Decorator;
import ui.main.stage.MainStage;
import ui.override.VBox;

public class ClickMenu extends Popup {
	private static final CustomList<ClickMenu> instances = new CustomList<>();
	private static final CustomList<ClickMenu> staticInstances = new CustomList<>();
	
	private final Class<?> c;
	private final Direction direction;
	private final MouseButton mouseButton;
	
	private ClickMenu(Class<?> c, Direction direction, MouseButton mouseButton, Region... children) {
		this.c = c;
		this.direction = direction;
		this.mouseButton = mouseButton;
		
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
	public static void register(Class<?> c, Direction direction, MouseButton mouseButton, Region... children) {
		staticInstances.add(new ClickMenu(c, direction, mouseButton, children));
	}
	public static ClickMenu install(Region root) {
		return install(root, 0, -1);
	}
	public static ClickMenu install(Region root, double offsetX, double offsetY) {
		for (ClickMenu staticInstance : staticInstances) {
			if (staticInstance.c == root.getClass()) {
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == staticInstance.mouseButton) {
						staticInstance.show(event, root, staticInstance.direction, offsetX, offsetY);
					}
				});
				return staticInstance;
			}
		}
		return null;
	}
	
	private ClickMenu(Region... children) {
		this(null, null, null, children);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, Region... children) {
		return install(root, direction, mouseButton, 0, -1, children);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, double offsetX, double offsetY, Region... children) {
		ClickMenu clickMenu = new ClickMenu(children);
		root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == mouseButton) {
				clickMenu.show(event, root, direction, offsetX, offsetY);
			}
		});
		return clickMenu;
	}
	
	private void show(MouseEvent event, Region root, Direction direction, double offsetX, double offsetY) {
		hideMenus();
		
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
}
