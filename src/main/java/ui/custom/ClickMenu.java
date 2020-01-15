package ui.custom;

import base.CustomList;
import base.entity.EntityCollectionUtil;
import control.Select;
import enums.Direction;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import ui.decorator.Decorator;
import ui.main.side.TagNode;
import ui.main.stage.StageMain;
import ui.node.BoxSeparator;
import ui.node.NodeTemplates;
import ui.override.VBox;

public class ClickMenu extends Popup {
	private static TagNode tagNode;
	public static TagNode getTagNode() {
		return tagNode;
	}
	public static void setTagNode(TagNode tagNode) {
		ClickMenu.tagNode = tagNode;
	}
	
	protected static final CustomList<ClickMenu> instanceList = new CustomList<>();
	protected VBox vBox;
	
	protected CustomList<Node> getValidNodes() {
		CustomList<Node> list = new CustomList<>();
		
		list.add(NodeTemplates.ENTITY_OPEN_FILE.get());
		list.add(NodeTemplates.ENTITY_SHOW_EXPLORER.get());
		list.add(NodeTemplates.ENTITY_EDIT_PAINT.get());
		list.add(NodeTemplates.FILTER_SIMILAR.get());
		list.add(NodeTemplates.ENTITY_REVERSE_IMAGE_SEARCH.get());
		list.add(new BoxSeparator());
		list.add(NodeTemplates.ENTITY_COPY_NAME.get());
		list.add(NodeTemplates.ENTITY_COPY_PATH.get());
		list.add(new BoxSeparator());
		list.add(NodeTemplates.SELECTION_DELETE.get());
		if (Select.getEntities().size() > 1) {
			list.add(new BoxSeparator());
			if (EntityCollectionUtil.isCollection(Select.getEntities())) {
				list.add(NodeTemplates.COLLECTION_DISCARD.get());
			} else {
				list.add(NodeTemplates.COLLECTION_CREATE.get());
			}
		}
		return list;
	}
	private static ClickMenu clickMenuData = new ClickMenu() {
		@Override
		public void show(Node anchor, MouseEvent event) {
			vBox.getChildren().setAll(getValidNodes());
			super.show(anchor, event);
		}
		@Override
		public void show(Region root, Direction direction) {
			vBox.getChildren().setAll(getValidNodes());
			super.show(root, direction);
		}
	};
	private static ClickMenu clickMenuTag = new ClickMenu(NodeTemplates.TAG_EDIT.get(), NodeTemplates.TAG_REMOVE.get());
	private static ClickMenu clickMenuSelect = new ClickMenu(NodeTemplates.SELECTION_SET_ALL.get(), NodeTemplates.SELECTION_SET_NONE.get());
	
	public static void install(Region root, Direction direction, Region... children) {
		new ClickMenu(root, direction, children);
	}
	public static void install(Region root, Direction direction, MouseButton mouseButton, StaticInstance staticInstance) {
		switch (staticInstance) {
			case ENTITY:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuData.show(root, direction);
					}
				});
				break;
			case TAG:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuTag.show(root, direction);
					}
				});
				break;
			case SELECT:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuSelect.show(root, direction);
					}
				});
				break;
		}
	}
	public static void install(Node root, MouseButton mouseButton, StaticInstance staticInstance) {
		switch (staticInstance) {
			case ENTITY:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuData.show(root, event);
					}
				});
				break;
			case TAG:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuTag.show(root, event);
					}
				});
				break;
			case SELECT:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuSelect.show(root, event);
					}
				});
				break;
		}
	}
	
	public static void hideAll() {
		instanceList.forEach(leftClickMenu -> {
			if (leftClickMenu.isShowing()) {
				leftClickMenu.hide();
			}
		});
	}
	
	private ClickMenu(Region... children) {
		this(null, null, children);
	}
	private ClickMenu(Region root, Direction direction, Region... children) {
		vBox = new VBox();
		vBox.setBorder(Decorator.getBorder(1, 1, 1, 1));
		vBox.getChildren().setAll(children);
		vBox.setBackground(Decorator.getBackgroundPrimary());
		
		if (root != null) {
			root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getButton() == MouseButton.PRIMARY && event.getSource() == root) {
					this.show(root, direction);
				}
			});
			root.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
				if (!this.isShowing()) {
					for (ClickMenu clickMenu : instanceList) {
						if (clickMenu.isShowing()) {
							clickMenu.hide();
							show(root, direction);
							break;
						}
					}
				}
			});
			
			EventHandler<WindowEvent> eventHandler = event -> root.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event1 -> hideAll());
			this.sceneProperty().addListener((observable, oldScene, newScene) -> {
				if (newScene.getWindow() == null) {
					newScene.windowProperty().addListener((observable1, oldStage, newStage) -> {
						if (newStage != null) newStage.addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
					});
				} else {
					newScene.getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
				}
			});
		}
		
		for (Node node : vBox.getChildren()) {
			((Region) node).setMaxWidth(Double.MAX_VALUE);
		}
		
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instanceList.add(this);
	}
	
	protected void show(Region root, Direction direction) {
		hideAll();
		
		double x;
		double y;
		Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
		
		switch (direction) {
			case LEFT:
				x = rootBounds.getMinX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
				this.show(StageMain.getInstance(), x, y);
				this.setAnchorX(this.getAnchorX() - this.getWidth());
				break;
			case RIGHT:
				x = rootBounds.getMaxX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
				this.show(StageMain.getInstance(), x, y);
				break;
			case DOWN:
				x = rootBounds.getMinX();
				y = rootBounds.getMaxY();
				this.show(StageMain.getInstance(), x, y);
				break;
		}
	}
	protected void show(Node anchor, MouseEvent event) {
		hideAll();
		super.show(anchor, event.getScreenX(), event.getScreenY());
	}
	public static void show(Node anchor, MouseEvent event, StaticInstance staticInstance) {
		hideAll();
		if (event.getPickResult().getIntersectedNode() == anchor) {
			switch (staticInstance) {
				case ENTITY:
					clickMenuData.show(anchor, event.getScreenX(), event.getScreenY());
					break;
				case TAG:
					clickMenuTag.show(anchor, event.getScreenX(), event.getScreenY());
					break;
				case SELECT:
					clickMenuSelect.show(anchor, event.getScreenX(), event.getScreenY());
					break;
			}
		}
	}
	
	public enum StaticInstance {
		ENTITY,
		TAG,
		SELECT,
		;
	}
}
