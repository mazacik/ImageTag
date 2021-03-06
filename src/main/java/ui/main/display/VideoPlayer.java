package ui.main.display;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.ControlsApi;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurfaceAdapters;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import java.io.File;
import java.nio.ByteBuffer;

public class VideoPlayer {
	private final WritablePixelFormat<ByteBuffer> pixelFormat;
	private final MediaPlayerFactory mediaPlayerFactory;
	private final EmbeddedMediaPlayer mediaPlayer;
	private final Canvas canvas;
	private PixelWriter pixelWriter;
	private WritableImage img;
	private VideoPlayer videoPlayer = this;
	private AnimationTimer frameTimer = new AnimationTimer() {
		@Override
		public void handle(long now) {
			videoPlayer.renderFrame();
		}
	};
	private boolean playing;
	
	private static boolean bVLCLibsFound = false;
	
	public static boolean findVLCLibs() {
		bVLCLibsFound = new NativeDiscovery().discover();
		return bVLCLibsFound;
	}
	public static boolean hasLibs() {
		return bVLCLibsFound;
	}
	
	private VideoPlayer(Canvas canvas) {
		this.canvas = canvas;
		
		pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
		pixelFormat = PixelFormat.getByteBgraInstance();
		
		mediaPlayerFactory = new MediaPlayerFactory();
		mediaPlayerFactory.application().newLog().release();
		mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
		mediaPlayer.videoSurface().set(new JavaFxVideoSurface());
		mediaPlayer.controls().setRepeat(true);
		mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				super.timeChanged(mediaPlayer, newTime);
				PaneDisplay.getInstance().getControls().setTimeCurrent(newTime);
			}
			@Override
			public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
				super.positionChanged(mediaPlayer, newPosition);
				PaneDisplay.getInstance().getControls().setVideoProgress(newPosition);
			}
			@Override
			public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
				super.lengthChanged(mediaPlayer, newLength);
				PaneDisplay.getInstance().getControls().setTimeTotal(newLength);
			}
			@Override
			public void opening(MediaPlayer mediaPlayer) {
				super.opening(mediaPlayer);
				PaneDisplay.getInstance().getControls().setVideoProgress(0);
			}
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				super.finished(mediaPlayer);
				PaneDisplay.getInstance().getControls().setVideoProgress(1);
			}
		});
	}
	public static VideoPlayer create(Canvas canvas) {
		if (findVLCLibs()) return new VideoPlayer(canvas);
		else return null;
	}
	
	public void start(String videoFilePath) {
		playing = true;
		frameTimer.start();
		mediaPlayer.media().play(videoFilePath);
	}
	public void pause() {
		if (hasMedia() && mediaPlayer.status().isPlaying()) {
			playing = false;
			frameTimer.stop();
			mediaPlayer.controls().pause();
		}
	}
	public void resume() {
		if (hasMedia() && !mediaPlayer.status().isPlaying()) {
			playing = true;
			frameTimer.start();
			mediaPlayer.controls().play();
		}
	}
	public void dispose() {
		if (hasMedia()) {
			playing = false;
			frameTimer.stop();
			mediaPlayer.controls().stop();
		}
		mediaPlayer.release();
		mediaPlayerFactory.release();
	}
	public void swapMute() {
		mediaPlayer.audio().setMute(!mediaPlayer.audio().isMute());
	}
	
	public void snapshot(File file, int width, int height) {
		mediaPlayer.snapshots().save(file, width, height);
	}
	
	public boolean isPlaying() {
		return playing;
	}
	public boolean hasMedia() {
		return mediaPlayer.media().isValid();
	}
	public ControlsApi getControls() {
		return mediaPlayer.controls();
	}
	public float getPosition() {
		return mediaPlayer.status().position();
	}
	
	public void setPosition(double position) {
		mediaPlayer.controls().setPosition((float) position);
	}
	public void renderFrame() {
		if (img != null) {
			double originWidth = img.getWidth();
			double originHeight = img.getHeight();
			double maxWidth = canvas.getWidth();
			double maxHeight = canvas.getHeight();
			
			boolean upScale = false;
			
			double resultWidth;
			double resultHeight;
			
			if (!upScale && originWidth < maxWidth && originHeight < maxHeight) {
				// image is smaller than canvas or upscaling is off
				resultWidth = originWidth;
				resultHeight = originHeight;
			} else {
				// scale image to fit width
				resultWidth = maxWidth;
				resultHeight = originHeight * maxWidth / originWidth;
				
				// if scaled image is too tall, scale to fit height instead
				if (resultHeight > maxHeight) {
					resultHeight = maxHeight;
					resultWidth = originWidth * maxHeight / originHeight;
				}
			}
			
			double resultX = maxWidth / 2 - resultWidth / 2;
			double resultY = maxHeight / 2 - resultHeight / 2;
			
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.clearRect(0, 0, maxWidth, maxHeight);
			gc.drawImage(img, resultX, resultY, resultWidth, resultHeight);
		}
	}
	private class JavaFxVideoSurface extends CallbackVideoSurface {
		JavaFxVideoSurface() {
			super(new JavaFxBufferFormatCallback(), new JavaFxRenderCallback(), true, VideoSurfaceAdapters.getVideoSurfaceAdapter());
		}
	}
	private class JavaFxBufferFormatCallback implements BufferFormatCallback {
		@Override
		public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
			VideoPlayer.this.img = new WritableImage(sourceWidth, sourceHeight);
			VideoPlayer.this.pixelWriter = img.getPixelWriter();
			return new RV32BufferFormat(sourceWidth, sourceHeight);
		}
	}
	private class JavaFxRenderCallback implements RenderCallback {
		@Override
		public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
			pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, nativeBuffers[0], bufferFormat.getPitches()[0]);
		}
	}
}
