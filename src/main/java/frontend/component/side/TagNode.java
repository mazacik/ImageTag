package frontend.component.side;

import backend.TagUtil;
import frontend.decorator.DecoratorUtil;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

public class TagNode extends BorderPane {
	
	private final TextNode tagNode = new TextNode();
	
	public TagNode(String tag) {
		tagNode.setText(tag);
		tagNode.setAlignment(Pos.CENTER_LEFT);
		
		this.setCenter(tagNode);
		BorderPane.setAlignment(tagNode, Pos.CENTER_LEFT);
		
		this.setPadding(new Insets(2, 5, 2, 5));
		this.setBorder(DecoratorUtil.getBorder(1));
		this.setMaxWidth(Double.MAX_VALUE);
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			switch (event.getButton()) {
				case SECONDARY:
					TagUtil.setCurrentNode(this);
					break;
			}
		});
		this.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(DecoratorUtil.getBackgroundSecondary()));
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		
		//		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY);
	}
	
	public String getText() {
		return tagNode.getText();
	}
	
}
