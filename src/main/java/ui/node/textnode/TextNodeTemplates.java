package ui.node.textnode;

import base.CustomList;
import base.entity.CollectionUtil;
import base.entity.Entity;
import base.entity.EntityList;
import base.tag.TagList;
import base.tag.TagUtil;
import cache.CacheUtil;
import control.reload.Notifier;
import control.reload.Reload;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import main.Root;
import misc.FileUtil;
import misc.HttpUtil;
import misc.Project;
import ui.EntityDetailsUtil;
import ui.custom.ListMenu;
import ui.stage.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public enum TextNodeTemplates {
	FILE_OPEN {
		public TextNode get() {
			TextNode textNode = new TextNode("Open", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				try {
					Desktop.getDesktop().open(new File(FileUtil.getFileEntity(Root.SELECT.getTarget())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				try {
					Runtime.getRuntime().exec("mspaint.exe \"" + FileUtil.getFileEntity(Root.SELECT.getTarget()) + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_BROWSE {
		public TextNode get() {
			TextNode textNode = new TextNode("Browse", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				try {
					Runtime.getRuntime().exec("explorer.exe /select,\"" + FileUtil.getFileEntity(Root.SELECT.getTarget()) + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			return textNode;
		}
	},
	FILE_COPYFILENAME {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Name", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Root.SELECT.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	FILE_COPYFILEPATH {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy File Path", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				ClipboardContent content = new ClipboardContent();
				content.putString(FileUtil.getFileEntity(Root.SELECT.getTarget()));
				Clipboard.getSystemClipboard().setContent(content);
			});
			return textNode;
		}
	},
	FILE_DELETE {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.deleteTarget();
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolve() {
			return Root.SELECT.size() <= 1;
		}
	},
	
	FILE_TAGS_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.setClipboard(new TagList(Root.SELECT.getTarget().getTagList()));
			});
			return textNode;
		}
	},
	FILE_TAGS_PASTE {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.getTarget().clearTags();
				TagUtil.getClipboard().forEach(tag -> Root.SELECT.getTarget().addTag(tag.getID()));
				
				Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
				Reload.start();
			});
			return textNode;
		}
	},
	FILE_TAGS_CLEAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Clear", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				if (new ConfirmationStage("Clear tags of \"" + Root.SELECT.getTarget().getName() + "\"?").getResult()) {
					Root.SELECT.getTarget().getTagList().clear();
					
					Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
					Reload.start();
				}
			});
			return textNode;
		}
	},
	
	SELECTION_DESELECT_LARGEST {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Deselect Largest", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				double maxSize = -1;
				CustomList<Pair<Entity, Double>> pairs = new CustomList<>();
				
				for (Entity entity : Root.SELECT) {
					Image image = new Image("file:" + FileUtil.getFileEntity(entity));
					double size = image.getWidth() * image.getHeight();
					
					pairs.add(new Pair<>(entity, size));
					
					if (size > maxSize) {
						maxSize = size;
					}
				}
				
				for (Pair<Entity, Double> pair : pairs) {
					if (pair.getValue() == maxSize) {
						Root.SELECT.remove(pair.getKey());
						Reload.requestBorderUpdate(pair.getKey());
					}
				}
				
				Reload.start();
			});
			
			return textNode;
		}
		@Override public boolean resolve() {
			return Root.SELECT.size() > 1;
		}
	},
	
	SELECTION_TAGS_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.setClipboard(new TagList(Root.SELECT.getTagList()));
			});
			return textNode;
		}
	},
	SELECTION_TAGS_PASTE {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.clearTags();
				TagUtil.getClipboard().forEach(tag -> Root.SELECT.addTag(tag.getID()));
				
				Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_TAGS_CLEAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Clear", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				if (new ConfirmationStage("Clear tags of " + Root.SELECT.size() + " entities?").getResult()) {
					Root.SELECT.forEach(entity -> entity.getTagList().clear());
					
					Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
					Reload.start();
				}
			});
			return textNode;
		}
	},
	
	FILE_GOOGLE_RIS {
		public TextNode get() {
			TextNode textNode = new TextNode("Google RIS", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				HttpUtil.googleReverseImageSearch(Root.SELECT.getTarget());
				new SimpleMessageStage("Google RIS", "Request Sent.").show();
			});
			return textNode;
		}
	},
	FILE_DETAILS {
		public TextNode get() {
			TextNode textNode = new TextNode("Details", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				EntityDetailsUtil.show();
			});
			return textNode;
		}
	},
	
	FILTER_SIMILAR {
		public TextNode get() {
			TextNode textNode = new TextNode("Filter Similar", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.MAIN_STAGE.getMainScene().viewGallery();
				Root.FILTER.showSimilar(Root.SELECT.getTarget());
				Reload.start();
			});
			return textNode;
		}
	},
	FILTER_COLLAGE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Collage", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				new CollageStage();
			});
			return textNode;
		}
	},
	
	SELECTION_SET_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.setAll(Root.FILTER);
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_SET_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.clear();
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_DELETE {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Root.SELECT.deleteSelect();
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolve() {
			return Root.SELECT.size() > 1;
		}
	},
	
	TAG_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("Create a Tag", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.create();
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_CREATE_CHILD {
		public TextNode get() {
			TextNode textNode = new TextNode("Create a Tag", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.create(TagUtil.getCurrentTagNode().getLevels());
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit Tag", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.edit(TagUtil.getCurrentTagNode().getStringValue(), TagUtil.getCurrentTagNode().getLevels().size());
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove Tag", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.remove();
				
				Reload.start();
			});
			return textNode;
		}
	},
	
	COLLECTION_CREATE {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Create Collection", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				CollectionUtil.create();
				//todo add an option to merge tags
				
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolve() {
			return !CollectionUtil.isCollection(Root.SELECT);
		}
	},
	COLLECTION_DISCARD {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Discard Collection", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				CollectionUtil.discard();
				
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolve() {
			return CollectionUtil.isCollection(Root.SELECT);
		}
	},
	
	CACHE_RESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Reset Cache", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				CacheUtil.reset();
			});
			return textNode;
		}
	},
	
	APPLICATION_SAVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Project.getCurrent().writeToDisk();
			});
			return textNode;
		}
	},
	APPLICATION_IMPORT {
		public TextNode get() {
			TextNode textNode = new TextNode("Import", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				new ImportStage().show();
			});
			return textNode;
		}
	},
	APPLICATION_REFRESH {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Refresh", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				CustomList<String> entitiesMain = new CustomList<>();
				EntityList.getMain().forEach(entity -> entitiesMain.add(FileUtil.getFileEntity(entity)));
				
				CustomList<String> entitiesDisk = new CustomList<>();
				File directorySource = new File(Project.getCurrent().getDirectorySource());
				FileUtil.getFiles(directorySource, true).forEach(file -> entitiesDisk.add(file.getAbsolutePath()));
				
				CustomList<String> helperMain = new CustomList<>(entitiesMain);
				entitiesMain.removeAll(entitiesDisk);
				entitiesDisk.removeAll(helperMain);
				
				//todo remove orphans
				
				if (!entitiesDisk.isEmpty()) {
					EntityList entities = new EntityList();
					entitiesDisk.forEach(path -> entities.addImpl(new Entity(new File(path))));
					CacheUtil.loadCache(entities);
					
					EntityList.getMain().addAllImpl(entities);
					EntityList.getMain().sort();
					
					Root.FILTER.getLastImport().setAllImpl(entities);
				}
				
				Reload.notify(Notifier.ENTITYLIST_CHANGED);
				Reload.start();
			});
			return textNode;
		}
	},
	APPLICATION_SETTINGS {
		public TextNode get() {
			TextNode textNode = new TextNode("Settings", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> new SettingsStage().show());
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Exit", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> Root.MAIN_STAGE.fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST)));
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
	public boolean resolve() {
		return true;
	}
	
	private static void setupNode(TextNode textNode) {
		textNode.setMaxWidth(Double.MAX_VALUE);
		textNode.setAlignment(Pos.CENTER_LEFT);
	}
}
