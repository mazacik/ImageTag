package application.gui.nodes.buttons;

import application.controller.Reload;
import application.database.list.DataObjectList;
import application.database.list.TagListMain;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.nodes.popup.ClickMenuBase;
import application.gui.nodes.popup.ClickMenuLeft;
import application.gui.nodes.popup.ClickMenuTag;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.stage.Stages;
import application.main.Instances;
import application.misc.ClipboardUtil;
import application.misc.FileUtil;
import com.sun.jna.platform.FileUtils;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.apache.commons.text.WordUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public enum ButtonTemplates {
	OBJECT_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String fullPath = Instances.getTarget().getCurrentTarget().getPath();
				try {
					Desktop.getDesktop().open(new File(fullPath));
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenuBase.hideAll();
				}
			});
			return textNode;
		}
	},
	OBJECT_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String fullPath = Instances.getTarget().getCurrentTarget().getPath();
				try {
					Runtime.getRuntime().exec("mspaint.exe " + fullPath);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenuBase.hideAll();
				}
			});
			return textNode;
		}
	},
	OBJECT_COPY_NAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy Name", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getName());
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	OBJECT_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy Path", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getPath());
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	OBJECT_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteCurrentTarget();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getFilter().showSimilar(Instances.getTarget().getCurrentTarget());
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	FILTER_RANDOM {
		public TextNode get() {
			TextNode textNode = new TextNode("Random", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().setRandom();
				Instances.getReload().doReload();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().setAll(Instances.getFilter());
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_SET_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().clear();
				Instances.getReload().doReload();
				ClickMenuLeft.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_MERGE {
		public TextNode get() {
			TextNode textNode = new TextNode("Merge Selection", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().merge();
				Instances.getReload().doReload();
				Instances.getGalleryPane().loadCacheOfTilesInViewport();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteSelection();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
					String newGroup = WordUtils.capitalize(Stages.getGroupEditStage()._show(oldGroup).toLowerCase());
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
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			DataObjectList dataObjectListMain = Instances.getObjectListMain();
			ClickMenuTag clickMenuTag = Instances.getClickMenuTag();
			FilterPane filterPane = Instances.getFilterPane();
			SelectPane selectPane = Instances.getSelectPane();
			TagListMain tagListMain = Instances.getTagListMain();
			
			TextNode textNode = new TextNode("Remove", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = clickMenuTag.getGroup();
				String name = clickMenuTag.getName();
				if (name.isEmpty()) {
					if (Stages.getOkCancelStage()._show("Remove \"" + group + " \" and all of its tags?")) {
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
					if (Stages.getOkCancelStage()._show("Remove \"" + tagObject.getFull() + "\" ?")) {
						dataObjectListMain.forEach(dataObject -> dataObject.getTagList().remove(tagObject));
						tagListMain.remove(tagObject);
						filterPane.removeNameNode(group, name);
						selectPane.removeNameNode(group, name);
					}
				}
				
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	
	GROUP_UNMERGE {
		public TextNode get() {
			TextNode textNode = new TextNode("Unmerge Group", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().unmerge();
				Instances.getReload().doReload();
				ClickMenuBase.hideAll();
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getObjectListMain().writeToDisk();
				Instances.getTagListMain().writeDummyToDisk();
				ClickMenuLeft.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				FileUtil.importFiles();
				ClickMenuLeft.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Exit", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
					Stages.getMainStage().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
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
		
		if (Stages.getOkCancelStage()._show("Delete " + dataObjectsToDelete.size() + " file(s)?")) {
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
			if (Stages.getOkCancelStage()._show("Delete " + currentTarget.getMergeGroup().size() + " file(s)?")) {
				Instances.getTarget().storePosition();
				currentTarget.getMergeGroup().forEach(this::deleteDataObject);
				Instances.getTarget().restorePosition();
				Instances.getReload().doReload();
			}
		} else {
			String sourcePath = Instances.getTarget().getCurrentTarget().getPath();
			if (Stages.getOkCancelStage()._show("Delete file: " + sourcePath + "?")) {
				Instances.getTarget().storePosition();
				this.deleteDataObject(currentTarget);
				Instances.getTarget().restorePosition();
				
				Instances.getReload().notify(Reload.Control.FILTER, Reload.Control.TARGET);
				Instances.getReload().doReload();
			}
		}
	}
}
