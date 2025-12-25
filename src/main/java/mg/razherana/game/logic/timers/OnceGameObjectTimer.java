package mg.razherana.game.logic.timers;

import mg.razherana.game.logic.GameObject;

/**
 * The tick method is ran once, then this object is discarded
 */
abstract public class OnceGameObjectTimer<T extends GameObject> extends GameObjectTimer<T> {

  public OnceGameObjectTimer(T gameObject, float endTime) {
    super(gameObject, endTime);
  }

  @Override
  public void updateCurrentTime(double deltaTime) {
    if (isRunning() && !isDone())
      currentTime += deltaTime;

    if (currentTime >= getFrequency()) {
      currentTime = 0f;
      setDone(true);
      onTick();
    }
  }
}
