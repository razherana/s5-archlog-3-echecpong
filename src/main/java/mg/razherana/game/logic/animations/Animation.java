package mg.razherana.game.logic.animations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import mg.razherana.game.logic.utils.Assets;
import mg.razherana.game.logic.utils.Vector2;

public class Animation {
  protected final float animationFrameDuration;
  protected final BufferedImage animationSpriteSheet;
  protected AnimationAsset[] animationAssets;
  protected final int tileSize;

  protected int[] margins = { 0, 0, 0, 0 };

  protected int currentAnimationFrame;
  protected float currentAnimationTimer;

  /**
   * @param animationFrameDuration
   * @param animationSpriteSheet
   * @param animationAssets
   * @param tileSize
   */
  public Animation(float animationFrameDuration, BufferedImage animationSpriteSheet, AnimationAsset[] animationAssets,
      int tileSize) {
    this.animationFrameDuration = animationFrameDuration;
    this.animationSpriteSheet = animationSpriteSheet;
    this.animationAssets = animationAssets;
    this.tileSize = tileSize;
    this.margins = null;
  }

  /**
   * @param animationFrameDuration
   * @param animationSpriteSheet
   * @param animationAssets
   * @param tileSize
   * @param margins
   */
  public Animation(float animationFrameDuration, BufferedImage animationSpriteSheet, AnimationAsset[] animationAssets,
      int tileSize, int[] margins) {
    this.animationFrameDuration = animationFrameDuration;
    this.animationSpriteSheet = animationSpriteSheet;
    this.animationAssets = animationAssets;
    this.tileSize = tileSize;
    this.margins = margins;
  }

  /**
   * @param animationFrameDuration
   * @param animationSpriteSheet
   * @param animationAssets
   * @param tileSize
   */
  public Animation(float animationFrameDuration, BufferedImage animationSpriteSheet, AnimationAsset[] animationAssets,
      int tileSize, int margin) {
    this.animationFrameDuration = animationFrameDuration;
    this.animationSpriteSheet = animationSpriteSheet;
    this.animationAssets = animationAssets;
    this.tileSize = tileSize;
    this.margins = new int[] { margin };
  }

  /**
   * @param margins the margins to set
   */
  public void setMargins(int[] margins) {
    this.margins = margins;
  }

  public void update(float deltaTime) {
    currentAnimationTimer += deltaTime;
    if (currentAnimationTimer >= animationFrameDuration) {
      currentAnimationFrame = (currentAnimationFrame + 1) % animationAssets.length;
      currentAnimationTimer = 0f;
    }
  }

  public void render(Graphics2D g2d, Vector2 position, Vector2 size) {
    render_0(g2d, position, size, getCurrentAnimationAsset());
  }

  /**
   * @return the animationFrameDuration
   */
  public float getAnimationFrameDuration() {
    return animationFrameDuration;
  }

  /**
   * @return the animationSpriteSheet
   */
  public BufferedImage getAnimationSpriteSheet() {
    return animationSpriteSheet;
  }

  /**
   * @return the animationAssets
   */
  public AnimationAsset[] getAnimationAssets() {
    return animationAssets;
  }

  /**
   * @return the tileSize
   */
  public int getTileSize() {
    return tileSize;
  }

  /**
   * @return the currentAnimationFrame
   */
  public int getCurrentAnimationFrame() {
    return currentAnimationFrame;
  }

  /**
   * @return the currentAnimationTimer
   */
  public float getCurrentAnimationTimer() {
    return currentAnimationTimer;
  }

  /**
   * @return the margins
   */
  public int[] getMargins() {
    return margins;
  }

  /**
   * Render logic
   * 
   * @param g2d
   * @param position
   * @param size
   * @param animationAsset
   */
  protected void render_0(Graphics2D g2d, Vector2 position, Vector2 size, AnimationAsset animationAsset) {
    int[] currentMargins = getCurrentMargins();

    Assets.drawAnimationImageTile(
        tileSize,
        animationAsset.imgx(), animationAsset.imgy(),
        (int) position.x, (int) position.y,
        (int) size.x, (int) size.y,
        g2d,
        animationSpriteSheet,
        currentMargins[0],
        currentMargins[1],
        currentMargins[2],
        currentMargins[3]);
  }

  /**
   * Get margins logic
   * @return
   */
  protected int[] getCurrentMargins() {
    int[] currentMargins = null;

    if (margins == null && margins.length == 0)
      currentMargins = new int[] { 0, 0, 0, 0 };
    else if (margins.length == 1)
      currentMargins = new int[] { margins[0], margins[0], margins[0], margins[0] };
    else if (margins.length == 2)
      currentMargins = new int[] { margins[0], margins[1], margins[0], margins[1] };
    else if (margins.length >= 4)
      currentMargins = margins;
    else
      currentMargins = new int[] { 0, 0, 0, 0 };

    return currentMargins;
  }

  /**
   * Get the AnimationAsset to draw
   * @return AnimationAsset
   */
  protected AnimationAsset getCurrentAnimationAsset() {
    return animationAssets[currentAnimationFrame];
  }
}
