package frontend.stage.primary.scene;

import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.lire.LireUtil;
import backend.list.entity.Entity;
import backend.misc.Direction;
import frontend.EntityDetailsUtil;
import frontend.component.side.SidePaneBase;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.ProgressNode;
import frontend.node.menu.ListMenu;
import frontend.node.override.HBox;
import frontend.node.override.Scene;
import frontend.node.override.VBox;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import main.Root;

public class MainScene extends Scene {
	private final VBox vBox;
	private final HBox hBox;
	private final ProgressNode loadingBar;
	
	public MainScene() {
		loadingBar = new ProgressNode();
		loadingBar.setVisible(false);
		
		hBox = new HBox(Root.FILTER_PANE, Root.GALLERY_PANE, Root.SELECT_PANE);
		VBox.setVgrow(hBox, Priority.ALWAYS);
		vBox = new VBox(Root.TOOLBAR_PANE, hBox);
		vBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(vBox);
		stackPane.getChildren().add(loadingBar);
		stackPane.setAlignment(Pos.BOTTOM_CENTER);
		
		this.setRoot(stackPane);
		this.getStylesheets().add("/css/Styles.css");
		this.widthProperty().addListener((observable, oldValue, newValue) -> this.onStageWidthChange());
	}
	public void processKeyEvent(KeyEvent event) {
		if (this.getFocusOwner() instanceof EditNode) {
			keybindsEditNode(event);
		} else {
			keybindsGlobal(event);
		}
	}
	private void keybindsGlobal(KeyEvent event) {
		ListMenu.hideMenus();
		switch (event.getCode()) {
			case BACK_QUOTE:
				
				break;
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
				this.viewGallery();
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
					Entity randomEntityFromCollection = Root.SELECT.getTarget().getCollection().getRepresentingRandom();
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
	private void keybindsEditNode(KeyEvent event) {
		switch (event.getCode()) {
			case ESCAPE:
			case TAB:
				vBox.requestFocus();
				event.consume();
				break;
			case UP:
				if (this.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.UP, event.isControlDown());
					event.consume();
				}
				break;
			case DOWN:
				if (this.getFocusOwner() == Root.SELECT_PANE.getNodeSearch()) {
					Root.SELECT_PANE.nextMatch(Direction.DOWN, event.isControlDown());
					event.consume();
				}
				break;
		}
	}
	
	public void viewGallery() {
		if (!isViewGallery()) {
			Root.DISPLAY_PANE.interruptVideoPlayer();
			
			hBox.getChildren().setAll(Root.FILTER_PANE, Root.GALLERY_PANE, Root.SELECT_PANE);
			
			Root.GALLERY_PANE.requestFocus();
			Root.GALLERY_PANE.moveViewportToTarget();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	public void viewDisplay() {
		if (isViewGallery()) {
			hBox.getChildren().setAll(Root.FILTER_PANE, Root.DISPLAY_PANE, Root.SELECT_PANE);
			
			Root.DISPLAY_PANE.requestFocus();
			
			Reload.notify(Notifier.VIEWMODE_CHANGED);
		}
	}
	
	public boolean isViewGallery() {
		return hBox.getChildren().contains(Root.GALLERY_PANE);
	}
	
	public void showLoadingBar(Thread caller, int total) {
		loadingBar.setup(caller, total);
		
		if (Platform.isFxApplicationThread()) {
			loadingBar.setVisible(true);
		} else {
			Platform.runLater(() -> loadingBar.setVisible(true));
		}
	}
	public void hideLoadingBar(Thread caller) {
		if (loadingBar.getCaller() == caller) {
			if (Platform.isFxApplicationThread()) {
				loadingBar.setVisible(false);
			} else {
				Platform.runLater(() -> loadingBar.setVisible(false));
			}
		}
	}
	public void advanceLoadingBar(Thread caller) {
		if (Platform.isFxApplicationThread()) {
			loadingBar.advance(caller);
		} else {
			Platform.runLater(() -> loadingBar.advance(caller));
		}
	}
	
	private void onStageWidthChange() {
		double estimateAvailableWidth = this.getWidth() - 2 * SidePaneBase.MIN_WIDTH - 50;
		
		TilePane tilePane = Root.GALLERY_PANE.getTilePane();
		double tileSize = tilePane.getPrefTileWidth();
		double increment = tileSize + tilePane.getHgap();
		
		double calcViewportWidth = 0;
		while (calcViewportWidth + increment <= estimateAvailableWidth) {
			calcViewportWidth += increment;
		}
		
		int prefColumnsNew = (int) (calcViewportWidth / tileSize);
		int prefColumnsOld = tilePane.getPrefColumns();
		
		if (prefColumnsNew <= 0) prefColumnsNew = 1;
		
		if (prefColumnsNew != prefColumnsOld) {
			tilePane.setPrefColumns(prefColumnsNew);
		}
	}
}
