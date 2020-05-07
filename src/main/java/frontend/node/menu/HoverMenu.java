package frontend.node.menu;

import backend.list.BaseList;
import backend.misc.Direction;
import frontend.decorator.DecoratorUtil;
import frontend.node.override.VBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import main.Main;

public class HoverMenu extends ListMenu {
	private HoverMenu(Region... children) {
		this.children = new BaseList<>(children);
		
		vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.setBorder(DecoratorUtil.getBorder(1));
		vBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.add(this);
	}
	public static HoverMenu install(Region root, Direction direction, Region... children) {
		return install(root, direction, 0, -1, children);
	}
	public static HoverMenu install(Region root, Direction direction, double offsetX, double offsetY, Region... children) {
		HoverMenu hoverMenu = new HoverMenu(children);
		root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> hoverMenu.show(root, event, direction, offsetX, offsetY));
		return hoverMenu;
	}
	
	private void show(Region root, MouseEvent event, Direction direction, double offsetX, double offsetY) {
		hideMenus(this);
		resolveChildrenVisible();
		
		if (!this.isShowing()) {
			Bounds onScreenBounds;
			
			double x;
			double y;
			
			switch (direction) {
				case NONE:
					Point2D point = root.localToScreen(event.getX(), event.getY());
					x = point.getX() + offsetX;
					y = point.getY() + offsetY;
					this.show(Main.STAGE, x, y);
					break;
				case UP:
					//onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					//x =
					//y =
					//this.show(main.Root.MAIN_STAGE, x, y);
					break;
				case DOWN:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMaxY() + offsetY;
					this.show(Main.STAGE, x, y);
					break;
				case LEFT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(Main.STAGE, x, y);
					this.setAnchorX(this.getAnchorX() - this.getWidth());
					break;
				case RIGHT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMaxX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(Main.STAGE, x, y);
					break;
			}
		}
	}
}
