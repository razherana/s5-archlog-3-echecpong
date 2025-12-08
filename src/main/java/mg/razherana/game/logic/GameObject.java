package mg.razherana.game.logic;

import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.utils.Vector2;

public abstract class GameObject {
  private Vector2 position;

  private final Game game;

  private final int priority;

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }

  public GameObject(Game game, int priority) {
    this(game, new Vector2(0, 0), priority);
  }

  public GameObject(Game game, Vector2 position, int priority) {
    this.position = position;
    this.game = game;
    this.priority = priority;
  }

  public abstract void update(double deltaTime);

  /**
   * Render the game object.
   * 
   * @param g2d the graphics context is created only for this GameObject.
   */
  public abstract void render(Graphics2D g2d);

  /**
   * @return the position
   */
  public Vector2 getPosition() {
    return position;
  }

  /**
   * @param position the position to set
   */
  public void setPosition(Vector2 position) {
    this.position = position;
  }
}
