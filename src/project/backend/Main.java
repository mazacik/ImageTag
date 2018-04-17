package project.backend;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import project.gui_components.*;

import java.util.Random;

import static project.backend.ImageDisplayMode.GALLERY;
import static project.backend.ImageDisplayMode.MAXIMIZED;

enum ImageDisplayMode {GALLERY, MAXIMIZED}

public class Main extends Application {
    private static final BorderPane mainBorderPane = new BorderPane();
    private static final Scene mainScene = new Scene(mainBorderPane);
    private static final TopPane topPane = new TopPane();
    private static final LeftPane leftPane = new LeftPane();
    private static final RightPane rightPane = new RightPane();
    private static final GalleryPane galleryPane = new GalleryPane();
    private static final PreviewPane previewPane = new PreviewPane();
    private static final InnerShadow galleryTileHighlightEffect = new InnerShadow();
    private static ImageDisplayMode imageDisplayMode = GALLERY;

    @Override
    public void start(Stage primaryStage) {
        new DatabaseBuilder();
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    Database.clearSelection();
                    Database.addIndexToSelection(new Random().nextInt(Database.getItemDatabaseFiltered().size()));
                    break;
                case F12:
                    swapImageDisplayMode();
                    break;
                default:
                    break;
            }
        });
        primaryStage.setScene(mainScene);
        mainBorderPane.setTop(topPane);
        mainBorderPane.setLeft(leftPane);
        mainBorderPane.setCenter(galleryPane);
        mainBorderPane.setRight(rightPane);

        primaryStage.show();
    }

    public static void main(String[] args) {
        initializeVariables();
        launch(args);
    }

    private static void initializeVariables() {
        galleryTileHighlightEffect.setColor(Color.RED);
        galleryTileHighlightEffect.setOffsetX(0);
        galleryTileHighlightEffect.setOffsetY(0);
        galleryTileHighlightEffect.setWidth(5);
        galleryTileHighlightEffect.setHeight(5);
        galleryTileHighlightEffect.setChoke(1);
    }

    public static void selectionChanged() {
        for (Node node : galleryPane.getTilePane().getChildren()) {
            if (node.getEffect() != null)
                node.setEffect(null);
        }
        if (leftPane.getListView().isFocused()) {
            leftPane.getListView().getSelectionModel().clearSelection();
            for (int index : Database.getSelectedIndexes()) {
                leftPane.getListView().getSelectionModel().select(index);
                galleryPane.getTilePane().getChildren().get(index).setEffect(galleryTileHighlightEffect);
            }
        } else
            for (int index : Database.getSelectedIndexes())
                galleryPane.getTilePane().getChildren().get(index).setEffect(galleryTileHighlightEffect);

        if (imageDisplayMode == MAXIMIZED)
            previewPane.drawPreview();
    }

    private static void swapImageDisplayMode() {
        if (imageDisplayMode == GALLERY) {
            imageDisplayMode = MAXIMIZED;
            previewPane.setCanvasSize(galleryPane.getWidth(), galleryPane.getHeight());
            mainBorderPane.setCenter(previewPane);
            previewPane.drawPreview();
        } else {
            Main.imageDisplayMode = GALLERY;
            mainBorderPane.setCenter(galleryPane);
        }
    }

    public static GalleryPane getGalleryPane() {
        return galleryPane;
    }

    public static LeftPane getLeftPane() {
        return leftPane;
    }

    public static RightPane getRightPane() {
        return rightPane;
    }

    public static TopPane getTopPane() {
        return topPane;
    }
}
