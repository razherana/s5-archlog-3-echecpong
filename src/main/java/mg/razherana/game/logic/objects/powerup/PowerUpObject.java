package mg.razherana.game.logic.objects.powerup;

import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

abstract public class PowerUpObject<T extends GameObject> extends GameObject {

  private Class<T> targetClass;

  public PowerUpObject(Game game, Vector2 position, Vector2 size, Class<T> targetClass) {
    super(game, position, 5);
    this.targetClass = targetClass;

    setSize(size);
    setCollision(true);
  }

  /**
   * Code ran when target collides with this powerup.
   * 
   * @param gameObject
   */
  @SuppressWarnings("unchecked")
  public void targetCollided(GameObject gameObject) {
    if (isTarget(gameObject))
      applyPower((T) gameObject);
  }

  @Override
  public void update(double deltaTime) {
    updateAnimation(deltaTime);
  }

  @Override
  public void render(Graphics2D g2d) {
    renderAnimation(g2d);
  }

  @Override
  public void onCollision(GameObject other) {
    targetCollided(other);
  }

  /**
   * Powerup to apply to the object
   * 
   * @param gameObject
   */
  abstract protected void applyPower(T gameObject);

  /**
   * Is target condition.
   * 
   * @param gameObject
   * @return
   */
  protected boolean isTarget(GameObject gameObject) {
    return targetClass.isInstance(gameObject);
  }
}
