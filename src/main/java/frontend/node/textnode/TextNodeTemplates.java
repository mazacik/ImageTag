package frontend.node.textnode;

import backend.BaseList;
import backend.TagUtil;
import backend.cache.CacheLoader;
import backend.entity.Entity;
import backend.entity.EntityList;
import backend.group.EntityGroup;
import backend.misc.FileUtil;
import backend.misc.HttpUtil;
import backend.misc.Settings;
import backend.project.ProjectUtil;
import backend.reload.InvokeHelper;
import backend.reload.Notifier;
import backend.reload.Reload;
import frontend.UserInterface;
import frontend.node.menu.ListMenu;
import frontend.stage.CollageStage;
import frontend.stage.ConfirmationStage;
import frontend.stage.SimpleMessageStage;
import frontend.stage.fileimport.ImportStage;
import frontend.stage.settings.SettingsStage;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import main.Main;

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
				
				if (Main.SELECT.getTarget() != null) {
					try {
						Desktop.getDesktop().open(new File(FileUtil.getFileEntity(Main.SELECT.getTarget())));
					} catch (IOException e) {
						e.printStackTrace();
					}
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
				
				if (Main.SELECT.getTarget() != null) {
					try {
						Runtime.getRuntime().exec("mspaint.exe \"" + FileUtil.getFileEntity(Main.SELECT.getTarget()) + "\"");
					} catch (IOException e) {
						e.printStackTrace();
					}
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
				
				if (Main.SELECT.getTarget() != null) {
					try {
						Runtime.getRuntime().exec("explorer.exe /select,\"" + FileUtil.getFileEntity(Main.SELECT.getTarget()) + "\"");
					} catch (IOException e) {
						e.printStackTrace();
					}
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
				
				if (Main.SELECT.getTarget() != null) {
					ClipboardContent content = new ClipboardContent();
					content.putString(Main.SELECT.getTarget().getName());
					Clipboard.getSystemClipboard().setContent(content);
				}
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
				
				if (Main.SELECT.getTarget() != null) {
					ClipboardContent content = new ClipboardContent();
					content.putString(FileUtil.getFileEntity(Main.SELECT.getTarget()));
					Clipboard.getSystemClipboard().setContent(content);
				}
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
				
				if (Main.SELECT.getTarget() != null) {
					Main.SELECT.deleteTarget();
					Reload.start();
				}
			});
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() <= 1;
		}
	},
	
	FILE_TAGS_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				if (Main.SELECT.getTarget() != null) {
					TagUtil.setClipboard(Main.SELECT.getTarget().getTagList());
				}
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
				
				if (Main.SELECT.getTarget() != null) {
					Main.SELECT.getTarget().getTagList().setAll(TagUtil.getClipboard());
					
					Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
					Reload.start();
				}
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
				
				if (Main.SELECT.getTarget() != null) {
					if (new ConfirmationStage("Clear tags of \"" + Main.SELECT.getTarget().getName() + "\"?").getResult()) {
						Main.SELECT.getTarget().getTagList().clear();
						
						Reload.notify(Notifier.SELECT_TAGLIST_CHANGED);
						Reload.start();
					}
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
				
				if (Main.SELECT.size() > 1) {
					double maxSize = -1;
					BaseList<Pair<Entity, Double>> pairs = new BaseList<>();
					
					for (Entity entity : Main.SELECT) {
						Image image = new Image("file:" + FileUtil.getFileEntity(entity));
						double size = image.getWidth() * image.getHeight();
						
						pairs.add(new Pair<>(entity, size));
						
						if (size > maxSize) {
							maxSize = size;
						}
					}
					
					for (Pair<Entity, Double> pair : pairs) {
						if (pair.getValue() == maxSize) {
							Main.SELECT.remove(pair.getKey());
							Reload.requestBorderUpdate(pair.getKey());
						}
					}
					
					Reload.start();
				}
			});
			
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() > 1;
		}
	},
	
	SELECTION_TAGS_COPY {
		public TextNode get() {
			TextNode textNode = new TextNode("Copy", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.setClipboard(Main.SELECT.getTagList());
			});
			return textNode;
		}
	},
	SELECTION_TAGS_PASTE_ADD {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste (Add)", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.addTags(TagUtil.getClipboard());
				
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_TAGS_PASTE_REPLACE {
		public TextNode get() {
			TextNode textNode = new TextNode("Paste (Replace)", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.setTags(TagUtil.getClipboard());
				
				Reload.start();
			});
			return textNode;
		}
	},
	SELECTION_TAGS_MERGE {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Merge", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.mergeTags();
				
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
				
				if (new ConfirmationStage("Clear tags of " + Main.SELECT.size() + " entities?").getResult()) {
					Main.SELECT.forEach(entity -> entity.getTagList().clear());
					
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
				
				if (Main.SELECT.getTarget() != null) {
					HttpUtil.googleRIS(Main.SELECT.getTarget());
				}
			});
			return textNode;
		}
	},
	
	FILTER_CREATE_PRESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Create Preset", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
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
				
				if (Main.SELECT.getTarget() != null) {
					UserInterface.getCenterPane().showGalleryPane();
					Main.FILTER.showSimilar(Main.SELECT.getTarget());
					Reload.start();
				}
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
				
				if (Main.SELECT.getTarget() != null) {
					new CollageStage();
				}
			});
			return textNode;
		}
	},
	
	SELECT_ALL {
		public TextNode get() {
			TextNode textNode = new TextNode("Select All", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.setAll(Main.FILTER);
				Reload.start();
			});
			return textNode;
		}
	},
	SELECT_NONE {
		public TextNode get() {
			TextNode textNode = new TextNode("Select None", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.clear();
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
				
				Main.SELECT.deleteSelect();
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.size() > 1;
		}
	},
	
	TAG_CREATE {
		public TextNode get() {
			TextNode textNode = new TextNode("+", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.create();
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_EDIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Edit", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.edit();
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_REMOVE {
		public TextNode get() {
			TextNode textNode = new TextNode("Remove", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.remove();
				
				Reload.start();
			});
			return textNode;
		}
	},
	TAG_DELETE {
		public TextNode get() {
			TextNode textNode = new TextNode("Delete", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				TagUtil.delete();
				
				Reload.start();
			});
			return textNode;
		}
	},
	
	GROUP_CREATE {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Create", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				EntityGroup.createFrom(Main.SELECT);
				
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return !Main.SELECT.isGroup();
		}
	},
	GROUP_DISCARD {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Discard", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				if (Main.SELECT.isGroup()) {
					Main.SELECT.getFirst().getEntityGroup().discard();
				}
				
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.isGroup();
		}
	},
	
	GROUP_MERGE_TAGS {
		@Override public TextNode get() {
			TextNode textNode = new TextNode("Merge Tags", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				Main.SELECT.getTarget().getEntityGroup().mergeTags();
				
				Reload.start();
			});
			return textNode;
		}
		@Override public boolean resolveVisible() {
			return Main.SELECT.isGroup();
		}
	},
	
	CACHE_RESET {
		public TextNode get() {
			TextNode textNode = new TextNode("Reset Cache", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				
				CacheLoader.recreate();
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
				
				Settings.write();
				
				if (ProjectUtil.getCurrentProject().write()) {
					new SimpleMessageStage("Save", "OK").showAndWait();
				} else {
					new SimpleMessageStage("Save", "Error").showAndWait();
				}
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
			TextNode textNode = new TextNode("Rescan", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				BaseList<String> entitiesInApp = new BaseList<>();
				Main.ENTITYLIST.forEach(entity -> entitiesInApp.add(FileUtil.getFileEntity(entity)));
				
				BaseList<String> entitiesOnDisk = new BaseList<>();
				File projectDirectory = new File(ProjectUtil.getCurrentProject().getDirectory());
				FileUtil.getFiles(projectDirectory, true).forEach(file -> entitiesOnDisk.add(file.getAbsolutePath()));
				
				//add entitiesOnDiskNotInApp
				BaseList<String> entitiesOnDiskNotInApp = new BaseList<>(entitiesOnDisk);
				entitiesOnDiskNotInApp.removeAll(entitiesInApp);
				
				if (!entitiesOnDiskNotInApp.isEmpty()) {
					EntityList entityList = new EntityList();
					entitiesOnDiskNotInApp.forEach(path -> entityList.add(new Entity(new File(path))));
					CacheLoader.startCacheThread(entityList);
					
					Main.ENTITYLIST.addAll(entityList);
					Main.ENTITYLIST.sortByName();
					
					Main.FILTER.getLastImport().setAll(entityList);
					
					Reload.notify(Notifier.ENTITYLIST_CHANGED);
				}
				
				//remove entitiesInAppNotOnDisk
				BaseList<String> entitiesInAppNotOnDisk = new BaseList<>(entitiesInApp);
				entitiesInAppNotOnDisk.removeAll(entitiesOnDisk);
				
				if (!entitiesInAppNotOnDisk.isEmpty()) {
					EntityList entitiesToRemove = new EntityList();
					
					entitiesInAppNotOnDisk.forEach(path -> {
						for (Entity entity : Main.ENTITYLIST) {
							if (FileUtil.getFileEntity(entity).equals(path)) {
								entitiesToRemove.add(entity);
								break;
							}
						}
					});
					
					Main.ENTITYLIST.removeAll(entitiesToRemove);
					
					Reload.notify(Notifier.ENTITYLIST_CHANGED);
				}
				
				Reload.request(InvokeHelper.FILTER_REFRESH);
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
	APPLICATION_SAVE_AND_CLOSE {
		public TextNode get() {
			TextNode textNode = new TextNode("Save and Close", true, true, false, true, this);
			setupNode(textNode);
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				ListMenu.hideMenus();
				Main.THREADGROUP.interrupt();
				
				Settings.write();
				
				if (ProjectUtil.getCurrentProject().write()) {
					UserInterface.getStage().showIntroScene();
				}
			});
			return textNode;
		}
	},
	APPLICATION_EXIT {
		public TextNode get() {
			TextNode textNode = new TextNode("Save and Exit", true, true, false, true, this);
			setupNode(textNode);
			//noinspection Convert2MethodRef
			textNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> UserInterface.getStage().requestExit());
			return textNode;
		}
	},
	;
	
	public TextNode get() {
		return null;
	}
	public boolean resolveVisible() {
		return true;
	}
	
	private static void setupNode(TextNode textNode) {
		textNode.setMaxWidth(Double.MAX_VALUE);
		textNode.setAlignment(Pos.CENTER_LEFT);
	}
}
