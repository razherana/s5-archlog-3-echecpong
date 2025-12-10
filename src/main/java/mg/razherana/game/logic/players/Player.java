package mg.razherana.game.logic.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.objects.platform.Platform;

public class Player {
  private String name;
  private List<ChessPiece> chessPieces = new ArrayList<>();
  private Color primaryColor;
  private Color secondaryColor;
  private Platform platform;
  private int whiteOrBlack;

  /**
   * @param name
   * @param chessPieces the list of chess pieces belonging to the player
   */
  public Player(String name, Color primaryColor, Color secondaryColor, List<ChessPiece> chessPieces) {
    this.name = name;
    this.chessPieces = chessPieces;
    this.primaryColor = primaryColor;
    this.secondaryColor = secondaryColor;
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

  /**
   * @return the color
   */
  public Color getPrimaryColor() {
    return primaryColor;
  }

  /**
   * @param color the color to set
   */
  public void setPrimaryColor(Color color) {
    this.primaryColor = color;
  }

  /**
   * @return the secondaryColor
   */
  public Color getSecondaryColor() {
    return secondaryColor;
  }

  /**
   * @param secondaryColor the secondaryColor to set
   */
  public void setSecondaryColor(Color secondaryColor) {
    this.secondaryColor = secondaryColor;
  }

  /**
   * @return the platform
   */
  public Platform getPlatform() {
    return platform;
  }

  /**
   * @param platform the platform to set
   */
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  /**
   * @return the whiteOrBlack
   */
  public int getWhiteOrBlack() {
    return whiteOrBlack;
  }

  /**
   * @param whiteOrBlack the whiteOrBlack to set
   */
  public void setWhiteOrBlack(int whiteOrBlack) {
    this.whiteOrBlack = whiteOrBlack;
  }

}
