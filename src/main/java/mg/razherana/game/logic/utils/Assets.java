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

  public static void drawAnimationImageTile(int tilesize,
      int ximg, int yimg,
      int x, int y, int w, int h,
      Graphics2D g2d,
      BufferedImage spriteSheet) {
    drawAnimationImageTile(tilesize, ximg, yimg, x, y, w, h, g2d, spriteSheet, 0, 0, 0, 0);
  }

  public static void drawAnimationImageTile(int tilesize,
      int ximg, int yimg,
      int x, int y, int w, int h,
      Graphics2D g2d,
      BufferedImage spriteSheet,
      int marginX1, int marginY1, int marginX2, int marginY2) {
    int sx1 = ximg * tilesize + marginX1;
    int sy1 = yimg * tilesize + marginY1;
    int sx2 = sx1 + tilesize + marginX2;
    int sy2 = sy1 + tilesize + marginY2;

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
  private BufferedImage ballSpriteSheet = null;

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

  /**
   * @return the ballSpriteSheet
   */
  public BufferedImage getBallSpriteSheet() {
    return ballSpriteSheet;
  }

  /**
   * @param ballSpriteSheet the ballSpriteSheet to set
   */
  public void setBallSpriteSheet(BufferedImage ballSpriteSheet) {
    this.ballSpriteSheet = ballSpriteSheet;
  }
}
