package frontend.stage.primary.scene;

import backend.entity.Entity;
import backend.lire.LireUtil;
import backend.misc.Direction;
import backend.reload.Reload;
import frontend.UserInterface;
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
import main.Main;

import static frontend.component.side.SidePaneBase.WIDTH;

public class MainScene extends Scene {
	private final VBox vBox;
	private final HBox hBox;
	private final ProgressNode loadingBar;
	
	public MainScene() {
		loadingBar = new ProgressNode();
		loadingBar.setVisible(false);
		
		hBox = new HBox(UserInterface.getFilterPane(), UserInterface.getCenterPane(), UserInterface.getSelectPane());
		VBox.setVgrow(hBox, Priority.ALWAYS);
		vBox = new VBox(UserInterface.getToolbarPane(), hBox);
		vBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(vBox);
		stackPane.getChildren().add(loadingBar);
		stackPane.setAlignment(Pos.BOTTOM_CENTER);
		
		this.setRoot(stackPane);
		this.getStylesheets().add("/css/Styles.css");
		this.widthProperty().addListener((observable, oldValue, newValue) -> this.handleWidthChange());
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
			case SHIFT:
				Main.SELECT.setupShiftSelect();
				break;
			case F1:
				LireUtil.index();
				break;
			case F2:
				LireUtil.echo(99);
				break;
			case ESCAPE:
				UserInterface.getCenterPane().getGalleryPane().toBack(); // TODO test
				Reload.start();
				break;
			case TAB:
				UserInterface.getSelectPane().getSelectionTagsPane().getNodeSearch().requestFocus();
				break;
			case DELETE:
				Main.SELECT.deleteSelect();
				Reload.start();
				break;
			case E:
				if (Main.SELECT.getTarget().hasGroup()) {
					Main.SELECT.getTarget().getEntityGroup().toggle();
				}
				Reload.start();
				break;
			case R:
				Main.SELECT.setRandom();
				Reload.start();
				break;
			case G:
				if (Main.SELECT.getTarget().hasGroup()) {
					Entity randomEntityFromGroup = Main.SELECT.getTarget().getEntityGroup().getRepresentingRandom();
					if (randomEntityFromGroup != null) {
						Main.SELECT.setTarget(randomEntityFromGroup);
						if (!Main.SELECT.contains(randomEntityFromGroup)) {
							Main.SELECT.set(randomEntityFromGroup);
						}
						Reload.start();
					}
				}
				break;
			case F:
				UserInterface.getCenterPane().swapCurrentPane();
				Reload.start();
				break;
			case W:
			case S:
			case D:
				Main.SELECT.moveTarget(event);
				Reload.start();
				break;
			case A:
				if (event.isControlDown()) {
					Main.SELECT.setAll(Main.FILTER);
				} else {
					Main.SELECT.moveTarget(event);
				}
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
				if (this.getFocusOwner() == UserInterface.getSelectPane().getSelectionTagsPane().getNodeSearch()) {
					UserInterface.getSelectPane().getSelectionTagsPane().nextMatch(Direction.UP, event.isControlDown());
					event.consume();
				}
				break;
			case DOWN:
				if (this.getFocusOwner() == UserInterface.getSelectPane().getSelectionTagsPane().getNodeSearch()) {
					UserInterface.getSelectPane().getSelectionTagsPane().nextMatch(Direction.DOWN, event.isControlDown());
					event.consume();
				}
				break;
		}
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
	
	public void handleWidthChange() {
		handleWidthChange(WIDTH, WIDTH);
	}
	public void handleWidthChange(double filterWidth, double selectWidth) {
		double estimateAvailableWidth = this.getWidth() - filterWidth - selectWidth - 20;
		
		TilePane tilePane = UserInterface.getCenterPane().getGalleryPane().getTilePane();
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
