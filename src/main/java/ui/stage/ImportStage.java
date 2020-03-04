package ui.stage;

import base.entity.Entity;
import base.entity.EntityList;
import cache.CacheManager;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import misc.FileUtil;
import misc.Project;
import misc.Settings;
import ui.node.CheckboxNode;
import ui.node.EditNode;
import ui.node.SwitchNode;
import ui.node.textnode.TextNode;
import ui.override.GridPane;
import ui.override.HBox;
import ui.override.VBox;

import java.io.File;
import java.util.logging.Logger;

public class ImportStage extends AbstractStage {
	private VBox boxStage1;
	private TextNode nodeOK;
	private TextNode nodeCancel;
	
	private VBox boxStage2;
	private TextNode nodeMessage;
	private CheckboxNode nodeSetupFilter;
	private TextNode nodeFinish;
	
	public ImportStage() {
		super("Import", true);
		createStage1();
		createStage2();
	}
	
	private void createStage1() {
		TextNode nodeTextPath = new TextNode("Import Path", false, false, false, true);
		EditNode nodeEditPath = new EditNode(Settings.IMPORT_LAST_PATH.getValue(), "");
		TextNode nodeBrowse = new TextNode("Browse", true, true, true, true);
		nodeBrowse.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select a directory to import");
			directoryChooser.setInitialDirectory(new File(Settings.IMPORT_LAST_PATH.getValue()));
			File directory = directoryChooser.showDialog(this);
			if (directory != null) {
				nodeEditPath.setText(directory.getAbsolutePath());
			}
		});
		HBox boxPath = new HBox(nodeTextPath, nodeEditPath, nodeBrowse);
		boxPath.setAlignment(Pos.CENTER);
		
		SwitchNode nodeMode = new SwitchNode("Copy", "Move", 150);
		CheckboxNode nodeEmptyFolders = new CheckboxNode("Remove Empty Folders", Direction.LEFT, true);
		CheckboxNode nodeThread = new CheckboxNode("Work in Background", Direction.LEFT, true);
		
		nodeMode.selectRight();
		nodeMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			setImportMode(ImportMode.COPY);
			nodeEmptyFolders.setDisable(true);
			nodeMode.selectLeft();
		});
		nodeMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			setImportMode(ImportMode.MOVE);
			nodeEmptyFolders.setDisable(false);
			nodeMode.selectRight();
		});
		
		GridPane paneOptions = new GridPane();
		paneOptions.setVgap(3);
		paneOptions.setAlignment(Pos.CENTER);
		paneOptions.add(nodeEmptyFolders, 0, 0);
		paneOptions.add(nodeThread, 0, 1);
		
		boxStage1 = new VBox(boxPath, nodeMode, paneOptions);
		boxStage1.setAlignment(Pos.CENTER);
		boxStage1.setSpacing(3);
		boxStage1.setPadding(new Insets(3));
		
		nodeOK = new TextNode("Import", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			File directory = new File(nodeEditPath.getText());
			if (directory.exists() && directory.isDirectory()) {
				Settings.IMPORT_LAST_PATH.setValue(directory.getAbsolutePath());
				
				boolean useThread = nodeThread.isChecked();
				if (!useThread) {
					setRoot(boxStage2);
					importFiles(directory, useThread);
				} else {
					close();
					importFilesUsingThread(directory);
				}
			} else {
				setErrorMessage("Import Path Invalid");
			}
		});
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
	}
	private void createStage2() {
		nodeMessage = new TextNode("", false, false, false, true);
		nodeSetupFilter = new CheckboxNode("Filter Imported Files", Direction.LEFT, true);
		nodeSetupFilter.setAlignment(Pos.CENTER);
		
		nodeFinish = new TextNode("Finish", true, true, false, true);
		nodeFinish.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (nodeSetupFilter.isChecked()) setupFilter();
			
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
			Reload.start();
			
			close();
		});
		
		boxStage2 = new VBox(nodeMessage, nodeSetupFilter);
		boxStage2.setAlignment(Pos.CENTER);
		boxStage2.setSpacing(3);
		boxStage2.setPadding(new Insets(3));
		
		//todo if there was a problem importing a file, show a message
	}
	
	private void importFilesUsingThread(File directory) {
		if (thread == null || !thread.isAlive()) {
			thread = new Thread(() -> {
				if (importFiles(directory, true)) {
					Logger.getGlobal().info("IMPORT: THREAD FINISHED");
				} else {
					Logger.getGlobal().info("IMPORT: THREAD INTERRUPTED");
				}
			});
			thread.start();
		}
	}
	private boolean importFiles(File directory, boolean usingThread) {
		EntityList entities = this.importEntities(directory, usingThread);
		if (entities == null) return false;
		
		if (!entities.isEmpty()) {
			CacheManager.checkCacheInBackground(entities);
			
			EntityList.getMain().addAll(entities);
			EntityList.getMain().sort();
			
			Filter.getLastImport().setAll(entities);
		}
		
		displayResults(entities, usingThread);
		return true;
	}
	
	private EntityList importEntities(File directory, boolean useThread) {
		String directoryPath = directory.getAbsolutePath();
		
		EntityList entities = new EntityList();
		for (File file : FileUtil.getSupportedFiles(directory)) {
			if (useThread && Thread.currentThread().isInterrupted()) return null;
			entities.add(this.importEntity(file, directoryPath));
		}
		
		return entities;
	}
	private Entity importEntity(File file, String directoryPath) {
		String pathOld = file.getAbsolutePath();
		String pathNew = Project.getCurrent().getDirectorySource() + File.separator + FileUtil.createEntityName(file, directoryPath);
		
		File fileNew = new File(pathNew);
		if (!fileNew.exists()) {
			FileUtil.moveFile(pathOld, pathNew);
			return new Entity(fileNew);
		} else {
			Logger.getGlobal().info("IMPORT: COULD NOT IMPORT \"" + file.getName() + "\", ALREADY EXISTS");
			return null;
		}
	}
	
	private void displayResults(EntityList entities, boolean usingThread) {
		if (!usingThread) {
			_displayResults(entities);
		} else {
			_displayResultsUsingThread(entities);
		}
	}
	private void _displayResults(EntityList entities) {
		setErrorMessage("");
		setButtons(nodeFinish);
		nodeMessage.setText(getMessageImportCount(entities.size()));
		
		if (entities.isEmpty()) {
			nodeSetupFilter.setDisable(true);
			nodeSetupFilter.setChecked(false);
		}
	}
	private void _displayResultsUsingThread(EntityList entities) {
		Platform.runLater(() -> {
			if (!entities.isEmpty()) {
				String message = getMessageImportCount(entities.size()) + "\nSet filter to recently imported files?";
				if (ConfirmationStage.show(message)) setupFilter();
				
				Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
				Reload.start();
			} else {
				SimpleMessageStage.show(getMessageImportCount(entities.size()));
			}
		});
	}
	
	private String getMessageImportCount(int importCount) {
		return "Imported " + importCount + " files.";
	}
	private void setupFilter() {
		Filter.getSettings().setShowImages(true);
		Filter.getSettings().setShowGifs(true);
		Filter.getSettings().setShowVideos(true);
		Filter.getSettings().setShowOnlyNewEntities(true);
		Filter.getSettings().setEnableLimit(false);
	}
	
	public void show(@SuppressWarnings("unused") String... args) {
		setRoot(boxStage1);
		setButtons(nodeOK, nodeCancel);
		showAndWait();
	}
	
	private static Thread thread = null;
	public static Thread getThread() {
		return thread;
	}
	
	ImportMode importMode = ImportMode.MOVE;
	private enum ImportMode {
		COPY,
		MOVE
	}
	private void setImportMode(ImportMode importMode) {
		this.importMode = importMode;
	}
}
