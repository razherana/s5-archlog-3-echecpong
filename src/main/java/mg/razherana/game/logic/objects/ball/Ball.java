package mg.razherana.game.logic.objects.ball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.animations.Animation;
import mg.razherana.game.logic.animations.AnimationAsset;
import mg.razherana.game.logic.animations.AnimationState;
import mg.razherana.game.logic.animations.MutlipleStateAnimation;
import mg.razherana.game.logic.objects.board.BoardBorder;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.objects.platform.Platform;
import mg.razherana.game.logic.objects.powerup.balls.PieceCollisionDoubleDamagePowerUpObject;
import mg.razherana.game.logic.objects.ui.UIProgressFade;
import mg.razherana.game.logic.utils.Vector2;
import mg.razherana.game.logic.utils.Collision;
import mg.razherana.game.logic.utils.Collision.CollisionSidesResult;

public class Ball extends GameObject {
  // Og is 25
  public static final int RADIUS = 15;
  public static final float DIAMETER = RADIUS * 2;

  static final float ANIMATION_FRAME_DURATION = 0.075f; // Duration of each frame in seconds
  Vector2 velocity = new Vector2(0, 0);

  Vector2 baseVelocity;
  float damage = 1f;

  // Animation details
  BallMovementType movementType = BallMovementType.IDLE;

  private Ellipse2D.Float rest = null;
  private final float baseDamage;

  // Piece collision double damage power-up progress
  private float pieceDamagePowerUpProgress = 0f;

  private static final float PIECE_DAMAGE_POWERUP_MAX_PROGRESS = 100f;
  private static final float PIECE_DAMAGE_POWERUP_STEP_PROGRESS = 25f;

  // Constructor
  public Ball(Game game, Vector2 position, float damage, float speedX, float speedY) {
    super(game, position, 2);
    this.damage = damage;
    this.baseDamage = damage;
    this.velocity = new Vector2(speedX, speedY);
    this.baseVelocity = new Vector2(speedX, speedY);

    Map<AnimationState<Ball>, AnimationAsset[]> entryOfAnimations = Map.ofEntries(
        BallMovementType.BOTTOM_TO_TOP.getEntry(),
        BallMovementType.DIAGONAL_BOTTOMLEFT_TO_TOPRIGHT.getEntry(),
        BallMovementType.DIAGONAL_BOTTOMRIGHT_TO_TOPLEFT.getEntry(),
        BallMovementType.DIAGONAL_TOPLEFT_TO_BOTTOMRIGHT.getEntry(),
        BallMovementType.DIAGONAL_TOPRIGHT_TO_BOTTOMLEFT.getEntry(),
        BallMovementType.LEFT_TO_RIGHT.getEntry(),
        BallMovementType.RIGHT_TO_LEFT.getEntry(),
        BallMovementType.TOP_TO_BOTTOM.getEntry(),
        BallMovementType.IDLE.getEntry());

    setSize(new Vector2(RADIUS * 2, RADIUS * 2));

    Animation animation = new MutlipleStateAnimation<Ball>(
        this,
        ANIMATION_FRAME_DURATION,
        getGame().getAssets().getBallSpriteSheet(),
        entryOfAnimations,
        BallMovementType.TILE_SIZE,
        BallMovementType.IDLE.getState());

    animation.setMargins(new int[] {
        20, 20, -40, -40
    });

    setAnimation(animation);
  }

  /**
   * @return the baseDamage
   */
  public float getBaseDamage() {
    return baseDamage;
  }

  /**
   * @return the damage
   */
  public float getDamage() {
    return damage;
  }

  /**
   * @param damage the damage to set
   */
  public void setDamage(float damage) {
    this.damage = damage;
  }

  /**
   * @param velocity the velocity to set
   */
  public void setVelocity(Vector2 velocity) {
    this.velocity = velocity;
  }

  @Override
  public void update(double deltaTime) {
    updateAnimation(deltaTime);

    // Update ball position based on velocity
    Vector2 newPosition = getPosition().add(velocity.multiply((float) deltaTime));

    setPosition(newPosition);

    checkAndActivatePieceCollisionDoubleDamagePowerUp();
  }

  private void checkAndActivatePieceCollisionDoubleDamagePowerUp() {
    if (pieceDamagePowerUpProgress >= PIECE_DAMAGE_POWERUP_MAX_PROGRESS) {
      // Reset progress
      pieceDamagePowerUpProgress = 0f;

      // Activate the power-up
      PieceCollisionDoubleDamagePowerUpObject powerUp = new PieceCollisionDoubleDamagePowerUpObject(this);
      getGame().addGameObject(powerUp);

      System.out.println("[Ball/PowerUp] Activated Piece Collision Double Damage Power-Up!");
    }
  }

  @Override
  public void render(Graphics2D g2d) {
    // Draw the ball using the appropriate animation frame
    renderAnimation(g2d);

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

      // Add progress to piece damage power-up
      if (pieceDamagePowerUpProgress < PIECE_DAMAGE_POWERUP_MAX_PROGRESS) {
        pieceDamagePowerUpProgress += PIECE_DAMAGE_POWERUP_STEP_PROGRESS;
        System.out.println("[Ball/Collision] Piece damage power-up progress: " + pieceDamagePowerUpProgress);

        float max = Math.min(PIECE_DAMAGE_POWERUP_MAX_PROGRESS, pieceDamagePowerUpProgress);
        float percentage = (max / PIECE_DAMAGE_POWERUP_MAX_PROGRESS);
        System.out.println("[Ball/Collision] Piece damage power-up progress percentage: " + (percentage * 100f) + "%");

        // Show progress UI
        var progressPosition = getPosition().add(new Vector2(-10, -20));
        var progressUI = new UIProgressFade(getGame(), progressPosition, new Vector2(DIAMETER + 4, 5), .75f,
            percentage, Color.BLACK, Color.YELLOW);

        getGame().addGameObject(progressUI);

        checkAndActivatePieceCollisionDoubleDamagePowerUp();
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
      float[] random = getGame().getNextRandom();
      float randomFactorX = random[0];
      float randomFactorY = random[1];

      System.out.println("[Ball/Collision/RandomFactor] Random factors: " + randomFactorX + ", " + randomFactorY);

      velocity.x = baseVelocity.x * randomFactorX;
      velocity.y = baseVelocity.y * randomFactorY;
    }

    // // Testing the draw string
    // if(Math.random() < 0.1) {
    // System.out.println("[Ball/Collision] Ball position after collision: " +
    // getPosition().toString());
    // // Add UI
    // // getGame().addGameObject(new UIScreenTextScaleFade(getGame(), new
    // Vector2(200, 200), new Vector2(350, 150), 5, 0.7f, 1.3f, "Teste", 50));
    // }
  }

  public Vector2 getVelocity() {
    return velocity;
  }

  public Vector2 getBaseVelocity() {
    return baseVelocity;
  }

  public void setBaseVelocity(Vector2 baseVelocity) {
    this.baseVelocity = baseVelocity;
  }

}
