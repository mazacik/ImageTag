package application.cache;

import application.baseobject.entity.Entity;
import application.gui.main.center.VideoPlayer;
import application.main.InstanceCollector;
import application.tools.FileUtil;
import application.tools.GifDecoder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public abstract class CacheWriter implements InstanceCollector {
	private static final String CACHE_EXTENSION = ".jpg";
	
	public static synchronized Image write(Entity entity) {
		Logger.getGlobal().info(entity.getName());
		
		switch (FileUtil.getFileType(entity)) {
			case IMAGE:
				return createFromImage(entity);
			case GIF:
				return createFromGif(entity);
			case VIDEO:
				return createFromVideo(entity);
			default:
				return null;
		}
	}
	
	private static Image createFromImage(Entity entity) {
		int thumbSize = settings.getGalleryTileSize();
		Image image = new Image("file:" + entity.getPath(), thumbSize, thumbSize, false, false);
		BufferedImage buffer = SwingFXUtils.fromFXImage(image, null);
		
		if (buffer != null) {
			try {
				ImageIO.write(buffer, "jpg", new File(FileUtil.getCacheFilePath(entity)));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			Logger.getGlobal().warning(entity.getName());
		}
		
		return image;
	}
	private static Image createFromGif(Entity entity) {
		GifDecoder gifDecoder = new GifDecoder();
		gifDecoder.read(entity.getPath());
		int thumbSize = settings.getGalleryTileSize();
		
		java.awt.Image frame = gifDecoder.getFrame(gifDecoder.getFrameCount() / 2).getScaledInstance(thumbSize, thumbSize, java.awt.Image.SCALE_FAST);
		BufferedImage buffer = new BufferedImage(thumbSize, thumbSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = buffer.createGraphics();
		graphics.setBackground(Color.BLACK);
		graphics.clearRect(0, 0, thumbSize, thumbSize);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(frame, 0, 0, thumbSize, thumbSize, null);
		
		try {
			ImageIO.write(buffer, "jpg", new File(FileUtil.getCacheFilePath(entity)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return SwingFXUtils.toFXImage(buffer, null);
	}
	private static Image createFromVideo(Entity entity) {
		if (VideoPlayer.hasLibs()) {
			String[] vlcArgs = {
					"--intf", "dummy",          /* no interface */
					"--vout", "dummy",          /* no video (output) */
					"--no-audio",               /* no audio (decoding) */
					"--no-snapshot-preview",    /* no blending in dummy vout */
			};
			
			float mediaPosition = 0.5f;         /* approximate video position to take the snapshot from */
			
			MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
			MediaPlayer mediaPlayer = factory.mediaPlayers().newMediaPlayer();
			
			final CountDownLatch inPositionLatch = new CountDownLatch(1);
			final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);
			
			mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
				@Override
				public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
					if (newPosition >= mediaPosition * 0.9f) { /* 10% error margin, don't set it lower */
						inPositionLatch.countDown();
					}
				}
				
				@Override
				public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
					snapshotTakenLatch.countDown();
				}
			});
			
			File cacheFile = new File(FileUtil.getCacheFilePath(entity));
			
			if (mediaPlayer.media().start(entity.getPath())) {
				try {
					mediaPlayer.controls().setPosition(mediaPosition);
					inPositionLatch.await(); // might wait forever if error
					
					int thumbSize = settings.getGalleryTileSize();
					mediaPlayer.snapshots().save(cacheFile, thumbSize, thumbSize);
					snapshotTakenLatch.await(); // might wait forever if error
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				mediaPlayer.controls().stop();
			}
			
			mediaPlayer.release();
			factory.release();
			
			if (cacheFile.exists()) {
				return new Image("file:" + cacheFile.getAbsolutePath());
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static void writeAllCacheInBackground() {
		new Thread(() -> {
			for (Entity entity : entityListMain) {
				File cacheFile = new File(FileUtil.getCacheFilePath(entity));
				if (!cacheFile.exists()) {
					CacheWriter.write(entity);
				}
			}
		}).start();
	}
	
	public static String getCacheExtension() {
		return CACHE_EXTENSION;
	}
}
