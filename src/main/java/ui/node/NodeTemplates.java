package ui.node;

import base.entity.EntityCollectionUtil;
import base.entity.EntityList;
import base.tag.TagList;
import base.tag.TagUtil;
import cache.CacheManager;
import control.Select;
import control.filter.Filter;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import misc.FileUtil;
import misc.HttpUtil;
import misc.Project;
import ui.custom.ClickMenu;
import ui.custom.HoverMenu;
import ui.main.stage.MainStage;
import ui.stage.CollageStage;
import ui.stage.ImportStage;
import ui.stage.SettingsStage;
import ui.stage.SimpleMessageStage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum NodeTemplates {
	FILE {
		public TextNode get() {
			TextNode textNode = new TextNode("File", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, FILE_OPEN.get()
					, FILE_EDIT.get()
					, FILE_BROWSE.get()
					, FILE_DELETE.get()
					, new SeparatorNode()
					, FILE_TAGS.get()
					, new SeparatorNode()
					, FILE_COPYFILENAME.get()
					, FILE_COPYFILEPATH.get()
					, new SeparatorNode()
					, FILE_GOOGLE_RIS.get()
					, FILE_DETAILS.get()
			);
			return textNode;
		}
	},
	FILE_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				try {
					Desktop.getDesktop().open(new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				try {
					Runtime.getRuntime().exec("mspaint.exe " + FileUtil.getFileEntity(Select.getTarget()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_BROWSE {
		public TextNode get() {
			TextNode textNode = new TextNode("Browse", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + new File(FileUtil.getFileEntity(Select.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_COPYFILENAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Name", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	FILE_COPYFILEPATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Select.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	FILE_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.deleteTarget();
				Reload.start();
			});
			return textNode;
		}
	},
	
	FILE_TAGS {
		public TextNode get() {
			TextNode textNode = new TextNode("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, NodeTemplates.FILE_TAGS_COPY.get()
					, NodeTemplates.FILE_TAGS_PASTE.get()
					, NodeTemplates.FILE_TAGS_CLEAR.get()
			);
			return textNode;
		}
	},
	FILE_TAGS_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.setClipboard(new TagList(Select.getTarget().getTagList()));
			});
			return textNode;
		}
	},
	FILE_TAGS_PASTE {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.getTarget().getTagList().setAll(new TagList(TagUtil.getClipboard()));
				
				Reload.notify(Notifier.TAGS_OF_SELECT);
				Reload.start();
			});
			return textNode;
		}
	},
	FILE_TAGS_CLEAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Clear", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.getTarget().getTagList().clear();
				
				Reload.notify(Notifier.TAGS_OF_SELECT);
				Reload.start();
			});
			return textNode;
		}
	},
	
	SELECTION {
		public TextNode get() {
			TextNode textNode = new TextNode("Selection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, SELECTION_TAGS.get()
					, new SeparatorNode()
					, SELECTION_DELETE.get()
			                  //TODO NodeTemplates.COLLECTION_CREATE.get() and NodeTemplates.COLLECTION_DISCARD.get()
			);
			return textNode;
		}
	},
	
	SELECTION_TAGS {
		public TextNode get() {
			TextNode textNode = new TextNode("Tags", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			HoverMenu.install(textNode, Direction.RIGHT
					, NodeTemplates.SELECTION_TAG_COPY.get()
					, NodeTemplates.SELECTION_TAG_PASTE.get()
					, NodeTemplates.SELECTION_TAG_CLEAR.get()
			);
			return textNode;
		}
	},
	SELECTION_TAG_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.setClipboard(new TagList(Select.getEntities().getTagList()));
			});
			return textNode;
		}
	},
	SELECTION_TAG_PASTE {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.getEntities().forEach(entity -> entity.getTagList().setAll(new TagList(TagUtil.getClipboard())));
				
				Reload.notify(Notifier.TAGS_OF_SELECT);
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_TAG_CLEAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Clear", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.getEntities().forEach(entity -> entity.getTagList().clear());
				
				Reload.notify(Notifier.TAGS_OF_SELECT);
				Reload.start();
			});
			return textNode;
		}
	},
	
	FILE_GOOGLE_RIS {
		public TextNode get() {
			TextNode textNode = new TextNode("Google RIS", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				HttpUtil.googleReverseImageSearch(Select.getTarget());
				SimpleMessageStage.show("Info", "Request Sent.");
			});
			return textNode;
		}
	},
	FILE_DETAILS {
		public TextNode get() {
			TextNode textNode = new TextNode("Details", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Image entityImage = new Image("file:" + FileUtil.getFileEntity(Select.getTarget()));
				int width = (int) entityImage.getWidth();
				int height = (int) entityImage.getHeight();
				
				SimpleMessageStage.show("Details", width + "x" + height);
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Filter Similar", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				MainStage.getMainScene().viewGallery();
				Filter.showSimilar(Select.getTarget());
				Reload.start();
			});
			return textNode;
		}
	},
	FILTER_COLLAGE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collage", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				new CollageStage();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
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
				hideMenus();
				
				Select.getEntities().clear();
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				Select.getEntities().deleteFiles();
				Reload.start();
			});
			return textNode;
		}
	},
	
	TAG_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create a Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.create();
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_CREATE_CHILD {
		public TextNode get() {
			TextNode textNode = new TextNode("Create a Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.create(TagUtil.getCurrentTagNode().getLevels());
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.edit(TagUtil.getCurrentTagNode().getStringValue(), TagUtil.getCurrentTagNode().getLevels().size());
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
				TagUtil.remove();
				
				Reload.start();
			});
			return textNode;
		}
	},
	
	COLLECTION_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collection", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				hideMenus();
				
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
				hideMenus();
				
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
				hideMenus();
				
				if (CacheManager.getThread() != null) CacheManager.getThread().interrupt();
				
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
				hideMenus();
				
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
				hideMenus();
				new ImportStage().show("");
			});
			return textNode;
		}
	},
	APPLICATION_SETTINGS {
		public TextNode get() {
			TextNode textNode = new TextNode("Settings", true, true, false, true);
			textNode.setMaxWidth(Double.MAX_VALUE);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> SettingsStage.show(""));
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
	
	public static void hideMenus() {
		ClickMenu.hideMenus();
		HoverMenu.hideMenus();
	}
}
