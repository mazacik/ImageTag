package frontend.stage.fileimport;

import backend.BaseList;
import backend.entity.EntityList;
import backend.filter.FilterOption;
import backend.misc.FileUtil;
import backend.misc.Settings;
import backend.reload.InvokeHelper;
import backend.reload.Reload;
import frontend.node.*;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import frontend.stage.BaseStage;
import frontend.stage.ConfirmationStage;
import frontend.stage.SimpleMessageStage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class ImportStage extends BaseStage {
	private HBox boxBrowseForDirectory;
	private File directory;
	
	private TextNode nodeNext;
	private TextNode nodeCancel;
	
	private HBox boxListFilesToImport;
	private ListBox listBoxFilesInDirectory;
	private BaseList<File> filesInDirectory;
	
	private TextNode nodeBack;
	private TextNode nodeImport;
	
	public ImportStage() {
		super("Import", 0.75);
		
		this.getScene().getStylesheets().add("/css/Styles.css");
		
		createBrowseForDirectory();
		createListFilesToImport();
		
		showBrowseForDirectory();
	}
	
	private void createBrowseForDirectory() {
		TextNode nodeTextPath = new TextNode("Import Directory", false, false, false, true);
		EditNode nodeEditPath = new EditNode(Settings.IMPORT_LAST_PATH.getValue(), "");
		nodeEditPath.setMinWidth(300);
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
		
		boxBrowseForDirectory = new HBox(nodeTextPath, nodeEditPath, nodeBrowse);
		boxBrowseForDirectory.setAlignment(Pos.CENTER);
		boxBrowseForDirectory.setSpacing(3);
		boxBrowseForDirectory.setPadding(new Insets(3));
		
		nodeNext = new TextNode("Next", true, true, false, true);
		nodeNext.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			File directory = new File(nodeEditPath.getText());
			if (directory.exists() && directory.isDirectory()) {
				Settings.IMPORT_LAST_PATH.setValue(directory.getAbsolutePath());
				this.directory = directory;
				showListFilesToImport();
			} else {
				new SimpleMessageStage("Error", "Import Path Invalid").showAndWait();
			}
		});
		
		nodeCancel = new TextNode("Cancel", true, true, false, true);
		nodeCancel.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setRoot(boxBrowseForDirectory);
		this.setButtons(nodeNext, nodeCancel);
	}
	private void createListFilesToImport() {
		listBoxFilesInDirectory = new ListBox();
		listBoxFilesInDirectory.setPrefWidth(this.getWidth() * 0.75);
		
		CheckBox nodeEmptyFolders = new CheckBox("Remove Empty Folders", true);
		
		AtomicReference<ImportMode> importMode = new AtomicReference<>(ImportMode.MOVE);
		SwitchNode nodeImportMode = new SwitchNode("Copy", "Move", 150);
		nodeImportMode.getLeft().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			importMode.set(ImportMode.COPY);
			nodeEmptyFolders.setDisable(true);
			nodeImportMode.selectLeft();
		});
		nodeImportMode.getRight().addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			importMode.set(ImportMode.MOVE);
			nodeEmptyFolders.setDisable(false);
			nodeImportMode.selectRight();
		});
		nodeImportMode.selectRight();
		
		VBox boxOptions = new VBox();
		boxOptions.setAlignment(Pos.CENTER);
		boxOptions.getChildren().addAll(nodeImportMode, nodeEmptyFolders);
		boxOptions.setPrefWidth(this.getWidth() * 0.25);
		
		boxListFilesToImport = new HBox(boxOptions, listBoxFilesInDirectory);
		
		nodeImport = new TextNode("Import", true, true, false, true);
		nodeImport.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			String directoryPath = directory.getAbsolutePath();
			
			BaseList<File> filesToImport = new BaseList<>();
			BaseList<CheckBox> checkBoxes = new BaseList<>();
			
			listBoxFilesInDirectory.getBox().getChildren().forEach(node -> {
				if (node instanceof CheckBox) {
					checkBoxes.add((CheckBox) node);
				}
			});
			checkBoxes.forEach(checkBox -> {
				if (checkBox.isSelected()) {
					filesToImport.add(filesInDirectory.get(checkBoxes.indexOf(checkBox)));
				}
			});
			
			this.startImport(importMode.get(), directoryPath, filesToImport);
		});
		
		nodeBack = new TextNode("Back", true, true, false, true);
		nodeBack.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::showBrowseForDirectory);
	}
	
	private void showBrowseForDirectory() {
		this.setRoot(boxBrowseForDirectory);
		this.setButtons(nodeNext, nodeCancel);
	}
	private void showListFilesToImport() {
		filesInDirectory = FileUtil.getFiles(directory, true);
		
		BaseList<Node> nodes = new BaseList<>();
		filesInDirectory.forEach(file -> {
			String absolutePath = file.getAbsolutePath();
			String fileName = absolutePath.substring(absolutePath.lastIndexOf(File.separator) + 1);
			
			CheckBox checkBox = new CheckBox(fileName, true);
			
			nodes.add(checkBox);
			nodes.add(new SeparatorNode());
		});
		listBoxFilesInDirectory.getBox().getChildren().setAll(nodes);
		
		this.setRoot(boxListFilesToImport);
		this.setButtons(nodeBack, nodeImport);
	}
	
	private void startImport(ImportMode importMode, String directory, BaseList<File> files) {
		ImportUtil.startImportThread(importMode, directory, files);
		this.close();
	}
	
	public static void displayResults(EntityList entityList) {
		if (entityList != null) {
			String message = "Imported " + entityList.size() + " files.";
			if (!entityList.isEmpty()) {
				message += "\nSet filter to only show the imported files?";
				if (new ConfirmationStage("Import Result", message).getResult()) {
					FilterOption.reset();
					
					FilterOption.ENABLE_IMG.setValue(true);
					FilterOption.ENABLE_VID.setValue(true);
					FilterOption.ONLY_LAST_IMPORT.setValue(true);
					
					Reload.request(InvokeHelper.FILTER_REFRESH);
					Reload.start();
				}
			} else {
				new SimpleMessageStage("Import Result", message).show();
			}
		}
	}
}
