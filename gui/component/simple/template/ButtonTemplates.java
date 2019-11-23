package gui.component.simple.template;

import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import com.sun.jna.platform.FileUtils;
import control.reload.ChangeIn;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.TextNode;
import gui.stage.StageManager;
import gui.stage.template.ButtonBooleanValue;
import gui.stage.template.tageditstage.TagEditStageResult;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import main.InstanceCollector;
import org.apache.commons.text.WordUtils;
import tools.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public enum ButtonTemplates implements InstanceCollector {
	ENTITY_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				File entityFile = FileUtil.getEntityFile(target.get());
				try {
					Desktop.getDesktop().open(entityFile);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String entityFilePath = FileUtil.getEntityFilePath(target.get());
				try {
					Runtime.getRuntime().exec("mspaint.exe " + entityFilePath);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String entityName = target.get().getName();
				SystemUtil.setClipboardContent(entityName.substring(entityName.lastIndexOf(File.separatorChar)));
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				SystemUtil.setClipboardContent(FileUtil.getEntityFilePath(target.get()));
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete File", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				HttpUtil.googleReverseImageSearch(target.get());
				StageManager.getErrorStage().show("Info", "Request sent.");
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				filter.showSimilar(target.get());
				if (StageManager.getMainStage().isFullView()) {
					StageManager.getMainStage().swapViewMode();
				}
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	FILTER_RANDOM {
		public TextNode get() {
			TextNode textNode = new TextNode("Random", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (!ClickMenu.getName().isEmpty()) {
					//  click was on name node
					String groupBefore = ClickMenu.getGroup();
					String nameBefore = ClickMenu.getName();
					Tag tag = tagListMain.getTag(groupBefore, nameBefore);
					
					TagEditStageResult result = StageManager.getTagEditStage().show(groupBefore, nameBefore);
					String groupAfter = WordUtils.capitalize(result.getGroup().toLowerCase());
					String nameAfter = WordUtils.capitalize(result.getName().toLowerCase());
					
					tag.set(groupAfter, nameAfter);
					tagListMain.sort();
					
					if (result.isAddToSelect()) {
						select.addTag(tag);
					}
					
					if (!groupBefore.equals(groupAfter)) {
						filterPane.updateGroupNode(groupBefore, groupAfter);
						selectPane.updateGroupNode(groupBefore, groupAfter);
					}
					
					if (!nameBefore.equals(nameAfter)) {
						filterPane.updateNameNode(groupAfter, nameBefore, nameAfter);
						selectPane.updateNameNode(groupAfter, nameBefore, nameAfter);
					}
				} else {
					//  click on group node
					String groupBefore = ClickMenu.getGroup();
					String groupAfter = WordUtils.capitalize(StageManager.getGroupEditStage().show(groupBefore).toLowerCase());
					
					tagListMain.forEach(tag -> {
						if (tag.getGroup().equals(groupBefore)) {
							tag.setGroup(groupAfter);
						}
					});
					
					filterPane.updateGroupNode(groupBefore, groupAfter);
					selectPane.updateGroupNode(groupBefore, groupAfter);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityGroupUtil.createGroup(select);
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	ENTITY_GROUP_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Entity Group", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityGroupUtil.discardGroup(select);
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	CACHE_RESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Reset Cache", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CacheManager.stopThread();
				for (Entity entity : entityListMain) {
					try {
						Files.delete(Paths.get(FileUtil.getCacheFilePath(entity)));
					} catch (IOException e) {
						e.printStackTrace();
					}
					entity.getGalleryTile().setImage(null);
				}
				CacheManager.createCacheInBackground();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				entityListMain.writeToDisk();
				tagListMain.writeToDisk();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			textNode.setMaxWidth(Double.MAX_VALUE);
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
			File entityFile = FileUtil.getEntityFile(target.get());
			File cacheFile = new File(FileUtil.getCacheFilePath(entity));
			
			FileUtils fileUtils = FileUtils.getInstance();
			if (fileUtils.hasTrash()) {
				try {
					//todo
					fileUtils.moveToTrash(new File[]{entityFile, cacheFile});
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
				entitiesToDelete.addAll(entity.getEntityGroup(), true);
			} else {
				entitiesToDelete.add(entity, true);
			}
		});
		
		ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Delete " + entitiesToDelete.size() + " file(s)?");
		if (result == ButtonBooleanValue.YES) {
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
			String sourcePath = FileUtil.getEntityFilePath(currentTarget);
			ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Delete file: " + sourcePath + "?");
			if (result == ButtonBooleanValue.YES) {
				target.storePosition();
				this.deleteEntity(currentTarget);
				target.restorePosition();
				
				reload.notify(ChangeIn.FILTER, ChangeIn.TARGET);
				reload.doReload();
			}
		} else {
			ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Delete " + currentTarget.getEntityGroup().size() + " file(s)?");
			if (result == ButtonBooleanValue.YES) {
				
				target.storePosition();
				for (Entity entity : currentTarget.getEntityGroup()) {
					this.deleteEntity(entity);
				}
				target.restorePosition();
				reload.doReload();
			}
		}
	}
}
