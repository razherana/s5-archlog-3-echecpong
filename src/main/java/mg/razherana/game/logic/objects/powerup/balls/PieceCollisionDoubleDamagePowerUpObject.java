package mg.razherana.game.logic.objects.powerup.balls;

import java.awt.Graphics2D;

import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.objects.ball.Ball;

public class PieceCollisionDoubleDamagePowerUpObject extends GameObject {

  private Ball ball;

  private static final float DURATION = 10f;

  public PieceCollisionDoubleDamagePowerUpObject(Ball ball) {
    super(ball.getGame(), 0);
    this.ball = ball;

    final float originalDamage = ball.getDamage();

    ball.setDamage(originalDamage * 2);

    addOnceTimer((thing) -> {
      getGame().removeGameObject(this);
      ball.setDamage(originalDamage);
    }, DURATION);
  }

  @Override
  public void render(Graphics2D g2d) {
    // Do nothing
  }

  @Override
  public void update(double deltaTime) {
    // Do nothing
  }

  /**
   * @return the ball
   */
  public Ball getBall() {
    return ball;
  }

  /**
   * @param ball the ball to set
   */
  public void setBall(Ball ball) {
    this.ball = ball;
  }
}
