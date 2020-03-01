package ui.custom;

import base.CustomList;
import enums.Direction;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
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
	public static void install(Region root) {
		for (ClickMenu staticInstance : staticInstances) {
			if (staticInstance.c == root.getClass()) {
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == staticInstance.mouseButton) {
						staticInstance.show(event, root, staticInstance.direction);
					}
				});
				break;
			}
		}
	}
	
	private ClickMenu(Region... children) {
		this(null, null, null, children);
	}
	public static ClickMenu install(Region root, Direction direction, MouseButton mouseButton, Region... children) {
		ClickMenu clickMenu = new ClickMenu(children);
		root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == mouseButton) {
				clickMenu.show(event, root, direction);
			}
		});
		return clickMenu;
	}
	
	private void show(MouseEvent event, Region root, Direction direction) {
		hideMenus();
		
		double x;
		double y;
		Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());//todo don't do bounds + mainstage.getinstance(), figure out something more simple
		
		switch (direction) {
			case POINT:
				this.show(root, event.getSceneX(), event.getSceneY());
				break;
			case UP:
				break;
			case DOWN:
				x = rootBounds.getMinX();
				y = rootBounds.getMaxY();
				this.show(MainStage.getInstance(), x, y);
				break;
			case LEFT:
				x = rootBounds.getMinX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
				this.show(MainStage.getInstance(), x, y);
				this.setAnchorX(this.getAnchorX() - this.getWidth());
				break;
			case RIGHT:
				x = rootBounds.getMaxX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
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
}
