package mg.razherana.game.logic.objects.ball;

import java.util.Map;

import mg.razherana.game.logic.animations.AnimationAsset;
import mg.razherana.game.logic.animations.AnimationState;
import static java.lang.Math.abs;

public enum BallMovementType {
  LEFT_TO_RIGHT(new AnimationAsset[] {
      new AnimationAsset(0, 0),
      new AnimationAsset(1, 0),
      new AnimationAsset(2, 0),
      new AnimationAsset(3, 0),
      new AnimationAsset(4, 0),
      new AnimationAsset(5, 0),
      new AnimationAsset(6, 0),
      new AnimationAsset(7, 0),
  }, (ball) -> abs(ball.velocity.x) > abs(ball.velocity.y) && ball.velocity.x >= 0),
  RIGHT_TO_LEFT(new AnimationAsset[] {
      new AnimationAsset(0, 1),
      new AnimationAsset(1, 1),
      new AnimationAsset(2, 1),
      new AnimationAsset(3, 1),
      new AnimationAsset(4, 1),
      new AnimationAsset(5, 1),
      new AnimationAsset(6, 1),
      new AnimationAsset(7, 1),
  }, (ball) -> abs(ball.velocity.x) > abs(ball.velocity.y) && ball.velocity.x < 0),
  TOP_TO_BOTTOM(new AnimationAsset[] {
      new AnimationAsset(0, 2),
      new AnimationAsset(1, 2),
      new AnimationAsset(2, 2),
      new AnimationAsset(3, 2),
      new AnimationAsset(4, 2),
      new AnimationAsset(5, 2),
      new AnimationAsset(6, 2),
      new AnimationAsset(7, 2),
  }, (ball) -> abs(ball.velocity.y) > abs(ball.velocity.x) && ball.velocity.y >= 0),
  BOTTOM_TO_TOP(new AnimationAsset[] {
      new AnimationAsset(0, 3),
      new AnimationAsset(1, 3),
      new AnimationAsset(2, 3),
      new AnimationAsset(3, 3),
      new AnimationAsset(4, 3),
      new AnimationAsset(5, 3),
      new AnimationAsset(6, 3),
      new AnimationAsset(7, 3),
  }, (ball) -> abs(ball.velocity.y) > abs(ball.velocity.x) && ball.velocity.y < 0),
  DIAGONAL_TOPLEFT_TO_BOTTOMRIGHT(new AnimationAsset[] {
      new AnimationAsset(0, 4),
      new AnimationAsset(1, 4),
      new AnimationAsset(2, 4),
      new AnimationAsset(3, 4),
      new AnimationAsset(4, 4),
      new AnimationAsset(5, 4),
      new AnimationAsset(6, 4),
      new AnimationAsset(7, 4),
  }, (ball) -> ball.velocity.x < 0 && ball.velocity.y < 0),
  DIAGONAL_TOPRIGHT_TO_BOTTOMLEFT(new AnimationAsset[] {
      new AnimationAsset(0, 5),
      new AnimationAsset(1, 5),
      new AnimationAsset(2, 5),
      new AnimationAsset(3, 5),
      new AnimationAsset(4, 5),
      new AnimationAsset(5, 5),
      new AnimationAsset(6, 5),
      new AnimationAsset(7, 5),
  }, (ball) -> ball.velocity.x > 0 && ball.velocity.y < 0),
  DIAGONAL_BOTTOMLEFT_TO_TOPRIGHT(new AnimationAsset[] {
      new AnimationAsset(0, 6),
      new AnimationAsset(1, 6),
      new AnimationAsset(2, 6),
      new AnimationAsset(3, 6),
      new AnimationAsset(4, 6),
      new AnimationAsset(5, 6),
      new AnimationAsset(6, 6),
      new AnimationAsset(7, 6),
  }, (ball) -> ball.velocity.x < 0 && ball.velocity.y > 0),
  DIAGONAL_BOTTOMRIGHT_TO_TOPLEFT(new AnimationAsset[] {
      new AnimationAsset(0, 7),
      new AnimationAsset(1, 7),
      new AnimationAsset(2, 7),
      new AnimationAsset(3, 7),
      new AnimationAsset(4, 7),
      new AnimationAsset(5, 7),
      new AnimationAsset(6, 7),
      new AnimationAsset(7, 7),
  }, (ball) -> ball.velocity.x > 0 && ball.velocity.y > 0),
  IDLE(new AnimationAsset[] {
      new AnimationAsset(0, 0)
  }, (ball) -> ball.velocity.x == 0 && ball.velocity.y == 0);

  public static final int FRAME_COUNT = 8;
  public static final int TILE_SIZE = 128;

  private AnimationAsset[] assets;
  private AnimationState<Ball> state;

  BallMovementType(AnimationAsset[] assets, AnimationState<Ball> state) {
    this.assets = assets;
    this.state = state;
  }

  public AnimationAsset[] getAssets() {
    return assets;
  }

  public AnimationState<Ball> getState() {
    return state;
  }

  public Map.Entry<AnimationState<Ball>, AnimationAsset[]> getEntry() {
    AnimationAsset[] animationAssets = new AnimationAsset[assets.length];
    for (int i = 0; i < assets.length; i++)
      animationAssets[i] = new AnimationAsset(assets[i].imgx(), assets[i].imgy());

    return Map.entry(getState(), animationAssets);
  }
}
