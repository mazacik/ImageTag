package application.gui.stage.template;

import application.gui.component.tooltip.Tooltip;
import application.gui.decorator.ColorUtil;
import application.gui.stage.base.StageBaseInterface;
import application.main.InstanceCollector;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CacheCompareStage extends YesNoCancelStage implements StageBaseInterface, InstanceCollector {
	private ImageView imageView1;
	private ImageView imageView2;
	
	private Tooltip tooltip1;
	private Tooltip tooltip2;
	
	public CacheCompareStage() {
		HBox hBoxImageViews = new HBox();
		
		hBoxImageViews.setSpacing(vBoxMain.getPadding().getLeft());
		
		imageView1 = new ImageView();
		imageView2 = new ImageView();
		
		tooltip1 = new Tooltip("", 250);
		tooltip2 = new Tooltip("", 250);
		
		tooltip1.getTextNode().setBackground(ColorUtil.getBackgroundDef());
		tooltip2.getTextNode().setBackground(ColorUtil.getBackgroundDef());
		
		Tooltip.install(imageView1, tooltip1);
		Tooltip.install(imageView2, tooltip2);
		
		hBoxImageViews.getChildren().add(imageView1);
		hBoxImageViews.getChildren().add(imageView2);
		
		hBoxImageViews.setAlignment(Pos.CENTER);
		
		//  index 1 places the pictures in between the text and the buttons
		vBoxMain.getChildren().add(1, hBoxImageViews);
	}
	
	@Override
	public Result show(String... args) {
		labelContent.setText("This file was likely renamed outside of the application.\nAre the pictures identical? If so, the file will keep its properties.");
		
		tooltip1.getTextNode().setText("File in Database: " + args[0]);
		tooltip2.getTextNode().setText("New File: " + args[1]);
		
		double tileSize = settings.getGalleryTileSize();
		imageView1.setImage(new Image("file:" + args[2], tileSize, tileSize, false, false));
		imageView2.setImage(new Image("file:" + args[3], tileSize, tileSize, false, false));
		
		this.showAndWait();
		
		return result;
	}
}
