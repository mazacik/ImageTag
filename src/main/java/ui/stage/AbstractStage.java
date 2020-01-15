package ui.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.node.TextNode;
import ui.override.HBox;
import ui.override.VBox;

public abstract class AbstractStage extends Stage {
	private BorderPane paneMain;
	private TextNode errorNode;
	private HBox buttonBox;
	
	public AbstractStage(String title, boolean showErrorNode) {
		paneMain = new BorderPane();
		paneMain.setBackground(Decorator.getBackgroundPrimary());
		paneMain.setBorder(Decorator.getBorder(1));
		
		errorNode = new TextNode("", false, false, false, false);
		errorNode.setTextFill(Decorator.getColorNegative());
		
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		
		this.setScene(new Scene(paneMain));
		this.initStyle(StageStyle.UNDECORATED);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setOnShown(event -> this.centerOnScreen());
		
		paneMain.setTop(new TitleBar(this, title));
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
			root.setPadding(new Insets(5));
		}
		paneMain.setCenter(root);
	}
	public void setErrorMessage(String errorMessage) {
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
