package ui.node;

import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
import base.tag.Tag;
import base.tag.TagList;
import cache.CacheManager;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import misc.FileUtil;
import misc.HttpUtil;
import misc.Project;
import org.apache.commons.text.WordUtils;
import ui.custom.ClickMenu;
import ui.main.side.PaneFilter;
import ui.main.side.PaneSelect;
import ui.main.stage.StageMain;
import ui.stage.StageSimpleMessage;
import ui.stage.StageEditGroup;
import ui.stage.StageEditTag;
import ui.stage.StageConfirmation;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum NodeTemplates {
	ENTITY_OPEN {
		public NodeText get() {
			NodeText nodeText = new NodeText("Open", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				File entityFile = new File(FileUtil.getFileEntity(Select.getTarget()));
				try {
					Desktop.getDesktop().open(entityFile);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenu.hideAll();
				}
			});
			return nodeText;
		}
	},
	ENTITY_OPEN_DIRECTORY {
		public NodeText get() {
			NodeText nodeText = new NodeText("Open in Directory", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				File entityFile = new File(FileUtil.getFileEntity(Select.getTarget()));
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + entityFile);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenu.hideAll();
				}
			});
			return nodeText;
		}
	},
	ENTITY_EDIT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Edit", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String entityFilePath = FileUtil.getFileEntity(Select.getTarget());
				try {
					Runtime.getRuntime().exec("mspaint.exe " + entityFilePath);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					ClickMenu.hideAll();
				}
			});
			return nodeText;
		}
	},
	ENTITY_COPY_NAME {
		public NodeText get() {
			NodeText nodeText = new NodeText("Copy File Name", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
				
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	ENTITY_COPY_PATH {
		public NodeText get() {
			NodeText nodeText = new NodeText("Copy File Path", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
				
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	ENTITY_REVERSE_IMAGE_SEARCH {
		public NodeText get() {
			NodeText nodeText = new NodeText("Reverse Image Search", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				HttpUtil.googleReverseImageSearch(Select.getTarget());
				StageSimpleMessage.show("Info", "Request sent.");
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	FILTER_SIMILAR {
		public NodeText get() {
			NodeText nodeText = new NodeText("Show Similar Files", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.showSimilar(Select.getTarget());
				StageMain.getSceneMain().viewGallery();
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	SELECTION_SET_ALL {
		public NodeText get() {
			NodeText nodeText = new NodeText("Select All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().setAll(Filter.getEntities());
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	SELECTION_SET_NONE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Select None", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().clear();
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	SELECTION_DELETE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Delete Selection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Select.getEntities().deleteFiles();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	TAG_GROUP_EDIT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Edit Tag Group", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String groupBefore = ClickMenu.getGroup();
				String groupAfter = WordUtils.capitalize(StageEditGroup.show(groupBefore).toLowerCase());
				
				TagList.getMain().forEach(tag -> {
					if (tag.getGroup().equals(groupBefore)) {
						tag.setGroup(groupAfter);
					}
				});
				
				PaneFilter.getInstance().getGroupNode(groupBefore).setGroup(groupAfter);
				PaneSelect.getInstance().getGroupNode(groupBefore).setGroup(groupAfter);
				
				ClickMenu.hideAll();
				Reload.start();
			});
			return nodeText;
		}
	},
	TAG_GROUP_REMOVE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Remove Tag Group", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				
				if (StageConfirmation.show("Remove \"" + group + "\" and all of its tags?")) {
					for (String name : TagList.getMain().getNames(group)) {
						PaneFilter.getInstance().getGroupNode(group).removeNameNode(name);
						PaneSelect.getInstance().getGroupNode(group).removeNameNode(name);
						Tag tag = TagList.getMain().getTag(group, name);
						EntityList.getMain().forEach(entity -> entity.getTagList().remove(tag));
						Filter.getListManager().unlist(tag);
						TagList.getMain().remove(tag);
						Reload.notify(Notifier.TAG_LIST_MAIN);
					}
				}
				
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	TAG_GROUP_WHITELIST {
		public NodeText get() {
			NodeText nodeText = new NodeText("Whitelist All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().whitelist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return nodeText;
		}
	},
	TAG_GROUP_BLACKLIST {
		public NodeText get() {
			NodeText nodeText = new NodeText("Blacklist All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().blacklist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return nodeText;
		}
	},
	TAG_GROUP_UNLIST {
		public NodeText get() {
			NodeText nodeText = new NodeText("Unlist All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Filter.getListManager().unlist(ClickMenu.getGroup());
				Filter.refresh();
				ClickMenu.hideAll();
				Reload.start();
			});
			return nodeText;
		}
	},
	
	TAG_NAME_EDIT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Edit Tag", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Tag tag = TagList.getMain().getTag(ClickMenu.getGroup(), ClickMenu.getName());
				StageEditTag.Result result = StageEditTag.show(ClickMenu.getGroup(), ClickMenu.getName());
				
				if (result != null) {
					tag.setGroup(WordUtils.capitalize(result.getGroup().toLowerCase()));
					tag.setName(WordUtils.capitalize(result.getName().toLowerCase()));
					
					TagList.getMain().sort();
					
					if (result.isAddToSelect()) Select.getEntities().addTag(tag);
					
					Reload.notify(Notifier.TAG_LIST_MAIN);
					Reload.start();
				}
			});
			return nodeText;
		}
	},
	TAG_NAME_REMOVE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Remove Tag", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				String group = ClickMenu.getGroup();
				String name = ClickMenu.getName();
				
				Tag tag = TagList.getMain().getTag(group, name);
				if (StageConfirmation.show("Remove \"" + tag.getGroup() + " - " + tag.getName() + "\" ?")) {
					PaneFilter.getInstance().getGroupNode(group).removeNameNode(name);
					PaneSelect.getInstance().getGroupNode(group).removeNameNode(name);
					EntityList.getMain().forEach(entity -> entity.getTagList().remove(tag));
					Filter.getListManager().unlist(tag);
					TagList.getMain().remove(tag);
					Reload.notify(Notifier.TAG_LIST_MAIN);
				}
				
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	COLLECTION_CREATE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Create Collection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityCollectionUtil.create();
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	COLLECTION_DISCARD {
		public NodeText get() {
			NodeText nodeText = new NodeText("Discard Collection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				EntityCollectionUtil.discard();
				Reload.start();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	CACHE_RESET {
		public NodeText get() {
			NodeText nodeText = new NodeText("Reset Cache", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CacheManager.stopThread();
				
				EntityList.getMain().forEach(entity -> entity.getTile().setImage(null));
				
				FileUtil.deleteFile(FileUtil.getDirectoryCache());
				
				CacheManager.checkCacheInBackground(EntityList.getMain());
				
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	
	APPLICATION_SAVE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Save", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Project.getCurrent().writeToDisk();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	APPLICATION_IMPORT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Import", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				FileUtil.importFiles();
				ClickMenu.hideAll();
			});
			return nodeText;
		}
	},
	APPLICATION_EXIT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Exit", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> StageMain.getInstance().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return nodeText;
		}
	},
	;
	
	public NodeText get() {
		return null;
	}
}
