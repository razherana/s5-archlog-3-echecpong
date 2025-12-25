package mg.razherana.game.logic.objects.powerup;

import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

abstract public class PowerUpObject<T> extends GameObject {

  private Class<T> targetClass;

  public PowerUpObject(Game game, Vector2 position, Vector2 size, Class<T> targetClass) {
    super(game, position, 11);
    this.targetClass = targetClass;

    setSize(size);
    setCollision(true);
  }

  /**
   * Code ran when target collides with this powerup.
   * 
   * @param gameObject
   */
  abstract void targetCollided(GameObject gameObject);

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
