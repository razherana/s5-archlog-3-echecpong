package mg.razherana.game.logic.utils;

public class RandomGen {
  private RandomGen() {
  }

  public static float generateRandomFloat(float min, float max) {
    return (float) (Math.random() * (max - min) + min);
  }
}
