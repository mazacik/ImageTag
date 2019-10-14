package application.gui.nodes;

import application.data.list.CustomList;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.stage.Stages;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class ClickMenu extends Popup {
	private static String name;
	private static String group;
	
	public static String getName() {
		return name;
	}
	public static String getGroup() {
		return group;
	}
	
	public static void setName(String name) {
		ClickMenu.name = name;
	}
	public static void setGroup(String group) {
		ClickMenu.group = group;
	}
	
	protected static final ArrayList<ClickMenu> instanceList = new ArrayList<>();
	protected VBox vBox;
	
	protected CustomList<Node> getValidNodes() {
		CustomList<Node> list = new CustomList<>();
		
		list.add(ButtonTemplates.OBJECT_OPEN.get());
		list.add(ButtonTemplates.OBJECT_EDIT.get());
		list.add(ButtonTemplates.FILTER_SIMILAR.get());
		list.add(ButtonTemplates.OBJECT_REVERSE_IMAGE_SEARCH.get());
		list.add(new SeparatorNode());
		list.add(ButtonTemplates.OBJECT_COPY_NAME.get());
		list.add(ButtonTemplates.OBJECT_COPY_PATH.get());
		list.add(new SeparatorNode());
		list.add(ButtonTemplates.OBJECT_DELETE.get());
		if (Instances.getSelect().size() > 1) {
			list.add(ButtonTemplates.SELECTION_DELETE.get());
			list.add(new SeparatorNode());
			if (Instances.getSelect().isSelectJoint()) list.add(ButtonTemplates.JOINT_OBJECT_DISCARD.get());
			else list.add(ButtonTemplates.JOINT_OBJECT_CREATE.get());
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
	private static ClickMenu clickMenuTags = new ClickMenu(ButtonTemplates.TAG_EDIT.get(), ButtonTemplates.TAG_REMOVE.get());
	private static ClickMenu clickMenuSelect = new ClickMenu(ButtonTemplates.SELECTION_SET_ALL.get(), ButtonTemplates.SELECTION_SET_NONE.get());
	
	public static void install(Region root, Direction direction, Region... labels) {
		new ClickMenu(root, direction, labels);
	}
	public static void install(Region root, Direction direction, MouseButton mouseButton, StaticInstance staticInstance) {
		switch (staticInstance) {
			case DATA:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuData.show(root, direction);
					}
				});
				break;
			case TAGS:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuTags.show(root, direction);
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
			case DATA:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuData.show(root, event);
					}
				});
				break;
			case TAGS:
				root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
					if (event.getButton() == mouseButton && event.getSource() == root) {
						clickMenuTags.show(root, event);
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
		vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		vBox.getChildren().setAll(children);
		vBox.setBackground(ColorUtil.getBackgroundDef());
		
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
		
		this.addEventFilter(WindowEvent.WINDOW_SHOWN, event -> NodeUtil.equalizeWidth(vBox.getChildren()));
		this.getContent().setAll(vBox);
		this.setAutoHide(true);
		this.setHideOnEscape(true);
		
		instanceList.add(this);
	}
	
	public void show(Region root, Direction direction) {
		hideAll();
		
		double x;
		double y;
		Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());
		
		switch (direction) {
			case LEFT:
				x = rootBounds.getMinX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
				this.show(Stages.getMainStage(), x, y);
				this.setAnchorX(this.getAnchorX() - this.getWidth());
				break;
			case RIGHT:
				x = rootBounds.getMaxX();
				y = rootBounds.getMinY();
				if (root.getBorder() != null) y -= root.getBorder().getInsets().getBottom();
				this.show(Stages.getMainStage(), x, y);
				break;
			case DOWN:
				x = rootBounds.getMinX();
				y = rootBounds.getMaxY();
				this.show(Stages.getMainStage(), x, y);
				break;
		}
	}
	public void show(Node anchor, MouseEvent event) {
		hideAll();
		super.show(anchor, event.getScreenX(), event.getScreenY());
	}
	public static void show(Node anchor, MouseEvent event, StaticInstance staticInstance) {
		hideAll();
		if (event.getPickResult().getIntersectedNode() == anchor) {
			switch (staticInstance) {
				case DATA:
					clickMenuData.show(anchor, event.getScreenX(), event.getScreenY());
					break;
				case TAGS:
					clickMenuTags.show(anchor, event.getScreenX(), event.getScreenY());
					break;
				case SELECT:
					clickMenuSelect.show(anchor, event.getScreenX(), event.getScreenY());
					break;
			}
		}
	}
	
	public enum StaticInstance {
		DATA,
		TAGS,
		SELECT
	}
}
