package client.stage.primary;

import client.EntityDetailsUtil;
import client.component.side.SidePaneBase;
import client.decorator.DecoratorUtil;
import client.node.EditNode;
import client.node.ProgressNode;
import client.node.menu.ListMenu;
import client.node.override.HBox;
import client.node.override.Scene;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import main.Main;
import main.Root;
import server.base.entity.Entity;
import server.control.reload.Notifier;
import server.control.reload.Reload;
import server.enums.Direction;
import server.lire.LireUtil;

public class MainStage extends Stage {
	public MainStage() {
		Scene mainScene = this.getMainScene();
		
		mainScene.getRoot().requestFocus();
		mainScene.widthProperty().addListener((observable, oldValue, newValue) -> this.onStageWidthChange());
		
		if (Main.DEBUG_UI_SCALING) {
			this.setWidth(DecoratorUtil.getUsableScreenWidth());
			this.setHeight(DecoratorUtil.getUsableScreenHeight());
			Root.GALLERY_PANE.widthProperty().addListener((observable, oldValue, newValue) -> {
				double availableWidth = MainStage.this.getScene().getWidth() - newValue.doubleValue();
				double width = availableWidth / 2;
				Root.FILTER_PANE.setPrefWidth(width);
				Root.SELECT_PANE.setPrefWidth(width);
			});
		}
		
		this.setScene(mainScene);
		this.centerOnScreen();
		this.setOnCloseRequest(event -> Main.exitApplication());
		//this.initStyle(StageStyle.UNDECORATED);
	}
	
	private void onStageWidthChange() {
		TilePane tiles = Root.GALLERY_PANE.getTilePane();
		
		double galleryTileSize = tiles.getPrefTileWidth();
		double sceneWidth = MainStage.this.getScene().getWidth();
		
		double width = 0;
		double availableWidth = sceneWidth - 2 * SidePaneBase.MIN_WIDTH;
		
		if (!Main.DEBUG_UI_SCALING) availableWidth = 300;
		
		double increment = galleryTileSize + tiles.getHgap();
		while (width + increment <= availableWidth) width += increment;
		
		int prefColumnsNew = (int) (width / galleryTileSize);
		int prefColumnsOld = tiles.getPrefColumns();
		
		if (prefColumnsNew <= 0) prefColumnsNew = 1;
		
		if (prefColumnsNew != prefColumnsOld) {
			tiles.setPrefColumns(prefColumnsNew);
			Root.GALLERY_PANE.setPrefViewportWidth(width);
		}
	}
	
	private BorderPane borderPane;
	private ProgressNode loadingBar;
	
	private Scene getMainScene() {
		loadingBar = new ProgressNode();
		
		borderPane = new BorderPane();
		borderPane.setTop(Root.TOOLBAR_PANE);
		borderPane.setLeft(Root.FILTER_PANE);
		borderPane.setCenter(new HBox(Root.GALLERY_PANE));
		HBox.setHgrow(Root.GALLERY_PANE, Priority.NEVER);
		borderPane.setRight(Root.SELECT_PANE);
		borderPane.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(borderPane);
		stackPane.getChildren().add(loadingBar);
		stackPane.setAlignment(Pos.BOTTOM_CENTER);
		
		Scene mainScene = new Scene(stackPane);
		mainScene.getStylesheets().add("/css/ScrollPane.css");
		
		this.initKeybinds(mainScene);
		
		return mainScene;
	}
	
	private void initKeybinds(Scene mainScene) {
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (mainScene.getFocusOwner() instanceof EditNode) {
				keybindsEditNode(event, mainScene);
			} else {
				keybindsGlobal(event);
			}
		});
	}
	private void keybindsGlobal(KeyEvent event) {
		ListMenu.hideMenus();
		switch (event.getCode()) {
			case SHIFT:
				Root.SELECT.setupShiftSelect();
				break;
			case F1:
				LireUtil.index();
				break;
			case F2:
				LireUtil.echo(99);
				break;
			case I:
				EntityDetailsUtil.show();
				break;
			case ESCAPE:
				Root.PSC.MAIN_STAGE.viewGallery();
				Reload.start();
				break;
			case TAB:
				Root.SELECT_PANE.getNodeSearch().requestFocus();
				break;
			case DELETE:
				Root.SELECT.deleteSelect();
				Reload.start();
				break;
			case E:
				if (Root.SELECT.getTarget().hasCollection()) {
					Root.SELECT.getTarget().getCollection().toggle();
				}
				Reload.start();
				break;
			case R:
				Root.SELECT.setRandom();
				Reload.start();
				break;
			case G:
				if (Root.SELECT.getTarget().hasCollection()) {
					Entity randomEntityFromCollection = Root.SELECT.getTarget().getCollection().getRandom();
					if (randomEntityFromCollection == null) {
						int i = 0;
					}
					Root.SELECT.setTarget(randomEntityFromCollection);
					if (!Root.SELECT.contains(randomEntityFromCollection)) {
						Root.SELECT.set(randomEntityFromCollection);
					}
					Reload.start();
				}
				break;
			case F:
				if (this.isViewGallery()) this.viewDisplay();
				else this.viewGallery();
				Reload.start();
				break;
			case W:
			case A:
			case S:
			case D:
				Root.SELECT.moveTarget(event);
				Reload.start();
				break;
		}
	}
	private void keybindsEditNode(KeyEvent event, Scene mainScene) {
		switch (event.getCode()) {
			case ESCAPE:
			case TAB:
				borderPane.requestFocus();
				event.consume();
				break;
			case UP:
				if (mainScene.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.UP, event.isControlDown());
					event.consume();
				}
				break;
			case DOWN:
				if (mainScene.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.DOWN, event.isControlDown());
					event.consume();
				}
				break;
		}
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			Root.DISPLAY_PANE.interruptVideoPlayer();
			
			borderPane.setCenter(Root.GALLERY_PANE);
			Root.GALLERY_PANE.requestFocus();
			Root.GALLERY_PANE.moveViewportToTarget();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	public void viewDisplay() {
		if (isViewGallery()) {
			borderPane.setCenter(Root.DISPLAY_PANE);
			Root.DISPLAY_PANE.requestFocus();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	
	public boolean isViewGallery() {
		return borderPane.getCenter() != Root.DISPLAY_PANE;
	}
	
	public void showLoadingBar(Object caller, int total) {
		if (Platform.isFxApplicationThread()) {
			loadingBar.setup(caller, total);
			loadingBar.setVisible(true);
		} else {
			Platform.runLater(() -> {
				loadingBar.setup(caller, total);
				loadingBar.setVisible(true);
			});
		}
	}
	public void hideLoadingBar(Object caller) {
		if (loadingBar.getCaller() == caller) {
			if (Platform.isFxApplicationThread()) {
				loadingBar.setVisible(false);
			} else {
				Platform.runLater(() -> loadingBar.setVisible(false));
			}
		}
	}
	public void advanceLoadingBar(Object caller) {
		if (Platform.isFxApplicationThread()) {
			loadingBar.advance(caller);
		} else {
			Platform.runLater(() -> loadingBar.advance(caller));
		}
	}
}
