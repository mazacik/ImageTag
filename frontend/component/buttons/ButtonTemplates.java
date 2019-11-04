package application.frontend.component.buttons;

import application.backend.base.entity.Entity;
import application.backend.base.tag.Tag;
import application.backend.control.Reload;
import application.backend.util.ClipboardUtil;
import application.backend.util.EntityGroupUtil;
import application.backend.util.FileUtil;
import application.backend.util.HttpUtil;
import application.frontend.component.ClickMenu;
import application.frontend.component.simple.TextNode;
import application.frontend.stage.StageManager;
import application.frontend.stage.template.YesNoCancelStage;
import application.main.InstanceCollector;
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

public enum ButtonTemplates implements InstanceCollector {
	OBJECT_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String fullPath = target.get().getPath();
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
				String fullPath = target.get().getPath();
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
				ClipboardUtil.setClipboardContent(target.get().getName());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	OBJECT_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(target.get().getPath());
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
	OBJECT_REVERSE_IMAGE_SEARCH {
		public TextNode get() {
			TextNode textNode = new TextNode("Reverse Image Search", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				HttpUtil.googleReverseImageSearch(target.get());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				filter.showSimilar(target.get());
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	FILTER_RANDOM {
		public TextNode get() {
			TextNode textNode = new TextNode("Random", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.setRandom();
				reload.doReload();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.setAll(filter);
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	SELECTION_SET_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.clear();
				reload.doReload();
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
					Tag tag = tagListMain.getTag(group, oldName);
					tagListMain.edit(tag);
					if (!oldName.equals(tag.getName())) {
						filterPane.updateNameNode(group, oldName, tag.getName());
						selectPane.updateNameNode(group, oldName, tag.getName());
					}
				} else {
					String oldGroup = ClickMenu.getGroup();
					String newGroup = WordUtils.capitalize(StageManager.getGroupEditStage()._show(oldGroup).toLowerCase());
					if (!newGroup.isEmpty()) {
						tagListMain.forEach(tagObject -> {
							if (tagObject.getGroup().equals(oldGroup)) {
								tagObject.setGroup(newGroup);
								filterPane.updateGroupNode(oldGroup, newGroup);
								selectPane.updateGroupNode(oldGroup, newGroup);
							}
						});
					}
				}
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
				if (name.isEmpty()) {
					if (StageManager.getOkCancelStage()._show("Remove \"" + group + "\" and all of its tags?")) {
						for (String n : tagListMain.getNames(group)) {
							filterPane.removeNameNode(group, n);
							selectPane.removeNameNode(group, n);
							Tag tag = tagListMain.getTag(group, n);
							entityListMain.forEach(dataObject -> dataObject.getTagList().remove(tag));
							filter.unlist(tag);
							tagListMain.remove(tag);
						}
					}
				} else {
					Tag tag = tagListMain.getTag(group, name);
					if (StageManager.getOkCancelStage()._show("Remove \"" + tag.getFull() + "\" ?")) {
						filterPane.removeNameNode(group, name);
						selectPane.removeNameNode(group, name);
						entityListMain.forEach(dataObject -> dataObject.getTagList().remove(tag));
						filter.unlist(tag);
						tagListMain.remove(tag);
					}
				}
				
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	ENTITY_GROUP_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Entity Group", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.entityGroupCreate();
				reload.doReload();
				galleryPane.updateViewportTilesVisibility();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_GROUP_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Entity Group", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.entityGroupDiscard();
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true);
			textNode.addEventFilter(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				entityListMain.writeToDisk();
				tagListMain.writeDummyToDisk();
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
					StageManager.getMainStage().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
	
	private void deleteDataObject(Entity entity) {
		if (filter.contains(entity)) {
			String sourcePath = entity.getPath();
			String cachePath = FileUtil.getCacheFilePath(entity);
			
			FileUtils fileUtils = FileUtils.getInstance();
			if (fileUtils.hasTrash()) {
				try {
					fileUtils.moveToTrash(new File[]{new File(sourcePath), new File(cachePath)});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			galleryPane.getTilePane().getChildren().remove(entity.getGalleryTile());
			select.remove(entity);
			filter.remove(entity);
			entityListMain.remove(entity);
		}
	}
	private void deleteSelection() {
		if (select.isEmpty()) {
			Logger.getGlobal().info("deleteSelection() - empty selection");
			return;
		}
		
		ArrayList<Entity> dataObjectsToDelete = new ArrayList<>();
		select.forEach(entity -> {
			if (entity.getEntityGroupID() != 0 && !galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
				dataObjectsToDelete.addAll(EntityGroupUtil.getEntityGroup(entity));
			} else {
				dataObjectsToDelete.add(entity);
			}
		});
		
		YesNoCancelStage.Result result = StageManager.getYesNoCancelStage()._show("Delete " + dataObjectsToDelete.size() + " file(s)?");
		if (result == YesNoCancelStage.Result.YES) {
			target.storePosition();
			dataObjectsToDelete.forEach(this::deleteDataObject);
			target.restorePosition();
			
			reload.notify(Reload.Control.FILTER, Reload.Control.TARGET);
			reload.doReload();
		}
	}
	private void deleteCurrentTarget() {
		Entity currentTarget = target.get();
		if (currentTarget.getEntityGroupID() == 0 || galleryPane.getExpandedGroups().contains(currentTarget.getEntityGroupID())) {
			String sourcePath = target.get().getPath();
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage()._show("Delete file: " + sourcePath + "?");
			if (result == YesNoCancelStage.Result.YES) {
				target.storePosition();
				this.deleteDataObject(currentTarget);
				target.restorePosition();
				
				reload.notify(Reload.Control.FILTER, Reload.Control.TARGET);
				reload.doReload();
			}
		} else {
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage()._show("Delete " + EntityGroupUtil.getEntityGroup(currentTarget).size() + " file(s)?");
			if (result == YesNoCancelStage.Result.YES) {
				
				target.storePosition();
				EntityGroupUtil.getEntityGroup(currentTarget).forEach(this::deleteDataObject);
				target.restorePosition();
				reload.doReload();
			}
		}
	}
}
