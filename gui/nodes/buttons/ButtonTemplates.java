package application.gui.nodes.buttons;

import application.controller.Reload;
import application.database.list.DataObjectList;
import application.database.list.TagListMain;
import application.database.object.DataObject;
import application.database.object.TagObject;
import application.gui.nodes.ClickMenu;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.side.FilterPane;
import application.gui.panes.side.SelectPane;
import application.gui.stage.Stages;
import application.gui.stage.YesNoCancelStage;
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
					ClickMenu.hideAll();
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
					ClickMenu.hideAll();
				}
			});
			return textNode;
		}
	},
	OBJECT_COPY_NAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Name", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getName());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	OBJECT_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(Instances.getTarget().getCurrentTarget().getPath());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	OBJECT_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete File", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteCurrentTarget();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getFilter().showSimilar(Instances.getTarget().getCurrentTarget());
				Instances.getReload().doReload();
				ClickMenu.hideAll();
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
				ClickMenu.hideAll();
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
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete Selection", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteSelection();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (!ClickMenu.getName().isEmpty()) {
					String group = ClickMenu.getGroup();
					String oldName = ClickMenu.getName();
					TagObject tagObject = Instances.getTagListMain().getTagObject(group, oldName);
					Instances.getTagListMain().edit(tagObject);
					if (!oldName.equals(tagObject.getName())) {
						Instances.getFilterPane().updateNameNode(group, oldName, tagObject.getName());
						Instances.getSelectPane().updateNameNode(group, oldName, tagObject.getName());
					}
				} else {
					String oldGroup = ClickMenu.getGroup();
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
				ClickMenu.hideAll();
				Instances.getReload().doReload();
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			DataObjectList dataObjectListMain = Instances.getObjectListMain();
			FilterPane filterPane = Instances.getFilterPane();
			SelectPane selectPane = Instances.getSelectPane();
			TagListMain tagListMain = Instances.getTagListMain();
			
			TextNode textNode = new TextNode("Remove", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
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
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	JOINT_OBJECT_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Joint Object", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().jointObjectCreate();
				Instances.getReload().doReload();
				Instances.getGalleryPane().loadCacheOfTilesInViewport();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	JOINT_OBJECT_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Joint Object", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Instances.getSelect().jointObjectDiscard();
				Instances.getReload().doReload();
				ClickMenu.hideAll();
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
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				FileUtil.importFiles();
				ClickMenu.hideAll();
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
			if (dataObject.getJointID() != 0 && !Instances.getGalleryPane().getExpandedGroups().contains(dataObject.getJointID())) {
				dataObjectsToDelete.addAll(dataObject.getJointObjects());
			} else {
				dataObjectsToDelete.add(dataObject);
			}
		});
		
		YesNoCancelStage.Result result = Stages.getYesNoCancelStage()._show("Delete " + dataObjectsToDelete.size() + " file(s)?");
		if (result == YesNoCancelStage.Result.YES) {
			Instances.getTarget().storePosition();
			dataObjectsToDelete.forEach(this::deleteDataObject);
			Instances.getTarget().restorePosition();
			
			Instances.getReload().notify(Reload.Control.FILTER, Reload.Control.TARGET);
			Instances.getReload().doReload();
		}
	}
	private void deleteCurrentTarget() {
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (currentTarget.getJointID() == 0 || Instances.getGalleryPane().getExpandedGroups().contains(currentTarget.getJointID())) {
			String sourcePath = Instances.getTarget().getCurrentTarget().getPath();
			YesNoCancelStage.Result result = Stages.getYesNoCancelStage()._show("Delete file: " + sourcePath + "?");
			if (result == YesNoCancelStage.Result.YES) {
				Instances.getTarget().storePosition();
				this.deleteDataObject(currentTarget);
				Instances.getTarget().restorePosition();
				
				Instances.getReload().notify(Reload.Control.FILTER, Reload.Control.TARGET);
				Instances.getReload().doReload();
			}
		} else {
			YesNoCancelStage.Result result = Stages.getYesNoCancelStage()._show("Delete " + currentTarget.getJointObjects().size() + " file(s)?");
			if (result == YesNoCancelStage.Result.YES) {
				
				Instances.getTarget().storePosition();
				currentTarget.getJointObjects().forEach(this::deleteDataObject);
				Instances.getTarget().restorePosition();
				Instances.getReload().doReload();
			}
		}
	}
}
