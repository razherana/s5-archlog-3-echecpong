package mg.razherana.game.logic.objects.ball;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mg.razherana.game.logic.utils.Assets;

enum BallMovementType {
  LEFT_TO_RIGHT(new BallAsset[] {
      new BallAsset(0, 0),
      new BallAsset(1, 0),
      new BallAsset(2, 0),
      new BallAsset(3, 0),
      new BallAsset(4, 0),
      new BallAsset(5, 0),
      new BallAsset(6, 0),
      new BallAsset(7, 0),
  }),
  RIGHT_TO_LEFT(new BallAsset[] {
      new BallAsset(0, 1),
      new BallAsset(1, 1),
      new BallAsset(2, 1),
      new BallAsset(3, 1),
      new BallAsset(4, 1),
      new BallAsset(5, 1),
      new BallAsset(6, 1),
      new BallAsset(7, 1),
  }),
  TOP_TO_BOTTOM(new BallAsset[] {
      new BallAsset(0, 2),
      new BallAsset(1, 2),
      new BallAsset(2, 2),
      new BallAsset(3, 2),
      new BallAsset(4, 2),
      new BallAsset(5, 2),
      new BallAsset(6, 2),
      new BallAsset(7, 2),
  }),
  BOTTOM_TO_TOP(new BallAsset[] {
      new BallAsset(0, 3),
      new BallAsset(1, 3),
      new BallAsset(2, 3),
      new BallAsset(3, 3),
      new BallAsset(4, 3),
      new BallAsset(5, 3),
      new BallAsset(6, 3),
      new BallAsset(7, 3),
  }),
  DIAGONAL_TOPLEFT_TO_BOTTOMRIGHT(new BallAsset[] {
      new BallAsset(0, 4),
      new BallAsset(1, 4),
      new BallAsset(2, 4),
      new BallAsset(3, 4),
      new BallAsset(4, 4),
      new BallAsset(5, 4),
      new BallAsset(6, 4),
      new BallAsset(7, 4),
  }),
  DIAGONAL_TOPRIGHT_TO_BOTTOMLEFT(new BallAsset[] {
      new BallAsset(0, 5),
      new BallAsset(1, 5),
      new BallAsset(2, 5),
      new BallAsset(3, 5),
      new BallAsset(4, 5),
      new BallAsset(5, 5),
      new BallAsset(6, 5),
      new BallAsset(7, 5),
  }),
  DIAGONAL_BOTTOMLEFT_TO_TOPRIGHT(new BallAsset[] {
      new BallAsset(0, 6),
      new BallAsset(1, 6),
      new BallAsset(2, 6),
      new BallAsset(3, 6),
      new BallAsset(4, 6),
      new BallAsset(5, 6),
      new BallAsset(6, 6),
      new BallAsset(7, 6),
  }),
  DIAGONAL_BOTTOMRIGHT_TO_TOPLEFT(new BallAsset[] {
      new BallAsset(0, 7),
      new BallAsset(1, 7),
      new BallAsset(2, 7),
      new BallAsset(3, 7),
      new BallAsset(4, 7),
      new BallAsset(5, 7),
      new BallAsset(6, 7),
      new BallAsset(7, 7),
  }),
  IDLE(new BallAsset[] {
      new BallAsset(0, 0)
  });

  public static final int FRAME_COUNT = 8;
  public static final int TILE_SIZE = 128;

  private BallAsset[] assets;

  BallMovementType(BallAsset[] assets) {
    this.assets = assets;
  }

  public BallAsset[] getAssets() {
    return assets;
  }

  public void draw(int x, int y, int w, int h, int frame, Graphics2D g2d, BufferedImage spriteSheet) {
    BallAsset asset = assets[frame % assets.length];
    Assets.drawAnimationImageTile(TILE_SIZE, asset.ximg(), asset.yimg(), x, y, w, h, g2d, spriteSheet, 
        20, 20, -40, -40
    );
  }
}
