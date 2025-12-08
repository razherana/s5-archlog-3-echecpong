package mg.razherana.game.logic.objects.chesspiece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mg.razherana.game.logic.utils.Assets;

public enum ChessPieceType {
  PAWN_WHITE(new ChessPieceAsset(0, 0)),
  KNIGHT_WHITE(new ChessPieceAsset(1, 0)),
  BISHOP_WHITE(new ChessPieceAsset(3, 0)),
  ROOK_WHITE(new ChessPieceAsset(2, 0)),
  QUEEN_WHITE(new ChessPieceAsset(4, 0)),
  KING_WHITE(new ChessPieceAsset(5, 0)),

  PAWN_BLACK(new ChessPieceAsset(0, 1)),
  KNIGHT_BLACK(new ChessPieceAsset(1, 1)),
  BISHOP_BLACK(new ChessPieceAsset(3, 1)),
  ROOK_BLACK(new ChessPieceAsset(2, 1)),
  QUEEN_BLACK(new ChessPieceAsset(4, 1)),
  KING_BLACK(new ChessPieceAsset(5, 1));

  private final ChessPieceAsset asset;

  /**
   * @return the asset
   */
  public ChessPieceAsset getAsset() {
    return asset;
  }

  public void draw(int x, int y, int w, int h, Graphics2D g2d, BufferedImage spriteSheet) {
    Assets.drawChessImageTile(asset.ximg(), asset.yimg(), x, y, w, h, g2d, spriteSheet);
  }

  ChessPieceType(ChessPieceAsset asset) {
    this.asset = asset;
  }
}
