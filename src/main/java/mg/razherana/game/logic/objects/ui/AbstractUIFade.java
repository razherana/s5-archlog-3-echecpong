package mg.razherana.game.logic.objects.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

public abstract class AbstractUIFade extends GameObject {
  /**
   * The total lifetime of the fade effect in seconds.
   */
  protected final float lifetime;

  /**
   * Indicates whether the UI element should be removed from the game after the fade
   * effect completes.
   */
  private boolean removeAfterFade = true;

  /**
   * The current alpha value (opacity) of the UI element, ranging from 1.0 (fully
   * opaque) to 0.0 (fully transparent).
   */
  float alpha = 1.0f;

  /**
   * The time passed since the fade effect started in seconds.
   */
  float timePassed = 0.0f;

  protected AbstractUIFade(Game game, Vector2 position, int priority, Vector2 size, float lifetime) {
    super(game, position, priority);

    this.lifetime = lifetime;

    setCollision(false);
    setSize(size);
  }

  @Override
  public void update(double deltaTime) {
    timePassed += deltaTime; // increase elapsed time
    alpha = 1.0f - (timePassed / lifetime); // fade linearly
    if (alpha < 0)
      alpha = 0; // clamp to 0

    if (timePassed >= lifetime && removeAfterFade) {
      // Remove this UI element from the game
      getGame().removeGameObject(this);
    }
  }

  public void setAlpha(Graphics2D g2d) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
  }

  /**
   * @return the lifetime
   */
  public float getLifetime() {
    return lifetime;
  }

  /**
   * @return the removeAfterFade
   */
  public boolean isRemoveAfterFade() {
    return removeAfterFade;
  }

  /**
   * Sets whether the UI element should be removed from the game after the fade
   * effect completes.
   * 
   * @param removeAfterFade the removeAfterFade to set
   */
  public void setRemoveAfterFade(boolean removeAfterFade) {
    this.removeAfterFade = removeAfterFade;
  }

  /**
   * @return the alpha
   */
  public float getAlpha() {
    return alpha;
  }

  /**
   * @param alpha the alpha to set
   */
  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  /**
   * @return the timePassed
   */
  public float getTimePassed() {
    return timePassed;
  }

  /**
   * @param timePassed the timePassed to set
   */
  public void setTimePassed(float timePassed) {
    this.timePassed = timePassed;
  }
}
