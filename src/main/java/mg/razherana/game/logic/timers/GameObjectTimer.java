package mg.razherana.game.logic.timers;

import mg.razherana.game.logic.GameObject;

abstract public class GameObjectTimer<T extends GameObject> {
  // Attribute to keep track of the current time of this timer
  protected float currentTime;

  // If done, then this timer should be removed
  private boolean done = false;

  private boolean running = true;

  protected final T gameObject;

  // When currentTime == frequency, then we run the code
  private final float frequency;

  /**
   * @param gameObject
   * @param frequency
   */
  public GameObjectTimer(T gameObject, float frequency) {
    this.gameObject = gameObject;
    this.frequency = frequency;
  }

  public void updateCurrentTime(double deltaTime) {
    if (isRunning() && !isDone())
      currentTime += deltaTime;

    if (currentTime >= getFrequency()) {
      currentTime = 0f;
      onTick();
    }
  }

  protected abstract void onTick();

  /**
   * @return the currentTime
   */
  public float getCurrentTime() {
    return currentTime;
  }

  /**
   * @param currentTime the currentTime to set
   */
  public void setCurrentTime(float currentTime) {
    this.currentTime = currentTime;
  }

  /**
   * @return the done
   */
  public boolean isDone() {
    return done;
  }

  /**
   * @param done the done to set
   */
  public void setDone(boolean done) {
    this.done = done;
  }

  /**
   * @return the running
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * @param running the running to set
   */
  public void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * @return the gameObject
   */
  public T getGameObject() {
    return gameObject;
  }

  /**
   * @return the frequency
   */
  public float getFrequency() {
    return frequency;
  }
}
