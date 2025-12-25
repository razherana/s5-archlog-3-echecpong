package mg.razherana.game.logic.animations;

import mg.razherana.game.logic.GameObject;

@FunctionalInterface
public interface AnimationState<T extends GameObject> {
  public boolean change(T gameObject);
}