package frontend.component.side;

import backend.TagUtil;
import backend.misc.Direction;
import frontend.decorator.DecoratorUtil;
import frontend.node.menu.ClickMenu;
import frontend.node.menu.ListMenu;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Main;

public class TagNode extends BorderPane {
	private final TextNode tagNode = new TextNode();
	private final TextNode countNode = new TextNode();
	
	static {
		ClickMenu.register(TagNode.class, ListMenu.Preset.TAG);
	}
	
	public TagNode(String tag, int count) {
		tagNode.setText(tag);
		tagNode.setAlignment(Pos.CENTER_LEFT);
		
		int percent = (int) Math.floor((double) count / (double) Main.SELECT.size() * 100);
		countNode.setText(percent + "%");
		countNode.setPrefWidth(50);
		countNode.setAlignment(Pos.CENTER_RIGHT);
		
		this.setMinWidth(200);
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
		
		this.setLeft(tagNode);
		this.setRight(countNode);
		
		ClickMenu.install(this, Direction.NONE, MouseButton.SECONDARY);
	}
	
	public String getText() {
		return tagNode.getText();
	}
}
