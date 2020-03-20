package ui.stage;

import base.CustomList;
import base.entity.Entity;
import base.entity.EntityList;
import cache.CacheUtil;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import main.Root;
import misc.FileUtil;
import misc.Project;
import misc.Settings;
import thread.Thread;
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
	private static final int MIN_FILE_COUNT_TO_SHOW_LOADING_BAR = 10;
	
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
			directoryChooser.setInitialDirectory(FileUtil.getLastImportDirectory());
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
		
		VBox boxStage1 = new VBox(boxPath, nodeMode, paneOptions);
		boxStage1.setAlignment(Pos.CENTER);
		boxStage1.setSpacing(3);
		boxStage1.setPadding(new Insets(3));
		
		TextNode nodeOK = new TextNode("Import", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			File directory = new File(nodeEditPath.getText());
			if (directory.exists() && directory.isDirectory()) {
				Settings.IMPORT_LAST_PATH.setValue(directory.getAbsolutePath());
				
				if (nodeThread.isChecked()) {
					close();
					importFilesUsingThread(directory);
				} else {
					setRoot(boxStage2);
					importFiles(directory);
				}
			} else {
				setErrorMessage("Import Path Invalid");
			}
		});
		TextNode nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setRoot(boxStage1);
		this.setButtons(nodeOK, nodeCancel);
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
	
	private boolean importFiles(File directory) {
		EntityList entities = this.importEntities(directory);
		if (entities == null) return false;
		
		if (!entities.isEmpty()) {
			CacheUtil.loadCache(entities);
			
			EntityList.getMain().addAllImpl(entities);
			EntityList.getMain().sort();
			
			Root.FILTER.getLastImport().setAllImpl(entities);
		}
		
		displayResults(entities);
		return true;
	}
	private void importFilesUsingThread(File directory) {
		new Thread(() -> {
			if (this.importFiles(directory)) {
				Logger.getGlobal().info("IMPORT: THREAD FINISHED");
			} else {
				Logger.getGlobal().info("IMPORT: THREAD INTERRUPTED");
			}
		}).start();
	}
	
	private EntityList importEntities(File directory) {
		String directoryPath = directory.getAbsolutePath();
		CustomList<File> files = FileUtil.getFiles(directory, true);
		
		boolean needLoadingBar = files.size() >= MIN_FILE_COUNT_TO_SHOW_LOADING_BAR;
		if (needLoadingBar) Root.MAIN_STAGE.getMainScene().showLoadingBar(this, files.size());
		
		EntityList entities = new EntityList();
		for (File file : files) {
			if (!Platform.isFxApplicationThread() && Thread.currentThread().isInterrupted()) {
				return null;
			}
			entities.addImpl(this.importEntity(file, directoryPath));
			if (needLoadingBar) Root.MAIN_STAGE.getMainScene().advanceLoadingBar(this);
		}
		
		if (needLoadingBar) Root.MAIN_STAGE.getMainScene().hideLoadingBar(this);
		
		return entities;
	}
	private Entity importEntity(File file, String directoryPath) {
		String pathOld = file.getAbsolutePath();
		String pathNew = Project.getCurrent().getDirectorySource() + File.separator + FileUtil.createEntityName(file, directoryPath);
		
		File fileNew = new File(pathNew);
		if (!fileNew.exists()) {
			//todo respect ImportMode
			FileUtil.moveFile(pathOld, pathNew);
			return new Entity(fileNew);
		} else {
			Logger.getGlobal().info("IMPORT: COULD NOT IMPORT \"" + file.getName() + "\", ALREADY EXISTS");
			return null;
		}
	}
	
	private void displayResults(EntityList entities) {
		if (Platform.isFxApplicationThread()) {
			setErrorMessage("");
			setButtons(nodeFinish);
			nodeMessage.setText(getMessageImportCount(entities.size()));
			
			if (entities.isEmpty()) {
				nodeSetupFilter.setDisable(true);
				nodeSetupFilter.setChecked(false);
			}
			
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
			Reload.start();
		} else {
			Platform.runLater(() -> {
				if (!entities.isEmpty()) {
					String message = getMessageImportCount(entities.size()) + "\nSet filter to only show the imported files?";
					if (new ConfirmationStage("Import Result", message).getResult()) {
						setupFilter();
					}
					
					Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
					Reload.start();
				} else {
					new SimpleMessageStage("Import Result", this.getMessageImportCount(entities.size())).show();
				}
			});
		}
	}
	
	private String getMessageImportCount(int importCount) {
		return "Imported " + importCount + " files.";
	}
	private void setupFilter() {
		Root.FILTER.getSettings().setShowImages(true);
		Root.FILTER.getSettings().setShowGifs(true);
		Root.FILTER.getSettings().setShowVideos(true);
		Root.FILTER.getSettings().setShowOnlyNewEntities(true);
		Root.FILTER.getSettings().setEnableLimit(false);
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
