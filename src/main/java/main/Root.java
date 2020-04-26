package main;

import backend.cache.CacheLoader;
import backend.control.filter.Filter;
import backend.control.reload.Notifier;
import backend.control.reload.Reload;
import backend.control.select.Select;
import backend.list.BaseList;
import backend.list.collection.Collection;
import backend.list.entity.Entity;
import backend.list.entity.EntityList;
import backend.list.tag.Tag;
import backend.list.tag.TagList;
import backend.misc.FileUtil;
import backend.misc.Project;
import backend.override.Threadpool;
import frontend.component.display.DisplayPane;
import frontend.component.gallery.GalleryPane;
import frontend.component.side.FilterPane;
import frontend.component.side.SelectPane;
import frontend.component.top.ToolbarPane;
import frontend.stage.primary.PrimaryStageController;

import java.io.File;

public abstract class Root {
	public static final Threadpool THREADPOOL;
	
	public static final EntityList ENTITYLIST;
	public static final TagList TAGLIST;
	
	public static final Filter FILTER;
	public static final Select SELECT;
	
	public static final ToolbarPane TOOLBAR_PANE;
	public static final GalleryPane GALLERY_PANE;
	public static final DisplayPane DISPLAY_PANE;
	public static final FilterPane FILTER_PANE;
	public static final SelectPane SELECT_PANE;
	
	public static final PrimaryStageController PSC;
	
	static {
		THREADPOOL = new Threadpool("ROOT");
		
		ENTITYLIST = new EntityList();
		TAGLIST = new TagList();
		
		FILTER = new Filter();
		SELECT = new Select();
		
		TOOLBAR_PANE = new ToolbarPane();
		GALLERY_PANE = new GalleryPane();
		DISPLAY_PANE = new DisplayPane();
		FILTER_PANE = new FilterPane();
		SELECT_PANE = new SelectPane();
		
		PSC = new PrimaryStageController();
	}
	
	public static void startProjectDatabaseLoading() {
		initTags();
		initEntities();
		initCollections();
		
		Entity target = Root.ENTITYLIST.getFirst();
		if (target != null) {
			SELECT.setTarget(target);
			if (target.hasCollection()) {
				SELECT.setAll(target.getCollection());
			} else {
				SELECT.set(target);
			}
		}
		
		Reload.notify(Notifier.values());
		Reload.start();
		
		CacheLoader.startCacheThread(Root.ENTITYLIST);
	}
	private static void initEntities() {
		Root.ENTITYLIST.setAllImpl(Project.getCurrent().getEntityList());
		
		EntityList entitiesWithoutFiles = new EntityList(Root.ENTITYLIST);
		BaseList<File> filesWithoutEntities = FileUtil.getFiles(new File(Project.getCurrent().getDirectory()), true);
		
		BaseList<String> newFileNames = new BaseList<>();
		filesWithoutEntities.forEach(file -> newFileNames.addImpl(FileUtil.createEntityName(file)));
		
		/* match files in the source directory with known entities in the database */
		for (int i = 0; i < entitiesWithoutFiles.size(); i++) {
			for (int j = 0; j < filesWithoutEntities.size(); j++) {
				if (entitiesWithoutFiles.get(i).getName().equals(newFileNames.get(j))) {
					entitiesWithoutFiles.remove(i--);
					newFileNames.remove(j);
					filesWithoutEntities.remove(j);
					break;
				}
			}
		}
		
		/* match files with the exact same size, these were probably renamed outside of the application */
		for (int i = 0; i < filesWithoutEntities.size(); i++) {
			File newFile = filesWithoutEntities.get(i);
			long newFileLength = newFile.length();
			for (int j = 0; j < entitiesWithoutFiles.size(); j++) {
				Entity orphanEntity = entitiesWithoutFiles.get(j);
				if (newFileLength == orphanEntity.getSize()) {
					/* rename the object and client.cache file */
					File oldCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					orphanEntity.setName(FileUtil.createEntityName(newFile));
					File newCacheFile = new File(FileUtil.getFileCache(orphanEntity));
					
					if (oldCacheFile.exists() && !newCacheFile.exists()) {
						oldCacheFile.renameTo(newCacheFile);
					}
					
					filesWithoutEntities.remove(i--);
					entitiesWithoutFiles.remove(j);
					break;
				}
			}
		}
		
		if (!entitiesWithoutFiles.isEmpty()) {
			Root.ENTITYLIST.removeAll(entitiesWithoutFiles);
		}
		if (!filesWithoutEntities.isEmpty()) {
			EntityList newEntities = new EntityList(filesWithoutEntities);
			Root.ENTITYLIST.addAllImpl(newEntities);
			FILTER.getLastImport().addAllImpl(newEntities);
			Root.ENTITYLIST.sort();
		}
		
		for (Entity entity : Root.ENTITYLIST) {
			entity.initTags();
		}
	}
	private static void initCollections() {
		BaseList<Collection> collections = new BaseList<>();
		for (Entity entity : Root.ENTITYLIST) {
			if (entity.hasCollection()) {
				boolean collectionExists = false;
				for (Collection collection : collections) {
					if (collection.getFirst().getCollectionID() == entity.getCollectionID()) {
						collection.addImpl(entity);
						entity.setCollection(collection);
						collectionExists = true;
						break;
					}
				}
				if (!collectionExists) {
					Collection collection = new Collection(entity);
					entity.setCollection(collection);
					collections.addImpl(collection);
				}
			}
		}
	}
	private static void initTags() {
		TagList projectTags = Project.getCurrent().getTagList();
		if (projectTags != null) Root.TAGLIST.setAllImpl(projectTags);
		
		Root.TAGLIST.forEach(Tag::updateStringValue);
		Root.TAGLIST.sort();
		
		Reload.notify(Notifier.TAGLIST_CHANGED);
	}
}
