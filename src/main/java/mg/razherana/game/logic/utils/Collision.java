package mg.razherana.game.logic.utils;

import java.awt.geom.Rectangle2D;

public class Collision {
  // Record to store collision info
  public record CollisionSidesResult(
      boolean collided,
      String side, // "LEFT", "RIGHT", "TOP", "BOTTOM"
      float correctedX,
      float correctedY,
      float newVx,
      float newVy) {
    public boolean left() {
      return side.equals("LEFT");
    }

    public boolean right() {
      return side.equals("RIGHT");
    }

    public boolean top() {
      return side.equals("TOP");
    }

    public boolean bottom() {
      return side.equals("BOTTOM");
    }
  }

  static float EPSILON = 0.0001f;

  /**
   * Detects collision between two rectangles and resolves it.
   *
   * @param rect1 The first rectangle
   * @param vx1   Velocity X of rect1
   * @param vy1   Velocity Y of rect1
   * @param rect2 The second rectangle
   * @param vx2   Velocity X of rect2
   * @param vy2   Velocity Y of rect2
   * @return CollisionResult
   */
  public static CollisionSidesResult collideSides(Rectangle2D.Float rect1, float vx1, float vy1,
      Rectangle2D.Float rect2, float vx2, float vy2) {

    if (!rect1.intersects(rect2)) {
      return new CollisionSidesResult(false, "", rect1.x, rect1.y, vx1, vy1);
    }

    // Relative position and velocity
    float dx = (rect1.x + rect1.width / 2f) - (rect2.x + rect2.width / 2f);
    float dy = (rect1.y + rect1.height / 2f) - (rect2.y + rect2.height / 2f);

    float combinedHalfWidth = (rect1.width + rect2.width) / 2f;
    float combinedHalfHeight = (rect1.height + rect2.height) / 2f;

    float overlapX = combinedHalfWidth - Math.abs(dx);
    float overlapY = combinedHalfHeight - Math.abs(dy);

    float correctedX = rect1.x;
    float correctedY = rect1.y;
    float newVx = vx1;
    float newVy = vy1;
    String side = "";

    if (overlapX < overlapY) {
      // Horizontal collision
      newVx = -vx1; // reflect horizontally

      if (dx > 0) {
        // rect1 hit rect2 from the left
        side = "RIGHT";
      } else {
        // rect1 hit rect2 from the right
        side = "LEFT";
      }
    } else {
      // Vertical collision
      newVy = -vy1; // reflect vertically

      if (dy < 0) {
        // rect1 hit rect2 from below
        side = "TOP";
      } else {
        // rect1 hit rect2 from above
        side = "BOTTOM";
      }
    }

    if (side.equals("LEFT")) {
      // If collided from left, move rect1 to the left of rect2
      correctedX = rect2.x - rect1.width - EPSILON;
    } else if (side.equals("RIGHT")) {
      // If collided from right, move rect1 to the right of rect2
      correctedX = rect2.x + rect2.width + EPSILON;
    } else if (side.equals("TOP")) {
      // If collided from top, move rect1 above rect2
      correctedY = rect2.y - rect1.height - EPSILON;
    } else if (side.equals("BOTTOM")) {
      // If collided from bottom, move rect1 below rect2
      correctedY = rect2.y + rect2.height + EPSILON;
    }

    return new CollisionSidesResult(true, side, correctedX, correctedY, newVx, newVy);
  }
}
