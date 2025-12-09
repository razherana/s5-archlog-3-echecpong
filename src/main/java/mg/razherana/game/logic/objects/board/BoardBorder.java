package mg.razherana.game.logic.objects.board;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

/**
 * Represents the border of the game board.
 * Used for collision detection and boundary management.
 */
public class BoardBorder extends GameObject {

  public static final float BORDER_THICKNESS = 3f;
  private Board board;

  public BoardBorder(Game game, Board board) {
    super(game, 1);
    this.board = board;
    setSize(board.getSize().add(new Vector2(BORDER_THICKNESS, BORDER_THICKNESS)));
  }

  @Override
  public void update(double deltaTime) {
  }

  @Override
  public void render(Graphics2D g2d) {
    // Draw a simple red border for debugging purposes
    g2d.setColor(java.awt.Color.RED);
    g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
    g2d.drawRect(0 - (int) BORDER_THICKNESS, 0 - (int) BORDER_THICKNESS, (int) getSize().x, (int) getSize().y);
  }

  @Override
  public boolean isCollidingWith(GameObject other) {
    Rectangle2D.Float[] borders = getBorders();
    for (Rectangle2D.Float border : borders) {
      if (border.intersects(other.getDefaultBounds())) {
        return true;
      }
    }
    return false;
  }

  private Rectangle2D.Float[] getBorders() {
    return new Rectangle2D.Float[] {
        // Top border
        new Rectangle2D.Float(0, 0, board.getSize().x, BORDER_THICKNESS),
        // Bottom border
        new Rectangle2D.Float(0, board.getSize().y - BORDER_THICKNESS, board.getSize().x, BORDER_THICKNESS),
        // Left border
        new Rectangle2D.Float(0, 0, BORDER_THICKNESS, board.getSize().y),
        // Right border
        new Rectangle2D.Float(board.getSize().x - BORDER_THICKNESS, 0, BORDER_THICKNESS, board.getSize().y)
    };
  }
}
