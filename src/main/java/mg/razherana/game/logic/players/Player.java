package mg.razherana.game.logic.players;

import java.util.ArrayList;
import java.util.List;

import mg.razherana.game.logic.objects.chesspiece.ChessPiece;

public class Player {
  private String name;
  private List<ChessPiece> chessPieces = new ArrayList<>();

  /**
   * @param name
   * @param chessPieces the list of chess pieces belonging to the player
   */
  public Player(String name, List<ChessPiece> chessPieces) {
    this.name = name;
    this.chessPieces = chessPieces;
  }

  /**
   * @return the name of the player
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the chessPieces
   */
  public List<ChessPiece> getChessPieces() {
    return chessPieces;
  }

  /**
   * @param chessPieces the chessPieces to set
   */
  public void setChessPieces(List<ChessPiece> chessPieces) {
    this.chessPieces = chessPieces;
  }

}
