package frontend.stage.primary.scene;

import backend.entity.Entity;
import backend.lire.LireUtil;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.EditNode;
import frontend.node.ProgressNode;
import frontend.node.SeparatorNode;
import frontend.node.menu.ListMenu;
import frontend.node.override.Scene;
import frontend.node.override.VBox;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import main.Main;

public class MainScene extends Scene {
	private final VBox vBox;
	private final ProgressNode loadingBar;
	
	public static final double SIDEPANE_WIDTH = 300;
	
	public MainScene() {
		loadingBar = new ProgressNode();
		loadingBar.setVisible(false);
		
		vBox = new VBox(UserInterface.getToolbarPane(), new SeparatorNode(), UserInterface.getSelectPane(), new SeparatorNode(), UserInterface.getCenterPane());
		vBox.setBackground(DecoratorUtil.getBackgroundPrimary());
		
		StackPane stackPane = new StackPane();
		stackPane.getChildren().add(vBox);
		stackPane.getChildren().add(loadingBar);
		stackPane.setAlignment(Pos.BOTTOM_CENTER);
		
		this.setRoot(stackPane);
		this.getStylesheets().add("/css/Styles.css");
	}
	public void processKeyEvent(KeyEvent event) {
		if (!(this.getFocusOwner() instanceof EditNode) && !(this.getFocusOwner() instanceof ComboBox)) {
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
				UserInterface.getCenterPane().showGalleryPane();
				Reload.start();
				break;
			case TAB:
				UserInterface.getSelectPane().getTagsPane().getSearchNode().requestFocus();
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
}
