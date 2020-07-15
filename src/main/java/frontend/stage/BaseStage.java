package frontend.stage;

import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class BaseStage extends Stage {
	private static final int DEFAULT_PADDING = 3;
	
	private final BorderPane paneMain;
	private final HBox buttonBox;
	
	public BaseStage(String title, double sizeModifier) {
		buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(0, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING));
		
		paneMain = new BorderPane();
		paneMain.setBackground(DecoratorUtil.getBackgroundPrimary());
		paneMain.setBottom(buttonBox);
		
		this.setScene(new Scene(paneMain));
		this.setTitle(title);
		this.initModality(Modality.APPLICATION_MODAL);
		this.setOnShown(event -> this.centerOnScreen());
		this.setWidth(DecoratorUtil.getUsableScreenWidth() * sizeModifier);
		this.setHeight(DecoratorUtil.getUsableScreenHeight() * sizeModifier);
	}
	
	public void setRoot(Region root) {
		if (root.getPadding() == null) {
			root.setPadding(new Insets(DEFAULT_PADDING));
		}
		
		paneMain.setCenter(root);
	}
	public void setButtons(TextNode... buttons) {
		buttonBox.getChildren().clear();
		buttonBox.getChildren().addAll(buttons);
	}
	
	@Override
	public void showAndWait() {
		if (this.isIconified() || this.isShowing()) this.setIconified(false);
		else super.showAndWait();
	}
}
