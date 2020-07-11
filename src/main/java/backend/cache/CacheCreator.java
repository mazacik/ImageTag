package backend.cache;

import backend.entity.Entity;
import backend.misc.FileUtil;
import backend.misc.Settings;
import frontend.component.display.VideoPlayer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.Main;
import org.apache.commons.lang3.StringUtils;
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

abstract class CacheCreator {
	static Image create(Entity entity) {
		String entityIndex = StringUtils.right("00000000" + (Main.DB_ENTITY.indexOf(entity) + 1), String.valueOf(Main.DB_ENTITY.size()).length());
		Logger.getGlobal().info(String.format("[%s/%s] %s", entityIndex, Main.DB_ENTITY.size(), entity.getName()));
		
		switch (entity.getType()) {
			case IMG:
				return fromImg(entity);
			case GIF:
				return fromGif(entity);
			case VID:
				return fromVid(entity);
			default:
				return null;
		}
	}
	
	private static Image fromImg(Entity entity) {
		int thumbSize = Settings.GALLERY_TILE_SIZE.getInteger();
		Image image = new Image("file:" + FileUtil.getFileEntity(entity), thumbSize, thumbSize, false, false);
		BufferedImage buffer = SwingFXUtils.fromFXImage(image, null);
		
		if (buffer != null) {
			try {
				File cacheFile = new File(FileUtil.getFileCache(entity));
				cacheFile.getParentFile().mkdirs();
				ImageIO.write(buffer, FileUtil.EXT_CACHE, cacheFile);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return image;
	}
	private static Image fromGif(Entity entity) {
		GifDecoder gifDecoder = new GifDecoder();
		gifDecoder.read(FileUtil.getFileEntity(entity));
		int thumbSize = Settings.GALLERY_TILE_SIZE.getInteger();
		
		java.awt.Image frame = gifDecoder.getFrame(gifDecoder.getFrameCount() / 2).getScaledInstance(thumbSize, thumbSize, java.awt.Image.SCALE_FAST);
		BufferedImage buffer = new BufferedImage(thumbSize, thumbSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = buffer.createGraphics();
		graphics.setBackground(Color.BLACK);
		graphics.clearRect(0, 0, thumbSize, thumbSize);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.drawImage(frame, 0, 0, thumbSize, thumbSize, null);
		
		entity.setMediaDuration(gifDecoder.getFrameCount() * gifDecoder.getDelay(1));
		
		try {
			File cacheFile = new File(FileUtil.getFileCache(entity));
			cacheFile.getParentFile().mkdirs();
			ImageIO.write(buffer, FileUtil.EXT_CACHE, cacheFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return SwingFXUtils.toFXImage(buffer, null);
	}
	private static Image fromVid(Entity entity) {
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
			
			File cacheFile = new File(FileUtil.getFileCache(entity));
			cacheFile.getParentFile().mkdirs();
			
			if (mediaPlayer.media().start(FileUtil.getFileEntity(entity))) {
				try {
					entity.setMediaDuration(mediaPlayer.media().info().duration());
					mediaPlayer.controls().setPosition(mediaPosition);
					inPositionLatch.await(); // might wait forever if error
					
					int thumbSize = Settings.GALLERY_TILE_SIZE.getInteger();
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
}
