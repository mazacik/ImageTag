package ui.main.display;

import base.entity.Entity;
import cache.CacheManager;
import control.Select;
import control.reload.Reload;
import enums.Direction;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import misc.FileUtil;
import misc.Settings;
import ui.decorator.Decorator;
import ui.node.NodeText;
import ui.override.HBox;

import java.io.File;

public class ControlsBase extends BorderPane {
	private NodeText btnPlayPause;
	
	private NodeText btnSkipBackward;
	private NodeText btnSkipForward;
	
	private NodeText btnSkipBackward5s;
	private NodeText btnSkipForward5s;
	
	private NodeText btnMute;
	private NodeText btnSnapshot;
	
	private NodeText btnPrevious;
	private NodeText btnNext;
	
	private NodeText lblTimeCurrent;
	private NodeText lblTimeTotal;
	
	private ProgressBar progressBar = new ProgressBar();
	
	public ControlsBase(VideoPlayer videoPlayer) {
		progressBar.prefWidthProperty().bind(this.prefWidthProperty());
		progressBar.setProgress(0);
		progressBar.skinProperty().addListener((observable, oldValue, newValue) -> progressBar.lookup(".bar").setStyle("-fx-background-insets: 1 1 1 1; -fx-padding: 0.25em;"));
		
		btnSkipBackward = new NodeText("SkipBackward", true, true, false, true);
		btnSkipBackward5s = new NodeText("SkipBackward5s", true, true, false, true);
		btnPlayPause = new NodeText("PlayPause", true, true, false, true);
		btnSkipForward = new NodeText("SkipForward", true, true, false, true);
		btnSkipForward5s = new NodeText("SkipForward5s", true, true, false, true);
		btnMute = new NodeText("Mute", true, true, false, true);
		btnSnapshot = new NodeText("Snapshot", true, true, false, true);
		
		btnPrevious = new NodeText("Previous", true, true, false, true);
		btnNext = new NodeText("Next", true, true, false, true);
		
		lblTimeCurrent = new NodeText("00:00:00", true, true, false, true);
		lblTimeTotal = new NodeText("23:59:59", true, true, false, true);
		
		this.setBackground(Decorator.getBackgroundPrimary());
		Decorator.getNodeList().add(this);
		
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
				Entity target = Select.getTarget();
				File cacheFile = new File(FileUtil.getFileCache(target));
				int thumbSize = Settings.getTileSize();
				PaneDisplay.getInstance().getVideoPlayer().snapshot(cacheFile, thumbSize, thumbSize);
				
				Image cache = CacheManager.get(target);
				target.getTile().setImage(cache);
			});
		}
		
		btnPrevious.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Select.moveTarget(Direction.LEFT);
			Select.getEntities().set(Select.getTarget());
			Reload.start();
		});
		btnNext.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Select.moveTarget(Direction.RIGHT);
			Select.getEntities().set(Select.getTarget());
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
			if (PaneDisplay.getInstance().getVideoPlayer() != null)
				PaneDisplay.getInstance().getVideoPlayer().pause();
			
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
