package userinterface.stage;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.buttons.ButtonFactory;
import userinterface.nodes.buttons.ButtonTemplates;
import userinterface.nodes.node.TitleBar;
import userinterface.style.ColorUtil;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;

public class StageError extends Stage implements StageBase {
	private TextNode labelContent;
	
	StageError() {
		TextNode buttonPositive = ButtonFactory.getInstance().get(ButtonTemplates.STAGE_OK);
		
		labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
		labelContent.setText("Error");
		labelContent.setFont(StyleUtil.getFont());
		
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
