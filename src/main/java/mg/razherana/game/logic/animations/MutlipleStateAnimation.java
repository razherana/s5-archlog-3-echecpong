package mg.razherana.game.logic.animations;

import java.awt.image.BufferedImage;
import java.util.Map;

import mg.razherana.game.logic.GameObject;

public class MutlipleStateAnimation<T extends GameObject> extends Animation {
  private final Map<AnimationState<T>, AnimationAsset[]> mapAnimationAsset;
  private final T gameObject;

  private AnimationState<T> currentAnimationState = null;
  private final AnimationState<T> fallbackAnimationState;

  /**
   * @param animationFrameDuration
   * @param animationSpriteSheet
   * @param mapAnimationAssets
   * @param tileSize
   */
  public MutlipleStateAnimation(T gameObject, float animationFrameDuration, BufferedImage animationSpriteSheet,
      Map<AnimationState<T>, AnimationAsset[]> mapAnimationAssets,
      int tileSize,
      AnimationState<T> fallbackAnimationState  
    ) {
    super(animationFrameDuration, animationSpriteSheet, null, tileSize);
    this.mapAnimationAsset = mapAnimationAssets;
    this.gameObject = gameObject;
    this.fallbackAnimationState = fallbackAnimationState;
  }

  private AnimationAsset[] getCurrentAnimationAssets() {
    if(currentAnimationState == null)
      currentAnimationState = fallbackAnimationState;

    var animationAssets = mapAnimationAsset.get(currentAnimationState);
    if (animationAssets == null) {
      throw new IllegalStateException("Animation state is null!!! Current = " + currentAnimationState);
    }

    return animationAssets;
  }

  @Override
  protected int getAnimationLength() {
    return getCurrentAnimationAssets().length;
  }

  @Override
  public void update(double deltaTime) {
    // Do normal update
    super.update(deltaTime);

    // Then update the state
    for (AnimationState<T> animationState : mapAnimationAsset.keySet())
      if (animationState.change(gameObject)) {
        currentAnimationState = animationState;
        return;
      }

    currentAnimationState = fallbackAnimationState;
  }

  @Override
  public AnimationAsset getCurrentAnimationAsset() {
    return getCurrentAnimationAssets()[currentAnimationFrame];
  }
}
