package mg.razherana.game.logic.objects.powerup.balls;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.animations.Animation;
import mg.razherana.game.logic.objects.ball.Ball;
import mg.razherana.game.logic.objects.ball.BallMovementType;
import mg.razherana.game.logic.objects.powerup.PowerUpObject;
import mg.razherana.game.logic.utils.Vector2;

public class DoubleBallPowerUpObject extends PowerUpObject<Ball> {

  private static final float ANIMATION_FRAME_DURATION = 0.075f;
  private static final float DIAMETER_X = 40f;
  private static final float DIAMETER_Y = 40f;
  private static final float ALPHA = 0.7f;

  public DoubleBallPowerUpObject(Game game, Vector2 position) {
    super(game, position, new Vector2(DIAMETER_X, DIAMETER_Y), Ball.class);

    // Set the animation
    Animation animation = new Animation(
        ANIMATION_FRAME_DURATION,
        getGame().getAssets().getBallSpriteSheet(),
        BallMovementType.LEFT_TO_RIGHT.getAssets(),
        BallMovementType.TILE_SIZE);

    setAnimation(animation);
  }

  @Override
  public void render(Graphics2D g2d) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA));

    super.render(g2d);

    g2d.setColor(Color.BLACK);
    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 20));

    var strPosition = getPosition().add(getSize());

    g2d.drawString("x2", strPosition.x, strPosition.y);
  }

  @Override
  protected void applyPower(Ball appliedBall) {
    // Creates another ball beside the original ball
    float damage = appliedBall.getDamage();

    Ball ball = new Ball(getGame(),
        // Move the ball, top left of the original ball
        appliedBall.getPosition().add(new Vector2(-20, -20)),
        damage,
        appliedBall.getVelocity().x, appliedBall.getVelocity().y);

    ball.addOnceTimer(
        (Ball timedBall) -> getGame().removeGameObject(timedBall),
        5f);

    getGame().addGameObject(ball);

    getGame().removeGameObject(this);
  }

  @Override
  public String getMultiplayerAdditionalInformation() {
    return getAnimation().getCurrentAnimationFrame() + ";" + getAnimation().getCurrentAnimationTimer();
  }
}
