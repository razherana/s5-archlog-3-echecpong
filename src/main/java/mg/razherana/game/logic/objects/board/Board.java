package mg.razherana.game.logic.objects.board;

import java.awt.Color;
import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.utils.Vector2;

public class Board extends GameObject {
  private final int width;
  private final int height;
  private final int col;
  private final int row;

  static final int TILE = ChessPiece.TILE;
  
  public static final int DEFAULT_WIDTH = 8 * TILE;
  public static final int DEFAULT_HEIGHT = 8 * TILE;

  public Board(int col, int row, Game game) {
    super(game, new Vector2(0, 0), 0);
    this.width = col * TILE;
    this.height = row * TILE;
    this.col = col;
    this.row = row;

    setSize(new Vector2(width, height));
  }

  @Override
  public void update(double deltaTime) {
    // Update board logic if necessary
  }

  @Override
  public void render(Graphics2D g2d) {
    // Render the board
    g2d.setColor(Color.LIGHT_GRAY);
    g2d.fillRect(0, 0, (int) getSize().x, (int) getSize().y);

    // Draw the grid
    for (int i = 0, c = 0; i < col; i++) {
      for (int j = 0; j < row; j++, c++) {
        if (c % 2 == 0) {
          g2d.setColor(new Color(0xeae9d2));
          g2d.fillRect(i * TILE, j * TILE, TILE, TILE);
        } else {
          g2d.setColor(new Color(0x4b7399));
          g2d.fillRect(i * TILE, j * TILE, TILE, TILE);
        }
      }
      c++;
    }
    // Additional rendering logic can be added here
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
