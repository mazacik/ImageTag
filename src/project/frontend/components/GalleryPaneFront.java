package project.frontend.components;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import project.backend.shared.Backend;
import project.backend.shared.Main;
import project.frontend.shared.RightClickContextMenu;

public class GalleryPaneFront extends ScrollPane {
  private final TilePane tilePane = new TilePane();
  private final InnerShadow highlightEffect = new InnerShadow();

  public GalleryPaneFront() {
    tilePane.setVgap(1);
    tilePane.setHgap(1);
    tilePane.setPrefTileWidth(Main.GALLERY_ICON_SIZE);
    tilePane.setPrefTileHeight(Main.GALLERY_ICON_SIZE);

    highlightEffect.setColor(Color.RED);
    highlightEffect.setOffsetX(0);
    highlightEffect.setOffsetY(0);
    highlightEffect.setWidth(5);
    highlightEffect.setHeight(5);
    highlightEffect.setChoke(1);

    setContextMenu(new RightClickContextMenu());
    setOnContextMenuRequested(
        event -> getContextMenu().show(this, event.getScreenX(), event.getScreenY()));

    setHbarPolicy(ScrollBarPolicy.NEVER);
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    setFitToWidth(true);
    setContent(tilePane);
  }

  public TilePane getTilePane() {
    return tilePane;
  }

  public InnerShadow getHighlightEffect() {
    return highlightEffect;
  }
}
