package frontend.node;

import backend.BaseList;
import frontend.component.side.TagNode;
import frontend.node.override.VBox;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;

import java.util.Collection;

public class ListBox extends ScrollPane {
	private final VBox vBox = new VBox();
	
	public ListBox() {
		this.setContent(vBox);
		this.setFitToWidth(true);
		this.setFitToHeight(true);
		this.setBackground(Background.EMPTY);
		this.setHbarPolicy(ScrollBarPolicy.NEVER);
		this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		this.addEventFilter(ScrollEvent.SCROLL, this::onScroll);
	}
	
	public ObservableList<Node> getNodes() {
		return vBox.getChildren();
	}
	public void setNodes(Collection<? extends Node> collection) {
		vBox.getChildren().setAll(collection);
	}
	
	public void moveViewportToMatch(BaseList<TagNode> tagNodes, TagNode match) {
		if (this.getHeight() > 0) {
			int matchIndex = tagNodes.indexOf(match);
			if (matchIndex >= 0) {
				Bounds viewportBounds = vBox.sceneToLocal(this.getViewportBounds());
				Bounds matchBounds = match.getBoundsInParent();
				
				double viewportHeight = viewportBounds.getHeight();
				double contentHeight = vBox.getHeight() - viewportHeight;
				
				double rowToContentRatio = match.getHeight() / contentHeight;
				double viewportToContentRatio = viewportHeight / contentHeight;
				
				double viewportTop = viewportBounds.getMinY();
				double viewportBottom = viewportBounds.getMaxY();
				
				double tileTop = matchBounds.getMinY();
				double tileBottom = matchBounds.getMaxY();
				
				double vValue = -1;
				if (tileBottom > viewportBottom) {
					vValue = (matchIndex + 1) * rowToContentRatio - viewportToContentRatio;
				} else if (tileTop < viewportTop) {
					vValue = matchIndex * rowToContentRatio;
				}
				if (vValue >= 0) {
					this.setVvalue(vValue);
				}
			}
		}
	}
	private void onScroll(ScrollEvent event) {
		event.consume();
		
		if (!this.getChildren().isEmpty()) {
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
}
