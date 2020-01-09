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
import ui.stage.StageConfirmation;
import ui.stage.StageEditGroup;
import ui.stage.StageEditTag;
import ui.stage.StageSimpleMessage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum NodeTemplates {
	ENTITY_OPEN_FILE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Open File", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Desktop.getDesktop().open(new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return nodeText;
		}
	},
	ENTITY_SHOW_EXPLORER {
		public NodeText get() {
			NodeText nodeText = new NodeText("Show in Explorer", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return nodeText;
		}
	},
	ENTITY_EDIT_PAINT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Edit in Paint", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Runtime.getRuntime().exec("mspaint.exe " + FileUtil.getFileEntity(Select.getTarget()));
				} catch (IOException e) {
					e.printStackTrace();
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
				ClickMenu.hideAll();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return nodeText;
		}
	},
	ENTITY_COPY_PATH {
		public NodeText get() {
			NodeText nodeText = new NodeText("Copy File Path", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return nodeText;
		}
	},
	ENTITY_REVERSE_IMAGE_SEARCH {
		public NodeText get() {
			NodeText nodeText = new NodeText("Reverse Image Search", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				HttpUtil.googleReverseImageSearch(Select.getTarget());
				StageSimpleMessage.show("Info", "Request Sent.");
			});
			return nodeText;
		}
	},
	
	FILTER_SIMILAR {
		public NodeText get() {
			NodeText nodeText = new NodeText("Show Similar Files", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				StageMain.getSceneMain().viewGallery();
				Filter.showSimilar(Select.getTarget());
				Reload.start();
			});
			return nodeText;
		}
	},
	
	SELECTION_SET_ALL {
		public NodeText get() {
			NodeText nodeText = new NodeText("Select All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().setAll(Filter.getEntities());
				Reload.start();
			});
			return nodeText;
		}
	},
	SELECTION_SET_NONE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Select None", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().clear();
				Reload.start();
			});
			return nodeText;
		}
	},
	SELECTION_DELETE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Delete Selection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().deleteFiles();
			});
			return nodeText;
		}
	},
	
	TAG_GROUP_EDIT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Edit Tag Group", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				String groupBefore = ClickMenu.getGroup();
				String groupAfter = WordUtils.capitalize(StageEditGroup.show(groupBefore).toLowerCase());
				
				for (Tag tag : TagList.getMain()) {
					if (groupBefore.equals(tag.getGroup())) {
						tag.setGroup(groupAfter);
					}
				}
				
				TagList.getMain().sort();
				
				Reload.notify(Notifier.TAG_LIST_MAIN);
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
				ClickMenu.hideAll();
				
				String group = ClickMenu.getGroup();
				if (StageConfirmation.show("Remove \"" + group + "\" and all of its tags?")) {
					for (String name : TagList.getMain().getNames(group)) {
						PaneFilter.getInstance().getGroupNode(group).removeNameNode(name);
						PaneSelect.getInstance().getGroupNode(group).removeNameNode(name);
						
						Tag tag = TagList.getMain().getTag(group, name);
						EntityList.getMain().forEach(entity -> entity.getTagList().remove(tag));
						Filter.getListManager().unlist(tag);
						TagList.getMain().remove(tag);
					}
					Reload.notify(Notifier.TAG_LIST_MAIN);
					Reload.start();
				}
			});
			return nodeText;
		}
	},
	TAG_GROUP_WHITELIST {
		public NodeText get() {
			NodeText nodeText = new NodeText("Whitelist All", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Filter.getListManager().whitelist(ClickMenu.getGroup());
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
				ClickMenu.hideAll();
				
				Filter.getListManager().blacklist(ClickMenu.getGroup());
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
				ClickMenu.hideAll();
				
				Filter.getListManager().unlist(ClickMenu.getGroup());
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
				ClickMenu.hideAll();
				
				Tag tag = TagList.getMain().getTag(ClickMenu.getGroup(), ClickMenu.getName());
				StageEditTag.Result result = StageEditTag.show(ClickMenu.getGroup(), ClickMenu.getName());
				
				if (result != null) {
					tag.setGroup(result.getGroup());
					tag.setName(result.getName());
					
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
				ClickMenu.hideAll();
				
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
					Reload.start();
				}
			});
			return nodeText;
		}
	},
	
	COLLECTION_CREATE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Create Collection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				EntityCollectionUtil.create();
				Reload.start();
			});
			return nodeText;
		}
	},
	COLLECTION_DISCARD {
		public NodeText get() {
			NodeText nodeText = new NodeText("Discard Collection", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				EntityCollectionUtil.discard();
				Reload.start();
			});
			return nodeText;
		}
	},
	
	CACHE_RESET {
		public NodeText get() {
			NodeText nodeText = new NodeText("Reset Cache", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				CacheManager.stopCacheThread();
				
				EntityList.getMain().forEach(entity -> entity.getTile().setImage(null));
				
				FileUtil.deleteFile(FileUtil.getDirectoryCache());
				
				CacheManager.checkCacheInBackground(EntityList.getMain());
			});
			return nodeText;
		}
	},
	
	APPLICATION_SAVE {
		public NodeText get() {
			NodeText nodeText = new NodeText("Save", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Project.getCurrent().writeToDisk();
			});
			return nodeText;
		}
	},
	APPLICATION_IMPORT {
		public NodeText get() {
			NodeText nodeText = new NodeText("Import", true, true, false, true);
			nodeText.setMaxWidth(Double.MAX_VALUE);
			nodeText.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				FileUtil.importFiles();
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
