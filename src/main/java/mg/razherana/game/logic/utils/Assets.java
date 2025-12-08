package mg.razherana.game.logic.utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Assets {

  static final int chessTileSize = 16;

  public static BufferedImage loadImage(String path) {
    try {
      return ImageIO.read(
          Assets.class.getResourceAsStream(path));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load image: " + path, e);
    }
  }

  public static void drawChessImageTile(int imgx, int imgy, int x, int y, int w, int h, Graphics2D g2d,
      BufferedImage spriteSheet) {
    int sx1 = imgx * chessTileSize;
    int sy1 = imgy * chessTileSize;
    int sx2 = sx1 + chessTileSize;
    int sy2 = sy1 + chessTileSize;

    int dx1 = x;
    int dy1 = y;
    int dx2 = x + w;
    int dy2 = y + h;

    g2d.drawImage(
        spriteSheet,
        dx1, dy1, dx2, dy2,
        sx1, sy1, sx2, sy2,
        null);
  }

  private BufferedImage chessPieceSpriteSheet = null;

  /**
   * @return the chessPieceSpriteSheet
   */
  public BufferedImage getChessPieceSpriteSheet() {
    return chessPieceSpriteSheet;
  }

  /**
   * @param chessPieceSpriteSheet the chessPieceSpriteSheet to set
   */
  public void setChessPieceSpriteSheet(BufferedImage chessPieceSpriteSheet) {
    this.chessPieceSpriteSheet = chessPieceSpriteSheet;
  }
}
