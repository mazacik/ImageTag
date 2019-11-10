package application.component.buttons;

import application.base.entity.Entity;
import application.base.entity.EntityList;
import application.base.tag.Tag;
import application.component.ClickMenu;
import application.component.simple.TextNode;
import application.control.reload.ChangeIn;
import application.main.InstanceCollector;
import application.stage.StageManager;
import application.stage.template.YesNoCancelStage;
import application.util.ClipboardUtil;
import application.util.EntityGroupUtil;
import application.util.FileUtil;
import application.util.HttpUtil;
import com.sun.jna.platform.FileUtils;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.apache.commons.text.WordUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public enum ButtonTemplates implements InstanceCollector {
	ENTITY_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
	ENTITY_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
	ENTITY_COPY_NAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Name", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(target.get().getName());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardUtil.setClipboardContent(target.get().getPath());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete File", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteCurrentTarget();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_REVERSE_IMAGE_SEARCH {
		public TextNode get() {
			TextNode textNode = new TextNode("Reverse Image Search", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				HttpUtil.googleReverseImageSearch(target.get());
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				select.setRandom();
				reload.doReload();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				super.deleteSelection();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
					String newGroup = WordUtils.capitalize(StageManager.getGroupEditStage().show(oldGroup).toLowerCase());
					if (!newGroup.isEmpty()) {
						tagListMain.forEach(tag -> {
							if (tag.getGroup().equals(oldGroup)) {
								tag.setGroup(newGroup);
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
				if (name.isEmpty()) {
					if (StageManager.getOkCancelStage().show("Remove \"" + group + "\" and all of its tags?")) {
						for (String n : tagListMain.getNames(group)) {
							filterPane.removeNameNode(group, n);
							selectPane.removeNameNode(group, n);
							Tag tag = tagListMain.getTag(group, n);
							entityListMain.forEach(entity -> entity.getTagList().remove(tag));
							filter.unlist(tag);
							tagListMain.remove(tag);
						}
					}
				} else {
					Tag tag = tagListMain.getTag(group, name);
					if (StageManager.getOkCancelStage().show("Remove \"" + tag.getFull() + "\" ?")) {
						filterPane.removeNameNode(group, name);
						selectPane.removeNameNode(group, name);
						entityListMain.forEach(entity -> entity.getTagList().remove(tag));
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
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
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				FileUtil.importFiles();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Exit", true, true, false, true);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () ->
					StageManager.getMainStage().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
	
	private void deleteEntity(Entity entity) {
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
		
		EntityList entitiesToDelete = new EntityList();
		select.forEach(entity -> {
			if (entity.getEntityGroupID() != 0 && !galleryPane.getExpandedGroups().contains(entity.getEntityGroupID())) {
				entitiesToDelete.addAll(EntityGroupUtil.getEntityGroup(entity), true);
			} else {
				entitiesToDelete.add(entity, true);
			}
		});
		
		YesNoCancelStage.Result result = StageManager.getYesNoCancelStage().show("Delete " + entitiesToDelete.size() + " file(s)?");
		if (result == YesNoCancelStage.Result.YES) {
			target.storePosition();
			entitiesToDelete.forEach(this::deleteEntity);
			target.restorePosition();
			
			reload.notify(ChangeIn.FILTER, ChangeIn.TARGET);
			reload.doReload();
		}
	}
	private void deleteCurrentTarget() {
		Entity currentTarget = target.get();
		if (currentTarget.getEntityGroupID() == 0 || galleryPane.getExpandedGroups().contains(currentTarget.getEntityGroupID())) {
			String sourcePath = target.get().getPath();
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage().show("Delete file: " + sourcePath + "?");
			if (result == YesNoCancelStage.Result.YES) {
				target.storePosition();
				this.deleteEntity(currentTarget);
				target.restorePosition();
				
				reload.notify(ChangeIn.FILTER, ChangeIn.TARGET);
				reload.doReload();
			}
		} else {
			YesNoCancelStage.Result result = StageManager.getYesNoCancelStage().show("Delete " + EntityGroupUtil.getEntityGroup(currentTarget).size() + " file(s)?");
			if (result == YesNoCancelStage.Result.YES) {
				
				target.storePosition();
				EntityGroupUtil.getEntityGroup(currentTarget).forEach(this::deleteEntity);
				target.restorePosition();
				reload.doReload();
			}
		}
	}
}
