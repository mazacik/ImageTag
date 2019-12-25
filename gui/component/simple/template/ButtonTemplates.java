package gui.component.simple.template;

import baseobject.Project;
import baseobject.entity.Entity;
import baseobject.entity.EntityList;
import baseobject.tag.Tag;
import control.reload.ChangeIn;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.TextNode;
import gui.stage.StageManager;
import gui.stage.template.ButtonBooleanValue;
import gui.stage.template.tageditstage.TagEditStageResult;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import main.InstanceCollector;
import org.apache.commons.text.WordUtils;
import tools.CacheManager;
import tools.CollectionUtil;
import tools.FileUtil;
import tools.HttpUtil;

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
				File entityFile = new File(FileUtil.getFileEntity(target.get()));
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
				String entityFilePath = FileUtil.getFileEntity(target.get());
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
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(target.get()));
				Clipboard.getSystemClipboard().setContent(content);
				
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
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(target.get()));
				Clipboard.getSystemClipboard().setContent(content);
				
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
				StageManager.getStageMain().getSceneMain().viewGallery();
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
	
	TAG_GROUP_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag Group", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String groupBefore = ClickMenu.getGroup();
				String groupAfter = WordUtils.capitalize(StageManager.getGroupEditStage().show(groupBefore).toLowerCase());
				
				mainTagList.forEach(tag -> {
					if (tag.getGroup().equals(groupBefore)) {
						tag.setGroup(groupAfter);
					}
				});
				
				paneFilter.updateGroupNode(groupBefore, groupAfter);
				paneSelect.updateGroupNode(groupBefore, groupAfter);
				
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	TAG_GROUP_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag Group", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				
				if (StageManager.getOkCancelStage().show("Remove \"" + group + "\" and all of its tags?")) {
					for (String n : mainTagList.getNames(group)) {
						paneFilter.removeNameNode(group, n);
						paneSelect.removeNameNode(group, n);
						Tag tag = mainTagList.getTag(group, n);
						mainEntityList.forEach(entity -> entity.getTagList().remove(tag));
						filter.unlist(tag);
						mainTagList.remove(tag);
						reload.notify(ChangeIn.TAG_LIST_MAIN);
					}
				}
				
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	TAG_GROUP_WHITELIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Whitelist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				filter.whitelist(ClickMenu.getGroup());
				filter.refresh();
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	TAG_GROUP_BLACKLIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Blacklist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				filter.blacklist(ClickMenu.getGroup());
				filter.refresh();
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	TAG_GROUP_UNLIST {
		public TextNode get() {
			TextNode textNode = new TextNode("Unlist All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				filter.unlist(ClickMenu.getGroup());
				filter.refresh();
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	
	TAG_NAME_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String groupBefore = ClickMenu.getGroup();
				String nameBefore = ClickMenu.getName();
				Tag tag = mainTagList.getTag(groupBefore, nameBefore);
				
				TagEditStageResult result = StageManager.getTagEditStage().show(groupBefore, nameBefore);
				String groupAfter = WordUtils.capitalize(result.getGroup().toLowerCase());
				String nameAfter = WordUtils.capitalize(result.getName().toLowerCase());
				
				tag.set(groupAfter, nameAfter);
				mainTagList.sort();
				
				if (result.isAddToSelect()) {
					select.addTag(tag);
				}
				
				if (!groupBefore.equals(groupAfter)) {
					paneFilter.updateGroupNode(groupBefore, groupAfter);
					paneSelect.updateGroupNode(groupBefore, groupAfter);
				}
				
				if (!nameBefore.equals(nameAfter)) {
					paneFilter.updateNameNode(groupAfter, nameBefore, nameAfter);
					paneSelect.updateNameNode(groupAfter, nameBefore, nameAfter);
				}
				
				ClickMenu.hideAll();
				reload.doReload();
			});
			return textNode;
		}
	},
	TAG_NAME_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
				
				Tag tag = mainTagList.getTag(group, name);
				if (StageManager.getOkCancelStage().show("Remove \"" + tag.getFull() + "\" ?")) {
					paneFilter.removeNameNode(group, name);
					paneSelect.removeNameNode(group, name);
					mainEntityList.forEach(entity -> entity.getTagList().remove(tag));
					filter.unlist(tag);
					mainTagList.remove(tag);
					reload.notify(ChangeIn.TAG_LIST_MAIN);
				}
				
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	
	COLLECTION_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CollectionUtil.create();
				reload.doReload();
				ClickMenu.hideAll();
			});
			return textNode;
		}
	},
	COLLECTION_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CollectionUtil.discard(target.get());
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
				for (Entity entity : mainEntityList) {
					try {
						Files.delete(Paths.get(FileUtil.getFileCache(entity)));
					} catch (IOException e) {
						e.printStackTrace();
					}
					entity.getGalleryTile().setImage(null);
				}
				CacheManager.createCacheInBackground(mainEntityList);
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
				Project.getCurrent().writeToDisk();
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
					StageManager.getStageMain().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
	
	private void deleteEntity(Entity entity) {
		if (filter.contains(entity)) {
			FileUtil.deleteFile(FileUtil.getFileEntity(target.get()));
			FileUtil.deleteFile(FileUtil.getFileCache(entity));
			
			paneGallery.getTilePane().getChildren().remove(entity.getGalleryTile());
			select.remove(entity);
			filter.remove(entity);
			mainEntityList.remove(entity);
		}
	}
	private void deleteSelection() {
		if (select.isEmpty()) {
			Logger.getGlobal().info("deleteSelection() - empty selection");
			return;
		}
		
		EntityList entitiesToDelete = new EntityList();
		select.forEach(entity -> {
			if (entity.getCollectionID() != 0 && !paneGallery.getExpandedGroups().contains(entity.getCollectionID())) {
				entitiesToDelete.addAll(entity.getCollection(), true);
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
		if (currentTarget.getCollectionID() == 0 || paneGallery.getExpandedGroups().contains(currentTarget.getCollectionID())) {
			String sourcePath = FileUtil.getFileEntity(currentTarget);
			ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Delete file: " + sourcePath + "?");
			if (result == ButtonBooleanValue.YES) {
				target.storePosition();
				this.deleteEntity(currentTarget);
				target.restorePosition();
				
				reload.notify(ChangeIn.FILTER, ChangeIn.TARGET);
				reload.doReload();
			}
		} else {
			ButtonBooleanValue result = StageManager.getYesNoCancelStage().show("Delete " + currentTarget.getCollection().size() + " file(s)?");
			if (result == ButtonBooleanValue.YES) {
				
				target.storePosition();
				for (Entity entity : currentTarget.getCollection()) {
					this.deleteEntity(entity);
				}
				target.restorePosition();
				reload.doReload();
			}
		}
	}
}
