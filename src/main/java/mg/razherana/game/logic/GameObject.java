package mg.razherana.game.logic;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.listeners.GameKeyListener;
import mg.razherana.game.logic.listeners.KeyboardAdapter;
import mg.razherana.game.logic.utils.Vector2;

public abstract class GameObject {
  private Vector2 position;
  private Vector2 size = new Vector2(80, 80); // Default size, can be overridden
  private boolean collision = true;

  private final Game game;

  private final int priority;
  private GameKeyListener keyListener;

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

    registerListeners();
  }

  private void registerListeners() {
    keyListener = new GameKeyListener() {
      @Override
      public void onKeyPressed(int keyCode, KeyboardAdapter keyboardAdapter) {
        GameObject.this.onKeyPressed(keyCode, keyboardAdapter);
      }

      @Override
      public void onKeyReleased(int keyCode, KeyboardAdapter keyboardAdapter) {
        GameObject.this.onKeyReleased(keyCode, keyboardAdapter);
      }
    };
    getGame().getKeyboardAdapter().addListener(keyListener);
  }

  public abstract void update(double deltaTime);

  /**
   * Render the game object.
   * 
   * @param g2d the graphics context is created only for this GameObject.
   */
  public abstract void render(Graphics2D g2d);

  /**
   * Handle collision with another game object.
   * 
   * @param other
   */
  public void onCollision(GameObject other) {
    // Default implementation does nothing
  }

  /**
   * Handle key press event.
   * It is ran anytime a key is pressed. No actual order is guaranteed.
   * 
   * @param keyCode
   * @param keyboardAdapter
   */
  public void onKeyPressed(int keyCode, KeyboardAdapter keyboardAdapter) {
    // Default implementation does nothing
  }

  /**
   * Handle key release event.
   * It is ran anytime a key is pressed. No actual order is guaranteed.
   * 
   * @param keyCode
   * @param keyboardAdapter
   */
  public void onKeyReleased(int keyCode, KeyboardAdapter keyboardAdapter) {
    // Default implementation does nothing
  }

  public void freeListeners() {
    getGame().getKeyboardAdapter().removeListener(keyListener);
  }

  public boolean isCollidingWith(GameObject other) {
    // Use Rect2D for collision detection
    Rectangle2D.Double rectA = new Rectangle2D.Double(
        this.getPosition().x, this.getPosition().y,
        this.getSize().x, this.getSize().y);

    Rectangle2D.Double rectB = new Rectangle2D.Double(
        other.getPosition().x, other.getPosition().y,
        other.getSize().x, other.getSize().y);

    return rectA.intersects(rectB);
  }

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

  /**
   * @return the size
   */
  public Vector2 getSize() {
    return size;
  }

  /**
   * @param size the size to set
   */
  public void setSize(Vector2 size) {
    this.size = size;
  }

  public Rectangle2D.Float getDefaultBounds() {
    return new Rectangle2D.Float(position.x, position.y, size.x, size.y);
  }

  /**
   * @return the collision
   */
  public boolean isCollision() {
    return collision;
  }

  /**
   * @param collision the collision to set
   */
  public void setCollision(boolean collision) {
    this.collision = collision;
  }
}
