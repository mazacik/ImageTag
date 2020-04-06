package ui.custom;

import base.CustomList;
import enums.Direction;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import main.Root;
import ui.decorator.Decorator;
import ui.override.VBox;

public class ClickMenu extends ListMenu {
	private static final CustomList<ClickMenu> staticInstances = new CustomList<>();
	
	private Class<?> c;
	
	private ClickMenu(Class<?> c, Region... children) {
		this.c = c;
		this.children = new CustomList<>(children);
		
		vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.setBorder(Decorator.getBorder(1));
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.addImpl(this);
	}
	public static void register(Class<?> c, Preset si) {
		staticInstances.addImpl(new ClickMenu(c, si.regions));
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton) {
		for (ClickMenu staticInstance : staticInstances) {
			if (staticInstance.c == root.getClass()) {
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton) {
						staticInstance.show(root, event, direction, 0, 0);
					}
				});
				return staticInstance;
			}
		}
		return null;
	}
	
	private ClickMenu(Region... children) {
		this.children = new CustomList<>(children);
		
		vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.setBorder(Decorator.getBorder(1));
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.addImpl(this);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, Preset preset) {
		return install(root, direction, mouseButton, 0, 0, preset.regions);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, Region... children) {
		return install(root, direction, mouseButton, 0, 0, children);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, double offsetX, double offsetY, Region... children) {
		ClickMenu clickMenu = new ClickMenu(children);
		root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == mouseButton) {
				clickMenu.show(root, event, direction, offsetX, offsetY);
			}
		});
		return clickMenu;
	}
	
	private void show(Region root, MouseEvent event, Direction direction, double offsetX, double offsetY) {
		hideMenus();
		resolveChildren();
		
		if (!this.isShowing()) {
			Bounds onScreenBounds;
			
			double x;
			double y;
			
			switch (direction) {
				case NONE:
					Point2D point = root.localToScreen(event.getX(), event.getY());
					x = point.getX() + offsetX;
					y = point.getY() + offsetY;
					this.show(Root.PSC.MAIN_STAGE, x, y);
					break;
				case UP:
					//onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					//x =
					//y =
					//this.show(Root.MAIN_STAGE, x, y);
					break;
				case DOWN:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMaxY() + offsetY;
					this.show(Root.PSC.MAIN_STAGE, x, y);
					break;
				case LEFT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(Root.PSC.MAIN_STAGE, x, y);
					this.setAnchorX(this.getAnchorX() - this.getWidth());
					break;
				case RIGHT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMaxX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(Root.PSC.MAIN_STAGE, x, y);
					break;
			}
		}
	}
}
