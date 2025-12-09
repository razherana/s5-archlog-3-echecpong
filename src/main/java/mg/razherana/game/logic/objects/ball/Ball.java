package mg.razherana.game.logic.objects.ball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.objects.board.BoardBorder;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.utils.Vector2;
import mg.razherana.game.logic.utils.Collision;
import mg.razherana.game.logic.utils.Collision.CollisionSidesResult;

public class Ball extends GameObject {
  public static final int RADIUS = 25;
  public static final float DIAMETER = RADIUS * 2;

  Vector2 velocity = new Vector2(0, 0);
  float damage = 1f;

  public Ball(Game game, Vector2 position, float damage, float speed) {
    super(game, position, 2);
    this.damage = damage;
    this.velocity = new Vector2(speed * 1.5f, speed);

    setSize(new Vector2(RADIUS * 2, RADIUS * 2));
  }

  @Override
  public void update(double deltaTime) {
    // Update ball position based on velocity
    Vector2 newPosition = getPosition().add(velocity.multiply((float) deltaTime));

    setPosition(newPosition);
  }

  @Override
  public void render(Graphics2D g2d) {
    Ellipse2D.Float circle = new Ellipse2D.Float(getPosition().x, getPosition().y, RADIUS * 2, RADIUS * 2);
    g2d.setColor(java.awt.Color.LIGHT_GRAY);

    g2d.fill(circle);

    g2d.setColor(Color.DARK_GRAY);

    g2d.draw(circle);
  }

  @Override
  public void onCollision(GameObject other) {
    // Handle collision with other game objects

    // Check if the other object is a BoardBorder
    if (other instanceof BoardBorder) {
      // Get the collision sides
      System.out.println("[Collision/Border] Colliding with " + other);

      CollisionSidesResult sides = Collision.collideSides(
          new Rectangle2D.Float(getPosition().x, getPosition().y, getSize().x, getSize().y),
          velocity.x, velocity.y,
          new Rectangle2D.Float(other.getPosition().x, other.getPosition().y, other.getSize().x, other.getSize().y),
          0, 0);

      // Reflect velocity based on collision sides
      if (!sides.collided())
        return;

      // Correct position
      setPosition(new Vector2(sides.correctedX(), sides.correctedY()));

      if (sides.top() || sides.bottom()) {
        velocity.y = -velocity.y;
      }

      if (sides.left() || sides.right()) {
        velocity.x = -velocity.x;
      }
    }

    // Check if the other object is a ChessPiece
    if (other instanceof ChessPiece chessPiece) {
      // Inflict damage to the chess piece
      chessPiece.takeDamage(damage);

      // Reflect velocity based on collision sides
      CollisionSidesResult sides = Collision.collideSides(
          new Rectangle2D.Float(getPosition().x, getPosition().y, getSize().x, getSize().y),
          velocity.x, velocity.y,
          new Rectangle2D.Float(other.getPosition().x, other.getPosition().y, other.getSize().x, other.getSize().y),
          0, 0);

      if (!sides.collided())
        return;

      // Correct position
      setPosition(new Vector2(sides.correctedX(), sides.correctedY()));

      if (sides.top() || sides.bottom()) {
        velocity.y = -velocity.y;
      }

      if (sides.left() || sides.right()) {
        velocity.x = -velocity.x;
      }
    }
  }

}
