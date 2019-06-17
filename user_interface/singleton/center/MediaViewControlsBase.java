package user_interface.singleton.center;

import database.loader.ThumbnailReader;
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
import lifecycle.InstanceManager;
import user_interface.factory.base.TextNode;
import user_interface.utils.ColorUtil;
import user_interface.utils.NodeUtil;
import user_interface.utils.SizeUtil;
import user_interface.utils.StyleUtil;
import user_interface.utils.enums.ColorType;
import utils.ConverterUtil;
import utils.enums.Direction;

import java.io.File;

public class MediaViewControlsBase extends BorderPane {
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

    public MediaViewControlsBase(VideoPlayer videoPlayer) {
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
        if (videoPlayer != null) {
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
                    DataObject dataObject = InstanceManager.getTarget().getCurrentTarget();
                    File cacheFile = new File(dataObject.getCacheFile());
                    int thumbSize = (int) SizeUtil.getGalleryIconSize();
                    InstanceManager.getMediaPane().getVideoPlayer().snapshot(cacheFile, thumbSize, thumbSize);
                    Image thumbnail = ThumbnailReader.readThumbnail(dataObject);

                    ObservableList<Node> visibleTiles = InstanceManager.getGalleryPane().getTilePane().getChildren();
                    if (visibleTiles.contains(dataObject.getBaseTile())) {
                        int galleryPosition = InstanceManager.getGalleryPane().getTilePane().getChildren().indexOf(dataObject.getBaseTile());
                        dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
                        visibleTiles.set(galleryPosition, dataObject.getBaseTile());
                    } else {
                        dataObject.setBaseTile(new BaseTile(dataObject, thumbnail));
                    }
                }
            });
        }

        btnPrevious.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getTarget().move(Direction.LEFT);
                InstanceManager.getReload().doReload();
            }
        });
        btnNext.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                InstanceManager.getTarget().move(Direction.RIGHT);
                InstanceManager.getReload().doReload();
            }
        });
    }

    public void setVideoMode(boolean enabled) {
        if (enabled) {
            HBox hBoxLeft = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF);
            hBoxLeft.getChildren().add(btnPrevious);
            hBoxLeft.getChildren().add(lblTimeCurrent);

            HBox hBoxCenter = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF);
            hBoxCenter.getChildren().add(btnSkipBackward5s);
            hBoxCenter.getChildren().add(btnSkipBackward);
            hBoxCenter.getChildren().add(btnPlayPause);
            hBoxCenter.getChildren().add(btnSkipForward);
            hBoxCenter.getChildren().add(btnSkipForward5s);
            hBoxCenter.getChildren().add(btnMute);
            hBoxCenter.setAlignment(Pos.CENTER);

            HBox hBoxRight = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF);
            hBoxRight.getChildren().add(lblTimeTotal);
            hBoxRight.getChildren().add(btnNext);

            this.setLeft(hBoxLeft);
            this.setRight(hBoxRight);
            this.setCenter(hBoxCenter);
            this.setBottom(progressBar);
        } else {
            if (InstanceManager.getMediaPane().getVideoPlayer() != null)
                InstanceManager.getMediaPane().getVideoPlayer().pause();

            this.setLeft(btnPrevious);
            this.setRight(btnNext);
            this.setCenter(null);
            this.setBottom(null);
        }

        StyleUtil.applyStyle(this);
    }
    public void setVideoProgress(double value) {
        if (value > 0 && value <= 1) Platform.runLater(() -> progressBar.setProgress(value));
    }

    public void setTimeCurrent(long time) {
        Platform.runLater(() -> lblTimeCurrent.setText(ConverterUtil.msToHMS(time)));
    }
    public void setTimeTotal(long time) {
        Platform.runLater(() -> lblTimeTotal.setText(ConverterUtil.msToHMS(time)));
    }
}
