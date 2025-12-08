package mg.razherana.game.logic.objects.chesspiece;

import java.awt.Graphics2D;
import java.util.List;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.players.Player;
import mg.razherana.game.logic.utils.Vector2;

public class ChessPiece extends GameObject {
  public static final int TILE = 80;

  public static List<ChessPiece> initDefaultPieces(Game game, Player player, int whiteOrBlack) {
    if (whiteOrBlack == 1) {
      return List.of(
          new ChessPiece(game, new Vector2(0 * TILE, 7 * TILE), player, ChessPieceType.ROOK_WHITE),
          new ChessPiece(game, new Vector2(1 * TILE, 7 * TILE), player, ChessPieceType.KNIGHT_WHITE),
          new ChessPiece(game, new Vector2(2 * TILE, 7 * TILE), player, ChessPieceType.BISHOP_WHITE),
          new ChessPiece(game, new Vector2(3 * TILE, 7 * TILE), player, ChessPieceType.QUEEN_WHITE),
          new ChessPiece(game, new Vector2(4 * TILE, 7 * TILE), player, ChessPieceType.KING_WHITE),
          new ChessPiece(game, new Vector2(5 * TILE, 7 * TILE), player, ChessPieceType.BISHOP_WHITE),
          new ChessPiece(game, new Vector2(6 * TILE, 7 * TILE), player, ChessPieceType.KNIGHT_WHITE),
          new ChessPiece(game, new Vector2(7 * TILE, 7 * TILE), player, ChessPieceType.ROOK_WHITE),
          new ChessPiece(game, new Vector2(0 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(1 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(2 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(3 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(4 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(5 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(6 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE),
          new ChessPiece(game, new Vector2(7 * TILE, 6 * TILE), player, ChessPieceType.PAWN_WHITE));
    } else {
      return List.of(
          new ChessPiece(game, new Vector2(0 * TILE, 0 * TILE), player, ChessPieceType.ROOK_BLACK),
          new ChessPiece(game, new Vector2(1 * TILE, 0 * TILE), player, ChessPieceType.KNIGHT_BLACK),
          new ChessPiece(game, new Vector2(2 * TILE, 0 * TILE), player, ChessPieceType.BISHOP_BLACK),
          new ChessPiece(game, new Vector2(3 * TILE, 0 * TILE), player, ChessPieceType.QUEEN_BLACK),
          new ChessPiece(game, new Vector2(4 * TILE, 0 * TILE), player, ChessPieceType.KING_BLACK),
          new ChessPiece(game, new Vector2(5 * TILE, 0 * TILE), player, ChessPieceType.BISHOP_BLACK),
          new ChessPiece(game, new Vector2(6 * TILE, 0 * TILE), player, ChessPieceType.KNIGHT_BLACK),
          new ChessPiece(game, new Vector2(7 * TILE, 0 * TILE), player, ChessPieceType.ROOK_BLACK),
          new ChessPiece(game, new Vector2(0 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(1 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(2 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(3 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(4 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(5 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(6 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK),
          new ChessPiece(game, new Vector2(7 * TILE, 1 * TILE), player, ChessPieceType.PAWN_BLACK));
    }
  }

  private Player player;

  private ChessPieceType type;

  public ChessPiece(Game game, Vector2 position, Player player, ChessPieceType type) {
    super(game, position, 1);
    this.player = player;
    this.type = type;
  }

  @Override
  public void update(double deltaTime) {
    // Nothing to update here
  }

  @Override
  public void render(Graphics2D g2d) {
    // Draw the piece
    type.draw((int) getPosition().x, (int) getPosition().y, TILE, TILE, g2d,
        getGame().getAssets().getChessPieceSpriteSheet());
  }

  /**
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * @param player the player to set
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * @return the type
   */
  public ChessPieceType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(ChessPieceType type) {
    this.type = type;
  }
}
