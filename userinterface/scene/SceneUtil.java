package userinterface.scene;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.InstanceManager;
import userinterface.main.center.GalleryPane;
import userinterface.main.center.MediaPane;
import userinterface.main.center.MediaViewControls;
import userinterface.main.center.VideoPlayer;
import userinterface.nodes.base.TextNode;
import userinterface.style.SizeUtil;
import userinterface.style.StyleUtil;

public abstract class SceneUtil {
    private static IntroScene introScene;
    private static ProjectScene projectScene;
    private static MainScene mainScene;

    public static void stageLayoutIntro() {
        Stage mainStage = InstanceManager.getMainStage();
        mainStage.setTitle("Welcome");
        double width = SizeUtil.getUsableScreenWidth() / 2.5;
        double height = SizeUtil.getUsableScreenHeight() / 2;
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.setMinWidth(width);
        mainStage.setMinHeight(height);
        mainStage.centerOnScreen();
	
	}
    public static void stageLayoutMain() {
        Stage mainStage = InstanceManager.getMainStage();
        mainStage.setMinWidth(100 + SizeUtil.getMinWidthSideLists() * 2 + SizeUtil.getGalleryIconSize());
        mainStage.setMinHeight(100 + SizeUtil.getPrefHeightTopMenu() + SizeUtil.getGalleryIconSize());
        mainStage.setWidth(SizeUtil.getUsableScreenWidth());
        mainStage.setHeight(SizeUtil.getUsableScreenHeight());
        mainStage.setMaximized(true);
    }

    public static void createIntroScene() {
        introScene = new IntroScene();
    }
    public static void createProjectScene() {
        projectScene = new ProjectScene();
    }
    public static void createMainScene() {
        mainScene = new MainScene();
    }

    public static void showIntroScene() {
        introScene.show();
    }
    public static void showProjectScene() {
        projectScene.show();
    }
    public static void showMainScene() {
        InstanceManager.getMainStage().setOnCloseRequest(event -> {
			InstanceManager.getLogger().debug("application exit");
            VideoPlayer videoPlayer = InstanceManager.getMediaPane().getVideoPlayer();
            if (videoPlayer != null) videoPlayer.dispose();
            InstanceManager.getSettings().writeToDisk();
            InstanceManager.getObjectListMain().writeToDisk();
            InstanceManager.getTagListMain().writeDummyToDisk();
        });
        mainScene.show();
    }
    public static Image textToImage(String text) {
        TextNode label = new TextNode(text);
        label.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        label.setTextFill(Color.RED);
        label.setWrapText(true);
        label.setFont(StyleUtil.getFont());
        label.setPadding(new Insets(0, 0, 0, 0));

        Text theText = new Text(label.getText());
        theText.setFont(label.getFont());
        int width = (int) theText.getBoundsInLocal().getWidth();
        int height = (int) theText.getBoundsInLocal().getHeight();

        WritableImage img = new WritableImage(width, height);
        Scene scene = new Scene(new Group(label));
        scene.setFill(Color.TRANSPARENT);
        scene.snapshot(img);
        return img;
    }

    public static void swapViewMode() {
        GalleryPane galleryPane = InstanceManager.getGalleryPane();
        MediaPane mediaPane = InstanceManager.getMediaPane();
        ObservableList<Node> panes = mainScene.getPanes();

        if (panes.contains(mediaPane)) {
            MediaViewControls controls = mediaPane.getControls();
            if (controls.isShowing()) controls.hide();

            VideoPlayer videoPlayer = mediaPane.getVideoPlayer();
            if (videoPlayer != null && videoPlayer.isPlaying()) videoPlayer.pause();

            panes.set(panes.indexOf(mediaPane), galleryPane);
            galleryPane.adjustViewportToCurrentTarget();
            galleryPane.requestFocus();
        } else if (panes.contains(galleryPane)) {
            panes.set(panes.indexOf(galleryPane), mediaPane);
            mediaPane.requestFocus();
        }
    }
    public static boolean isFullView() {
		return mainScene.getPanes().contains(InstanceManager.getMediaPane());
    }
}
