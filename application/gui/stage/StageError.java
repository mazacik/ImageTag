package application.gui.stage;

import application.gui.decorator.ColorUtil;
import application.gui.decorator.Decorator;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.buttons.ButtonFactory;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.simple.TextNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageError extends Stage implements StageBase {
	private TextNode labelContent;
	
	StageError() {
		TextNode buttonPositive = ButtonFactory.getInstance().get(ButtonTemplates.STAGE_OK);
		
		labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
		labelContent.setText("Error");
		labelContent.setFont(Decorator.getFont());
		
		VBox vBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		Scene scene = new Scene(vBox);
		vBox.setAlignment(Pos.CENTER);
		vBox.getChildren().add(new TitleBar("Error"));
		vBox.getChildren().add(labelContent);
		vBox.getChildren().add(buttonPositive);
		vBox.setBackground(ColorUtil.getBackgroundDef());
		vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		this.setOnShown(event -> this.centerOnScreen());
		this.initStyle(StageStyle.UNDECORATED);
		this.setScene(scene);
		this.setAlwaysOnTop(true);
	}
	
	@Override
	public Object _show(String... args) {
		labelContent.setText(args[0]);
		this.showAndWait();
		return null;
	}
}
