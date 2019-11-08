package application.backend.loader;

import application.backend.base.entity.Entity;
import application.backend.loader.cache.CacheWriter;
import application.backend.util.FileUtil;
import application.main.InstanceCollector;

import java.io.File;

public class BackgroundCacheLoader extends Thread implements InstanceCollector {
	@Override
	public void run() {
		for (Entity entity : entityListMain) {
			File cacheFile = new File(FileUtil.getCacheFilePath(entity));
			if (!cacheFile.exists()) {
				CacheWriter.write(entity, cacheFile);
			}
		}
	}
}
