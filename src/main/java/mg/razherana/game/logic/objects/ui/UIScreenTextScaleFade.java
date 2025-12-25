package mg.razherana.game.logic.objects.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.utils.Vector2;

public class UIScreenTextScaleFade extends AbstractUIFade {
  private final float initialScale;
  private final float targetScale;
  private String text;
  private int baseFontSize;
  private static Font font;

  public UIScreenTextScaleFade(Game game, Vector2 position, Vector2 size, float lifetime,
      float initialScale, float targetScale, String text, int baseFontSize, Font baseFont) {
    super(game, position, 100, size, lifetime);

    this.initialScale = initialScale;
    this.targetScale = targetScale;
    this.baseFontSize = baseFontSize;
    font = baseFont;

    this.text = text;
  }

  /**
   * @return the current scale based on the elapsed time.
   */
  public float getCurrentScale() {
    float progress = timePassed / lifetime;
    if (progress > 1.0f)
      progress = 1.0f; // clamp to 1

    return initialScale + (targetScale - initialScale) * progress;
  }

  @Override
  public void render(Graphics2D g2d) {
    setAlpha(g2d);
    // Rendering logic would go here, using getCurrentScale() to determine the scale
    // of the text to render.

    font = font.deriveFont((int) (baseFontSize * getCurrentScale()));

    g2d.setFont(font);

    // Render text at the specified position
    g2d.setColor(Color.WHITE);
    g2d.drawString(text, getPosition().x, getPosition().y);
  }
}
