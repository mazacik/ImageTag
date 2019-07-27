package userinterface.nodes.node;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;

public class TitleBar extends BorderPane {
	private final TextNode labelTitle;
	
	public TitleBar() {
		this("", true);
	}
	public TitleBar(boolean movement) {
		this("", movement);
	}
	public TitleBar(String title) {
		this(title, true);
	}
	public TitleBar(String title, boolean movement) {
		labelTitle = new TextNode(title, ColorType.DEF, ColorType.DEF);
		labelTitle.setFont(StyleUtil.getFont());
		labelTitle.setPadding(new Insets(1, 5, 1, 5));
		BorderPane.setAlignment(labelTitle, Pos.CENTER_LEFT);
		
		TextNode btnExit = new TextNode("✕", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.ALT);
		btnExit.setPadding(new Insets(1, 5, 1, 5));
		btnExit.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				btnExit.fireEvent(new WindowEvent(this.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
		BorderPane.setAlignment(btnExit, Pos.CENTER);
		
		this.setLeft(labelTitle);
		this.setRight(btnExit);
		this.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		StyleUtil.addToManager(this, ColorType.DEF);
		
		if (movement) {
			final double[] xOffset = {0};
			final double[] yOffset = {0};
			final boolean[] clickOnExit = {false};
			this.setOnMousePressed(event -> {
				if (event.getPickResult().getIntersectedNode().getParent().equals(btnExit)) {
					clickOnExit[0] = true;
				}
				xOffset[0] = this.getScene().getWindow().getX() - event.getScreenX();
				yOffset[0] = this.getScene().getWindow().getY() - event.getScreenY();
			});
			this.setOnMouseDragged(event -> {
				if (!clickOnExit[0]) {
					this.getScene().getWindow().setX(event.getScreenX() + xOffset[0]);
					this.getScene().getWindow().setY(event.getScreenY() + yOffset[0]);
				}
			});
			this.setOnMouseReleased(event -> clickOnExit[0] = false);
		}
	}
	
	public void setTitle(String title) {
		labelTitle.setText(title);
	}
}
