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

  static final float BORDER_THICKNESS = 3f;
  static final float BORDER_THICKNESS_FOR_COLLISION = 100f;
  private Board board;
  private Vector2 sizeRender;

  public BoardBorder(Game game, Board board) {
    super(game, 1);
    this.board = board;
    setSize(board.getSize().add(new Vector2(BORDER_THICKNESS_FOR_COLLISION, BORDER_THICKNESS_FOR_COLLISION)));
    sizeRender = board.getSize().add(new Vector2(BORDER_THICKNESS, BORDER_THICKNESS));
  }

  @Override
  public void update(double deltaTime) {
  }

  @Override
  public void render(Graphics2D g2d) {
    // Draw a simple red border for debugging purposes
    g2d.setColor(java.awt.Color.DARK_GRAY);
    g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
    g2d.drawRect(0 - (int) BORDER_THICKNESS, 0 - (int) BORDER_THICKNESS, (int) sizeRender.x, (int) sizeRender.y);

    // Debug collision borders
    for (Rectangle2D.Float border : getBorders()) {
      g2d.draw(border);
    }
  }

  @Override
  public boolean isCollidingWith(GameObject other) {
    var otherBounds = other.getDefaultBounds();

    Rectangle2D.Float[] borders = getBorders();

    for (Rectangle2D.Float border : borders) {
      if (border.intersects(otherBounds)) {
        return true;
      }
    }
    return false;
  }

  public Rectangle2D.Float[] getBorders() {
    return new Rectangle2D.Float[] {
        // Top border
        new Rectangle2D.Float(0, -BORDER_THICKNESS_FOR_COLLISION, board.getSize().x, BORDER_THICKNESS_FOR_COLLISION),
        // Bottom border
        new Rectangle2D.Float(0, board.getSize().y, board.getSize().x, BORDER_THICKNESS_FOR_COLLISION),
        // Left border
        new Rectangle2D.Float(-BORDER_THICKNESS_FOR_COLLISION, 0, BORDER_THICKNESS_FOR_COLLISION, board.getSize().y),
        // Right border
        new Rectangle2D.Float(board.getSize().x, 0, BORDER_THICKNESS_FOR_COLLISION, board.getSize().y)
    };
  }
}
