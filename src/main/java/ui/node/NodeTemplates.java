package ui.node;

import base.CustomList;
import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
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
import ui.custom.ClickMenu;
import ui.main.side.TagNode;
import ui.main.stage.MainStage;
import ui.stage.ConfirmationStage;
import ui.stage.SimpleMessageStage;
import ui.stage.TagEditStage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum NodeTemplates {
	ENTITY_OPEN_FILE {
		public TextNode get() {
			TextNode textNode = new TextNode("Open File", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Desktop.getDesktop().open(new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	ENTITY_SHOW_EXPLORER {
		public TextNode get() {
			TextNode textNode = new TextNode("Show in Explorer", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	ENTITY_EDIT_PAINT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit in Paint", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				try {
					Runtime.getRuntime().exec("mspaint.exe " + FileUtil.getFileEntity(Select.getTarget()));
				} catch (IOException e) {
					e.printStackTrace();
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
				ClickMenu.hideAll();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	ENTITY_COPY_PATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	ENTITY_REVERSE_IMAGE_SEARCH {
		public TextNode get() {
			TextNode textNode = new TextNode("Reverse Image Search", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				HttpUtil.googleReverseImageSearch(Select.getTarget());
				SimpleMessageStage.show("Info", "Request Sent.");
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Show Similar Files", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				MainStage.getMainScene().viewGallery();
				Filter.showSimilar(Select.getTarget());
				Reload.start();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().setAll(Filter.getEntities());
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_SET_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().clear();
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete Selection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Select.getEntities().deleteFiles();
			});
			return textNode;
		}
	},
	
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				String stringBefore = ClickMenu.getTagNode().getStringValue();
				int countBefore = ClickMenu.getTagNode().getLevel() + 1;
				CustomList<String> levelsAfter = TagEditStage.show(ClickMenu.getTagNode().getLevels());
				
				if (levelsAfter != null) {
					TagList.getMain().getTagsContaining(stringBefore).forEach(tag -> tag.replaceLevelsFromStart(countBefore, levelsAfter));
					TagList.getMain().sort();
					
					Reload.notify(Notifier.TAG_LIST_MAIN);
					Reload.start();
				}
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				TagNode tagNode = ClickMenu.getTagNode();
				if (ConfirmationStage.show("Remove \"" + tagNode.getText() + "\" ?")) {
					TagList tagList = TagList.getMain().getTagsContaining(tagNode.getStringValue());
					
					tagNode.getSubNodesComplete().forEach(subNode -> Filter.getListManager().unlist(subNode));
					EntityList.getMain().forEach(entity -> entity.removeTag(tagList));
					TagList.getMain().removeAll(tagList);
					
					Reload.notify(Notifier.TAG_LIST_MAIN);
					Reload.start();
				}
			});
			return textNode;
		}
	},
	
	COLLECTION_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				EntityCollectionUtil.create();
				Reload.start();
			});
			return textNode;
		}
	},
	COLLECTION_DISCARD {
		public TextNode get() {
			TextNode textNode = new TextNode("Discard Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				EntityCollectionUtil.discard();
				Reload.start();
			});
			return textNode;
		}
	},
	
	CACHE_RESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Reset Cache", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				CacheManager.stopCacheThread();
				
				EntityList.getMain().forEach(entity -> entity.getTile().setImage(null));
				
				FileUtil.deleteFile(FileUtil.getDirectoryCache());
				
				CacheManager.checkCacheInBackground(EntityList.getMain());
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				Project.getCurrent().writeToDisk();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ClickMenu.hideAll();
				
				FileUtil.importFiles();
			});
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Exit", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> MainStage.getInstance().fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
}
