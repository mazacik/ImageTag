package application.gui.scene;

import application.gui.decorator.Decorator;
import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.main.Instances;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class MainScene {
	private ObservableList<Node> panes;
	private final Scene mainScene;
	
	public MainScene() {
		mainScene = create();
	}
	
	private Scene create() {
		HBox mainHBox = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF, Instances.getFilterPane(), Instances.getGalleryPane(), Instances.getSelectPane());
		panes = mainHBox.getChildren();
		
		Scene scene = new Scene(NodeUtil.getVBox(ColorType.DEF, ColorType.DEF, Instances.getToolbarPane(), mainHBox));
		
		scene.widthProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageWidthChangeHandler());
		scene.heightProperty().addListener((observable, oldValue, newValue) -> SizeUtil.stageHeightChangehandler());
		
		return scene;
	}
	public void show() {
		Instances.getMainStage().setScene(mainScene);
		Instances.getGalleryPane().requestFocus();
		
		Decorator.applyScrollbarStyle(Instances.getGalleryPane());
		Decorator.applyScrollbarStyle(Instances.getFilterPane().getScrollPane());
		Decorator.applyScrollbarStyle(Instances.getSelectPane().getScrollPane());
	}
	
	public ObservableList<Node> getPanes() {
		return panes;
	}
}
