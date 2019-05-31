package user_interface.singleton.center;

import database.object.DataObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import loader.cache.CacheReader;
import system.Direction;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.factory.base.TextNode;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.utils.SizeUtil;

import java.io.File;

public class MediaViewControls extends BorderPane implements Instances {
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

    public MediaViewControls(VideoPlayer videoPlayer) {
        this.setBackground(ColorUtil.getBackgroundDef());
        progressBar.prefWidthProperty().bind(this.prefWidthProperty());
        progressBar.setProgress(0);
        progressBar.skinProperty().addListener((observable, oldValue, newValue) -> progressBar.lookup(".bar").setStyle("-fx-background-insets: 1 1 1 1; -fx-padding: 0.25em;"));

        btnSkipBackward = new TextNode("SkipBackward", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnSkipBackward5s = new TextNode("SkipBackward5s", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnPlayPause = new TextNode("PlayPause", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnSkipForward = new TextNode("SkipForward", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnSkipForward5s = new TextNode("SkipForward5s", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnMute = new TextNode("Mute", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnSnapshot = new TextNode("Snapshot", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        btnPrevious = new TextNode("Previous", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnNext = new TextNode("Next", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        lblTimeCurrent = new TextNode("00:00:00", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
        lblTimeTotal = new TextNode("23:59:59", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);

        initEvents(videoPlayer);
    }

    private void initEvents(VideoPlayer videoPlayer) {
        btnPlayPause.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (videoPlayer.hasMedia()) {
                    if (videoPlayer.isPlaying()) {
                        videoPlayer.pause();
                    } else {
                        videoPlayer.resume();
                    }
                }
            }
        });

        long timeDelta = 5000; // time in milliseconds
        btnSkipBackward5s.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (videoPlayer.hasMedia()) {
                    videoPlayer.getControls().skipTime(-timeDelta);
                    if (!videoPlayer.isPlaying()) {
                        videoPlayer.renderFrame();
                        float position = videoPlayer.getPosition();
                        setVideoProgress(progressBar.getProgress() - position);
                    }
                }
            }
        });
        btnSkipForward5s.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (videoPlayer.hasMedia()) {
                    videoPlayer.getControls().skipPosition(+timeDelta);
                    if (!videoPlayer.isPlaying()) {
                        videoPlayer.renderFrame();
                        float position = videoPlayer.getPosition();
                        setVideoProgress(progressBar.getProgress() + position);
                    }
                }
            }
        });

        float skipDelta = 0.05f; // 0.01f = 1% of media length
        btnSkipBackward.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (videoPlayer.hasMedia()) {
                    videoPlayer.getControls().skipPosition(-skipDelta);
                    if (!videoPlayer.isPlaying()) {
                        videoPlayer.renderFrame();
                        setVideoProgress(progressBar.getProgress() - skipDelta);
                    }
                }
            }
        });
        btnSkipForward.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (videoPlayer.hasMedia()) {
                    videoPlayer.getControls().skipPosition(+skipDelta);
                    if (!videoPlayer.isPlaying()) {
                        videoPlayer.renderFrame();
                        setVideoProgress(progressBar.getProgress() + skipDelta);
                    }
                }
            }
        });

        progressBar.setOnMouseClicked(event -> {
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

        btnMute.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                videoPlayer.swapMute();
            }
        });
        btnSnapshot.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                DataObject dataObject = target.getCurrentTarget();
                File cacheFile = new File(dataObject.getCachePath());
                int thumbSize = (int) SizeUtil.getGalleryIconSize();
                mediaView.getVideoPlayer().snapshot(cacheFile, thumbSize, thumbSize);
                Image thumbnail = CacheReader.readCache(dataObject);

                ObservableList<Node> visibleTiles = tileView.getTilePane().getChildren();
                if (visibleTiles.contains(dataObject.getBaseTile())) {
                    int galleryPosition = tileView.getTilePane().getChildren().indexOf(dataObject.getBaseTile());
                    dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
                    visibleTiles.set(galleryPosition, dataObject.getBaseTile());
                } else {
                    dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
                }
            }
        });

        btnPrevious.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                target.move(Direction.LEFT);
                reload.doReload();
            }
        });
        btnNext.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                target.move(Direction.RIGHT);
                reload.doReload();
            }
        });
    }

    public void setVideoMode(boolean enabled) {
        if (enabled) {
            HBox hBoxLeft = NodeUtil.getHBox(ColorType.DEF);
            hBoxLeft.getChildren().add(btnPrevious);
            hBoxLeft.getChildren().add(lblTimeCurrent);

            HBox hBoxCenter = NodeUtil.getHBox(ColorType.DEF);
            hBoxCenter.getChildren().add(btnSkipBackward5s);
            hBoxCenter.getChildren().add(btnSkipBackward);
            hBoxCenter.getChildren().add(btnPlayPause);
            hBoxCenter.getChildren().add(btnSkipForward);
            hBoxCenter.getChildren().add(btnSkipForward5s);
            hBoxCenter.getChildren().add(btnMute);
            hBoxCenter.setAlignment(Pos.CENTER);

            HBox hBoxRight = NodeUtil.getHBox(ColorType.DEF);
            hBoxRight.getChildren().add(lblTimeTotal);
            hBoxRight.getChildren().add(btnNext);

            this.setLeft(hBoxLeft);
            this.setRight(hBoxRight);
            this.setCenter(hBoxCenter);
            this.setBottom(progressBar);
        } else {
            mediaView.getVideoPlayer().pause();

            this.setLeft(btnPrevious);
            this.setRight(btnNext);
            this.setCenter(null);
            this.setBottom(null);
        }
    }
    public void setVideoProgress(double value) {
        if (value > 0 && value <= 1) Platform.runLater(() -> progressBar.setProgress(value));
    }

    public String convertMillisToHMS(long millis) {
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
    public void setTimeCurrent(long time) {
        Platform.runLater(() -> lblTimeCurrent.setText(convertMillisToHMS(time)));
    }
    public void setTimeTotal(long time) {
        Platform.runLater(() -> lblTimeTotal.setText(convertMillisToHMS(time)));
    }
}
