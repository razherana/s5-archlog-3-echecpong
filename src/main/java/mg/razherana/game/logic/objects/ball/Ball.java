package mg.razherana.game.logic.objects.ball;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.objects.board.BoardBorder;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.objects.platform.Platform;
import mg.razherana.game.logic.utils.Vector2;
import mg.razherana.game.logic.utils.Collision;
import mg.razherana.game.logic.utils.Collision.CollisionSidesResult;

public class Ball extends GameObject {
  public static final int RADIUS = 25;
  public static final float DIAMETER = RADIUS * 2;

  Vector2 velocity = new Vector2(0, 0);
  Vector2 baseVelocity;
  float damage = 1f;

  // Animation details
  BallMovementType movementType = BallMovementType.IDLE;
  int animationFrame = 0;
  float animationTimer = 0f;

  static final float ANIMATION_FRAME_DURATION = 0.05f; // Duration of each frame in seconds

  public Ball(Game game, Vector2 position, float damage, float speedX, float speedY) {
    super(game, position, 2);
    this.damage = damage;
    this.velocity = new Vector2(speedX, speedY);
    this.baseVelocity = new Vector2(speedX, speedY);

    setSize(new Vector2(RADIUS * 2, RADIUS * 2));
  }

  @Override
  public void update(double deltaTime) {
    // Update ball position based on velocity
    Vector2 newPosition = getPosition().add(velocity.multiply((float) deltaTime));

    setPosition(newPosition);

    // Determine movement type based on velocity
    if (Math.abs(velocity.x) > Math.abs(velocity.y)) {
      if (velocity.x < 0)
        movementType = BallMovementType.RIGHT_TO_LEFT;
      else
        movementType = BallMovementType.LEFT_TO_RIGHT;
    } else if (Math.abs(velocity.y) > Math.abs(velocity.x)) {
      if (velocity.y < 0)
        movementType = BallMovementType.BOTTOM_TO_TOP;
      else
        movementType = BallMovementType.TOP_TO_BOTTOM;
    } else if (velocity.x < 0 && velocity.y < 0) {
      movementType = BallMovementType.DIAGONAL_TOPLEFT_TO_BOTTOMRIGHT;
    } else if (velocity.x > 0 && velocity.y < 0) {
      movementType = BallMovementType.DIAGONAL_TOPRIGHT_TO_BOTTOMLEFT;
    } else if (velocity.x < 0 && velocity.y > 0) {
      movementType = BallMovementType.DIAGONAL_BOTTOMLEFT_TO_TOPRIGHT;
    } else if (velocity.x > 0 && velocity.y > 0) {
      movementType = BallMovementType.DIAGONAL_BOTTOMRIGHT_TO_TOPLEFT;
    } else {
      movementType = BallMovementType.IDLE;
    }

    // Update animation
    animationTimer += deltaTime;
    if (animationTimer >= ANIMATION_FRAME_DURATION) {
      animationFrame = (animationFrame + 1) % BallMovementType.FRAME_COUNT;
      animationTimer = 0f;
    }
  }

  private Ellipse2D.Float rest = null;

  @Override
  public void render(Graphics2D g2d) {
    // Draw the ball using the appropriate animation frame
    movementType.draw(
        (int) getPosition().x,
        (int) getPosition().y,
        (int) getSize().x,
        (int) getSize().y,
        animationFrame,
        g2d,
        getGame().getAssets().getBallSpriteSheet());

    // Debug: draw collision bounds
    // g2d.setColor(new Color(255, 0, 0, 100));
    // g2d.draw(new Ellipse2D.Float(getPosition().x, getPosition().y, getSize().x,
    // getSize().y));

    // Debug: draw old and new position
    if (rest != null) {
      g2d.setColor(java.awt.Color.BLUE);
      g2d.fill(rest);
    }
  }

  @Override
  public void onCollision(GameObject other) {
    super.onCollision(other);

    var thisBounds = getDefaultBounds();

    // Check if the other object is a BoardBorder
    if (other instanceof BoardBorder) {
      // Get the collision sides

      Rectangle2D.Float[] borders = ((BoardBorder) other).getBorders();

      // Check which border we collided with
      Rectangle2D.Float borderCollided = null;
      for (Rectangle2D.Float border : borders) {
        if (border.intersects(thisBounds)) {
          borderCollided = border;
          break;
        }
      }

      if (borderCollided == null)
        return;

      CollisionSidesResult sides = Collision.collideSides(
          thisBounds,
          velocity.x, velocity.y,
          borderCollided,
          0, 0);

      // Reflect velocity based on collision sides
      if (!sides.collided())
        return;

      // rest = new Ellipse2D.Float(getPosition().x, getPosition().y, getSize().x,
      // getSize().y);

      // Correct position
      setPosition(new Vector2(sides.correctedX(), sides.correctedY()));

      if (sides.top() || sides.bottom()) {
        velocity.y = -velocity.y;
        baseVelocity.y = -baseVelocity.y;
      }

      if (sides.left() || sides.right()) {
        velocity.x = -velocity.x;
        baseVelocity.x = -baseVelocity.x;
      }
    }

    // Check if the other object is a ChessPiece
    else if (other instanceof ChessPiece chessPiece) {
      // Inflict damage to the chess piece
      chessPiece.takeDamage(damage);

      // Reflect velocity based on collision sides
      CollisionSidesResult sides = Collision.collideSides(
          thisBounds,
          velocity.x, velocity.y,
          new Rectangle2D.Float(other.getPosition().x, other.getPosition().y, other.getSize().x, other.getSize().y),
          0, 0);

      if (!sides.collided())
        return;

      // Correct position
      setPosition(new Vector2(sides.correctedX(), sides.correctedY()));

      if (sides.top() || sides.bottom()) {
        velocity.y = -velocity.y;
        baseVelocity.y = -baseVelocity.y;
      }

      if (sides.left() || sides.right()) {
        velocity.x = -velocity.x;
        baseVelocity.x = -baseVelocity.x;
      }
    }

    // Check if platform
    else if (other instanceof Platform) {
      // Reflect velocity based on collision sides
      CollisionSidesResult sides = Collision.collideSides(
          thisBounds,
          velocity.x, velocity.y,
          new Rectangle2D.Float(other.getPosition().x, other.getPosition().y, other.getSize().x, other.getSize().y),
          0, 0);

      if (!sides.collided())
        return;

      // Correct position
      setPosition(new Vector2(sides.correctedX(), sides.correctedY()));

      if (sides.top() || sides.bottom()) {
        velocity.y = -velocity.y;
        baseVelocity.y = -baseVelocity.y;
      }

      if (sides.left() || sides.right()) {
        velocity.x = -velocity.x;
        baseVelocity.x = -baseVelocity.x;
      }

      // Add random to make the ball less predictable
      float randomFactorX = (float) (0.3 + Math.random()); // Random value between 0.8 and 1.8
      float randomFactorY = (float) (0.3 + Math.random()); // Random value between 0.8 and 1.8

      System.out.println("[Ball/Collision/RandomFactor] Random factors: " + randomFactorX + ", " + randomFactorY);

      velocity.x = baseVelocity.x * randomFactorX;
      velocity.y = baseVelocity.y * randomFactorY;
    }
  }

}
