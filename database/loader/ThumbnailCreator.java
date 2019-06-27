package database.loader;

import database.object.DataObject;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import main.InstanceManager;
import settings.SettingsEnum;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import userinterface.main.center.VideoPlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public abstract class ThumbnailCreator {
    private static final String CACHE_EXTENSION = ".jpg";

    public static String getCacheExtension() {
        return CACHE_EXTENSION;
    }

    public static Image createThumbnail(DataObject dataObject, File cacheFile) {
        InstanceManager.getLogger().debug("generating thumbnail for " + dataObject.getName());

        int thumbSize = InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE);

        switch (dataObject.getFileType()) {
            case IMAGE:
                return createThumbnailFromImage(dataObject, thumbSize, cacheFile);
            case GIF:
                return createThumbnailFromGif(dataObject, thumbSize, cacheFile);
            case VIDEO:
                return createThumbnailFromVideo(dataObject, thumbSize, cacheFile);
            default:
                return null;
        }
    }

    private static Image createThumbnailFromImage(DataObject dataObject, int thumbSize, File cacheFile) {
        try {
            Image thumbnail = new Image("file:" + dataObject.getPath(), thumbSize, thumbSize, false, false);
            BufferedImage writableImage = SwingFXUtils.fromFXImage(thumbnail, null);
            ImageIO.write(writableImage, "jpg", cacheFile);
            return thumbnail;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Image createThumbnailFromGif(DataObject dataObject, int thumbSize, File cacheFile) {
        try {
            GifDecoder gifDecoder = new GifDecoder();
            gifDecoder.read(dataObject.getPath());
            java.awt.Image firstFrame = gifDecoder.getFrame(0).getScaledInstance(thumbSize, thumbSize, java.awt.Image.SCALE_FAST);
            BufferedImage buffer = new BufferedImage(thumbSize, thumbSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D tGraphics2D = buffer.createGraphics();
            tGraphics2D.setBackground(Color.BLACK);
            tGraphics2D.clearRect(0, 0, thumbSize, thumbSize);
            tGraphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            tGraphics2D.drawImage(firstFrame, 0, 0, thumbSize, thumbSize, null);
            ImageIO.write(buffer, "jpg", cacheFile);
            return SwingFXUtils.toFXImage(buffer, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Image createThumbnailFromVideo(DataObject dataObject, int thumbSize, File cacheFile) {
        if (VideoPlayer.hasLibs()) {
            String[] vlcArgs = {
                    "--intf", "dummy",          /* no interface */
                    "--vout", "dummy",          /* we don't want video (output) */
                    "--no-audio",               /* we don't want audio (decoding) */
                    "--no-snapshot-preview",    /* no blending in dummy vout */
            };

            float mediaPosition = 0.5f;

            MediaPlayerFactory factory = new MediaPlayerFactory(vlcArgs);
            MediaPlayer mediaPlayer = factory.mediaPlayers().newMediaPlayer();

            final CountDownLatch inPositionLatch = new CountDownLatch(1);
            final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);

            mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
                @Override
                public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                    if (newPosition >= mediaPosition * 0.9f) { /* 90% margin */
                        inPositionLatch.countDown();
                    }
                }

                @Override
                public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
                    snapshotTakenLatch.countDown();
                }
            });

            if (mediaPlayer.media().start(dataObject.getPath())) {
                try {
                    mediaPlayer.controls().setPosition(mediaPosition);
                    inPositionLatch.await(); // Might wait forever if error

                    mediaPlayer.snapshots().save(cacheFile, thumbSize, thumbSize);
                    snapshotTakenLatch.await(); // Might wait forever if error
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mediaPlayer.controls().stop();
            }

            mediaPlayer.release();
            factory.release();

            if (cacheFile.exists()) {
                return new Image("file:" + dataObject.getCacheFile());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
