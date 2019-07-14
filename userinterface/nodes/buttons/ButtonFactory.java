package userinterface.nodes.buttons;

import com.sun.jna.platform.FileUtils;
import control.Reload;
import database.list.DataObjectList;
import database.list.TagListMain;
import database.object.DataObject;
import database.object.TagObject;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.InstanceManager;
import org.apache.commons.text.WordUtils;
import userinterface.main.side.FilterPane;
import userinterface.main.side.SelectPane;
import userinterface.nodes.ColorData;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.menu.ClickMenuBase;
import userinterface.nodes.menu.ClickMenuLeft;
import userinterface.nodes.menu.ClickMenuTag;
import userinterface.stage.StageUtil;
import userinterface.style.enums.ColorType;
import utils.ClipboardUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
				String fullPath = InstanceManager.getTarget().getCurrentTarget().getPath();
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
				String fullPath = InstanceManager.getTarget().getCurrentTarget().getPath();
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
				ClipboardUtil.setClipboardContent(InstanceManager.getTarget().getCurrentTarget().getName());
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode objectCopyPath() {
		TextNode textNode = new TextNode("Copy Path", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				ClipboardUtil.setClipboardContent(InstanceManager.getTarget().getCurrentTarget().getPath());
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	private TextNode objectSimilar() {
		TextNode textNode = new TextNode("Show Similar", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				InstanceManager.getFilter().showSimilar(InstanceManager.getTarget().getCurrentTarget());
				InstanceManager.getReload().doReload();
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
				InstanceManager.getSelect().setAll(InstanceManager.getFilter());
				InstanceManager.getReload().doReload();
				ClickMenuLeft.hideAll();
			}
		});
		return textNode;
	}
	private TextNode selectNone() {
		TextNode textNode = new TextNode("Select None", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				InstanceManager.getSelect().clear();
				InstanceManager.getReload().doReload();
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
				InstanceManager.getSelect().merge();
				InstanceManager.getReload().doReload();
				InstanceManager.getGalleryPane().loadViewportCache();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private TextNode tagEdit() {
		TextNode textNode = new TextNode("Edit", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (!InstanceManager.getClickMenuTag().getName().isEmpty()) {
					String group = InstanceManager.getClickMenuTag().getGroup();
					String oldName = InstanceManager.getClickMenuTag().getName();
					TagObject tagObject = InstanceManager.getTagListMain().getTagObject(group, oldName);
					InstanceManager.getTagListMain().edit(tagObject);
					if (!oldName.equals(tagObject.getName())) {
						InstanceManager.getFilterPane().updateNameNode(group, oldName, tagObject.getName());
						InstanceManager.getSelectPane().updateNameNode(group, oldName, tagObject.getName());
					}
				} else {
					String oldGroup = InstanceManager.getClickMenuTag().getGroup();
					String newGroup = WordUtils.capitalize(StageUtil.showStageEditorGroup(oldGroup).toLowerCase());
					if (newGroup.isEmpty()) return;
					
					InstanceManager.getTagListMain().forEach(tagObject -> {
						if (tagObject.getGroup().equals(oldGroup)) {
							tagObject.setGroup(newGroup);
						}
					});
					InstanceManager.getFilterPane().refresh();
					InstanceManager.getSelectPane().refresh();
					InstanceManager.getReload().flag(Reload.Control.TAG);
				}
				ClickMenuBase.hideAll();
				InstanceManager.getReload().doReload();
			}
		});
		return textNode;
	}
	private TextNode tagRemove() {
		TextNode textNode = new TextNode("Remove", colorData);
		textNode.setOnMouseClicked(event -> {
			DataObjectList dataObjectListMain = InstanceManager.getObjectListMain();
			ClickMenuTag clickMenuTag = InstanceManager.getClickMenuTag();
			FilterPane filterPane = InstanceManager.getFilterPane();
			SelectPane selectPane = InstanceManager.getSelectPane();
			TagListMain tagListMain = InstanceManager.getTagListMain();
			
			if (event.getButton() == MouseButton.PRIMARY) {
				String group = clickMenuTag.getGroup();
				String name = clickMenuTag.getName();
				if (name.isEmpty()) {
					for (String n : tagListMain.getNames(group)) {
						TagObject tagObject = tagListMain.getTagObject(group, n);
						dataObjectListMain.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
						tagListMain.remove(tagObject);
						filterPane.removeNameNode(group, n);
						selectPane.removeNameNode(group, n);
					}
				} else {
					TagObject tagObject = tagListMain.getTagObject(group, name);
					dataObjectListMain.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
					tagListMain.remove(tagObject);
					filterPane.removeNameNode(group, name);
					selectPane.removeNameNode(group, name);
				}
				
				InstanceManager.getReload().doReload();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private TextNode groupUnmerge() {
		TextNode textNode = new TextNode("Unmerge Group", colorData);
		textNode.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				InstanceManager.getSelect().unmerge();
				InstanceManager.getReload().doReload();
				ClickMenuBase.hideAll();
			}
		});
		return textNode;
	}
	
	private void deleteDataObject(DataObject dataObject) {
		if (InstanceManager.getFilter().contains(dataObject)) {
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
			
			InstanceManager.getGalleryPane().getTilePane().getChildren().remove(dataObject.getBaseTile());
			InstanceManager.getSelect().remove(dataObject);
			InstanceManager.getFilter().remove(dataObject);
			InstanceManager.getObjectListMain().remove(dataObject);
		}
	}
	private void deleteSelection() {
		if (InstanceManager.getSelect().isEmpty()) {
			InstanceManager.getLogger().debug("deleteSelection() - empty selection");
			return;
		}
		
		ArrayList<DataObject> dataObjectsToDelete = new ArrayList<>();
		InstanceManager.getSelect().forEach(dataObject -> {
			if (dataObject.getMergeID() != 0 && !InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
				dataObjectsToDelete.addAll(dataObject.getMergeGroup());
			} else {
				dataObjectsToDelete.add(dataObject);
			}
		});
		
		if (StageUtil.showStageOkCancel("Delete " + dataObjectsToDelete.size() + " file(s)?")) {
			InstanceManager.getTarget().storePosition();
			dataObjectsToDelete.forEach(this::deleteDataObject);
			InstanceManager.getTarget().restorePosition();
			
			InstanceManager.getReload().flag(Reload.Control.FILTER, Reload.Control.TARGET);
			InstanceManager.getReload().doReload();
		}
	}
	private void deleteCurrentTarget() {
		DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
		if (currentTarget.getMergeID() != 0) {
			if (!InstanceManager.getGalleryPane().getExpandedGroups().contains(currentTarget.getMergeID())) {
				if (StageUtil.showStageOkCancel("Delete " + currentTarget.getMergeGroup().size() + " file(s)?")) {
					InstanceManager.getTarget().storePosition();
					currentTarget.getMergeGroup().forEach(this::deleteDataObject);
					InstanceManager.getTarget().restorePosition();
					InstanceManager.getReload().doReload();
				}
			}
		} else {
			String sourcePath = InstanceManager.getTarget().getCurrentTarget().getPath();
			if (StageUtil.showStageOkCancel("Delete file: " + sourcePath + "?")) {
				InstanceManager.getTarget().storePosition();
				this.deleteDataObject(currentTarget);
				InstanceManager.getTarget().restorePosition();
				
				InstanceManager.getReload().flag(Reload.Control.FILTER, Reload.Control.TARGET);
				InstanceManager.getReload().doReload();
			}
		}
	}
}
