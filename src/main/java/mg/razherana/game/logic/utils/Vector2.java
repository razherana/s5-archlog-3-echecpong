package mg.razherana.game.logic.utils;

import java.util.Objects;

public class Vector2 {
  public float x;
  public float y;

  public Vector2() {
    this.x = 0;
    this.y = 0;
  }

  public Vector2(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public static Vector2 from(String str) {
    String[] parts = str.split(",");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Invalid vector string: " + str);
    }

    float x = Float.parseFloat(parts[0].trim());
    float y = Float.parseFloat(parts[1].trim());

    return new Vector2(x, y);
  }

  public Vector2 add(Vector2 other) {
    return new Vector2(this.x + other.x, this.y + other.y);
  }

  public Vector2 subtract(Vector2 other) {
    return new Vector2(this.x - other.x, this.y - other.y);
  }

  public Vector2 multiply(float scalar) {
    return new Vector2(this.x * scalar, this.y * scalar);
  }

  public Vector2 divide(float scalar) {
    if (scalar == 0) {
      throw new IllegalArgumentException("Cannot divide by zero.");
    }
    return new Vector2(this.x / scalar, this.y / scalar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Vector2)) {
      return false;
    }
    Vector2 other = (Vector2) obj;
    return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
        && Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
  }

  @Override
  public String toString() {
    return "Vector2(" + x + ", " + y + ")";
  }
}
