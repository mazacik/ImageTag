package application.gui.stage;

import application.gui.decorator.ColorUtil;
import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.simple.TextNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SuppressWarnings("FieldCanBeLocal")
public class StageYesNo extends Stage implements StageBase {
	private boolean result;
	
	private TextNode labelContent;
	private TextNode buttonPositive;
	private TextNode buttonNegative;
	
	StageYesNo() {
		buttonPositive = new TextNode("Yes", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		buttonPositive.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				result = true;
				this.close();
			}
		});
		buttonNegative = new TextNode("No", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		buttonNegative.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				result = false;
				this.close();
			}
		});
		
		double padding = SizeUtil.getGlobalSpacing();
		labelContent = new TextNode("", ColorType.DEF, ColorType.DEF);
		labelContent.setText("Content");
		labelContent.setFont(Decorator.getFont());
		labelContent.setPadding(new Insets(0, 1.5 * padding, 0, 1.5 * padding));
		
		HBox hBox = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, buttonPositive, buttonNegative);
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(SizeUtil.getGlobalSpacing());
		
		VBox vBox = NodeUtil.getVBox(ColorType.DEF, ColorType.DEF);
		Scene scene = new Scene(vBox);
		vBox.getChildren().add(new TitleBar("Confirmation"));
		vBox.getChildren().add(labelContent);
		vBox.getChildren().add(hBox);
		vBox.setBackground(ColorUtil.getBackgroundDef());
		vBox.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		this.setOnShown(event -> this.centerOnScreen());
		this.initStyle(StageStyle.UNDECORATED);
		this.setScene(scene);
		this.setAlwaysOnTop(true);
		this.centerOnScreen();
	}
	
	@Override
	public Boolean _show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		this.showAndWait();
		return result;
	}
}
