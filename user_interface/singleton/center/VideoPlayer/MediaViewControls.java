package user_interface.singleton.center.VideoPlayer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.node.popup.Direction;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;

public class MediaViewControls extends BorderPane implements InstanceRepo {
    private Label btnPlayPause;
    private Label btnSkipBackward;
    private Label btnSkipForward;
    private Label btnMute;

    private Label btnPrevious;
    private Label btnNext;

    private Label lblTimeCurrent;
    private Label lblTimeTotal;

    private ProgressBar progressBar = new ProgressBar();

    public MediaViewControls(VideoPlayer videoPlayer) {
        this.setBackground(ColorUtil.getBackgroundDef());
        progressBar.prefWidthProperty().bind(this.widthProperty());
        progressBar.setProgress(0);
        progressBar.skinProperty().addListener((observable, oldValue, newValue) -> progressBar.lookup(".bar").setStyle("-fx-background-insets: 1 1 1 1; -fx-padding: 0.25em;"));

        btnSkipBackward = NodeFactory.getLabel("SkipBackward", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnPlayPause = NodeFactory.getLabel("PlayPause", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnSkipForward = NodeFactory.getLabel("SkipForward", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnMute = NodeFactory.getLabel("Mute", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        btnPrevious = NodeFactory.getLabel("Previous", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        btnNext = NodeFactory.getLabel("Next", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);

        lblTimeCurrent = NodeFactory.getLabel("00:00:00", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
        lblTimeTotal = NodeFactory.getLabel("23:59:59", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);

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
            HBox hBoxLeft = NodeFactory.getHBox(ColorType.DEF);
            hBoxLeft.getChildren().add(btnPrevious);
            hBoxLeft.getChildren().add(lblTimeCurrent);

            HBox hBoxCenter = NodeFactory.getHBox(ColorType.DEF);
            hBoxCenter.getChildren().add(btnSkipBackward);
            hBoxCenter.getChildren().add(btnPlayPause);
            hBoxCenter.getChildren().add(btnSkipForward);
            hBoxCenter.getChildren().add(btnMute);
            hBoxCenter.setAlignment(Pos.CENTER);

            HBox hBoxRight = NodeFactory.getHBox(ColorType.DEF);
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
