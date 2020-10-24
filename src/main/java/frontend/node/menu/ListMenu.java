package frontend.node.menu;

import backend.BaseList;
import backend.misc.Direction;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.override.VBox;
import frontend.node.textnode.ArrowTextNode;
import frontend.node.textnode.ArrowTextNodeTemplates;
import frontend.node.textnode.TextNode;
import frontend.node.textnode.TextNodeTemplates;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

import java.util.ArrayList;

public class ListMenu extends Popup {
	private static final ArrayList<ListMenu> templates = new ArrayList<>();
	private static final ArrayList<ListMenu> instances = new ArrayList<>();
	public static ArrayList<ListMenu> getTemplates() {
		return templates;
	}
	
	private MenuTrigger trigger;
	private final BaseList<Region> children;
	private final VBox vBox;
	private final boolean transientFocus;
	
	public ListMenu(BaseList<Region> children, boolean transientFocus) {
		this.children = children;
		this.transientFocus = transientFocus;
		
		vBox = new VBox();
		vBox.setMinWidth(200);
		vBox.setBorder(DecoratorUtil.getBorder(1));
		vBox.setBackground(DecoratorUtil.getBackgroundDefault());
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instances.add(this);
	}
	
	public static ListMenu install(Region root, Direction direction, MenuTrigger trigger, Region... regions) {
		return install(root, direction, trigger, 0, 0, new ListMenu(new BaseList<>(regions), true));
	}
	public static ListMenu install(Region root, Direction direction, MenuTrigger trigger, ListMenu instance) {
		return install(root, direction, trigger, 0, 0, instance);
	}
	public static ListMenu install(Region root, Direction direction, MenuTrigger trigger, double offsetX, double offsetY, ListMenu instance) {
		instance.trigger = trigger;
		switch (trigger) {
			case CLICK_LEFT:
			case CLICK_RIGHT:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == getMouseButton(trigger)) {
						hideMenus();
						instance.show(root, event, direction, offsetX, offsetY);
					}
				});
				root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					ListMenu showingInstance = getShowingInstance();
					if (showingInstance != null && showingInstance != instance && showingInstance.transientFocus && instance.transientFocus) {
						hideMenus();
						instance.show(root, event, direction, offsetX, offsetY);
					}
				});
				return instance;
			case HOVER:
				root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					hideHoverMenus();
					instance.show(root, event, direction, offsetX, offsetY);
				});
				return instance;
			default:
				return null;
		}
	}
	
	private static MouseButton getMouseButton(MenuTrigger trigger) {
		switch (trigger) {
			case CLICK_LEFT:
				return MouseButton.PRIMARY;
			case CLICK_RIGHT:
				return MouseButton.SECONDARY;
			default:
				return null;
		}
	}
	
	private void show(Region root, MouseEvent event, Direction direction, double offsetX, double offsetY) {
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
					this.show(UserInterface.getStage(), x, y);
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
					this.show(UserInterface.getStage(), x, y);
					break;
				case LEFT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMinX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(UserInterface.getStage(), x, y);
					this.setAnchorX(this.getAnchorX() - this.getWidth());
					break;
				case RIGHT:
					onScreenBounds = root.localToScreen(root.getBoundsInLocal());
					x = onScreenBounds.getMaxX() + offsetX;
					y = onScreenBounds.getMinY() + offsetY;
					this.show(UserInterface.getStage(), x, y);
					break;
			}
		}
	}
	
	private void resolveChildrenVisible() {
		BaseList<Region> resultList = new BaseList<>();
		
		for (Region child : children) {
			if (child instanceof TextNode) {
				TextNode textNode = (TextNode) child;
				for (TextNodeTemplates template : TextNodeTemplates.values()) {
					if (textNode.getTemplate() == template) {
						if (template.resolveVisible()) {
							resultList.add(textNode);
						}
						break;
					}
				}
			} else if (child instanceof ArrowTextNode) {
				ArrowTextNode arrowTextNode = (ArrowTextNode) child;
				for (ArrowTextNodeTemplates template : ArrowTextNodeTemplates.values()) {
					if (arrowTextNode.getTemplate() == template) {
						if (template.resolveVisible()) {
							resultList.add(arrowTextNode);
						}
						break;
					}
				}
			} else {
				resultList.add(child);
			}
		}
		
		vBox.getChildren().setAll(resultList);
	}
	
	public static void hideMenus() {
		instances.forEach(PopupWindow::hide);
	}
	public static void hideHoverMenus() {
		instances.forEach(instance -> {
			if (instance.trigger == MenuTrigger.HOVER) {
				instance.hide();
			}
		});
	}
	
	public static ListMenu getShowingInstance() {
		for (ListMenu instance : instances) {
			if (instance.isShowing()) {
				return instance;
			}
		}
		return null;
	}
	
	public enum MenuTrigger {
		CLICK_LEFT,
		CLICK_RIGHT,
		HOVER
	}
}
