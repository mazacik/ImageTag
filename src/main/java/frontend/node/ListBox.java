package frontend.node;

import backend.misc.Direction;
import frontend.decorator.DecoratorUtil;
import frontend.node.override.VBox;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

public class ListBox extends ScrollPane {
	private final VBox vBox = new VBox();
	
	private Region currentFocus;
	
	public ListBox() {
		this.setContent(vBox);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		this.setBackground(Background.EMPTY);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
	}
	private void onScroll(ScrollEvent event) {
		event.consume();
		
		if (!this.getNodes().isEmpty()) {
			Region region = (Region) vBox.getChildren().get(0);
			double rowHeight = region.getHeight();
			
			double contentHeight = vBox.getHeight() - this.getViewportBounds().getHeight();
			double rowToContentRatio = rowHeight / contentHeight;
			double currentVvalue = this.getVvalue();
			
			if (event.getDeltaY() > 0) {
				//mouse-scroll-up
				this.setVvalue(currentVvalue - rowToContentRatio);
			} else {
				//mouse-scroll-down
				this.setVvalue(currentVvalue + rowToContentRatio);
			}
		}
	}
	
	public void moveViewportToNode(Region node) {
		if (this.getHeight() > 0) {
			int index = vBox.getChildren().indexOf(node);
			if (index >= 0) {
				Bounds viewportBounds = vBox.sceneToLocal(this.getViewportBounds());
				Bounds matchBounds = node.getBoundsInParent();
				
				double viewportHeight = viewportBounds.getHeight();
				double contentHeight = vBox.getHeight() - viewportHeight;
				
				double rowToContentRatio = node.getHeight() / contentHeight;
				double viewportToContentRatio = viewportHeight / contentHeight;
				
				double viewportTop = viewportBounds.getMinY();
				double viewportBottom = viewportBounds.getMaxY();
				
				double tileTop = matchBounds.getMinY();
				double tileBottom = matchBounds.getMaxY();
				
				double vValue = -1;
				if (tileBottom > viewportBottom) {
					vValue = (index + 1) * rowToContentRatio - viewportToContentRatio;
				} else if (tileTop < viewportTop) {
					vValue = index * rowToContentRatio;
				}
				if (vValue >= 0) {
					this.setVvalue(vValue);
				}
			}
		}
	}
	
	public void moveFocus(Direction direction) {
		int newFocusIndex;
		if (currentFocus == null) {
			newFocusIndex = 0;
		} else {
			currentFocus.setBackground(DecoratorUtil.getBackgroundPrimary());
			int currentFocusIndex = vBox.getChildren().indexOf(currentFocus);
			newFocusIndex = calcNewFocusIndex(currentFocusIndex, direction);
		}
		Node newFocus = vBox.getChildren().get(newFocusIndex);
		if (newFocus instanceof Region) {
			Region region = (Region) newFocus;
			region.setBackground(DecoratorUtil.getBackgroundSecondary());
			currentFocus = region;
			moveViewportToNode(region);
		}
	}
	private int calcNewFocusIndex(int currentFocusIndex, Direction direction) {
		if (direction == Direction.UP) {
			if (currentFocusIndex == -1) {
				return 0;
			} else if (currentFocusIndex > 0) {
				return currentFocusIndex - 1;
			} else {
				return vBox.getChildren().size() - 1;
			}
		} else if (direction == Direction.DOWN) {
			if (currentFocusIndex < vBox.getChildren().size() - 1) {
				return currentFocusIndex + 1;
			} else {
				return 0;
			}
		}
		return 0;
	}
	
	public ObservableList<Node> getNodes() {
		return vBox.getChildren();
	}
	
	public Region getCurrentFocus() {
		return currentFocus;
	}
	
	public void setCurrentFocus(Region currentFocus) {
		this.currentFocus = currentFocus;
	}
}
