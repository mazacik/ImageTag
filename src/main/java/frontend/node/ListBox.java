package frontend.node;

import frontend.node.override.VBox;
import javafx.collections.ObservableList;
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
