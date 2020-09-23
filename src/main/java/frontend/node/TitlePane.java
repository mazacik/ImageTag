package frontend.node;

import frontend.decorator.DecoratorUtil;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class TitlePane extends BorderPane {
	private final TextNode titleNode;
	
	public TitlePane(String title, Node content) {
		titleNode = new TextNode(title);
		titleNode.setTranslateX(8);
		titleNode.setTranslateY(-14);
		StackPane.setAlignment(titleNode, Pos.TOP_LEFT);
		
		Rectangle rectangle = new Rectangle();
		titleNode.widthProperty().addListener((observable, oldValue, newValue) -> rectangle.setWidth(newValue.doubleValue() + 8));
		rectangle.setHeight(1);
		rectangle.setTranslateX(4);
		rectangle.setTranslateY(-1);
		rectangle.setFill(DecoratorUtil.getBackgroundPrimary().getFills().get(0).getFill());
		StackPane.setAlignment(rectangle, Pos.TOP_LEFT);
		
		BorderPane contentOffsetHelper = new BorderPane(content);
		contentOffsetHelper.setPadding(new Insets(8, 0, 0, 0));
		
		StackPane stackPane = new StackPane(rectangle, titleNode, contentOffsetHelper);
		stackPane.setBorder(DecoratorUtil.getBorder(1));
		
		BorderPane thisOffsetHelper = new BorderPane(stackPane);
		thisOffsetHelper.setPadding(new Insets(8, 0, 0, 0));
		
		stackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
				if (stackPane.getChildren().contains(contentOffsetHelper)) {
					if (event.getY() >= -8 && event.getY() <= 8) {
						stackPane.getChildren().remove(contentOffsetHelper);
					}
				} else {
					stackPane.getChildren().add(contentOffsetHelper);
				}
			}
		});
		
		this.setCenter(thisOffsetHelper);
	}
	
	public String getTitle() {
		return titleNode.getText();
	}
	public void setTitle(String text) {
		titleNode.setText(text);
	}
}
