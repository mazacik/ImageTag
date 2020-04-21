package frontend.stage;

import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseStage extends Stage {
	private static final int DEFAULT_PADDING = 3;
	
	private BorderPane paneMain;
	private TextNode errorNode;
	private HBox buttonBox;
	
	public BaseStage(String title, boolean showErrorNode) {
		paneMain = new BorderPane();
		paneMain.setBackground(DecoratorUtil.getBackgroundPrimary());
		paneMain.setBorder(DecoratorUtil.getBorder(1));
		
		errorNode = new TextNode("", false, false, false, false);
		errorNode.setTextFill(DecoratorUtil.getColorNegative());
		
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(0, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING));
		
		this.setScene(new Scene(paneMain));
		this.initModality(Modality.APPLICATION_MODAL);
		this.setOnShown(event -> this.centerOnScreen());
		this.setWidth(DecoratorUtil.getUsableScreenWidth() / 2);
		this.setHeight(DecoratorUtil.getUsableScreenHeight() / 2);
		
		//paneMain.setTop(new TitleBar(this, title));
		if (showErrorNode) {
			VBox boxBottom = new VBox(errorNode, buttonBox);
			boxBottom.setAlignment(Pos.CENTER);
			paneMain.setBottom(boxBottom);
		} else {
			paneMain.setBottom(buttonBox);
		}
	}
	
	public void setRoot(Region root) {
		if (root.getPadding() == null) {
			root.setPadding(new Insets(DEFAULT_PADDING));
		}
		paneMain.setCenter(root);
	}
	public void setErrorMessage(String errorMessage) {
		//todo remove this, if needed, replace with SimpleMessageStage
		errorNode.setText(errorMessage);
	}
	public void setButtons(TextNode... buttons) {
		buttonBox.getChildren().clear();
		buttonBox.getChildren().addAll(buttons);
	}
	
	public void setBorder(Border border) {
		paneMain.setBorder(border);
	}
	
	@Override
	public void showAndWait() {
		if (this.isIconified() || this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
