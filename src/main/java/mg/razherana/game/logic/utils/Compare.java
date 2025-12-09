package mg.razherana.game.logic.utils;

public class Compare {
  public static boolean floatEquals(float a, float b, float epsilon) {
    return Math.abs(a - b) < epsilon;
  }

  public static boolean doubleEquals(double a, double b, double epsilon) {
    return Math.abs(a - b) < epsilon;
  }

  public static boolean floatBetween(float value, float min, float max, float epsilon) {
    return value > min - epsilon && value < max + epsilon;
  }

  public static boolean doubleBetween(double value, double min, double max, double epsilon) {
    return value > min - epsilon && value < max + epsilon;
  }

  public static boolean floatBetweenEq(float value, float min, float max, float epsilon) {
    return value >= min - epsilon && value <= max + epsilon;
  }

  public static boolean doubleBetweenEq(double value, double min, double max, double epsilon) {
    return value >= min - epsilon && value <= max + epsilon;
  }

  public static boolean floatBetweenEqLt(float value, float min, float max, float epsilon) {
    return value >= min - epsilon && value < max + epsilon;
  }

  public static boolean doubleBetweenEqLt(double value, double min, double max, double epsilon) {
    return value >= min - epsilon && value < max + epsilon;
  }

  public static boolean floatBetweenEqGt(float value, float min, float max, float epsilon) {
    return value > min - epsilon && value <= max + epsilon;
  }

  public static boolean doubleBetweenEqGt(double value, double min, double max, double epsilon) {
    return value > min - epsilon && value <= max + epsilon;
  }
}
