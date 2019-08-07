package application.gui.nodes.buttons;

import application.controller.Reload;
import application.database.list.DataObjectList;
import application.database.list.TagListMain;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.ColorData;
import application.gui.nodes.popup.ClickMenuBase;
import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.nodes.popup.ClickMenuTag;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.stage.StageUtil;
import application.main.Instances;
import application.misc.ClipboardUtil;
import com.sun.jna.platform.FileUtils;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.text.WordUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ButtonFactory {
	private final ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
	
	private ButtonFactory() {
		if (FactoryLoader.instance != null) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
		}
	}
	private static class FactoryLoader {
		private static final ButtonFactory instance = new ButtonFactory();
	}
	public static ButtonFactory getInstance() {
		return FactoryLoader.instance;
	}
	
	public TextNode get(ButtonTemplates buttonTemplate) {
		switch (buttonTemplate) {
			case OBJ_SIMILAR:
				return objectSimilar();
			case OBJ_OPEN:
				return objectOpen();
			case OBJ_EDIT:
				return objectEdit();
			case OBJ_COPY_NAME:
				return objectCopyName();
			case OBJ_COPY_PATH:
				return objectCopyPath();
			case OBJ_DELETE:
				return objectDelete();
			case SEL_DELETE:
				return selectDelete();
			case SEL_MERGE:
				return selectMerge();
			case GRP_UNMERGE:
				return groupUnmerge();
			case TAG_EDIT:
				return tagEdit();
			case TAG_REMOVE:
				return tagRemove();
			case SEL_ALL:
				return selectAll();
			case SEL_NONE:
				return selectNone();
			case STAGE_OK:
				return stageOK();
			default:
				return null;
		}
	}
	
	private TextNode stageOK() {
		TextNode textNode = new TextNode("OK", colorData);
		textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Window window = textNode.getScene().getWindow();
				if (window instanceof Stage) ((Stage) window).close();
				else window.hide();
			}
		});
		return textNode;
	}
	
	private TextNode objectOpen() {
		TextNode textNode = new TextNode("Open", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String fullPath = Instances.getTarget().getCurrentTarget().getPath();
				try {
					Desktop.getDesktop().open(new File(fullPath));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenuBase.hideAll();
				}
			}
		});
		return textNode;
	}
	private TextNode objectEdit() {
		TextNode textNode = new TextNode("Edit", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				String fullPath = Instances.getTarget().getCurrentTarget().getPath();
				try {
					Runtime.getRuntime().exec("mspaint.exe " + fullPath);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenuBase.hideAll();
				}
			}
		});
		return textNode;
	}
	private TextNode objectCopyName() {
		TextNode textNode = new TextNode("Copy Name", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getName());
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode objectCopyPath() {
		TextNode textNode = new TextNode("Copy Path", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getPath());
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode objectSimilar() {
		TextNode textNode = new TextNode("Show Similar", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getFilter().showSimilar(Instances.getTarget().getCurrentTarget());
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode objectDelete() {
		TextNode textNode = new TextNode("Delete", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.deleteCurrentTarget();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private TextNode selectAll() {
		TextNode textNode = new TextNode("Select All", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getSelect().setAll(Instances.getFilter());
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
		return textNode;
	}
	private TextNode selectNone() {
		TextNode textNode = new TextNode("Select None", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getSelect().clear();
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
		return textNode;
	}
	private TextNode selectDelete() {
		TextNode textNode = new TextNode("Delete", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				this.deleteSelection();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode selectMerge() {
		TextNode textNode = new TextNode("Merge Selection", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getSelect().merge();
				Instances.getReload().doReload();
				Instances.getGalleryPane().loadCacheOfTilesInViewport();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private TextNode tagEdit() {
		TextNode textNode = new TextNode("Edit", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (!Instances.getClickMenuTag().getName().isEmpty()) {
					String group = Instances.getClickMenuTag().getGroup();
					String oldName = Instances.getClickMenuTag().getName();
					TagObject tagObject = Instances.getTagListMain().getTagObject(group, oldName);
					Instances.getTagListMain().edit(tagObject);
					if (!oldName.equals(tagObject.getName())) {
						Instances.getFilterPane().updateNameNode(group, oldName, tagObject.getName());
						Instances.getSelectPane().updateNameNode(group, oldName, tagObject.getName());
					}
				} else {
					String oldGroup = Instances.getClickMenuTag().getGroup();
					String newGroup = WordUtils.capitalize(StageUtil.showStageEditorGroup(oldGroup).toLowerCase());
					if (!newGroup.isEmpty()) {
						Instances.getTagListMain().forEach(tagObject -> {
							if (tagObject.getGroup().equals(oldGroup)) {
								tagObject.setGroup(newGroup);
								Instances.getFilterPane().updateGroupNode(oldGroup, newGroup);
								Instances.getSelectPane().updateGroupNode(oldGroup, newGroup);
							}
						});
					}
				}
				ClickMenuBase.hideAll();
				Instances.getReload().doReload();
			}
		});
		return textNode;
	}
	private TextNode tagRemove() {
		TextNode textNode = new TextNode("Remove", colorData);
		textNode.setOnMouseClicked(event -> {
			DataObjectList dataObjectListMain = Instances.getObjectListMain();
			ClickMenuTag clickMenuTag = Instances.getClickMenuTag();
			FilterPane filterPane = Instances.getFilterPane();
			SelectPane selectPane = Instances.getSelectPane();
			TagListMain tagListMain = Instances.getTagListMain();
			
			if (event.getButton() == MouseButton.PRIMARY) {
				String group = clickMenuTag.getGroup();
				String name = clickMenuTag.getName();
				if (name.isEmpty()) {
					if (StageUtil.showStageOkCancel("Remove \"" + group + " \" and all of its tags?")) {
						for (String n : tagListMain.getNames(group)) {
							TagObject tagObject = tagListMain.getTagObject(group, n);
							dataObjectListMain.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
							tagListMain.remove(tagObject);
							filterPane.removeNameNode(group, n);
							selectPane.removeNameNode(group, n);
						}
					}
				} else {
					TagObject tagObject = tagListMain.getTagObject(group, name);
					if (StageUtil.showStageOkCancel("Remove \"" + tagObject.getFull() + "\" ?")) {
						dataObjectListMain.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
						tagListMain.remove(tagObject);
						filterPane.removeNameNode(group, name);
						selectPane.removeNameNode(group, name);
					}
				}
				
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private TextNode groupUnmerge() {
		TextNode textNode = new TextNode("Unmerge Group", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				Instances.getSelect().unmerge();
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private void deleteDataObject(DataObject dataObject) {
		if (Instances.getFilter().contains(dataObject)) {
			String sourcePath = dataObject.getPath();
			String cachePath = dataObject.getCacheFile();
			
			FileUtils fileUtils = FileUtils.getInstance();
			if (fileUtils.hasTrash()) {
				try {
					fileUtils.moveToTrash(new File[]{new File(sourcePath), new File(cachePath)});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Instances.getGalleryPane().getTilePane().getChildren().remove(dataObject.getGalleryTile());
			Instances.getSelect().remove(dataObject);
			Instances.getFilter().remove(dataObject);
			Instances.getObjectListMain().remove(dataObject);
		}
	}
	private void deleteSelection() {
		if (Instances.getSelect().isEmpty()) {
			Logger.getGlobal().info("deleteSelection() - empty selection");
			return;
		}
		
		ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
		Instances.getSelect().forEach(dataObject -> {
			if (dataObject.getMergeID() != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
				dataObjectsToDelete.addAll(dataObject.getMergeGroup());
			} else {
				dataObjectsToDelete.add(dataObject);
			}
		});
		
		if (StageUtil.showStageOkCancel("Delete " + dataObjectsToDelete.size() + " file(s)?")) {
			Instances.getTarget().storePosition();
			dataObjectsToDelete.forEach(this::deleteDataObject);
			Instances.getTarget().restorePosition();
			
			Instances.getReload().notify(Reload.Control.FILTER, Reload.Control.TARGET);
			Instances.getReload().doReload();
		}
	}
	private void deleteCurrentTarget() {
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (currentTarget.getMergeID() != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(currentTarget.getMergeID())) {
			if (StageUtil.showStageOkCancel("Delete " + currentTarget.getMergeGroup().size() + " file(s)?")) {
				Instances.getTarget().storePosition();
				currentTarget.getMergeGroup().forEach(this::deleteDataObject);
				Instances.getTarget().restorePosition();
				Instances.getReload().doReload();
			}
		} else {
			String sourcePath = Instances.getTarget().getCurrentTarget().getPath();
			if (StageUtil.showStageOkCancel("Delete file: " + sourcePath + "?")) {
				Instances.getTarget().storePosition();
				this.deleteDataObject(currentTarget);
				Instances.getTarget().restorePosition();
				
				Instances.getReload().notify(Reload.Control.FILTER, Reload.Control.TARGET);
				Instances.getReload().doReload();
			}
		}
	}
}
