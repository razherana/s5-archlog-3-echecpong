package mg.razherana.game.logic.animations;

import java.awt.image.BufferedImage;
import java.util.Map;

import mg.razherana.game.logic.GameObject;

public class MutlipleStateAnimation<T extends GameObject> extends Animation {
  private final Map<AnimationState<T>, AnimationAsset[]> mapAnimationAsset;
  private final T gameObject;

  private AnimationState<T> currentAnimationState = null;

  /**
   * @param animationFrameDuration
   * @param animationSpriteSheet
   * @param mapAnimationAssets
   * @param tileSize
   */
  public MutlipleStateAnimation(T gameObject, float animationFrameDuration, BufferedImage animationSpriteSheet,
      Map<AnimationState<T>, AnimationAsset[]> mapAnimationAssets,
      int tileSize) {
    super(animationFrameDuration, animationSpriteSheet, null, tileSize);
    this.mapAnimationAsset = mapAnimationAssets;
    this.gameObject = gameObject;
  }

  @Override
  public void update(float deltaTime) {
    // Do normal update
    super.update(deltaTime);

    // Then update the state
    currentAnimationState.change(gameObject);
  }

  @Override
  public AnimationAsset getCurrentAnimationAsset() {
    var animationState = mapAnimationAsset.get(currentAnimationState);
    if (animationState == null) {
      throw new IllegalStateException("Animation state is null!!! Current = " + currentAnimationState);
    }

    return animationState[currentAnimationFrame];
  }
}
