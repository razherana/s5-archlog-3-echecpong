package mg.razherana.game.logic.timers;

import mg.razherana.game.logic.GameObject;

abstract public class NthGameObjectTimer<T extends GameObject> extends GameObjectTimer<T> {
  private int limit;
  private int times;

  public NthGameObjectTimer(T gameObject, float frequency, int limit) {
    super(gameObject, frequency);
    this.limit = limit;
  }

  @Override
  public void updateCurrentTime(double deltaTime) {
    if (isRunning() && !isDone())
      currentTime += deltaTime;

    if (currentTime >= getFrequency()) {
      currentTime = 0f;
      times++;
      onTick();

      if (times >= limit)
        setDone(true);
    }
  }

  /**
   * @return the limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * @param limit the limit to set
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }
}
