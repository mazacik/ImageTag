package application.gui.panes.top;

import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.scene.SceneUtil;
import application.gui.stage.StageUtil;
import application.main.Instances;
import application.misc.FileUtil;
import javafx.scene.input.MouseButton;
import javafx.stage.WindowEvent;

public class ToolbarPaneEvent {
	public ToolbarPaneEvent() {
		onMouseClick();
		
		onAction_menuSave();
		onAction_menuImport();
		onAction_menuSettings();
		onAction_menuExit();
		
		onAction_menuRandom();
		onAction_menuFullView();
	}
	
	private void onMouseClick() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.setOnMouseClicked(event -> toolbarPane.requestFocus());
	}
	
	private void onAction_menuSave() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeSave().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getObjectListMain().writeToDisk();
				Instances.getTagListMain().writeDummyToDisk();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuImport() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeImport().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				FileUtil.importFiles();
				ClickMenuLeft.hideAll();
			}
		});
	}
	private void onAction_menuSettings() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeSettings().setOnMouseClicked(event -> StageUtil.showStageSettings());
	}
	private void onAction_menuExit() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeExit().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				toolbarPane.fireEvent(new WindowEvent(Instances.getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});
	}
	
	private void onAction_menuRandom() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeRandom().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getSelect().setRandom();
				Instances.getReload().doReload();
			}
		});
	}
	private void onAction_menuFullView() {
		ToolbarPane toolbarPane = Instances.getToolbarPane();
		toolbarPane.getNodeFullview().setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				SceneUtil.swapViewMode();
				Instances.getReload().doReload();
			}
		});
	}
}
