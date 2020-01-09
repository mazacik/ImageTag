package ui.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.HBox;
import ui.override.VBox;

public abstract class StageBase extends Stage {
	private VBox vBoxMain;
	
	private TitleBar titleBar;
	
	private NodeText errorNode;
	private boolean showErrorNode;
	
	private HBox buttonBox;
	private boolean showButtonBox;
	
	private boolean defaultPadding;
	
	public StageBase(String title, boolean showErrorNode, boolean showButtonBox, boolean defaultPadding) {
		vBoxMain = new VBox();
		vBoxMain.setAlignment(Pos.TOP_CENTER);
		vBoxMain.setBackground(Decorator.getBackgroundPrimary());
		vBoxMain.setBorder(Decorator.getBorder(1));
		
		titleBar = new TitleBar(this, title);
		errorNode = new NodeText("", false, false, false, false);
		errorNode.setTextFill(Decorator.getColorNegative());
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		
		this.showErrorNode = showErrorNode;
		this.showButtonBox = showButtonBox;
		
		this.defaultPadding = defaultPadding;
		
		this.setScene(new Scene(vBoxMain));
		this.initStyle(StageStyle.UNDECORATED);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setOnShown(event -> this.centerOnScreen());
	}
	
	public void setRoot(Region root) {
		vBoxMain.getChildren().clear();
		vBoxMain.getChildren().add(titleBar);
		vBoxMain.getChildren().add(root);
		if (showErrorNode) vBoxMain.getChildren().add(errorNode);
		if (showButtonBox) vBoxMain.getChildren().add(buttonBox);
		
		if (defaultPadding) {
			root.setPadding(new Insets(5, 5, 5, 5));
		}
	}
	public void setErrorMessage(String errorMessage) {
		errorNode.setText(errorMessage);
	}
	public void setButtons(NodeText... buttons) {
		buttonBox.getChildren().clear();
		buttonBox.getChildren().addAll(buttons);
	}
	
	public void setBorder(Border border) {
		vBoxMain.setBorder(border);
	}
	
	@Override
	public void showAndWait() {
		if (this.isIconified() || this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
