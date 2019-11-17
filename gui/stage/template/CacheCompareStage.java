package gui.stage.template;

import gui.component.simple.TextNode;
import gui.component.tooltip.Tooltip;
import gui.stage.base.StageBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.InstanceCollector;

public class CacheCompareStage extends StageBase implements InstanceCollector {
	private TextNode labelContent;
	private ButtonBooleanValue result;
	
	private ImageView imageView1;
	private ImageView imageView2;
	
	private Tooltip tooltip1;
	private Tooltip tooltip2;
	
	public CacheCompareStage() {
		super("Confirmation");
		
		labelContent = new TextNode("", false, false, false, false);
		
		imageView1 = new ImageView();
		imageView2 = new ImageView();
		
		tooltip1 = new Tooltip("", 250);
		tooltip2 = new Tooltip("", 250);
		
		Tooltip.install(imageView1, tooltip1);
		Tooltip.install(imageView2, tooltip2);
		
		HBox hBoxImageViews = new HBox(imageView1, imageView2);
		hBoxImageViews.setAlignment(Pos.CENTER);
		hBoxImageViews.setSpacing(5);
		
		TextNode buttonPositive = new TextNode("Yes", true, true, false, true);
		buttonPositive.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.YES;
			this.close();
		});
		TextNode buttonNegative = new TextNode("No", true, true, false, true);
		buttonNegative.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.NO;
			this.close();
		});
		TextNode buttonCancel = new TextNode("Cancel", true, true, false, true);
		buttonCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = ButtonBooleanValue.CANCEL;
			this.close();
		});
		
		VBox vBoxMain = new VBox(labelContent, hBoxImageViews);
		vBoxMain.setPadding(new Insets(5));
		vBoxMain.setSpacing(5);
		
		this.setRoot(vBoxMain);
		this.setButtons(buttonPositive, buttonNegative, buttonCancel);
	}
	
	@Override
	public ButtonBooleanValue show(String... args) {
		labelContent.setText("This file was likely renamed outside of the application.\nAre the pictures identical? If so, the file will keep its properties.");
		
		tooltip1.getTextNode().setText("File in Database: " + args[0]);
		tooltip2.getTextNode().setText("New File: " + args[1]);
		
		double tileSize = settings.getTileSize();
		imageView1.setImage(new Image("file:" + args[2], tileSize, tileSize, false, false));
		imageView2.setImage(new Image("file:" + args[3], tileSize, tileSize, false, false));
		
		this.showAndWait();
		return result;
	}
}
