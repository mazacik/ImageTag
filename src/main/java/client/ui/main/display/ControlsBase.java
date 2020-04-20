package client.ui.main.display;

import client.cache.CacheUtil;
import client.ui.custom.textnode.TextNode;
import client.ui.decorator.Decorator;
import client.ui.override.HBox;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Root;
import server.base.entity.Entity;
import server.control.reload.Reload;
import server.enums.Direction;
import server.misc.FileUtil;
import server.misc.Settings;

import java.io.File;

public class ControlsBase extends BorderPane {
	private TextNode btnPlayPause;
	
	private TextNode btnSkipBackward;
	private TextNode btnSkipForward;
	
	private TextNode btnSkipBackward5s;
	private TextNode btnSkipForward5s;
	
	private TextNode btnMute;
	private TextNode btnSnapshot;
	
	private TextNode btnPrevious;
	private TextNode btnNext;
	
	private TextNode lblTimeCurrent;
	private TextNode lblTimeTotal;
	
	private ProgressBar progressBar = new ProgressBar();
	
	public ControlsBase(VideoPlayer videoPlayer) {
		progressBar.prefWidthProperty().bind(this.prefWidthProperty());
		progressBar.setProgress(0);
		progressBar.skinProperty().addListener((observable, oldValue, newValue) -> progressBar.lookup(".bar").setStyle("-fx-background-insets: 1 1 1 1; -fx-padding: 0.25em;"));
		
		btnSkipBackward = new TextNode("SkipBackward", true, true, false, true);
		btnSkipBackward5s = new TextNode("SkipBackward5s", true, true, false, true);
		btnPlayPause = new TextNode("PlayPause", true, true, false, true);
		btnSkipForward = new TextNode("SkipForward", true, true, false, true);
		btnSkipForward5s = new TextNode("SkipForward5s", true, true, false, true);
		btnMute = new TextNode("Mute", true, true, false, true);
		btnSnapshot = new TextNode("Snapshot", true, true, false, true);
		
		btnPrevious = new TextNode("Previous", true, true, false, true);
		btnNext = new TextNode("Next", true, true, false, true);
		
		lblTimeCurrent = new TextNode("00:00:00", true, true, false, true);
		lblTimeTotal = new TextNode("23:59:59", true, true, false, true);
		
		this.setBackground(Decorator.getBackgroundPrimary());
		this.maxHeightProperty().bind(btnPlayPause.heightProperty());
		Decorator.getNodeList().addImpl(this);
		
		initEvents(videoPlayer);
	}
	private void initEvents(VideoPlayer videoPlayer) {
		if (videoPlayer != null) {
			btnPlayPause.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (videoPlayer.hasMedia()) {
					if (videoPlayer.isPlaying()) {
						videoPlayer.pause();
					} else {
						videoPlayer.resume();
					}
				}
			});
			
			long timeDelta = 5000; // time in milliseconds
			btnSkipBackward5s.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (videoPlayer.hasMedia()) {
					videoPlayer.getControls().skipTime(-timeDelta);
					if (!videoPlayer.isPlaying()) {
						videoPlayer.renderFrame();
						float position = videoPlayer.getPosition();
						setVideoProgress(progressBar.getProgress() - position);
					}
				}
			});
			btnSkipForward5s.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (videoPlayer.hasMedia()) {
					videoPlayer.getControls().skipPosition(+timeDelta);
					if (!videoPlayer.isPlaying()) {
						videoPlayer.renderFrame();
						float position = videoPlayer.getPosition();
						setVideoProgress(progressBar.getProgress() + position);
					}
				}
			});
			
			float skipDelta = 0.05f; // 0.01f = 1% of media length
			btnSkipBackward.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (videoPlayer.hasMedia()) {
					videoPlayer.getControls().skipPosition(-skipDelta);
					if (!videoPlayer.isPlaying()) {
						videoPlayer.renderFrame();
						setVideoProgress(progressBar.getProgress() - skipDelta);
					}
				}
			});
			btnSkipForward.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				if (videoPlayer.hasMedia()) {
					videoPlayer.getControls().skipPosition(+skipDelta);
					if (!videoPlayer.isPlaying()) {
						videoPlayer.renderFrame();
						setVideoProgress(progressBar.getProgress() + skipDelta);
					}
				}
			});
			
			progressBar.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getButton() == MouseButton.PRIMARY) {
					if (videoPlayer.hasMedia()) {
						double eventX = event.getX();
						double barWidth = progressBar.getWidth();
						double position = (eventX / barWidth);
						videoPlayer.setPosition(position);
						if (!videoPlayer.isPlaying()) {
							videoPlayer.renderFrame();
						}
					}
				}
			});
			
			btnMute.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, videoPlayer::swapMute);
			btnSnapshot.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
				Entity target = Root.SELECT.getTarget();
				File cacheFile = new File(FileUtil.getFileCache(target));
				int thumbSize = Settings.GALLERY_TILE_SIZE.getInteger();
				Root.DISPLAY_PANE.getVideoPlayer().snapshot(cacheFile, thumbSize, thumbSize);
				
				Image cache = CacheUtil.getCache(target);
				target.getTile().setImage(cache);
			});
		}
		
		btnPrevious.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Root.SELECT.moveTarget(Direction.LEFT);
			Root.SELECT.set(Root.SELECT.getTarget());
			Reload.start();
		});
		btnNext.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Root.SELECT.moveTarget(Direction.RIGHT);
			Root.SELECT.set(Root.SELECT.getTarget());
			Reload.start();
		});
	}
	
	public void setVideoMode(boolean enabled) {
		if (enabled) {
			HBox hBoxLeft = new HBox();
			hBoxLeft.getChildren().add(btnPrevious);
			hBoxLeft.getChildren().add(lblTimeCurrent);
			
			HBox hBoxCenter = new HBox();
			hBoxCenter.getChildren().add(btnSkipBackward5s);
			hBoxCenter.getChildren().add(btnSkipBackward);
			hBoxCenter.getChildren().add(btnPlayPause);
			hBoxCenter.getChildren().add(btnSkipForward);
			hBoxCenter.getChildren().add(btnSkipForward5s);
			hBoxCenter.getChildren().add(btnMute);
			hBoxCenter.setAlignment(Pos.CENTER);
			
			HBox hBoxRight = new HBox();
			hBoxRight.getChildren().add(lblTimeTotal);
			hBoxRight.getChildren().add(btnNext);
			
			this.setLeft(hBoxLeft);
			this.setRight(hBoxRight);
			this.setCenter(hBoxCenter);
			this.setBottom(progressBar);
		} else {
			if (Root.DISPLAY_PANE.getVideoPlayer() != null)
				Root.DISPLAY_PANE.getVideoPlayer().pause();
			
			this.setLeft(btnPrevious);
			this.setRight(btnNext);
			this.setCenter(null);
			this.setBottom(null);
		}
	}
	public void setVideoProgress(double value) {
		if (value > 0 && value <= 1) Platform.runLater(() -> progressBar.setProgress(value));
	}
	
	public void setTimeCurrent(long time) {
		Platform.runLater(() -> lblTimeCurrent.setText(this.msToHMS(time)));
	}
	public void setTimeTotal(long time) {
		Platform.runLater(() -> lblTimeTotal.setText(this.msToHMS(time)));
	}
	
	public String msToHMS(long millis) {
		long totalSeconds = millis / 1000;
		long hours = (totalSeconds / 3600);
		long mins = (totalSeconds / 60) % 60;
		long secs = totalSeconds % 60;
		
		String hoursString = (hours == 0)
				? "00"
				: ((hours < 10)
				? "0" + hours
				: "" + hours);
		String minsString = (mins == 0)
				? "00"
				: ((mins < 10)
				? "0" + mins
				: "" + mins);
		String secsString = (secs == 0)
				? "00"
				: ((secs < 10)
				? "0" + secs
				: "" + secs);
		return hoursString + ":" + minsString + ":" + secsString;
	}
}
