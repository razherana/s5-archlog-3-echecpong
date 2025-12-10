package mg.razherana.game.gfx.utils;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GradientHelper {
  /**
   * Enum for gradient directions
   */
  private enum GradientDirection {
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_LEFT_TO_BOTTOM_RIGHT,
    BOTTOM_LEFT_TO_TOP_RIGHT
  }

  /**
   * Draws a linear gradient from one color to another
   * 
   * @param g2d        Graphics2D object
   * @param x          X position
   * @param y          Y position
   * @param width      Width of gradient area
   * @param height     Height of gradient area
   * @param startColor Starting color
   * @param endColor   Ending color
   * @param direction  Gradient direction (TOP_TO_BOTTOM, LEFT_TO_RIGHT, etc.)
   */
  public static void drawLinearGradient(Graphics2D g2d,
      int x, int y, int width, int height,
      Color startColor, Color endColor,
      GradientDirection direction) {

    Point2D startPoint = new Point2D.Float();
    Point2D endPoint = new Point2D.Float();

    switch (direction) {
      case TOP_TO_BOTTOM:
        startPoint.setLocation(0, 0);
        endPoint.setLocation(0, height);
        break;
      case BOTTOM_TO_TOP:
        startPoint.setLocation(0, height);
        endPoint.setLocation(0, 0);
        break;
      case LEFT_TO_RIGHT:
        startPoint.setLocation(0, 0);
        endPoint.setLocation(width, 0);
        break;
      case RIGHT_TO_LEFT:
        startPoint.setLocation(width, 0);
        endPoint.setLocation(0, 0);
        break;
      case TOP_LEFT_TO_BOTTOM_RIGHT:
        startPoint.setLocation(0, 0);
        endPoint.setLocation(width, height);
        break;
      case BOTTOM_LEFT_TO_TOP_RIGHT:
        startPoint.setLocation(0, height);
        endPoint.setLocation(width, 0);
        break;
    }

    GradientPaint gradient = new GradientPaint(
        (float) startPoint.getX() + x, (float) startPoint.getY() + y, startColor,
        (float) endPoint.getX() + x, (float) endPoint.getY() + y, endColor);

    g2d.setPaint(gradient);
    g2d.fillRect(x, y, width, height);
  }

  /**
   * Draws a gradient with multiple color stops
   * 
   * @param g2d       Graphics2D object
   * @param x         X position
   * @param y         Y position
   * @param width     Width of gradient area
   * @param height    Height of gradient area
   * @param colors    Array of colors
   * @param fractions Array of fractions (0.0 to 1.0)
   * @param direction Gradient direction
   */
  public static void drawMultiStopGradient(Graphics2D g2d,
      int x, int y, int width, int height,
      Color[] colors, float[] fractions,
      GradientDirection direction) {

    if (colors.length != fractions.length) {
      throw new IllegalArgumentException("Colors and fractions arrays must have same length");
    }

    Rectangle2D rect = new Rectangle2D.Float(x, y, width, height);

    Point2D startPoint = new Point2D.Float();
    Point2D endPoint = new Point2D.Float();

    switch (direction) {
      case TOP_TO_BOTTOM:
        startPoint.setLocation(x, y);
        endPoint.setLocation(x, y + height);
        break;
      case LEFT_TO_RIGHT:
        startPoint.setLocation(x, y);
        endPoint.setLocation(x + width, y);
        break;
      case TOP_LEFT_TO_BOTTOM_RIGHT:
        startPoint.setLocation(x, y);
        endPoint.setLocation(x + width, y + height);
        break;
      default:
        startPoint.setLocation(x, y);
        endPoint.setLocation(x, y + height);
    }

    LinearGradientPaint gradient = new LinearGradientPaint(
        startPoint, endPoint, fractions, colors);

    g2d.setPaint(gradient);
    g2d.fill(rect);
  }

  /**
   * Draws a radial gradient
   * 
   * @param g2d        Graphics2D object
   * @param centerX    Center X coordinate
   * @param centerY    Center Y coordinate
   * @param radius     Radius of gradient
   * @param startColor Center color
   * @param endColor   Edge color
   */
  public static void drawRadialGradient(Graphics2D g2d,
      int centerX, int centerY, int radius,
      Color startColor, Color endColor) {

    float[] fractions = { 0.0f, 1.0f };
    Color[] colors = { startColor, endColor };

    RadialGradientPaint gradient = new RadialGradientPaint(
        centerX, centerY, radius, fractions, colors);

    g2d.setPaint(gradient);
    g2d.fillOval(centerX - radius, centerY - radius,
        radius * 2, radius * 2);
  }

  /**
   * Creates a fancy background gradient for game menus
   * 
   * @param g2d    Graphics2D object
   * @param width  Width of component
   * @param height Height of component
   */
  public static void drawGameMenuBackground(Graphics2D g2d, int width, int height) {
    Color[] colors = {
        new Color(10, 10, 40), // Dark blue
        new Color(30, 30, 100), // Medium blue
        new Color(10, 10, 40) // Dark blue again
    };

    float[] fractions = { 0.0f, 0.5f, 1.0f };

    drawMultiStopGradient(g2d, 0, 0, width, height,
        colors, fractions, GradientDirection.TOP_TO_BOTTOM);
  }

  /**
   * Creates a glowing effect gradient
   * 
   * @param g2d       Graphics2D object
   * @param x         X position
   * @param y         Y position
   * @param width     Width
   * @param height    Height
   * @param baseColor Base color for glow
   */
  public static void drawGlowEffect(Graphics2D g2d,
      int x, int y, int width, int height,
      Color baseColor) {

    // Create semi-transparent versions of the color
    Color[] colors = {
        new Color(baseColor.getRed(), baseColor.getGreen(),
            baseColor.getBlue(), 150), // More opaque in center
        new Color(baseColor.getRed(), baseColor.getGreen(),
            baseColor.getBlue(), 50), // Less opaque middle
        new Color(baseColor.getRed(), baseColor.getGreen(),
            baseColor.getBlue(), 0) // Fully transparent at edge
    };

    float[] fractions = { 0.0f, 0.5f, 1.0f };

    int centerX = x + width / 2;
    int centerY = y + height / 2;
    int maxRadius = Math.max(width, height) / 2;

    RadialGradientPaint gradient = new RadialGradientPaint(
        centerX, centerY, maxRadius, fractions, colors);

    g2d.setPaint(gradient);
    g2d.fillOval(x, y, width, height);
  }

  /**
   * Creates a button gradient (pressed and unpressed states)
   * 
   * @param g2d       Graphics2D object
   * @param x         X position
   * @param y         Y position
   * @param width     Width
   * @param height    Height
   * @param baseColor Base color
   * @param isPressed Whether button is pressed
   * @param roundness Corner roundness
   */
  public static void drawButtonGradient(Graphics2D g2d,
      int x, int y, int width, int height,
      Color baseColor, boolean isPressed,
      int roundness) {

    // Adjust colors based on button state
    Color topColor, bottomColor;

    if (isPressed) {
      topColor = baseColor.darker();
      bottomColor = baseColor;
    } else {
      topColor = baseColor.brighter();
      bottomColor = baseColor.darker();
    }

    drawLinearGradient(g2d, x, y, width, height,
        topColor, bottomColor, GradientDirection.TOP_TO_BOTTOM);

    // Add a subtle border
    g2d.setColor(isPressed ? baseColor.darker().darker() : baseColor.brighter());
    g2d.drawRoundRect(x, y, width - 1, height - 1, roundness, roundness);
  }

  /**
   * Creates a sunset/sunrise gradient
   * 
   * @param g2d    Graphics2D object
   * @param width  Width
   * @param height Height
   */
  public static void drawSunsetBackground(Graphics2D g2d, int width, int height) {
    Color[] colors = {
        new Color(255, 100, 50), // Orange
        new Color(255, 200, 100), // Light orange
        new Color(150, 50, 150), // Purple
        new Color(0, 0, 50) // Dark blue
    };

    float[] fractions = { 0.0f, 0.3f, 0.7f, 1.0f };

    drawMultiStopGradient(g2d, 0, 0, width, height,
        colors, fractions, GradientDirection.TOP_TO_BOTTOM);
  }

  /**
   * Creates a metallic gradient
   * 
   * @param g2d    Graphics2D object
   * @param x      X position
   * @param y      Y position
   * @param width  Width
   * @param height Height
   */
  public static void drawMetallicGradient(Graphics2D g2d,
      int x, int y, int width, int height) {
    Color[] colors = {
        new Color(200, 200, 220), // Light metallic
        new Color(100, 100, 120), // Medium metallic
        new Color(150, 150, 170), // Light metallic
        new Color(80, 80, 100) // Dark metallic
    };

    float[] fractions = { 0.0f, 0.3f, 0.7f, 1.0f };

    drawMultiStopGradient(g2d, x, y, width, height,
        colors, fractions, GradientDirection.TOP_TO_BOTTOM);
  }
}