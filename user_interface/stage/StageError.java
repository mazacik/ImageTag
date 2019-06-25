package user_interface.stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import user_interface.nodes.NodeUtil;
import user_interface.nodes.base.TextNode;
import user_interface.nodes.buttons.ButtonFactory;
import user_interface.nodes.buttons.ButtonTemplates;
import user_interface.nodes.node.TitleBar;
import user_interface.style.ColorUtil;
import user_interface.style.SizeUtil;
import user_interface.style.StyleUtil;
import user_interface.style.enums.ColorType;

public class StageError extends Stage implements StageBase {
	private TextNode labelContent;
	
	StageError() {
		TextNode buttonPositive = ButtonFactory.getInstance().get(ButtonTemplates.STAGE_OK);

        double padding = SizeUtil.getGlobalSpacing();
		labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
		labelContent.setText("Error");
        labelContent.setFont(StyleUtil.getFont());
        labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));

        VBox vBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
        Scene scene = new Scene(vBox);
		vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(new TitleBar(scene, "Error"));
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
