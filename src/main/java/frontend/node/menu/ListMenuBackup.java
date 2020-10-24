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

public class ListMenuBackup extends Popup {
	private static final ArrayList<ListMenuBackup> instances = new ArrayList<>();
	public static ArrayList<ListMenuBackup> getInstances() {
		return instances;
	}
	
	private final BaseList<Region> children;
	private final VBox vBox;
	
	public ListMenuBackup(Region... children) {
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
	
	public static ListMenuBackup install(
			Region root,
			Direction direction,
			MenuTrigger trigger,
			Region... regions
	) {
		return install(root, new ListMenuBackup(regions), direction, trigger, 0, 0);
	}
	public static ListMenuBackup install(
			Region root,
			MenuPreset preset,
			Direction direction,
			MenuTrigger trigger
	) {
		return install(root, preset, direction, trigger, 0, 0);
	}
	public static ListMenuBackup install(
			Region root,
			MenuPreset preset,
			Direction direction,
			MenuTrigger trigger,
			double offsetX,
			double offsetY
	) {
		return install(root, preset.getInstance(), direction, trigger, offsetX, offsetY);
	}
	
	public static ListMenuBackup install(
			Region root,
			ListMenuBackup instance,
			Direction direction,
			MenuTrigger trigger,
			double offsetX,
			double offsetY
	) {
		switch (trigger) {
			case CLICK_LEFT:
			case CLICK_RIGHT:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == getMouseButton(trigger)) {
						instance.showClick(root, event, direction, offsetX, offsetY);
					}
				});
				root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					ListMenuBackup instanceShowing = ListMenuBackup.getShowingInstance();
					if (instanceShowing != null && instanceShowing != instance) {
						instanceShowing.hide();
						instance.showHover(root, event, direction, offsetX, offsetY);
					}
				});
				return instance;
			case HOVER:
				root.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
					if (event.getButton() == getMouseButton(trigger)) {
						instance.showHover(root, event, direction, offsetX, offsetY);
					}
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
	
	private void showClick(Region root, MouseEvent event, Direction direction, double offsetX, double offsetY) {
		hideMenus();
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
	private void showHover(Region root, MouseEvent event, Direction direction, double offsetX, double offsetY) {
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
	public static void hideMenus(ListMenuBackup except) {
		instances.forEach(listMenu -> {
			if (listMenu != except) {
				listMenu.hide();
			}
		});
	}
	
	public static ListMenuBackup getShowingInstance() {
		for (ListMenuBackup instance : instances) {
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
